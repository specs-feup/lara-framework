import { LaraJoinPoint } from "../../LaraJoinPoint.js";

type JpFilterRules = {
  [key: string]: RegExp | ((str: string) => boolean) | string | boolean;
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

        for (let obj = jp; obj !== null; obj = Object.getPrototypeOf(obj)) {
          const descriptor = Object.getOwnPropertyDescriptor(obj, key);
          if (descriptor !== undefined) {
            let attributeValue: any = undefined;
            if (Object.getOwnPropertyDescriptor(descriptor, "get")) {
              attributeValue = descriptor.get?.call?.(jp);
            } else if (Object.getOwnPropertyDescriptor(descriptor, "value")) {
              attributeValue = descriptor.value;
            } else {
              continue;
            }

            if (
              (pattern instanceof RegExp && !pattern.test(attributeValue)) ||
              (typeof pattern === "function" && !pattern(attributeValue)) ||
              (typeof pattern === "string" && attributeValue !== pattern) ||
              (typeof pattern === "boolean" && attributeValue !== pattern)
            ) {
              return false;
            }

            break;
          }
        }
      }

      return true;
    });

    return $filteredJps;
  }
}
