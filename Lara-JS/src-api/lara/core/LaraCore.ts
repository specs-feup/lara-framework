import { LaraJoinPoint } from "../../LaraJoinPoint.js";
import { object2string } from "../../core/output.js";
import JavaTypes from "../util/JavaTypes.js";

export let LARA_DEBUG = false;

export function setDebug(value = true) {
  LARA_DEBUG = value;
}

export function isDebug() {
  return LARA_DEBUG;
}

export function notImplemented(functionName = "<unknown>") {
  throw (
    "Function " +
    functionName +
    " not implemented yet for this weaver implementation"
  );
}

/**
 * Returns the value if defined or the provided default value. This useful for optional parameters of functions.
 *
 * @param value - the original value
 * @param defaultValue - the default value
 *
 * @deprecated Use the ECMAScript 6 default parameter value syntax instead
 */
export function defaultValue<T1, T2>(value: T1, defaultValue: T2) {
  return value ?? defaultValue;
}

/**
 * Temporary method that returns true if the given value is undefined or null,
 * to be used while WeaverGenerator does not support returning undefined from Java.
 *
 * @deprecated Use the javascript `===` operator instead
 */
export function isUndefined<T>(value: T) {
  return value === undefined || value === null;
}

/**
 * Throws an exception if the given expression evaluates to false.
 *
 * @deprecated Use the javascript `throw` statement instead
 */
export function checkTrue(
  booleanExpr: boolean,
  message = "checkTrue failed",
  source = "<unknown>"
) {
  if (!booleanExpr) {
    throw `${source}: ${message}`;
  }
}

/**
 * @deprecated Use the javascript '===' operator instead
 */
export function checkDefined<T>(
  value: T,
  varName = "<unknown>",
  source = "<unknown>"
) {
  if (value !== undefined) {
    return;
  }

  throw `${source}: Value ${varName} is undefined`;
}

// TODO: type should be a JP
/**
 * @deprecated Use the javascript `instanceof` operator or JavaTypes.instanceOf instead
 */
