export default class PassTransformationError extends Error {
    name = "PassTransformationError";
    /**
     * Joinpoint where the transformation was applied and failed.
     *
     */
    #joinpoint;
    /**
     * Message describing the error that occurred.
     *
     */
    #description;
    /**
     * Pass that was being applied when the error was emitted.
     *
     */
    #pass;
    constructor(pass, $joinpoint, description) {
        super(`${pass.name} @ ${$joinpoint.joinPointType}: ${description}`);
        this.#description = description;
        this.#joinpoint = $joinpoint;
        this.#pass = pass;
    }
    get description() {
        return this.#description;
    }
    get $joinpoint() {
        return this.#joinpoint;
    }
    get pass() {
        return this.#pass;
    }
}
//# sourceMappingURL=PassTransformationError.js.map