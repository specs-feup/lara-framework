import Numbers from "../Numbers.js";

/**
 * Generates sequential sequences of combinations, according to the given number of elements.
 */
export default class SequentialCombinations {
  numElements: number;
  currentValue = 1;
  lastValueUsed: number | undefined = undefined;
  maximumValue: number;

  constructor(numElements: number, upTo?: number) {
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
  next(): number[] {
    // Check if there are combinations left
    if (!this.hasNext()) {
      throw (
        "SequentialCombinations.next: Reached maximum number of combinations (" +
        this.maximumValue +
        ")"
      );
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
  hasNext(): boolean {
    return this.currentValue <= this.maximumValue;
  }
}
