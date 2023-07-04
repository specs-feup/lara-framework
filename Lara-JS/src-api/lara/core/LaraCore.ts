export let LARA_DEBUG = false;

export function setDebug(value: any) {
  if (value === undefined) {
    LARA_DEBUG = true;
    return;
  }

  LARA_DEBUG = value;
}

export function isDebug() {
  return LARA_DEBUG;
}

export function notImplemented(functionName: string) {
  functionName = defaultValue(functionName, "<unknown>");

  throw (
    "Function " +
    functionName +
    " not implemented yet for this weaver implementation"
  );
}

/**
 * Returns the value if defined or the provided default value. This useful for optional parameters of functions.
 *
 * @param {any} value - the original value
 * @param {any} defaultValue - the default value
 * */
export function defaultValue(value: any, defaultValue: any) {
  return value === undefined ? defaultValue : value;
}

/**
 * Temporary method that returns true if the given value is undefined or null,
 * to be used while WeaverGenerator does not support returning undefined from Java.
 */
export function isUndefined(value: any) {
  return value === undefined || value === null;
}

/**
 * Throws an exception if the given expression evaluates to false.
 */
export function checkTrue(
  booleanExpr: boolean,
  message: string | undefined,
  source?: string | undefined
) {
  if (!booleanExpr) {
    if (message === undefined) {
      message = "checkTrue failed";
    }

    if (source !== undefined) {
      message = source + ": " + message;
    }

    throw message;
  }
}

export function checkDefined(
  value: any,
  varName?: string | undefined,
  source?: string | undefined
) {
  if (!isUndefined(value)) {
    return;
  }

  // Undefined, throw exception
  var message = "Value ";
  if (varName !== undefined) {
    message += varName + " ";
  }
  if (source !== undefined) {
    message = source + ": " + message;
  }
  message += "is undefined";

  throw message;
}

// TODO: type should be a JP
export function checkInstance(
  value: any,
  type: any,
  source: string,
  userTypeName: string
) {
  if (value instanceof type) {
    return;
  }

  // Try to get name from type
  var typeName = type.class;

  // If no name, try to use user type name
  if (typeName === undefined || typeName.length === 0) {
    typeName = userTypeName;
  }

  // If still undefined, add placeholder
  if (typeName === undefined) {
    typeName = "<could not determine>";
  }

  var valueName = value.getClass().getName();
  if (valueName.length === 0) {
    valueName = undefined;
  }

  var message = "Expected value to be of type '" + typeName + "'";

  if (valueName !== undefined) {
    message += ", but is of type '" + valueName + "'";
  } else {
    message +=
      ", but is of another type. The code of the constructor function is:\n" +
      value.constructor;
  }

  if (source !== undefined) {
    message = source + ": " + message;
  }

  throw message;
}

export function checkType(
  value: any,
  type: string,
  source?: string | undefined
) {
  if (typeof type !== "string") {
    throw "checkType: parameter type must be a string";
  }

  if (typeof value === type) {
    return;
  }

  // Special case: array
  if (type === "array" && isArray(value)) {
    return;
  }

  var message =
    "Expected value to be of type '" +
    type +
    "', but is of type '" +
    typeof value +
    "'";

  if (source !== undefined) {
    message = source + ": " + message;
  }

  throw message;
}

export function checkBoolean(variable: any, source: string) {
  checkType(variable, "boolean", source);
}

export function checkString(variable: any, source?: string | undefined) {
  checkType(variable, "string", source);
}

export function checkNumber(variable: any, source: string) {
  checkType(variable, "number", source);
}

export function checkArray(variable: any, source: string) {
  checkType(variable, "array", source);
}

/**
 * @return true if the given value is an array, false otherwise
 */
export function isArray(value: any) {
  return value instanceof Array;
}

export function toArray(objectWithLength: any) {
	//return Array.prototype.slice.call(objectWithLength);
	
	var newArray = [];
	for(var index in objectWithLength) {		
		newArray.push(objectWithLength[index]);
	}
		
	return newArray;
}
