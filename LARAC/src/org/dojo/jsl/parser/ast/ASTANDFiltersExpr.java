/* Generated By:JJTree: Do not edit this line. ASTANDFiltersExpr.java Version 4.3 */
/*
 * JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,
 * NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true
 */
package org.dojo.jsl.parser.ast;

import org.lara.language.specification.artifactsmodel.ArtifactsModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ASTANDFiltersExpr extends SimpleNode {
	public ASTANDFiltersExpr(int id) {
		super(id);
	}

	public ASTANDFiltersExpr(LARAEcmaScript p, int id) {
		super(p, id);
	}

	@Override
	public String organize(String type, ArtifactsModel artifacts) {
		// ArrayList<String> props = new ArrayList<String>();
		for (final Node child : getChildren()) {
			// String s =
			((SimpleNode) child).organize(type, artifacts);
			/*
			 * WHY? if (props.contains(s)) { SimpleNode pcParent =
			 * getParentById(LARAEcmaScriptTreeConstants.JJTPOINTCUT); throw
			 * newException("In joinpoint \"" + pcParent.jjtGetValue() +
			 * "\": duplicate attribute used in an AND expression: \"\"" + s +
			 * "\""); } props.add(s);
			 */
		}
		return null;
	}

	@Override
	public Element getFilterElement(Document doc) {
		Element aux = null;
		boolean first = true;
		Element jpANDEl = null;
		for (final Node child : getChildren()) {
			final Element childEl = ((SimpleNode) child).getFilterElement(doc);
			if (childEl != null) {
				if (first) {
					aux = childEl;
					first = false;
				} else {
					if (jpANDEl == null) {
						jpANDEl = doc.createElement("op");
						jpANDEl.setAttribute("name", "AND");
						jpANDEl.appendChild(aux);
					}
					jpANDEl.appendChild(childEl);
				}
			}
		}
		if (jpANDEl != null) {
			return jpANDEl;
		}
		return aux;
	}
}
/*
 * JavaCC - OriginalChecksum=61255ba5f4f54f385b3c8ad768398374 (do not edit this
 * line)
 */
