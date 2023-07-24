import { JSONtoFile, fileToJSON } from "../core/output.js";
import JavaTypes, { JavaClasses } from "./util/JavaTypes.js";

/**
 * Utility methods related with input/output operations on files.
 */
export default class Io {
  /**
   * @param object - object to check
   * 
   * @returns true if the given object is a Java file, false otherwise.
   */
  static isJavaFile<T>(object: T): boolean {
    if (typeof object === "string") return false;
    return JavaTypes.instanceOf(object, "java.io.File");
  }


  /**
   * 
   * @param path - Path for the file creation
   * 
   * @returns new File object with path given
   */
  private static newFile(path: string | JavaClasses.File) {
    if (Io.isJavaFile(path)) {
      path = path.toString();
    }

    return new JavaTypes.File(path.toString()) as JavaClasses.File;
  }


  /**
   * 
   * @param base - Parent pathname
   * @param path - Child pathname 
   * 
   * @returns a new File object from parent and child pathnames 
   */
  private static newFileWithBase(
    base: string | JavaClasses.File,
    path: string | JavaClasses.File
  ) {
    // If path is a file, convert to String first
    if (Io.isJavaFile(path)) {
      path = path.toString();
    }

    // If base is not a file, convert to File first
    if (!Io.isJavaFile(base)) {
      base = Io.newFile(base);
    }

    return new JavaTypes.File(base, path) as JavaClasses.File;
  }

  /**
   * Creates a folder.
   * 
   * @param fileOrBaseFolder - File object or path to the base folder
   * @param optionalFile - Optional child pathname or file, if absolute ignores fileorBaseFolder
   * 
   * @returns the created folder
   */
  static mkdir(fileOrBaseFolder: JavaClasses.File | string, optionalFile?: JavaClasses.File | string) {
    return JavaTypes.SpecsIo.mkdir(
      Io.getPath(fileOrBaseFolder, optionalFile) as string | JavaClasses.File
      ) as JavaClasses.File;
  }


  /**
   * 
   * If folderName is undefined, returns the OS temporary folder.
   * If defined, creates a new folder inside the system's temporary folder and returns it.
   * @param folderName - Optional name for the temporaray folder
   * 
   * @returns the OS temporary folder or the created folder
   */
  static getTempFolder(folderName?: string): JavaClasses.File{
    if (folderName === undefined) {
      return JavaTypes.SpecsIo.getTempFolder();
    }
    return JavaTypes.SpecsIo.getTempFolder(folderName);
  }

  /**
   * Creates a randomly named folder in the OS temporary folder that is deleted when the virtual machine exits.
   * @returns the created temporary folder
   */
  static newRandomFolder(): JavaClasses.File {
    return JavaTypes.SpecsIo.newRandomFolder();
  }


