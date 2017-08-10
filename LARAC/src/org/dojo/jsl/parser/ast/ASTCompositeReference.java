/* Generated By:JJTree: Do not edit this line. ASTCompositeReference.java Version 4.3 */
/*
 * JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,
 * NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true
 */
package org.dojo.jsl.parser.ast;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import larac.LaraC;
import larac.objects.Enums.Types;
import larac.objects.Variable;
import larac.utils.output.MessageConstants;

public class ASTCompositeReference extends SimpleNode {
    private int endOfFirst = 0;

    private final ArrayList<String> codeParams = new ArrayList<>();
    private final LinkedHashMap<String, SimpleNode> codeMapping = new LinkedHashMap<>();

    public ASTCompositeReference(int id) {
        super(id);
    }

    public ASTCompositeReference(LARAEcmaScript p, int id) {
        super(p, id);
    }

    @Override
    public Object organize(Object obj) {
        // if (isTemplate) {
        // if (!(children[0] instanceof ASTIdentifier) || !(children[1] instanceof ASTFunctionCallParameters)) {
        // throw newException(
        // "When using actions, parameters of type 'template' can only be of type: \n\t-> literal string\n\t-> literal
        // code\n\t-> codedef");
        // }
        // final String codeName = ((ASTIdentifier) jjtGetChild(0)).value.toString();
        // codeDef = getLara().aspectIR().getCodedef(codeName);
        // if (codeDef == null) {
        // throw newException("Codedef '" + codeName + "' does not exist.");
        // }
        //
        // for (final String arg : codeDef.getArguments()) {
        // codeMapping.put(arg, null);
        // }
        // final ASTFunctionCallParameters params = (ASTFunctionCallParameters) jjtGetChild(1);
        // organizeArguments(params);
        // return obj;
        // }
        final LaraC lara = getLara();
        // if (parent instanceof ASTExpressionStatement)
        // if (actionReview())
        // return obj;
        if (!(children[0] instanceof ASTCompositeReference)) {
            lara.aspectIR().setCompositeReference(this);
        }
        int pos = 0;
        if ((children[0] instanceof ASTThisReference)) {
            pos = 1;
        }
        final Object childObj = ((SimpleNode) children[pos]).organize(obj);
        lara.aspectIR().setCompositeReference(null);
        if (childObj instanceof ASTPointcut && children.length == 2) {
            final ASTPointcut pc = (ASTPointcut) childObj;
            if (parent instanceof ASTCompositeReference) {
                if (((ASTCompositeReference) parent).children[1] instanceof ASTFunctionCallParameters) {
                    ((SimpleNode) children[1]).organizeActionReference(pc);
                }
            } else {
                ((SimpleNode) children[1]).organizePointcutReference(pc);
            }
        } else {
            for (int i = pos + 1; i < children.length; i++) {
                ((SimpleNode) children[i]).organize(obj);
            }
        }

        return obj;
    }

