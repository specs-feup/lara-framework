import lara.units.SiUnit;
import lara.units.SiModifier;

/**
 * @param {lara.units.SiModifier} siModifier
 */
var EnergyUnit = function(siModifier) {
	if(siModifier === undefined) {
		siModifier = SiModifier.BASE;
	}

	// Parent constructor
    SiUnit.call(this, "J", siModifier);
};

// Inheritance
EnergyUnit.prototype = Object.create(SiUnit.prototype);

//var EnergyUnit = {};

EnergyUnit.joule = function() {
	return new SiUnit('J', SiModifier.BASE);
}

EnergyUnit.milli = function() {
	return new SiUnit('J', SiModifier.MILLI);
}

EnergyUnit.micro = function() {
	return new SiUnit('J', SiModifier.MICRO);
}

/*
EnergyUnit.new = function(siModifier) {
	if(siModifier === undefined) {
		siModifier = SiModifier.BASE;
	}
	
	return new SiUnit('J', siModifier);
}
*/

/**
 * Override that discards parameter 'unitHasBaseName' and always requires the suffix 'J'.
 */
EnergyUnit.prototype.convert = function(value, unit) {
	return this._convert(value, unit, true);
}