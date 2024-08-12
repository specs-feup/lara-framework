import JavaInterop from "./JavaInterop.js";
import JavaTypes, { JavaClasses } from "./util/JavaTypes.js";

/**
 *  Utility methods related to Collections.
 *
 */
export default class Collections {
  /**
   * @param values - Values to sort in-place. Must be of type \{Object[]|java.util.List\}
   *
   * @returns The sorted collection
   */
  static sort<T>(values: T[]): T[];
  static sort<T>(values: JavaClasses.List<T>): JavaClasses.List<T>;
  static sort<T>(values: T[] | JavaClasses.List<T>): T[] | JavaClasses.List<T> {
    // If array
    if (values instanceof Array) {
      values.sort();
      return values;
    }

    // If Java List
    if (JavaInterop.isList(values)) {
      JavaTypes.Collections.sort(values);
      return values;
    }

    throw "Expected either an array or a Java List: " + values;
  }

  /**
   * https://stackoverflow.com/questions/29151435/javascript-place-elements-that-dont-match-filter-predicate-into-seperate-array
   * Returns an array with two arrays at index 0 and 1. The array at index 0 is
   * all the items in `arr` that passed the `predicate` truth test by returning
   * a truthy value. The array at index 1 is all the items in `arr` that failed
   * the `predicate` truth test by returning a falsy value.
   *
   * @param arr - The array to partition
   * @param predicate - The predicate function to test each element
   * @returns The partitioned array
   */
  static partition<T>(
    arr: T[],
    predicate: (el: T, index: number, arr: T[]) => boolean
  ): [T[], T[]] {
    return arr.reduce(
      // this callback will be called for each element of arr
      function (partitionsAccumulator: [T[], T[]], arrElement, i, arr) {
        if (predicate(arrElement, i, arr)) {
          // predicate passed push to left array
          partitionsAccumulator[0].push(arrElement);
        } else {
          // predicate failed push to right array
          partitionsAccumulator[1].push(arrElement);
        }

        // whatever is returned from reduce will become the new value of the
        // first parameter of the reduce callback in this case
        // partitionsAccumulator variable if there are no more elements
        // this return value will be the return value of the full reduce
        // function.
        return partitionsAccumulator;
      },
      // the initial value of partitionsAccumulator in the callback function above
      // if the arr is empty this will be the return value of the reduce
      [[], []]
    );
  }

  /**
   * Prints tabular data using 3 arrays,
   * first for headers ie. ["HeaderA", "HeaderB", ...],
   * second for the row data ie. [row1Obj, row2Obj, ...] where row1Obj.length == headers.length == spacing.length
   * third for spacing ie. [10, 100, ...]
   *
   * @param headers -
   * @param rowData -
   * @param spacing -
   */
  static printTable(headers: string[], rowData: string[][], spacing: number[]) {
    const headerStr = headers.map((h, i) => h.padEnd(spacing[i])).join("");
    console.log(headerStr);
    rowData.forEach((row) => {
      const rowStr = row.map((d, i) => d.padEnd(spacing[i])).join("");
      console.log(rowStr);
    });
  }
}
