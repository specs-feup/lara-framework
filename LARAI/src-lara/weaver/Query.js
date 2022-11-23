laraImport("weaver.Selector");
laraImport("weaver.JoinPoints");

/**
 * Class for selection of join points. Provides an API similar to the keyword 'select'.
 *
 * Search functions of this class return weaver.Selector objects, please refer to that class for more details regarding available functions and search options.
 *
 * @class
 */
class Query {
  /**
   * Returns the root node of the current AST.
   */
  static root() {
    return new JoinPoints().root();
  }

  /**
   * The same as Query.searchFrom(), but uses the root node as $baseJp.
   *
   * @param {String} type - type of the join point to search.
   * @param {Object|String|Function|Regex} [filter = {}] - filter rules for the search. If the value is an object, each field of the object represents a rule that will be applied over the attribute that has the same name as the name of the field. If the value is not an object (e.g., String, Regex, Lambda), it is interpreted as a single rule that will be applied over the default attribute of the given type. E.g., if type is 'function', the value is a String 'foo' and the default attribute of function is 'name', this is equivalent as passing as value the object {'name':'foo'}. Rules can be a String (i.e., will match the value of the attribute against a string), a Regex (will match the value of the attribute against a regex) or a Function (i.e., function receives the value of the attribute and returns true if there is a match, or false otherwise).
   * @param {String} [traversal = TraversalType.PREORDER] - AST traversal type, according to weaver.TraversalType
   *
   * @return {weaver.Selector} the results of the search.
   */
  static search(type, filter, traversal) {
    return Query.searchFrom(undefined, type, filter, traversal);
    //return (new Selector()).search(type, filter);
  }

  /**
   * In-depth search of join points of the given type, starting from a base join point (exclusive).
   *
   * @param {$jp} $baseJp - starting join point for the search.
   * @param {String} type - type of the join point to search.
   * @param {Object|String|Function|Regex} [filter = {}] - filter rules for the search. If the value is an object, each field of the object represents a rule that will be applied over the attribute that has the same name as the name of the field. If the value is not an object (e.g., String, Regex, Lambda), it is interpreted as a single rule that will be applied over the default attribute of the given type. E.g., if type is 'function', the value is a String 'foo' and the default attribute of function is 'name', this is equivalent as passing as value the object {'name':'foo'}. Rules can be a String (i.e., will match the value of the attribute against a string), a Regex (will match the value of the attribute against a regex) or a Function (i.e., function receives the value of the attribute and returns true if there is a match, or false otherwise).
   * @param {String} [traversal = TraversalType.PREORDER] - AST traversal type, according to weaver.TraversalType
   *
   * @return {weaver.Selector} the results of the search.
   */
  static searchFrom($baseJp, type, filter, traversal) {
    // These rules will be used to create a lara.util.JpFilter instance, please refer to that class for details on what kinds of rules are supported.
    return new Selector($baseJp).search(type, filter, traversal);
  }

  /**
   * The same as Query.searchFrom(), but $baseJp is included in the search.
   *
   * @param {$jp} $baseJp - starting join point for the search.
   * @param {String} type - type of the join point to search.
   * @param {Object|String|Function|Regex} [filter = {}] - filter rules for the search.
   * @param {String} [traversal = TraversalType.PREORDER] - AST traversal type, according to weaver.TraversalType
   */
  static searchFromInclusive($baseJp, type, filter, traversal) {
    return new Selector($baseJp, true).search(type, filter, traversal);
  }

  static childrenFrom($baseJp, type, filter) {
    // These rules will be used to create a lara.util.JpFilter instance, please refer to that class for details on what kinds of rules are supported.
    return new Selector($baseJp).children(type, filter);
  }

  /**
   * If $baseJp has the concept of scope (e.g. if, loop), search the direct children of that scope.
   *
   * @param {*} $baseJp
   * @param {*} type
   * @param {*} filter
   * @returns
   */
  static scopeFrom($baseJp, type, filter) {
    // These rules will be used to create a lara.util.JpFilter instance, please refer to that class for details on what kinds of rules are supported.
    return new Selector($baseJp).scope(type, filter);
  }
}

/**
 * The same as Query.search(), but available as a global function.
 *
 * @param {String} type - type of the join point to search.
 * @param {Object|String|Function|Regex} [filter = {}] - filter rules for the search.
 * @param {String} [traversal = TraversalType.PREORDER] - AST traversal type, according to weaver.TraversalType
 */
function search(type, filter, traversal) {
  return Query.search(type, filter, traversal);
}

/**
 * The same as Query.searchFrom(), but available as a global function.
 *
 * @param {$jp} $baseJp - starting join point for the search.
 * @param {String} type - type of the join point to search.
 * @param {Object|String|Function|Regex} [filter = {}] - filter rules for the search.
 * @param {String} [traversal = TraversalType.PREORDER] - AST traversal type, according to weaver.TraversalType
 */
function searchFrom($baseJp, type, filter, traversal) {
  return Query.searchFrom($baseJp, type, filter, traversal);
}

/**
 * The same as Query.searchFromInclusive(), but available as a global function.
 *
 * @param {$jp} $baseJp - starting join point for the search.
 * @param {String} type - type of the join point to search.
 * @param {Object|String|Function|Regex} [filter = {}] - filter rules for the search.
 * @param {String} [traversal = TraversalType.PREORDER] - AST traversal type, according to weaver.TraversalType
 */
function searchFromInclusive($baseJp, type, filter, traversal) {
  return Query.searchFromInclusive($baseJp, type, filter, traversal);
}
