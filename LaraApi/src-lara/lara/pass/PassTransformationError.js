class PassTransformationError extends Error {
  name = "PassTransformationError";

  /**
   * Joinpoint where the transformation was applied and failed.
   * @type {$joinpoint}
   */
  #joinpoint;

  /**
   * Message describing the error that occurred.
   * @type {string}
   */
  #description;

  /**
   * Pass that was being applied when the error was emitted.
   * @type {Pass}
   */
  #pass;

  constructor({ pass, $joinpoint, description }) {
    this.#description = description;
    this.#joinpoint = $joinpoint;
    this.#pass = pass;
    super(`${$joinpoint.location}: ${description}`);
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
