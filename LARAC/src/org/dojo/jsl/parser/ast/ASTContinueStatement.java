/* Generated By:JJTree: Do not edit this line. ASTContinueStatement.java Version 4.3 */
/*
 * JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,
 * NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true
 */
package org.dojo.jsl.parser.ast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ASTContinueStatement extends SimpleNode {
	private String targetLabel = "";

	public ASTContinueStatement(int id) {
		super(id);
	}

	public ASTContinueStatement(LARAEcmaScript p, int id) {
		super(p, id);
	}

	@Override
	public Object organize(Object obj) {
		if (children != null) {
			targetLabel = ((ASTIdentifier) children[0]).value.toString();
			if (targetLabel.startsWith("#")) {
				return null;
			}
			if (lookupLabel(targetLabel) == null) {
				throw newException("Label \"" + targetLabel + "\" does not exist.");
			}
		}
		return null;
	}

	@Override
	public void toXML(Document doc, Element parent) {
		final Element continueEl = doc.createElement("statement");
		continueEl.setAttribute("name", "continue");
		continueEl.setAttribute("coord", getCoords());
		final Element exprEl = doc.createElement("expression");
		continueEl.appendChild(exprEl);
		if (!targetLabel.isEmpty()) {
			final Element idEl = doc.createElement("id");
			idEl.setAttribute("name", targetLabel);
			exprEl.appendChild(idEl);
		}
		parent.appendChild(continueEl);
	}
}
/*
 * JavaCC - OriginalChecksum=c7cac251633ec6fdde2c2b8e1d56e60c (do not edit this
 * line)
 */
