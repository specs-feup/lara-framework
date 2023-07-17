import JavaTypes from "./util/JavaTypes.js";
import { checkDefined, isArray, isJavaClass, checkTrue, } from "./core/LaraCore.js";
import { JSONtoFile, fileToJSON } from "../core/output.js";
/**
 * Utility methods related with input/output operations on files.
 */
export default class Io {
    /**
     * @returns true if the given object is a Java file, false otherwise.
     */
    static isJavaFile(object) {
        return isJavaClass(object, "java.io.File");
    }
    static newFile(path) {
        if (Io.isJavaFile(path)) {
            path = path.toString();
        }
        return new JavaTypes.File(path.toString());
    }
    static newFileWithBase(base, path) {
        // If path is a file, convert to String first
        if (Io.isJavaFile(path)) {
            path = path.toString();
        }
        // If base is not a file, convert to File first
        if (!Io.isJavaFile(base)) {
            base = Io.newFile(base);
        }
        return new JavaTypes.File(base, path);
    }
    /**
     * Creates a folder.
     */
    static mkdir(fileOrBaseFolder, optionalFile) {
        return JavaTypes.SpecsIo.mkdir(Io.getPath(fileOrBaseFolder, optionalFile));
    }
    /**
     * Global version of the function.
     */
    static fmkdir(fileOrBaseFolder, optionalFile) {
        return Io.mkdir(fileOrBaseFolder, optionalFile);
    }
    /**
     * If folderName is undefined, returns the OS temporary folder.
     * If defined, creates a new folder inside the system's temporary folder and returns it.
     *
     */
    static getTempFolder(folderName) {
        if (folderName === undefined) {
            return JavaTypes.SpecsIo.getTempFolder();
        }
        return JavaTypes.SpecsIo.getTempFolder(folderName);
    }
    /**
     * Creates a randomly named folder in the OS temporary folder that is deleted when the virtual machine exits.
     */
    static newRandomFolder() {
        return JavaTypes.SpecsIo.newRandomFolder();
    }
    static getPath(fileOrBaseFolder, optionalFile) {
        if (optionalFile === undefined) {
            return Io.newFile(fileOrBaseFolder);
        }
        // Test if optionalFile is absolute
        const optionalFilePath = Io.newFile(optionalFile);
        if (optionalFilePath.isAbsolute()) {
            return optionalFilePath;
        }
        return Io.newFileWithBase(fileOrBaseFolder, optionalFile);
    }
    /**
     * @returns the absolute path of the given file
     */
    static getAbsolutePath(fileOrBaseFolder, optionalFile) {
        return JavaTypes.SpecsIo.normalizePath(JavaTypes.SpecsIo.getCanonicalPath(Io.getPath(fileOrBaseFolder, optionalFile)));
    }
    /**
     * Gets the paths (files and folders) in the given folder, corresponding to the given base folder and argument patterns.
     *
     * @param baseFolder -
     * @param args - Patterns to match
     */
    static getPaths(baseFolder, ...args) {
        checkDefined(baseFolder, "baseFolder", "Io.getPaths");
        const baseFolderFile = Io.getPath(baseFolder);
        // For each argument after the baseFolder, treat it as a different file/glob
        const files = [];
        // If empty, add all files
        const argsArray = args;
        if (argsArray.length === 0) {
            argsArray.push("*");
        }
        for (const argument of argsArray) {
            const foundFiles = JavaTypes.SpecsIo.getPathsWithPattern(baseFolderFile, argument, false, "FILES_AND_FOLDERS");
            for (const file of foundFiles) {
                files.push(file);
            }
        }
        return files;
    }
    /**
     * Gets the folders in the given folder, corresponding to the given base folder and argument patterns.
     *
     * @param baseFolder -
     * @param args - Patterns to match
     */
    static getFolders(baseFolder, ...args) {
        const paths = Io.getPaths(baseFolder, ...args);
        const folders = [];
        for (const path of paths) {
            if (Io.isFolder(path)) {
                folders.push(path);
            }
        }
        return folders;
    }
    /**
     * The files inside the given folder that respects the given pattern.
     *
     * @param baseFolder -
     * @param {string|Object[]} pattern -
     * @param isRecursive -
     */
    static getFiles(baseFolder = "./", pattern = "*", isRecursive = false) {
        // If pattern is an array, call function recursively
        if (isArray(pattern)) {
            const files = [];
            for (const singlePattern of pattern) {
                const newFiles = Io.getFiles(baseFolder, singlePattern, isRecursive);
                for (const newFile of newFiles) {
                    files.push(newFile);
                }
            }
            return files;
        }
        const list = JavaTypes.SpecsIo.getPathsWithPattern(Io.getPath(baseFolder), pattern.toString(), isRecursive, "FILES");
        const files = [];
        for (const file of list) {
            files.push(file);
        }
        return files;
    }
    /**
     * Returns a List with a string for each line of the given file
     */
    static readLines(fileOrBaseFolder, optionalFile) {
        return JavaTypes.LaraIo.readLines(Io.getPath(fileOrBaseFolder, optionalFile));
    }
    /**
     * Deletes the given file.
     */
    static deleteFile(fileOrBaseFolder, optionalFile) {
        const file = Io.getPath(fileOrBaseFolder, optionalFile);
        if (!Io.isFile(file)) {
            return;
        }
        return JavaTypes.LaraIo.deleteFile(file);
    }
    /**
     * Each argument is a file that will be deleted.
     */
    static deleteFiles(...args) {
        for (const argument of args) {
            Io.deleteFile(argument);
        }
    }
    /**
     * Deletes a folder and its contents.
     *
     * @returns true if both the contents and the folder could be deleted
     */
    static deleteFolder(folderPath) {
        const folder = Io.getPath(folderPath);
        return JavaTypes.SpecsIo.deleteFolder(folder);
    }
    /**
     * Deletes the contents of a folder.
     */
    static deleteFolderContents(folderPath) {
        const folder = Io.getPath(folderPath);
        return JavaTypes.SpecsIo.deleteFolderContents(folder);
    }
    /**
     * @returns true if and only if the file denoted by this abstract pathname exists and is a normal file; false otherwise
     */
    static isFile(path) {
        if (typeof path === "string") {
            path = Io.getPath(path);
        }
        return path.isFile();
    }
    /**
     * @returns true if and only if the file denoted by this abstract pathname exists and is a folder; false otherwise
     */
    static isFolder(path) {
        if (typeof path === "string") {
            path = Io.getPath(path);
        }
        return path.isDirectory();
    }
    static readJson(path) {
        return fileToJSON(path);
    }
    /**
     * @deprecated Use JSONtoFile from api/core/output.js instead
     */
    static writeJson(path, object) {
        JSONtoFile(path, object);
    }
    static copyFile(filepath, destination) {
        checkDefined(filepath, "filepath", "Io.copyFile");
        checkTrue(Io.isFile(filepath), "Io.copyFile: given filepath '" + filepath + "' is not a file");
        return JavaTypes.SpecsIo.copy(Io.getPath(filepath), Io.getPath(destination));
    }
    static copyFolder(filepath, destination, verbose = false) {
        checkDefined(filepath, "filepath", "Io.copyFolder");
        checkDefined(destination, "destination", "Io.copyFolder");
        return JavaTypes.SpecsIo.copyFolder(Io.getPath(filepath), Io.getPath(destination), verbose);
    }
    /**
     * Returns the given path, without extension.
     *
     */
    static removeExtension(path) {
        return JavaTypes.SpecsIo.removeExtension(path);
    }
    /**
     * Returns the extension of the given path.
     *
     */
    static getExtension(path) {
        return JavaTypes.SpecsIo.getExtension(path);
    }
    /**
     * @param path - The path of the file to write.
     * @param content - The contents to write.
     *
     * @returns The file to where the contents where written.
     */
    static writeFile(path, content) {
        const file = Io.newFile(path);
        JavaTypes.SpecsIo.write(file, content);
        return file;
    }
    /**
     * @param path - The path of the file to read.
     *
     * @returns The contents of the file.
     */
    static readFile(path) {
        const file = Io.newFile(path);
        const content = JavaTypes.SpecsIo.read(file);
        return content;
    }
    static appendFile(path, content) {
        const file = Io.newFile(path);
        JavaTypes.SpecsIo.append(file, content);
    }
    /**
     * Returns the path of 'targetFile', relative to 'baseFile'.
     *
     * If the file does not share a common ancestor with baseFile, returns undefined.
     */
    static getRelativePath(targetFile, baseFile) {
        const relativePath = JavaTypes.SpecsIo.getRelativePath(Io.getPath(targetFile), Io.getPath(baseFile));
        if (Io.getPath(relativePath).isAbsolute()) {
            return undefined;
        }
        return relativePath;
    }
    /**
     * 	The system-dependent path-separator (e.g., : or ;).
     */
    static getPathSeparator() {
        return JavaTypes.File.pathSeparator;
    }
    /**
     * 	The system-dependent name-separator (e.g., / or \).
     */
    static getSeparator() {
        return JavaTypes.File.separator;
    }
    static md5(fileOrBaseFolder, optionalFile) {
        return JavaTypes.SpecsIo.getMd5(Io.getPath(fileOrBaseFolder, optionalFile));
    }
    static getWorkingFolder() {
        return JavaTypes.SpecsIo.getWorkingDir();
    }
    /**
     * If value is a string that ends in .json, assume it is a file to a json object and parses it.
     * If it is a string but does not end in json, assume it is a stringified object.
     * Otherwise, returns the object as it is.
     *
     * @deprecated Use JSON.parse() instead or fileToJSON from api/core/output.js
     */
    static jsonObject(value) {
        if (typeof value === "string") {
            if (value.endsWith(".json")) {
                return this.readJson(value);
            }
            else {
                return JSON.parse(value);
            }
        }
        return value;
    }
}
//# sourceMappingURL=Io.js.map