    // private boolean actionReview() {
    // if (children[children.length - 1] instanceof ASTFunctionCallParameters) {
    // SimpleNode methodId = (SimpleNode) children[children.length - 2];
    // ASTFunctionCallParameters params = (ASTFunctionCallParameters)
    // children[children.length - 1];
    // LaraC lara = getLara();
    // if (methodId instanceof ASTCompositeReference) {
    // methodId = (SimpleNode) methodId.children[methodId.children.length - 1];
    // }
    // if (methodId instanceof ASTPropertyIdentifierReference) {
    // methodId = (SimpleNode) methodId.children[0];
    // }
    // if (methodId instanceof ASTIdentifier) {
    // String method = ((ASTIdentifier) methodId).getName();
    // LanguageSpecification languageSpec = lara.languageSpec();
    // ActionModel actionModel = languageSpec.getActionModel();
    // if (actionModel.contains(method)) {
    // Map<String, ActionArgument> actionParam = Utils.createPerformParameters(
    // actionModel, method);
    // ASTFunctionCallParameters newParams = new ASTFunctionCallParameters(
    // LARAEcmaScriptTreeConstants.JJTFUNCTIONCALLPARAMETERS);
    // if (params.areNamed) {
    // for (Node param : params.getChildren()) {
    // ASTNamedArgument na = (ASTNamedArgument) param;
    // if (!actionParam.containsKey(na.value))
    // throw newException("The argument '" + na.value + "' does not exist for
    // action '"
    // + method + "'");
    // ActionArgument actArg = actionParam.get(na.value);
    // actArg.setValue((SimpleNode) na.jjtGetChild(0));
    // }
    // } else {
    // if (params.children == null) {
    // if (actionParam.size() > 0) {
    // int given = 0;
    // String verb = given == 1 ? "was" : "were";
    // throw newException("Illegal number of arguments in action '" + method +
    // ". '"
    // + given + "' " + verb + " given, Expected '"
    // + actionParam.size() + "'");
    // }
    // } else if ((params.children.length != actionParam.size())) {
    // int given = params.children.length;
    // String verb = given == 1 ? "was" : "were";
    // throw newException("Illegal number of arguments in action '" + method +
    // ". '" + given
    // + "' " + verb + " given, Expected '" + actionParam.size() + "'");
    // }
    // int i = 0;
    // for (ActionArgument arg : actionParam.values()) {
    // arg.setValue((SimpleNode) params.jjtGetChild(i++));
    // }
    // }
    // int i = 0;
    // for (ActionArgument arg : actionParam.values()) {
    // if (arg.getValue() == null)
    // throw newException("Argument '" + arg.getName() + "' for action '" +
    // method
    // + "' must be defined.");
    // if (arg.getType().equals("template"))
    // arg.getValue().isTemplate = true;
    // arg.getValue().organize(this);
    // newParams.associateChild(arg.getValue(), i++);
    // }
    // associateChild(newParams, children.length - 1);
    // if (children.length - 2 == 0)
    // return true;
    // }
    // }
    // }
    // return false;
    // }

    @Override
    public void secondOrganize(ASTSelect sel) {
        ((SimpleNode) children[0]).secondOrganize(sel);
        /*
         * for(int i = 1; i < children.length; i++)
         * ((SimpleNode)children[i]).secondOrganize(sel);
         */
    }

    @Override
    public void toXML(Document doc, Element parent) {
        final SimpleNode firstChild = (SimpleNode) children[0];
        Element firstEl = doc.createElement("temp");
        firstChild.toXML(doc, firstEl);
        firstEl = (Element) firstEl.getChildNodes().item(0);

        for (int i = endOfFirst + 1; i < children.length; i++) {
            Element thisEl = null;
            if (children[i] instanceof ASTFunctionCallParameters) {
                thisEl = doc.createElement("call");
                final Element methodEl = doc.createElement("method");
                methodEl.appendChild(firstEl);
                thisEl.appendChild(methodEl);
            } else {
                thisEl = doc.createElement("property");
                thisEl.appendChild(firstEl);
            }
            ((SimpleNode) children[i]).toXML(doc, thisEl);
            firstEl = thisEl;
        }
        parent.appendChild(firstEl);
    }

    @Override
    public Types getExpressionType() {
        return Types.Undefined;
    }

    public void setEndOfFirst(int child) {
        endOfFirst = child;
    }

    public void setAlloc(ASTAllocationExpression astAlloc) {
        int length = children.length;
        if (children[length - 1] instanceof ASTFunctionCallParameters) {
            length--;
            astAlloc.setArgs((ASTFunctionCallParameters) children[length]);
        }
        String methodID = "";
        for (int i = 0; i < length; i++) {
            methodID += MessageConstants.NAME_SEPARATOR + ((SimpleNode) children[i]).getMethodId();
        }
        astAlloc.setMethodID(methodID.substring(1));
    }

