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
    $currentJps;
    lastName;
    addBaseJp;
    static STARTING_POINT = "_starting_point";
    constructor($baseJp, inclusive = false) {
        this.$currentJps =
            $baseJp === undefined ? undefined : [Selector.newJpChain($baseJp)];
        this.lastName = $baseJp === undefined ? "" : Selector.STARTING_POINT;
        this.addBaseJp = inclusive;
    }
    /// STATIC FUNCTIONS
    static copyChain($jpChain) {
        const copy = Object.assign({}, $jpChain);
        copy.counter = copy.counter.copy();
        copy.jpAttributes = Object.assign({}, copy.jpAttributes);
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
                for (const [k, v] of Object.entries(filter)) {
                    if (v instanceof RegExp) {
                        return v.test(laraGetter(jp, k));
                    }
                    else if (typeof v === "function") {
                        return v(laraGetter(jp, k), jp);
                    }
                    return laraGetter(jp, k) === v;
                }
                return true;
            };
        }
    }
    static parseStringFilter(joinPointType = "", filter = {}) {
        // If filter is not an object, or if it is a regex, build object with default attribute of given jp name
        if (typeof filter !== "object" || filter instanceof RegExp) {
            // Get default attribute
            const defaultAttr = Weaver.getDefaultAttribute(joinPointType);
            // If no default attribute, return empty filter
            if (defaultAttr == undefined) {
                console.log(`Selector: cannot use default filter for join point "${joinPointType}", it does not have a default attribute`);
                return () => true;
            }
            return this.parseStringFilter(joinPointType, {
                [defaultAttr]: filter,
            });
        }
        return (jp) => {
            for (const [k, v] of Object.entries(filter)) {
                if (v instanceof RegExp) {
                    return v.test(laraGetter(jp, k));
                }
                else if (typeof v === "function") {
                    return v(laraGetter(jp, k), jp);
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
        }
        else {
            console.log("Selector.iterator*: no join points have been searched, have you called a search function? (e.g., search, children)");
        }
        this.$currentJps = undefined;
    }
    search(type, filter = {}, traversal = TraversalType.PREORDER) {
        let jpFilter;
        if (type === undefined || typeof type === "string") {
            jpFilter = Selector.parseStringFilter(type, filter);
        }
        else {
            jpFilter = Selector.parseWrapperFilter(type, filter);
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
        return this.searchPrivate(type, jpFilter, fn);
    }
    children(type, filter = {}) {
        let jpFilter;
        if (type === undefined || typeof type === "string") {
            jpFilter = Selector.parseStringFilter(type, filter);
        }
        else {
            jpFilter = Selector.parseWrapperFilter(type, filter);
        }
        return this.searchPrivate(type, jpFilter, function ($jp, name) {
            return selectorJoinPointsClass.children($jp, name);
        });
    }
    scope(type, filter = {}) {
        let jpFilter;
        if (type === undefined || typeof type === "string") {
            jpFilter = Selector.parseStringFilter(type, filter);
        }
        else {
            jpFilter = Selector.parseWrapperFilter(type, filter);
        }
        return this.searchPrivate(type, jpFilter, function ($jp, name) {
            return selectorJoinPointsClass.scope($jp, name);
        });
    }
    searchPrivate(type, jpFilter = () => true, selectFunction) {
        const name = typeof type === "undefined" || typeof type === "string"
            ? type
            : Weaver.findJoinpointTypeName(type);
        const $newJps = [];
        // If add base jp, this._$currentJps must have at most 1 element
        if (this.addBaseJp && this.$currentJps !== undefined) {
            if (this.$currentJps.length === 0) {
                throw new Error("Selector._searchPrivate: 'inclusive' is true, but currentJps is empty, can this happen?");
            }
            if (this.$currentJps.length > 1) {
                throw new Error(`Selector._searchPrivate: 'inclusive' is true, but currentJps is larger than one ('${this.$currentJps.length}')`);
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
    addJps($newJps, $jps, jpFilter, $jpChain, name) {
        for (const $jp of $jps) {
            const $filteredJp = [$jp].filter(jpFilter);
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
            console.log("Selector.get(): no join points have been searched, have you called a search function? (e.g., search, children)");
            return [];
        }
        const returnJps = [];
        for (const $jpChain of this.$currentJps) {
            returnJps.push($jpChain.jpAttributes[this.lastName]);
        }
        this.$currentJps = undefined;
        return returnJps;
    }
    /**
     * @returns An array of objects where each object maps the name of the join point to the corresponding join point that was searched, as well as creating mappings of the format \<joinpoint_name\>_\<repetition\>. For instance, if the search chain has the same name multiple times (e.g., search("loop").search("loop")), the chain object will have an attribute "loop" mapped to the last loop of the chain, an attribute "loop_0" mapped to the first loop of the chain and an attribute "loop_1" mapped to the second loop of the chain.
     */
    chain() {
        if (this.$currentJps === undefined) {
            console.log("Selector.get(): no join points have been searched, have you called a search function? (e.g., search, children)");
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