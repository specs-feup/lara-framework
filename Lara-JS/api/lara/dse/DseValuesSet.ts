import { arrayFromArgs } from "../core/LaraCore.js";
import DseValues from "./DseValues.js";

type T = any;

/**
 * Iterates over the values of a set of DseValues.
 *
 * @param dseValues - The DseValues that will form the set.
 */
export default class DseValuesSet extends DseValues {
  dseValuesArray: DseValues[];
  numElements: number;

  constructor(...dseValues: DseValues[]) {
    super();

    if (dseValues.length === 0) {
      throw "DseValuesSet: needs at least one DseValues as argument";
    }

    this.dseValuesArray = arrayFromArgs(dseValues) as DseValues[];
    this.numElements = this.dseValuesArray[0].getNumElements();

    for (let i = 0; i < this.dseValuesArray.length; i++) {
      const dseValues = this.dseValuesArray[i];

      if (this.numElements !== dseValues.getNumElements()) {
        throw (
          "Argument " +
          i +
          " has " +
          dseValues.getNumElements() +
          " elements but previous arguments have " +
          this.numElements +
          " elements"
        );
      }
    }
  }

  getType(): string {
    return "DseValuesSet";
  }

  /**
   * @returns the next element.
   */
  next(): T[] {
    const values: T[] = [];

    for (const dseValues of this.dseValuesArray) {
      values.push(dseValues.next());
    }

    return values;
  }

  /**
   * @returns true if it has another element to return.
   */
  hasNext(): boolean {
    return this.dseValuesArray[0].hasNext();
  }

  /**
   * Resets the iterator.
   */
  reset(): void {
    for (const dseValues of this.dseValuesArray) {
      dseValues.reset();
    }
  }

  getNumElements(): number {
    return this.numElements;
  }

  /**
   * @returns The number of values returned by a call to next(). A value of one means one value, a value greater than one means an array with that amount of values.
   */
  getNumValuesPerElement(): number {
    return this.dseValuesArray.length;
  }
}
