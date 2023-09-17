import { arrayFromArgs } from "../core/LaraCore.js";
import Mutator from "./Mutator.js";
/**
 * Iterative mutator, allows to perform one mutation at a time, and restore the code before each mutation.
 *
 */
export default class IterativeMutator extends Mutator {
    joinPoints = [];
    mutations;
    mutantIterator = undefined;
    currentOriginalPoint = undefined;
    currentMutatedPoint = undefined;
    hasFinished = false;
    constructor(...mutations) {
        super("IterativeMutator");
        this.mutations = arrayFromArgs(mutations);
        if (this.mutations.length === 0) {
            throw "IterativeMutator needs at least one mutation";
        }
    }
    /**
     * Introduces a single mutation to the code.
     * If there are no mutations left, does nothing.
     *
     * @returns True if a mutation occurred, false otherwise
     */
    mutateSource() {
        // If no mutations left, do nothing
        if (!this.hasMutations()) {
            return false;
        }
        // If no iterator, create it
        if (this.mutantIterator === undefined) {
            this.mutantIterator = this.generator();
        }
        // Get next element
        const element = this.mutantIterator.next();
        if (!element.done) {
            return true;
        }
        this.hasFinished = true;
        return false;
    }
    mutatePrivate() {
        this.mutateSource();
    }
    /**
     *  If the code has been mutated, restores the code to its original state. If not, does nothing.
     */
    restoreSource() {
        // If not mutated, return
        if (!this.isMutated) {
            return;
        }
        if (this.currentOriginalPoint === undefined) {
            throw "Original point is undefined";
        }
        if (this.currentMutatedPoint === undefined) {
            throw "Mutated point is undefined";
        }
        this.isMutated = false;
        this.currentMutatedPoint.insert("replace", this.currentOriginalPoint);
        // Unset mutated point
        this.currentMutatedPoint = undefined;
    }
    restorePrivate() {
        this.restoreSource();
    }
    /**
     * @returns The point in the code where the mutation is occurring, or undefined if there are not more mutations left.
     */
    getMutationPoint() {
        return this.currentOriginalPoint;
    }
    /**
     * @returns The point with currently mutated code, or undefined if the code is not currently mutated.
     */
    getMutatedPoint() {
        return this.currentMutatedPoint;
    }
    *generator() {
        if (this.isMutated) {
            throw "Code is currently mutated, cannot create a generator";
        }
        // Iterate over all join points
        for (const $jp of this.joinPoints) {
            this.currentOriginalPoint = $jp;
            // Iterate over all mutations
            for (const mutation of this.mutations) {
                // If current mutation does not mutate the point, skip
                if (!mutation.isMutationPoint($jp)) {
                    continue;
                }
                // Found a mutation point, iterate over all mutations
                for (const mutationResult of mutation.mutate($jp)) {
                    const $mutatedJp = mutationResult.getMutation();
                    // Mutate code and return
                    this.isMutated = true;
                    this.currentMutatedPoint = $mutatedJp;
                    $jp.insert("replace", $mutatedJp);
                    yield mutationResult;
                    // After resuming, restore
                    this.restoreSource();
                }
            }
        }
        // Mark as finished
        this.hasFinished = true;
        // Unset current original point
        this.currentOriginalPoint = undefined;
    }
    addJp($joinpoint) {
        this.joinPoints.push($joinpoint);
    }
    addJps(...jps) {
        jps = arrayFromArgs(jps);
        for (const $jp of jps) {
            this.addJp($jp);
        }
    }
    hasMutations() {
        return !this.hasFinished;
    }
    getCurrentMutation() {
        return this.currentMutatedPoint;
    }
}
//# sourceMappingURL=IterativeMutator.js.map