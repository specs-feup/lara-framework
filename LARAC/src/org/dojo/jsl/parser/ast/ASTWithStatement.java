/* Generated By:JJTree: Do not edit this line. ASTWithStatement.java Version 4.3 */
/*
 * JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,
 * NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true
 */
package org.dojo.jsl.parser.ast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import larac.objects.Variable;

public class ASTWithStatement extends SimpleNode {
	public ASTWithStatement(int id) {
		super(id);
	}

	public ASTWithStatement(LARAEcmaScript p, int id) {
		super(p, id);
	}

	@Override
	public Object organize(Object obj) {
		for (final Node child : getChildren()) {
			((SimpleNode) child).organize(obj);
		}
		return null;
	}

	@Override
	public void toXML(Document doc, Element parent) {
		final Element statEl = doc.createElement("statement");
		statEl.setAttribute("name", "with");
		statEl.setAttribute("coord", getCoords());
		if (!label.isEmpty()) {
			statEl.setAttribute("label", label);
		}
		parent.appendChild(statEl);

		final Element exprEl = doc.createElement("expression");
		statEl.appendChild(exprEl);
		((SimpleNode) children[0]).toXML(doc, exprEl);
		final Element codeEl = doc.createElement("code");
		statEl.appendChild(codeEl);
		((SimpleNode) children[1]).toXML(doc, codeEl);
	}

	@Override
	public Variable lookup(String var) {
		return new Variable(var);
	}

	@Override
	public Variable lookupNoError(String var) {

		Variable retVar = super.lookupNoError(var);
		if (retVar == null) {
			retVar = new Variable(var);
		}
		return retVar;
	}
}
/*
 * JavaCC - OriginalChecksum=78f16403bd7e266f0ec510844f91e919 (do not edit this
 * line)
 */
