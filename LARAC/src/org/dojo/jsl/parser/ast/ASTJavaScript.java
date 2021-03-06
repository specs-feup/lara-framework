/* Generated By:JJTree: Do not edit this line. ASTJavaScript.java Version 4.3 */
/*
 * JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,
 * NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true
 */
package org.dojo.jsl.parser.ast;

import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import larac.objects.SymbolTable;
import larac.objects.Variable;

public class ASTJavaScript extends SimpleNode {
	private SymbolTable<String, Variable> localVars = new SymbolTable<>();

	public ASTJavaScript(int id) {
		super(id);
	}

	public ASTJavaScript(LARAEcmaScript p, int id) {
		super(p, id);
	}

	@Override
	public Object organize(Object obj) {
		if (getChildren() != null) {
			for (final Node child : getChildren()) {
				((SimpleNode) child).organize(obj);
			}
		}
		return null;
	}

	@Override
	public void toXML(Document doc, Element parent) {
		// createXMLDecl(localVars.values(), doc, parent);

		if (getChildren() != null) {
			for (final Node node : getChildren()) {
				((SimpleNode) node).toXML(doc, parent);
			}
		}
	}

	/**
	 * @param localVars
	 *            the localVars to set
	 */
	public void setLocalVars(SymbolTable<String, Variable> localVars) {
		this.localVars = localVars;
	}

	/**
	 * @return the localVars
	 */
	public SymbolTable<String, Variable> getLocalVars() {
		return localVars;
	}

	@Override
	public Variable lookup(String var) {
		if (localVars.containsKey(var)) {
			return localVars.get(var);
		}
		return ((SimpleNode) parent).lookup(var);
	}

	@Override
	public Variable lookupNoError(String var) {

		if (localVars.containsKey(var)) {
			return localVars.get(var);
		}

		return ((SimpleNode) parent).lookupNoError(var);
	}

	@Override
	public HashMap<String, Variable> getHMVars() {
		return localVars;
	}
}
/*
 * JavaCC - OriginalChecksum=f7b7ff61dae1ae6df2aaefd3fd88c256 (do not edit this
 * line)
 */
