import Mutation from "./Mutation.js";
/**
 * Abstract mutation class that implements .getMutants(), but makes function Mutation.mutate($jp) abstract. Allows more efficient, generator-based Mutation implementations.
 */
export default class IterativeMutation extends Mutation {
    getMutants($jp) {
        const mutations = [];
        for (const mutation of this.mutate($jp)) {
            mutations.push(mutation);
        }
        return mutations;
    }
}
//# sourceMappingURL=IterativeMutation.js.map