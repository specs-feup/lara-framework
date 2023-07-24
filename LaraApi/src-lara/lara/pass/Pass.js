laraImport("weaver.Query");
laraImport("lara.util.AbstractClassError");
laraImport("lara.pass.results.PassResult");
laraImport("lara.pass.PassTransformationError");

/**
 * Represents a Lara transformation pass.
 *
 * Need to implement:
 *  - _apply_impl($jp)
 */
class Pass {

  constructor() {
    if (this.constructor === Pass) {
      throw new AbstractClassError({
        kind: "constructor",
        baseClass: Pass,
      });
    }
  }

  /**
   * @return {string} Name of the pass
   */
  get name() {
    if (this.constructor === Pass) {
      throw new AbstractClassError({
        kind: "constructor",
        baseClass: Pass,
      });
    }
  }

  /**
   * Applies this pass starting at the given join point. If no join point is given, uses the root join point
   *
   * @param {JoinPoint} $jp Joint point on which the pass will be applied
   * @return {PassResult} Results of applying this pass to the given joint point
   */
  apply($jp) {
    let $actualJp = $jp ?? Query.root();
    debug(
      () =>
        `Applying pass '${this.name}' to ${$actualJp.joinPointType} (${$actualJp.location})`
    );

    return this._apply_impl($actualJp);
  }

  /**
   * Apply tranformation to
   * @abstract Contains default implementation only if matchJoinPoint and transformJoinpoint are implemented
   * 
   * @param {JoinPoint} $jp Joint point on which the pass will be applied
   * @return {PassResult} Results of applying this pass to the given joint point
   */
  _apply_impl($jp) {
    throw new AbstractClassError({
      kind: "abstractMethod",
      baseClass: Pass,
      derivedClass: this.constructor,
      method: this._apply_impl,
    });
  }

}
