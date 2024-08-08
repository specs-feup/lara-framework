import Io from "./Io.js";
import Platforms from "./Platforms.js";
import { info } from "./core/LaraCore.js";
import JavaTypes from "./util/JavaTypes.js";
import TimeUnits, { TimerUnit } from "./util/TimeUnits.js";
export default class System {
    static prepareExe(executable) {
        return JavaTypes.LARASystem.prepareExe(executable);
    }
    static getExecutableFile(executableName, executableFolder, isInPath = false) {
        let exe;
        if (executableFolder === undefined) {
            exe = executableName;
        }
        else {
            exe = Io.getPath(executableFolder, executableName).getAbsolutePath();
        }
        // Ensure exe is a string
        exe = exe.toString();
        if (Platforms.isUnix()) {
            const addDotSlash = !exe.startsWith("./") && !exe.startsWith("/") && !isInPath;
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
    static getExecutableName(baseName) {
        info("DEPRECATED, please use System.getExecutableFile", "System.getExecutableName");
        let exeName = baseName;
        if (Platforms.isWindows()) {
            exeName = exeName + ".exe";
        }
        else {
            exeName = "./" + exeName;
        }
        return exeName;
    }
    /**
     * Controls whether by default, if the execution of commands should print the output to the console
     **/
    static defaultPrintToConsole = true;
    /**
     * @returns Can be undefined, null or the name of a file. If undefined, prints the output to the console; if null, does not print the output to the console; otherwise should be a string with the name of the file where the output will be written (in this case, no output is printed in the console).
     */
    static execute(command, workingDir = "./", printToConsole = this.defaultPrintToConsole, outputFile, append = false, timeout, timeunit = new TimeUnits(TimerUnit.SECONDS)) {
        let timeoutNanos = undefined;
        if (timeout !== undefined) {
            timeoutNanos = timeunit.toNanos(timeout);
        }
        const executeOutput = JavaTypes.LaraSystemTools.runCommand(command, workingDir, printToConsole, timeoutNanos).getOutput();
        if (executeOutput !== undefined && typeof executeOutput !== "string") {
            throw new Error(`Expected output to be a string, but it is ${typeof executeOutput}`);
        }
        return executeOutput;
    }
    static sleep(durantionMilis) {
        JavaTypes.SpecsSystem.sleep(durantionMilis);
    }
    /**
     * @returns The current value of the running Java Virtual Machine's high-resolution time source, in nanoseconds
     */
    static nanos() {
        return JavaTypes.System.nanoTime();
    }
    static toc(nanoStart, message = "Time") {
        return JavaTypes.SpecsStrings.takeTime(message, nanoStart);
    }
    static getNumLogicalCores() {
        return JavaTypes.Runtime.getRuntime().availableProcessors();
    }
    static getCurrentFile() {
        return this.getCurrentFilePrivate(3);
    }
    static getCurrentFilePrivate(depth) {
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
        const filePathTemp = lastColonIndex == -1 ? stackline : stackline.substring(0, lastColonIndex);
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
//# sourceMappingURL=System.js.map