export function checkInstance(
  value: any,
  type: any,
  source?: string,
  userTypeName?: string
) {
  if (isJavaClass(value)) {
    throw "Should not receive a Java object here";
  }

  if (typeof type !== "function") {
    throw "LaraCore.checkInstance: parameter 'type' must be a function";
  }

  if (value instanceof type) {
    return;
  }

  // Try to get name from type
  let typeName = type.name;

  // If no name, try to use user type name
  if (typeName === undefined || typeName.length === 0) {
    typeName = userTypeName;
  }

  // If still undefined, add placeholder
  if (typeName === undefined) {
    typeName = "<could not determine>";
  }
  let valueName = value.constructor.name;
  if (valueName.length === 0) {
    valueName = undefined;
  }

  let message = "Expected value to be of type '" + typeName + "'";
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

export function checkType<T>(value: T, type: string, source?: string) {
  if (typeof value === type) {
    return;
  }

  // Special case: array
  if (type === "array" && value instanceof Array) {
    return;
  }

  // Special case: regex
  if (type === "regex" && value instanceof RegExp) {
    return;
  }

  let message =
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

/**
 * @deprecated Use the javascript `instanceof` operator instead
 */
export function checkBoolean<T>(variable: T, source: string) {
  checkType(variable, "boolean", source);
}

/**
 * @deprecated Use the javascript `instanceof` operator instead
 */
export function checkString<T>(variable: T, source?: string | undefined) {
  checkType(variable, "string", source);
}

/**
 * @deprecated Use the javascript `instanceof` operator instead
 */
export function checkNumber<T>(variable: T, source: string) {
  checkType(variable, "number", source);
}

/**
 * @deprecated Use the javascript `instanceof` operator instead
 */
export function checkArray<T>(variable: T, source: string) {
  checkType(variable, "array", source);
}

/**
 * @returns true if the given value is an array, false otherwise
 *
 * @deprecated Use the javascript `instanceof` operator instead
 */
export function isArray<T>(value: T) {
  return value instanceof Array;
}

/**
 * @deprecated Use the javascript built-in methods instead
 */
export function toArray(objectWithLength: any) {
  //return Array.prototype.slice.call(objectWithLength);

  var newArray = [];
  for (var index in objectWithLength) {
    newArray.push(objectWithLength[index]);
  }

  return newArray;
}

/**
 * @deprecated Use the javascript `instanceof` operator instead
 */
export function isString<T>(variable: T) {
  return typeof variable === "string" || variable instanceof String;
}

/**
 * @returns true if the given object is an instance of the given Java class name
 *
 * @deprecated Use JavaTypes.isJavaObject or JavaTypes.instanceOf instead
 */
export function isJavaClass<T>(variable: T, javaClassname?: string): boolean {
  if (javaClassname === undefined) {
    return JavaTypes.isJavaObject(variable);
  }
  return JavaTypes.instanceOf(variable, javaClassname);
}

/**
 * Converts an arguments object to a JavaScript array (Array).
 *
 * If there is a single argument after the start index and that argument is an array, that array will be returned.
 * This helper is kept for Lara code using directly the `arguments` object. If you are using Javascript, consider using [https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Functions/rest_parameters](rest parameters) to extract the variadic arguments of your function, and using the `flattenArgsArray` function to perform the single-element flattening, if needed.
 *
 * @param args - The original arguments object.
 * @param start - The index where we begin the conversion (inclusive).
 * */
export function arrayFromArgs(args: any, start = 0) {
  checkDefined(args, "args", "LaraCore arrayFromArgs");

  if (start === undefined || start < 0) {
    start = 0;
  }

  // If only one element and is already an array, just return the array
  if (args.length === start + 1 && isArray(args[start])) {
    return args[start];
  }

  if (args.length === start + 1 && isJavaList(args[start])) {
    return toArray(args[start]);
  }
  return Array.prototype.slice.call(args, start);
}

function isJavaList<T>(list: T) {
  return JavaTypes.instanceOf(list, "java.util.List");
}

export function info(message: string, origin?: string): void {
  if (origin !== undefined) {
    console.log(`${origin}: ${message}`);
    return;
  }
  console.log(message);
}

////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @deprecated Use the javascript `instanceof` operator instead
 */
export function isJoinPoint($jp: any, type?: string): boolean {
  if (!($jp instanceof LaraJoinPoint) && $jp.getClass === undefined ) {
    return false;
  }
  if (type) {
    return $jp.instanceOf(type);
  }

  return true;
}

/**
 * @deprecated Use the javascript `instanceof` operator instead
 */
export function checkJoinPoint($jp: any, source?: string): void {
  if (isJoinPoint($jp)) {
    return;
  }

  let message =
    "Expected variable to be of type join point, but it's of type '" +
    typeof $jp +
    "'";

  if (source !== undefined) {
    message = source + ": " + message;
  }

  throw message;
}

/**
 * @deprecated Use the javascript `instanceof` operator instead
 */
export function checkJoinPointType(
  $jp: LaraJoinPoint,
  type: string,
  source?: string
): void {
  checkJoinPoint($jp, source);

  if (isJoinPoint($jp, type)) {
    return;
  }

  let message =
    "Expected variable to be a join point of type '" +
    type +
    "', but it's of type '" +
    $jp.joinPointType +
    "'";
  if (source !== undefined) {
    message = source + ": " + message;
  }

  throw message;
}

/**
 * @deprecated Use the javascript `instanceof` operator instead
 */
export function isObject<T>(variable: T) {
  return typeof variable === "object";
}

/**
 * @deprecated Use the javascript `instanceof` operator instead
 */
export function isFunction<T>(variable: T) {
  return typeof variable === "function";
}

/**
 * @param message - The message to print. Accepts functions, that are only evaluated if debug information is enabled. Use functions if the debug message can include expensive processing.
 */
export function debug(message: string | (() => string), origin?: string) {
  if (LARA_DEBUG) {
    if (message instanceof Function) {
      message = message();
    }

    info("[DEBUG] " + message, origin);
  }
}

export function debugObject<T>(object: T, origin?: string) {
  if (LARA_DEBUG) {
    const lines = object2string(object).split("\n");
    info("[DEBUG] " + lines.join("\n[DEBUG] "), origin);
  }
}

/**
 * Flatten an Arguments array. In this context, it means that if the array only contains one element,
 * and that element is an Array, it will be returned instead of the outer Array.
 *
 * @param args - Arguments array. Must be some array-like object.
 * @returns Flattened argument array
 *
 * @deprecated This is implemented for compatibility reasons. As the Lara language used ES5 as its
 * base, there was no spread operator to pass argument arrays to variadic functions, so there
 * is calling code expecting to be able to pass a single array as the variadic argument.
 */
export function flattenArgsArray(args: any[]) {
  if (args.length === 1) {
    const singleArgument = args[0];
    if (Array.isArray(singleArgument)) {
      return singleArgument;
    }
    if (isJavaList(singleArgument)) {
      return toArray(singleArgument);
    }
  }
  // use Array.from to ensure it is an array, not some other Array-like variable
  return Array.from(args);
}

/*
 * Custom getter that is used as a compatibility layer between JS properties and Java methods.
 *
 * The name of this functions must be the same as the value of the field org.lara.interpreter.weaver.interf.JoinPoint.LARA_GETTER .
 */
export function laraGetter(object: any, property: string) {
  if (isJavaClass(object)) {
    const value = object[property];

    // If type is function, assume it should be called without arguments
    if (typeof value === "function") {
      // Java object
      if (isJavaClass(object) && !isUndefined(object.class)) {
        // Special case: property 'class'
        if (property === "class") {
          return object.class;
        }

        return JavaTypes.SpecsSystem.invokeAsGetter(object, property);
      }

      // JS object
      return value;
    }

    return value;
  } else {
    for (let obj = object; obj !== null; obj = Object.getPrototypeOf(obj)) {
      const descriptor = Object.getOwnPropertyDescriptor(obj, property);
      if (descriptor !== undefined) {
        let attributeValue: any = undefined;
        if (Object.getOwnPropertyDescriptor(descriptor, "get")) {
          return descriptor.get?.call?.(object);
        } else if (Object.getOwnPropertyDescriptor(descriptor, "value")) {
          return descriptor.value;
        } else {
          continue;
        }
      }
    }
  }
}

/**
 * @returns an array with the keys of an object
 *
 * @deprecated Use the javascript built-in methods instead
 */
export function getKeys<T>(object: T) {
  const keys = [];

  for (const key in object) {
    keys.push(key);
  }

  return keys;
}

/**
 * @returns an array with the values of an object
 *
 * @deprecated Use the javascript built-in methods instead
 */
export function getValues<T>(object: T) {
  const values = [];

  for (const key in object) {
    values.push(object[key]);
  }

  return values;
}

/**
 * Acts as a constructor where you can pass the arguments object.
 *
 * @returns the constructed object, of the constructor if it could not be built.
 *
 * @deprecated Use the javascript built-in methods instead
 */
export function newObject(aClass: any, args?: any[]) {
  const obj = Object.create(aClass.prototype);
  return aClass.apply(obj, args) || obj;
}

const _LARA_IMPORT_LOADED: Set<string> = new Set();

export function laraImport(importName: string) {
  // Return if already loaded
  if (_LARA_IMPORT_LOADED.has(importName)) {
    debug(
      () => "laraImport: import " + importName + " already processed, ignoring"
    );
    return;
  }

  // Import
  _LARA_IMPORT_LOADED.add(importName);
  debug(() => "laraImport: importing " + importName);

  // Check if Kleene Start
  if (importName.endsWith(".*")) {
    _laraImportKleeneStar(importName.substring(0, importName.length - 2));
  }
  // Simple import
  else {
    JavaTypes.LaraI.loadLaraImport(importName);
  }
}

function _laraImportKleeneStar(packageName: string) {
  const laraImports = JavaTypes.LaraI.getLaraImportInPackage(
    packageName
  ) as string[];

  for (const singleLaraImport of laraImports) {
    laraImport(singleLaraImport);
  }
}