  /**
  * 
  * @param fileOrBaseFolder - File object or path to the base folder
  * @param optionalFile - Optional child pathname or file, if absolute ignores fileorBaseFolder
  * 
  * @returns new File object with path given
  */
  static getPath(fileOrBaseFolder: string | JavaClasses.File,
     optionalFile?: JavaClasses.File | string): JavaClasses.File {
    
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
  static getAbsolutePath(
    fileOrBaseFolder: JavaClasses.File | string,
    optionalFile?: JavaClasses.File | string
  ): string {
    return JavaTypes.SpecsIo.normalizePath(
      JavaTypes.SpecsIo.getCanonicalPath(
        Io.getPath(fileOrBaseFolder, optionalFile)
      )
    );
  }

  /**
   * 
   * @param baseFolder - File object or path to the base folder
   * @param args - Patterns to match
   * 
   * @returns the paths (files and folders) in the given folder, corresponding to the given base folder and argument patterns.
   */
  static getPaths(baseFolder: string | JavaClasses.File, ...args: string[]) {
    

    if (baseFolder === undefined) throw "base Folder is undefined";

    const baseFolderFile = Io.getPath(baseFolder);

    // For each argument after the baseFolder, treat it as a different file/glob
    const files: JavaClasses.File[] = [];

    // If empty, add all files
    const argsArray = args;
    if (argsArray.length === 0) {
      argsArray.push("*");
    }

    for (const argument of argsArray) {
      const foundFiles: any = JavaTypes.SpecsIo.getPathsWithPattern(
        baseFolderFile,
        argument,
        false,
        "FILES_AND_FOLDERS"
      );
      
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
  static getFolders(baseFolder: string | JavaClasses.File, ...args: string[]) {
    const paths = Io.getPaths(baseFolder, ...args);

    const folders: JavaClasses.File[] = [];

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
  static getFiles(
    baseFolder: string | JavaClasses.File = "./",
    pattern: string | any[] = "*",
    isRecursive = false
  ) {
    // If pattern is an array, call function recursively
    if (pattern instanceof Array) {
      const files: Array<JavaClasses.File> = [];

      for (const singlePattern of pattern) {
        const newFiles = Io.getFiles(baseFolder, singlePattern, isRecursive);
        for (const newFile of newFiles) {
          files.push(newFile);
        }
      }

      return files;
    }

    const list = JavaTypes.SpecsIo.getPathsWithPattern(
      Io.getPath(baseFolder),
      pattern.toString(),
      isRecursive,
      "FILES"
    );
    const files: JavaClasses.File[] = [];

    for (const file of list) {
      files.push(file);
    }

    return files;
  }

  /**
   * @param fileOrBaseFolder - File object or path to the base folder
   * @param optionalFile - Optional child pathname or file
   * 
   * @returns a List with a string for each line of the given file
   */
  static readLines(fileOrBaseFolder: string | JavaClasses.File, optionalFile?: JavaClasses.File | string) {
    return JavaTypes.LaraIo.readLines(
      Io.getPath(fileOrBaseFolder, optionalFile)
    ) as string[];
  }

  /**
   * @param fileOrBaseFolder - File object or path to the base folder
   * @param optionalFile - Optional child pathname or file
   * 
   * @returns if the delete operation on the given file was successfull.
   */
  static deleteFile(fileOrBaseFolder: any, optionalFile?: JavaClasses.File | string) {
    const file = Io.getPath(fileOrBaseFolder, optionalFile);
    return JavaTypes.LaraIo.deleteFile(file) as boolean;
  }

  /**
   * @param args - Each argument is a file that will be deleted.
   */
  static deleteFiles(...args: any) {
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
  static deleteFolder(folderPath: string) {
    const folder = Io.getPath(folderPath);
    return JavaTypes.SpecsIo.deleteFolder(folder) as boolean;
  }

  /**
   * Deletes the contents of a folder.
   * 
   * @param folderPath - File object or path to the folder
   * 
   * @returns true if the content of the folder could be deleted
   */
  static deleteFolderContents(folderPath: string) {
    const folder = Io.getPath(folderPath);
    return JavaTypes.SpecsIo.deleteFolderContents(folder) as boolean;
  }

  /**
   * 
   * @param folderPath - File object or path to the folder
   * 
   * @returns true if and only if the file denoted by this abstract pathname exists and is a normal file; false otherwise
   */
  static isFile(path: string | JavaClasses.File) {
    if (typeof path === "string") {
      path = Io.getPath(path);
    }
    return path.isFile() as boolean;
  }

  /**
   * 
   * @param folderPath - File object or path to the folder
   * 
   * @returns true if and only if the file denoted by this abstract pathname exists and is a folder; false otherwise
   */
  static isFolder(path: any){
    if (typeof path === "string") {
      path = Io.getPath(path);
    }
    return path.isDirectory() as boolean;
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


  /**
   * 
   * @param filepath - path to the file to be copied
   * @param destination - path to the destination file
   * 
   * @returns true if the file was copied successfully
   */

  static copyFile(filepath: string| JavaClasses.File, destination: string | JavaClasses.File) {

    if(typeof filepath === undefined) throw "Value filepath is undefined"

    if(!Io.isFile(filepath)) throw "Io.copyFile: given filepath '" + filepath + "' is not a file"

    return JavaTypes.SpecsIo.copy(
      Io.getPath(filepath),
      Io.getPath(destination)
    ) as boolean;
  }


  /**
   * 
   * @param filepath - path to the file to be copied
   * @param destination - path to the destination file
   * @param verbose - enables additional information
   * 
   * @returns true if the folder was copied successfully
   */
  static copyFolder(filepath: string, destination: string, verbose = false) {

    if (filepath === undefined) throw "Value filepath is undefined";

    if(destination === undefined) throw "Value destination is undefined";

    return JavaTypes.SpecsIo.copyFolder(
      Io.getPath(filepath),
      Io.getPath(destination),
      verbose
    ) as boolean;
  }

  /**
   * 
   * @param path - base path
   * 
   * @returns the given path, without extension.
   *
   */
  static removeExtension(path: string | JavaClasses.File) {
    return JavaTypes.SpecsIo.removeExtension(path) as string;
  }

  /**
   * 
   * @param path - base path
   * 
   * @returns the extension of the given path.
   *
   */
  static getExtension(path: string | JavaClasses.File) {
    return JavaTypes.SpecsIo.getExtension(path) as string;
  }

  /**
   * @param path - The path of the file to write.
   * @param content - The contents to write.
   *
   * @returns The file to where the contents where written.
   */
  static writeFile(path: string | JavaClasses.File, content: string): JavaClasses.File {
    const file: JavaClasses.File = Io.newFile(path);
    JavaTypes.SpecsIo.write(file, content);
    return file;
  }

  /**
   * @param path - The path of the file to read.
   *
   * @returns The contents of the file.
   */
  static readFile(path: string) {
    const file = Io.newFile(path);
    const content = JavaTypes.SpecsIo.read(file) as string;
    return content;
  }

  /**
   * 
   * @param path - The path of the file to append
   * @param content - The content to append
   * 
   * @returns true if the content was appended successfully
   */

  static appendFile(path: string, content: any) {
    const file = Io.newFile(path);
    JavaTypes.SpecsIo.append(file, content) as boolean;
  }

  /**
   * 
   * @param targetFile -  The target file for which the relative path is calculated.
   * @param baseFile - The base file against which the relative path is calculated
   * 
   * @returns the path of 'targetFile', relative to 'baseFile' or undefined if the file does not share a common ancestor with baseFile.
   */
  static getRelativePath(
    targetFile: string | JavaClasses.File,
    baseFile: string | JavaClasses.File
  ) {
    const relativePath= JavaTypes.SpecsIo.getRelativePath(
      Io.getPath(targetFile),
      Io.getPath(baseFile)
    ) as string;

    if (Io.getPath(relativePath).isAbsolute()) {
      return undefined;
    }

    return relativePath;
  }

  /**
   * 	@returns the system-dependent path-separator (e.g., : or ;).
   */
  static getPathSeparator(): string {
    return JavaTypes.File.pathSeparator;
  }

  /**
   * @returns system-dependent name-separator (e.g., / or \).
   */
  static getSeparator(): string {
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

  static md5(fileOrBaseFolder: any, optionalFile?: JavaClasses.File | string): string {
    return JavaTypes.SpecsIo.getMd5(Io.getPath(fileOrBaseFolder, optionalFile));
  }

 /**
  * 
  * @returns the current working directory as a File object.
  */
  static getWorkingFolder(): JavaClasses.File{
    return JavaTypes.SpecsIo.getWorkingDir();
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
