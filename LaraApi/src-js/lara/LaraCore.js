function notImplemented(functionName) {
	
	functionName = defaultValue(functionName, "<unknown>");
	
	//println("Objects: " + Debug.getObjects(this).join());
	//println("Weaver functions: " + getFunctions(Weaver).join());
	
	throw "Function " + functionName + " not implemented yet for this weaver implementation";
}

function defaultValue(variable, defaultValue) {
	return variable === undefined ? defaultValue : variable;
}

/**
 * Temporary method that returns true if the given value is undefined or null, 
 * to be used while WeaverGenerator does not support returning undefined from Java. 
 */
function isUndefined(value) {
	return value === undefined || value === null;
}

function checkTrue(booleanExpr, message) {
	if(!booleanExpr) {
		if(message === undefined) {
			message = "checkTrue failed";
		}
		
		throw message;
	}
}

function checkDefined(value, varName, source) {
	if(!isUndefined(value)) {
		return;
	}
	
	// Undefined, throw exception
	var message = "Value ";
	if(varName !== undefined) {
		message += varName + " ";
	}
	if(source !== undefined) {
		message = source + ": " + message;
		//message += "from " + source + " ";
	}
	message += "is undefined";
	
	throw message;
}

function checkType(value, type, source) {
	
	if(typeof value === type) {
		return;
	}
	
	var message = "Expected value to be of type '" + type + "', but is of type " + (typeof value);

	if(source !== undefined) {
		message = source + ": " + message;
	}
	
	throw message;
}	 

function toArray(objectWithLength) {
	return Array.prototype.slice.call(objectWithLength);
}


