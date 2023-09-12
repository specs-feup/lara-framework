import lara.iterators.LaraIterator;

/**
 * Iterates over a sequence of lines.
 * @constructor
 *
 * @param {string|J#java.io.File} contents The contents that will be iterated as lines.
 */
var LineIterator = function(contents) {
    // Parent constructor
    LaraIterator.call(this);

	checkTrue(isString(contents) || isJavaClass(contents, "java.io.File"), "Is neither a string, or a Java File","LineIterator.new::contents");
	
	this.contents = contents;
	this.lineStream = Java.type("pt.up.fe.specs.util.utilities.LineStream").newInstance(this.contents);
};
// Inheritance
LineIterator.prototype = Object.create(LaraIterator.prototype);


LineIterator.prototype.getType = function() {
	return "LineIterator";
}

/**
 * @returns the next element.
 */
LineIterator.prototype.next = function() {
	if(!this.lineStream.hasNextLine()) {
		return undefined;
	}

	return this.lineStream.nextLine();
}

/**
 * @returns {boolean} true if it has another element to return.
 */
LineIterator.prototype.hasNext = function() {
	return this.lineStream.hasNextLine();
}

/**
 * Resets the iterator.
 */
LineIterator.prototype.reset = function() {
	this.lineStream.close();
	this.lineStream = Java.type("pt.up.fe.specs.util.utilities.LineStream").newInstance(this.contents);
}

/**
 * @returns {number} the total number of elements of the iterator, or undefined if it is not possible to calculate.
 */
LineIterator.prototype.getNumElements = function() {
	return undefined;
}

/**
 * @returns The number of values returned by a call to next(). A value of one means one value, a value greater than one means an array with that amount of values.
 */
LineIterator.prototype.getNumValuesPerElement = function() {
	return 1;
}

/**
 * Closes the iterator. For instane, if the iterator is backed-up by a file, it needs to be closed before the file can be deleted.
 */
LineIterator.prototype.close = function() {
	this.lineStream.close();
}