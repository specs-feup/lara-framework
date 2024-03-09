import fs from "fs";
import path from "path";
import EventEmitter from "events";
import java from "java";
import Debug from "debug";
import { fileURLToPath, pathToFileURL } from "url";
import JavaError from "./JavaError.js";
import { promisify } from "util";
import { isValidFileExtension } from "./FileExtensions.js";
import WeaverMessageFromLauncher from "./WeaverMessageFromLauncher.js";

let directExecution = false;

if (fileURLToPath(import.meta.url) === process.argv[1]) {
  directExecution = true;
} else {
  directExecution = false;
}

java.asyncOptions = {
  asyncSuffix: "Async",
  syncSuffix: "",
  promiseSuffix: "P",
  promisify: promisify,
};

export class Weaver {
  static #isSetup = false;
  static #javaWeaver: unknown;

  static isSetup() {
    return Weaver.#isSetup;
  }

  static async awaitSetup() {
    while (!Weaver.isSetup()) {
      await new Promise((resolve) => setTimeout(resolve, 100));
    }
  }

  static async setupJavaEnvironment(jarFilePath: string) {
    java.classpath.push(jarFilePath);
    await java.ensureJvm();
  }

  static async setupWeaver(
    args: WeaverMessageFromLauncher["args"],
    config: WeaverMessageFromLauncher["config"]
  ) {
    // Create debug instance
    const debug = Debug(`Weaver:${config.weaverPrettyName}`);
    debug("Initiating weaver setup.");

    await this.setupJavaEnvironment(config.jarFilePath);

    debug(`${config.weaverPrettyName} execution arguments: %O`, args);

    const javaWeaverClassName = config.javaWeaverQualifiedName.match(
      new RegExp("(?<=\\.)\\w+$")
    )?.[0];

    if (javaWeaverClassName === undefined || javaWeaverClassName === null) {
      throw new Error("Invalid weaver class name.");
    }

    /* eslint-disable */
    // This code is intentionally ignored by eslint
    const JavaArrayList = java.import("java.util.ArrayList");
    const JavaFile = java.import("java.io.File");
    const LaraiKeys = java.import(
      "org.lara.interpreter.joptions.config.interpreter.LaraiKeys"
    );
    const NodeJsEngine = java.import("pt.up.fe.specs.jsengine.NodeJsEngine");
    const JavaEventTrigger = java.import(
      "org.lara.interpreter.weaver.events.EventTrigger"
    );
    const JavaWeaverClass = java.import(config.javaWeaverQualifiedName);

    const fileList = new JavaArrayList();
    //const [command, clangArgs, env] = await Sandbox.splitCommandArgsEnv(args._[1]);
    const clangArgs = args._.slice(1);
    clangArgs.forEach((arg: string | number) => {
      fileList.add(new JavaFile(arg));
    });

    const javaWeaver = new JavaWeaverClass();
    javaWeaver.setWeaver();
    javaWeaver.setScriptEngine(new NodeJsEngine());
    javaWeaver.setEventTrigger(new JavaEventTrigger());

    let laraIDataStore;
    if (args.configClassic !== undefined && args.configClassic !== null) {
      try {
        const OptionsParser = java.import(
          "org.lara.interpreter.cli.OptionsParser"
        );

        const laraiDefinition = OptionsParser.getLaraStoreDefinition(javaWeaver);
        const appPersistence = OptionsParser.getXmlPersistence(laraiDefinition);
        laraIDataStore = appPersistence.loadData(new JavaFile(args.configClassic));
      } catch (error) {
        throw new Error("Failed to load Clava Classic configuration file.");
      }
    } else {
      const JavaLaraIDataStore = java.import(
        "org.lara.interpreter.joptions.config.interpreter.LaraIDataStore"
      );
      const JavaDataStore = java.import(
        "org.suikasoft.jOptions.Interfaces.DataStore"
      );

      const datastore = await new JavaDataStore.newInstanceP(
        `${javaWeaverClassName}DataStore`
      );

      laraIDataStore = new JavaLaraIDataStore(null, datastore, javaWeaver);
    }

    laraIDataStore.set(LaraiKeys.LARA_FILE, new JavaFile("placeholderFileName"));

    javaWeaver.begin(
      fileList,
      new JavaFile(JavaWeaverClass.getWovenCodeFoldername()),
      laraIDataStore.getWeaverArgs()
    );
    /* eslint-enable */

    Object.defineProperty(globalThis, config.weaverName, {
      value: new (class {
        get rootJp() {
          // eslint-disable-next-line @typescript-eslint/no-unsafe-return, @typescript-eslint/no-unsafe-member-access, @typescript-eslint/no-unsafe-call
          return javaWeaver.getRootJp();
        }
        get weaver() {
          // eslint-disable-next-line @typescript-eslint/no-unsafe-return
          return javaWeaver;
        }
      })(),
      enumerable: false,
      configurable: true,
      writable: false,
    });

    Weaver.#isSetup = true;
    Weaver.#javaWeaver = javaWeaver;
    // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
    return debug;
  }

  static async executeScript(
    args: WeaverMessageFromLauncher["args"],
    config: WeaverMessageFromLauncher["config"],
    debug: Debug.Debugger
  ) {
    debug("Executing user script...");
    if (
      typeof args.scriptFile === "string" &&
      fs.existsSync(args.scriptFile) &&
      isValidFileExtension(path.extname(args.scriptFile))
    ) {
      // import is using a URL converted to string.
      // The URL is used due to a Windows error with paths. See https://stackoverflow.com/questions/69665780/error-err-unsupported-esm-url-scheme-only-file-and-data-urls-are-supported-by
      // The conversion of the URl back to a string is due to a TS bug. See https://github.com/microsoft/TypeScript/issues/42866
      await import(pathToFileURL(path.resolve(args.scriptFile)).toString())
        .then(() => {
          debug("Execution completed successfully.");
        })
        .catch((error: JavaError) => {
          console.error("Execution failed.");

          if (error.cause !== undefined && error.cause !== null) {
            // Java exception
            console.error(error.cause.getMessage());
          }
          debug(error);
        });
    } else {
      new Error("Invalid file path or file type.");
    }
  }

  static shutdown() {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access, @typescript-eslint/no-unsafe-call, @typescript-eslint/no-explicit-any
    (Weaver.#javaWeaver as any).close();
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
    // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
    const debug = await Weaver.setupWeaver(
      messageFromParent.args,
      messageFromParent.config
    );

    if (directExecution) {
      await Weaver.executeScript(
        messageFromParent.args,
        messageFromParent.config,
        debug
      );

      Weaver.shutdown();

      debug("Exiting...");
      process.exit(0);
    }
  })
  .catch((error) => {
    console.error(error);
  });
