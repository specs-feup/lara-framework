var LARA_DEBUG = false;

function setDebug(value) {
	if(value === undefined) {
		LARA_DEBUG = true;
		return;
	}
	
	LARA_DEBUG = value;
}

function isDebug() {
	return LARA_DEBUG;
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
	//println("Instance of class? " + (type instanceof Java.type("java.lang.Class")));
	//println("Value is instance? " + (value instanceof type));
	
	// Not supported in Graal
    //if(typeof type !== "function")  {
    //    throw "checkInstance: parameter type must be a function";       
    //}
        // Try to get name from type


 
    if(value instanceof type) {
        return;
    }    


    //var typeClass = new type;
    //var typeName = typeClass.getSimpleName();

    // Try to get name from type
    var typeName = type.class;
    //println("Type name: " + typeName);

    // If no name, try to use user type name
    if(typeName === undefined || typeName.length === 0) {
        typeName = userTypeName;
    }

    // If still undefined, add placeholder
    if(typeName === undefined) {
        typeName = "<could not determine>";
    }

    var valueName = value.getClass().getName();
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


function isJoinPoint($jp, type) {
	var isJoinPoint = Java.type("org.lara.interpreter.weaver.interf.JoinPoint").isJoinPoint($jp);

	if(type === undefined) {
		return isJoinPoint;
	}

	if(!isJoinPoint) {
		throw "isJoinPoint: Asking if object is of join point '"+type+"', but object is not a join point";
	}
	
	return $jp.instanceOf(type);
	//return Weaver.isJoinPoint($jp);
}

function checkJoinPoint($jp, source) {
    
    if(isJoinPoint($jp)) {
        
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
   
    if(isJoinPoint($jp, type)) {
        
        return;
    }

    var message = "Expected variable to be a join point of type '" + type + "', but it's of type '" + $jp.joinPointType + "'";
    if(source !== undefined) {
        message = source + ": " + message;
    }

    throw message; 
}

function isString(variable) {
	return (typeof variable) === "string" || (variable instanceof String);
};

function isObject(variable) {
	return (typeof variable) === "object";
};

function isFunction(variable) {
	return (typeof variable) === "function";
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
	
	//var javaClass = Java.type(javaClassname);
	//return javaClass.class.isInstance(variable);
	return SpecsSystem.isInstance(javaClassname, variable);
};


function toArray(objectWithLength) {
	//return Array.prototype.slice.call(objectWithLength);
	
	var newArray = [];
	for(var index in objectWithLength) {		
		newArray.push(objectWithLength[index]);
	}
		
	return newArray;
}

function info(message, origin) {
	var composedMessage = message;
	
	if(origin !== undefined) {
		composedMessage = origin + ": " + composedMessage;
	}
	
	println(composedMessage);
}

/**
 * @param {string|function} message - The message to print. Accepts functions, that are only evaluated if debug information is enabled. Use functions if the debug message can include expensive processing.
 */
function debug(message, origin) {
	if(LARA_DEBUG) {
		let processedMessage = isFunction(message) ? message() : message;
		info("[DEBUG] " + processedMessage, origin);
	}
}

function debugObject(object, origin) {
	if(LARA_DEBUG) {
		var lines = object2string(object).split("\n");
		info("[DEBUG] " + lines.join("\n[DEBUG] ", origin));
	}
}


/**
 * Converts an arguments object to a JavaScript array (Array).
 * 
 * If there is a single argument after the start index and that argument is an array, that array will be returned.
 * This helper is kept for Lara code using directly the `arguments` object. If you are using Javascript, consider using [https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Functions/rest_parameters](rest parameters) to extract the variadic arguments of your function, and using the `flattenArgsArray` function to perform the single-element flattening, if needed.
 *
 * @args {Arguments} args  - The original arguments object.
 * @args {Number} [start=0] - The index where we begin the conversion (inclusive).
 * */
function arrayFromArgs(args, start) {
	
    checkDefined(args, 'args', 'LaraCore arrayFromArgs');
    
    if(start === undefined || start < 0) {
        start = 0;
    }
        
	// If only one element and is already an array, just return the array
	if(args.length === (start + 1) && isArray(args[start])) {		
		return args[start];
	}

	if(args.length === (start + 1) && isJavaList(args[start])) {
	//	return listToArray(args[start]);
		return toArray(args[start]);
	}
	
	
	//println("Slicing");

    return Array.prototype.slice.call(args, start);
    /*
    var newArray = [];
    var skip = start;
	for(var index in args) {
		if(skip > 0) {
			skip--;
			continue;
		}
		
		newArray.push(args[index]);
	}
		
	return newArray;
	*/
}

/**
 * Flatten an Arguments array. In this context, it means that if the array only contains one element,
 * and that element is an Array, it will be returned instead of the outer Array.
 * 
 * This is implemented for compatibility reasons. As the Lara language used ES5 as its
 * base, there was no spread operator to pass argument arrays to variadic functions, so there
 * is calling code expecting to be able to pass a single array as the variadic argument.
 * 
 * @param {ArrayLike<any>} args Arguments array. Must be some array-like object.
 * @returns Flattened argument array
 */
function flattenArgsArray(args) {
	checkDefined(args, 'args', 'LaraCore collapseArgsArray');
	if (args.length === 1) {
		const singleArgument = args[0];
		if (isArray(singleArgument)) {
			return singleArgument;
		}
		if (isJavaList(singleArgument)) {
			return toArray(singleArgument);
		}
	}
	// use Array.from to ensure it is an array, not some other Array-like variable
	return Array.from(args);
}

/**
 * @return true if the given value is an array, false otherwise
 */
function isArray(value) {
	return value instanceof Array;
	//return Array.isArray(value);
}

/**
 * Adds the elements of an array into another array.
 */
function pushArray(receivingArray, sourceArray) {
	for(var index in sourceArray) {
		receivingArray.push(sourceArray[index]);	
	}
}

/**
 * @param {J#java.util.List} array
 * @returns {Object[]} If the array is a Java list, converts the list to an array. Otherwise, throws an exception.
 */ 
 /*
function listToArray(array) {

	var newArray = [];
	for(var index in array) {
		newArray.push(array[index]);
	}
		
	return newArray;


//	if(!isJavaList(array)) {
//		throw "The argument provided is not a Java List."	
//	}
//
//	if(isArray(array)){
//		return array;
//	}
//
//	if(isJavaList(array)) {
//		
//		var newArray = [];
//		for(var index in array) {
//			newArray.push(array[index]);
//		}
//		
//		return newArray;
//	}
//
//	throw "The argument provided is not an array nor a List."

}
*/


function isJavaList(list) {
	return list instanceof Java.type("java.util.List");
}



/*
 * Custom getter that is used as a compatibility layer between JS properties and Java methods.
 *
 * The name of this functions must be the same as the value of the field org.lara.interpreter.weaver.interf.JoinPoint.LARA_GETTER .
 */
function laraGetter(object, property) {
	var value = object[property];
	var type = typeof value;


	// If type is function, assume it should be called without arguments
	if(type === 'function') {
		
		// Java object
		if(isJavaClass(object) && !isUndefined(object.class)) {
			// Special case: property 'class'
			if(property === 'class') {
				return object.class;
			}
		
			return Java.type("pt.up.fe.specs.util.SpecsSystem").invokeAsGetter(object, property);
			//return SpecsSystem.invokeAsGetter(object, property);
		}
		
		// JS object
		return value;
    }
    
	return value;
}

function jpGetter(object, property) {
	// If not a Java object, treat it as a normal JS object
	if(!isJavaClass(object)) {
		return object[property];
	}
	
	// Special case: property 'class'
	if(property === 'class') {
		return object.class;
	}
		
	return Java.type("pt.up.fe.specs.util.SpecsSystem").invokeAsGetter(object, property);
}

function stringReplacer(string, oldSequence, newSequence) {
	return string.replace(oldSequence, newSequence);
}

function exit() {
	throw "function 'exit()' has been deprecated. Please use another way of stoping the script (e.g., by returning)";
}

/**
 * @return an array with the keys of an object
 */
function getKeys(object) {
	var keys = [];
	
	for(var key in object) {
		keys.push(key);
	}
	
	return keys;
}

/**
 * @return an array with the values of an object
 */
function getValues(object) {
	var values = [];
	
	for(var key in object) {
		values.push(object[key]);
	}
	
	return values;
}

/**
 * Acts as a constructor where you can pass the arguments object.
 * 
 * @return the constructed object, of the constructor if it could not be built.
 */
function newObject(aClass, args){
	var obj = Object.create(aClass.prototype);
	return (aClass.apply(obj, args) || obj);
}

/**
 * Function to call aspects, returns the aspect with the results. 
 */
function callAspect(aspect) {
	var aspectObj = newObject(aspect, Array.prototype.slice.call(arguments, 1));
	aspectObj.call();
	return aspectObj;
}

let _LARA_IMPORT_LOADED = {};

function laraImport(importName) {
	checkString(importName, "laraImport (LaraCore.js)");

	// Return if already loaded
	if(_LARA_IMPORT_LOADED[importName] !== undefined) {
		debug(() => "laraImport: import " + importName + " already processed, ignoring");	
		return;
	}

	// Import 
	_LARA_IMPORT_LOADED[importName] = true;
	debug(() => "laraImport: importing " + importName);		
	LaraI.loadLaraImport(importName);
}