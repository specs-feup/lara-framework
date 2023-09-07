import Io from "../Io.js";
import JavaInterop from "../JavaInterop.js";
import { arrayFromArgs, checkType } from "../core/LaraCore.js";
import JavaTypes, { JavaClasses } from "./JavaTypes.js";
import TimeUnits, { TimerUnit } from "./TimeUnits.js";

/**
 * Launches processes.
 */
export default class ProcessExecutor {
  // Configuration
  workingDir: string = "./";
  printToConsole: boolean = true;
  outputFile: string | undefined = undefined;
  append: boolean = false;
  timeUnit = new TimeUnits(TimerUnit.SECONDS);
  timeout: number | undefined = undefined;
  outputPrefix: string | undefined = undefined;
  stopOnError: boolean = false;
  logErrorsOnly: boolean = false;

  // Private state
  private lastOutput: any | undefined = undefined;
  private lastCommand: string[] | undefined = undefined;
  private _customReturnValue: number | undefined = undefined;

  setWorkingDir(workingDir: string) {
    this.workingDir = workingDir;
    return this;
  }

  getWorkingDir() {
    return this.workingDir;
  }

  /**
   * Sets if the output of the process should be output to the console.
   *
   */
  setPrintToConsole(printToConsole: boolean = true) {
    this.printToConsole = printToConsole;
    return this;
  }

  setOutputFile(outputFile: string) {
    this.outputFile = outputFile;
    return this;
  }

  /**
   * Sets if the output should be appended to the output of the previous execution.
   *
   */
  setAppend(append: boolean = true) {
    this.append = append;
    return this;
  }

  /**
   * If true, throws an exception if there is a problem when running the process. By default is set to 'false'.
   */
  setStopOnError(stopOnError: boolean = true) {
    this.stopOnError = stopOnError;
    return this;
  }

  setLogErrorsOnly(logErrorsOnly: boolean = true) {
    this.logErrorsOnly = logErrorsOnly;
    return this;
  }

  /**
   * Sets the time unit of the timeout value. By defaults, is SECONDS.
   */
  setTimeUnit(timeUnit: TimerUnit) {
    this.timeUnit = new TimeUnits(timeUnit);
    return this;
  }

  setTimeout(timeout: number) {
    this.timeout = timeout;
    return this;
  }

  /**
   * @param returnValue - An integer that represents the return value of the process executor
   */
  setCustomReturnValue(returnValue: string): this;
  setCustomReturnValue(returnValue: number): this;
  setCustomReturnValue(returnValue: number | string) {
    if (typeof returnValue === "string") {
      // Convert string to integer
      returnValue = parseInt(returnValue, 10);
    }

    this._customReturnValue = returnValue;
    return this;
  }

  setOutputPrefix(outputPrefix: string) {
    this.outputPrefix = outputPrefix;
    return this;
  }

  /**
   *
   * @param command - The command to be executed. Accepts a command as a sequence of strings, or as an array with strings.
   */
  execute(...command: string[]): string | undefined {
    command = arrayFromArgs(command, 1) as string[];

    // If command is an array, make sure all arguments are strings
    if (command instanceof Array) {
      command = command.map((element) => element.toString());
    }

    this.lastCommand = command;

    // Important to use 0, if  it is null/undefined, calling the Java method will fail, will not be able to convert to Long
    let timeoutNanos = 0;
    if (this.timeout !== undefined) {
      timeoutNanos = this.timeUnit.toNanos(this.timeout);
    }

    let javaCommand: string[] | JavaClasses.List = command;

    // If Java command is an array, make sure array list is used
    if (javaCommand instanceof Array) {
      javaCommand = JavaInterop.arrayToList(javaCommand);
    }

    // TODO: Can change LaraApiTools.runCommand to accept a file and append flag, to be able to write to a file in stream mode
    this.lastOutput = JavaTypes.LaraSystemTools.runCommand(
      javaCommand,
      this.workingDir.toString(),
      this.printToConsole,
      timeoutNanos
    );

    if (this.getReturnValue() !== 0) {
      console.log(
        "ProcessExecutor.execute: process returned with value '" +
          this.getReturnValue() +
          "', which might signal a problem. Under these conditions, it is not guaranteed that we can obtain the output of the application. Please run the application directly in the a terminal."
      );
      console.log("Executed command: " + command.join(" "));
    }

    if (this.stopOnError && this.getReturnValue() !== 0) {
      throw (
        "Problem while running command '" +
        command.join(" ") +
        "'\nError: " +
        this.getConsoleOutput()
      );
    }

    let executeOutput: string | undefined = undefined;
    if (this.logErrorsOnly) {
      executeOutput = this.getStdErr();
    } else {
      executeOutput = this.getConsoleOutput();
    }

    if (executeOutput !== undefined) {
      checkType(executeOutput, "string");
    }

    // After previous TODO is done, this can be removed
    if (this.outputFile !== undefined && executeOutput !== undefined) {
      let outputContents = executeOutput;
      if (this.outputPrefix !== undefined && executeOutput.length > 0) {
        outputContents = this.outputPrefix + outputContents;
      }

      if (this.append) {
        Io.appendFile(this.outputFile, outputContents);
      } else {
        Io.writeFile(this.outputFile, outputContents);
      }
    }

    return executeOutput;
  }

  getCommand() {
    if (this.lastCommand === undefined) {
      console.log(
        "ProcessExecutor.getLastCommand: no execution has been done yet"
      );
    }

    return this.lastCommand;
  }

  /**
   * @returns The file of the executable of the command, or undefined if could not locate the file.
   */
  getExecutableFile(): JavaClasses.File | undefined {
    const command = this.getCommand();
    if (command === undefined) {
      return undefined;
    }

    // Get index of first space
    const commandString = command.toString();
    let endIndex = commandString.indexOf(" ");
    // If no space, use full command
    if (endIndex === -1) {
      endIndex = commandString.length;
    }

    const exeName = commandString.substring(0, endIndex);

    // Try to get the file, just with the name
    let exeFile = Io.getPath(exeName);

    if (Io.isFile(exeFile)) {
      return exeFile;
    }

    // Try again, but this time using the working directory
    exeFile = Io.getPath(this.getWorkingDir(), exeName);

    if (Io.isFile(exeFile)) {
      return exeFile;
    }

    // Could not find the exe file
    return undefined;
  }

  getConsoleOutput() {
    if (this.lastOutput === undefined) {
      console.log(
        "ProcessExecutor.getConsoleOutput: no execution has been done yet"
      );
      return undefined;
    }

    return this.lastOutput.getOutput() as string;
  }

  getReturnValue(): number | undefined {
    // Give priority to custom value
    if (this._customReturnValue !== undefined) {
      return this._customReturnValue;
    }

    if (this.lastOutput === undefined) {
      console.log(
        "ProcessExecutor.getReturnValue: no execution has been done yet"
      );
      return undefined;
    }

    return this.lastOutput.getReturnValue() as number;
  }

  getStdOut() {
    if (this.lastOutput === undefined) {
      console.log("ProcessExecutor.getStdOut: no execution has been done yet");
      return undefined;
    }

    return this.lastOutput.getStdOut();
  }

  getStdErr() {
    if (this.lastOutput === undefined) {
      console.log("ProcessExecutor.getStdErr: no execution has been done yet");
      return undefined;
    }

    return this.lastOutput.getStdErr();
  }

  // TODO: Implement for printToConsole, outputFile, append, timeUnit
}
