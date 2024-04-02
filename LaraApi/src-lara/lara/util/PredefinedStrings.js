import { arrayFromArgs } from "../core/LaraCore.js";
/**
 * Represents a set of predefined strings.
 */
export default class PredefinedStrings {
    _name;
    _strict;
    _valuesSet = new Set();
    constructor(name, strict, ...values) {
        this._name = name;
        this._strict = strict;
        values = arrayFromArgs(values);
        for (const value of values) {
            this._valuesSet.add(value.toString());
        }
    }
    /**
     * @returns Available values.
     */
    values() {
        return Array.from(this._valuesSet.values());
    }
    /**
     * @returns True if the given String is a valid value.
     */
    isValid(value) {
        return this._valuesSet.has(value);
    }
    test(value) {
        if (!this.isValid(value)) {
            const message = `Invalid ${this._name} '${value}'. Available values: ${this.values().join(", ")}`;
            if (this._strict) {
                throw message;
            }
            console.log(message);
            return false;
        }
        return true;
    }
    parse(...args) {
        const argsArray = arrayFromArgs(args);
        // Clear benchmarks
        const parsedValues = [];
        for (const arg of argsArray) {
            if (!this.test(arg)) {
                continue;
            }
            parsedValues.push(arg);
        }
        return parsedValues;
    }
}
//# sourceMappingURL=PredefinedStrings.js.map