import { LaraJoinPoint } from "../LaraJoinPoint.js";
import JoinPoints from "./JoinPoints.js";
import Selector, {
  type Filter_StringVariant,
  type Filter_WrapperVariant,
} from "./Selector.js";
import TraversalType from "./TraversalType.js";

/**
 * Class for selection of join points. Provides an API similar to the keyword 'select'.
 *
 * Search functions of this class return weaver.Selector objects, please refer to that class for more details regarding available functions and search options.
 *
 */
export default class Query {
  /**
   * Returns the root node of the current AST.
   *
   * @returns The root node
   */
  static root(): LaraJoinPoint {
    return JoinPoints.root();
  }

  /**
   * The same as Query.searchFrom(), but uses the root node as $baseJp.
   *
   * @param type - type of the join point to search.
   * @param filter - filter rules for the search. If the value is an object, each field of the object represents a rule that will be applied over the attribute that has the same name as the name of the field. If the value is not an object (e.g., String, Regex, Lambda), it is interpreted as a single rule that will be applied over the default attribute of the given type. E.g., if type is 'function', the value is a String 'foo' and the default attribute of function is 'name', this is equivalent as passing as value the object \{'name':'foo'\}. Rules can be a String (i.e., will match the value of the attribute against a string), a Regex (will match the value of the attribute against a regex) or a Function (i.e., function receives the value of the attribute and returns true if there is a match, or false otherwise).
   * @param traversal - AST traversal type, according to weaver.TraversalType
   *
   * @returns The results of the search.
   */
  static search<T extends typeof LaraJoinPoint>(
    type: T,
    filter?: Filter_WrapperVariant<T>,
    traversal?: TraversalType
  ): Selector;
  /**
   * The same as Query.searchFrom(), but uses the root node as $baseJp.
   *
   * @deprecated Use the new version of this function that receives a class as the first parameter.
   *
   * @param type - type of the join point to search.
   * @param filter - filter rules for the search. If the value is an object, each field of the object represents a rule that will be applied over the attribute that has the same name as the name of the field. If the value is not an object (e.g., String, Regex, Lambda), it is interpreted as a single rule that will be applied over the default attribute of the given type. E.g., if type is 'function', the value is a String 'foo' and the default attribute of function is 'name', this is equivalent as passing as value the object \{'name':'foo'\}. Rules can be a String (i.e., will match the value of the attribute against a string), a Regex (will match the value of the attribute against a regex) or a Function (i.e., function receives the value of the attribute and returns true if there is a match, or false otherwise).
   * @param traversal - AST traversal type, according to weaver.TraversalType
   *
   * @returns The results of the search.
   */
  static search(
    type: string,
    filter?: Filter_StringVariant,
    traversal?: TraversalType
  ): Selector;
  static search<T extends typeof LaraJoinPoint>(
    type: T | string,
    filter?: Filter_WrapperVariant<T> | Filter_StringVariant,
    traversal: TraversalType = TraversalType.PREORDER
  ): Selector {
    if (typeof type === "string") {
      return new Selector().search(type, filter as Filter_StringVariant, traversal);
    } else {
      return new Selector().search(
        type,
        filter as Filter_WrapperVariant<T>,
        traversal
      );
    }
  }

  /**
   * In-depth search of nodes of the given type, starting from a base node (exclusive).
   *
   * @param $baseJp - starting join point for the search.
   * @param type - type of the join point to search.
   * @param filter - filter rules for the search. If the value is an object, each field of the object represents a rule that will be applied over the attribute that has the same name as the name of the field. If the value is not an object (e.g., String, Regex, Lambda), it is interpreted as a single rule that will be applied over the default attribute of the given type. E.g., if type is 'function', the value is a String 'foo' and the default attribute of function is 'name', this is equivalent as passing as value the object \{'name':'foo'\}. Rules can be a String (i.e., will match the value of the attribute against a string), a Regex (will match the value of the attribute against a regex) or a Function (i.e., function receives the value of the attribute and returns true if there is a match, or false otherwise).
   * @param traversal - AST traversal type, according to weaver.TraversalType
   *
   * @returns The results of the search.
   */
  static searchFrom<T extends typeof LaraJoinPoint>(
    $baseJp: LaraJoinPoint,
    type?: T,
    filter?: Filter_WrapperVariant<T>,
    traversal?: TraversalType
  ): Selector;
  /**
   * In-depth search of nodes of the given type, starting from a base node (exclusive).
   *
   * @deprecated Use the new version of this function that receives a class as the first parameter.
   *
   * @param $baseJp - starting join point for the search.
   * @param type - type of the join point to search.
   * @param filter - filter rules for the search. If the value is an object, each field of the object represents a rule that will be applied over the attribute that has the same name as the name of the field. If the value is not an object (e.g., String, Regex, Lambda), it is interpreted as a single rule that will be applied over the default attribute of the given type. E.g., if type is 'function', the value is a String 'foo' and the default attribute of function is 'name', this is equivalent as passing as value the object \{'name':'foo'\}. Rules can be a String (i.e., will match the value of the attribute against a string), a Regex (will match the value of the attribute against a regex) or a Function (i.e., function receives the value of the attribute and returns true if there is a match, or false otherwise).
   * @param traversal - AST traversal type, according to weaver.TraversalType
   *
   * @returns The results of the search.
   */
  static searchFrom(
    $baseJp: LaraJoinPoint,
    type?: string,
    filter?: Filter_StringVariant,
    traversal?: TraversalType
  ): Selector;
  static searchFrom<T extends typeof LaraJoinPoint>(
    $baseJp: LaraJoinPoint,
    type?: string,
    filter?: Filter_WrapperVariant<T> | Filter_StringVariant,
    traversal: TraversalType = TraversalType.PREORDER
  ): Selector {
    if (typeof type === "string") {
      return new Selector($baseJp).search(
        type,
        filter as Filter_StringVariant,
        traversal
      );
    } else {
      return new Selector($baseJp).search(type, filter as Filter_WrapperVariant<T>, traversal);
    }
  }

