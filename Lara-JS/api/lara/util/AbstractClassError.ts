/**
 * Options to construct the error
 */
export interface AbstractClassErrorOptions {
  /**
   * Whether the error is being thrown by an abstract constructor or method
   */
  kind: "constructor" | "abstractMethod";
  /**
   * The base abstract class
   */
  baseClass: any;
  /**
   * The derived class. Only used when building an error for an abstract method.
   */
  derivedClass?: any;
  /**
   * The method that is not implemented. Only used when building an error for an abstract method.
   */
  method?: any;
}

/**
 * Custom error to be thrown when derived classes do not extend an abstract class correctly.
 *
 * To use the error correctly, write something like:
 *
 * @example
 * ```js
 * class AnAbstractClass {
 *   constructor() {
 *     if (this.constructor === AnAbstractClass) {
 *       throw new AbstractClassError({
 *         kind: "constructor",
 *         baseClass: AnAbstractClass,
 *       });
 *       // message: "AbstractClassError: Cannot instantiate abstract class AnAbstractClass."
 *     }
 *   }
 *
 *   notImplementedMethod() {
 *     throw new AbstractClassError({
 *       kind: "abstractMethod",
 *       baseClass: AnAbstractClass,
 *       derivedClass: this.constructor,
 *       method: this.notImplementedMethod,
 *     });
 *     // message: "AbstractClassError: Derived class ADerivedClass has not implemented abstract method AnAbstractClass::notImplementedMethod."
 *   }
 * }
 * ```
 *
 * Formats a message like:
 * "AbstractClassError:
 */
export default class AbstractClassError extends Error {
  constructor(options: AbstractClassErrorOptions) {
    let message;
    if (options.kind === "constructor") {
      message = `Cannot instantiate abstract class ${options.baseClass.name}.`;
    } else if (options.kind === "abstractMethod") {
      message = `Derived class ${options.derivedClass.name} has not implemented abstract method ${options.baseClass.name}::${options.method.name}.`;
    }
    super(message);
    this.name = "AbstractClassError";
  }
}
