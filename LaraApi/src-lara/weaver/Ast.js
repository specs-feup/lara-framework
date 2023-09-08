import Weaver from "./Weaver.js";
/**
 * Contains utility methods related to the AST provided by the LARA compiler.
 *
 * @deprecated This class deals with AST nodes. You should never use AST nodes directly. Use the provided join points instead. If you need this class, you are doing something wrong.
 */
export default class Ast {
    /**
     *
     * @returns An array with the children AST nodes of the given AST node.
     */
    static getChildren(astNode) {
        return Weaver.AST_METHODS.getChildren(astNode);
    }
    /**
     *
     * @returns The parent AST node of the given AST node.
     */
    static getParent(astNode) {
        return Weaver.AST_METHODS.getParent(astNode);
    }
    /**
     * @returns The number of children of the AST node.
     */
    static getNumChildren(astNode) {
        return Weaver.AST_METHODS.getNumChildren(astNode);
    }
    /**
     *
     * @returns The current root node of the static
     */
    static root() {
        return Weaver.AST_METHODS.getRoot();
    }
    /**
     *
     * @returns An array with the descendants AST nodes of the given AST node.
     */
    static getDescendants(astNode) {
        return Weaver.AST_METHODS.getDescendants(astNode);
    }
    /**
     *
     * @returns The child of the node at the given index.
     */
    static getChild(astNode, index) {
        return Ast.getChildren(astNode)[index];
    }
    /**
     * Converts the given AST node to the native join point supported by the current LARA compiler.
     *
     * @returns A native join point, as opposed to the join point of the current language specification.
     *
     * @deprecated You should never use ast nodes directly. If you need this method, you are doing something wrong.
     */
    static toWeaverJoinPoint(astNode) {
        return Weaver.AST_METHODS.toJavaJoinPoint(astNode);
    }
}
//# sourceMappingURL=Ast.js.map