    @Override
    public String getMethodId() {
        String methodId = "";
        for (final Node child : children) {
            methodId += MessageConstants.NAME_SEPARATOR + ((SimpleNode) child).getMethodId();
        }
        return methodId.substring(1);
    }

    @Override
    public void organizeLHS(Types type) {
        final String varName = getVarName();
        Variable var = null;
        if (varName.startsWith("$")) {
            var = new Variable(varName, Types.Joinpoint);
        } else if (varName.startsWith("@")) {
            var = new Variable(varName);
        } else {
            final SimpleNode father = (SimpleNode) parent;
            var = father.lookup(varName);
        }
        if (var == null) {
            throw newException("'" + value + "' is undefined");
        }
        setIdVar(var);
    }

    @Override
    public String getVarName() {
        int pos = 0;
        if (children[0] instanceof ASTThisReference) {
            pos = 1;
        }
        return ((SimpleNode) children[pos]).getVarName();
    }

    @Override
    public void setIdVar(Variable var) {
        int pos = 0;
        if (children[0] instanceof ASTThisReference) {
            pos = 1;
        }
        ((SimpleNode) children[pos]).setIdVar(var);
    }

    /*
    private void organizeArguments(ASTFunctionCallParameters astFunctionCallParameters) {
        if (astFunctionCallParameters.areNamed) {
            for (final Node node : astFunctionCallParameters.getChildren()) {
                final ASTNamedArgument named = (ASTNamedArgument) node;
    
                ((SimpleNode) named.getChildren()[0]).organize(this);
                codeMapping.put(named.value.toString(), (SimpleNode) named.getChildren()[0]);
            }
        } else {
            for (final Node node : astFunctionCallParameters.getChildren()) {
                final String arg = getParamWithNoValue();
                ((SimpleNode) node).organize(this);
                codeMapping.put(arg, (SimpleNode) node);
            }
        }
    
        for (final SimpleNode sn : codeMapping.values()) {
            if (sn == null) {
                String unsigned = "";
                for (final String nulls : codeMapping.keySet()) {
                    if (codeMapping.get(nulls) == null) {
                        unsigned += nulls + ",";
                    }
                }
                unsigned = unsigned.substring(0, unsigned.length() - 1);
                throw newException("In Action using codedef '" + codeDef.getName()
                        + "': not all codedef arguments are set: " + unsigned);
            }
        }
    }
    
    private String getParamWithNoValue() {
        for (final String param : codeMapping.keySet()) {
            if (codeMapping.get(param) == null) {
                return param;
            }
        }
        return null;
    }
    
    @Override
    public void toXMLTemplate(Document doc, Element parent) {
        throw new RuntimeException("This method is deprecated and should not be invoked!");
    
        final Element literalEl = doc.createElement("literal");
        literalEl.setAttribute("type", Types.Object.toString());
        parent.appendChild(literalEl);
        final Element propEl = doc.createElement("key");
        propEl.setAttribute("name", "'code'");
        literalEl.appendChild(propEl);
        final Element stringEl = doc.createElement("literal");
        stringEl.setAttribute("type", Types.String.toString());
        propEl.appendChild(stringEl);
        stringEl.setAttribute("value", codeDef.getName() + ".code");
        
        for (final String prop : codeMapping.keySet()) {
            final SimpleNode replace = codeMapping.get(prop);
            final Element propertyKeyEl = doc.createElement("key");
            literalEl.appendChild(propertyKeyEl);
            propertyKeyEl.setAttribute("name", Enums.SYMBOL_BEGIN + prop + Enums.SYMBOL_END);
            replace.toXML(doc, propertyKeyEl);
        }
        codeTemplateArgumentsToXML(doc, literalEl, codeParams);
       
    } */

}
/*
 * JavaCC - OriginalChecksum=4bff27c8421e69659ace3e1a4342cb99 (do not edit this
 * line)
 */
