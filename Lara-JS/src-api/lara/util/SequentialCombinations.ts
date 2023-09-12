import lara.Numbers;

/**
 * Generates sequential sequences of combinations, according to the given number of elements.
 */
var SequentialCombinations = function(numElements, upTo) {
	checkDefined(numElements);
	
	this.numElements = numElements;

	// Start at 1, to avoid empty sequence
	this.currentValue = 1;
	this.lastValueUsed = undefined;
	
	// Last combination
	this.maximumValue = Math.pow(2, numElements) - 1;
	if(upTo !== undefined) {
		this.maximumValue = Math.min(this.maximumValue, upTo);
	}

};

/**
 * @returns {number} the value used to generate the last combination
 */
SequentialCombinations.prototype.getLastSeed = function() {
	return this.lastValueUsed;
}

/**
 * @returns {number[]} the next sequence
 */
SequentialCombinations.prototype.next = function() {
	// Check if there are combinations left
	if(!this.hasNext()) {
		throw "SequentialCombinations.next: Reached maximum number of combinations (" + this.maximumValue + ")";
	}
	
	var combination = Numbers.toIndexesArray(this.currentValue);

	// Next value
	this.lastValueUsed = this.currentValue;
	this.currentValue++;

	return combination;
}

/**
 * @returns {boolean} true if there are stil combinations to generate
 */
SequentialCombinations.prototype.hasNext = function() {
	return this.currentValue <= this.maximumValue;
}