import lara.iterators.LineIterator;

/**
 * Helps inserting lines.
 *
 * @class
 */
var LineInserter = function() {
	// TODO: Use system newline? Allow user to change newline?
	this.newLine = "\n";
};

/**
 * Sets the new line.
 *
 * @param {string} newLine - the new line to use.
 */
LineInserter.prototype.setNewLine = function(newLine) {
	checkString(newLine);
	this.newLine = newLine;
}

/**
 *
 * @param {string|J#java.io.File} contents - The contents where lines will be inserted.
 * @param {object} linesToInsert - Maps line numbers to strings to insert.
 *
 * @returns the contents with the lines inserted.
 */
LineInserter.prototype.add = function(contents, linesToInsert) {
	var lineIterator = new LineIterator(contents);

	var newContents = "";
	var currentLine = 0;
	while(lineIterator.hasNext()) {
		var line = lineIterator.next();
		currentLine++;
		
		// Check if there is a mapping for the current line
		var toInsert = linesToInsert[currentLine];
		if(toInsert !== undefined) {
			newContents += toInsert + this.newLine;
		}
		
		// Insert old content
		newContents += line + this.newLine;
	}
	
	return newContents;
}