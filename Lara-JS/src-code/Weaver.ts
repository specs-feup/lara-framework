import * as fs from "fs";
import * as path from "path";
import java from "java";
import Debug from "debug";
import JavaError from "./JavaError.js";
import { promisify } from "util";
import { isValidFileExtension } from "./FileExtensions.js";
import WeaverMessageFromLauncher from "./WeaverMessageFromLauncher.js";

java.asyncOptions = {
  asyncSuffix: "Async",
  syncSuffix: "",
  promiseSuffix: "P",
  promisify: promisify,
};

function waitForMessage(): Promise<WeaverMessageFromLauncher> {
  return new Promise((resolve) => {
    process.once("message", (message: WeaverMessageFromLauncher) => {
      resolve(message);
    });
  });
}

// eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
const messageFromParent = await waitForMessage();

const args = messageFromParent.args;
const config = messageFromParent.config;

const debug = Debug(`Weaver:${config.weaverPrettyName}`);
debug("Starting weaver child process.");

//java.classpath.push("../../../.local/bin/Clava/Clava.jar");
java.classpath.push(config.jarFilePath);
await java.ensureJvm();

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
const JavaLaraIDataStore = java.import(
  "org.lara.interpreter.joptions.config.interpreter.LaraIDataStore"
);
const JavaDataStore = java.import(
  "org.suikasoft.jOptions.Interfaces.DataStore"
);
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

const datastore = await new JavaDataStore.newInstanceP(
  `${javaWeaverClassName}DataStore`
);
datastore.set(LaraiKeys.LARA_FILE, new JavaFile("placeholderFileName"));

const laraIDataStore = new JavaLaraIDataStore(null, datastore, javaWeaver);
javaWeaver.begin(
  fileList,
  new JavaFile(JavaWeaverClass.getWovenCodeFoldername()),
  laraIDataStore.getWeaverArgs()
);
/* eslint-enable */

Object.defineProperty(globalThis, config.weaverName, {
  value: new (class {
    get rootJp() {
      return (javaWeaver as { getRootJp(): unknown }).getRootJp();
    }
  })(),
  enumerable: false,
  configurable: true,
  writable: false,
});

debug("Executing user script...");

if (
  typeof args.scriptFile === "string" &&
  fs.existsSync(args.scriptFile) &&
  isValidFileExtension(path.extname(args.scriptFile))
) {
  await import(path.resolve(args.scriptFile))
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
  console.error("Invalid file path or file type.");
}

// eslint-disable-next-line @typescript-eslint/no-unsafe-member-access, @typescript-eslint/no-unsafe-call
javaWeaver.close();

debug("Exiting...");
process.exit(0);
