import Io from "../Io.js";
import JavaTypes from "./JavaTypes.js";
/**
 * Replaces strings inside a larger string.
 */
export default class Replacer {
    javaReplacer;
    constructor(contentsOrFile) {
        // If a file, read the contents
        if (JavaTypes.instanceOf(contentsOrFile, "java.io.File")) {
            contentsOrFile = Io.readFile(contentsOrFile);
        }
        this.javaReplacer = new JavaTypes.ReplacerHelper(contentsOrFile);
    }
    static fromFilename(filename) {
        return new Replacer(Io.getPath(filename));
    }
    replaceAll(target, replacement) {
        this.javaReplacer.replaceAll(target, replacement);
        return this;
    }
    getString() {
        return this.javaReplacer.getString();
    }
}
//# sourceMappingURL=Replacer.js.map