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

/**
 * Returns the value if defined or the provided default value. This useful for optional parameters of functions.
 * 
 * @param {Object} value - the original value
 * @param {Object} defaultValue - the default value
 * */
function defaultValue(value, defaultValue) {
	return value === undefined ? defaultValue : value;
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
	//println("checkDefined() deprecated, use instead lara.Check");
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
    if(typeName === undefined || typeName.length === 0) {
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
	
	// Special case: array
	if(type === "array" && isArray(value)) {
		return;
	}
    
    var message = "Expected value to be of type '" + type + "', but is of type '" + (typeof value) + "'";

	if(source !== undefined) {
		message = source + ": " + message;
	}
	
	throw message;
}	 

function checkBoolean(variable, source) {
	checkType(variable, "boolean", source);
};

function checkString(variable, source) {
	checkType(variable, "string", source);
};

function checkNumber(variable, source) {
	checkType(variable, "number", source);
};

function checkArray(variable, source) {
	checkType(variable, "array", source);
};

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

function isString(variable) {
	return (typeof variable) === "string";
};

/**
 * @param {*} variable
 * @param {string} [javaClassname = java.lang.Object] 
 *
 * @return {boolean} true if the given object is an instance of the given Java class name
 */
function isJavaClass(variable, javaClassname) {
	if(javaClassname === undefined) {
		javaClassname = "java.lang.Object";
	}
	
	var javaClass = Java.type(javaClassname);
	return javaClass.class.isInstance(variable);
};


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
 * If there is a single argument after the start index and that argument is an array, that array will be returned.
 * 
 * @args {Arguments} args  - The original arguments object.
 * @args {Number} [start=0] - The index where we begin the conversion (inclusive).
 * */
function arrayFromArgs(args, start) {
	
    checkDefined(args, 'args', 'LaraCore arrayFromArgs');
    
    if(start === undefined) {
        start = 0;
    }
    
	// If only one element and is already an array, just return the array
	if(args.length === (start + 1) && isArray(args[start])) {
		return args[start];
	}
	
    return Array.prototype.slice.call(args, start);
}

/**
 * @return true if the given value is an array, false otherwise
 */
function isArray(value) {
	return Array.isArray(value);
}

/**
 * Adds the elements of an array into another array.
 */
function pushArray(receivingArray, sourceArray) {
	for(var index in sourceArray) {
		receivingArray.push(sourceArray[index]);	
	}
}
