/* Generated By:JJTree: Do not edit this line. ASTOrFiltersExpr.java Version 4.3 */
/*
 * JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,
 * NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true
 */
package org.dojo.jsl.parser.ast;

import org.lara.language.specification.dsl.LanguageSpecification;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ASTOrFiltersExpr extends SimpleNode {
    public ASTOrFiltersExpr(int id) {
        super(id);
    }

    public ASTOrFiltersExpr(LARAEcmaScript p, int id) {
        super(p, id);
    }

    @Override
    public String organize(String type, LanguageSpecification langSpec) {
        for (final Node child : getChildren()) {
            ((SimpleNode) child).organize(type, langSpec);
        }
        return null;
    }

    @Override
    public Element getFilterElement(Document doc) {
        Element aux = null;
        boolean first = true;
        Element jpOREl = null;

        for (final Node child : getChildren()) {
            final Element childEl = ((SimpleNode) child).getFilterElement(doc);
            if (childEl != null) {
                if (first) {
                    aux = childEl;
                    first = false;
                } else {
                    if (jpOREl == null) {
                        jpOREl = doc.createElement("op");
                        jpOREl.setAttribute("name", "OR");
                        jpOREl.appendChild(aux);
                    }
                    jpOREl.appendChild(childEl);
                }
            }
        }
        if (jpOREl != null) {
            return jpOREl;
        }
        return aux;
    }
}
/*
 * JavaCC - OriginalChecksum=3506f10ce2f5a7c50c491a5b58d86e9b (do not edit this
 * line)
 */
