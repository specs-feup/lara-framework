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

import org.lara.interpreter.aspectir.Aspect;
import org.lara.interpreter.aspectir.Base;
import org.lara.interpreter.aspectir.CodeElem;
import org.lara.interpreter.aspectir.ExprLiteral;
import org.lara.interpreter.aspectir.ExprOp;
import org.lara.interpreter.aspectir.Expression;
import org.lara.interpreter.aspectir.Parameter;
import org.lara.interpreter.aspectir.Statement;

import com.google.common.base.Preconditions;

import pt.up.fe.specs.lara.doc.aspectir.elements.AspectElement;
import pt.up.fe.specs.lara.doc.aspectir.elements.AssignmentElement;
import pt.up.fe.specs.lara.doc.aspectir.elements.FunctionDeclElement;
import pt.up.fe.specs.lara.doc.aspectir.elements.StatementElement;
import pt.up.fe.specs.lara.doc.aspectir.elements.VarDeclElement;
import pt.up.fe.specs.lara.doc.comments.LaraDocComment;
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTag;
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTagName;
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTagProperty;
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
        // CodeElem firstElement = statement.components.get(0);
        // Preconditions.checkArgument(firstElement instanceof Expression,
        // "Expected first code element of function decl to be an expression");
        //
        // Expression expression = (Expression) firstElement;

        Preconditions.checkArgument(expression.exprs.size() == 1,
                "Expected one expression, has " + expression.exprs.size());

        ExprOp op = CodeElems.get(0, expression.exprs, ExprOp.class);

        return parseFunctionDecl(op, laraComment);
    }

    public static AspectIrElement parseFunctionDecl(ExprOp op, LaraDocComment laraComment) {
        Preconditions.checkArgument(op.name.equals("FN"), "Expected op to have the name FN, has '" + op.name + "'");

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

        // First child before the body is the name of the function
        String functionName = literals.get(0).value;
        // Remaining children until the body are the names of the parameters
        List<String> parameters = SpecsCollections.subList(literals, 1).stream()
                .map(literal -> literal.value)
                .collect(Collectors.toList());

        // Add information to documentation
        laraComment
                .addTagIfMissing(new JsDocTag(JsDocTagName.ALIAS).setValue(JsDocTagProperty.NAME_PATH, functionName));

        // Add parameters if not present
        for (String parameter : parameters) {
            JsDocTag inputTag = laraComment.getInput(parameter);
            if (inputTag == null) {
                inputTag = new JsDocTag(JsDocTagName.PARAM).setValue(JsDocTagProperty.NAME, parameter);
                laraComment.addTag(inputTag);
            }

        }

        /*
        String vardeclName = CodeElems.parseStringLiteralExpr(expression);
        System.out.println("LARA COMMENT:" + laraComment);
        laraComment.addTagIfMissing(new JsDocTag("alias").setValue(JsDocTagProperty.NAME_PATH, vardeclName));
        */
        return new FunctionDeclElement(functionName, parameters, laraComment);
    }

    public AspectIrElement parseAspect(Aspect aspect, LaraDocComment laraComment) {

        // Get aspect name
        String aspectName = aspect.name;
        if (!laraComment.hasTag(JsDocTagName.ASPECT)) {
            laraComment.addTag(new JsDocTag(JsDocTagName.ASPECT).setValue(JsDocTagProperty.NAME_PATH, aspectName));
        }

        // Process each input
        for (Parameter parameter : getInputParameters(aspect)) {
            // Extract name
            String paramName = parameter.name;

            // Add parameters if not present
            JsDocTag inputTag = laraComment.getInput(paramName);
            if (inputTag == null) {
                inputTag = new JsDocTag(JsDocTagName.PARAM).setValue(JsDocTagProperty.NAME, paramName);
                laraComment.addTag(inputTag);
            }

            // Add default value to parameter tag
            if (!parameter.exprs.isEmpty()) {
                Preconditions.checkArgument(parameter.exprs.size() == 1,
                        "Expected only one argument, found " + parameter.exprs.size());

                String defaultValue = CodeElems.getLaraCode(parameter.exprs.get(0));
                // ExprLiteral literal = (ExprLiteral) parameter.exprs.get(0);
                // String defaultValue = literal.value;
                inputTag.setValue(JsDocTagProperty.DEFAULT_VALUE, defaultValue);
            }
        }

        for (Parameter parameter : getOutputParameters(aspect)) {
            // TODO: Add outputs if not present
            throw new RuntimeException("Not implemented yet: " + CodeElems.toXml(parameter));
        }

        return new AspectElement(laraComment);
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
        Optional<AssignmentElement> assignment = parseAssignmentTry(expression, laraComment);
        if (assignment.isPresent()) {
            return assignment.get();
        }

        // Generic expression

        /// Detect assignment to static method
        /// Detect assignment to instance method
        /// Detect assignment to variable
        // TODO Auto-generated method stub
        return null;
    }

    private Optional<AssignmentElement> parseAssignmentTry(Expression expression, LaraDocComment laraComment) {
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

        AspectIrElement rightHand = parseRightHand(op.exprs.get(1));

        // laraComment.addTagIfMissing(new JsDocTag("alias").setValue(JsDocTagProperty.NAME_PATH, leftHandCode));

        return Optional.of(new AssignmentElement(leftHandCode, rightHand, laraComment));
    }

    private AspectIrElement parseRightHand(Expression expression) {
        // Check if function
        if (expression instanceof ExprOp) {
            ExprOp op = (ExprOp) expression;

            if (op.name.equals("FN")) {
                return parseFunctionDecl(op, new LaraDocComment());
            }

            // System.out.println("ANOTHER OP: " + CodeElems.toXml(op));
            return null;
        }

        // System.out.println("ANOTHER EXPR: " + CodeElems.toXml(expression));

        // TODO Auto-generated method stub
        return null;
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
        laraComment.addTagIfMissing(new JsDocTag(JsDocTagName.ALIAS).setValue(JsDocTagProperty.NAME_PATH, vardeclName));

        return new VarDeclElement(vardeclName, laraComment);
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
