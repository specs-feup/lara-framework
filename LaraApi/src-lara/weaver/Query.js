import JoinPoints from "./JoinPoints.js";
import Selector from "./Selector.js";
import TraversalType from "./TraversalType.js";
/**
 * Class for selection of join points. Provides an API similar to the keyword 'select'.
 *
 * Search functions of this class return weaver.Selector objects, please refer to that class for more details regarding available functions and search options.
 *
 */
export default class Query {
    /**
     * Returns the root node of the current AST.
     *
     * @returns The root node
     */
    static root() {
        return JoinPoints.root();
    }
    static search(type, filter, traversal = TraversalType.PREORDER) {
        if (typeof type === "string") {
            return new Selector().search(type, filter, traversal);
        }
        else {
            return new Selector().search(type, filter, traversal);
        }
    }
    static searchFrom($baseJp, type, filter, traversal = TraversalType.PREORDER) {
        if (typeof type === "string") {
            return new Selector($baseJp).search(type, filter, traversal);
        }
        else {
            return new Selector($baseJp).search(type, filter, traversal);
        }
    }
    static searchFromInclusive($baseJp, type, filter, traversal = TraversalType.PREORDER) {
        if (typeof type === "string") {
            return new Selector($baseJp, true).search(type, filter, traversal);
        }
        else {
            return new Selector($baseJp, true).search(type, filter, traversal);
        }
    }
    static childrenFrom($baseJp, type, filter) {
        if (typeof type === "string") {
            return new Selector($baseJp).children(type, filter);
        }
        else {
            return new Selector($baseJp).children(type, filter);
        }
    }
    static scopeFrom($baseJp, type, filter) {
        if (typeof type === "string") {
            return new Selector($baseJp).scope(type, filter);
        }
        else {
            return new Selector($baseJp).scope(type, filter);
        }
    }
}
//# sourceMappingURL=Query.js.map