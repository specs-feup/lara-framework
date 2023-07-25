import lara.Io;

/**
 * Replaces strings inside a larger string.
 */
var Replacer = function(contentsOrFile) {
	var contents = contentsOrFile;
	
	// If a file, read the contents
	if(contentsOrFile instanceof Java.type("java.io.File")) {
		contents = Io.readFile(contentsOrFile);
	}
	
	var ReplacerHelper = Java.type("pt.up.fe.specs.lara.util.ReplacerHelper");
	this.javaReplacer = new ReplacerHelper(contents);
};

Replacer.fromFilename = function(filename) {
	return new Replacer(Io.getPath(filename));
}

Replacer.prototype.replaceAll = function(target, replacement) {
	this.javaReplacer.replaceAll(target, replacement);
	
	return this;
}

Replacer.prototype.getString = function() {
	return this.javaReplacer.getString();
}
