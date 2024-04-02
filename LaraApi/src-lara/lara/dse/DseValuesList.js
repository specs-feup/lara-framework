import { arrayFromArgs } from "../core/LaraCore.js";
import DseValues from "./DseValues.js";
/**
 * Iterates over a list of values.
 */
export default class DseValuesList extends DseValues {
    currentIndex = 0;
    values;
    constructor(...values) {
        super();
        if (values.length === 0) {
            throw "DseValuesList: needs at least one value as argument";
        }
        this.values = arrayFromArgs(values);
    }
    getType() {
        return "DseValuesList";
    }
    /**
     * @returns the next element.
     */
    next() {
        const value = this.values[this.currentIndex];
        this.currentIndex++;
        return value;
    }
    /**
     * @returns true if it has another element to return.
     */
    hasNext() {
        return this.currentIndex < this.values.length;
    }
    /**
     * Resets the iterator.
     */
    reset() {
        this.currentIndex = 0;
    }
    getNumElements() {
        return this.values.length;
    }
    /**
     * @returns The number of values returned by a call to next(). A value of one means one value, a value greater than one means an array with that amount of values.
     */
    getNumValuesPerElement() {
        return 1;
    }
}
//# sourceMappingURL=DseValuesList.js.map