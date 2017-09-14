/* Generated By:JJTree: Do not edit this line. SimpleNode.java Version 4.3 */
/*
 * JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,
 * NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true
 */
/*
 * Copyright 2013 SPeCS.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */
package org.dojo.jsl.parser.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringEscapeUtils;
import org.lara.language.specification.artifactsmodel.ArtifactsModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import larac.LaraC;
import larac.exceptions.LARACompilerException;
import larac.exceptions.ParseExceptionData;
import larac.objects.Enums;
import larac.objects.Enums.Types;
import larac.objects.Variable;
import larac.utils.OrganizeUtils;
import pt.up.fe.specs.util.exceptions.NotImplementedException;
import tdrc.utils.StringUtils;

public class SimpleNode implements Node {
    protected int beginLine = -1;
    protected int endLine = -1;
    protected int beginColumn = -1;
    protected int endColumn = -1;
    public int lineNumber = 0;
    protected Node parent;
    protected Node[] children;
    protected int id;
    protected Object value;
    protected String label = "";
    protected LARAEcmaScript parser;
    protected boolean insertTag = true;
    private boolean coords = false;
    protected boolean isTemplate = false;
    protected ParseExceptionData exceptionData;
    protected Token firstToken;
    protected Token lastToken;

    public SimpleNode(int i) {
        this(null, i);
    }

    public SimpleNode(LARAEcmaScript p, int i) {
        id = i;
        parser = p;
    }

    @Override
    public void jjtOpen() {
        // System.out.println("OPENING a " + LARAEcmaScriptTreeConstants.jjtNodeName[id]);
    }

    @Override
    public void jjtClose() {
    }

    @Override
    public void jjtSetParent(Node n) {
        parent = n;
    }

    @Override
    public Node jjtGetParent() {
        return parent;
    }

    @Override
    public void jjtAddChild(Node n, int i) {
        if (getChildren() == null) {
            setChildren(new Node[i + 1]);
        } else if (i >= getChildren().length) {
            final Node c[] = new Node[i + 1];
            System.arraycopy(getChildren(), 0, c, 0, getChildren().length);
            setChildren(c);
        }
        getChildren()[i] = n;
    }

    public void appendChildAsFirst(Node n) {
        if (getChildren() == null) {
            setChildren(new Node[1]);
        } else {
            final Node c[] = new Node[children.length + 1];
            System.arraycopy(getChildren(), 0, c, 1, getChildren().length);
            setChildren(c);
        }
        getChildren()[0] = n;
    }

    /**
     * Returns a child as a {@link Node}
     */
    @Override
    public Node jjtGetChild(int i) {
        return getChildren()[i];
    }

    /**
     * Returns a child casted as {@link SimpleNode}
     * 
     * @param i
     * @return
     */
    public SimpleNode getChild(int i) {
        return (SimpleNode) getChildren()[i];
    }

    @Override
    public int jjtGetNumChildren() {
        return (getChildren() == null) ? 0 : getChildren().length;
    }

    public void jjtSetValue(Object value) {
        this.value = value;
    }

    public Object jjtGetValue() {
        return value;
    }

    /*
     * You can override these two methods in subclasses of SimpleNode to
     * customize the way the node appears when the tree is dumped. If your
     * output uses more than one line you should override toString(String),
     * otherwise overriding toString() is probably all you need to do.
     */

    @Override
    public String toString() {
        return LARAEcmaScriptTreeConstants.jjtNodeName[id] + (value != null ? " [" + value + "]" : "");
    }

    public String toString(String prefix) {
        return prefix + toString();
    }

    /*
     * Override this method if you want to customize how the node dumps out its
     * children.
     */

    public void dump(String prefix) {
        LaraC lara = getLara();
        String string = toString(prefix);
        Token specialToken = jjtGetFirstToken().specialToken;
        // if (specialToken != null) {
        // string += " special: " + specialToken.image;
        // }
        if (lara == null) {
            System.out.println(string);
        } else {
            lara.println(string);
        }
        if (getChildren() != null) {
            for (int i = 0; i < getChildren().length; ++i) {
                final SimpleNode n = (SimpleNode) getChildren()[i];
                if (n != null) {
                    n.dump(prefix + "  ");
                }
            }
        }
    }

