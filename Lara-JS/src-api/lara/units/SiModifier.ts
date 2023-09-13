import lara.units.UnitModifier;
//import lara.util.StringSet;

/**
 * @class
 */
var SiModifier = {};

SiModifier._unitModifier = new UnitModifier("");

SiModifier.GIGA = SiModifier._unitModifier.newModifier("G", "giga", 1e9);
SiModifier.MEGA = SiModifier._unitModifier.newModifier("M", "mega", 1e6);
SiModifier.KILO = SiModifier._unitModifier.newModifier("k", "kilo", 1e3);
SiModifier.BASE = SiModifier._unitModifier.newModifier("", "base", 1);
SiModifier.MILLI = SiModifier._unitModifier.newModifier("m", "milli", 1e-3);
SiModifier.MICRO = SiModifier._unitModifier.newModifier("u", "micro", 1e-6);
SiModifier.NANO = SiModifier._unitModifier.newModifier("n", "nano", 1e-9);

SiModifier.getUnitModifier = function() {
	return SiModifier._unitModifier;
}


SiModifier.convert = function(value, fromModifier, toModifier) {
	return SiModifier._unitModifier.convert(value, fromModifier, toModifier);
}

SiModifier.checkModifier = function(modifier, source) {
	SiModifier._unitModifier.checkModifier(modifier, source);
}

SiModifier.isValid = function(modifier) {
	return SiModifier._unitModifier.isValid(modifier);
}

SiModifier.getModifierByName = function(name) {
	return SiModifier._unitModifier.getModifierByName(name);
}

