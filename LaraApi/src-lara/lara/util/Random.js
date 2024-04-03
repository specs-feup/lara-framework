import JavaTypes from "./JavaTypes.js";
/**
 * Can generate random numbers from a user-defined seed.
 */
export default class Random {
    static RANDOM_CLASS = "java.util.Random";
    javaRandom;
    constructor(seed) {
        const RandomClass = JavaTypes.getType(Random.RANDOM_CLASS);
        this.javaRandom =
            seed !== undefined ? new RandomClass(seed) : new RandomClass();
    }
    /**
     * @returns The next pseudorandom, a uniformly distributed value between 0.0 (inclusive) and 1.0 (exclusive) from this random number generator's sequence.
     */
    next() {
        return this.javaRandom.nextDouble();
    }
}
//# sourceMappingURL=Random.js.map