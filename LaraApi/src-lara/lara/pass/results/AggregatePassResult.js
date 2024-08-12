import PassResult from "./PassResult.js";
export default class AggregatePassResult extends PassResult {
    /**
     * Intermediate results collected from the pass
     *
     */
    _intermediateResults = [];
    /**
     * Errors thrown where the pass transformation failed
     *
     */
    _transformationErrors = [];
    _casesApplied = 0;
    /**
     * @param pass - Pass which generated this result
     * @param $jp - Join point related to this pass result
     */
    constructor(pass, $jp) {
        super(pass, $jp, { appliedPass: false, insertedLiteralCode: false });
    }
    /**
     * @returns True if the pass was applied successfully at least once
     */
    get appliedPass() {
        return this._casesApplied > 0;
    }
    /**
     * @returns Total number of cases where this pass applied, independently of its success
     */
    get casesFound() {
        return this.casesFailed + this._intermediateResults.length;
    }
    /**
     * @returns Number of cases where this pass was successfully applied
     */
    get casesApplied() {
        return this._casesApplied;
    }
    /**
     * @returns Number of cases that resulted in an error
     */
    get casesFailed() {
        return this._transformationErrors.length;
    }
    /**
     * @returns List of results registered during the pass
     */
    get results() {
        return this._intermediateResults;
    }
    /**
     * @returns List of errors registered during the pass
     */
    get errors() {
        return this._transformationErrors;
    }
    /**
     * Register a new error
     *
     */
    pushError(error) {
        this._transformationErrors.push(Object.freeze(error));
    }
    /**
     * Register a new partial result
     * @param result - PassResult from applying a predicate to a joinpoint
     */
    pushResult(result) {
        this._intermediateResults.push(Object.freeze(result));
        if (result.appliedPass) {
            this._appliedPass = true;
            this._casesApplied += 1;
        }
        this._insertedLiteralCode =
            this._insertedLiteralCode || result.insertedLiteralCode;
    }
    toString() {
        return (`PassResult { name: ${this.name}; ` +
            `appliedPass: ${this.appliedPass}; ` +
            `insertedLiteralCode: ${this.insertedLiteralCode}; ` +
            `casesFound: ${this.casesFound}; ` +
            `casesApplied: ${this.casesApplied}; ` +
            `casesFailed: ${this.casesFailed} }`);
    }
}
//# sourceMappingURL=AggregatePassResult.js.map