laraImport("weaver.Query");
laraImport("lara.pass.PassResult");

/**
 * Represents a Lara transformation pass.
 *
 * Need to implement:
 *  - _apply_impl($jp)
 */
class Pass {
  #name;

  constructor(name) {
    this.#name = name;
  }

  get name() {
    return this.#name;
  }

  set name(name) {
    this.#name = name;
  }

  /**
   * Applies this pass starting at the given join point. If no join point is given, uses the root join point.
   *
   * @param {$jp} $jp - The point in the code where the pass will be applied.
   * @return {PassResult} - Object containing information about the results of applying this pass to the given node
   */
  apply($jp) {
    let $actualJp = $jp ?? Query.root();
    debug(
      () =>
        `Applying pass '${this.name}' to ${$actualJp.joinPointType} (${$actualJp.location})`
    );

    const result = this._apply_impl($actualJp);
    return result ?? this._new_default_result();
  }

  /**
   * @abstract
   */
  _apply_impl($jp) {
    throw new Error(
      `Method '_apply_impl' of pass ${this.name} not implemented`
    );
  }

  _new_default_result() {
    return new PassResult(this.name);
  }
}
