/* Generated By:JJTree: Do not edit this line. ASTConditionalExpression.java Version 4.3 */
/*
 * JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,
 * NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true
 */
package org.dojo.jsl.parser.ast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ASTConditionalExpression extends SimpleNode {
	public ASTConditionalExpression(int id) {
		super(id);
	}

	public ASTConditionalExpression(LARAEcmaScript p, int id) {
		super(p, id);
	}

	@Override
	public Object organize(Object obj) {
		for (final Node child : children) {
			((SimpleNode) child).organize(obj);
		}
		return null;
	}

	@Override
	public void toXML(Document doc, Element parent) {
		final Element opEl = doc.createElement("op");
		opEl.setAttribute("name", "COND");
		parent.appendChild(opEl);
		for (final Node child : children) {
			((SimpleNode) child).toXML(doc, opEl);
		}
	}
}
/*
 * JavaCC - OriginalChecksum=f10fcc7a136035db53470e9701d1a475 (do not edit this
 * line)
 */
