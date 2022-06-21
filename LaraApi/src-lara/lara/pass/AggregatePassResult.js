class AggregatePassResult {
  /**
   * Pass this result came from
   * @type {Pass}
   */
  #pass;

  /**
   * Number of joinpoints that matched the pass predicate.
   * @type {number}
   */
  #casesFound;

  /**
   * Number of joinpoints where the pass transformation was applied successfully.
   * @type {number}
   */
  #casesApplied;

  /**
   * Errors thrown where the pass transformation failed.
   * @type {readonly PassTransformationError[]}
   */
  #transformationErrors;

  /**
   * Whether literal code was inserted during the pass. If so, a rebuild of the AST might be required after the pass.
   * @type {boolean}
   */
  #insertedLiteralCode;

  constructor({
    pass,
    casesFound,
    casesApplied,
    transformationErrors,
    insertedLiteralCode,
  }) {
    this.#pass = pass;
    this.#casesFound = casesFound;
    this.#casesApplied = casesApplied;
    this.#transformationErrors = Object.freeze([...transformationErrors]);
    this.#insertedLiteralCode = insertedLiteralCode;
  }

  get pass() {
    return this.#pass;
  }

  get casesFound() {
    return this.#casesFound;
  }

  get casesApplied() {
    return this.#casesApplied;
  }

  get transformationErrors() {
    return this.#transformationErrors;
  }

  get insertedLiteralCode() {
    return this.#insertedLiteralCode;
  }
}