    public String dumpToString(String prefix) {
        String out = toString(prefix) + "\n";
        if (getChildren() != null) {
            for (int i = 0; i < getChildren().length; ++i) {
                final SimpleNode n = (SimpleNode) getChildren()[i];
                if (n != null) {
                    out += n.dumpToString(prefix + "\t");
                }
            }
        }
        return out;
    }

    public void declareGlobal(LaraC lara) {
        throw new RuntimeException("Global declaration is not supported for node type '"
                + LARAEcmaScriptTreeConstants.jjtNodeName[id] + "'");
    }

    public Object organizeFirst(Object obj, int i) {
        getLara().warnln(
                "Organize(Object,int): Node \"" + LARAEcmaScriptTreeConstants.jjtNodeName[id]
                        + "\" not supported");
        return null;
    }

    public Object organize(Object obj) {
        getLara().warnln("Organize(Object,ASTSelect): Node \"" + LARAEcmaScriptTreeConstants.jjtNodeName[id]
                + "\" not supported");
        return null;
    }

    public void associateChild(SimpleNode child, int i) {
        jjtAddChild(child, i);
        child.parent = this;
    }

    public void toXML(Document doc, Element parent) {
        getLara().warnln(
                "toXML(Document,Element): Node \"" + LARAEcmaScriptTreeConstants.jjtNodeName[id]
                        + "\" not supported");
        final Element el = doc.createElement(LARAEcmaScriptTreeConstants.jjtNodeName[id]);
        if (value != null) {
            el.setAttribute("value", value.toString());
        }
        el.setAttribute("isSupported", "false");
        parent.appendChild(el);
    }

    public void globalToXML(Document doc, Element parent) {
        throw new RuntimeException("Global declaration to XML is not supported for node type '"
                + LARAEcmaScriptTreeConstants.jjtNodeName[id] + "'");

    }

    public boolean hasFilter() {
        return false;
    }

    public String organize(String type, ArtifactsModel artifacts) {
        // getLara().warnln("Organize: Node \"" + LARAEcmaScriptTreeConstants.jjtNodeName[id] + "\" not supported");
        // return null;
        throw new RuntimeException(
                "Organize: Node \"" + LARAEcmaScriptTreeConstants.jjtNodeName[id] + "\" not supported");
    }

    public Element getFilterElement(Document doc) {
        // getLara().warnln("Get Filter Element: Node
        // \""+LARAEcmaScriptTreeConstants.jjtNodeName[id]+
        // "\" not supported");
        return null;
    }

    public Types getExpressionType() {
        // getLara().warnln("ExpressionType: Node
        // \""+LARAEcmaScriptTreeConstants.jjtNodeName[id]+
        // "\" not supported\n");
        return Types.getDefault();
    }

    public Object organize(Object obj, Object obj2) {
        getLara()
                .warnln("Organize(2): Node \"" + LARAEcmaScriptTreeConstants.jjtNodeName[id] + "\" not supported");
        return null;
    }

    public void secondOrganize(ASTSelect sel) {
        if (children != null) {
            for (final Node n : children) {
                ((SimpleNode) n).secondOrganize(sel);
            }
        }
    }

    public Variable lookup(String var) {
        if (parent == null) {
            getLara().warnln("Variable was not found: " + var);
            return null;
        }
        return ((SimpleNode) parent).lookup(var);
    }

    public Variable lookupNoError(String var) {
        if (parent == null) {
            return null;
        }
        return ((SimpleNode) parent).lookupNoError(var);
    }

    public HashMap<String, Variable> getHMVars() {
        if (parent == null) {
            throw newException("Could not found any SymbolTable for variables. Last node was: " + toString());
        }

        return ((SimpleNode) parent).getHMVars();
    }

