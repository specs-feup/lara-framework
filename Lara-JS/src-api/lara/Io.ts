import JavaTypes from "./util/JavaTypes.js";
import {
  checkDefined,
  isArray,
  isJavaClass,
  checkTrue,
} from "./core/LaraCore.js";

import { JSONtoFile, fileToJSON } from "../core/output.js";

/**
 * Utility methods related with input/output operations on files.
 */
export default class Io {
  /**
   * @returns true if the given object is a Java file, false otherwise.
   */
  static isJavaFile<T>(object: T): boolean {
    return isJavaClass(object, "java.io.File");
  }

  private static newFile(path: string | JavaTypes.JavaFile) {
    if (Io.isJavaFile(path)) {
      path = path.toString();
    }

    return new (JavaTypes.getJavaFile())(path.toString());
  }

  private static newFileWithBase(
    base: string | JavaTypes.JavaFile,
    path: string | JavaTypes.JavaFile
  ) {
    // If path is a file, convert to String first
    if (Io.isJavaFile(path)) {
      path = path.toString();
    }

    // If base is not a file, convert to File first
    if (!Io.isJavaFile(base)) {
      base = Io.newFile(base);
    }

    return new (JavaTypes.getJavaFile())(base, path);
  }

  /**
   * Creates a folder.
   */
  static mkdir(fileOrBaseFolder: any, optionalFile?: JavaTypes.JavaFile) {
    return JavaTypes.getJavaSpecsIo().mkdir(
      Io.getPath(fileOrBaseFolder, optionalFile)
    );
  }

  /**
   * Global version of the function.
   */
  static fmkdir(fileOrBaseFolder: any, optionalFile?: JavaTypes.JavaFile) {
    return Io.mkdir(fileOrBaseFolder, optionalFile);
  }

  /**
   * If folderName is undefined, returns the OS temporary folder.
   * If defined, creates a new folder inside the system's temporary folder and returns it.
   *
   */
  static getTempFolder(folderName?: string) {
    if (folderName === undefined) {
      return JavaTypes.getJavaSpecsIo().getTempFolder();
    }
    return JavaTypes.getJavaSpecsIo().getTempFolder(folderName);
  }

  /**
   * Creates a randomly named folder in the OS temporary folder that is deleted when the virtual machine exits.
   */
  static newRandomFolder(): JavaTypes.JavaFile {
    return JavaTypes.getJavaSpecsIo().newRandomFolder();
  }

