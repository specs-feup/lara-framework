/**
 * Creates ids for arbitrary tuples.
 */
export default class TupleId {
    currentId = 0;
    tupleChain = this.newTuple();
    /**
     * @returns An unique id associated with the given tuple
     */
    getId(...tuple) {
        // Get an array from the arguments
        let currentTuple = this.tupleChain;
        // Iterate over all tuple elements except the last one
        for (const element of tuple) {
            currentTuple = this.nextTuple(currentTuple, element);
        }
        // Get id from last tuples
        if (!this.isTuple(currentTuple)) {
            this.setTupleId(currentTuple, this.nextId());
        }
        return this.getTupleId(currentTuple);
    }
    /**
     * @returns An array where each element is an array which contains a tuple and the corresponding id
     */
    getTuples() {
        const tuples = {};
        this.getTuplesRecursive(this.tupleChain, [], tuples);
        return tuples;
    }
    getTuplesRecursive(currentTuples, prefix, tuplesMap) {
        // If currentTuple has an id, add it
        const currentId = this.getTupleId(currentTuples);
        if (this.isTuple(currentTuples)) {
            tuplesMap[currentId] = prefix;
        }
        // Iterate over all the indexed tuples
        for (const key in currentTuples) {
            if (key == "!tuple_id") {
                continue;
            }
            if (currentTuples["!tuple_id"] === undefined) {
                continue;
            }
            // Call recursively, building the prefix
            this.getTuplesRecursive(currentTuples[key], prefix.concat(key), tuplesMap);
        }
    }
    isTuple(tuple) {
        const id = this.getTupleId(tuple);
        return id !== undefined && id !== -1;
    }
    /**
     * Creates a new tuples object.
     */
    newTuple() {
        const newTuples = {
            "!tuple_id": -1,
        };
        return newTuples;
    }
    getTupleId(tuple) {
        return tuple["!tuple_id"];
    }
    setTupleId(tuple, id) {
        tuple["!tuple_id"] = id;
    }
    /**
     * @param tuples - Should always be a valid tuples object, created with _newTuples()
     * @param element - The key for the next tuples object
     * @returns the next tuples object, creating a new tuples if necessary.
     */
    nextTuple(tuple, element) {
        // If undefined, or not a tuples object, create a new one and add it
        if (tuple[element] === undefined ||
            tuple[element]["!tuple_id"] === undefined) {
            tuple[element] = this.newTuple();
        }
        return tuple[element];
    }
    /**
     * @returns the next id
     */
    nextId() {
        const id = this.currentId;
        this.currentId++;
        return id;
    }
}
//# sourceMappingURL=TupleId.js.map