    public Variable lookupLastJPVariable() {
        getLara().warnln(
                "lookupLastJPVariable: Node \"" + LARAEcmaScriptTreeConstants.jjtNodeName[id]
                        + "\" not supported");
        return null;
    }
    /*
    protected void varDeclToXML(Document doc, Element parent, Variable var) {
    
    	final Element nameExprEl = doc.createElement("expression");
    	parent.appendChild(nameExprEl);
    	// if(var.getName().startsWith("$"))
    	// nameExprEl.setAttribute("type", Types.Joinpoint.toString());
    	final Element literalNameEl = doc.createElement("literal");
    	literalNameEl.setAttribute("value", var.getName());
    	literalNameEl.setAttribute("type", Types.String.toString());
    	nameExprEl.appendChild(literalNameEl);
    	final Element initEl = doc.createElement("expression");
    	initEl.setAttribute("desc", "init");
    	parent.appendChild(initEl);
    	if (var.getInitialize() != null) {
    	    var.getInitialize().toXML(doc, initEl);
    	}
    }
    
    /*
    protected void createXMLDecl(Collection<Variable> vars, Document doc, Element parent) {
    	final Element statementDeclEl = doc.createElement("statement");
    	statementDeclEl.setAttribute("name", "vardecl");
    	statementDeclEl.setAttribute("coord", getCoords());
    	if (!label.isEmpty()) {
    	    statementDeclEl.setAttribute("label", label);
    	}
    
    	final ArrayList<Variable> fnDecl = new ArrayList<>();
    	for (final Variable var : vars) {
    	    if (var.getType().equals(Types.FNDecl)) {
    		fnDecl.add(var);
    	    } else {
    		varDeclToXML(doc, statementDeclEl, var);
    	    }
    	}
    	if (statementDeclEl.hasChildNodes()) {
    	    parent.appendChild(statementDeclEl);
    	    /*
    	     * for(Variable var: fnDecl){ var.getInitialize().toXML(doc,
    	     * parent); }
    	     *//*
                                       }}*/

    protected void createInputXMLDecl(SimpleNode simpleNode, Map<String, Variable> vars, Document doc, Element parent) {
        if (simpleNode instanceof ASTVariableDeclarationList) {
            for (SimpleNode node : simpleNode.getSimpleNodeChildren()) {
                ASTVariableDeclaration varDecl = (ASTVariableDeclaration) node;
                createInputXMLDecl(varDecl, vars.get(varDecl.getId()), doc, parent);
            }
        } else {
            ASTVariableDeclaration varDecl = (ASTVariableDeclaration) simpleNode;
            createInputXMLDecl(varDecl, vars.get(varDecl.getId()), doc, parent);
        }

        /*
        for (final Variable var : vars) {
        final Element paramEl = doc.createElement("parameter");
        paramEl.setAttribute("name", var.getName());
        parent.appendChild(paramEl);
        paramEl.setAttribute("type", var.getType().toString());
        if (var.getInitialize() != null) {
        	var.getInitialize().toXML(doc, paramEl);
        }
        }*/
    }

    protected void createInputXMLDecl(ASTVariableDeclaration simpleNode, Variable var, Document doc,
            Element parent) {
        final Element paramEl = doc.createElement("parameter");
        paramEl.setAttribute("name", var.getName());
        parent.appendChild(paramEl);
        paramEl.setAttribute("type", var.getType().toString());
        if (simpleNode.jjtGetNumChildren() > 1) {
            simpleNode.getChild(1).toXML(doc, paramEl);
        }
    }

    public LaraC getLara() {
        if (parent == null) {
            return null;
        }
        return ((SimpleNode) parent).getLara();
    }

    protected SimpleNode getParentById(int id) {
        if (this.id == id) {
            return this;
        }
        if (parent != null) {
            return ((SimpleNode) parent).getParentById(id);
        }
        return null;
    }

    protected SimpleNode lookupLabel(String label) {
        if (this.label.equals(label)) {
            return this;
        }
        if (parent != null) {
            return ((SimpleNode) parent).lookupLabel(label);
        }
        return null;
    }

    public void organizeLHS(Types type) {
        getLara().warnln(
                "organizeLHS(1): Node \"" + LARAEcmaScriptTreeConstants.jjtNodeName[id] + "\" not supported");
    }

    /**
     * @param children
     *            the children to set
     */
    public void setChildren(Node[] children) {
        this.children = children;
    }

    /**
     * @return the children
     */
    public Node[] getChildren() {
        return children;
    }

    /**
     * @return the children
     */
    public List<SimpleNode> getSimpleNodeChildren() {
        return Arrays.asList(children).stream().map(n -> (SimpleNode) n).collect(Collectors.toList());
    }

    public Element getXML(Document doc) {
        return doc.createElement("getXML:NOT_DONE");
    }

    public void organizePointcutReference(ASTPointcut pc) {
    }

    public void organizeActionReference(ASTPointcut pc) {
    }

