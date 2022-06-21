class PassTransformationResult {
  /**
   * Pass this result came from
   * @type {Pass}
   */
  #pass;

  /**
   * Joinpoint this transformation was applied to
   * @type {$joinpoint}
   */
  #joinpoint;

  /**
   * Whether literal code was inserted
   * @type {boolean}
   */
  #insertedLiteralCode;

  constructor({ pass, $joinpoint, insertedLiteralCode }) {
    this.#pass = pass;
    this.#joinpoint = $joinpoint;
    this.#insertedLiteralCode = insertedLiteralCode;
  }

  get pass() {
    return this.#pass;
  }

  get $joinpoint() {
    return this.#joinpoint;
  }

  get insertedLiteralCode() {
    return this.#insertedLiteralCode;
  }

  toString() {
    return `PassTransformationResult { pass: ${this.pass.name}, $joinpoint: ${this.$joinpoint}, insertedLiteralCode: ${this.insertedLiteralCode} }`;
  }
}
