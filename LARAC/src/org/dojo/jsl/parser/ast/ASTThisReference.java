/* Generated By:JJTree: Do not edit this line. ASTThisReference.java Version 4.3 */
/*
 * JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,
 * NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true
 */
package org.dojo.jsl.parser.ast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import larac.objects.Variable;

public class ASTThisReference extends SimpleNode {
    public ASTThisReference(int id) {
        super(id);
    }

    public ASTThisReference(LARAEcmaScript p, int id) {
        super(p, id);
    }

    @Override
    public Object organize(Object obj) {
        return new Variable("this");// getParentById(LARAEcmaScriptTreeConstants.JJTASPECTDEF);
    }

    @Override
    public void toXML(Document doc, Element parent) {
        final Element thisEl = doc.createElement("id");
        thisEl.setAttribute("name", "this");
        parent.appendChild(thisEl);
    }

    @Override
    public String toSource(int indentation) {
        return indent(indentation) + "this";
    }

}
/*
 * JavaCC - OriginalChecksum=e8f88206dc48b417690bf934d8e6788e (do not edit this
 * line)
 */