    public String getMethodId() {
        return null;
    }

    public String getVarName() {
        throw newException(
                "getVarName(0): Node \"" + LARAEcmaScriptTreeConstants.jjtNodeName[id] + "\" not supported");
        // return null;
    }

    public void setIdVar(Variable var) {
    }

    public void setCoord(Token begin, Token end) {
        beginLine = begin.beginLine;
        endLine = end.endLine;
        beginColumn = begin.beginColumn;
        endColumn = end.endColumn;
        setCoords(true);
    }

    public RuntimeException newException(String error) {
        if (beginColumn != -1) {
            String message = "in line " + beginLine + ", column " + beginColumn + ": " + error;
            return new LARACompilerException(message);
        }

        if (parent != null) {
            return ((SimpleNode) parent).newException(error);
        }

        return new LARACompilerException(error);
        // throw new LaraException(error);
    }

    public void toXMLTemplate(Document doc, Element callEl) {
        throw newException(
                "When using actions, parameters of type 'template' can only be of type: \n\t-> literal string\n\t-> literal code\n\t-> codedef");
    }

    public List<String> getCodeArguments(String code) {
        final List<String> codeParams = new ArrayList<>(OrganizeUtils.getTagValues(code));
        return codeParams;
    }

    protected void verifyCodeArguments(List<String> codeParams) {
        for (final String param : codeParams) {

            // Change this to accept a[0], a[0][0][0], a(), a(0,1)...
            //
            // if (param.contains("[") || param.contains("(")) {
            // throw newException("Complex reference using \"[ ]\" and/or \"( )\" : " + param
            // + ". Only simple reference can be defined here.");
            // }
            if (param.startsWith("$")) {
                continue;
            }
            if (param.contains(".")) {
                if (lookup(param.substring(0, param.indexOf("."))) == null) {
                    throw newException("Variable '" + param.substring(0, param.indexOf(".")) + "' is undefined");
                }

            } else if (lookup(param) == null) {
                throw newException("Variable '" + param + "' is undefined");
            }
        }
    }

    public void codeTemplateArgumentsToXML(Document doc, Element parent, List<String> codeParams) {
        for (final String param : codeParams) {
            final Element propertyKeyEl = doc.createElement("key");
            parent.appendChild(propertyKeyEl);
            propertyKeyEl.setAttribute("name", Enums.SYMBOL_BEGIN + param + Enums.SYMBOL_END);
            SimpleNode expression = OrganizeUtils.parseExpression(param.trim());
            expression.toXML(doc, propertyKeyEl);
            /*
            if (!param.contains(".")) {
            final Element idRefEl = doc.createElement("id");
            propertyKeyEl.appendChild(idRefEl);
            idRefEl.setAttribute("name", param);
            } else {
            final String[] props = param.split("\\.");
            Element lastEl = doc.createElement("id");
            
            lastEl.setAttribute("name", props[0]);
            for (int j = 1; j < props.length; j++) {
            final Element propEl = doc.createElement("property");
            propEl.appendChild(lastEl);
            final Element propLiteralEl = doc.createElement("literal");
            propEl.appendChild(propLiteralEl);
            propLiteralEl.setAttribute("value", props[j]);
            propLiteralEl.setAttribute("type", Types.String.toString());
            lastEl = propEl;
            }
            propertyKeyEl.appendChild(lastEl);
            }
            */
        }
    }

    /**
     * @param coords
     *            the coords to set
     */
    public void setCoords(boolean coords) {
        this.coords = coords;
    }

    /**
     * @return the coords
     */
    public boolean hasCoords() {
        return coords;
    }

    public String nodeCoords() {
        final LaraC lara = getLara();
        String path = "";
        if (lara != null) {
            path = lara.getLaraPath();
        }
        return path + ":" + beginLine + ":" + beginColumn + ":" + endLine
                + ":"
                + endColumn;
    }

    public String lookDownCoords() {
        if (hasCoords()) {
            return nodeCoords();
        }
        if (children != null) {
            for (final Node child : children) {
                final String coords = ((SimpleNode) child).lookDownCoords();
                if (coords != null) {
                    return coords;
                }
            }
        }
        return null;
    }

    public String lookupCoords() {
        if (hasCoords()) {
            return nodeCoords();
        }
        if (parent != null) {
            return ((SimpleNode) parent).lookupCoords();
        }
        return null;
    }

