import Io from "../Io.js";
import JavaTypes from "./JavaTypes.js";

/**
 * Replaces strings inside a larger string.
 */
export default class Replacer {
  javaReplacer: any;
  constructor(contentsOrFile: any) {
    // If a file, read the contents
    if (contentsOrFile instanceof JavaTypes.getJavaFile()) {
      contentsOrFile = Io.readFile(contentsOrFile);
    }
    this.javaReplacer = new (JavaTypes.getJavaReplacerHelper())(contentsOrFile);
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
