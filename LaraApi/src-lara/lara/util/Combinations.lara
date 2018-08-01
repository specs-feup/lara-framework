import lara.Numbers;
import lara.util.SequentialCombinations;

/**
 * Generates sequential sequences of combinations, according to the given number of elements.
 */
var Combinations = function(elements, combinationSize) {
	checkDefined(elements);
	
	this.elements = elements;
	this.combinationSize = combinationSize;

	// Start at 1, to avoid empty sequence
	this.currentValue = 1;
	this.lastValueUsed = undefined;
	
	// Number of combinations
	// n!/(r!(n−r)!)
	var nFact = Numbers.factorial(elements.length);
	var rFact = Numbers.factorial(combinationSize);
	var nrDiff = (elements.length - combinationSize);
	var nrDiffFact = Numbers.factorial(nrDiff);

	this.numCombinations = nFact / (rFact*nrDiffFact);
	this.currentCombinations = 0;
	
	// The number of elements to combine
	this.generator = new SequentialCombinations(elements.length);
	
};


/**
 * @returns {elements[]} the next sequence
 */
Combinations.prototype.next = function() {
	// Check if there are combinations left
	if(!this.hasNext()) {
		throw "Combinations.next: Reached maximum number of combinations (" + this.numCombinations + ")";
	}
	
	// Get new values, until one with length of combinationSize appear
	var sequence = [];
	while(sequence.length !== this.combinationSize) {
		sequence = this.generator.next();
	}

	// Found sequence, create combination
	var combination = [];
	for(var index of sequence) {
		combination.push(this.elements[index]);
	}

	this.currentCombinations++;

	return combination;
}

/**
 * @returns {boolean} true if there are stil combinations to generate
 */
Combinations.prototype.hasNext = function() {
	return this.currentCombinations < this.numCombinations;
}