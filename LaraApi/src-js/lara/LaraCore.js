var LARA_DEBUG = false;

function setDebug(value) {
	if(value === undefined) {
		LARA_DEBUG = true;
		return;
	}
	
	LARA_DEBUG = value;
}

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

/**
 * Throws an exception if the given expression evaluates to false.
 */
function checkTrue(booleanExpr, message, source) {
	if(!booleanExpr) {
		if(message === undefined) {
			message = "checkTrue failed";
		}
		
		if(source !== undefined) {
			message = source + ": " + message;
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

function info(message, origin) {
	var composedMessage = message;
	
	if(origin !== undefined) {
		composedMessage = origin + ": " + composedMessage;
	}
	
	println(composedMessage);
}

function debug(message, origin) {
	if(LARA_DEBUG) {
		info("[DEBUG] " + message, origin);
	}
}


/**
 * Converts an arguments object to a JavaScript array (Array).
 * 
 * args  - Mandatory. The original arguments object.
 * start - Optional. The index where we begin the conversion (inclusive).
 * */
function arrayFromArgs(args, start) {
	
    checkDefined(args, 'args', 'LaraCore arrayFromArgs');
    
    if(start === undefined) {
    
        start = 0;
    }
    
    return Array.prototype.slice.call(args, start);
}
