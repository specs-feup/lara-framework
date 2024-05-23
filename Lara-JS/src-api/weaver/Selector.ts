import { LaraJoinPoint, type DefaultAttribute } from "../LaraJoinPoint.js";
import { laraGetter } from "../lara/core/LaraCore.js";
import Accumulator from "../lara/util/Accumulator.js";
import { type JpFilterRules } from "../lara/util/JpFilter.js";
import JoinPoints from "./JoinPoints.js";
import TraversalType from "./TraversalType.js";
import Weaver from "./Weaver.js";

/**
 * @internal Lara Common Language dirty hack. IMPROPER USAGE WILL BREAK THE WHOLE WEAVER!
 */
let selectorJoinPointsClass: typeof JoinPoints = JoinPoints;

/**
 * @internal Lara Common Language dirty hack. IMPROPER USAGE WILL BREAK THE WHOLE WEAVER!
 */
export function setSelectorJoinPointsClass(
  value: typeof JoinPoints = JoinPoints
) {
  selectorJoinPointsClass = value;
}

/**
 * Extracts the return type of a method or the type of a property.
 */
type MemberType<T, key extends keyof T> = key extends never
  ? never
  : T[key] extends (...args: never) => infer R
  ? R
  : T[key];

type FilterFunction<T, Class> = (value: T, obj: Class) => boolean;

/**
 * If the type is a string, expands it to a string or a RegExp.
 */
type StringExpander<T> = T extends string ? T | RegExp : T;

/**
 * Expand type to allow for the basic type or a filter function accepting the basic type.
 */
type FilterFunctionExpander<T, Class> = T extends never
  ? never
  : T | FilterFunction<T, Class>;

/**
 * Filter type for Joinpoints. It can be a string to filter using the default attribute of the Joinpoint, or an object where each key represents the name of a join point attribute, and the value the pattern that we will use to match against the attribute.
 */
type JpFilter<T> = {
  -readonly [key in keyof T]?: StringExpander<
    FilterFunctionExpander<MemberType<T, key>, T>
  >;
};

type JpFilterFunction<T extends typeof LaraJoinPoint = typeof LaraJoinPoint> = (
  jp: InstanceType<T>
) => boolean;

type AllowedDefaultAttributeTypes = StringExpander<
  string | number | bigint | boolean
>;
function isAllowedDefaultAttributeType(
  obj: unknown
): obj is AllowedDefaultAttributeTypes {
  if (obj instanceof RegExp) return true;

  const type = typeof obj;
  return (
    type === "string" ||
    type === "number" ||
    type === "bigint" ||
    type === "boolean"
  );
}

export type Filter_WrapperVariant<T extends typeof LaraJoinPoint> =
  | Omit<JpFilter<InstanceType<T>>, "toString">
  | JpFilterFunction<T>
  | StringExpander<
      Extract<
        MemberType<InstanceType<T>, DefaultAttribute<T>>,
        AllowedDefaultAttributeTypes
      >
    >;

export type Filter_StringVariant =
  | string
  | RegExp
  | ((str: string) => boolean)
  | JpFilterRules;

