import lara.util.StringSet;

/**
 *
 */
var UnitModifier = function(base, baseModifier, newModifier) {

	checkDefined(base);

	// Stores the factors for each modifier 
	this._factorTable = {};


	//The set of valid modifiers. 
	this._validModifiers = new StringSet();

	// The set of modifier names.
	this._names = {};
	this._namesToModifier = {};

	this._base = base;
	
	if(baseModifier !== undefined) {
		for(var modifier of baseModifier.values()) {
			
			if(newModifier !== undefined) {

				newModifier[baseModifier._names[modifier].toUpperCase()] = modifier;
			}
			
			this.newModifier(modifier, baseModifier._names[modifier], baseModifier._factorTable[modifier]);
		}
	}
};


/**
 * @param {string} modifier - 
 * @param {number} factor - 
 */ 
UnitModifier.prototype.newModifier = function (modifier, name, factor) {
	checkString(modifier, "UnitModifier._newModifier::modifier");
	checkNumber(factor, "UnitModifier._newModifier::factor");

	// Add name
	this._names[modifier] = name;
	this._namesToModifier[name] = modifier;
	
	// Add factor
	this._factorTable[modifier] = factor;
	
	// Add modifier to the set
	this._validModifiers.add(modifier);
	
	return modifier;
}

UnitModifier.prototype.convert = function(value, fromModifier, toModifier) {
    
    this.checkModifier(fromModifier, "UnitModifier.convert::fromModifier");
    this.checkModifier(toModifier, "UnitModifier.convert::toModifier");
    
    var fromFactor = this._factorTable[fromModifier];
    // test for undefined
    
    var toFactor = this._factorTable[toModifier];
    // test for undefined

    var conversionFactor = toFactor / fromFactor;
    
    return value / conversionFactor;
}

UnitModifier.prototype.checkModifier = function(modifier, source) {
	checkTrue(this.isValid(modifier), modifier + " is not a valid SI modifier. Valid modifiers are: " + this._validModifierString(), source);
}

UnitModifier.prototype._validModifierString = function() {	
	var stringValues = [];
	for(var modifier of this._validModifiers.values()) {
		var currentModifier = this._names[modifier] + (modifier.length === 0 ? "" : "("+modifier+")");
		stringValues.push(currentModifier);
	}
	
	return stringValues.join(", ");

}

UnitModifier.prototype.isValid = function(modifier) {
	return this._validModifiers.has(modifier);
}

UnitModifier.prototype.getModifierByName = function(name) {
	return this._namesToModifier[name];
}

UnitModifier.prototype.values = function() {
	return this._validModifiers.values();
}

UnitModifier.prototype.normalize = function(modifier) {
	// Check if unit is specified by name
	//var modifierByName = SiModifier.getModifierByName(modifier);
	var modifierByName = this.getModifierByName(modifier);

	if(modifierByName !== undefined) {
		return modifierByName;
	}
	
	return modifier;
}

