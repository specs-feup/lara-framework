export default class Numbers {
  /**
   * @param number - A number to convert to indexes.
   * @returns An array with indexes corresponding to the active bits of the given number
   */
  static toIndexesArray(number: number) {
    // Convert number to binary string
    const binaryString = number.toString(2);

    const indexesArray = [];
    const numBits = binaryString.length;
    for (let i = 0; i < numBits; i++) {
      if (binaryString[i] === "0") {
        continue;
      }

      indexesArray.push(numBits - i - 1);
    }

    return indexesArray.reverse();
  }

  /**
   * Taken from here: https://stackoverflow.com/questions/3959211/fast-factorial-function-in-javascript#3959275
   */
  static factorial(num: number): number {
    let rval = 1;
    for (let i = 2; i <= num; i++) {
      rval *= i;
    }

    return rval;
  }

  /**
   * @deprecated THIS IS NOT A MEAN!!! THIS CALCULATES AN AVERAGE!!!.
   */
  static mean(values: number[]): number {
    return Numbers.average(values);
  }

  static average(values: number[]): number {
    return values.reduce((acc, value) => acc + value, 0) / values.length;
  }

  /**
   * @returns Sum of the given values.
   */
  static sum(values: number[]): number {
    return values.reduce((acc, value) => acc + value, 0);
  }
}
