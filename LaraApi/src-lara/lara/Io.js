import { JSONtoFile, fileToJSON } from "../core/output.js";
import JavaTypes from "./util/JavaTypes.js";
/**
 * Utility methods related with input/output operations on files.
 */
export default class Io {
    /**
     * @param object - object to check
     *
     * @returns true if the given object is a Java file, false otherwise.
     */
    static isJavaFile(object) {
        if (typeof object === "string")
            return false;
        return JavaTypes.instanceOf(object, "java.io.File");
    }
    /**
     *
     * @param path - Path for the file creation
     *
     * @returns new File object with path given
     */
    static newFile(path) {
        if (Io.isJavaFile(path)) {
            path = path.toString();
        }
        return new JavaTypes.File(path.toString());
    }
    /**
     *
     * @param base - Parent pathname
     * @param path - Child pathname
     *
     * @returns a new File object from parent and child pathnames
     */
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
     *
     * @param fileOrBaseFolder - File object or path to the base folder
     * @param optionalFile - Optional child pathname or file, if absolute ignores fileorBaseFolder
     *
     * @returns the created folder
     */
    static mkdir(fileOrBaseFolder, optionalFile) {
        return JavaTypes.SpecsIo.mkdir(Io.getPath(fileOrBaseFolder, optionalFile));
    }
    /**
     *
     * If folderName is undefined, returns the OS temporary folder.
     * If defined, creates a new folder inside the system's temporary folder and returns it.
     * @param folderName - Optional name for the temporaray folder
     *
     * @returns the OS temporary folder or the created folder
     */
    static getTempFolder(folderName) {
        if (folderName === undefined) {
            return JavaTypes.SpecsIo.getTempFolder();
        }
        return JavaTypes.SpecsIo.getTempFolder(folderName);
    }
    /**
     * Creates a randomly named folder in the OS temporary folder that is deleted when the virtual machine exits.
     * @returns the created temporary folder
     */
    static newRandomFolder() {
        return JavaTypes.SpecsIo.newRandomFolder();
    }
    /**
     *
     * @param fileOrBaseFolder - File object or path to the base folder
     * @param optionalFile - Optional child pathname or file, if absolute ignores fileorBaseFolder
     *
     * @returns new File object with path given
     */
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
     * @param fileOrBaseFolder - File object or path to the base folder
     * @param optionalFile - Optional child pathname or file
     *
     * @returns the absolute path of the given file
     */
    static getAbsolutePath(fileOrBaseFolder, optionalFile) {
        return JavaTypes.SpecsIo.normalizePath(JavaTypes.SpecsIo.getCanonicalPath(Io.getPath(fileOrBaseFolder, optionalFile)));
    }
    /**
     *
     * @param baseFolder - File object or path to the base folder
     * @param args - Patterns to match
     *
     * @returns the paths (files and folders) in the given folder, corresponding to the given base folder and argument patterns.
     */
    static getPaths(baseFolder, ...args) {
        if (baseFolder === undefined)
            throw "base Folder is undefined";
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
            for (const file of foundFiles.toArray()) {
                files.push(file);
            }
        }
        return files;
    }
    /**
     *
     * @param baseFolder - File object or path to the base folder
     * @param args - Patterns to match
     *
     * @returns the folders in the given folder, corresponding to the given base folder and argument patterns.
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
     * @param baseFolder - File object or path to the base folder
     * @param pattern - Pattern to match
     * @param isRecursive - If true, search recursively inside the folder
     *
     * @returns the files inside the given folder that respects the given pattern.
     */
    static getFiles(baseFolder = "./", pattern = "*", isRecursive = false) {
        // If pattern is an array, call function recursively
        if (pattern instanceof Array) {
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
        for (let i = 0; i < list.size(); i++) {
            files.push(list.get(i));
        }
        return files;
    }
    /**
     * @param fileOrBaseFolder - File object or path to the base folder
     * @param optionalFile - Optional child pathname or file
     *
     * @returns a List with a string for each line of the given file
     */
    static readLines(fileOrBaseFolder, optionalFile) {
        return JavaTypes.LaraIo.readLines(Io.getPath(fileOrBaseFolder, optionalFile));
    }
    /**
     * @param fileOrBaseFolder - File object or path to the base folder
     * @param optionalFile - Optional child pathname or file
     *
     * @returns if the delete operation on the given file was successfull.
     */
    static deleteFile(fileOrBaseFolder, optionalFile) {
        const file = Io.getPath(fileOrBaseFolder, optionalFile);
        return JavaTypes.LaraIo.deleteFile(file);
    }
    /**
     * @param args - Each argument is a file that will be deleted.
     */
    static deleteFiles(...args) {
        for (const argument of args) {
            Io.deleteFile(argument);
        }
    }
    /**
     * Deletes a folder and its contents.
     *
     * @param folderPath - File object or path to the folder
     *
     * @returns true if both the contents and the folder could be deleted
     */
    static deleteFolder(folderPath) {
        const folder = Io.getPath(folderPath);
        return JavaTypes.SpecsIo.deleteFolder(folder);
    }
    /**
     * Deletes the contents of a folder.
     *
     * @param folderPath - File object or path to the folder
     *
     * @returns true if the content of the folder could be deleted
     */
    static deleteFolderContents(folderPath) {
        const folder = Io.getPath(folderPath);
        return JavaTypes.SpecsIo.deleteFolderContents(folder);
    }
    /**
     *
     * @param folderPath - File object or path to the folder
     *
     * @returns true if and only if the file denoted by this abstract pathname exists and is a normal file; false otherwise
     */
    static isFile(path) {
        if (typeof path === "string") {
            path = Io.getPath(path);
        }
        return path.isFile();
    }
    /**
     *
     * @param folderPath - File object or path to the folder
     *
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
    /**
     *
     * @param filepath - path to the file to be copied
     * @param destination - path to the destination file
     *
     * @returns true if the file was copied successfully
     */
    static copyFile(filepath, destination) {
        if (filepath === undefined)
            throw new Error("Value filepath is undefined");
        if (!Io.isFile(filepath))
            throw new Error(`Io.copyFile: given filepath '${filepath}' is not a file`);
        return JavaTypes.SpecsIo.copy(Io.getPath(filepath), Io.getPath(destination));
    }
    /**
     *
     * @param filepath - path to the file to be copied
     * @param destination - path to the destination file
     * @param verbose - enables additional information
     *
     * @returns true if the folder was copied successfully
     */
    static copyFolder(filepath, destination, verbose = false) {
        if (filepath === undefined)
            throw new Error("Value filepath is undefined");
        if (destination === undefined)
            throw new Error("Value destination is undefined");
        return JavaTypes.SpecsIo.copyFolder(Io.getPath(filepath), Io.getPath(destination), verbose);
    }
    /**
     *
     * @param path - base path
     *
     * @returns the given path, without extension.
     *
     */
    static removeExtension(path) {
        return JavaTypes.SpecsIo.removeExtension(path);
    }
    /**
     *
     * @param path - base path
     *
     * @returns the extension of the given path.
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
    /**
     *
     * @param path - The path of the file to append
     * @param content - The content to append
     *
     * @returns true if the content was appended successfully
     */
    static appendFile(path, content) {
        const file = Io.newFile(path);
        JavaTypes.SpecsIo.append(file, content);
    }
    /**
     *
     * @param targetFile -  The target file for which the relative path is calculated.
     * @param baseFile - The base file against which the relative path is calculated
     *
     * @returns the path of 'targetFile', relative to 'baseFile' or undefined if the file does not share a common ancestor with baseFile.
     */
    static getRelativePath(targetFile, baseFile) {
        const relativePath = JavaTypes.SpecsIo.getRelativePath(Io.getPath(targetFile), Io.getPath(baseFile));
        if (Io.getPath(relativePath).isAbsolute()) {
            return undefined;
        }
        return relativePath;
    }
    /**
     * 	@returns the system-dependent path-separator (e.g., : or ;).
     */
    static getPathSeparator() {
        return JavaTypes.File.pathSeparator;
    }
    /**
     * @returns system-dependent name-separator (e.g., / or \).
     */
    static getSeparator() {
        return JavaTypes.File.separator;
    }
    /**
     *
     * @param fileOrBaseFolder - File object or path to the file
     * @param optionalFile - Optional child pathname or file
     *
     * @returns the MD5 checksum of the file represented as a hexadecimal string.
     *
     * @throws RuntimeException if there are any issues while reading the file or calculating the MD5 checksum
     */
    static md5(fileOrBaseFolder, optionalFile) {
        return JavaTypes.SpecsIo.getMd5(Io.getPath(fileOrBaseFolder, optionalFile));
    }
    /**
     *
     * @returns the current working directory as a File object.
     */
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