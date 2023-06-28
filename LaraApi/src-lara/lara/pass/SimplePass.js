laraImport("weaver.TraversalType");
laraImport("lara.util.AbstractClassError");
laraImport("lara.pass.results.AggregatePassResult");
laraImport("lara.pass.PassTransformationError");

/**
 * Represents a Lara transformation pass.
 *
 * Need to implement:
 *  - matchJoinpoint($jp)
 *  - transformJoinpoint($jp)
 */
class SimplePass extends Pass {

  /**
   * 
   * @param {boolean} includeDescendants Apply pass to the join point's descendents  
   */
  constructor(includeDescendants=true) {
    this.#includeDescendants = includeDescendants;
  }

  /**
   * Should the Pass be applied to the join point's descendants
   * @type {boolean}
   */
  #includeDescendants;

  /**
   * Order in which the join point's descendants should be visited
   * @type {TraversalType}
   */
  get traversalType() {
    return TraversalType.PREORDER;
  }

  /**
   * Selects the join points and its descendants, if needed, according to the traversalType
   * @param {JoinPoint} $jp The point in the code from which to select
   * @returns {JoinPoint[]} Array of join points selected
   */
  _selectedJps($jp) {
    if (this.#includeDescendants === false) {
      return [$jp];
    }

    switch (this.traversalType) {
      case TraversalType.PREORDER:
        return [$jp, ...$jp.descendants];
      case TraversalType.POSTORDER:
        return [$jp, ...$jp.descendantsPostorder];
      default:
        throw new Error("Traversal type not implemented: " + traversal);
    }
  }

  /**
   * Apply tranformation to
   * @abstract Contains default implementation only if matchJoinPoint and transformJoinpoint are implemented
   * 
   * @param {JoinPoint} $jp Joint point on which the pass will be applied
   * @return {AggregatePassResult} Results of applying this pass to the given joint point
   */
  _apply_impl($jp) {
    const matchingJps = this._selectedJps($jp).filter(($jp) => {
      this.matchJoinpoint($jp)
    });;

    const aggResult = new AggregatePassResult(this.name, $jp);
    for (const $jp of matchingJps) {
      try {
        const result = this.transformJoinpoint($jp);
        aggResult.pushResult(result);
      } catch (e) {
        if (e instanceof PassTransformationError) {
          transformationErrors.push(e);
        } else throw e;
      }
    }

    return aggResult;
  }

  /**
   * Predicate that informs the pass whether a certain joinpoint should be transformed
   * @abstract
   * @param {JoinPoint} $jp Join point to match
   * @returns {boolean} Returns true if the joint point matches the predicate for this pass
   */
  matchJoinpoint($jp) {
    throw new AbstractClassError({
      kind: "abstractMethod",
      baseClass: SimplePass,
      derivedClass: this.constructor,
      method: this.matchJoinpoint,
    });
  }

  /**
   * Transformation to be applied to matching joinpoints
   * @abstract
   * @param {JoinPoint} $jp Join point to transform
   * @throws {PassTransformationError} If the transformation fails
   * @return {PassTransformationResult} The result of the transformation
   */
  transformJoinpoint($jp) {
    throw new AbstractClassError({
      kind: "abstractMethod",
      baseClass: SimplePass,
      derivedClass: this.constructor,
      method: this.transformJoinpoint,
    });
  }
}
