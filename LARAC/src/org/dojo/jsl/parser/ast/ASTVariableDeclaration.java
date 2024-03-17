/* Generated By:JJTree: Do not edit this line. ASTVariableDeclaration.java Version 4.3 */
/*
 * JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,
 * NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true
 */
package org.dojo.jsl.parser.ast;

import java.util.HashMap;
import java.util.Optional;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import larac.LaraC;
import larac.objects.Variable;
import larac.utils.xml.AspectIRFactory;

public class ASTVariableDeclaration extends SimpleNode {

    private Optional<String> type;

    public ASTVariableDeclaration(int id) {
        super(id);
    }

    public ASTVariableDeclaration(LARAEcmaScript p, int id) {
        super(p, id);
    }

    @Override
    public void declareGlobal(LaraC lara) {

        final ASTIdentifier id = (ASTIdentifier) getChildren()[0];
        final String varName = id.value.toString();
        if (getChildren().length > 1) {
            SimpleNode init = getChild(1);
            if (init instanceof ASTAssignmentExpression) {
                throw newException("Chainned variable declaration assignments are not supported.");
            }
            init.organize(this);
            // vars.put(varName, new Variable(varName, (SimpleNode) getChildren()[1]));
        }

        lara.aspectIR().addGlobalElement(varName, this);
        Optional<ASTVariableStatement> ancestorOfType = getAncestorOfType(ASTVariableStatement.class);
        if (ancestorOfType.isPresent()) {
            jjtGetFirstToken().specialToken = ancestorOfType.get().jjtGetFirstToken().specialToken;
        }
    }

    @Override
    public Object organize(Object obj) {
        final HashMap<String, Variable> vars = ((SimpleNode) obj).getHMVars();
        // final HashMap<String, Variable> vars = getHMVars();
        if (!(getChildren()[0] instanceof ASTIdentifier)) {
            throw newException("Variable not supported: \"" + ((SimpleNode) getChildren()[0]).toString() + "\"\n");
        }
        final ASTIdentifier id = (ASTIdentifier) getChildren()[0];
        final String varName = id.value.toString();
        /*
         * if(vars.containsKey(varName)) throw newException("Variable '"
         * +varName+"' is already declared"); /
         **/if (getChildren().length > 1) {

            SimpleNode init = getChild(1);
            if (init instanceof ASTAssignmentExpression) {
                throw newException("Chainned variable declaration assignments are not supported.");
            }
            init.organize(this);
            // vars.put(varName, new Variable(varName, (SimpleNode) getChildren()[1]));
        }
        vars.put(varName, new Variable(varName));

        return null;

    }

    @Override
    public Object organize(Object obj, Object obj2) {
        if (!(obj instanceof HashMap<?, ?>)) {
            throw newException("Should have been an HashMap on ASTVariableDeclaration!\n");
        }
        @SuppressWarnings("unchecked")
        final HashMap<String, Variable> vars = (HashMap<String, Variable>) obj;
        @SuppressWarnings("unchecked")
        final HashMap<String, Variable> otherVars = (HashMap<String, Variable>) obj2;
        if (getChildren()[0] instanceof ASTIdentifier) {
            final String varName = getIdName();
            /*
             * if(vars.containsKey(varName)) throw newException("Variable '"
             * +varName+"' is already declared"); /
             **/
            if (getChildren().length > 1) {
                if (otherVars.containsKey(varName)) {
                    throw newException("Variable \"" + varName
                            + "\", used in both in Input and Output, can only be initialized on the Input.\n");
                }

                if (getChildren()[1] instanceof ASTAssignmentExpression) {
                    final ASTAssignmentExpression assignExpr = (ASTAssignmentExpression) getChild(1);
                    // final SimpleNode expr =
                    assignExpr.solveInitializeAssignment(vars);
                    // vars.put(varName, new Variable(varName));// , expr));
                } else {
                    getChild(1).organize(this);
                    // else {
                }
                // vars.put(varName, new Variable(varName));// , (SimpleNode) getChildren()[1]));
                // }
            } // else {
            /*
             * if(otherVars.containsKey(varName)) throw newException(
             * "Variable '"+varName+"' already declared"
             * );//vars.put(varName, otherVars.get(varName)); else/
             **/
            vars.put(varName, new Variable(varName));
            // }
        } else

        {
            throw newException("Variable not supported: \"" + ((SimpleNode) getChildren()[0]).toString() + "\"\n");
        }
        // ASTAspectDef aspectDef = ((ASTAspectDef)obj);
        // aspectDef.putInput(this);
        return null;

    }

    @Override
    public void toXML(Document doc, Element parent) {
        final SimpleNode init = jjtGetNumChildren() > 1 ? getChild(1) : null;
        AspectIRFactory.varDeclExprToXML(doc, parent, getIdName(), init, type);
        //// final Variable var = lookupNoError(id.getName());
        // varDeclToXML(doc, parent, var); // <--replace this invocation!
    }

    @Override
    public void globalToXML(Document doc, Element parent) {
        final Element statementDeclEl = doc.createElement("declaration");
        statementDeclEl.setAttribute("name", "vardecl");
        statementDeclEl.setAttribute("coord", getCoords());
        addXMLComent(statementDeclEl);
        parent.appendChild(statementDeclEl);
        toXML(doc, statementDeclEl);

    }

    public String getIdName() {
        ASTIdentifier id = (ASTIdentifier) jjtGetChild(0);
        return id.getName();
    }

    public Optional<String> getType() {
        return type;
    }

    public void setType(String type) {
        this.type = Optional.of(type);
    }
}
/*
 * JavaCC - OriginalChecksum=f4dddfe6c7daf45cbac10963428e4a90 (do not edit this
 * line)
 */
