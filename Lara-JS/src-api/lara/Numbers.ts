/**
 * @class
 */
var Numbers = {};



/**
 * @param {number} number - A number to convert to indexes.
 * @returns An array with indexes corresponding to the active bits of the given number
 */
Numbers.toIndexesArray = function(number) {
	checkNumber(number, "toIndexesArray");

	// Convert number to binary string
	var binaryString = number.toString(2);

	var indexesArray = [];
	var numBits = binaryString.length;
	for(var i=0; i<numBits; i++) {
		if(binaryString[i] === "0") {
			continue;
		}

		indexesArray.push((numBits-i)-1);
	}

	return indexesArray.reverse();
}

/**
 * Taken from here: https://stackoverflow.com/questions/3959211/fast-factorial-function-in-javascript#3959275
 */ 
Numbers.factorial = function(num) {
    var rval=1;
    for (var i = 2; i <= num; i++) {
        rval = rval * i;
	}

    return rval;
}

/**
 * @return {number} Arithmetic mean of the given values.
 */
Numbers.mean = function(values) {

	var acc = 0;
	var total = 0;
	for(var value of values) {
		acc += value;
		total++;
	}
	
	return acc / total;
}

/**
 * @return {number} Sum of the given values.
 */
Numbers.sum = function(values) {
	
	var acc = 0;
	for(var value of values) {
		acc += value;
	}
	
	return acc;
}