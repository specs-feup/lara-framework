import { JoinPointsBase } from "./JoinPointsBase.js";
import Accumulator from "../lara/util/Accumulator.js";
import Weaver from "./Weaver.js";
import { println } from "../core/output.js";
import TraversalType from "./TraversalType.js";
import JpFilter from "../lara/util/JpFilter.js";


/**
 * Selects join points according to their type and filter rules.
 *
 * @param {$jp} [$baseJp = undefined] - starting join point for the search.
 * @param {boolean} [inclusive = false] - if true, $baseJp is included in the search.
 *
 */
export default class Selector {

    _$currentJps: any;
    _lastName: any;
    _addBaseJp: any;
    static _COUNTER = "_counter";
    static _STARTING_POINT = "_starting_point";

    constructor($baseJp: any, inclusive?: any) {
	    this._$currentJps = $baseJp === undefined ? undefined : [Selector._newJpChain($baseJp)];
	    this._lastName = $baseJp === undefined ? undefined : Selector._STARTING_POINT;
	    this._addBaseJp = inclusive === undefined ? false : inclusive;
};


    /// STATIC FUNCTIONS

    static _copyChain($jpChain: any) {
	    const copy = Object.assign({}, $jpChain);		

	    copy[Selector._COUNTER] = copy[Selector._COUNTER].copy();

	    return copy;
    }

    static _newJpChain($startingPoint: any) {
	    // Add starting point
	    const chain: any = {_starting_point: $startingPoint};
	
	    // Add counter
	    chain[Selector._COUNTER] = new Accumulator();
	
	    return chain;
    }

    static _parseFilter(filter: any, name: string) {
	    // If undefined, return empty object
	    if(filter === undefined) {
		    return {};
	    }
	
	    // If filter is not an object, or if it is a regex, build object with default attribute of given jp name
	    if( typeof filter === "object" || filter instanceof RegExp) {
		    // Get default attribute
		    const defaultAttr = Weaver.getDefaultAttribute(name);
		
		    // If no default attribute, return empty filter
		    if(defaultAttr === undefined) {
			    println("Selector: cannot use default filter for join point '"+name+"', it does not have a default attribute");
			    return {};
		    }
		
		    const defaultFilter: any = {};
		    defaultFilter[defaultAttr] = filter;

		    return defaultFilter;
	    }
	
	    // Just return the filter
	    return filter;
    }	


    /// INSTANCE FUNCTIONS

    /**
     * Generator function, allows Selector to be used in for..of statements.
     *
     * Returns join points iteratively, as if .get() was called.
     */
    *[Symbol.iterator]() {

        if(this._$currentJps === undefined) {
            println("Selector.iterator*: no join points have been searched, have you called a search function? (e.g., search, children)");
            yield undefined;
        }
        

        for(var $jpChain of this._$currentJps) {
            yield $jpChain[this._lastName];
        }
        
        this._$currentJps = undefined;	
    }


    /**
    * @param {String} type - type of the join point to search.
    * @param {Object|String|Function|Regex} [filter = {}] - filter rules for the search. 
    * @param {String} [traversal = TraversalType.PREORDER] - AST traversal type, according to weaver.TraversalType 
    *
    * @return {weaver.Selector} the results of the search.
    */
    search(name: string, filter:Object|string|Function|RegExp, traversal:string) {
	    if(traversal === undefined) {
		    traversal = TraversalType.PREORDER;
	    }
	
	switch(traversal) {
		case TraversalType.PREORDER: 
			return this._searchPrivate(name, filter, function($jp: any , joinPoints: any, name: string) {return joinPoints.descendants($jp, name);});		
		case TraversalType.POSTORDER:
			return this._searchPrivate(name, filter, function($jp: any, joinPoints: any, name: string ) {return joinPoints.descendantsPostorder($jp, name);});
		default:
			throw new Error("Traversal type not implemented: " + traversal);
	}

}

    /**
     * Search in the children of the previously selected nodes.
     *
     * @param {String} type - type of the join point to search.
     * @param {Object|String|Function|Regex} [filter = {}] - filter rules for the search. 
     *
     * @return {weaver.Selector} the results of the search.
     */
    children(type: string, filter:any) {
        return this._searchPrivate(type, filter, function($jp: any, joinPoints: any, name: string) {return joinPoints.children($jp, name);});
    }

    /** 
     * If previously select nodes have the concept of scope (e.g. if, loop), search the direct children of that scope.
     *
     * @param {String} name - type of the join point to search.
     * @param {Object|String|Function|Regex} [filter = {}] - filter rules for the search.
     *
     * @returns {weaver.Selector} the results of the search. 
     */
    scope(name: string, filter: any) {
	    return this._searchPrivate(name, filter, function($jp: any, joinPoints: any, name: string) {return joinPoints.scope($jp, name);});
    }

