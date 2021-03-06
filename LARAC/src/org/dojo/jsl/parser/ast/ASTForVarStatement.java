/* Generated By:JJTree: Do not edit this line. ASTForVarStatement.java Version 4.3 */
/*
 * JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,
 * NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true
 */
package org.dojo.jsl.parser.ast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import larac.objects.Enums;

public class ASTForVarStatement extends SimpleNode {
	// private SymbolTable<String, Variable> localVars = new SymbolTable<String,
	// Variable>();
	public ASTForVarStatement(int id) {
		super(id);
	}

	public ASTForVarStatement(LARAEcmaScript p, int id) {
		super(p, id);
	}

	@Override
	public Object organize(Object obj) {

		for (int i = 0; i < children.length; i++) {
			((SimpleNode) children[i]).organize(this);
		}
		return null;
	}

	@Override
	public void toXML(Document doc, Element parent) {

		final Element statEl = doc.createElement("statement");
		parent.appendChild(statEl);
		final String coords = getCoords();
		statEl.setAttribute("name", "loop");
		statEl.setAttribute("desc", Enums.LoopTypes.FOR.toString());
		statEl.setAttribute("coord", coords);
		if (!label.isEmpty()) {
			statEl.setAttribute("label", label);
		}

		final Element initEl = doc.createElement("code");
		statEl.appendChild(initEl);
		initEl.setAttribute("desc", "init");
		final Element varDeclEl = doc.createElement("statement");
		varDeclEl.setAttribute("name", "vardecl");
		varDeclEl.setAttribute("coord", coords);
		initEl.appendChild(varDeclEl);
		((SimpleNode) children[0]).toXML(doc, varDeclEl);
		// createXMLDecl(localVars.values(), doc, initEl);

		final Element conditionEl = doc.createElement("expression");
		statEl.appendChild(conditionEl);
		conditionEl.setAttribute("desc", "condition");
		((SimpleNode) children[1]).toXML(doc, conditionEl);

		final Element codeEl = doc.createElement("code");
		statEl.appendChild(codeEl);
		codeEl.setAttribute("desc", "body");
		((SimpleNode) children[3]).toXML(doc, codeEl);

		final Element exprItEl = doc.createElement("expression");
		exprItEl.setAttribute("desc", "next-iterator");
		statEl.appendChild(exprItEl);
		((SimpleNode) children[2]).toXML(doc, exprItEl);
	}
	/*
	 * public Variable lookup(String var) { if(localVars.containsKey(var))
	 * return localVars.get(var); return ((SimpleNode)parent).lookup(var); }
	 * 
	 * public Variable lookupNoError(String var) {
	 * if(localVars.containsKey(var)) return localVars.get(var); return
	 * ((SimpleNode)parent).lookupNoError(var); }
	 * 
	 * @Override public HashMap<String, Variable> getHMVars() { return
	 * localVars; }
	 */
}
/*
 * JavaCC - OriginalChecksum=275faf88f67bd8be5cce441ad4c2c51a (do not edit this
 * line)
 */
