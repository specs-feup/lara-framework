/**
 * The result of a Lara transformation pass
 *
 */
class PassResult {
    /**
     * Name of the pass that generated this result
     *
     */
    _name;
    /**
     * Joinpoint where this pass was applied
     *
     */
    _$jp;
    /**
     * True if the pass was applied to the given node, false if the node was ignored or there were no changes
     *
     */
    _appliedPass;
    /**
     * True if the pass inserted literal code
     *
     */
    _insertedLiteralCode;
    /**
     * @param pass - Pass which generated this result
     * @param $jp - Join point related to this pass result
     * @param params - Properties of a defined PassResult
     */
    constructor(pass, $jp, params = {
        appliedPass: true,
        insertedLiteralCode: false,
    }) {
        this._name = pass.name;
        this._$jp = $jp;
        this._appliedPass = params.appliedPass;
        this._insertedLiteralCode = params.insertedLiteralCode;
    }
    /**
     * @returns Name of the pass that generated this result
     */
    get name() {
        return this._name;
    }
    /**
     * @returns Joinpoint where this pass was applied
     */
    get jp() {
        return this._$jp;
    }
    /**
     * @returns True if the pass was applied to the given node, false if the node was ignored or there were no changes
     */
    get appliedPass() {
        return this._appliedPass;
    }
    /**
     * @returns True if the pass inserted literal code
     */
    get insertedLiteralCode() {
        return this._insertedLiteralCode;
    }
    toString() {
        return (`PassResult { name: ${this.name}; ` +
            `appliedPass: ${this.appliedPass}; ` +
            `insertedLiteralCode: ${this.insertedLiteralCode} }`);
    }
}
export default PassResult;
//# sourceMappingURL=PassResult.js.map