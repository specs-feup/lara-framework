import Check from "../lara/Check.js";
import Weaver from "./Weaver.js";
/**
 * Object which provides low-level join point-related methods.
 * @class
 */
export class JoinPointsBase {
    /**
     *
     * @return {$jp} the current root node of the AST
     */
    static root() {
        return Weaver.getWeaverEngine().getRootJp();
    }
    /**
    * Converts an AST node to a JointPoint.
    *
    * @param {node}
    * @return {$jp}
    */
    static toJoinPoint(node) {
        throw "JoinPoints.toJoinPoint: not implemented";
    }
    /**
     *
     * @return {$jp[]} all the children of the given node
     */
    static _all_children($jp) {
        throw "JoinPoints._all_children: not implemented";
    }
    /**
     *
     * @return {$jp[]} all the descendants of the given node
     */
    static _all_descendants($jp) {
        throw "JoinPoints._all_descendants: not implemented";
    }
    /**
     *
     * @return {$jp[]} all the nodes that are inside the scope of a given node
     */
    static _all_scope_nodes($jp) {
        throw "JoinPoints._all_scope: not implemented";
    }
    /**
     *
     * @return {$jp[]} all the descendants of the given node, in post order
     */
    static _all_descendants_postorder($jp) {
        const descendants = [];
        for (const child of JoinPointsBase._all_children($jp)) {
            const result = JoinPointsBase._all_descendants_postorder_helper($jp);
            for (const resultNode of result) {
                descendants.push(resultNode);
            }
        }
        return descendants;
    }
    static _all_descendants_postorder_helper($jp) {
        const nodes = [];
        for (const child of JoinPointsBase._all_children($jp)) {
            const postorderDescendants = JoinPointsBase._all_descendants_postorder_helper(child);
            for (const result of postorderDescendants) {
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
    static scope($jp, jpType) {
        return JoinPointsBase._getNodes(JoinPointsBase._all_scope_nodes, $jp, jpType);
    }
    /**
     *
     * @return {$jp[]} the children of the given node, according to the AST
     */
    static children($jp, jpType) {
        return JoinPointsBase._getNodes(JoinPointsBase._all_children, $jp, jpType);
    }
    /**
     *
     * @return {$jp[]} the descendants of the given node, according to the AST, preorder traversal
     */
    static descendants($jp, jpType) {
        return JoinPointsBase._getNodes(JoinPointsBase._all_descendants, $jp, jpType);
    }
    /**
     *
     * @return {$jp[]} the descendants of the given node, according to the AST, postorder traversal
     */
    static descendantsPostorder($jp, jpType) {
        return JoinPointsBase._getNodes(JoinPointsBase._all_descendants_postorder, $jp, jpType);
    }
    /**
    *
    * @return {$jp[]} the nodes related with the given node, according to the search function
    */
    static _getNodes(searchFunction, $jp, jpType) {
        // TODO: This function can be optimized by using streaming
        if (searchFunction === undefined) {
            throw "Value searchFunction is undefined";
        }
        if ($jp === undefined) {
            return [];
        }
        Check.isJoinPoint($jp);
        const descendants = searchFunction($jp);
        if (jpType === undefined) {
            return descendants;
        }
        return JoinPointsBase._filterNodes(descendants, jpType);
    }
    static _filterNodes($jps, jpType) {
        const filteredJps = [];
        for (const $jp of $jps) {
            if (!$jp.instanceOf(jpType)) {
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
    getAttribute($jp, attributeNames) {
        for (const attribute of attributeNames) {
            const value = $jp[attribute];
            if (value !== undefined) {
                return value;
            }
        }
        return undefined;
    }
    /**
     * Helper method of getAttribute which throws an exception if no value is found
     */
    getAttributeStrict($jp, attributeNames) {
        const value = this.getAttribute($jp, attributeNames);
        if (value === undefined) {
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
    getCode($jp) {
        Check.isJoinPoint($jp);
        // Check if attribute code is defined
        //if(!Weaver.hasAttribute($jp, "code")) {
        if (!$jp.attributes.contains("code")) {
            throw "JoinPoints.getCode(): expected attribute 'code' to exist";
        }
        return $jp["code"];
    }
}
//# sourceMappingURL=JoinPointsBase.js.map