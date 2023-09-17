/**
 * Base class that represents a code mutation.
 *
 * This should not be instantiated directly, instead it should be extended.
 *
 * @param name - the name of the mutation
 *
 */
export default class Mutation {
    name;
    constructor(name = "<unnamed mutation>") {
        this.name = name;
    }
    /**
     * @returns The name of this mutation
     */
    getName() {
        return this.name;
    }
    /**
     * Generator function for the mutations.
     *
     * @param $jp - The point in the code to mutate.
     *
     * @returns An iterator that results the results of each mutation on each iteration.
     */
    *mutate($jp) {
        const mutants = this.getMutants($jp);
        for (const $mutant of mutants) {
            yield $mutant;
        }
    }
}
//# sourceMappingURL=Mutation.js.map