import fs from "fs";
import path from "path";
import EventEmitter from "events";
import java from "java-bridge";
import Debug from "debug";
import { fileURLToPath, pathToFileURL } from "url";
import { isJavaError } from "./JavaError.js";
import { isValidFileExtension } from "./FileExtensions.js";
import WeaverMessageFromLauncher from "./WeaverMessageFromLauncher.js";
import assert from "assert";

let directExecution = false;

if (fileURLToPath(import.meta.url) === process.argv[1]) {
  directExecution = true;
} else {
  directExecution = false;
}

export class Weaver {
  private static debug: Debug.Debugger;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  private static datastore: any;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  private static javaWeaver: any;

  static getDatastore(): any {
    return Weaver.datastore;
  }

  static setupJavaEnvironment(sourceDir: string) {
    // IMPORTANT: This is a Bun-specific workaround that must be applied BEFORE the JVM is created.
    // If the JVM hasn't been created yet by a preload script, we need to ensure it's created with
    // the proper options. The -Djdk.lang.processReaperUseDefaultStackSize=true option is required
    // because Bun uses a smaller thread stack size (136k) than Node.js, which causes Java's process
    // reaper thread to fail with "pthread_create failed (EINVAL)" when spawning external processes.
    // This call is idempotent - if JVM is already created, it does nothing.
    java.ensureJvm({
      opts: ["-Djdk.lang.processReaperUseDefaultStackSize=true"],
    });

    java.config.asyncSuffix = "Async";
    java.config.syncSuffix = "";

    java.stdout.enableRedirect(
        (_, data) => {
            console.log(data);
        },
        (_, data) => {
            console.error(data);
        }
    );
    const files = fs.readdirSync(sourceDir, { recursive: true });
    const jarFiles = files
        .filter((file) => typeof file === "string")
        .filter((file) => file.endsWith(".jar"))
        .map((jar) => path.join(sourceDir, jar));

    java.appendClasspath(jarFiles);
  }

  static async setupWeaver(
    args: WeaverMessageFromLauncher["args"],
    config: WeaverMessageFromLauncher["config"]
  ) {
    // Create debug instance
    Weaver.debug = Debug(`Weaver:${config.weaverPrettyName}`);
    Weaver.debug("Initiating weaver setup.");

    this.setupJavaEnvironment(config.jarPath);

    Weaver.debug(`${config.weaverPrettyName} execution arguments: %O`, args);

    const javaWeaverClassName = RegExp(/(?<=\.)\w+$/).exec(
      config.javaWeaverQualifiedName
    )?.[0];

    if (javaWeaverClassName === undefined || javaWeaverClassName === null) {
      throw new Error("Invalid weaver class name.");
    }

    // Run this before any other Java code, to have properly formatted prints
    const JavaSpecsSystem = java.importClass("pt.up.fe.specs.util.SpecsSystem");
    JavaSpecsSystem.programStandardInit();

    /* eslint-disable */
    // This code is intentionally ignored by eslint
    const JavaArrayList = java.importClass("java.util.ArrayList");
    const JavaFile = java.importClass("java.io.File");
    const JavaFileList = java.importClass(
      "org.lara.interpreter.joptions.keys.FileList"
    );
    const JavaLaraI = java.importClass("larai.LaraI");
    const JavaLaraIDataStore = java.importClass(
      "org.lara.interpreter.joptions.config.interpreter.LaraIDataStore"
    );
    const LaraiKeys = java.importClass(
      "org.lara.interpreter.joptions.config.interpreter.LaraiKeys"
    );

    const JavaWeaverClass = java.importClass(config.javaWeaverQualifiedName);

    const javaWeaver = new JavaWeaverClass();
    javaWeaver.setWeaver();

    const isClassicCli =
      args.configClassic !== undefined && args.configClassic !== null;

    let datastore;
    if (isClassicCli) {
      try {
        assert(args.configClassic instanceof Array);

        datastore = JavaLaraI.convertArgsToDataStore(
          args.configClassic,
          javaWeaver
        ).get();

        // Arguments parser has shown help, exit
        if (datastore.get(LaraiKeys.SHOW_HELP)) {
          process.exit(0);
        }

        args.scriptFile = datastore.get(LaraiKeys.LARA_FILE).toString();
      } catch (error) {
        throw new Error(
          "Failed to parse 'Classic' weaver arguments:\n" + error
        );
      }
    } else {
      const JavaDataStore = java.importClass(
        "org.suikasoft.jOptions.Interfaces.DataStore"
      );

      const storeDefinition = JavaLaraI.getStoreDefinition(javaWeaver);

      datastore = JavaDataStore.newInstance(storeDefinition);

      const fileList = new JavaArrayList();
      //const [command, clangArgs, env] = await Sandbox.splitCommandArgsEnv(args._[1]);
      const clangArgs = args._.slice(1);
      clangArgs.forEach((arg: string | number) => {
        fileList.add(new JavaFile(arg));
      });

      datastore.set(LaraiKeys.LARA_FILE, new JavaFile("placeholderFileName"));
      datastore.set(
        LaraiKeys.WORKSPACE_FOLDER,
        JavaFileList.newInstance(fileList)
      );
    }

    // Needed only for side-effects over the datastore
    new JavaLaraIDataStore(null, datastore, javaWeaver); // nosonar typescript:S1848
    javaWeaver.setData(datastore);

    Weaver.javaWeaver = javaWeaver;
    Weaver.datastore = datastore;

    for (const file of config.importForSideEffects ?? []) {
      await import(file);
    }
    /* eslint-enable */
  }

