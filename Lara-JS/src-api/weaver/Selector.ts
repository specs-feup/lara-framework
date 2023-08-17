import { LaraJoinPoint } from "../LaraJoinPoint.js";
import Accumulator from "../lara/util/Accumulator.js";
import JpFilter from "../lara/util/JpFilter.js";
import JoinPointsBase from "./JoinPointsBase.js";
import TraversalType from "./TraversalType.js";
import Weaver from "./Weaver.js";

interface SelectorChain {
  counter: Accumulator;
  jpAttributes: {
    _starting_point: LaraJoinPoint;
    [key: string]: LaraJoinPoint;
  };
}

export type SelectorFilter =
  | string
  | RegExp
  | { [key: string]: RegExp | ((str: string) => boolean) | string | boolean }
  | ((str: string) => boolean);

/**
 * Selects join points according to their type and filter rules.
 *
 * @param $baseJp - starting join point for the search.
 * @param inclusive - if true, $baseJp is included in the search.
 *
 */
export default class Selector {
  private $currentJps: SelectorChain[] | undefined;
  private lastName: string;
  private addBaseJp: boolean;
  private static STARTING_POINT = "_starting_point";

  constructor($baseJp?: LaraJoinPoint, inclusive = false) {
    this.$currentJps =
      $baseJp === undefined ? undefined : [Selector.newJpChain($baseJp)];
    this.lastName = $baseJp === undefined ? "" : Selector.STARTING_POINT;
    this.addBaseJp = inclusive;
  }

  /// STATIC FUNCTIONS

  private static copyChain($jpChain: SelectorChain) {
    const copy = Object.assign({}, $jpChain);

    copy.counter = copy.counter.copy();
    copy.jpAttributes = Object.assign({}, copy.jpAttributes);

    return copy;
  }

  private static newJpChain($startingPoint: LaraJoinPoint): SelectorChain {
    return {
      counter: new Accumulator(),
      jpAttributes: { _starting_point: $startingPoint },
    };
  }

  private static parseFilter(
    filter: SelectorFilter = {},
    joinPointTypeName = ""
  ): JpFilter {
    // If filter is not an object, or if it is a regex, build object with default attribute of given jp name
    if (typeof filter !== "object" || filter instanceof RegExp) {
      // Get default attribute
      const defaultAttr = Weaver.getDefaultAttribute(joinPointTypeName);

      // If no default attribute, return empty filter
      if (defaultAttr === undefined) {
        console.log(
          "Selector: cannot use default filter for join point '" +
            joinPointTypeName +
            "', it does not have a default attribute"
        );
        return new JpFilter({});
      }

      return new JpFilter({
        [defaultAttr]: filter,
      });
    }

    return new JpFilter(filter);
  }

  /**
   * Generator function, allows Selector to be used in for..of statements.
   *
   * Returns join points iteratively, as if .get() was called.
   */
  *[Symbol.iterator]() {
    if (this.$currentJps) {
      for (const $jpChain of this.$currentJps) {
        yield $jpChain.jpAttributes[this.lastName];
      }
    } else {
      console.log(
        "Selector.iterator*: no join points have been searched, have you called a search function? (e.g., search, children)"
      );
    }
    this.$currentJps = undefined;
  }

  /**
   * @param type - type of the join point to search.
   * @param filter - filter rules for the search.
   * @param traversal - AST traversal type, according to TraversalType
   *
   * @returns The results of the search.
   */
  search(
    name?: string,
    filter: SelectorFilter = {},
    traversal: TraversalType = TraversalType.PREORDER
  ): Selector {
    switch (traversal) {
      case TraversalType.PREORDER:
        return this.searchPrivate(
          name,
          Selector.parseFilter(filter, name),
          function ($jp: LaraJoinPoint, name?: string) {
            return JoinPointsBase.descendants($jp, name);
          }
        );
      case TraversalType.POSTORDER:
        return this.searchPrivate(
          name,
          Selector.parseFilter(filter, name),
          function ($jp: LaraJoinPoint, name?: string) {
            return JoinPointsBase.descendantsPostorder($jp, name);
          }
        );
      default:
        throw new Error(
          // eslint-disable-next-line @typescript-eslint/restrict-template-expressions
          `Traversal type not implemented: ${traversal}`
        );
    }
  }

  /**
   * Search in the children of the previously selected nodes.
   *
   * @param type - type of the join point to search.
   * @param filter - filter rules for the search.
   *
   * @returns The results of the search.
   */
  children(type?: string, filter: SelectorFilter = {}): Selector {
    return this.searchPrivate(
      type,
      Selector.parseFilter(filter, type),
      function ($jp: LaraJoinPoint, name?: string) {
        return JoinPointsBase.children($jp, name);
      }
    );
  }

