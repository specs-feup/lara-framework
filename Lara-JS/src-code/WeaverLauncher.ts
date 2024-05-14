import Debug from "debug";
import yargs, { Arguments } from "yargs";
import * as path from "path";
import * as chokidar from "chokidar";
import { hideBin } from "yargs/helpers";
import { fork } from "child_process";
import { fileURLToPath } from "url";
import { dirname } from "path";
import {
  addActiveChildProcess,
  getActiveChildProcesses,
  listenForTerminationSignals,
} from "./ChildProcessHandling.js";
import WeaverConfiguration from "./WeaverConfiguration.js";
import WeaverMessageFromLauncher from "./WeaverMessageFromLauncher.js";

listenForTerminationSignals();

export default class WeaverLauncher {
  debug!: Debug.Debugger;

  private config!: WeaverConfiguration;

  private midExecution = false;

  constructor(config: WeaverConfiguration) {
    this.config = config;
    this.debug = Debug(`WeaverLauncher:${this.config.weaverPrettyName}:main`);
  }

  async execute(customArgs: string | undefined = undefined): Promise<void> {
    await this.generateConfig(customArgs).parse();
  }

  static capitalizeFirstLetter(string: string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
  }

  protected main(args: Arguments): void {
    this.debug(`${this.config.weaverPrettyName} execution arguments: %O`, args);
    void this.executeWeaver(args);

    if (args.watch) {
      for (const directory of args.watch as string[]) {
        this.debug(`Watching directory: ${directory}`);
      }

      chokidar
        .watch(args.watch as string[], { ignoreInitial: true })
        .on("all", (event: string, filepath: string) => {
          try {
            this.debug(
              `Source file event: ${WeaverLauncher.capitalizeFirstLetter(
                event
              )} '${filepath}'`
            );
            void this.executeWeaver(args);
          } catch (error) {
            console.error(error);
          }
        });
    }
  }

  protected async executeWeaver(args: Arguments) {
    if (this.midExecution) return;
    this.midExecution = true;
    const activeProcess = Object.values(getActiveChildProcesses())[0];

    if (activeProcess?.exitCode === null) {
      const promise = new Promise((resolve) => {
        activeProcess.once("exit", (code) => {
          resolve(code);
        });
      });
      if (activeProcess.kill()) {
        await promise;
        this.debug("Killed active process");
      } else {
        throw new Error("Could not kill active process");
      }
    }

    const child = fork(
      this.config.weaverFileName
        ? fileURLToPath(import.meta.resolve(this.config.weaverFileName))
        : path.join(dirname(fileURLToPath(import.meta.url)), "Weaver.js")
    );
    child.send({
      config: this.config,
      args,
    } as WeaverMessageFromLauncher);

    addActiveChildProcess(child);
    this.midExecution = false;
  }

  protected generateConfig(args: string | undefined = undefined) {
    return yargs(args ?? hideBin(process.argv))
      .scriptName(this.config.weaverName)
      .command({
        command: "$0 [script-file]",
        describe: `Execute a ${this.config.weaverPrettyName} script`,
        builder: (yargs) => {
          return yargs
            .positional("script-file", {
              describe: `Path to ${this.config.weaverPrettyName} script file`,
              type: "string",
            })
            .option("c", {
              alias: "config",
              describe: "Path to JSON config file",
              type: "string",
              config: true,
            })
            .option("w", {
              alias: "watch",
              describe: "Watch the following directory for changes",
              type: "array",
              default: [],
              defaultDescription: "none",
            })
            .option("config-classic", {
              describe: "Path to XML config file from Classic weaver",
              type: "string",
            });
        },
        handler: (argv) => {
          try {
            console.log(`Executing ${this.config.weaverPrettyName} script...`);
            void this.main(argv);
          } catch (error) {
            console.error(error);
          }
        },
      })
      .command({
        command: "init",
        describe: `Initialize a new ${this.config.weaverPrettyName} project`,
        handler: () => {
          // TODO: Implement
          console.log(
            `Initializing new ${this.config.weaverPrettyName} project...`
          );
        },
      })
      .help()
      .showHelpOnFail(true)
      .strict()
      .pkgConf(this.config.weaverName);
  }
}
