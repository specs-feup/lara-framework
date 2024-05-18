import { fileToJSON } from "../../core/output.js";
import Io from "../Io.js";
import JavaTypes, { JavaClasses } from "./JavaTypes.js";

/**
 * Provides access to files in a specific folder.
 */
export default class LocalFolder {
  baseFolder: JavaClasses.File;

  constructor(foldername: string | JavaClasses.File) {
    // TODO: DESIGN: currently foldername must exist, should create the folder if it does not exist?
    this.baseFolder = Io.getPath(foldername);
    if (!Io.isFolder(this.baseFolder)) {
      throw `LocalFolder: given foldername must represent an existing folder: ${foldername.toString()}`;
    }
  }

  /**
   * @returns A java File that represents the root of this LocalFolder
   */
  getBaseFolder() {
    return this.baseFolder;
  }

  /**
   * Returns a file from the path relative to the LocalFolder location.
   *
   * <p>If the path does not exist, or is not a file, throws an exception.
   *
   * @returns A java File representing the given path relative to this LocalFolder
   */
  getFile(path: string | JavaClasses.File): JavaClasses.File {
    const file = Io.getPath(this.baseFolder, path);
    if (!Io.isFile(file)) {
      throw `Path '${path.toString()}' is not a file in the folder '${Io.getAbsolutePath(
        this.baseFolder
      )}'`;
    }

    return file;
  }

  /**
   * Returns a folder from the path relative to the LocalFolder location.
   *
   * <p>If the path does not exist, or is not a folder, throws an exception.
   *
   * @returns A java File representing the given path relative to this LocalFolder
   */
  getFolder(path: string | JavaClasses.File): JavaClasses.File {
    const folder = Io.getPath(this.baseFolder, path);
    if (!Io.isFolder(folder)) {
      throw `Path '${path.toString()}' is not a folder in the folder '${Io.getAbsolutePath(
        this.baseFolder
      )}'`;
    }

    return folder;
  }

  hasFolder(path: string | JavaClasses.File) {
    return Io.isFolder(Io.getPath(this.baseFolder, path));
  }

  /**
   * @returns String with the contents of the given path
   */
  getString(path: string | JavaClasses.File): string {
    return Io.readFile(this.getFile(path));
  }

  /**
   * @returns Decodes the specified file as a JSON file.
   */
  getJson(path: string | JavaClasses.File): object {
    return fileToJSON(this.getFile(path).getAbsolutePath()) as object;
  }

  /**
   * @returns A java List with all the files in this LocalFolder
   */
  getFileList(
    path?: string | JavaClasses.File
  ): JavaClasses.List<JavaClasses.File> {
    let basePath = this.baseFolder;

    if (path !== undefined) {
      basePath = Io.getPath(basePath, path);
    }

    return JavaTypes.SpecsIo.getFilesRecursive(basePath);
  }
}
