/**
 * Copyright 2017 SPeCS.
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

package pt.up.fe.specs.lara.doc.aspectir;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import com.google.common.base.Preconditions;

import larac.code.CodeElems;
import pt.up.fe.specs.lara.aspectir.Aspect;
import pt.up.fe.specs.lara.aspectir.Base;
import pt.up.fe.specs.lara.aspectir.CodeElem;
import pt.up.fe.specs.lara.aspectir.ExprBody;
import pt.up.fe.specs.lara.aspectir.ExprId;
import pt.up.fe.specs.lara.aspectir.ExprLiteral;
import pt.up.fe.specs.lara.aspectir.ExprOp;
import pt.up.fe.specs.lara.aspectir.Expression;
import pt.up.fe.specs.lara.aspectir.Parameter;
import pt.up.fe.specs.lara.aspectir.Statement;
import pt.up.fe.specs.lara.doc.aspectir.elements.AspectElement;
import pt.up.fe.specs.lara.doc.aspectir.elements.ClassElement;
import pt.up.fe.specs.lara.doc.aspectir.elements.FunctionDeclElement;
import pt.up.fe.specs.lara.doc.aspectir.elements.NamedElement;
import pt.up.fe.specs.lara.doc.aspectir.elements.StatementElement;
import pt.up.fe.specs.lara.doc.aspectir.elements.VarDeclElement;
import pt.up.fe.specs.lara.doc.comments.LaraDocComment;
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTag;
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTagName;
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTagProperty;
import pt.up.fe.specs.util.SpecsCheck;
import pt.up.fe.specs.util.SpecsCollections;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.classmap.BiFunctionClassMap;
import tdrc.utils.StringUtils;

public class AspectIrParser {

    private final BiFunctionClassMap<Base, LaraDocComment, AspectIrElement> parsers;
    private final Set<String> seenUnsupportedNodes;

    public AspectIrParser() {
        this.parsers = new BiFunctionClassMap<>();
        this.seenUnsupportedNodes = new HashSet<>();
        addParsers(parsers);
    }

    private void addParsers(BiFunctionClassMap<Base, LaraDocComment, AspectIrElement> parsers) {
        parsers.put(Aspect.class, this::parseAspect);
        parsers.put(Statement.class, this::parseStatement);
    }

    public static String toXml(Base aspectIrNode) {
        try {
            return StringUtils.xmlToStringBuffer(aspectIrNode.getXmlDocument(), 3).toString();
        } catch (TransformerFactoryConfigurationError | TransformerException e) {
            throw new RuntimeException("Could not convert aspect IR node to XML: ", e);
        }
    }

    public AspectIrElement parse(Base aspectIrNode, LaraDocComment laraComment) {
        return parsers.apply(aspectIrNode, laraComment);
    }

    public AspectIrElement parseStatement(Statement statement, LaraDocComment laraComment) {

        // Check type of statement
        switch (statement.name) {
        case "vardecl":
            return parseVarDeclStatement(statement, laraComment);
        case "expr":
            return parseExprStatement(statement, laraComment);
        case "fndecl":
            return parseFunctionDeclStatement(statement, laraComment);
        default:
            if (!seenUnsupportedNodes.contains(statement.name)) {
                SpecsLogs.msgInfo("AspectIrParser does not support yet statement '" + statement.name + "'");
                seenUnsupportedNodes.add(statement.name);
            }

            return new StatementElement(laraComment);
        }
    }

    private AspectIrElement parseFunctionDeclStatement(Statement statement, LaraDocComment laraComment) {
        // Extract function name and inputs.

        Preconditions.checkArgument(statement.components.size() == 1,
                "Expected one component, has " + statement.components.size());

        Expression expression = CodeElems.get(0, statement.components, Expression.class);

        ExprOp functionOp = CodeElems.getOp(expression, ExtraOp.FN.getName())
                .orElseThrow(() -> new RuntimeException("Expected to find a function: " + expression));

        return parseFunctionOp(functionOp, laraComment);
    }

    public static AspectIrElement parseFunctionOp(ExprOp op, LaraDocComment laraComment) {
        return parseFunctionOp(op, laraComment, null);
    }

    public static AspectIrElement parseFunctionOp(ExprOp op, LaraDocComment laraComment, String varName) {
        // Preconditions.checkArgument(op.name.equals("FN"), "Expected op to have the name FN, has '" + op.name + "'");

        // Get all literal nodes until a different appears
        List<ExprLiteral> literals = new ArrayList<>();
        for (Expression expr : op.exprs) {
            // If not a literal, break
            if (!(expr instanceof ExprLiteral)) {
                break;
            }

            // If a literal, collect
            literals.add((ExprLiteral) expr);
        }

        Preconditions.checkArgument(!literals.isEmpty(), "Expected to find at least one literal node");

        // TODO: take into account boolean isAnonymous

        // First child before the body is the name of the function
        String functionName = literals.get(0).value;

        if (functionName.isEmpty() && varName != null) {
            functionName = varName;
        }

        // Remaining children until the body are the names of the parameters
        List<String> parameters = SpecsCollections.subList(literals, 1).stream()
                .map(literal -> literal.value)
                .collect(Collectors.toList());

        // Add information to documentation
        if (!functionName.isEmpty()) {
            // JsDocTag tag = new JsDocTag(JsDocTagName.ALIAS).setValue(JsDocTagProperty.NAME_PATH, functionName);
            // laraComment.addTagIfMissing(tag);
            laraComment.getTag(JsDocTagName.ALIAS).setValueIfMissing(JsDocTagProperty.NAME_PATH, functionName);
        }

        // System.out.println("LARA COMMENT:" + laraComment);

        // Add parameters if not present
        for (String parameter : parameters) {
            JsDocTag inputTag = laraComment.getInput(parameter);

            if (inputTag == null) {
                inputTag = new JsDocTag(JsDocTagName.PARAM).setValue(JsDocTagProperty.NAME, parameter);
                laraComment.addTag(inputTag);
            }

        }

        // If no name, it is not a class
        if (functionName.isEmpty()) {
            return new FunctionDeclElement(functionName, parameters, laraComment);
        }

        ////

        // Check if marked with tag class
        if (laraComment.hasTag(JsDocTagName.CLASS)) {
            // System.out.println("FOUND CLASS 1:" + functionName);
            return new ClassElement(functionName, parameters, laraComment);
        }

        ExprBody body = CodeElems.getBody(op);

        // If there is an id with name 'this', consider this to be a class
        boolean isClass = CodeElems.toElemStream(body)
                .filter(ExprId.class::isInstance)
                .map(ExprId.class::cast)
                .filter(exprId -> exprId.name.equals("this"))
                .findFirst()
                .isPresent();

        // Add tag class
        if (isClass) {
            // JsDocTag classTag = new JsDocTag(JsDocTagName.CLASS).setValue(JsDocTagProperty.NAME, functionName);
            // laraComment.addTagIfMissing(classTag);
            laraComment.getTag(JsDocTagName.CLASS).setValueIfMissing(JsDocTagProperty.NAME, functionName);
            // System.out.println("FOUND CLASS 2:" + functionName);

        }

        return isClass ? new ClassElement(functionName, parameters, laraComment)
                : new FunctionDeclElement(functionName, parameters, laraComment);

        ///

        /*
        String vardeclName = CodeElems.parseStringLiteralExpr(expression);
        System.out.println("LARA COMMENT:" + laraComment);
        laraComment.addTagIfMissing(new JsDocTag("alias").setValue(JsDocTagProperty.NAME_PATH, vardeclName));
        */
        // return new FunctionDeclElement(functionName, parameters, laraComment);
    }

    public AspectIrElement parseAspect(Aspect aspect, LaraDocComment laraComment) {

        // Get aspect name
        String aspectName = aspect.name;
        // if (!laraComment.hasTag(JsDocTagName.ASPECT)) {
        // laraComment.addTag(new JsDocTag(JsDocTagName.ASPECT).setValue(JsDocTagProperty.NAME_PATH, aspectName));
        // }

        // JsDocTag aspectTag = laraComment.getTag(JsDocTagName.ASPECT);
        // if (aspectTag == null) {
        // aspectTag = new JsDocTag(JsDocTagName.ASPECT);
        // laraComment.addTag(aspectTag);
        // }
        // aspectTag.setValue(JsDocTagProperty.NAME_PATH, aspectName);

        // Set aspect name (always use the name in the code)
        laraComment.getTag(JsDocTagName.ASPECT).setValue(JsDocTagProperty.NAME_PATH, aspectName);
        // laraComment.addTagIfMissing(new JsDocTag(JsDocTagName.ASPECT).setValue(JsDocTagProperty.NAME_PATH,
        // aspectName));

        // laraComment.addTagIfMissing(new JsDocTag(JsDocTagName.ALIAS).setValue(JsDocTagProperty.NAME_PATH,
        // aspectName));
        laraComment.getTag(JsDocTagName.ALIAS).setValueIfMissing(JsDocTagProperty.NAME_PATH, aspectName);

        // Process each input
        for (Parameter parameter : getInputParameters(aspect)) {
            addParameter(laraComment, JsDocTagName.PARAM, parameter);
        }

        for (Parameter parameter : getOutputParameters(aspect)) {
            addParameter(laraComment, JsDocTagName.OUTPUT, parameter);
        }

        return new AspectElement(laraComment);
    }

    private void addParameter(LaraDocComment laraComment, JsDocTagName tagName, Parameter parameter) {
        // Extract name
        String paramName = parameter.name;

        // Add parameters if not present
        JsDocTag tag = laraComment.getInput(paramName);
        if (tag == null) {
            tag = new JsDocTag(tagName).setValue(JsDocTagProperty.NAME, paramName);
            laraComment.addTag(tag);
        }

        // Add default value to parameter tag
        if (!parameter.exprs.isEmpty()) {
            Preconditions.checkArgument(parameter.exprs.size() == 1,
                    "Expected only one argument, found " + parameter.exprs.size());

            String defaultValue = CodeElems.getLaraCode(parameter.exprs.get(0));
            // ExprLiteral literal = (ExprLiteral) parameter.exprs.get(0);
            // String defaultValue = literal.value;
            // Default value implies optional
            tag.setValue(JsDocTagProperty.OPTIONAL, "");
            tag.setValue(JsDocTagProperty.DEFAULT_VALUE, defaultValue);
        }
    }

    private List<Parameter> getInputParameters(Aspect aspect) {
        if (aspect.parameters == null) {
            return Collections.emptyList();
        }

        if (aspect.parameters.input == null) {
            return Collections.emptyList();
        }

        return aspect.parameters.input.parameters;
    }

    private List<Parameter> getOutputParameters(Aspect aspect) {
        if (aspect.parameters == null) {
            return Collections.emptyList();
        }

        if (aspect.parameters.output == null) {
            return Collections.emptyList();
        }

        return aspect.parameters.output.parameters;
    }

    private AspectIrElement parseExprStatement(Statement statement, LaraDocComment laraComment) {

        Preconditions.checkArgument(!statement.components.isEmpty(),
                "Expected expr to have at least one code element");

        CodeElem firstElement = statement.components.get(0);

        Preconditions.checkArgument(firstElement instanceof Expression,
                "Expected first code element of expr to be an expression");

        Expression expression = (Expression) firstElement;

        // Detect assignment
        Optional<NamedElement> assignment = parseAssignmentTry(expression, laraComment);
        if (assignment.isPresent()) {
            // System.out.println("ASSIGNMENT:" + CodeElems.toXml(expression));
            return assignment.get();
        }

        // Generic expression

        /// Detect assignment to static method
        /// Detect assignment to instance method
        /// Detect assignment to variable
        // TODO Auto-generated method stub
        return null;
    }

    private Optional<NamedElement> parseAssignmentTry(Expression expression, LaraDocComment laraComment) {

        if (expression.exprs.size() != 1) {
            return Optional.empty();
        }

        if (!(expression.exprs.get(0) instanceof ExprOp)) {
            return Optional.empty();
        }

        ExprOp op = (ExprOp) expression.exprs.get(0);

        if (!op.name.equals("ASSIGN")) {
            return Optional.empty();
        }

        Preconditions.checkArgument(op.exprs.size() == 2,
                "Expected op to have two expressions, has " + op.exprs.size());

        // Get code for the left hand
        String leftHandCode = CodeElems.getLaraCode(op.exprs.get(0));
        // AspectIrElement rightHand = parseRightHand(op.exprs.get(1), laraComment);

        var functionDecl = extractFunctionDecl(op.exprs.get(1), laraComment);
        var parentClass = extractParentClass(op.exprs.get(1));

        // laraComment.addTagIfMissing(new JsDocTag("alias").setValue(JsDocTagProperty.NAME_PATH, leftHandCode));

        // return Optional.of(new NamedElement(leftHandCode, rightHand, laraComment));
        var namedElement = new NamedElement(leftHandCode, functionDecl, laraComment);
        namedElement.setParentClass(parentClass);

        return Optional.of(namedElement);
    }

    private FunctionDeclElement extractFunctionDecl(Expression expression, LaraDocComment laraComment) {
        // Check if function
        if (!(expression instanceof ExprOp)) {
            return null;
        }

        ExprOp op = (ExprOp) expression;

        if (!op.name.equals("FN")) {
            return null;
        }

        var element = parseFunctionOp(op, laraComment, null);

        SpecsCheck.checkArgument(element instanceof FunctionDeclElement,
                () -> "Expected a FunctionDecl, found " + element.getClass().getName());

        return (FunctionDeclElement) element;
    }

    private static String extractParentClass(CodeElem codeElem) {

        // Look for a property that has a literal 'prototype' and an id
        List<ExprLiteral> prototypeLiterals = BaseNodes.toStream(codeElem)
                .filter(ExprLiteral.class::isInstance)
                .map(ExprLiteral.class::cast)
                .filter(literal -> "prototype".equals(literal.value))
                .collect(Collectors.toList());

        if (prototypeLiterals.size() != 1) {
            // SpecsLogs.warn("Expected to find one 'prototype' literal, found "
            // + prototypeLiterals.size() + ":\n " + CodeElems.toXml(codeElem));
            // return "";
            return null;
        }

        ExprLiteral prototypeLiteral = prototypeLiterals.get(0);

        Base parent = (Base) prototypeLiteral.getParent();

        // Look for ID node
        List<ExprId> idNodes = BaseNodes.toStream(parent)
                .filter(ExprId.class::isInstance)
                .map(ExprId.class::cast)
                .collect(Collectors.toList());

        if (idNodes.size() != 1) {
            // SpecsLogs.warn("Expected to find one id node, found "
            // + idNodes.size() + ":\n " + BaseNodes.toXml(parent));
            // return "";
            return null;
        }

        ExprId id = idNodes.get(0);
        return id.name;
    }

    private AspectIrElement parseVarDeclStatement(Statement statement, LaraDocComment laraComment) {
        // Extract vardecl name. The first child should be an expression, with a
        // literal of type String
        Preconditions.checkArgument(!statement.components.isEmpty(),
                "Expected var decl to have at least one code element");
        CodeElem expression = statement.components.get(0);

        Preconditions.checkArgument(expression instanceof Expression,
                "Expected first code element of var decl to be an expression");

        String vardeclName = CodeElems.parseStringLiteralExpr((Expression) expression);
        // System.out.println("LARA COMMENT:" + laraComment);
        // laraComment.addTagIfMissing(new JsDocTag(JsDocTagName.ALIAS).setValue(JsDocTagProperty.NAME_PATH,
        // vardeclName));
        laraComment.getTag(JsDocTagName.ALIAS).setValueIfMissing(JsDocTagProperty.NAME_PATH, vardeclName);

        // Check if vardecl is a class
        Optional<AspectIrElement> functionElement = parseFunctionElementFromVarDecl(statement, vardeclName,
                laraComment);
        if (functionElement.isPresent()) {
            return functionElement.get();
        }

        return new VarDeclElement(vardeclName, laraComment);
    }

    private Optional<AspectIrElement> parseFunctionElementFromVarDecl(Statement statement, String className,
            LaraDocComment laraComment) {

        /**
         * VarDecl without assignment
         */
        if (statement.components.size() < 2) {
            return Optional.empty();
        }

        // Second child must be an expression
        Expression expression = (Expression) statement.components.get(1);

        Optional<ExprOp> op = CodeElems.getOp(expression, ExtraOp.FN.getName());
        if (!op.isPresent()) {
            // No function. Test if annotated with class tag
            return laraComment.hasTag(JsDocTagName.CLASS)
                    ? Optional.of(new ClassElement(className, Collections.emptyList(), laraComment))
                    : Optional.empty();
        }

        AspectIrElement parsedFunction = parseFunctionOp(op.get(), laraComment, className);

        // Only return ClassElement
        return parsedFunction instanceof ClassElement ? Optional.of((ClassElement) parsedFunction) : Optional.empty();
        /*
        // Check if marked with tag class
        if (laraComment.hasTag(JsDocTagName.CLASS)) {
            return Optional.of(new ClassElement(className, laraComment));
        }
        
        ExprBody body = CodeElems.getBody(op.get());
        
        // If there is an id with name 'this', consider this to be a class
        boolean isClass = CodeElems.toElemStream(body)
                .filter(ExprId.class::isInstance)
                .map(ExprId.class::cast)
                .filter(exprId -> exprId.name.equals("this"))
                .findFirst()
                .isPresent();
        
        // Add tag class
        if (isClass) {
            JsDocTag classTag = new JsDocTag(JsDocTagName.CLASS).setValue(JsDocTagProperty.NAME, className);
            laraComment.addTagIfMissing(classTag);
        }
        
        return isClass ? Optional.of(new ClassElement(className, laraComment)) : Optional.empty();
        */
    }

    /*
     * public AspectIrElement parseDeclaration(Statement declaration,
     * LaraDocComment laraComment) {
     * 
     * System.out.println("XML:" + toXml(declaration)); // TODO Auto-generated
     * method stub return null; }
     * 
     * public AspectIrElement parseAspect(Aspect aspect, LaraDocComment
     * laraComment) { // TODO Auto-generated method stub return null; }
     */

}
