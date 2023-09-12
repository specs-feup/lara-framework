type LaraTupleKey = string | number | symbol;
export interface LaraTupleChain
  extends Record<LaraTupleKey, LaraTupleChain | number> {
  "!tuple_id": number;
}

/**
 * Creates ids for arbitrary tuples.
 */
export default class TupleId {
  private currentId: number = 0;
  private tupleChain: LaraTupleChain = this.newTuple();

  /**
   * @returns An unique id associated with the given tuple
   */
  getId(...tuple: LaraTupleKey[]): number {
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
    const tuples: Record<number, LaraTupleKey[]> = {};

    this.getTuplesRecursive(this.tupleChain, [], tuples);

    return tuples;
  }

  private getTuplesRecursive(
    currentTuples: LaraTupleChain,
    prefix: LaraTupleKey[],
    tuplesMap: Record<number, LaraTupleKey[]>
  ) {
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
      this.getTuplesRecursive(
        currentTuples[key] as LaraTupleChain,
        prefix.concat(key),
        tuplesMap
      );
    }
  }

  private isTuple(tuple: LaraTupleChain): boolean {
    const id = this.getTupleId(tuple);
    return id !== undefined && id !== -1;
  }

  /**
   * Creates a new tuples object.
   */
  private newTuple() {
    const newTuples: LaraTupleChain = {
      "!tuple_id": -1,
    };

    return newTuples;
  }

  private getTupleId(tuple: LaraTupleChain): number {
    return tuple["!tuple_id"];
  }

  private setTupleId(tuple: LaraTupleChain, id: number) {
    tuple["!tuple_id"] = id;
  }

  /**
   * @param tuples - Should always be a valid tuples object, created with _newTuples()
   * @param element - The key for the next tuples object
   * @returns the next tuples object, creating a new tuples if necessary.
   */
  private nextTuple(
    tuple: LaraTupleChain,
    element: Exclude<LaraTupleKey, "!tuple_id">
  ): LaraTupleChain {
    // If undefined, or not a tuples object, create a new one and add it
    if (
      (tuple[element] as LaraTupleChain | undefined) === undefined ||
      (tuple[element] as any)["!tuple_id"] === undefined
    ) {
      tuple[element] = this.newTuple();
    }

    return tuple[element] as LaraTupleChain;
  }

  /**
   * @returns the next id
   */
  private nextId(): number {
    const id = this.currentId;
    this.currentId++;
    return id;
  }
}
