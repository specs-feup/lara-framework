import TimeUnits, { TimerUnit } from "./util/TimeUnits.js";
import JavaTypes, { JavaClasses } from "./util/JavaTypes.js";
import Io from "./Io.js";
import { notImplemented, info } from "./core/LaraCore.js";
import Platforms from "./Platforms.js";

export default class System {
  /**
   * Returns the name of the platform where this code is executing
   */
  static getCurrentPlatform() {
    notImplemented("getCurrentPlatform");
  }

  static prepareExe(executable: string | JavaClasses.File): string {
    return JavaTypes.LARASystem.prepareExe(executable);
  }

  static getExecutableFile(
    executableName: string,
    executableFolder?: string | JavaClasses.File,
    isInPath: boolean = false
  ): string {
    let exe: string;
    if (executableFolder === undefined) {
      exe = executableName;
    } else {
      exe = Io.getPath(
        executableFolder,
        executableName
      ).getAbsolutePath() as string;
    }

    // Ensure exe is a string
    exe = exe.toString();

    if (Platforms.isUnix()) {
      const addDotSlash =
        !exe.startsWith("./") && !exe.startsWith("/") && !isInPath;
      if (addDotSlash) {
        exe = "./" + exe;
      }
    }

    if (Platforms.isWindows() && !exe.endsWith(".exe")) {
      exe = exe + ".exe";
    }

    if (executableFolder !== undefined && !Io.isFile(exe)) {
      throw "System.getExecutableFile: Could not find executable '" + exe + "'";
    }

    return exe;
  }

  /**
   * @deprecated Please use System.getExecutableFile
   */
  static getExecutableName(baseName: string): string {
    info(
      "DEPRECATED, please use System.getExecutableFile",
      "System.getExecutableName"
    );
    let exeName = baseName;
    if (Platforms.isWindows()) {
      exeName = exeName + ".exe";
    } else {
      exeName = "./" + exeName;
    }

    return exeName;
  }

  /**
   * Controls whether by default, if the execution of commands should print the output to the console
   **/
  static defaultPrintToConsole: boolean = true;

  /**
   * @returns Can be undefined, null or the name of a file. If undefined, prints the output to the console; if null, does not print the output to the console; otherwise should be a string with the name of the file where the output will be written (in this case, no output is printed in the console).
   */
  static execute(
    command: string,
    workingDir: string = "./",
    printToConsole: boolean = this.defaultPrintToConsole,
    outputFile?: string | JavaClasses.File,
    append: boolean = false,
    timeout?: number,
    timeunit: TimeUnits = new TimeUnits(TimerUnit.SECONDS)
  ): string | undefined {
    let timeoutNanos = null;
    if (timeout !== undefined) {
      timeoutNanos = timeunit.toNanos(timeout);
    }

    const executeOutput = JavaTypes.LaraSystemTools.runCommand(
      command,
      workingDir,
      printToConsole,
      timeoutNanos
    ).getOutput();

    if (executeOutput !== undefined && typeof executeOutput !== "string") {
      throw new Error(
        `Expected output to be a string, but it is ${typeof executeOutput}`
      );
    }

    return executeOutput;
  }

  static sleep(durantionMilis: number): void {
    JavaTypes.SpecsSystem.sleep(durantionMilis);
  }

  /**
   * @returns The current value of the running Java Virtual Machine's high-resolution time source, in nanoseconds
   */
  static nanos(): number {
    return JavaTypes.System.nanoTime();
  }

  static toc(nanoStart: number, message: string = "Time"): string {
    return JavaTypes.SpecsStrings.takeTime(message, nanoStart);
  }

  static getNumLogicalCores(): number {
    return JavaTypes.Runtime.getRuntime().availableProcessors();
  }
}
