export default class PassTransformationError extends Error {
    name = "PassTransformationError";
    /**
     * Joinpoint where the transformation was applied and failed.
     *
     */
    joinpoint;
    /**
     * Message describing the error that occurred.
     *
     */
    errorDescription;
    /**
     * Pass that was being applied when the error was emitted.
     *
     */
    compilationPass;
    constructor(pass, $joinpoint, description) {
        super(`${pass.name} @ ${$joinpoint.joinPointType}: ${description}`);
        this.errorDescription = description;
        this.joinpoint = $joinpoint;
        this.compilationPass = pass;
    }
    get description() {
        return this.errorDescription;
    }
    get $joinpoint() {
        return this.joinpoint;
    }
    get pass() {
        return this.compilationPass;
    }
}
//# sourceMappingURL=PassTransformationError.js.map