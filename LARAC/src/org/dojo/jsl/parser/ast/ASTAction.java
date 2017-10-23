/* Generated By:JJTree: Do not edit this line. ASTAction.java Version 4.3 */
/*
 * JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,
 * NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true
 */
package org.dojo.jsl.parser.ast;

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import larac.objects.Enums.Types;
import larac.utils.xml.entity.ActionArgument;

public class ASTAction extends SimpleNode {
    private String method = "";
    private Map<String, ActionArgument> arguments = null;
    private String varName = null;

    public ASTAction(int id) {
        super(id);
    }

    public ASTAction(LARAEcmaScript p, int id) {
        super(p, id);
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, ActionArgument> getArguments() {
        return arguments;
    }

    public void setArguments(Map<String, ActionArgument> arguments) {
        this.arguments = arguments;
    }

    @Override
    public Object organize(Object obj) {
        /*
         * if(select == null) throw newException(
         * "Cannot invoke this action(s) outside of an apply statement. \n" +
         * "First select the pointcut, such as: select "
         * +(value!=null?value.toString():"<pointcut>")+" end");
         */
        ((SimpleNode) getChildren()[0]).organize(obj);
        if (children.length > 1) {
            ((SimpleNode) getChildren()[1]).organize(obj);
        }
        return null;
    }

    @Override
    public void toXML(Document doc, Element parent) {
        final Element statEl = doc.createElement("statement");
        statEl.setAttribute("coord", getCoords());
        parent.appendChild(statEl);

        final Element exprEl = doc.createElement("expression");
        exprEl.setAttribute("desc", "action");
        final Element callEl = doc.createElement("call");
        exprEl.appendChild(callEl);
        final Element methodEl = doc.createElement("method");
        callEl.appendChild(methodEl);

        /*
         * if(children.length == 1){ ASTApply apl = (ASTApply)
         * getParentById(LARAEcmaScriptTreeConstants.JJTAPPLY); Variable v =
         * apl.getActiveSelect().lookupLastJPVariable(); String pc =
         * v.getName(); Element idEl = doc.createElement("id");
         * idEl.setAttribute("name", pc); propEl.appendChild(idEl); }else
         * ((SimpleNode)getChildren()[0]).toXML(doc, propEl); /
         **/
        if (children.length != 1) {
            final Element propEl = doc.createElement("property");
            methodEl.appendChild(propEl);
            ((SimpleNode) getChildren()[0]).toXML(doc, propEl);
            final Element litEl = doc.createElement("literal");
            litEl.setAttribute("value", method);
            litEl.setAttribute("type", Types.String.toString());
            propEl.appendChild(litEl);
        } else {
            final Element idEl = doc.createElement("id");
            idEl.setAttribute("name", method);
            methodEl.appendChild(idEl);
        }
        for (final ActionArgument arg : arguments.values()) {
            SimpleNode argValueNode = arg.getValue();
            if (argValueNode == null) {
                throw newException("Argument '" + arg.getName() + "' for action '" + method + "' must be defined.");
            }
            final Element argEl = doc.createElement("argument");
            callEl.appendChild(argEl);
            // if (arg.getType().equals("template")) {
            // argValueNode.toXMLTemplate(doc, argEl);
            // } else {
            argValueNode.toXML(doc, argEl);

            // }
        }

        if (varName != null) {
            statEl.setAttribute("name", "vardecl");

            final Element nameExprEl = doc.createElement("expression");
            statEl.appendChild(nameExprEl);
            // if(var.getName().startsWith("$"))
            // nameExprEl.setAttribute("type", Types.Joinpoint.toString());
            final Element literalNameEl = doc.createElement("literal");
            literalNameEl.setAttribute("value", varName);
            literalNameEl.setAttribute("type", Types.String.toString());
            nameExprEl.appendChild(literalNameEl);
            // final Element initEl = doc.createElement("expression");
            // initEl.setAttribute("desc", "init");
            // statEl.appendChild(initEl);
            // initEl.appendChild(exprEl);
        } else {
            statEl.setAttribute("name", "expr");
        }
        statEl.appendChild(exprEl);

    }

    @Override
    public void secondOrganize(ASTSelect sel) {
        ((SimpleNode) getChildren()[0]).secondOrganize(sel);
        if (children.length > 1) {
            ((SimpleNode) getChildren()[1]).secondOrganize(sel);
        }
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }
}
/*
 * JavaCC - OriginalChecksum=f7d66d4d016739e22aba1a5140aba067 (do not edit this
 * line)
 */