interface SelectorChain {
  counter: Accumulator;
  jpAttributes: {
    _starting_point: LaraJoinPoint;
    [key: string]: LaraJoinPoint;
  };
}

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
    const copy = { ...$jpChain };

    copy.counter = copy.counter.copy();
    copy.jpAttributes = { ...copy.jpAttributes };

    return copy;
  }

  private static newJpChain($startingPoint: LaraJoinPoint): SelectorChain {
    return {
      counter: new Accumulator(),
      jpAttributes: { _starting_point: $startingPoint },
    };
  }

  private static parseWrapperFilter<T extends typeof LaraJoinPoint>(
    joinPointType: T,
    filter: Filter_WrapperVariant<T> = () => true
  ): JpFilterFunction<T> {
    if (isAllowedDefaultAttributeType(filter)) {
      // If filter is a string, RegExp, number, boolean or bigint, return a JpFilter type object that filters by the default attribute
      const defaultAttribute = Weaver.getDefaultAttribute(joinPointType);
      if (defaultAttribute == undefined) {
        throw new Error(
          `Selector: cannot use default filter for join point "${joinPointType.prototype.toString()}", it does not have a default attribute`
        );
      }

      return this.parseWrapperFilter(joinPointType, {
        [defaultAttribute]: filter,
      } as JpFilter<InstanceType<T>>);
    } else if (typeof filter === "function") {
      // If filter is a function, then it must be a JpFilterFunction type. Return as is.
      return filter;
    } else {
      // Filter must be an object (JpFilter type). Return a function that filters by the given rules.
      return (jp: InstanceType<T>): boolean => {
        for (const [k, v] of Object.entries(
          filter as JpFilter<InstanceType<T>>
        )) {
          if (v instanceof RegExp) {
            return v.test(laraGetter(jp, k) as string);
          } else if (typeof v === "function") {
            return (v as FilterFunction<unknown, InstanceType<T>>)(
              laraGetter(jp, k),
              jp
            );
          }

          return laraGetter(jp, k) === v;
        }
        return true;
      };
    }
  }

  private static parseStringFilter(
    joinPointType: string = "",
    filter: Filter_StringVariant = {}
  ): JpFilterFunction {
    // If filter is not an object, or if it is a regex, build object with default attribute of given jp name
    if (typeof filter !== "object" || filter instanceof RegExp) {
      // Get default attribute
      const defaultAttr = Weaver.getDefaultAttribute(joinPointType);

      // If no default attribute, return empty filter
      if (defaultAttr == undefined) {
        console.log(
          `Selector: cannot use default filter for join point "${joinPointType}", it does not have a default attribute`
        );
        return () => true;
      }

      return this.parseStringFilter(joinPointType, {
        [defaultAttr]: filter,
      });
    }

    return (jp: LaraJoinPoint): boolean => {
      for (const [k, v] of Object.entries(filter)) {
        if (v instanceof RegExp) {
          return v.test(laraGetter(jp, k) as string);
        } else if (typeof v === "function") {
          return (v as FilterFunction<unknown, LaraJoinPoint>)(
            laraGetter(jp, k),
            jp
          );
        }
        return laraGetter(jp, k) === v;
      }
      return true;
    };
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
  search<T extends typeof LaraJoinPoint>(
    type?: T,
    filter?: Filter_WrapperVariant<T>,
    traversal?: TraversalType
  ): Selector;
  /**
   * @deprecated Use the new search function variant that accepts a wrapper class.
   *
   * @param name - type of the join point to search.
   * @param filter - filter rules for the search.
   * @param traversal - AST traversal type, according to TraversalType
   *
   * @returns The results of the search.
   */
  search(
    name?: string,
    filter?: Filter_StringVariant,
    traversal?: TraversalType
  ): Selector;
  search<T extends typeof LaraJoinPoint>(
    type?: T | string,
    filter: Filter_WrapperVariant<T> | Filter_StringVariant = {},
    traversal: TraversalType = TraversalType.PREORDER
  ): Selector {
    let jpFilter: JpFilterFunction<T>;

    if (type === undefined || typeof type === "string") {
      jpFilter = Selector.parseStringFilter(
        type,
        filter as Filter_StringVariant
      );
    } else {
      jpFilter = Selector.parseWrapperFilter(
        type,
        filter as Filter_WrapperVariant<T>
      );
    }

    let fn;
    switch (traversal) {
      case TraversalType.PREORDER:
        fn = function ($jp: LaraJoinPoint, name?: string) {
          return selectorJoinPointsClass.descendants($jp, name);
        };
        break;
      case TraversalType.POSTORDER:
        fn = function ($jp: LaraJoinPoint, name?: string) {
          return selectorJoinPointsClass.descendantsPostorder($jp, name);
        };
        break;
      default:
        throw new Error(
          // eslint-disable-next-line @typescript-eslint/restrict-template-expressions
          `Traversal type not implemented: ${traversal}`
        );
    }

    return this.searchPrivate(type, jpFilter, fn);
  }

  /**
   * Search in the children of the previously selected nodes.
   *
   * @param type - type of the join point to search.
   * @param filter - filter rules for the search.
   *
   * @returns The results of the search.
   */
  children<T extends typeof LaraJoinPoint>(
    type?: T,
    filter?: Filter_WrapperVariant<T>
  ): Selector;
  /**
   * Search in the children of the previously selected nodes.
   *
   * @deprecated Use the new children function variant that accepts a wrapper class.
   *
   * @param name - type of the join point to search.
   * @param filter - filter rules for the search.
   *
   * @returns The results of the search.
   */
  children(name?: string, filter?: Filter_StringVariant): Selector;
  children<T extends typeof LaraJoinPoint>(
    type?: T | string,
    filter: Filter_WrapperVariant<T> | Filter_StringVariant = {}
  ): Selector {
    let jpFilter: JpFilterFunction<T>;

    if (type === undefined || typeof type === "string") {
      jpFilter = Selector.parseStringFilter(
        type,
        filter as Filter_StringVariant
      );
    } else {
      jpFilter = Selector.parseWrapperFilter(
        type,
        filter as Filter_WrapperVariant<T>
      );
    }

    return this.searchPrivate(
      type,
      jpFilter,
      function ($jp: LaraJoinPoint, name?: string) {
        return selectorJoinPointsClass.children($jp, name);
      }
    );
  }

  /**
   * If previously select nodes have the concept of scope (e.g. if, loop), search the direct children of that scope.
   *
   * @param type - type of the join point to search.
   * @param filter - filter rules for the search.
   *
   * @returns The results of the search.
   */
  scope<T extends typeof LaraJoinPoint>(
    type?: T,
    filter?: Filter_WrapperVariant<T>
  ): Selector;
  /**
   * If previously select nodes have the concept of scope (e.g. if, loop), search the direct children of that scope.
   *
   * @deprecated Use the new scope function variant that accepts a wrapper class.
   *
   * @param name - type of the join point to search.
   * @param filter - filter rules for the search.
   *
   * @returns The results of the search.
   */
  scope(name?: string, filter?: Filter_StringVariant): Selector;
  scope<T extends typeof LaraJoinPoint>(
    type?: T | string,
    filter: Filter_WrapperVariant<T> | Filter_StringVariant = {}
  ): Selector {
    let jpFilter: JpFilterFunction<T>;

    if (type === undefined || typeof type === "string") {
      jpFilter = Selector.parseStringFilter(
        type,
        filter as Filter_StringVariant
      );
    } else {
      jpFilter = Selector.parseWrapperFilter(
        type,
        filter as Filter_WrapperVariant<T>
      );
    }

    return this.searchPrivate(
      type,
      jpFilter,
      function ($jp: LaraJoinPoint, name?: string) {
        return selectorJoinPointsClass.scope($jp, name);
      }
    );
  }

  private searchPrivate<T extends typeof LaraJoinPoint>(
    type: T | string | undefined,
    jpFilter: JpFilterFunction<T> = () => true,
    selectFunction: (jp: LaraJoinPoint, name?: string) => LaraJoinPoint[]
  ) {
    const name =
      typeof type === "undefined" || typeof type === "string"
        ? type
        : Weaver.findJoinpointTypeName(type);

    const $newJps: SelectorChain[] = [];

    // If add base jp, this._$currentJps must have at most 1 element
    if (this.addBaseJp && this.$currentJps !== undefined) {
      if (this.$currentJps.length === 0) {
        throw new Error(
          "Selector._searchPrivate: 'inclusive' is true, but currentJps is empty, can this happen?"
        );
      }

      if (this.$currentJps.length > 1) {
        throw new Error(
          `Selector._searchPrivate: 'inclusive' is true, but currentJps is larger than one ('${this.$currentJps.length}')`
        );
      }

      this.addBaseJp = false;

      // Filter does not test if the join point is of the right type
      const $root = this.$currentJps[0].jpAttributes[this.lastName];
      if (name && $root.instanceOf(name)) {
        this.addJps($newJps, [$root], jpFilter, this.$currentJps[0], name);
      }
    }

    const isCurrentJpsUndefined = this.$currentJps === undefined;
    this.$currentJps ??= [Selector.newJpChain(selectorJoinPointsClass.root())];
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
    jpFilter: JpFilterFunction,
    $jpChain: SelectorChain,
    name: string
  ) {
    for (const $jp of $jps) {
      const $filteredJp = [$jp].filter(jpFilter);

      if ($filteredJp.length === 0) {
        continue;
      }

      if ($filteredJp.length > 1) {
        throw new Error(
          `Selector._addJps: Expected $filteredJp to have length 1, has ${$filteredJp.length}`
        );
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

    const returnJps = this.$currentJps.map(
      (chain) => chain.jpAttributes[this.lastName]
    );

    this.$currentJps = undefined;
    return returnJps;
  }

  /**
   * @returns An array of objects where each object maps the name of the join point to the corresponding join point that was searched, as well as creating mappings of the format \<joinpoint_name\>_\<repetition\>. For instance, if the search chain has the same name multiple times (e.g., search("loop").search("loop")), the chain object will have an attribute "loop" mapped to the last loop of the chain, an attribute "loop_0" mapped to the first loop of the chain and an attribute "loop_1" mapped to the second loop of the chain.
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
  first() {
    return this.getFirst();
  }
}
