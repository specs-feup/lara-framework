laraImport("lara._JavaTypes");

/**
 * Utility methods to check preconditions.
 *
 * @class
 */
var Check = {};

/**
 * Throws an exception if the given expression evaluates to false.
 */
Check.isTrue = function (booleanExpr, message, source) {
  if (!booleanExpr) {
    if (message === undefined) {
      message = "Check.isTrue failed";
    }

    if (source !== undefined) {
      message = source + ": " + message;
    }

    throw message;
  }
};

/**
 * @return {boolean} true if the given value is either undefined or null.
 */
Check.isUndefined = function (value) {
  return value === undefined || value === null;
};

/**
 * @param value
 * @param varName
 * @param source
 */
Check.isDefined = function (value, message) {
  //Check.isDefined = function(value, varName, source) {
  if (!Check.isUndefined(value)) {
    return;
  }

  // Undefined, throw exception
  if (message === undefined) {
    message = "Value is undefined";
  }
  /*	
	var message = "Value ";
	if(varName !== undefined) {
		message += varName + " ";
	}
	if(source !== undefined) {
		message = source + ": " + message;
		//message += "from " + source + " ";
	}
	message += "is undefined";
	*/
  throw message;
};

Check.instance = function (value, type, source, userTypeName) {
  if (typeof type !== "function") {
    throw "Check.instance: parameter type must be a function";
  }

  if (value instanceof type) {
    return;
  }

  // Try to get name from type
  var typeName = type.name;

  // If no name, try to use user type name
  if (typeName === undefined || typeName.length === 0) {
    typeName = userTypeName;
  }

  // If still undefined, add placeholder
  if (typeName === undefined) {
    typeName = "<could not determine>";
  }

  var valueName = value.constructor.name;
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

  //_throwTypeException(value.constructor.name, type.name, source);
  //_throwTypeException(value, type.name, source);
};

Check.type = function (value, type, source) {
  if (typeof type !== "string") {
    throw "Check.type: parameter type must be a string";
  }

  if (typeof value === type) {
    return;
  }

  // Special case: array
  if (type === "array" && isArray(value)) {
    return;
  }

  // Special case: regex
  if (type === "regex" && value instanceof RegExp) {
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
};

Check.isBoolean = function (variable, source) {
  Check.type(variable, "boolean", source);
};

Check.isString = function (variable, source) {
  Check.type(variable, "string", source);
};

Check.isNumber = function (variable, source) {
  Check.type(variable, "number", source);
};

Check.isArray = function (variable, source) {
  Check.type(variable, "array", source);
};

Check.isRegex = function (variable, source) {
  Check.type(variable, "regex", source);
};

/**
 * Checks if the given value is a join point. If a type is given, checks if the join point is an instance of the given type. Otherwise, throws an exception.
 *
 * @param {$jp} $jp
 * @param {string} [type=undefined]
 * @param {boolean} [isOptional=false] - If true, passes check if value is undefined
 */
Check.isJoinPoint = function ($jp, type, isOptional) {
  if (isOptional && $jp === undefined) {
    return;
  }

  if (!_JavaTypes.getJoinPoint().isJoinPoint($jp)) {
    throw (
      "Expected variable to be of type join point, but it is of type '" +
      typeof $jp +
      "'"
    );
  }

  if (type !== undefined && !$jp.instanceOf(type)) {
    throw (
      "Expected join point to be an instance of type '" +
      type +
      "' but its type is '" +
      $jp.joinPointType +
      "'"
    );
  }
};

/**
 * Checks if two strings are identical, not considering empty spaces. Throws and exception if strings do not match.
 */
Check.strings = function (currentString, expectedString) {
  //Check.isString(currentString);
  //Check.isString(expectedString);

  // Normalize both strings
  currentString = _JavaTypes
    .getSpecsStrings()
    .normalizeFileContents(currentString.toString(), true);
  expectedString = _JavaTypes
    .getSpecsStrings()
    .normalizeFileContents(expectedString.toString(), true);

  if (currentString !== expectedString) {
    //throw "Current result does not match expected result.\nCurrent result begin:\n"+currentString+"\nCurrent result end\n\nExpected result begin:\n"+expectedString+"\nExpected result end";
    throw (
      "Current result does not match expected result. Diff:\n" +
      Check.diff(expectedString, currentString)
    );
  }
};

/**
 * @param {Object} original - The original text
 * @param {Object} revised - The revised text
 */
Check.diff = function (original, revised) {
  return _JavaTypes
    .getJavaDiff()
    .getDiff(original.toString(), revised.toString());
};

/**
 * Checks if the array contains the element. Throws an expression if it doens't.
 * The test is equivalent to array.indexOf(element) != -1.
 */
Check.arrayContains = function (array, element, message, source) {
  if (array.indexOf(element) == -1) {
    if (message === undefined) {
      message = "Check.arrayContains failed";
    }

    if (source !== undefined) {
      message = source + ": " + message;
    }

    throw message;
  }
};
