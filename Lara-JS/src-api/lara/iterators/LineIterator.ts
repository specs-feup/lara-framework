import JavaTypes, { JavaClasses } from "../util/JavaTypes.js";
import LaraIterator from "./LaraIterator.js";

/**
 * Iterates over a sequence of lines.
 *
 * @param contents - The contents that will be iterated as lines.
 */
export default class LineIterator extends LaraIterator<string> {
  private contents: string | JavaClasses.File;
  private lineStream!: JavaClasses.LineStream;

  constructor(contents: string | JavaClasses.File) {
    super();

    this.contents = contents;
    this.reset();
  }

  getType() {
    return "LineIterator";
  }

  /**
   * @returns the next element.
   */
  next(): string | undefined {
    if (!this.hasNext()) {
      return undefined;
    }

    return this.lineStream.nextLine();
  }

  /**
   * @returns True if it has another element to return.
   */
  hasNext() {
    return this.lineStream.hasNextLine();
  }

  /**
   * Resets the iterator.
   */
  reset() {
    if (this.lineStream !== undefined) {
      this.close();
    }

    this.lineStream = JavaTypes.LineStream.newInstance(this.contents);
  }

  /**
   * @returns The total number of elements of the iterator, or undefined if it is not possible to calculate.
   */
  getNumElements(): number | undefined {
    return undefined;
  }

  /**
   * @returns The number of values returned by a call to next(). A value of one means one value, a value greater than one means an array with that amount of values.
   */
  getNumValuesPerElement() {
    return 1;
  }

  /**
   * Closes the iterator. For instane, if the iterator is backed-up by a file, it needs to be closed before the file can be deleted.
   */
  close() {
    this.lineStream.close();
  }
}
