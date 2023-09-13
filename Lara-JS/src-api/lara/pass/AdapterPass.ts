laraImport("weaver.TraversalType");
laraImport("lara.pass.results.AggregatePassResult");
laraImport("lara.pass.PassTransformationError");

/**
 *
 * @typedef AdapterPassDefinition
 * @type {object}
 * @property {string} name Name of the pass
 * @property {TraversalType} [traversalType=TraversalType.PREORDER] Order in which the join point's descendants should be visited
 * @property {Function} matchJp Predicate that informs the pass whether a certain joinpoint should be transformed 
 * @property {Function} transformJp Transformation to be applied to matching joinpoints
 */

/**
 * Helper class to wrap existing code into a Lara transformation pass.
 */
class AdapterPass extends SimplePass {

  #name;
  #traversalType;
  #matchFn;
  #transformFn;

  /**
   * @param {boolean} includeDescendants Apply pass to the join point's descendents  
   * @param {AdapterPassDefinition} [definition] Definition for the Pass
   */
  constructor(includeDescendants, {name, traversalType = TraversalType.PREORDER, matchJp, transformJp} = {}) {
    super(includeDescendants);

    if (name === undefined) {
      throw new Error("PassAdapterDefinition must include a name");
    }

    if (matchJp === undefined || transformJp === undefined) {
      throw new Error("PassAdapterDefinition must include both match and transform predicates");
    }

    this.#name = name;
    this.#matchFn = matchJp;
    this.#transformFn = transformJp;
    this.#traversalType = traversalType;
  }

  /**
   * 
   * @param {boolean} includeDescendants Apply pass to the join point's descendents
   */
  constructor(includeDescendants=true) {
    super(includeDescendants);
  }

  /**
   * @return {string} Name of the pass
   * @override
   */
  get name() {
    return this.#name;
  }

  /**
   * Order in which the join point's descendants should be visited
   * @type {TraversalType}
   */
  get traversalType() {
    return this.#traversalType;
  }


  /**
   * Predicate that informs the pass whether a certain joinpoint should be transformed
   * @override
   * @param {JoinPoint} $jp Join point to match
   * @returns {boolean} Returns true if the joint point matches the predicate for this pass
   */
  matchJoinpoint($jp) {
    return this.#matchFn($jp);
  }

  /**
   * Transformation to be applied to matching joinpoints
   * @override
   * @param {JoinPoint} $jp Join point to transform
   * @throws {PassTransformationError} If the transformation fails
   * @return {PassTransformationResult} The result of the transformation
   */
  transformJoinpoint($jp) {
    return this.#transformFn($jp);
  }
}
