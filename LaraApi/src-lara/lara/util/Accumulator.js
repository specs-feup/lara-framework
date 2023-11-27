/**
 * Counts occurrences of tuples.
 */
export default class Accumulator {
    value = 0;
    accs = {};
    seenKeys = new Set();
    /**
     * Adds the tuple to the accumulator. Each time a tuple is added, the corresponding value increments by 1.
     *
     * Alternatively, can also receive an array with the chain of values
     *
     * @returns the previous count of the added element
     */
    add(...args) {
        let currentAcc = this;
        const argsParsed = this.parseArguments(args);
        // Travel chain of values
        for (const chainElement of argsParsed) {
            let nextAcc = currentAcc.accs[chainElement];
            // If no accumulator, create
            if (nextAcc === undefined) {
                nextAcc = new Accumulator();
                currentAcc.accs[chainElement] = nextAcc;
                currentAcc.seenKeys.add(chainElement);
            }
            // Update acc
            currentAcc = nextAcc;
        }
        // Store previous value
        const previousValue = currentAcc.value;
        // Increment acc value
        currentAcc.value++;
        return previousValue;
    }
    /**
     * Adds the value associated to the given tuple. If no value is defined for the given tuple, returns 0.
     * <p>
     * Alternatively, can also receive an array with the chain of values
     */
    get(...args) {
        const chainArray = this.parseArguments(arguments);
        let currentAcc = this;
        // Travel chain of values
        for (const chainElement of chainArray) {
            const nextAcc = currentAcc.accs[chainElement];
            // If no accumulator, return 0
            if (nextAcc === undefined) {
                return 0;
            }
            // Update acc
            currentAcc = nextAcc;
        }
        // Return acc value
        return currentAcc.value;
    }
    copy(...args) {
        let copy = new Accumulator();
        for (const key of this.keys()) {
            const value = this.get(key);
            // TODO: Not efficient, should have a method to internally set a value   ---OLD---
            for (let i = 0; i < value; i++) {
                copy.add(key);
            }
        }
        return copy;
    }
    /**
     * Returns an array of arrays with keys that have a value set.
     */
    keys() {
        const chains = [];
        const currentChain = [];
        this.keysPrivate(currentChain, chains);
        return chains;
    }
    keysPrivate(currentChain, chains) {
        // If this accumulator has a value, add current chain
        if (this.value > 0) {
            chains.push(currentChain);
        }
        for (const key of this.seenKeys) {
            const updatedChain = currentChain.concat(key);
            const nextAcc = this.accs[key];
            if (nextAcc === undefined) {
                continue;
            }
            nextAcc.keysPrivate(updatedChain, chains);
        }
    }
    /**
     * Receives an array with the arguments of the previous function.
     */
    parseArguments(...args) {
        if (args.length !== 1) {
            throw "Accumulator._parseArguments: Expected arguments to have length 1";
        }
        let functionArguments = args[0];
        // If one argument and array, return it
        if (functionArguments.length === 1 &&
            functionArguments[0].constructor === Array) {
            // Retrive the array in the previous arguments
            return functionArguments[0];
        }
        // Transform arguments into array
        return Array.from(functionArguments);
    }
}
//# sourceMappingURL=Accumulator.js.map