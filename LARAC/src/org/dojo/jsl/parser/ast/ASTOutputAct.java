/* Generated By:JJTree: Do not edit this line. ASTOutputAct.java Version 4.3 */
/*
 * JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,
 * NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true
 */
package org.dojo.jsl.parser.ast;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.xml.bind.DatatypeConverter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import larac.objects.Enums;
import larac.objects.Enums.Types;

@Deprecated
public class ASTOutputAct extends SimpleNode {
    private String language;
    private boolean pureCode;
    private ASTCodeDef codeDef;
    private final ArrayList<String> codeParams = new ArrayList<>();
    private LinkedHashMap<String, SimpleNode> codeMapping = new LinkedHashMap<>();

    public ASTOutputAct(int id) {
        super(id);
    }

    public ASTOutputAct(LARAEcmaScript p, int id) {
        super(p, id);
    }

    @Override
    public String toString() {
        return LARAEcmaScriptTreeConstants.jjtNodeName[id];
    }

    /**
     * @param language
     *            the language to set
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param paramMapping
     *            the paramMapping to set
     */
    public void setParamMapping(LinkedHashMap<String, SimpleNode> paramMapping) {
        codeMapping = paramMapping;
    }

    /**
     * @return the paramMapping
     */
    public LinkedHashMap<String, SimpleNode> getParamMapping() {
        return codeMapping;
    }

    @Override
    public Object organize(Object obj) {
        /*
        final ASTAction act = (ASTAction) parent;
        act.setMethod("out");
        act.setArguments(OrganizeUtils.createOutputActionParameters(getLara().languageSpec()));
        final Map<String, ActionArgument> arguments = act.getArguments();
        if (jjtGetChild(0) instanceof ASTFunctionCallParameters) {
            final ASTFunctionCallParameters params = (ASTFunctionCallParameters) jjtGetChild(0);
            if (params.areNamed) {
                for (final Node param : params.getChildren()) {
                    final ASTNamedArgument na = (ASTNamedArgument) param;
                    if (!arguments.containsKey(na.value)) {
                        throw newException("The argument '" + na.value + "' does not exist for action 'out'");
                    }
                    final ActionArgument actArg = arguments.get(na.value);
                    if (na.value.equals("code")) {
                        verifyCodeDef(na.jjtGetChild(0), arguments);
                    } else {
                        na.organize(obj);
                        actArg.setValue((SimpleNode) na.jjtGetChild(0));
                    }
                }
            } else {
                if (params.getChildren().length != arguments.size()) {
                    throw newException("Illegal number of arguments for action 'out'");
                }
                verifyCodeDef(params.jjtGetChild(0), arguments);
            }
        } else {
            if (children[0] instanceof ASTLiteral) {
                ((SimpleNode) children[0]).organize(this);
                arguments.get("code").setValue((SimpleNode) children[0]);
            } else {
                verifyCodeDef(children[0], arguments);
            }
        }*/
        return null;
    }

    /*
    private void verifyCodeDef(Node node, Map<String, ActionArgument> arguments) {
        String codeName = "";
        if (node instanceof ASTLiteral) {
            ((SimpleNode) node).organize(this);
    
            arguments.get("code").setValue((SimpleNode) node);
            return;
        }
        if (node instanceof ASTIdentifier) {
            codeName = ((ASTIdentifier) node).value.toString();
            codeDef = getLara().aspectIR().getCodedef(codeName);
            if (codeDef == null) {
                throw newException("Codedef '" + codeName + "' does not exist.");
            }
        } else if (node instanceof ASTCompositeReference) {
    
            codeName = ((ASTIdentifier) node.jjtGetChild(0)).value.toString();
            codeDef = getLara().aspectIR().getCodedef(codeName);
            if (codeDef == null) {
                throw newException("Codedef '" + codeName + "' does not exist.");
            }
    
            for (final String arg : codeDef.getArguments()) {
                codeMapping.put(arg, null);
            }
            final ASTFunctionCallParameters params = (ASTFunctionCallParameters) node.jjtGetChild(1);
            organizeArguments(params);
        } else {
            throw newException("Argument 'code' for out is invalid.");
        }
    
        arguments.get("code").setValue(this);
    }
    
    private void organizeArguments(ASTFunctionCallParameters astFunctionCallParameters) {
        if (astFunctionCallParameters.areNamed) {
            for (final Node node : astFunctionCallParameters.getChildren()) {
                final ASTNamedArgument named = (ASTNamedArgument) node;
                if (!codeDef.getArguments().contains(named.value)) {
                    throw newException("In Action 'out': '" + named.value + "' does not exist.");
                }
                ((SimpleNode) named.getChildren()[0]).organize(this);
                getParamMapping().put(named.value.toString(), (SimpleNode) named.getChildren()[0]);
            }
        } else {
            for (final Node node : astFunctionCallParameters.getChildren()) {
                final String arg = getParamWithNoValue();
                if (arg == null) {
                    throw newException("In Action 'out': Illegal number of arguments");
                }
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
                throw newException("In Action 'out': not all codedef arguments are set: " + unsigned);
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
    */
    /**
     * @param codeDef
     *            the codeDef to set
     */
    public void setCodeDef(ASTCodeDef codeDef) {
        this.codeDef = codeDef;
    }

    /**
     * @return the codeDef
     */
    public ASTCodeDef getCodeDef() {
        return codeDef;
    }

    /**
     * @param pureCode
     *            the pureCode to set
     */
    public void setPureCode(boolean pureCode) {
        this.pureCode = pureCode;
    }

    /**
     * @return the pureCode
     */
    public boolean isPureCode() {
        return pureCode;
    }

    /**
     * @param doc
     *            Document where the Aspect-IR is going to be saved
     * @param parent
     *            the parent node owner for the nodes generated in this node
     */
    @Override
    public void toXMLTemplate(Document doc, Element parent) {
        final Element literalEl = doc.createElement("literal");
        literalEl.setAttribute("type", Types.Object.toString());
        parent.appendChild(literalEl);
        final Element propEl = doc.createElement("key");
        propEl.setAttribute("name", "'code'");
        literalEl.appendChild(propEl);
        final Element stringEl = doc.createElement("literal");
        // stringEl.setAttribute("type", Types.String.toString());
        // propEl.appendChild(stringEl);
        // stringEl.setAttribute("value", codeDef.getName() + ".code");
        stringEl.setAttribute("type", Types.Base64.toString());
        propEl.appendChild(stringEl);
        try {
            final String codeBase64 = DatatypeConverter.printBase64Binary(codeDef.getCode().getBytes("UTF-8"));
            stringEl.setAttribute("value", codeBase64);
        } catch (final UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        for (final String prop : codeMapping.keySet()) {
            final SimpleNode replace = codeMapping.get(prop);
            final Element propertyKeyEl = doc.createElement("key");
            literalEl.appendChild(propertyKeyEl);
            propertyKeyEl.setAttribute("name", Enums.SYMBOL_BEGIN + prop + Enums.SYMBOL_END);
            replace.toXML(doc, propertyKeyEl);
        }
        codeTemplateArgumentsToXML(doc, literalEl, codeParams);
    }

}
/*
 * JavaCC - OriginalChecksum=686f16c31cea47f2f993b8d2a4070c21 (do not edit this
 * line)
 */
