import JavaTypes from "./util/JavaTypes.js";
import * as LaraCore from "./core/LaraCore.js";

/**
 * Utility methods to check preconditions.
 *
 */
export default class Check {
  /**
   * @deprecated Use the javascript `throw` statement instead
   */
  static isTrue(
    booleanExpr: boolean,
    message = "Check.isTrue failed",
    source?: string
  ) {
    LaraCore.checkTrue(booleanExpr, message, source);
  }

  /**
   * @deprecated Use the javascript '===' operator instead
   */
  static isUndefined<T>(value: T): boolean {
    return LaraCore.isUndefined(value);
  }

  /**
   * @deprecated Use the javascript '===' operator instead
   */
  static isDefined<T>(value: T, message = "Value is undefined") {
    LaraCore.checkDefined(value, message);
  }

  /**
   * @deprecated Use the javascript 'instanceof' operator instead
   */
  static instance(
    value: any,
    type: any,
    source?: string,
    userTypeName?: string
  ) {
    LaraCore.checkInstance(value, type, source, userTypeName);
  }

  /**
   * @deprecated Use LaraCore.checkType instead
   */
  static type<T>(value: T, type: string, source: string) {
    LaraCore.checkType(value, type, source);
  }

  /**
   * @deprecated Use the javascript `instanceof` operator instead
   */
  static isBoolean<T>(variable: T, source: string) {
    Check.type(variable, "boolean", source);
  }

  /**
   * @deprecated Use the javascript `instanceof` operator instead
   */
  static isString<T>(variable: T, source: string) {
    Check.type(variable, "string", source);
  }

  /**
   * @deprecated Use the javascript `instanceof` operator instead
   */
  static isNumber<T>(variable: T, source: string) {
    Check.type(variable, "number", source);
  }

  /**
   * @deprecated Use the javascript `instanceof` operator instead
   */
  static isArray<T>(variable: T, source: string) {
    Check.type(variable, "array", source);
  }

  /**
   * @deprecated Use the javascript `instanceof` operator instead
   */
  static isRegex<T>(variable: T, source: string) {
    Check.type(variable, "regex", source);
  }

  /**
   * Checks if the given value is a join point. If a type is given, checks if the join point is an instance of the given type. Otherwise, throws an exception.
   *
   * @param {$jp} $jp -
   * @param type -
   * @param isOptional - If true, passes check if value is undefined
   */
  static isJoinPoint(
    $jp: any,
    type: string | undefined = undefined,
    isOptional = false
  ) {
    if (isOptional && $jp === undefined) {
      return;
    }
    if (!JavaTypes.JoinPoint.isJoinPoint($jp)) {
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
  }

  /**
   * Checks if two strings are identical, not considering empty spaces. Throws and exception if strings do not match.
   */
  static strings(currentString: string, expectedString: string) {
    // Normalize both strings
    currentString = JavaTypes.SpecsStrings().normalizeFileContents(
      currentString.toString(),
      true
    );
    expectedString = JavaTypes.SpecsStrings().normalizeFileContents(
      expectedString.toString(),
      true
    );
    if (currentString !== expectedString) {
      //throw "Current result does not match expected result.\nCurrent result begin:\n"+currentString+"\nCurrent result end\n\nExpected result begin:\n"+expectedString+"\nExpected result end";
      throw (
        "Current result does not match expected result. Diff:\n" +
        Check.diff(expectedString, currentString)
      );
    }
  }

  /**
   * @param original - The original text
   * @param revised - The revised text
   */
  static diff(original: any, revised: any) {
    return JavaTypes.Diff().Diff(original.toString(), revised.toString());
  }

  /**
   * Checks if the array contains the element. Throws an expression if it doens't.
   * The test is equivalent to array.indexOf(element) != -1.
   */
  static arrayContains(
    array: Array<any>,
    element: any,
    message: string | undefined,
    source: string | undefined
  ) {
    if (array.indexOf(element) == -1) {
      if (message === undefined) {
        message = "Check.arrayContains failed";
      }
      if (source !== undefined) {
        message = source + ": " + message;
      }
      throw message;
    }
  }
}
