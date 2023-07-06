import Io from "../Io.js";
import JavaTypes from "./JavaTypes.js";
/**
 * Replaces strings inside a larger string.
 * @class
 */

export default class Replacer{

	javaReplacer: any;
	constructor(contentsOrFile: any) {
		
		let contents = contentsOrFile;
	
		// If a file, read the contents
		if(contentsOrFile instanceof JavaTypes.JavaFile) {
			contents = Io.readFile(contentsOrFile);
		}
	
		const ReplacerHelper = JavaTypes.ReplacerHelper;
		this.javaReplacer = new ReplacerHelper(contents);
	};

	static fromFilename(filename: string) {
		return new Replacer(Io.getPath(filename));
	}

	replaceAll(target: string, replacement: string) {
		this.javaReplacer.replaceAll(target, replacement);
		return this;
	}

	getString() {
		return this.javaReplacer.getString();
	}
};	
