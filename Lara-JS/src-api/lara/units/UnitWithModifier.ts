import lara.units.Unit;

/**
 * 
 */
var UnitWithModifier = function(unitModifier, baseUnit, modifier) {
	// Parent constructor
    Unit.call(this);

	//this._unitModifier = SiModifier.getUnitModifier();
	this._unitModifier = unitModifier;

	checkString(baseUnit, "UnitWithModifier::baseUnit");
	
	if(arguments.length === 1) {
		modifier = unitModifier.getBase();
	}
	
	this._unitModifier.checkModifier(modifier, "UnitWithModifier::modifier");

	this._baseUnit = baseUnit;
  	this._modifier = modifier;
};

// Inheritance
UnitWithModifier.prototype = Object.create(Unit.prototype);

UnitWithModifier.prototype.getName = function() {
	
	return this._modifier + this._baseUnit;
}

/**
 * @param {string} unit - Unit of the value.
 */
UnitWithModifier.prototype.convert = function(value, unit, unitHasBaseName) {
	return this._convert(value, unit, unitHasBaseName);
}

UnitWithModifier.prototype._convert = function(value, unit, unitHasBaseName) {
	
	if(unitHasBaseName === undefined) {
		unitHasBaseName = false;
	}
		
    var fromModifier = this._extractModifier(unit, unitHasBaseName);
    
    
    return this._unitModifier.convert(value, fromModifier, this._modifier);
}

UnitWithModifier.prototype._extractModifier = function(unit, unitHasBaseName) {
	var currentModifier = unit;
	

	// Check that unit ends with the baseUnit
	if(unitHasBaseName) {
		if(currentModifier.endsWith(this._baseUnit)) {
			currentModifier = currentModifier.substring(0, currentModifier.length-this._baseUnit.length);
		}
	}


	currentModifier = this._unitModifier.normalize(currentModifier);
	/*
	// Check if unit is specified by name
	var modifierByName = SiModifier.getModifierByName(currentModifier);

	if(modifierByName !== undefined) {
		currentModifier = modifierByName;
	}
	*/
	this._unitModifier.checkModifier(currentModifier, "UnitWithModifier._extractModifier::unit");
	
	return currentModifier;
}
