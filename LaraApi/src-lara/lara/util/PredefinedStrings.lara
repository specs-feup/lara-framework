import lara.util.StringSet;
import lara.Check;

/**
 * Represents a set of predefined strings.
 */
var PredefinedStrings = function(name, strict) {
	Check.isDefined(name);
	/*
	if(strict === undefined) {
		strict = false;
	}
	*/
	Check.isBoolean(strict);

	this._name = name.toString();
	this._strict = strict;



	this._valuesSet = new StringSet();	

	var values = arrayFromArgs(arguments, 2);
	for(var value of values) {
		this._valuesSet.add(value.toString());
	}
	
	//println(" Values: " + this._valuesSet.values());
};




/**
 * @return {String[]} Available values.
 */
 
PredefinedStrings.prototype.values = function() {
	return this._valuesSet.values();
}


/**
 * @return {boolean} true if the given String is a valid value.
 */
PredefinedStrings.prototype.isValid = function(value) {
	return this._valuesSet.has(value);
}


PredefinedStrings.prototype.test = function(value) {
	if(!this.isValid(value)) {
		var message = "Invalid " + this._name + " '" + value + "'. Available values: " + this.values();
		if(this._strict) {
			throw message;
		} 
				
		println(message);
		return false;
	}
	
	return true;
}


PredefinedStrings.prototype.parse = function() {

 	var argsArray = arrayFromArgs(arguments);
	
	// Clear benchmarks
	parsedValues = [];
	for(var arg of argsArray) {
		if(!this.test(arg)) {
			continue;
		}
		
		parsedValues.push(arg);
	}
	
	return parsedValues;
}

