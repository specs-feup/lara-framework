import Io from "../Io.js";
import JavaTypes, { JavaClasses } from "./JavaTypes.js";

/**
 * Replaces strings inside a larger string.
 */
export default class Replacer {
  _contents: string;

  constructor(contentsOrFile: string | JavaClasses.File) {
    // If a file, read the contents
    if (JavaTypes.instanceOf(contentsOrFile, "java.io.File")) {
      contentsOrFile = Io.readFile(contentsOrFile);
    }

    this._contents = contentsOrFile as string;
  }

  static fromFilename(filename: string) {
    return new Replacer(Io.getPath(filename));
  }

  replaceAll(target: string, replacement: string) {
    this._contents.replaceAll(target, replacement);
    return this;
  }

  getString(): string {
    return this._contents;
  }
}
