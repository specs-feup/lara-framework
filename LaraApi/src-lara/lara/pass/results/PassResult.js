/**
 * The result of a Lara transformation pass
 *
 */
class PassResult {
  /**
   * Name of the pass that generated this result
   * @type {string}
   */
  _name;
  /**
   * Joinpoint where this pass was applied
   * @type {JoinPoint}
   */
  _$jp;
  /**
   * True if the pass was applied to the given node, false if the node was ignored or there were no changes
   * @type {boolean}
   */
  _appliedPass;
  /**
   * True if the pass inserted literal code
   * @type {boolean}
   */
  _insertedLiteralCode;


  /**
   *
   * @typedef PassResultParams
   * @type {object}
   * @property {boolean} [appliedPass=true] True if the pass was applied to the given node, false if the node was ignored or there were no changes
   * @property {boolean} [insertedLiteralCode=false] True if the pass inserted literal code
   */

  /**
   * @param {string} name Name of the pass that generated this result
   * @param {PassResultParams} [params] - Properties of a defined PassResult
   */
  constructor(name, $jp, {appliedPass = true, insertedLiteralCode = false} = {}) {
    this._name = name;
    this._$jp = $jp;
    this._appliedPass = appliedPass;
    this._insertedLiteralCode = insertedLiteralCode;
  }

  /**
   * @returns {string} Name of the pass that generated this result
   */
  get name() {
    return this._name;
  }
  
  /**
   * @returns {JoinPoint} Joinpoint where this pass was applied
   */
  get jp() {
    return this._$jp;
  }

  /**
   * @returns {boolean} True if the pass was applied to the given node, false if the node was ignored or there were no changes
   */
  get appliedPass() {
    return this._appliedPass;
  }

  /**
   * @returns {boolean} True if the pass inserted literal code
   */
  get insertedLiteralCode() {
    return this._insertedLiteralCode;
  }

}
