import weaver.Weaver;

/**
 * Contains utility methods related to the AST provided by the LARA compiler. 
 *
 * @class
 */
var Ast = {};


/** 
 *
 * @return {Object[]} an array with the children AST nodes of the given AST node.
 */
Ast.getChildren = function(astNode) {
	return Weaver.AST_METHODS.getChildren(astNode);
}

/** 
 *
 * @return {Object} the parent AST node of the given AST node.
 */
Ast.getParent = function(astNode) {
	return Weaver.AST_METHODS.getParent(astNode);
}


/**
 * @return {integer} the number of children of the AST node.
 */
Ast.getNumChildren = function(astNode) {
	return Weaver.AST_METHODS.getNumChildren(astNode);
}


/**
 * 
 * @return {Object} the current root node of the AST.
 */
Ast.root = function() {
	return Weaver.AST_METHODS.getRoot();
}

/** 
 *
 * @return {Object[]} an array with the descendants AST nodes of the given AST node.
 */
Ast.getDescendants = function(astNode, accum) {

	return Weaver.AST_METHODS.getDescendants(astNode);
/*
    accum = accum || [];
    for(child of Ast.getChildren(astNode)){
    	accum.push(child);
    	Ast.getDescendants(child,accum);
    }
	return accum;
*/	
}


/** 
 *
 * @return {Object} the child of the node at the given index.
 */
Ast.getChild = function(astNode, index) {
	return Ast.getChildren[index];
}

/**
 * Converts the given AST node to the native join point supported by the current LARA compiler.
 *
 * @return {$jp} a native join point, as opposed to the join point of the current language specification.
 */
Ast.toWeaverJoinPoint = function(astNode) {
	return Weaver.AST_METHODS.toJavaJoinPoint(astNode);
}