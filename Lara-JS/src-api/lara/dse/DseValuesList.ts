import { arrayFromArgs } from "../core/LaraCore.js";
import DseValues from "./DseValues.js";

type T = any;

/**
 * Iterates over a list of values.
 */
export default class DseValuesList extends DseValues {
  currentIndex = 0;
  values: T[];

  constructor(...values: T[]) {
    super();

    if (values.length === 0) {
      throw "DseValuesList: needs at least one value as argument";
    }

    this.values = arrayFromArgs(values) as T[];
  }

  getType(): string {
    return "DseValuesList";
  }

  /**
   * @returns the next element.
   */
  next(): T {
    const value = this.values[this.currentIndex];
    this.currentIndex++;

    return value;
  }

  /**
   * @returns true if it has another element to return.
   */
  hasNext(): boolean {
    return this.currentIndex < this.values.length;
  }

  /**
   * Resets the iterator.
   */
  reset(): void {
    this.currentIndex = 0;
  }

  getNumElements(): number {
    return this.values.length;
  }

  /**
   * @returns The number of values returned by a call to next(). A value of one means one value, a value greater than one means an array with that amount of values.
   */
  getNumValuesPerElement(): number {
    return 1;
  }
}
