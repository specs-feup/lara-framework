import Numbers from "../Numbers.js";
import SequentialCombinations from "./SequentialCombinations.js";
/**
 * Generates sequential sequences of combinations, according to the given number of elements.
 */
export default class Combinations extends SequentialCombinations {
    elements;
    combinationSize;
    numCombinations;
    currentCombinations = 0;
    constructor(elements, combinationSize) {
        super(elements.length);
        this.elements = elements;
        this.combinationSize = combinationSize;
        // Number of combinations
        // n!/(r!(n−r)!)
        const nFact = Numbers.factorial(elements.length);
        const rFact = Numbers.factorial(combinationSize);
        const nrDiff = elements.length - combinationSize;
        const nrDiffFact = Numbers.factorial(nrDiff);
        this.numCombinations = nFact / (rFact * nrDiffFact);
    }
    /**
     * @returns {elements[]} the next sequence
     */
    next() {
        // Check if there are combinations left
        if (!this.hasNext()) {
            throw `Combinations.next: Reached maximum number of combinations (${this.numCombinations})`;
        }
        // Get new values, until one with length of combinationSize appear
        let sequence = [];
        while (sequence.length !== this.combinationSize) {
            sequence = super.next();
        }
        // Found sequence, create combination
        const combination = [];
        for (const index of sequence) {
            combination.push(this.elements[index]);
        }
        this.currentCombinations++;
        return combination;
    }
    /**
     * @returns True if there are stil combinations to generate
     */
    hasNext() {
        return this.currentCombinations < this.numCombinations;
    }
}
//# sourceMappingURL=Combinations.js.map