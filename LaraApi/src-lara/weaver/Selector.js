import { LaraJoinPoint, } from "../LaraJoinPoint.js";
import { laraGetter } from "../lara/core/LaraCore.js";
import Accumulator from "../lara/util/Accumulator.js";
import JoinPoints from "./JoinPoints.js";
import TraversalType from "./TraversalType.js";
import Weaver from "./Weaver.js";
/**
 * @internal Lara Common Language dirty hack. IMPROPER USAGE WILL BREAK THE WHOLE WEAVER!
 */
let selectorJoinPointsClass = JoinPoints;
/**
 * @internal Lara Common Language dirty hack. IMPROPER USAGE WILL BREAK THE WHOLE WEAVER!
 */
export function setSelectorJoinPointsClass(value = JoinPoints) {
    selectorJoinPointsClass = value;
}
function isAllowedDefaultAttributeType(obj) {
    if (obj instanceof RegExp)
        return true;
    const type = typeof obj;
    return (type === "string" ||
        type === "number" ||
        type === "bigint" ||
        type === "boolean");
}
/**
 * Selects join points according to their type and filter rules.
 *
 * @param $baseJp - starting join point for the search.
 * @param inclusive - if true, $baseJp is included in the search.
 *
 */
export default class Selector {
    $currentJps = [];
    lastName = "";
    $baseJp;
    addBaseJp;
    static STARTING_POINT = "_starting_point";
    constructor($baseJp, inclusive = false) {
        this.$baseJp = $baseJp ?? selectorJoinPointsClass.root();
        this.addBaseJp = inclusive;
        this.lastName = Selector.STARTING_POINT;
        this.$currentJps.push(Selector.newJpChain(this.$baseJp));
    }
    /// STATIC FUNCTIONS
    static copyChain($jpChain) {
        const copy = { ...$jpChain };
        copy.counter = copy.counter.copy();
        copy.jpAttributes = { ...copy.jpAttributes };
        return copy;
    }
    static newJpChain($startingPoint) {
        return {
            counter: new Accumulator(),
            jpAttributes: { _starting_point: $startingPoint },
        };
    }
    static parseWrapperFilter(joinPointType, filter = () => true) {
        if (isAllowedDefaultAttributeType(filter)) {
            // If filter is a string, RegExp, number, boolean or bigint, return a JpFilter type object that filters by the default attribute
            const defaultAttribute = Weaver.getDefaultAttribute(joinPointType);
            if (defaultAttribute == undefined) {
                throw new Error(`Selector: cannot use default filter for join point "${joinPointType.prototype.toString()}", it does not have a default attribute`);
            }
            return this.parseWrapperFilter(joinPointType, {
                [defaultAttribute]: filter,
            });
        }
        else if (typeof filter === "function") {
            // If filter is a function, then it must be a JpFilterFunction type. Return as is.
            return filter;
        }
        else {
            // Filter must be an object (JpFilter type). Return a function that filters by the given rules.
            return (jp) => {
                let allCriteriaMatch = true;
                for (const [k, v] of Object.entries(filter)) {
                    if (v instanceof RegExp) {
                        allCriteriaMatch &&= v.test(laraGetter(jp, k));
                    }
                    else if (typeof v === "function") {
                        allCriteriaMatch &&= v(laraGetter(jp, k), jp);
                    }
                    else {
                        allCriteriaMatch &&= laraGetter(jp, k) === v;
                    }
                }
                return allCriteriaMatch;
            };
        }
    }
    static convertStringFilterToWrapperFilter(joinPointType = "", filter) {
        if (filter == undefined) {
            return () => true;
        }
        // If filter is not an object, or if it is a regex, build object with default attribute of given jp name
        if (typeof filter !== "object" || filter instanceof RegExp) {
            // Get default attribute
            const defaultAttr = Weaver.getDefaultAttribute(joinPointType);
            // If no default attribute, return empty filter
            if (defaultAttr == undefined) {
                throw new Error(`Selector: cannot use default filter for join point "${joinPointType}", it does not have a default attribute`);
            }
            return this.convertStringFilterToWrapperFilter(joinPointType, {
                [defaultAttr]: filter,
            });
        }
        return (jp) => {
            let allCriteriaMatch = true;
            for (const [k, v] of Object.entries(filter)) {
                if (v instanceof RegExp) {
                    allCriteriaMatch &&= v.test(laraGetter(jp, k));
                }
                else if (typeof v === "function") {
                    allCriteriaMatch &&= v(laraGetter(jp, k), jp);
                }
                else {
                    allCriteriaMatch &&= laraGetter(jp, k) === v;
                }
            }
            return allCriteriaMatch;
        };
    }
    /**
     * Generator function, allows Selector to be used in for..of statements.
     *
     * Returns join points iteratively, as if .get() was called.
     */
    *[Symbol.iterator]() {
        if (this.lastName === Selector.STARTING_POINT) {
            console.log("Selector.iterator*: no join points have been searched, have you called a search function? (e.g., search, children)");
        }
        else {
            for (const $jpChain of this.$currentJps) {
                yield $jpChain.jpAttributes[this.lastName];
            }
        }
        this.$currentJps = [];
    }
    search(type = LaraJoinPoint, filter, traversal = TraversalType.PREORDER) {
        let jpFilter;
        if (typeof type === "string") {
            jpFilter = Selector.convertStringFilterToWrapperFilter(type, filter);
            const jpType = Weaver.findJoinpointType(type);
            if (!jpType) {
                throw new Error(`Join point type '${type}' not found.`);
            }
            return this.search(jpType, jpFilter, traversal);
        }
        else {
            jpFilter = Selector.parseWrapperFilter(type, filter ?? (() => true));
        }
        let fn;
        switch (traversal) {
            case TraversalType.PREORDER:
                fn = function ($jp, name) {
                    return selectorJoinPointsClass.descendants($jp, name);
                };
                break;
            case TraversalType.POSTORDER:
                fn = function ($jp, name) {
                    return selectorJoinPointsClass.descendantsPostorder($jp, name);
                };
                break;
            default:
                throw new Error(
                // eslint-disable-next-line @typescript-eslint/restrict-template-expressions
                `Traversal type not implemented: ${traversal}`);
        }
        return this.searchPrivate(type, fn, jpFilter);
    }
    children(type = LaraJoinPoint, filter) {
        let jpFilter;
        if (typeof type === "string") {
            jpFilter = Selector.convertStringFilterToWrapperFilter(type, filter);
            const jpType = Weaver.findJoinpointType(type);
            if (!jpType) {
                throw new Error(`Join point type '${type}' not found.`);
            }
            return this.children(jpType, jpFilter);
        }
        else {
            jpFilter = Selector.parseWrapperFilter(type, filter ?? (() => true));
        }
        return this.searchPrivate(type, function ($jp, name) {
            return selectorJoinPointsClass.children($jp, name);
        }, jpFilter);
    }
    scope(type = LaraJoinPoint, filter) {
        let jpFilter;
        if (typeof type === "string") {
            jpFilter = Selector.convertStringFilterToWrapperFilter(type, filter);
            const jpType = Weaver.findJoinpointType(type);
            if (!jpType) {
                throw new Error(`Join point type '${type}' not found.`);
            }
            return this.scope(jpType, jpFilter);
        }
        else {
            jpFilter = Selector.parseWrapperFilter(type, filter ?? (() => true));
        }
        return this.searchPrivate(type, function ($jp, name) {
            return selectorJoinPointsClass.scope($jp, name);
        }, jpFilter);
    }
    searchPrivate(type, selectFunction, jpFilter = () => true) {
        const name = Weaver.findJoinpointTypeName(type) ?? "joinpoint";
        const $newJps = [];
        /**
         * Lara Common Language dirty hack. REMOVE ME PLEASE!
         */
        if (selectorJoinPointsClass !== JoinPoints && type === LaraJoinPoint) {
            // We are in LCL mode.
            throw new Error("In LCL mode you are required to specify a type in a search.");
        }
        // If add base jp, this._$currentJps must have at most 1 element
        if (this.addBaseJp && this.$currentJps.length > 0) {
            if (this.$currentJps.length > 1) {
                throw new Error(`Selector._searchPrivate: 'inclusive' is true, but currentJps is larger than one ('${this.$currentJps.length}')`);
            }
            this.addBaseJp = false;
            // Filter does not test if the join point is of the right type
            const $root = this.$currentJps[0].jpAttributes[this.lastName];
            if ($root instanceof type) {
                this.addJps(name, $newJps, [$root], jpFilter, this.$currentJps[0]);
            }
        }
        // Each $jp is an object with the current chain
        for (const $jpChain of this.$currentJps) {
            const $jp = $jpChain.jpAttributes[this.lastName];
            const $allJps = selectFunction($jp, type);
            this.addJps(name, $newJps, $allJps, jpFilter, $jpChain);
        }
        // Update
        this.$currentJps = $newJps;
        this.lastName = name;
        return this;
    }
    addJps(name, $newJps, $jps, jpFilter, $jpChain) {
        for (const $jp of $jps) {
            const $filteredJp = [$jp].filter(jpFilter);
            if ($filteredJp.length === 0) {
                continue;
            }
            if ($filteredJp.length > 1) {
                throw new Error(`Selector._addJps: Expected $filteredJp to have length 1, has ${$filteredJp.length}`);
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
        if (this.lastName === Selector.STARTING_POINT) {
            console.log("Selector.get(): no join points have been searched, have you called a search function? (e.g., search, children)");
            return [];
        }
        const returnJps = this.$currentJps.map((chain) => chain.jpAttributes[this.lastName]);
        this.$currentJps = [];
        return returnJps;
    }
    /**
     * @returns An array of objects where each object maps the name of the join point to the corresponding join point that was searched, as well as creating mappings of the format \<joinpoint_name\>_\<repetition\>. For instance, if the search chain has the same name multiple times (e.g., search("loop").search("loop")), the chain object will have an attribute "loop" mapped to the last loop of the chain, an attribute "loop_0" mapped to the first loop of the chain and an attribute "loop_1" mapped to the second loop of the chain.
     */
    chain() {
        if (this.lastName === Selector.STARTING_POINT) {
            console.log("Selector.get(): no join points have been searched, have you called a search function? (e.g., search, children)");
            return [];
        }
        const returnJps = this.$currentJps.map((chain) => chain.jpAttributes);
        this.$currentJps = [];
        return returnJps;
    }
    /**
     * Same as .first()
     *
     * @returns The first selected node
     */
    getFirst() {
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
//# sourceMappingURL=Selector.js.map