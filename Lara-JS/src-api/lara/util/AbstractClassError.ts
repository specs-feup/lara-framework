/**
 * Custom error to be thrown when derived classes do not extend an abstract class correctly.
 *
 * To use the error correctly, write something like:
 *
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
 class AbstractClassError extends Error {
    /**
     * @param {} options Options to construct the error
     * @param {"constructor"|"abstractMethod"} options.kind Whether the error is being thrown by an abstract constructor or method
     * @param {} options.baseClass The base abstract class
     * @param {} options.derivedClass The derived class. Only used when building an error for an abstract method.
     * @param {} options.method The method that is not implemented. Only used when building an error for an abstract method.
     */
    constructor(options: any) {
      let message;
      if (options.kind === "constructor") {
        const { baseClass } = options;
        message = `Cannot instantiate abstract class ${baseClass.name}.`;
      } else if (options.kind === "abstractMethod") {
        const { baseClass, derivedClass, method } = options;
        message = `Derived class ${derivedClass.name} has not implemented abstract method ${baseClass.name}::${method.name}.`;
      }
      super(message);
      this.name = "AbstractClassError";
    }
  }
  