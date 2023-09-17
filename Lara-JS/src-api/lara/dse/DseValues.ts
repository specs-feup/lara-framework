/**
 * An iterator for a set of values to be used during Design-Space Exploration.
 *
 * @class
 */
var DseValues = function() {

};

/**
 * @returns the type of this DseValues.
 */
DseValues.prototype.getType = function() {
	notImplemented("DseValues.getType");
}

/**
 * @returns the next element.
 */
DseValues.prototype.next = function() {
	notImplemented("DseValues.next");
}

/**
 * @returns true if it has another element to return.
 */
DseValues.prototype.hasNext = function() {
	notImplemented("DseValues.hasNext");
}

/**
 * Resets the iterator.
 */
DseValues.prototype.reset = function() {
	notImplemented("DseValues.reset");
}

DseValues.prototype.getNumElements = function() {
	notImplemented("DseValues.getNumElements");
}

/**
 * @returns The number of values returned by a call to next(). A value of one means one value, a value greater than one means an array with that amount of values.
 */
DseValues.prototype.getNumValuesPerElement = function() {
	notImplemented("DseValues.getNumValuesPerElement");
}