  /**
   * If previously select nodes have the concept of scope (e.g. if, loop), search the direct children of that scope.
   *
   * @param name - type of the join point to search.
   * @param filter - filter rules for the search.
   *
   * @returns The results of the search.
   */
  scope(name?: string, filter: SelectorFilter = {}): Selector {
    return this.searchPrivate(
      name,
      Selector.parseFilter(filter, name),
      function ($jp: LaraJoinPoint, name?: string) {
        return JoinPointsBase.scope($jp, name);
      }
    );
  }

  private searchPrivate(
    name: string | undefined = undefined,
    jpFilter: JpFilter,
    selectFunction: (jp: LaraJoinPoint, name?: string) => LaraJoinPoint[]
  ) {
    const $newJps: SelectorChain[] = [];

    // If add base jp, this._$currentJps must have at most 1 element
    if (this.addBaseJp && this.$currentJps !== undefined) {
      if (this.$currentJps.length === 0) {
        throw "Selector._searchPrivate: 'inclusive' is true, but currentJps is empty, can this happen?";
      }

      if (this.$currentJps.length > 1) {
        throw `Selector._searchPrivate: 'inclusive' is true, but currentJps is larger than one ('
          ${this.$currentJps.length}')`;
      }

      this.addBaseJp = false;

      // Filter does not test if the join point is of the right type
      const $root = this.$currentJps[0].jpAttributes[this.lastName];
      if ($root.joinPointType === name) {
        this.addJps($newJps, [$root], jpFilter, this.$currentJps[0], name);
      }
    }

    const isCurrentJpsUndefined = this.$currentJps === undefined;
    this.$currentJps ??= [Selector.newJpChain(JoinPointsBase.root())];
    this.lastName = isCurrentJpsUndefined
      ? Selector.STARTING_POINT
      : this.lastName;

    // Each $jp is an object with the current chain
    for (const $jpChain of this.$currentJps) {
      const $jp = $jpChain.jpAttributes[this.lastName];

      const $allJps = selectFunction($jp, name);

      this.addJps($newJps, $allJps, jpFilter, $jpChain, name ?? "joinpoint");
    }

    // Update
    this.$currentJps = $newJps;
    this.lastName = name ?? "joinpoint";

    return this;
  }

  private addJps(
    $newJps: SelectorChain[],
    $jps: LaraJoinPoint[],
    jpFilter: JpFilter,
    $jpChain: SelectorChain,
    name: string
  ) {
    for (const $jp of $jps) {
      const $filteredJp = jpFilter.filter([$jp]);

      if ($filteredJp.length === 0) {
        continue;
      }

      if ($filteredJp.length > 1) {
        throw `Selector._addJps: Expected $filteredJp to have length 1, has 
        ${$filteredJp.length}`;
      }

      // Copy chain
      const $updatedChain = Selector.copyChain($jpChain);

      // Update join point
      $updatedChain.jpAttributes[name] = $jp;

      // Add jp with unique id
      const id = `${name}_${$updatedChain.counter.add(name)}`;
      $updatedChain.jpAttributes[id] = $jp;

      $newJps.push($updatedChain);
    }
  }

  /**
   * @returns an array with the join points of the last chain (e.g., search("function").search("call").get() returns an array of $call join points).
   */
  get() {
    if (this.$currentJps === undefined) {
      console.log(
        "Selector.get(): no join points have been searched, have you called a search function? (e.g., search, children)"
      );
      return [];
    }

    const returnJps: LaraJoinPoint[] = [];

    for (const $jpChain of this.$currentJps) {
      returnJps.push($jpChain.jpAttributes[this.lastName]);
    }

    this.$currentJps = undefined;
    return returnJps;
  }

  /**
   * @returns an array of objects where each object maps the name of the join point to the corresponding join point that was searched, as well as creating mappings of the format <joinpoint_name>_<repetition>. For instance, if the search chain has the same name multiple times (e.g., search("loop").search("loop")), the chain object will have an attribute "loop" mapped to the last loop of the chain, an attribute "loop_0" mapped to the first loop of the chain and an attribute "loop_1" mapped to the second loop of the chain.
   */
  chain() {
    if (this.$currentJps === undefined) {
      console.log(
        "Selector.get(): no join points have been searched, have you called a search function? (e.g., search, children)"
      );
      return [];
    }

    const returnJps = this.$currentJps.map((chain) => chain.jpAttributes);

    this.$currentJps = undefined;
    return returnJps;
  }

  /**
   * Same as .first()
   *
   * @returns The first selected node
   */
  getFirst(): LaraJoinPoint | undefined {
    const $jps = this.get();
    if ($jps.length === 0) {
      console.log("Selector.getFirst(): no join point found");
      return undefined;
    }

    return $jps[0];
  }

  /**
   * @returns the first selected node
   */
  first(): LaraJoinPoint | undefined {
    return this.getFirst();
  }
}