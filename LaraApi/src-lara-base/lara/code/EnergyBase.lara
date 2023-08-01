
/**
 * Class that measures the energy spent when executing a section of code.
 */
var Energy = function(filename) {
    this.filename = filename;
    this.printUnit = true;
	this.print = true;
};

/**
 * If true, suffixes 'J' to the end of the value.
 *
 * @param printUnit boolean
 */
Energy.prototype.setPrintUnit = function(printUnit) {
    checkType(printUnit, 'boolean'); 
	this.printUnit = printUnit;
    return this;
}

Energy.prototype.getPrintUnit = function() {
    return "J";
}

Energy.prototype.setPrint = function(print) {
    checkType(print, 'boolean'); 
	this.print = print;
    return this;
}

Energy.prototype._warn = function(message) {
    println("[EnergyBase Warning] " + message);
}

/**
 * Verifies that join point start is not undefined, that it is inside a function.
 * Additionally, if $end is not undefined, checks if it is inside the same function as $start.
 *
 * [Requires] global attribute 'ancestor'. 
 *
 * @return true if $start is a valid join point for the 'measure' function
 */
Energy.prototype._measureValidate = function($start, $end, functionJpName) {
    if ($start === undefined) {
        this._warn("Energy: $start join point is undefined");
        return false;
    }
	
	var typeofStart = typeof $start;
	if(typeofStart !== "object") {
	    this._warn("Energy: $start should be an object, it is a " + typeofStart + " instead");
        return false;
	}

    var $function = $start.getAncestor(functionJpName);

    if ($function === undefined) {
        println("Energy: tried to measure energy at joinpoit " + $start + ", but it is not inside a function");
        return false;
    }
	
    if ($end !== undefined) {
		
		var typeofEnd = typeof $end;
		if(typeofEnd !== "object") {
			this._warn("Energy: $end should be an object, it is a " + typeofEnd + " instead");
			return false;
		}
		
        var $endFunction = $end.getAncestor(functionJpName);

        if ($endFunction === undefined) {
            println("Energy: tried to end measuring energy at joinpoit " + $end + ", but it is not inside a function");
            return false;
        }

        // TODO: Checking if it is the same function not implemented yet, requires attribute '$function.id'
    }

    return true;
}


