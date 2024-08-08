import Io from "./Io.js";
import Platforms from "./Platforms.js";
import { info } from "./core/LaraCore.js";
import JavaTypes, { JavaClasses } from "./util/JavaTypes.js";
import TimeUnits, { TimerUnit } from "./util/TimeUnits.js";

export default class System {
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
      exe = Io.getPath(executableFolder, executableName).getAbsolutePath();
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
    let timeoutNanos = undefined;
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

  static getCurrentFile(): string | undefined {
    return this.getCurrentFilePrivate(3);
  }

  static getCurrentFolder(): string | undefined {
    const filepath = this.getCurrentFilePrivate(3);

    if (filepath === undefined) {
      return undefined;
    }

    return Io.getPath(filepath).getParentFile().getAbsolutePath();
  }

  private static getCurrentFilePrivate(depth: number): string | undefined {
    // Store originakl stack trace limit
    const originalStackTraceLimit = Error.stackTraceLimit;

    // Set to the depth we want to go
    Error.stackTraceLimit = depth;

    // Create an Error to capture the stack trace
    const err = new Error();

    // Restore original stack trace limit
    Error.stackTraceLimit = originalStackTraceLimit;

    // Process the stack trace
    const stackTrace = err.stack;

    // Return if no stack trace was obtained
    if (stackTrace === undefined) {
      return undefined;
    }

    // Split the stack trace into lines
    const stackLines = stackTrace.split("\n");

    // The stack trace format is usually:
    // at FunctionName (filePath:lineNumber)
    // Go to the depth we are interested in

    let stackline;
    let currentDepth = 0;
    for (let i = 0; i < stackLines.length; i++) {
      console.log("Line " + i + ": " + stackLines[i]);
      const match = stackLines[i].match(/\(([^)]+)\)/);
      if (match && match[1]) {
        currentDepth++;
        stackline = match[1];

        if (currentDepth === depth) {
          break;
        }
      }
    }

    // Could not find a stack line at the required depth
    if (stackline === undefined) {
      return undefined;
    }

    // Extract file path
    const lastColonIndex = stackline.lastIndexOf(":");
    const filePathTemp =
      lastColonIndex == -1 ? stackline : stackline.substring(0, lastColonIndex);

    console.log("Potential Path: " + filePathTemp);

    let file = Io.getPath(filePathTemp);

    if (!file.isAbsolute()) {
      console.log("Base dir: " + __dirname);
      file = Io.getPath(Io.getPath(__dirname), file.getPath());
    }

    const absolutePath = file.getAbsolutePath();
    return absolutePath;
  }
}
