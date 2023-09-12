import Numbers from "../Numbers.js";
/**
 * Generates sequential sequences of combinations, according to the given number of elements.
 */
export default class SequentialCombinations {
    numElements;
    currentValue = 1;
    lastValueUsed = undefined;
    maximumValue;
    constructor(numElements, upTo) {
        this.numElements = numElements;
        // Last combination
        this.maximumValue = Math.pow(2, numElements) - 1;
        if (upTo !== undefined) {
            this.maximumValue = Math.min(this.maximumValue, upTo);
        }
    }
    /**
     * @returns The value used to generate the last combination
     */
    getLastSeed() {
        return this.lastValueUsed;
    }
    /**
     * @returns The next sequence
     */
    next() {
        // Check if there are combinations left
        if (!this.hasNext()) {
            throw ("SequentialCombinations.next: Reached maximum number of combinations (" +
                this.maximumValue +
                ")");
        }
        const combination = Numbers.toIndexesArray(this.currentValue);
        // Next value
        this.lastValueUsed = this.currentValue;
        this.currentValue++;
        return combination;
    }
    /**
     * @returns True if there are stil combinations to generate
     */
    hasNext() {
        return this.currentValue <= this.maximumValue;
    }
}
//# sourceMappingURL=SequentialCombinations.js.map