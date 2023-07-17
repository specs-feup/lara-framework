import JavaTypes from "../util/JavaTypes.js";
import { println } from "../../core/output.js";
export let LARA_DEBUG = false;
export function setDebug(value = true) {
    LARA_DEBUG = value;
}
export function isDebug() {
    return LARA_DEBUG;
}
export function notImplemented(functionName = "<unknown>") {
    throw ("Function " +
        functionName +
        " not implemented yet for this weaver implementation");
}
/**
 * Returns the value if defined or the provided default value. This useful for optional parameters of functions.
 *
 * @param value - the original value
 * @param defaultValue - the default value
 *
 * @deprecated Use the ECMAScript 6 default parameter value syntax instead
 */
export function defaultValue(value, defaultValue) {
    return value ?? defaultValue;
}
/**
 * Temporary method that returns true if the given value is undefined or null,
 * to be used while WeaverGenerator does not support returning undefined from Java.
 *
 * @deprecated Use the javascript `===` operator instead
 */
export function isUndefined(value) {
    return value === undefined || value === null;
}
/**
 * Throws an exception if the given expression evaluates to false.
 *
 * @deprecated Use the javascript `throw` statement instead
 */
export function checkTrue(booleanExpr, message = "checkTrue failed", source = "<unknown>") {
    if (!booleanExpr) {
        throw `${source}: ${message}`;
    }
}
/**
 * @deprecated Use the javascript '===' operator instead
 */
export function checkDefined(value, varName = "<unknown>", source = "<unknown>") {
    if (value !== undefined) {
        return;
    }
    throw `${source}: Value ${varName} is undefined`;
}
// TODO: type should be a JP
/**
 * @deprecated Use the javascript `instanceof` operator or JavaTypes.instanceOf instead
 */
export function checkInstance(value, type, source, userTypeName) {
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
    }
    else {
        message +=
            ", but is of another type. The code of the constructor function is:\n" +
                value.constructor;
    }
    if (source !== undefined) {
        message = source + ": " + message;
    }
    throw message;
}
export function checkType(value, type, source) {
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
    let message = "Expected value to be of type '" +
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
export function checkBoolean(variable, source) {
    checkType(variable, "boolean", source);
}
/**
 * @deprecated Use the javascript `instanceof` operator instead
 */
export function checkString(variable, source) {
    checkType(variable, "string", source);
}
/**
 * @deprecated Use the javascript `instanceof` operator instead
 */
export function checkNumber(variable, source) {
    checkType(variable, "number", source);
}
/**
 * @deprecated Use the javascript `instanceof` operator instead
 */
export function checkArray(variable, source) {
    checkType(variable, "array", source);
}
/**
 * @returns true if the given value is an array, false otherwise
 *
 * @deprecated Use the javascript `instanceof` operator instead
 */
export function isArray(value) {
    return value instanceof Array;
}
/**
 * @deprecated Use the javascript built-in methods instead
 */
export function toArray(objectWithLength) {
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
export function isString(variable) {
    return typeof variable === "string" || variable instanceof String;
}
/**
 * @returns true if the given object is an instance of the given Java class name
 *
 * @deprecated Use JavaTypes.instanceOf instead
 */
export function isJavaClass(variable, javaClassname = "java.lang.Object") {
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
export function arrayFromArgs(args, start = 0) {
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
function isJavaList(list) {
    return JavaTypes.instanceOf(list, "java.util.List");
}
export function info(message, origin) {
    if (origin !== undefined) {
        println(`${origin}: ${message}`);
        return;
    }
    println(message);
}
//# sourceMappingURL=LaraCore.js.map