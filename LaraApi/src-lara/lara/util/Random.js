laraImport("lara.util.JavaTypes");

/**
 * Can generate random numbers from a user-defined seed.
 */
class Random {

    static RANDOM_CLASS = "java.util.Random";

    #javaRandom;

    /**
     * 
     * @param {number|String} seed 
     */
    constructor(seed) {
        const RandomClass = JavaTypes.getType(Random.RANDOM_CLASS);
        this.#javaRandom = seed !== undefined ? new RandomClass(this.#parseSeed(seed)) : new RandomClass(); 
    }

    #parseSeed(seed) {
        const seedNumber = Number(seed)
        if(seedNumber != seedNumber) {
            throw "Could not parse seed '"+seed+"', returned NaN"; 
        }

        return seedNumber;
    }

    /**
     * @return {number} the next pseudorandom, a uniformly distributed value between 0.0 (inclusive) and 1.0 (exclusive) from this random number generator's sequence. 
     */
    next() {
        return this.#javaRandom.nextDouble();        
    }
}