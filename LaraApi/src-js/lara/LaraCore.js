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

function checkInstance(value, type, source, userTypeName) {
    if(typeof type !== "function")  {
        throw "checkInstance: parameter type must be a function";       
    }
    
    if(value instanceof type) {
        return;
    }    

    // Try to get name from type
    var typeName = type.name;
    
    // If no name, try to use user type name
    if(typeName.length === 0) {
        typeName = userTypeName;
    }

    // If still undefined, add placeholder
    if(typeName === undefined) {
        typeName = "<could not determine>";
    }

    var valueName = value.constructor.name;
    if(valueName.length === 0) {
        valueName = undefined;
    }
    

    var message = "Expected value to be of type '" + typeName + "'";

    if(valueName !== undefined) {
        message +=  ", but is of type '" + valueName + "'";
    } else {
        message += ", but is of another type. The code of the constructor function is:\n" + value.constructor;
    }

	
    if(source !== undefined) {
		message = source + ": " + message;
	}
    
	throw message;
    
    //_throwTypeException(value.constructor.name, type.name, source);
    //_throwTypeException(value, type.name, source);
}


function checkType(value, type, source) {
    if(typeof type !== "string")  {
        throw "checkType: parameter type must be a string";       
    }
    
    if(typeof value === type) {
        return;
    }
    
    var message = "Expected value to be of type '" + type + "', but is of type '" + (typeof value) + "'";

	if(source !== undefined) {
		message = source + ": " + message;
	}
	
	throw message;
}	 

function checkJoinPoint($jp, source) {
    
    if(Weaver.isJoinPoint($jp)) {
        
        return;
    }
    
    var message = "Expected variable to be of type join point, but it's of type '" + (typeof $jp) + "'";
    
    if(source !== undefined) {
        message = source + ": " + message;
    }

    throw message; 
}

function checkJoinPointType($jp, type, source) {
   
    checkJoinPoint($jp, source);
   
    if(Weaver.isJoinPoint($jp, type)) {
        
        return;
    }

    var message = "Expected variable to be a join point of type '" + type + "', but it's of type '" + $jp.joinPointType + "'";
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
