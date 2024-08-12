import JavaTypes from "./JavaTypes.js";

/**
 * Can generate random numbers from a user-defined seed.
 */
export default class Random {
  static readonly RANDOM_CLASS = "java.util.Random";

  private javaRandom;

  constructor(seed?: number) {
    const RandomClass = JavaTypes.getType(Random.RANDOM_CLASS);
    this.javaRandom =
      seed !== undefined ? new RandomClass(seed) : new RandomClass();
  }

  /**
   * @returns The next pseudorandom, a uniformly distributed value between 0.0 (inclusive) and 1.0 (exclusive) from this random number generator's sequence.
   */
  next(): number {
    return this.javaRandom.nextDouble() as number;
  }
}
