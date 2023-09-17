import lara.dse.DseValues;

/**
 * Iterates over the values of a set of DseValues.
 *
 * @class
 *
 * @param {lara.dse.DseValues[]} dseValues - The DseValues that will form the set.
 */
var DseValuesSet = function() {
    // Parent constructor
    DseValues.call(this);
	
	if(arguments.length === 0) {
		throw "DseValuesSet: needs at least one DseValues as argument";
	}
	
	this.dseValuesArray = arrayFromArgs(arguments);
	this.numElements = undefined;
	
	//for(var dseValues of dseValuesArray) {
	for(var i=0; i<this.dseValuesArray.length; i++) {
		var dseValues = this.dseValuesArray[i];
		
		// Check it is a DseValues
		checkInstance(dseValues, DseValues, "DseValuesSet::arg["+i+"]");
		
		// Check if all DseValues have the same number of elements
		if(this.numElements === undefined) {
			this.numElements = dseValues.getNumElements();
		}
		else if(this.numElements !== dseValues.getNumElements()) {
				throw "Argument " + i + " has " + dseValues.getNumElements() + " elements but previous arguments have " + this.numElements + " elements";
		}
		
	}
	
};
// Inheritance
DseValuesSet.prototype = Object.create(DseValues.prototype);

DseValuesSet.prototype.getType = function() {
	return "DseValuesSet";
}

/**
 * @returns the next element.
 */
DseValuesSet.prototype.next = function() {
	var values = [];

	for(var dseValues of this.dseValuesArray) {
		values.push(dseValues.next());
	}

	return values;
}

/**
 * @returns true if it has another element to return.
 */
DseValuesSet.prototype.hasNext = function() {
	return this.dseValuesArray[0].hasNext(); 
}

/**
 * Resets the iterator.
 */
DseValuesSet.prototype.reset = function() {
	for(var dseValues of this.dseValuesArray) {
		dseValues.reset();
	}
}

DseValuesSet.prototype.getNumElements = function() {
	return this.numElements;
}

/**
 * @returns The number of values returned by a call to next(). A value of one means one value, a value greater than one means an array with that amount of values.
 */
DseValuesSet.prototype.getNumValuesPerElement = function() {
	return this.dseValuesArray.length;
}
