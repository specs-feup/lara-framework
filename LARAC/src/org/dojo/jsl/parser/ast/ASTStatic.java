/* Generated By:JJTree: Do not edit this line. ASTStatic.java Version 4.3 */
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

public class ASTStatic extends SimpleNode {
	private SymbolTable<String, Variable> localVars = new SymbolTable<>();

	public ASTStatic(int id) {
		super(id);
	}

	public ASTStatic(LARAEcmaScript p, int id) {
		super(p, id);
	}

	@Override
	public Object organize(Object obj) {
		if (getChildren() == null) {
			return null;
		}
		if (((ASTAspectDef) parent).getStatic() != null) {
			throw newException("Can only have one static section");
		}
		((ASTAspectDef) parent).setStatic(this);
		for (final Node child : getChildren()) {
			((SimpleNode) child).organize(this);
		}
		return null;
	}

	@Override
	public void toXML(Document doc, Element parent) {
		if (localVars.isEmpty()) {
			return;
		}
		final Element staticEl = doc.createElement("static");
		parent.appendChild(staticEl);
		// createXMLDecl(localVars.values(),doc,staticEl);
		for (final Node child : children) {
			final SimpleNode sn = (SimpleNode) child;
			sn.toXML(doc, staticEl);
		}
	}

	/**
	 * @return the localVars
	 */
	public SymbolTable<String, Variable> getLocalVars() {
		return localVars;
	}

	@Override
	public HashMap<String, Variable> getHMVars() {
		return localVars;
	}

	/**
	 * @param localVars
	 *            the localVars to set
	 */
	public void setLocalVars(SymbolTable<String, Variable> localVars) {
		this.localVars = localVars;
	}

	@Override
	public Variable lookup(String var) {
		if (localVars.containsKey(var)) {
			return localVars.get(var);
		}
		return ((SimpleNode) ((SimpleNode) parent).parent).lookupNoError(var); // grandfather
	}

	public Variable lookupStatic(String var) {
		if (localVars.containsKey(var)) {
			return localVars.get(var);
		}
		return null;
	}

	@Override
	public Variable lookupNoError(String var) {
		if (localVars.containsKey(var)) {
			return localVars.get(var);
		}
		return ((SimpleNode) ((SimpleNode) parent).parent).lookupNoError(var); // grandfather
	}

	public Variable lookupNoErrorStatic(String var) {
		if (localVars.containsKey(var)) {
			return localVars.get(var);
		}
		return null;
	}
}
/*
 * JavaCC - OriginalChecksum=5e81d4475827eff1b045f39e6f06f00f (do not edit this
 * line)
 */
