import { wrapJoinPoint } from "../LaraJoinPoint.js";
import Weaver from "./Weaver.js";
/**
 * Object which provides low-level join point-related methods.
 */
export default class JoinPoints {
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
        throw new Error("JoinPoints.toJoinPoint: not implemented");
    }
    /**
     *
     * @returns all the children of the given node
     */
    static _all_children($jp) {
        return $jp.children;
    }
    /**
     *
     * @returns all the descendants of the given node
     */
    static _all_descendants($jp) {
        return $jp.descendants;
    }
    /**
     *
     * @returns all the nodes that are inside the scope of a given node
     */
    static _all_scope_nodes($jp) {
        return $jp.scopeNodes;
    }
    /**
     *
     * @returns all the descendants of the given node, in post order
     */
    static _all_descendants_postorder($jp) {
        const descendants = [];
        for (const child of JoinPoints._all_children($jp)) {
            const result = JoinPoints._all_descendants_postorder_helper($jp);
            descendants.push(...result);
        }
        return descendants;
    }
    static _all_descendants_postorder_helper($jp) {
        const nodes = [];
        for (const child of JoinPoints._all_children($jp)) {
            const postorderDescendants = JoinPoints._all_descendants_postorder_helper(child);
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
        return JoinPoints._getNodes(jpType, JoinPoints._all_scope_nodes($jp));
    }
    /**
     *
     * @returns the children of the given node, according to the AST
     */
    static children($jp, jpType) {
        return JoinPoints._getNodes(jpType, JoinPoints._all_children($jp));
    }
    /**
     *
     * @returns the descendants of the given node, according to the AST, preorder traversal
     */
    static descendants($jp, jpType) {
        return JoinPoints._getNodes(jpType, JoinPoints._all_descendants($jp));
    }
    /**
     *
     * @returns the descendants of the given node, according to the AST, postorder traversal
     */
    static descendantsPostorder($jp, jpType) {
        return JoinPoints._getNodes(jpType, JoinPoints._all_descendants_postorder($jp));
    }
    /**
     *
     * @returns  the nodes related with the given node, according to the search function
     */
    static _getNodes(jpType, $allJps = []) {
        // TODO: This function can be optimized by using streaming
        if (jpType === undefined) {
            return $allJps;
        }
        return $allJps.filter((jp) => jp instanceof jpType);
    }
    /**
     * Iterates of attributeNames, returns the first value that is not null or undefined.
     * If no value is found for the given attributes, returns undefined
     *
     * @deprecated Just don't...
     */
    static getAttribute($jp, attributeNames) {
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
    static getAttributeStrict($jp, attributeNames) {
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
    static getCode($jp) {
        const property = "code";
        for (let obj = $jp; obj !== null; obj = Object.getPrototypeOf(obj)) {
            const descriptor = Object.getOwnPropertyDescriptor(obj, property);
            if (descriptor !== undefined) {
                let attributeValue = undefined;
                if (Object.getOwnPropertyDescriptor(descriptor, "get")) {
                    return descriptor.get?.call?.($jp);
                }
                else if (Object.getOwnPropertyDescriptor(descriptor, "value")) {
                    return descriptor.value;
                }
                else {
                    continue;
                }
            }
        }
        throw "JoinPoints.getCode(): expected attribute 'code' to exist";
    }
}
//# sourceMappingURL=JoinPoints.js.map