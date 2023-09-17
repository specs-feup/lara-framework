import lara.dse.DseValues;

/**
 * Iterates over a list of values.
 * @constructor
 */
var DseValuesList = function() {
    // Parent constructor
    DseValues.call(this);
	
	if(arguments.length === 0) {
		throw "DseValuesList: needs at least one value as argument";
	}
	
	this.values = arrayFromArgs(arguments);
	this.currentIndex = 0;
};
// Inheritance
DseValuesList.prototype = Object.create(DseValues.prototype);

DseValuesList.prototype.getType = function() {
	return "DseValuesList";
}

/**
 * @returns the next element.
 */
DseValuesList.prototype.next = function() {
	var value = this.values[this.currentIndex];
	this.currentIndex++;
	
	return value;
}

/**
 * @returns true if it has another element to return.
 */
DseValuesList.prototype.hasNext = function() {
	return this.currentIndex < this.values.length;
}

/**
 * Resets the iterator.
 */
DseValuesList.prototype.reset = function() {
	this.currentIndex = 0;
}

DseValuesList.prototype.getNumElements = function() {
	return this.values.length; 
}

/**
 * @returns The number of values returned by a call to next(). A value of one means one value, a value greater than one means an array with that amount of values.
 */
DseValuesList.prototype.getNumValuesPerElement = function() {
	return 1;
}
