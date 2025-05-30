import fs from "fs";
import path from "path";
import EventEmitter from "events";
import java from "java";
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

java.asyncOptions = {
  asyncSuffix: "Async",
  syncSuffix: "",
  promiseSuffix: "P"
};

export class Weaver {
  private static debug: Debug.Debugger;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  private static datastore: any;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  private static javaWeaver: any;

  static getDatastore(): any {
    return Weaver.datastore;
  }

  static async setupJavaEnvironment(sourceDir: string) {
    const files = fs.readdirSync(sourceDir, { recursive: true });

    for (const file of files) {
      if (typeof file === "string") {
        if (file.endsWith(".jar")) {
          java.classpath.push(path.join(sourceDir, file));
        }
      } else {
        // TODO: review this Buffer thing and why it exists.
        throw new Error(
          `Returned a Buffer instead of a string for path: ${file.toString()}.`
        );
      }
    }

    await java.ensureJvm();
  }

  static async setupWeaver(
    args: WeaverMessageFromLauncher["args"],
    config: WeaverMessageFromLauncher["config"]
  ) {
    // Create debug instance
    Weaver.debug = Debug(`Weaver:${config.weaverPrettyName}`);
    Weaver.debug("Initiating weaver setup.");

    await this.setupJavaEnvironment(config.jarPath);

    Weaver.debug(`${config.weaverPrettyName} execution arguments: %O`, args);

    const javaWeaverClassName = RegExp(/(?<=\.)\w+$/).exec(
      config.javaWeaverQualifiedName
    )?.[0];

    if (javaWeaverClassName === undefined || javaWeaverClassName === null) {
      throw new Error("Invalid weaver class name.");
    }

    /* eslint-disable */
    // This code is intentionally ignored by eslint
    const JavaArrayList = java.import("java.util.ArrayList");
    const JavaFile = java.import("java.io.File");
    const JavaFileList = java.import(
      "org.lara.interpreter.joptions.keys.FileList"
    );
    const JavaLaraI = java.import("larai.LaraI");
    const JavaLaraIDataStore = java.import(
      "org.lara.interpreter.joptions.config.interpreter.LaraIDataStore"
    );
    const LaraiKeys = java.import(
      "org.lara.interpreter.joptions.config.interpreter.LaraiKeys"
    );
    const NodeJsEngine = java.import("pt.up.fe.specs.jsengine.NodeJsEngine");
    const JavaEventTrigger = java.import(
      "org.lara.interpreter.weaver.events.EventTrigger"
    );
    const JavaSpecsSystem = java.import("pt.up.fe.specs.util.SpecsSystem");

    const JavaWeaverClass = java.import(config.javaWeaverQualifiedName);

    const javaWeaver = new JavaWeaverClass();
    javaWeaver.setWeaver();
    javaWeaver.setScriptEngine(new NodeJsEngine());
    javaWeaver.setEventTrigger(new JavaEventTrigger());

    const isClassicCli =
      args.configClassic !== undefined && args.configClassic !== null;

    let datastore;
    if (isClassicCli) {
      //if (args._[0] === "classic") {
      try {
        assert(args.configClassic instanceof Array);
        //console.log("FLAGS: " + args.configClassic);

        datastore = JavaLaraI.convertArgsToDataStore(
          //args._.slice(1),
          args.configClassic,
          javaWeaver
        ).get();

        // Arguments parser has shown help, exit
        if (datastore.get("help")) {
          process.exit(0);
        }

        args.scriptFile = datastore.get("aspect").toString();
      } catch (error) {
        throw new Error(
          "Failed to parse 'Classic' weaver arguments:\n" + error
        );
      }
    } else {
      const JavaDataStore = java.import(
        "org.suikasoft.jOptions.Interfaces.DataStore"
      );

      datastore = await new JavaDataStore.newInstanceP(
        `${javaWeaverClassName}DataStore`
      );

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
    JavaSpecsSystem.programStandardInit();

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
    Weaver.javaWeaver.close();
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