    public String getCoords() {
        String coords = lookDownCoords();
        if (coords != null) {
            return coords;
        }
        coords = lookupCoords();
        if (coords != null) {
            return coords;
        }
        final LaraC lara = getLara();
        return lara.getLaraPath() + ":0:0:0:0";
    }

    public ParseExceptionData getExceptionData() {
        return exceptionData;
    }

    public void setExceptionData(ParseExceptionData exceptionData) {
        this.exceptionData = exceptionData;
    }

    public Token jjtGetFirstToken() {
        return firstToken;
    }

    public void jjtSetFirstToken(Token token) {
        firstToken = token;
    }

    public Token jjtGetLastToken() {
        return lastToken;
    }

    public void jjtSetLastToken(Token token) {
        lastToken = token;
    }

    /**
     * Return the LARA source code of the AST <br>
     * <NOTE> This code is not equal to the input as it is automatically generated
     * 
     * @return
     */
    public final String toSource() {
        return toSource(0);
    }

    /**
     * Return the LARA source code of the AST
     * 
     * @see SimpleNode#toSource()
     * @param indentation
     * @return
     */
    public String toSource(int indentation) {
        throw new NotImplementedException(
                "To source method not supported for AST node type " + LARAEcmaScriptTreeConstants.jjtNodeName[id]);
    }

    protected static String indent(int indentation) {
        return StringUtils.repeat("\t", indentation);
    }

    protected ASTAspectDef getAspectDefForDeclStmt(String typeOfStatement) {
        return getAncestorOfType(ASTAspectDef.class)
                .orElseThrow(
                        () -> new LARACompilerException(
                                typeOfStatement + " statement can only be used inside an aspectdef"));
    }

    public <T extends SimpleNode> Optional<T> getAncestorOfType(Class<T> type) {
        if (parent == null) {
            return Optional.empty();
        }

        if (type.isInstance(parent)) {
            return Optional.of(type.cast(parent));
        }
        return ((SimpleNode) parent).getAncestorOfType(type);
    }

    public <T extends SimpleNode> List<T> getDescendantsOfType(Class<T> type) {

        if (children == null || children.length == 0) {
            return Collections.emptyList();
        }
        List<T> descendants = new ArrayList<>();
        for (Node node : children) {
            SimpleNode simpleNode = (SimpleNode) node;
            if (type.isInstance(node)) {
                descendants.add(type.cast(simpleNode));
            }
            descendants.addAll(simpleNode.getDescendantsOfType(type));

        }

        return descendants;
    }

    /**
     * Verifies if a node is equal or descendant of this node
     * 
     * @param descendant
     * @return
     */
    public boolean isDescendant(Node descendant) {
        if (equals(descendant)) {
            return true;
        }
        if (children == null || children.length == 0) {
            return false;
        }

        for (Node node : children) {
            SimpleNode simpleNode = (SimpleNode) node;
            boolean childDescendant = simpleNode.isDescendant(descendant);
            if (childDescendant) {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * @param methodID
     * @return
     */
    public ASTAllocationExpression newAllocExpr(String methodID) {
        final ASTAllocationExpression alloc = new ASTAllocationExpression(
                LARAEcmaScriptTreeConstants.JJTALLOCATIONEXPRESSION);
        alloc.setMethodID(methodID);
        return alloc;
    }

    public void addXMLComent(Element el) {

        StringBuilder comment = new StringBuilder();
        Token special = jjtGetFirstToken().specialToken;
        while (special != null) {
            comment.append(special);
            comment.append("\n");
            special = special.specialToken;
        }
        if (comment.length() != 0) {
            String content;
            // try {
            // content = DatatypeConverter.printBase64Binary(comment.toString().getBytes("UTF-8"));
            content = StringEscapeUtils.escapeHtml4(comment.toString());
            el.setAttribute("comment", content);
            // } catch (UnsupportedEncodingException e) {
            // getLara().warnln(
            // "Could not add the following comment(s) to aspect-ir: " + comment.toString() + ". Reason: "
            // + e.getMessage());
            // }
        }
    }
}

/*
 * JavaCC - OriginalChecksum=d27917bd78bc237db3717563f5dd3d25 (do not edit this
 * line)
 */
