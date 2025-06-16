import Io from "../Io.js";
import JavaTypes, { JavaClasses } from "./JavaTypes.js";

/**
 * Replaces strings inside a larger string.
 */
export default class Replacer {
  _contents: string;

  constructor(contentsOrFile: string | JavaClasses.File) {
    // If a file, read the contents
    if (typeof contentsOrFile === "string") {
      this._contents = contentsOrFile;
    } else if (JavaTypes.instanceOf(contentsOrFile, "java.io.File")) {
      this._contents = Io.readFile(contentsOrFile);
    } else {
      throw new Error("Replacer constructor expects a string or a java.io.File instance.");
    }
  }

  static fromFilename(filename: string) {
    return new Replacer(Io.getPath(filename));
  }

  replaceAll(target: string, replacement: string) {
    this._contents = this._contents.replaceAll(target, replacement);
    return this;
  }

  getString(): string {
    return this._contents;
  }
}
