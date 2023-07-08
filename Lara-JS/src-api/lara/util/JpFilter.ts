import { println } from "../../core/output.js";
import Check from "../Check.js";

/**
 * Filters join points according to the given rules.
 *
 * @param rules - Object where each key represents the name of a join point attribute, and the value the pattern that we will use to match against the attribute.
 * The pattern can be a string (exact match), a regex or a function that receives the attribute and returns a boolean.
 */
export default class JpFilter {
  rules: {
    [key: string]: RegExp | ((str: any) => boolean) | string;
  };

  constructor(rules: {
    [key: string]: RegExp | ((str: any) => boolean) | string;
  }) {
    this.rules = rules;
  }

  /**
   * Filters an array of join points.
   *
   * @returns an array of the join points that pass the filter
   */
  filter($jps: any[]) {
    const $filteredJps = $jps.filter((jp) => {
      const keys = Object.keys(this.rules);
      for (let key of keys) {
        const rxPrefix = "rx_";
        if (key.startsWith(rxPrefix)) {
          key = key.substring(rxPrefix.length);
        }

        const pattern = this.rules[key];
        const attributeValue: string = jp[key];

        if (
          attributeValue === undefined ||
          (pattern instanceof RegExp && !pattern.test(attributeValue)) ||
          (typeof pattern === "function" && !pattern(attributeValue)) ||
          (typeof pattern === "string" && attributeValue !== pattern)
        ) {
          return false;
        }
      }

      return true;
    });

    return $filteredJps;
  }
}
