import { laraGetter } from "../core/LaraCore.js";
/**
 * Filters join points according to the given rules.
 *
 * @param rules - Object where each key represents the name of a join point attribute, and the value the pattern that we will use to match against the attribute.
 * The pattern can be a string (exact match), a regex or a function that receives the attribute and returns a boolean.
 *
 * @deprecated Use the javascript .filter() method instead.
 */
export default class JpFilter {
    rules;
    constructor(rules) {
        this.rules = rules;
    }
    /**
     * Filters an array of join points.
     *
     * @returns an array of the join points that pass the filter
     */
    filter($jps) {
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
    match(value, pattern) {
        if ((pattern instanceof RegExp && pattern.test(value)) ||
            (typeof pattern === "function" && pattern(value)) ||
            (typeof pattern === "string" && value === pattern) ||
            (typeof pattern === "boolean" && value === pattern) ||
            (typeof pattern === "number" && value === pattern)) {
            return true;
        }
        return false;
    }
}
//# sourceMappingURL=JpFilter.js.map