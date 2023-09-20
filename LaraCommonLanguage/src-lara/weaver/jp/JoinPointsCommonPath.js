laraImport("weaver.jp.JoinPoint");
laraImport("weaver.jp.CommonJoinPointsBase");
laraImport("weaver.JoinPoints");
laraImport("weaver.Ast");
laraImport("lara.Check");
laraImport("weaver.Weaver");
laraImport("weaver.Selector")


/**
 *
 * @return {$jp[]} the children of the given node
 */

class LCLJoinPoints extends JoinPoints {
  static _all_children($jp) {
    return CommonJoinPoints.toJoinPoints(Ast.getChildren($jp.astNode));
  }

  /**
   *
   * @return {$jp} the parent of the given node
   */

  static _parent($jp) {
    var parent = Ast.getParent($jp.astNode);

    if (parent !== undefined) return CommonJoinPoints.toJoinPoint(parent);

    return undefined;
  }

  /**
   *
   * @return {$jp[]} the descendants of the given node
   */

  static _all_descendants($jp) {
    return CommonJoinPoints.toJoinPoints(Ast.getDescendants($jp.astNode));
  }

  /**
   *
   * @return {$jp[]} all the nodes that are inside the scope of a given node
   */

  static _all_scope_nodes($jp) {
    return [];
  }

  /**
   *
   * @return {Object} the current root node of the AST.
   */

  static root() {
    return CommonJoinPoints.toJoinPoint(Ast.root());
  }

  /**
   * Getter of the parent of a joinpoint
   *
   */

  static getParent($jp) {
    return this._parent($jp);
  }

  /**
   *  Getter of the children of a joinpoint
   *
   */

  static getChildren($jp) {
    return this._all_children($jp);
  }

  /**
   *  Getter of the descendants of a joinpoint
   *
   */
  static getDescendants($jp) {
    return this._all_descendants($jp);
  }
}

/**
 * Checks if the given value is a join point. If a type is given, checks if the join point is an instance of the given type. Otherwise, throws an exception.
 *
 * @param $jp
 * @param type
 * @param {boolean} [isOptional=false] - If true, passes check if value is undefined
 */
Check.isJoinPoint = function($jp, type, isOptional) {
  if (isOptional && $jp === undefined) {
    return;
  }

  if (!($jp instanceof JoinPoint)) {
    println("$jp:");
    printlnObject($jp);
    throw (
      "Expected variable $jp to be of type (common) JoinPoint, but it is of type '" +
      typeof $jp +
      "'"
    );
  }

  if (type !== undefined && !$jp.instanceOf(type)) {
    throw (
      "Expected join point to be an instance of type '" +
      type +
      "' but its type is '" +
      $jp.joinPointType +
      "'"
    );
  }
};


setUsingLaraCommonLanguage(true);
setSelectorJoinPointsClass(LCLJoinPoints);
clearJoinpointMappers();
//registerJoinpointMapper(classesMapping);
