/**
 * Base class for iterators in Lara.
 *
 * @deprecated This class is not a true iterable object. Use the native JS implementations instead.
 * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Iterators_and_Generators
 */
export default abstract class LaraIterator<T> {
  /**
   * @returns The type of this iterator.
   */
  abstract getType(): string;

  /**
   * @returns The next element.
   */
  abstract next(): T | undefined;

  /**
   * @returns True if it has another element to return.
   */
  abstract hasNext(): boolean;

  /**
   * Resets the iterator.
   */
  abstract reset(): void;

  /**
   * @returns The total number of elements of the iterator, or undefined if it is not possible to calculate.
   */
  abstract getNumElements(): number | undefined;

  /**
   * @returns The number of values returned by a call to next(). A value of one means one value, a value greater than one means an array with that amount of values.
   */
  abstract getNumValuesPerElement(): number;

  jsIterator() {
    return new JsIterator<T>(this);
  }
}

/**
 * Iterator that wraps a LaraIterator and implements the JS iterator interface.
 *
 * @deprecated This class is not a true iterable object. Use the native JS implementations instead.
 * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Iterators_and_Generators
 */
export class JsIterator<T> {
  laraIterator: LaraIterator<T>;

  constructor(laraIterator: LaraIterator<T>) {
    this.laraIterator = laraIterator;
  }

  next() {
    if (this.laraIterator.hasNext()) {
      return { value: this.laraIterator.next(), done: false };
    }

    return { done: true };
  }
}
