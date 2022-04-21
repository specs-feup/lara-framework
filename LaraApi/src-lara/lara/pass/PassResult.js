/**
 * The result of a Lara transformation pass.
 *
 */
class PassResult {
  /**
   * Name of the pass this result came from
   * @type {string}
   */
  #name;
  /**
   * True if the pass is not returning PassResult instances, false otherwise. Remaining properties might not be valid if this is true
   * @type {boolean}
   */
  #isUndefined;
  /**
   * True if the pass was applied to the given node, false if the node was ignored or there were no changes
   * @type {boolean}
   */
  #appliedPass;
  /**
   * True if the pass inserted literal code
   * @type {boolean}
   */
  #insertedLiteralCode;
  /**
   * Location where pass was applied, usually is the location of the given node
   * @type {string}
   */
  #location;

  /**
   *
   * @typedef PassResultParams
   * @type {object}
   * @property {boolean} appliedPass - True if the pass was applied to the given node, false if the node was ignored or there were no changes
   * @property {boolean} insertedLiteralCode - True if the pass inserted literal code
   * @property {string} location - Location where pass was applied, usually is the location of the given node
   */

  /**
   *
   * @param {string} name - Name of the pass this result came from
   * @param {PassResultParams} [params] - Properties of a defined PassResult
   */
  constructor(name, params) {
    this.#name = name;
    if (params) {
      const { appliedPass, insertedLiteralCode, location } = params;
      this.#isUndefined = false;
      this.#appliedPass = appliedPass;
      this.#insertedLiteralCode = insertedLiteralCode;
      this.#location = location;
    } else {
      this.#isUndefined = true;
    }
  }

  toString() {
    let print = `PassResult { name: ${this.name};`;

    if (this.isUndefined) {
      print += ` isUndefined: ${this.isUndefined}`;
    } else {
      print +=
        ` appliedPass: ${this.appliedPass};` +
        ` insertedLiteralCode: ${this.insertedLiteralCode};` +
        ` location: ${this.location}`;
    }

    print += "}";

    return print;
  }

  get name() {
    return this.#name;
  }

  get isUndefined() {
    return this.#isUndefined;
  }

  set isUndefined(newValue) {
    this.#isUndefined = newValue;
  }

  get appliedPass() {
    return this.#appliedPass;
  }

  set appliedPass(newValue) {
    this.#appliedPass = newValue;
  }

  get insertedLiteralCode() {
    return this.#insertedLiteralCode;
  }

  set insertedLiteralCode(newValue) {
    this.#insertedLiteralCode = newValue;
  }

  get location() {
    return this.#location;
  }

  set location(newValue) {
    this.#location = newValue;
  }
}
