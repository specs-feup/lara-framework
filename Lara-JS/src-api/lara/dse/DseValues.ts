/**
 * An iterator for a set of values to be used during Design-Space Exploration.
 *
 */
export default abstract class DseValues {
  /**
   * @returns the type of this DseValues.
   */
  abstract getType(): string;

  /**
   * @returns the next element.
   */
  abstract next(): any; // Because who designed this class had problems...

  /**
   * @returns true if it has another element to return.
   */
  abstract hasNext(): boolean;

  /**
   * Resets the iterator.
   */
  abstract reset(): void;

  abstract getNumElements(): number;

  /**
   * @returns The number of values returned by a call to next(). A value of one means one value, a value greater than one means an array with that amount of values.
   */
  abstract getNumValuesPerElement(): number;
}