  static start() {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-call, @typescript-eslint/no-unsafe-member-access
    Weaver.javaWeaver.run(Weaver.datastore);
  }

  static async executeScript(
    args: WeaverMessageFromLauncher["args"],
    config: WeaverMessageFromLauncher["config"]
  ): Promise<boolean> {
    if (args.scriptFile == undefined) {
      Weaver.debug("No script file provided.");
    }

    if (typeof args.scriptFile !== "string") {
      throw new Error(
        "Script file '" +
          args.scriptFile +
          "' is not a string: " +
          typeof args.scriptFile
      );
    }

    const scriptFile = args.scriptFile;

    Weaver.debug("Executing user script...");
    if (
      fs.existsSync(scriptFile) &&
      isValidFileExtension(path.extname(scriptFile))
    ) {
      // import is using a URL converted to string.
      // The URL is used due to a Windows error with paths. See https://stackoverflow.com/questions/69665780/error-err-unsupported-esm-url-scheme-only-file-and-data-urls-are-supported-by
      // The conversion of the URl back to a string is due to a TS bug. See https://github.com/microsoft/TypeScript/issues/42866
      let success = true;
      await import(pathToFileURL(path.resolve(scriptFile)).toString())
        .then(() => {
          Weaver.debug("Execution completed successfully.");
        })
        .catch((error: unknown) => {
          success = false;
          console.error("Execution failed.");
          if (error instanceof Error) {
            // JS exception
            console.error(error);
          } else if (isJavaError(error)) {
            // Java exception
            console.error(error.cause.getMessage());
          } else {
            console.error("UNKNOWN ERROR: Execute in debug mode to see more.");
          }
          Weaver.debug(error);
        });
      return success;
    } else {
      throw new Error("Invalid file path or file type: " + scriptFile);
    }
  }

  static shutdown() {
    Weaver.debug("Exiting...");
    // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access, @typescript-eslint/no-unsafe-call
    Weaver.javaWeaver.end();
  }
}

/////////////////////////
// import side effects //
/////////////////////////

/**
 * Creates a promise that resolves to the message received from the event emitter.
 * @param eventEmitter - The event emitter from which the message will be received.
 * @returns A promise that resolves to the message received.
 */
function waitForMessage(
  eventEmitter: EventEmitter
): Promise<WeaverMessageFromLauncher> {
  return new Promise((resolve) => {
    eventEmitter.once("message", (message: WeaverMessageFromLauncher) => {
      resolve(message);
    });
  });
}

/**
 * This event emitter is used to receive conbfiguration data from the caller.
 * If the weaver is being executed directly, the 'process' object is used instead.
 *
 * The event emitter could be the 'process' object in both cases but in the case of
 * importing this file, it is a good idea to use a different object to avoid
 * conflicts.
 */
const eventEmitter: EventEmitter = directExecution
  ? process
  : new EventEmitter();

/**
 * This function is used to send the configuration data to the weaver.
 * @param message - Configuration data.
 */
export function setupWeaver(message: WeaverMessageFromLauncher) {
  if (directExecution) {
    throw new Error(
      "Cannot run the setupWeaver function in direct execution mode."
    );
  }
  eventEmitter.emit("message", message);
}

/**
 * This promise is used to wait for the configuration data to be received.
 * The weaver is executed the configuration data is received.
 */
waitForMessage(eventEmitter)
  .then(async (messageFromParent) => {
    await Weaver.setupWeaver(messageFromParent.args, messageFromParent.config);

    if (directExecution) {
      Weaver.start();

      const success = await Weaver.executeScript(
        messageFromParent.args,
        messageFromParent.config
      ).catch((error: unknown) => {
        return false;
      });

      Weaver.shutdown();
      if (success) {
        process.exit(0);
      } else {
        process.exit(-1);
      }
    }
  })
  .catch((error) => {
    console.error(error);
  });