  static getPath(fileOrBaseFolder: any, optionalFile?: JavaTypes.JavaFile) {
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
  static getAbsolutePath(
    fileOrBaseFolder: any,
    optionalFile?: JavaTypes.JavaFile
  ): string {
    return JavaTypes.getJavaSpecsIo().normalizePath(
      JavaTypes.getJavaSpecsIo().getCanonicalPath(
        Io.getPath(fileOrBaseFolder, optionalFile)
      )
    );
  }

  /**
   * Gets the paths (files and folders) in the given folder, corresponding to the given base folder and argument patterns.
   *
   * @param baseFolder -
   * @param args - Patterns to match
   */
  static getPaths(baseFolder: string | JavaTypes.JavaFile, ...args: string[]) {
    checkDefined(baseFolder, "baseFolder", "Io.getPaths");

    const baseFolderFile = Io.getPath(baseFolder);

    // For each argument after the baseFolder, treat it as a different file/glob
    const files: any = [];

    // If empty, add all files
    const argsArray = args;
    if (argsArray.length === 0) {
      argsArray.push("*");
    }

    for (const argument of argsArray) {
      const foundFiles = JavaTypes.getJavaSpecsIo().getPathsWithPattern(
        baseFolderFile,
        argument,
        false,
        "FILES_AND_FOLDERS"
      );
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
  static getFolders(
    baseFolder: string | JavaTypes.JavaFile,
    ...args: string[]
  ) {
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
  static getFiles(
    baseFolder: string | JavaTypes.JavaFile = "./",
    pattern: string | Array<any> = "*",
    isRecursive = false
  ) {
    // If pattern is an array, call function recursively
    if (isArray(pattern)) {
      const files: Array<JavaTypes.JavaFile> = [];

      for (const singlePattern of pattern) {
        const newFiles = Io.getFiles(baseFolder, singlePattern, isRecursive);
        for (const newFile of newFiles) {
          files.push(newFile);
        }
      }

      return files;
    }

    const list = JavaTypes.getJavaSpecsIo().getPathsWithPattern(
      Io.getPath(baseFolder),
      pattern.toString(),
      isRecursive,
      "FILES"
    );
    const files: Array<JavaTypes.JavaFile> = [];

    for (const file of list) {
      files.push(file);
    }

    return files;
  }

  /**
   * Returns a List with a string for each line of the given file
   */
  static readLines(fileOrBaseFolder: any, optionalFile?: JavaTypes.JavaFile) {
    return JavaTypes.getJavaLaraIo().readLines(
      Io.getPath(fileOrBaseFolder, optionalFile)
    );
  }

  /**
   * Deletes the given file.
   */
  static deleteFile(fileOrBaseFolder: any, optionalFile?: JavaTypes.JavaFile) {
    const file = Io.getPath(fileOrBaseFolder, optionalFile);
    if (!Io.isFile(file)) {
      return;
    }

    return JavaTypes.getJavaLaraIo().deleteFile(file);
  }

  /**
   * Each argument is a file that will be deleted.
   */
  static deleteFiles(...args: any) {
    for (const argument of args) {
      Io.deleteFile(argument);
    }
  }

  /**
   * Deletes a folder and its contents.
   *
   * @returns true if both the contents and the folder could be deleted
   */
  static deleteFolder(folderPath: string) {
    const folder = Io.getPath(folderPath);
    return JavaTypes.getJavaSpecsIo().deleteFolder(folder);
  }

  /**
   * Deletes the contents of a folder.
   */
  static deleteFolderContents(folderPath: string) {
    const folder = Io.getPath(folderPath);
    return JavaTypes.getJavaSpecsIo().deleteFolderContents(folder);
  }

  /**
   * @returns true if and only if the file denoted by this abstract pathname exists and is a normal file; false otherwise
   */
  static isFile(path: any) {
    if (typeof path === "string") {
      path = Io.getPath(path);
    }
    return path.isFile();
  }

  /**
   * @returns true if and only if the file denoted by this abstract pathname exists and is a folder; false otherwise
   */
  static isFolder(path: any) {
    if (typeof path === "string") {
      path = Io.getPath(path);
    }
    return path.isDirectory();
  }

  static readJson(path: string) {
    return fileToJSON(path);
  }

  /**
   * @deprecated Use JSONtoFile from api/core/output.js instead
   */
  static writeJson<T>(path: string, object: T) {
    JSONtoFile(path, object);
  }

  static copyFile(filepath: string, destination: string) {
    checkDefined(filepath, "filepath", "Io.copyFile");
    checkTrue(
      Io.isFile(filepath),
      "Io.copyFile: given filepath '" + filepath + "' is not a file"
    );

    return JavaTypes.getJavaSpecsIo().copy(
      Io.getPath(filepath),
      Io.getPath(destination)
    );
  }

  static copyFolder(filepath: string, destination: string, verbose = false) {
    checkDefined(filepath, "filepath", "Io.copyFolder");
    checkDefined(destination, "destination", "Io.copyFolder");

    return JavaTypes.getJavaSpecsIo().copyFolder(
      Io.getPath(filepath),
      Io.getPath(destination),
      verbose
    );
  }

  /**
   * Returns the given path, without extension.
   *
   */
  static removeExtension(path: string | JavaTypes.JavaFile) {
    return JavaTypes.getJavaSpecsIo().removeExtension(path);
  }

  /**
   * Returns the extension of the given path.
   *
   */
  static getExtension(path: string | JavaTypes.JavaFile) {
    return JavaTypes.getJavaSpecsIo().getExtension(path);
  }

  /**
   * @param path - The path of the file to write.
   * @param content - The contents to write.
   *
   * @returns The file to where the contents where written.
   */
  static writeFile(path: string, content: string): JavaTypes.JavaFile {
    const file: JavaTypes.JavaFile = Io.newFile(path);
    JavaTypes.getJavaSpecsIo().write(file, content);
    return file;
  }

  /**
   * @param path - The path of the file to read.
   *
   * @returns The contents of the file.
   */
  static readFile(path: string) {
    const file = Io.newFile(path);
    const content = JavaTypes.getJavaSpecsIo().read(file);
    return content;
  }

  static appendFile(path: string, content: any) {
    const file = Io.newFile(path);
    JavaTypes.getJavaSpecsIo().append(file, content);
  }

  /**
   * Returns the path of 'targetFile', relative to 'baseFile'.
   *
   * If the file does not share a common ancestor with baseFile, returns undefined.
   */
  static getRelativePath(
    targetFile: string | JavaTypes.JavaFile,
    baseFile: string | JavaTypes.JavaFile
  ) {
    const relativePath = JavaTypes.getJavaSpecsIo().getRelativePath(
      Io.getPath(targetFile),
      Io.getPath(baseFile)
    );

    if (Io.getPath(relativePath).isAbsolute()) {
      return undefined;
    }

    return relativePath;
  }

  /**
   * 	The system-dependent path-separator (e.g., : or ;).
   */
  static getPathSeparator() {
    return JavaTypes.getJavaFile().pathSeparator;
  }

  /**
   * 	The system-dependent name-separator (e.g., / or \).
   */
  static getSeparator() {
    return JavaTypes.getJavaFile().separator;
  }

  static md5(fileOrBaseFolder: any, optionalFile?: JavaTypes.JavaFile) {
    return JavaTypes.getJavaSpecsIo().getMd5(
      Io.getPath(fileOrBaseFolder, optionalFile)
    );
  }

  static getWorkingFolder() {
    return JavaTypes.getJavaSpecsIo().getWorkingDir();
  }

  /**
   * If value is a string that ends in .json, assume it is a file to a json object and parses it.
   * If it is a string but does not end in json, assume it is a stringified object.
   * Otherwise, returns the object as it is.
   *
   * @deprecated Use JSON.parse() instead or fileToJSON from api/core/output.js
   */
  static jsonObject<T>(value: T): T | string {
    if (typeof value === "string") {
      if (value.endsWith(".json")) {
        return this.readJson(value);
      } else {
        return JSON.parse(value);
      }
    }

    return value;
  }
}
