laraImport("weaver.Query");
laraImport("lara.pass.PassResult");
laraImport("lara.util.AbstractClassError");
laraImport("lara.pass.AggregatePassResult");
laraImport("lara.pass.PassTransformationError");

/**
 * Represents a Lara transformation pass.
 *
 * Need to implement:
 *  - _apply_impl($jp)
 */
class Pass {
  constructor(_name) {
    if (this.constructor === Pass) {
      throw new AbstractClassError({
        kind: "constructor",
        baseClass: Pass,
      });
    }
  }

  get name() {
    return this.constructor.name;
  }

  /**
   * @deprecated Automatically infered
   */
  set name(_name) {}

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
   * Apply tranformation to
   * @abstract Contains default implementation only if matchJoinPoint and transformJoinpoint are implemented.
   */
  _apply_impl($jp) {
    if (
      this.matchJoinpoint === Pass.prototype.matchJoinpoint ||
      this.transformJoinpoint === Pass.prototype.transformJoinpoint
    ) {
      throw new AbstractClassError({
        kind: "abstractMethod",
        baseClass: Pass,
        derivedClass: this.constructor,
        method: this._apply_impl,
      });
    }

    const matchingJps = [$jp, ...$jp.descendants].filter(($jp) =>
      this.matchJoinpoint($jp)
    );

    let insertedLiteralCode = false;
    let casesApplied = 0;
    let transformationErrors = [];

    for (const $jp of matchingJps) {
      try {
        const result = this.transformJoinpoint($jp);
        casesApplied++;
        insertedLiteralCode = insertedLiteralCode || result.insertedLiteralCode;
      } catch (e) {
        if (e instanceof PassTransformationError) {
          transformationErrors.push(e);
        } else throw e;
      }
    }

    return AggregatePassResult({
      pass: this.constructor,
      casesFound: matchingJps.length,
      casesApplied,
      transformationErrors,
      insertedLiteralCode,
    });
  }

  _new_default_result() {
    return new PassResult(this.name);
  }

  /**
   * Predicate that informs the pass whether a certain joinpoint should be transformed.
   * @abstract
   * @param {joinpoint} $jp Join point to match
   * @returns {boolean}
   */
  matchJoinpoint($jp) {
    throw new AbstractClassError({
      kind: "abstractMethod",
      baseClass: Pass,
      derivedClass: this.constructor,
      method: this.matchJoinpoint,
    });
  }

  /**
   * Transformation to be applied to matching joinpoints
   * @abstract
   * @param {joinpoint} $jp Join point to transform
   * @throws {PassTransformationError} If the transformation fails
   * @return {PassTransformationResult} The result of the transformation
   */
  transformJoinpoint($jp) {
    throw new AbstractClassError({
      kind: "abstractMethod",
      baseClass: Pass,
      derivedClass: this.constructor,
      method: this.transformJoinpoint,
    });
  }
}