  /**
   * The same as Query.searchFrom(), but $baseJp is included in the search.
   *
   * @param $baseJp - starting join point for the search.
   * @param type - type of the join point to search.
   * @param filter - filter rules for the search.
   * @param traversal - AST traversal type, according to weaver.TraversalType
   *
   * @returns The results of the search.
   */
  static searchFromInclusive<T extends typeof LaraJoinPoint>(
    $baseJp: LaraJoinPoint,
    type?: T,
    filter?: Filter_WrapperVariant<T>,
    traversal?: TraversalType
  ): Selector;
  /**
   * The same as Query.searchFrom(), but $baseJp is included in the search.
   *
   * @deprecated Use the new version of this function that receives a class as the first parameter.
   *
   * @param $baseJp - starting join point for the search.
   * @param type - type of the join point to search.
   * @param filter - filter rules for the search.
   * @param traversal - AST traversal type, according to weaver.TraversalType
   *
   * @returns The results of the search.
   */
  static searchFromInclusive(
    $baseJp: LaraJoinPoint,
    type?: string,
    filter?: Filter_StringVariant,
    traversal?: TraversalType
  ): Selector;
  static searchFromInclusive<T extends typeof LaraJoinPoint>(
    $baseJp: LaraJoinPoint,
    type?: T | string,
    filter?: Filter_WrapperVariant<T> | Filter_StringVariant,
    traversal: TraversalType = TraversalType.PREORDER
  ): Selector {
    if (typeof type === "string") {
      return new Selector($baseJp, true).search(
        type,
        filter as Filter_StringVariant,
        traversal
      );
    } else {
      return new Selector($baseJp, true).search(
        type,
        filter as Filter_WrapperVariant<T>,
        traversal
      );
    }
  }

  /**
   * Search the direct children of the given $baseJp.
   *
   * @param $baseJp - starting join point for the search.
   * @param type - type of the join point to search.
   * @param filter - filter rules for the search.
   *
   * @returns The results of the search.
   */
  static childrenFrom<T extends typeof LaraJoinPoint>(
    $baseJp: LaraJoinPoint,
    type?: T,
    filter?: Filter_WrapperVariant<T>
  ): Selector;
  /**
   * Search the direct children of the given $baseJp.
   *
   * @deprecated Use the new version of this function that receives a class as the first parameter.
   *
   * @param $baseJp - starting join point for the search.
   * @param type - type of the join point to search.
   * @param filter - filter rules for the search.
   *
   * @returns The results of the search.
   */
  static childrenFrom(
    $baseJp: LaraJoinPoint,
    type?: string,
    filter?: Filter_StringVariant
  ): Selector;
  static childrenFrom<T extends typeof LaraJoinPoint>(
    $baseJp: LaraJoinPoint,
    type?: T | string,
    filter?: Filter_WrapperVariant<T> | Filter_StringVariant
  ): Selector {
    if (typeof type === "string") {
      return new Selector($baseJp).children(
        type,
        filter as Filter_StringVariant
      );
    } else {
      return new Selector($baseJp).children(
        type,
        filter as Filter_WrapperVariant<T>
      );
    }
  }

  /**
   * If $baseJp has the concept of scope (e.g. if, loop), search the direct children of that scope.
   *
   * @param $baseJp - starting join point for the search.
   * @param type - type of the join point to search.
   * @param filter - filter rules for the search.
   *
   * @returns The results of the search.
   */
  static scopeFrom<T extends typeof LaraJoinPoint>(
    $baseJp: LaraJoinPoint,
    type?: T,
    filter?: Filter_WrapperVariant<T>
  ): Selector;
  /**
   * If $baseJp has the concept of scope (e.g. if, loop), search the direct children of that scope.
   *
   * @deprecated Use the new version of this function that receives a class as the first parameter.
   *
   * @param $baseJp - starting join point for the search.
   * @param type - type of the join point to search.
   * @param filter - filter rules for the search.
   *
   * @returns The results of the search.
   */
  static scopeFrom(
    $baseJp: LaraJoinPoint,
    type?: string,
    filter?: Filter_StringVariant
  ): Selector;
  static scopeFrom<T extends typeof LaraJoinPoint>(
    $baseJp: LaraJoinPoint,
    type?: T | string,
    filter?: Filter_WrapperVariant<T> | Filter_StringVariant
  ): Selector {
    if (typeof type === "string") {
      return new Selector($baseJp).scope(type, filter as Filter_StringVariant);
    } else {
      return new Selector($baseJp).scope(
        type,
        filter as Filter_WrapperVariant<T>
      );
    }
  }
}
