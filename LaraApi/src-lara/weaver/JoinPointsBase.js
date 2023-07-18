import { wrapJoinPoint } from "../LaraJoinPoint.js";
import Check from "../lara/Check.js";
import Weaver from "./Weaver.js";
/**
 * Object which provides low-level join point-related methods.
 */
export class JoinPointsBase {
    /**
     *
     * @returns the current root node of the AST
     */
    static root() {
        return wrapJoinPoint(Weaver.getWeaverEngine().getRootJp());
    }
    /**
     * Converts an AST node to a JointPoint.
     *
     */
    static toJoinPoint(node) {
        throw "JoinPoints.toJoinPoint: not implemented";
    }
    /**
     *
     * @returns all the children of the given node
     */
    static _all_children($jp) {
        throw "JoinPoints._all_children: not implemented";
    }
    /**
     *
     * @returns all the descendants of the given node
     */
    static _all_descendants($jp) {
        throw "JoinPoints._all_descendants: not implemented";
    }
    /**
     *
     * @returns all the nodes that are inside the scope of a given node
     */
    static _all_scope_nodes($jp) {
        throw "JoinPoints._all_scope: not implemented";
    }
    /**
     *
     * @returns all the descendants of the given node, in post order
     */
    static _all_descendants_postorder($jp) {
        const descendants = [];
        for (const child of JoinPointsBase._all_children($jp)) {
            const result = JoinPointsBase._all_descendants_postorder_helper($jp);
            descendants.push(...result);
        }
        return descendants;
    }
    static _all_descendants_postorder_helper($jp) {
        const nodes = [];
        for (const child of JoinPointsBase._all_children($jp)) {
            const postorderDescendants = JoinPointsBase._all_descendants_postorder_helper(child);
            nodes.push(...postorderDescendants);
        }
        nodes.push($jp);
        return nodes;
    }
    /**
     *
     * @returns the nodes inside the scope of the given node.
     */
    static scope($jp, jpType) {
        return JoinPointsBase._getNodes(JoinPointsBase._all_scope_nodes, $jp, jpType);
    }
    /**
     *
     * @returns the children of the given node, according to the AST
     */
    static children($jp, jpType) {
        return JoinPointsBase._getNodes(JoinPointsBase._all_children, $jp, jpType);
    }
    /**
     *
     * @returns the descendants of the given node, according to the AST, preorder traversal
     */
    static descendants($jp, jpType) {
        return JoinPointsBase._getNodes(JoinPointsBase._all_descendants, $jp, jpType);
    }
    /**
     *
     * @returns the descendants of the given node, according to the AST, postorder traversal
     */
    static descendantsPostorder($jp, jpType) {
        return JoinPointsBase._getNodes(JoinPointsBase._all_descendants_postorder, $jp, jpType);
    }
    /**
     *
     * @returns  the nodes related with the given node, according to the search function
     */
    static _getNodes(searchFunction, $jp, jpType) {
        // TODO: This function can be optimized by using streaming
        const descendants = searchFunction($jp);
        if (jpType === undefined) {
            return descendants;
        }
        return JoinPointsBase._filterNodes(descendants, jpType);
    }
    static _filterNodes($jps, jpType) {
        return $jps.filter((jp) => jp.joinPointType === jpType);
    }
    /**
     * Iterates of attributeNames, returns the first value that is not null or undefined.
     * If no value is found for the given attributes, returns undefined
     *
     * @deprecated Just don't...
     */
    getAttribute($jp, attributeNames) {
        for (const attribute of attributeNames) {
            const value = Object.getOwnPropertyDescriptor($jp, attribute)?.value;
            if (value !== undefined) {
                return value;
            }
        }
        return undefined;
    }
    /**
     * Helper method of getAttribute which throws an exception if no value is found
     *
     * @deprecated Just don't...
     */
    getAttributeStrict($jp, attributeNames) {
        const value = this.getAttribute($jp, attributeNames);
        if (value === undefined) {
            throw ("Could not find any of the given attributes in " +
                $jp.toString() +
                ":" +
                attributeNames.join(", "));
        }
        return value;
    }
    /**
     * Converts the join point to a string of code. Expects attribute 'code' to exist.
     *
     * @param $jp - join point to convert to code.
     *
     * @returns a String with the code representation of this join point.
     */
    getCode($jp) {
        Check.isJoinPoint($jp);
        // Check if attribute code is defined
        if (!$jp.attributes.includes("code")) {
            throw "JoinPoints.getCode(): expected attribute 'code' to exist";
        }
        return Object.getOwnPropertyDescriptor($jp, "code")?.value;
    }
}
//# sourceMappingURL=JoinPointsBase.js.map