import Io from "../Io.js";
import JavaTypes, { JavaClasses } from "./JavaTypes.js";

/**
 * Replaces strings inside a larger string.
 */
export default class Replacer {
  javaReplacer: JavaClasses.ReplacerHelper;

  constructor(contentsOrFile: any) {
    // If a file, read the contents
    if (JavaTypes.instanceOf(contentsOrFile, "java.io.File")) {
      contentsOrFile = Io.readFile(contentsOrFile);
    }

    this.javaReplacer = new JavaTypes.ReplacerHelper(contentsOrFile);
  }

  static fromFilename(filename: string) {
    return new Replacer(Io.getPath(filename));
  }

  replaceAll(target: string, replacement: string) {
    this.javaReplacer.replaceAll(target, replacement);
    return this;
  }

  getString(): string {
    return this.javaReplacer.getString();
  }
}
