import Debug from "debug";
import yargs from "yargs";
import * as path from "path";
import * as chokidar from "chokidar";
import { hideBin } from "yargs/helpers";
import { fork } from "child_process";
import {
  addActiveChildProcess,
  activeChildProcesses,
  listenForTerminationSignals,
} from "./ChildProcessHandling.js";

listenForTerminationSignals();

export default class WeaverLauncher {
  debug!: Debug.Debugger;

  weaverName!: string;
  weaverPrettyName!: string;
  weaverFileName!: string;

  commandLineArgumentsConfig!: yargs.Arguments;

  midExecution = false;

  constructor(
    weaverName: string,
    prettyWeaverName: string = WeaverLauncher.capitalizeFirstLetter(weaverName)
  ) {
    this.weaverName = weaverName;
    this.weaverPrettyName = prettyWeaverName;
    this.weaverFileName = `${prettyWeaverName}.js`;
    this.debug = Debug(`WeaverLauncher:${prettyWeaverName}:main`);
  }

  async execute(): Promise<void> {
    this.commandLineArgumentsConfig = await this.generateConfig().parse();
  }

  static capitalizeFirstLetter(string: string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
  }

  protected main(args: yargs.Arguments): void {
    this.debug(`${this.weaverPrettyName} execution arguments: %O`, args);
    void this.executeWeaver(args);

    if (args.watch) {
      for (const directory of args.watch as string[]) {
        this.debug(`Watching directory: ${directory}`);
      }

      chokidar
        .watch(args.watch as string[], { ignoreInitial: true })
        .on("all", (event: string, filepath: string) => {
          try {
            return this.filesystemEventHandler(event, filepath, args);
          } catch (error) {
            console.error(error);
          }
        });
    }
  }

  protected filesystemEventHandler(
    event: string,
    filepath: string,
    args: yargs.Arguments
  ) {
    this.debug(
      `Source file event: ${WeaverLauncher.capitalizeFirstLetter(
        event
      )} '${filepath}'`
    );
    void this.executeWeaver(args);
  }

  async executeWeaver(args: yargs.Arguments) {
    if (this.midExecution) return;
    this.midExecution = true;
    const activeProcess = Object.values(activeChildProcesses)[0];

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

    addActiveChildProcess(
      fork(path.join("dist", this.weaverFileName), [JSON.stringify(args)])
    );
    this.midExecution = false;
  }

  protected generateConfig() {
    return yargs(hideBin(process.argv))
      .scriptName(this.weaverName)
      .command({
        command: "$0 [script-file]",
        describe: `Execute a ${this.weaverPrettyName} script`,
        builder: (yargs) => {
          return yargs
            .positional("script-file", {
              describe: `Path to ${this.weaverPrettyName} script file`,
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
            });
        },
        handler: (argv) => {
          try {
            console.log(`Executing ${this.weaverPrettyName} script...`);
            void this.main(argv);
          } catch (error) {
            console.error(error);
          }
        },
      })
      .command({
        command: "init",
        describe: `Initialize a new ${this.weaverPrettyName} project`,
        handler: () => {
          // TODO: Implement
          console.log(`Initializing new ${this.weaverPrettyName} project...`);
        },
      })
      .help()
      .showHelpOnFail(true)
      .strict()
      .pkgConf(this.weaverName);
  }
}
