import lara.dse.DseVariant;

/**
 * Associates a variable name to a DseValues.
 * @constructor
 */
var VariableVariant = function(variableNames, dseValues) {
    // Parent constructor
    DseVariant.call(this);
	
	if(arguments.length < 2) {
		throw "VariableVariant: needs at least two arguments, a dseValues and variable names";
	}
	
	this.dseValues = dseValues instanceof DseValues ? dseValues : new DseValuesList(arrayFromArgs(arguments, 1));
	//this.dseValues = dseValues;
		
	this.variableNames = isArray(variableNames) ? variableNames : [variableNames];
	//this.variableNames = arrayFromArgs(arguments, 1);

	// Verify that the number of variables is the same as the number of values per element
	checkTrue(this.dseValues.getNumValuesPerElement() === this.variableNames.length);
	
};
// Inheritance
VariableVariant.prototype = Object.create(DseVariant.prototype);


VariableVariant.prototype.getType = function() {
	return "VariableVariant";
}


VariableVariant.prototype.getNames = function() {
	return this.variableNames;
}


VariableVariant.prototype.getDseValues = function() {
	return this.dseValues;
}
