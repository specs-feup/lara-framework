/**
 * Base class that represents a unit of what is being measured in ANTAREX, e.g., time or energy.
 * 
 * This should not be instantiated directly.
 * 
 * @constructor
 * */
var Unit = function() {
    // should this be called as a super?
};

/**
 * Returns the name of the unit.
 * 
 * This should not be called directly.
 * */
Unit.prototype.getName = function() {
    notImplemented("Unit.getName");
}

/**
 * Converts the value, in the provided unit, to the unit this instance represents.
 * 
 * This presents a default implementation of the conversion assuming there is a factor table accessable throught the method _getFactorTable().
 * 
 * @param {string} unit - 
 * */
Unit.prototype.convert = function(value, unit) {
    
    var originalFactor = this._getFactorTable()[unit];
    // test for undefined
    
    var thisFactor = this._getFactorTable()[this.getName()];
    // test for undefined
    
    var conversionFactor = thisFactor / originalFactor;
    
    return value * conversionFactor;
}


Unit.prototype._getFactorTable = function() {
	notImplemented("Unit._getFactorTable");
}
