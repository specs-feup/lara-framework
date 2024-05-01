import { LaraJoinPoint } from "../../LaraJoinPoint.js";
import { laraGetter } from "../core/LaraCore.js";

type JpFilterTypes =
  | RegExp
  | ((str: string, jp?: LaraJoinPoint) => boolean)
  | ((jp: LaraJoinPoint) => boolean)
  | string
  | boolean
  | number
  | undefined;

export type JpFilterRules = {
  [key: string]: JpFilterTypes;
};

/**
 * Filters join points according to the given rules.
 *
 * @param rules - Object where each key represents the name of a join point attribute, and the value the pattern that we will use to match against the attribute.
 * The pattern can be a string (exact match), a regex or a function that receives the attribute and returns a boolean.
 *
 * @deprecated Use the javascript .filter() method instead.
 */
export default class JpFilter {
  private rules: JpFilterRules;

  constructor(rules: JpFilterRules) {
    this.rules = rules;
  }

  /**
   * Filters an array of join points.
   *
   * @returns an array of the join points that pass the filter
   */
  filter($jps: LaraJoinPoint[]) {
    const $filteredJps = $jps.filter((jp) => {
      const keys = Object.keys(this.rules);
      for (let key of keys) {
        const rxPrefix = "rx_";
        if (key.startsWith(rxPrefix)) {
          key = key.substring(rxPrefix.length);
        }

        const pattern = this.rules[key];

        // TODO: Revert this change when we ensure that all weavers use the new LaraJoinPoint class
        const attributeValue = laraGetter(jp, key);

        if (!this.match(attributeValue, pattern)) {
          return false;
        }
      }

      return true;
    });

    return $filteredJps;
  }

  private match(value: any, pattern: JpFilterTypes): boolean {
    if (
      (pattern instanceof RegExp && pattern.test(value as string)) ||
      (typeof pattern === "function" && pattern(value)) ||
      (typeof pattern === "string" && value === pattern) ||
      (typeof pattern === "boolean" && value === pattern) ||
      (typeof pattern === "number" && value === pattern)
    ) {
      return true;
    }

    return false;
  }
}
