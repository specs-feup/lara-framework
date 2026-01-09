import { LaraJoinPoint } from "../../LaraJoinPoint.js";
import JoinPoints from "../../weaver/JoinPoints.js";
import { isJoinPoint } from "../core/LaraCore.js";

type ElementType = { content: string | number; type: number };

/**
 * Logger object, for inserting code that prints/saves information to files.
 *
 * @param isGlobal - Not implemented, please ignore
 * @param filename - If set, instead of printing, will insert code for writing output to this file
 *
 */
export default abstract class LoggerBase<T extends LaraJoinPoint> {
  protected isGlobal: boolean;
  protected filename: string | undefined;
  protected currentElements: ElementType[] = [];
  private functionMap: Record<string, string> = {};
  protected afterJp: T | undefined = undefined;

  Type: Map<string, number> = new Map();

  constructor(isGlobal = false, filename?: string) {
    if (isGlobal) {
      console.log(
        "[Logger-warning] global Logger is not implemented yet, reverting to local Logger"
      );
      isGlobal = false;
    }

    this.isGlobal = isGlobal;
    this.filename = filename;

    this.Type.set("NORMAL", 1);
    this.Type.set("INT", 2);
    this.Type.set("DOUBLE", 3);
    this.Type.set("STRING", 4);
    this.Type.set("CHAR", 5);
    this.Type.set("HEX", 6);
    this.Type.set("OCTAL", 7);
    this.Type.set("LITERAL", 8);
    this.Type.set("LONG", 9);
  }

  /**
   * Used for both C and Java printf functions
   */
  printfFormat: Record<number, string | undefined> = {
    1: undefined,
    2: "%d",
    3: "%f",
    4: "%s",
    5: "%c",
    6: "%x",
    7: "%o",
    8: undefined,
    9: "%ld",
  };

  isGlobalFn() {
    console.log("Is Global Fn:", this.isGlobal);
  }

  /**
   *  The 'last' join point after .log() is called.
   *
   */
  getAfterJp(): T | undefined {
    return this.afterJp;
  }

  private clear() {
    this.currentElements = [];
  }

  abstract log($jp: T, insertBefore: boolean): void;

  /**
   * Helper method which call 'log' with 'insertBefore' set to true
   *
   */
  logBefore($jp: T) {
    this.log($jp, true);
  }

  /**
   * Verifies that the given $jp is inside a function.
   *
   * Requires global attribute 'ancestor'.
   *
   * @returns true if $jp is inside a function, false otherwise
   */
  // TODO: This function should receive LaraJoinPoints but they do not have the getAncestor method
  _validateJp($jp: any, functionJpName: string = "function") {
    const $function = $jp.getAncestor(functionJpName);

    if ($function === undefined) {
      console.log(
        "Logger: tried to insert log around joinpoint " +
          $jp.joinPointType +
          ", but is not inside a function"
      );
      this.clear();
      return false;
    }

    return true;
  }

  _insert($jp: T, insertBefore: boolean, code: string) {
    this._insertCode($jp, insertBefore, code);

    // Clear internal state
    this.clear();
  }

  /**
   * Inserts the given code before/after the given join point.
   *
   * Override this method if you need to specialize the insertion.
   */
  // TODO: This function should receive LaraJoinPoints but they do not have the insertAfter method
  _insertCode($jp: T, insertBefore: boolean, code: string) {
    const insertBeforeString = insertBefore ? "before" : "after";

    if (insertBefore) {
      $jp.insert(insertBeforeString, code);
      this.afterJp = $jp;
    } else {
      this.afterJp = $jp.insert("after", code)[0] as T;
    }
  }

  /**
   * Appends the given string to the current buffer.
   *
   * @param text - The text to append
   * @returns The current logger instance
   */
  append(text: string) {
    return this._append_private(text, this.Type.get("NORMAL"));
  }

  /**
   * The same as 'append'.
   *
   * @param text - the text to append
   * @returns The current logger instance
   */
  text(text: string) {
    return this.append(text);
  }

  /**
   * The same as 'append', but adds a new line at the end of the buffer.
   *
   * @param text - the text to append
   * @returns The current logger instance
   */
  appendln(text: string) {
    return this.append(text).ln();
  }

  /**
   * Appends a new line to the buffer.
   *
   * @returns The current logger instance
   */
  ln() {
    return this._append_private("\\n", this.Type.get("NORMAL"));
  }

  /**
   * Appends a tab to the buffer.
   *
   * @returns The current logger instance
   */
  tab() {
    return this.append("\\t");
  }

  /**
   * Appends an expression that represents a double.
   *
   * @param expr - The expression to append
   * @returns The current logger instance
   */
  appendDouble(expr: T | string) {
    return this._append_private(expr, this.Type.get("DOUBLE"));
  }

  /**
   * The same as 'appendDouble'.
   *
   * @param expr - The expression to append
   * @returns The current logger instance
   */
  double(expr: T | string) {
    return this.appendDouble(expr);
  }

  /**
   * Appends an expression that represents a int.
   *
   * @param expr - The expression to append
   * @returns The current logger instance
   */
  appendInt(expr: T | string) {
    return this._append_private(expr, this.Type.get("INT"));
  }

  /**
   * The same as 'appendInt'.
   *
   * @param expr - The expression to append
   * @returns The current logger instance
   */
  int(expr: T | string) {
    return this.appendInt(expr);
  }

