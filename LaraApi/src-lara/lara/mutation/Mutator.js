/**
 * Base class that represents a code mutator.
 *
 * This should not be instantiated directly, instead it should be extended.
 *
 * @param name - the name of the mutator
 *
 */
export default class Mutator {
    name;
    /**
     * True if the current code is mutated, false otherwise
     */
    isMutated = false;
    /**
     * If true, before each call to .mutate() will check if the code is already mutated,
     * and call restore before the mutation is applied
     */
    automaticRestore = true;
    constructor(name = "<unnamed mutator>") {
        this.name = name;
    }
    /**
     * @returns The name of this mutator
     */
    getName() {
        return this.name;
    }
    /**
     * Enables/disables automatic restore. Is enabled by default.
     *
     * If enabled, before each call to .mutate() will check if the code is already mutated, and call restore before the mutation is applied.
     *
     * @param value - true to enable, false to disable
     */
    setAutomaticRestore(value = true) {
        this.automaticRestore = value;
    }
    /**
     * Introduces a single mutation to the code.
     * If the code has been mutated already, restores the code before mutating again.
     * If there are no mutations left, does nothing.
     */
    mutate() {
        // If no mutations left, do nothing
        if (!this.hasMutations()) {
            return;
        }
        // If code is currently mutated, call restore first
        // Otherwise, do nothing
        if (this.isMutated) {
            if (this.automaticRestore) {
                this.restore();
            }
            else {
                console.log("Calling .mutate() without .restore()");
                return;
            }
        }
        // Now can do the mutation
        this.isMutated = true;
        this.mutatePrivate();
    }
    /**
     * If the code has been mutated, restores the code to its original state. If not, does nothing.
     */
    restore() {
        // If not mutated, return
        if (!this.isMutated) {
            return;
        }
        this.isMutated = false;
        this.restorePrivate();
    }
    /**
     * @deprecated use getName() instead
     * @returns The name of this Mutator
     */
    getType() {
        return this.getName();
    }
}
//# sourceMappingURL=Mutator.js.map