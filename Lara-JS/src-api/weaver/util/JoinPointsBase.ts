import Weaver from "./Weaver.js";
import Check from "../../lara/Check.js";

/**
 * Object which provides low-level join point-related methods.
 * @class
 */
export class JoinPoints {

 //------ CHANGE ANY FOR JOINPOINT----------

   /*
 
	JoinPoints._INSTANCE = new JoinPoints();

	JoinPoints.getInstance = function() {
		return JoinPoints._INSTANCE;
	}

	*/

	/**
	 * 
	 * @return {$jp} the current root node of the AST
	 */
	static root(): any {
		return Weaver.getWeaverEngine().getRootJp();
	}

	/**
 	* Converts an AST node to a JointPoint.
 	*
 	* @param {node}
 	* @return {$jp} 
 	*/
	toJoinPoint(node: unknown): any {
		throw "JoinPoints.toJoinPoint: not implemented";
	}


	/**
	 * 
	 * @return {$jp[]} all the children of the given node
	 */
	_all_children($jp: unknown): any {
		throw "JoinPoints._all_children: not implemented";
	}


	/**
	 * 
	 * @return {$jp[]} all the descendants of the given node
	 */
	_all_descendants($jp: unknown): any {
		throw "JoinPoints._all_descendants: not implemented";
	}


	/**
	 * 
	 * @return {$jp[]} all the nodes that are inside the scope of a given node
	 */
	_all_scope_nodes($jp: unknown): any{
		throw "JoinPoints._all_scope: not implemented";
	}


	/**
	 * 
	 * @return {$jp[]} all the descendants of the given node, in post order
	 */
	_all_descendants_postorder($jp: unknown): any{
		
		const descendants = [];

		for(const child of this._all_children($jp)) {
			const result = this._all_descendants_postorder_helper($jp);
			for(const resultNode of result) {
				descendants.push(resultNode);
			}
		}
		
		return descendants;
	}


	_all_descendants_postorder_helper($jp: any): any {
	
		const nodes = [];
	
		for(const child of this._all_children($jp)) {
			const postorderDescendants = this._all_descendants_postorder_helper(child);
			for(const result of postorderDescendants) {
				nodes.push(result);
			}
		}
	
		nodes.push($jp);
	
		return nodes;
	}

	/**
	 * 
	 * @return {$jp[]} the nodes inside the scope of the given node.
	*/

	scope($jp: any, jpType: any): any {
		return this._getNodes(this._all_scope_nodes.bind(this), $jp, jpType);
	}

	/**
	 * 
	 * @return {$jp[]} the children of the given node, according to the AST
	 */
	children($jp: any, jpType: any): any {
		return this._getNodes(this._all_children.bind(this), $jp, jpType);
	}

	/**
	 * 
	 * @return {$jp[]} the descendants of the given node, according to the AST, preorder traversal
	 */
	descendants($jp: any, jpType: any): any {
		return this._getNodes(this._all_descendants.bind(this), $jp, jpType);
	}

	/**
	 * 
	 * @return {$jp[]} the descendants of the given node, according to the AST, postorder traversal
	 */
	descendantsPostorder($jp: any, jpType: any) {
		return this._getNodes(this._all_descendants_postorder.bind(this), $jp, jpType);	
	}

	/**
 	* 
 	* @return {$jp[]} the nodes related with the given node, according to the search function
 	*/
	_getNodes(searchFunction: Function, $jp: any, jpType: any) {
		// TODO: This function can be optimized by using streaming
	

		if (searchFunction === undefined) {
			throw "Value searchFunction is undefined";
		  }

		if($jp === undefined) {
			return [];
		}

		Check.isJoinPoint($jp);	

		const descendants = searchFunction($jp);
	
		if(jpType === undefined) {
			return descendants;
		}	

		return this._filterNodes(descendants, jpType);
	}



	_filterNodes($jps: any, jpType: any) {

		const filteredJps = [];

		for(const $jp of $jps) {

			if(!$jp.instanceOf(jpType)) {
				continue;
			}
		
			filteredJps.push($jp);
		}
	
		return filteredJps;

	}





	/**
	 * Iterates of attributeNames, returns the first value that is not null or undefined.
	 * If no value is found for the given attributes, returns undefined
	 * 
	 */
	getAttribute($jp: any, attributeNames: Array<any>) {
		for(const attribute of attributeNames) {
			const value = $jp[attribute];
			if( value !== undefined){
				return value;
			}
		}
		
		return undefined;
	}

	/**
	 * Helper method of getAttribute which throws an exception if no value is found
	 */
	getAttributeStrict($jp: any, attributeNames: Array<any>) {
		const value = this.getAttribute($jp, attributeNames);
		
		if(value === undefined) {
			throw "Could not find any of the given attributes in " + $jp + ":" + attributeNames.join(", ");
		}

		return value;
	}


	/**
	 * Converts the join point to a string of code. Expects attribute 'code' to exist.
	 * 
	 * @param {joinpoint} $jp - join point to convert to code.
	 *
	 * @return {String} a String with the code representation of this join point.
	 */
	getCode($jp: any) {
		
		Check.isJoinPoint($jp);
		
		// Check if attribute code is defined
		//if(!Weaver.hasAttribute($jp, "code")) {
		if(!$jp.attributes.contains("code")) {	
			throw "JoinPoints.getCode(): expected attribute 'code' to exist";
		}
		
		return $jp["code"]as String;
	}
}
