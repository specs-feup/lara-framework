import lara.units.UnitWithModifier;
import lara.units.TimeModifier;

/**
 * @param {lara.units.TimeModifier} timeModifier
 */
//var TimeUnit = function(baseUnit, modifier) {
var TimeUnit = function(timeModifier) {
	// Parent constructor
    UnitWithModifier.call(this, TimeModifier.getUnitModifier(), 's', timeModifier);
};

// Inheritance
TimeUnit.prototype = Object.create(UnitWithModifier.prototype);

/**
 * Factory methods to create TimeUnits.
 * */
TimeUnit.nano = function() {
	
	return new TimeUnit(TimeModifier.NANO);
}

TimeUnit.micro = function() {
	
	return new TimeUnit(TimeModifier.MICRO);
}

TimeUnit.milli = function() {
	
	return new TimeUnit(TimeModifier.MILLI);
}

TimeUnit.second = function() {
	
	return new TimeUnit(TimeModifier.BASE);
}

TimeUnit.minute = function() {
	
	return new TimeUnit(TimeModifier.MINUTE);
}

TimeUnit.hour = function() {
	
	return new TimeUnit(TimeModifier.HOUR);
}

TimeUnit.day = function() {
	
	return new TimeUnit(TimeModifier.DAY);
}

/**
 * Override that adds support to time-specific units (days, hours, minutes). Discards parameter 'unitHasBaseName'.
 */
TimeUnit.prototype.convert = function(value, unit) {

	// Time-specific units
	if(unit === "minute" || unit === "minutes") {
		return this._convert(value, "minute", true);
	}
	
	if(unit === "hour" || unit === "hours") {
		return this._convert(value, "hour", true);
	}

	if(unit === "day" || unit === "days") {
		return this._convert(value, "day", true);
	}
	
	
	// For SI modifiers, require base name
	return this._convert(value, unit, true);
}