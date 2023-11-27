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
    constructor(options) {
        let message;
        if (options.kind === "constructor") {
            message = `Cannot instantiate abstract class ${options.baseClass.name}.`;
        }
        else if (options.kind === "abstractMethod") {
            message = `Derived class ${options.derivedClass.name} has not implemented abstract method ${options.baseClass.name}::${options.method.name}.`;
        }
        super(message);
        this.name = "AbstractClassError";
    }
}
//# sourceMappingURL=AbstractClassError.js.map