    _searchPrivate(name: string, filter: any, selectFunction: Function) {

        if (selectFunction === undefined){
            throw "value is undefined"
        }
	
	    if(name === undefined) {
		    name = "joinpoint";
	    }

	    filter = Selector._parseFilter(filter, name);
	
	    const jpFilter = new JpFilter(filter);
	
	    const $newJps: Array<any> = [];
	    const jpCounter: Accumulator = new Accumulator();

	    // If add base jp, this._$currentJps must have at most 1 element
	    if(this._addBaseJp && (this._$currentJps !== undefined)) {

		    if(this._$currentJps.length === 0) {
			    throw "Selector._searchPrivate: 'inclusive' is true, but currentJps is empty, can this happen?";
		    }
		
		    if(this._$currentJps.length > 1) {
			    throw "Selector._searchPrivate: 'inclusive' is true, but currentJps is larger than one ('" + this._$currentJps.length + "')";
		    }
		
		    this._addBaseJp = false;
		
		    // Filter does not test if the join point is of the right type
		    const $root = this._$currentJps[0][this._lastName];
		    if($root.instanceOf(name)) {
			    this._addJps($newJps, [$root], jpFilter, this._$currentJps[0], name);		
		    }

	    }
	
	    const isCurrentJpsUndefined = this._$currentJps === undefined;
	    this._$currentJps = isCurrentJpsUndefined ? [Selector._newJpChain(JoinPointsBase.root())] : this._$currentJps;
	    this._lastName = isCurrentJpsUndefined ? Selector._STARTING_POINT : this._lastName;

	    // Each $jp is an object with the current chain
	    for(const $jpChain of this._$currentJps) {

		    const $jp = $jpChain[this._lastName];

		    const $allJps = selectFunction($jp, JoinPointsBase, name);
		
		    this._addJps($newJps, $allJps, jpFilter, $jpChain, name);
	    }

	    // Update
	    this._$currentJps = $newJps;
	    this._lastName = name;

	    return this;
    }

    _addJps($newJps: any, $jps: any, jpFilter: any, $jpChain: any, name: string) {
		
	    for(const $jp of $jps) {
		    const $filteredJp: any = jpFilter.filter([$jp]);
		
		    if($filteredJp.length === 0) {
			    continue;
		    }
		
		    if($filteredJp.length > 1) {
			    throw "Selector._addJps: Expected $filteredJp to have length 1, has " + $filteredJp.length;
		    }

		    // Copy chain
		    const $updatedChain = Selector._copyChain($jpChain);
		
		    // Update join point
		    $updatedChain[name] = $jp;
		
		    // Add jp with unique id
		    const id = name + "_" + $updatedChain[Selector._COUNTER].add(name);		
		    $updatedChain[id] = $jp;
		
		    $newJps.push($updatedChain);
	    }

    }




    /**
     * @return an array with the join points of the last chain (e.g., search("function").search("call").get() returns an array of $call join points).
     */
    get() {
        if(this._$currentJps === undefined) {
            println("Selector.get(): no join points have been searched, have you called a search function? (e.g., search, children)");
            return [];
        }
        
        const returnJps = [];
        for(const $jpChain of this._$currentJps) {
            returnJps.push($jpChain[this._lastName]);
        }
        
        this._$currentJps = undefined;
        return returnJps;
    }

    /**
     * @return an array of objects where each object maps the name of the join point to the corresponding join point that was searched, as well as creating mappings of the format <joinpoint_name>_<repetition>. For instance, if the search chain has the same name multiple times (e.g., search("loop").search("loop")), the chain object will have an attribute "loop" mapped to the last loop of the chain, an attribute "loop_0" mapped to the first loop of the chain and an attribute "loop_1" mapped to the second loop of the chain.
     */
    chain() {
        if(this._$currentJps === undefined) {
            println("Selector.get(): no join points have been searched, have you called a search function? (e.g., search, children)");
            return [];
        }
        
        const returnJps = this._$currentJps;
        
        this._$currentJps = undefined;
        return returnJps;
    }

    //  * @arg {bool} warnIfMultiple - if true, displays a warning if the search returns more than one result

    /**
     * Same as .first()
     * 
     * @return {$jp} the first selected node
     */
    getFirst() {
        const $jps = this.get();
        if($jps.length === 0) {
            println("Selector.getFirst(): no join point found");
            return undefined;
        }

        return $jps[0];
    }

    /**
     * @return {$jp} the first selected node
     */
    first(){
        return this.getFirst();
    }
}