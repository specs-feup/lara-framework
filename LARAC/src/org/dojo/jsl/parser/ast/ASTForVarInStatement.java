/* Generated By:JJTree: Do not edit this line. ASTForVarInStatement.java Version 4.3 */
/*
 * JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,
 * NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true
 */
package org.dojo.jsl.parser.ast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import larac.objects.Enums.LoopTypes;
import larac.objects.Variable;
import larac.utils.xml.AspectIRFactory;

public class ASTForVarInStatement extends SimpleNode {
    private boolean each = false;
    private Variable varForVarIn;

    /**
     * @return the varForVarIn
     */
    public Variable getVarForVarIn() {
        return varForVarIn;
    }

    /**
     * @param varForVarIn
     *            the varForVarIn to set
     */
    public void setVarForVarIn(Variable varForVarIn) {
        this.varForVarIn = varForVarIn;
    }

    /**/
    public ASTForVarInStatement(int id) {
        super(id);
    }

    public ASTForVarInStatement(LARAEcmaScript p, int id) {
        super(p, id);
    }

    @Override
    public Object organize(Object obj) {
        // final Map<String, Variable> vars = getHMVars();
        // if (getChild(1) instanceof ASTEmptyExpression) {
        varForVarIn = new Variable(((ASTIdentifier) jjtGetChild(0)).getName());
        // } else {
        // varForVarIn = new Variable(((ASTIdentifier) children[0]).getName(), (SimpleNode) children[1]);
        // }
        // vars.put(varForVarIn.getName(), varForVarIn);
        for (int i = 1; i < children.length; i++) {
            ((SimpleNode) children[i]).organize(obj);
        }
        return null;
    }

    @Override
    public Variable lookup(String var) {
        if (getVarForVarIn().getName().equals(var)) {
            return getVarForVarIn();
        }
        return ((SimpleNode) parent).lookup(var);
    }

    @Override
    public Variable lookupNoError(String var) {
        if (getVarForVarIn().getName().equals(var)) {
            return getVarForVarIn();
        }
        return ((SimpleNode) parent).lookupNoError(var);
    }

    @Override
    public void toXML(Document doc, Element parent) {
        final Element stmtEl = doc.createElement("statement");
        stmtEl.setAttribute("name", "loop");
        final LoopTypes loopType = isEach() ? LoopTypes.FOREACH : LoopTypes.FORIN;
        stmtEl.setAttribute("desc", loopType.toString());
        final String coords = getCoords();
        stmtEl.setAttribute("coord", coords);
        if (!label.isEmpty()) {
            stmtEl.setAttribute("label", label);
        }
        parent.appendChild(stmtEl);

        final Element initEl = doc.createElement("code");
        initEl.setAttribute("desc", "init");
        stmtEl.appendChild(initEl);
        stmtEl.setAttribute("coord", getCoords());

        SimpleNode initNode = getChild(1);
        if (!(initNode instanceof ASTEmptyExpression)) {
            AspectIRFactory.varDeclStmtToXML(doc, initEl, varForVarIn.getName(), coords);
        } else {
            AspectIRFactory.varDeclStmtToXML(doc, initEl, varForVarIn.getName(), initNode, coords, null);
        }
        // varDeclToXML(doc, statementDeclEl, getVarForVarIn());

        final Element containerEl = doc.createElement("expression");
        containerEl.setAttribute("desc", "container");
        stmtEl.appendChild(containerEl);
        ((SimpleNode) children[2]).toXML(doc, containerEl);

        final Element codeEl = doc.createElement("code");
        codeEl.setAttribute("desc", "body");
        stmtEl.appendChild(codeEl);
        ((SimpleNode) children[3]).toXML(doc, codeEl);
    }

    public boolean isEach() {
        return each;
    }

    public void setEach(boolean each) {
        this.each = each;
    }

}
/*
 * JavaCC - OriginalChecksum=f7161e8ad63b4575966d675f4c8aa0d7 (do not edit this
 * line)
 */