  /**
   * Appends an expression that represents a long.
   *
   * @param expr - The expression to append
   * @returns The current logger instance
   */
  appendLong(expr: T | string) {
    return this._append_private(expr, this.Type.get("LONG"));
  }

  /**
   * The same as 'appendLong'.
   *
   * @param expr - The expression to append
   * @returns The current logger instance
   */
  long(expr: T | string) {
    return this.appendLong(expr);
  }

  /**
   * Appends an expression that represents a string.
   *
   * @param expr - The expression to append
   * @returns The current logger instance
   */
  appendString(expr: T | string) {
    return this._append_private(expr, this.Type.get("STRING"));
  }

  /**
   * The same as 'appendString'.
   *
   * @param expr - The expression to append
   * @returns The current logger instance
   */
  string(expr: T | string) {
    return this.appendString(expr);
  }

  /**
   * Appends an expression that represents a char.
   *
   * @param expr - The expression to append
   * @returns The current logger instance
   */
  appendChar(expr: T | string) {
    return this._append_private(expr, this.Type.get("CHAR"));
  }

  /**
   * The same as 'appendChar'.
   *
   * @param expr - The expression to append
   * @returns The current logger instance
   */
  char(expr: T | string) {
    return this.appendChar(expr);
  }

  /**
   * Appends an expression that represents a hex number.
   *
   * @param expr - The expression to append
   * @returns The current logger instance
   */
  appendHex(expr: T | string) {
    return this._append_private(expr, this.Type.get("HEX"));
  }

  /**
   * The same as 'appendHex'.
   *
   * @param expr - The expression to append
   * @returns The current logger instance
   */
  hex(expr: T | string) {
    return this.appendHex(expr);
  }

  /**
   * Appends an expression that represents an octal.
   *
   * @param expr - The expression to append
   * @returns The current logger instance
   */
  appendOctal(expr: T | string) {
    return this._append_private(expr, this.Type.get("OCTAL"));
  }

  /**
   * The same as 'appendOctal'.
   *
   * @param expr - The expression to append
   * @returns The current logger instance
   */
  octal(expr: T | string) {
    return this.appendOctal(expr);
  }

  /**** PRIVATE METHODS ****/

  protected _append_private(message: T | string, type?: number) {
    // If message is a join point, convert to code first
    if (isJoinPoint(message) || message instanceof LaraJoinPoint) {
      message = JoinPoints.getCode(message as LaraJoinPoint);
    }

    // Do not push message if empty
    if (message === "" || type === undefined) {
      return this;
    }

    this.currentElements.push({ content: message, type: type });
    return this;
  }

  protected _warn(message: string) {
    console.log("[Logger Warning]", message);
  }

  protected _info(message: string) {
    console.log("[Logger]", message);
  }

  // Receives an element{content, type} and returns the content with or without quotation marks, accordingly
  protected _getPrintableContent(element: ElementType): string {
    const enumType = this.Type;
    let content = element.content;
    if (element.type === enumType.get("LITERAL")) {
      return String(content);
    }

    if (
      element.type === enumType.get("NORMAL") ||
      element.type === enumType.get("STRING")
    ) {
      return '"' + content + '"';
    }

    if (element.type === enumType.get("CHAR")) {
      return "'" + content + "'";
    }

    // Test if it has a decimal point
    if (element.type === enumType.get("DOUBLE")) {
      if (typeof content !== "number") {
        return content;
      }

      const indexOfDecimal = String(content).indexOf(".");
      if (indexOfDecimal === -1) {
        content = String(content) + ".0";
      }

      return String(content);
    }

    return String(content);
  }

  /**
   * Generates printf like code for c and java
   *
   * @param printFunctionName - The name of the function to use (printf for C, System.out.println for Java)
   */
  protected _printfFormat(
    printFunctionName: string,
    prefix: string = "(",
    suffix: string = ");",
    delimiter: string = '"'
  ): string {
    // Create code from elements
    let code =
      printFunctionName +
      prefix +
      delimiter +
      this.currentElements
        .map((element) => {
          const enumType = this.Type;
          if (element.type === enumType.get("NORMAL")) {
            return element.content;
          }

          return this.printfFormat[element.type];
        })
        .join("") +
      delimiter;

    const valuesCode = this.currentElements
      // Filter only non-NORMAL types
      .filter((element) => {
        const enumType = this.Type;
        return element.type !== enumType.get("NORMAL");
      })
      .map((element) => {
        // Even though _getPrintableContent tests an always unmet condition (type === NORMAL) it represents a reusable piece of code for both C and C++
        return this._getPrintableContent(element);
      })
      .join(", ");

    if (valuesCode.length > 0) {
      code = code + ", " + valuesCode;
    }

    code = code + suffix;
    return code;
  }

  /**
   *
   *
   * @param $function - Function where name will be declared
   * @param nameGenerator - Function that receives no arguments and generates a new name
   */
  protected _declareName(functionId: string, nameGenerator: () => string) {
    let name = this.functionMap[functionId];
    let alreadyDeclared = false;

    if (name !== undefined) {
      alreadyDeclared = true;
    } else {
      name = nameGenerator();
      this.functionMap[functionId] = name;
      alreadyDeclared = false;
    }

    return {
      name: name,
      alreadyDeclared: alreadyDeclared,
    };
  }
}
