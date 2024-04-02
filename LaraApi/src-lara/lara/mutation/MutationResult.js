/**
 * Contains the results of a single mutation.
 */
export default class MutationResult {
    $mutation;
    constructor($mutation) {
        this.$mutation = $mutation;
    }
    /**
     * @returns A copy of the original join point, where the mutation was applied.
     */
    getMutation() {
        return this.$mutation;
    }
}
//# sourceMappingURL=MutationResult.js.map