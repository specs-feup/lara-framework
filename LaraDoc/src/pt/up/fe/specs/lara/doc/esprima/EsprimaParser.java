/**
 * Copyright 2021 SPeCS.
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

package pt.up.fe.specs.lara.doc.esprima;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import pt.up.fe.specs.jsengine.libs.EsprimaNode;
import pt.up.fe.specs.lara.doc.aspectir.AspectIrElement;
import pt.up.fe.specs.lara.doc.aspectir.elements.AssignmentElement;
import pt.up.fe.specs.lara.doc.aspectir.elements.ClassElement;
import pt.up.fe.specs.lara.doc.aspectir.elements.FunctionDeclElement;
import pt.up.fe.specs.lara.doc.aspectir.elements.NamedType;
import pt.up.fe.specs.lara.doc.aspectir.elements.VarDeclElement;
import pt.up.fe.specs.lara.doc.comments.LaraCommentsParser;
import pt.up.fe.specs.lara.doc.comments.LaraDocComment;
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTag;
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTagName;
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTagProperty;
import pt.up.fe.specs.util.SpecsCheck;
import pt.up.fe.specs.util.SpecsLogs;

public class EsprimaParser {

    private final Set<String> seenUnsupportedNodes;
    private final LaraCommentsParser commentParser;

    public EsprimaParser() {
        this.seenUnsupportedNodes = new HashSet<>();
        commentParser = new LaraCommentsParser();
    }

    public Optional<AspectIrElement> parseTry(EsprimaNode node, LaraDocComment laraComment) {
        return Optional.ofNullable(parse(node, laraComment));
    }

    public AspectIrElement parse(EsprimaNode node, LaraDocComment laraComment) {
        var type = node.getType();
        // Check type of node
        switch (type) {
        case "ClassDeclaration":
            return parseClassDeclaration(node, laraComment);
        case "FunctionDeclaration":
            return parseFunctionDeclaration(node, laraComment);
        case "VariableDeclarator":
            return parseVariableDeclarator(node, laraComment);
        case "ExpressionStatement":
            return parseExpressionStatement(node, laraComment);
        default:
            if (!seenUnsupportedNodes.contains(type)) {
                SpecsLogs.msgInfo("EsprimaParser does not support yet node of type '" + type + "'");
                seenUnsupportedNodes.add(type);
            }

            return null;
        // return new StatementElement(laraComment);
        }
    }

    private AspectIrElement parseExpressionStatement(EsprimaNode node, LaraDocComment laraComment) {

        var expr = node.getAsNode("expression");

        // Must be assignment
        if (!expr.getType().equals("AssignmentExpression")) {
            return null;
        }

        var leftMember = expr.getAsNode("left");

        // Must be member expression
        if (!leftMember.getType().equals("MemberExpression")) {
            return null;
        }

        var memberObject = leftMember.getAsNode("object");

        var classId = memberObject.getType().equals("MemberExpression") ? memberObject.getAsNode("object")
                : memberObject;

        if (!classId.getType().equals("Identifier")) {
            return null;
        }
        System.out.println("CLASS ID: " + classId);
        var className = classId.getAsString("name");

        var isInstanceMember = isInstanceMember(memberObject);

        var property = leftMember.getAsNode("property");
        if (!property.getType().equals("Identifier")) {
            return null;
        }

        var memberName = property.getAsString("name");

        var rightMember = expr.getAsNode("right");

        FunctionDeclElement function = null;
        if (rightMember.getType().equals("FunctionExpression")) {
            function = parseFunctionExpression(rightMember, memberName, laraComment);
        }

        System.out.println("Class name: " + className);
        System.out.println("Member name: " + memberName);
        System.out.println("Is static: " + !isInstanceMember);

        var namedType = isInstanceMember ? NamedType.INSTANCE : NamedType.STATIC;

        var prototype = isInstanceMember ? ".prototype" : "";
        var fullName = className + prototype + "." + memberName;

        return new AssignmentElement(fullName, function, namedType, laraComment);
    }

    private boolean isInstanceMember(EsprimaNode memberObject) {
        if (!memberObject.getType().equals("MemberExpression")) {
            return false;
        }

        var property = memberObject.getAsNode("property");

        if (!property.getType().equals("Identifier")) {
            return false;
        }

        return property.getAsString("name").equals("prototype");
    }

    private AspectIrElement parseVariableDeclarator(EsprimaNode node, LaraDocComment laraComment) {

        // System.out.println("VARDECL KEYS: " + node.getKeys());

        var name = getName(node);

        laraComment.getTag(JsDocTagName.ALIAS).setValueIfMissing(JsDocTagProperty.NAME_PATH, name);

        var hasClassTag = laraComment.hasTag(JsDocTagName.CLASS);

        var hasInit = node.hasValueFor("init");
        var init = hasInit ? node.getAsNode("init") : null;
        var isFunctionExpr = hasInit ? init.getType().equals("FunctionExpression") : false;
        var hasThis = isFunctionExpr ? init.getDescendantsAndSelfStream()
                .filter(descendant -> descendant.getType().equals("ThisExpression"))
                .findFirst()
                .isPresent()
                : false;

        var isClass = hasClassTag || hasThis;

        // System.out.println("Name: " + name);
        // System.out.println("Is class: " + isClass);
        // System.out.println("Is func expr: " + isFunctionExpr);

        // If class, make sure it has tag class
        if (isClass) {
            laraComment.addTagIfMissing(new JsDocTag(JsDocTagName.CLASS));
        }

        // If function expression, add parameters
        if (isFunctionExpr) {
            parseFunctionExpression(init, name, laraComment);
        }

        if (isClass) {
            return new ClassElement(name, laraComment.getParameters(), laraComment);
        }

        if (isFunctionExpr) {
            return new FunctionDeclElement(name, laraComment.getParameters(), laraComment);
        }

        return new VarDeclElement(name, laraComment);
    }

    private AspectIrElement parseFunctionDeclaration(EsprimaNode node, LaraDocComment laraComment) {
        // Extract function name and inputs.
        // System.out.println("FUNCTION KEYS: " + node.getKeys());

        var functionName = getName(node);

        var params = getParams(node);

        // Add information to documentation
        laraComment.getTag(JsDocTagName.ALIAS).setValueIfMissing(JsDocTagProperty.NAME_PATH, functionName);

        // Add parameters if not present
        for (String parameter : params) {
            JsDocTag inputTag = laraComment.getInput(parameter);

            if (inputTag == null) {
                inputTag = new JsDocTag(JsDocTagName.PARAM).setValue(JsDocTagProperty.NAME, parameter);
                laraComment.addTag(inputTag);
            }
        }

        return new FunctionDeclElement(functionName, params, laraComment);
    }

    public AspectIrElement parseClassDeclaration(EsprimaNode node, LaraDocComment laraComment) {

        var className = node.getAsNode("id").getAsString("name");

        var classElement = new ClassElement(className, Collections.emptyList(), laraComment);

        // Add Class tag, with property name
        laraComment.getTag(JsDocTagName.CLASS).setValueIfMissing(JsDocTagProperty.NAME, className);
        laraComment.getTag(JsDocTagName.ALIAS).setValueIfMissing(JsDocTagProperty.NAME_PATH, className);

        // Add super class
        if (node.hasValueFor("superClass")) {
            var superClass = node.getAsNode("superClass").getAsString("name");

            // Add to class
            classElement.setParentClass(superClass);

            // Add tag
            JsDocTag augmentsTag = new JsDocTag(JsDocTagName.AUGMENTS)
                    .setValue(JsDocTagProperty.NAME_PATH, superClass);

            laraComment.addTag(augmentsTag);
        }

        // Add members of the class element
        for (var classMember : node.getAsNode("body").getAsNodes("body")) {
            // System.out.println("Member type: " + classMember.getType());
            // System.out.println("Member keys: " + classMember.getKeys());

            var element = parseClassMember(className, classMember);

            if (element != null) {
                classElement.addAssignment(element);
            }

        }

        return classElement;
    }

    private AssignmentElement parseClassMember(String className, EsprimaNode classMember) {
        var type = classMember.getType();

        switch (type) {
        case "MethodDefinition":
            return parseMethodDefinition(className, classMember);
        default:
            SpecsLogs.info("parseClassMember not implemented for type '" + type + "'");
            return null;
        }

    }

    private AssignmentElement parseMethodDefinition(String className, EsprimaNode method) {
        var name = method.getAsNode("key").getAsString("name");

        var memberLaraComment = commentParser.parse(method.getComment().getCode());

        memberLaraComment.getTag(JsDocTagName.ALIAS).setValueIfMissing(JsDocTagProperty.NAME_PATH, name);

        var function = method.getAsNode("value");
        SpecsCheck.checkArgument(function.getType().equals("FunctionExpression"),
                () -> "Not implemented for type '" + function.getType() + "'");
        var functionElement = parseFunctionExpression(function, name, memberLaraComment);

        var kind = method.getAsString("kind");
        var memberType = method.getAsBool("static") ? NamedType.STATIC
                : kind.equals("constructor") ? NamedType.CONSTRUCTOR : NamedType.INSTANCE;

        // Mark as constructor
        if (memberType == NamedType.CONSTRUCTOR) {
            memberLaraComment.addTagIfMissing(new JsDocTag(JsDocTagName.CONSTRUCTOR));
        }

        var prototype = memberType == NamedType.STATIC ? "" : ".prototype";

        var fullName = className + prototype + "." + name;

        // return null;
        return new AssignmentElement(fullName, functionElement, memberType, memberLaraComment);
    }

    private FunctionDeclElement parseFunctionExpression(EsprimaNode function, String name,
            LaraDocComment memberLaraComment) {
        // System.out.println("FE TYPE: " + function.getType());
        // System.out.println("FE KEYS: " + function.getKeys());
        var paramNames = getParams(function);

        // Add tags if not present
        if (!paramNames.isEmpty() && !memberLaraComment.hasTag(JsDocTagName.PARAM)) {

            for (var param : paramNames) {
                var tag = new JsDocTag(JsDocTagName.PARAM);
                tag.setValue(JsDocTagProperty.NAME, param);
                tag.setValue(JsDocTagProperty.TYPE_NAME, "unknown");
                memberLaraComment.addTag(tag);
            }
        }

        var functionElement = new FunctionDeclElement(name, paramNames, memberLaraComment);
        return functionElement;
    }

    private String getName(EsprimaNode function) {
        return function.getAsNode("id").getAsString("name");
    }

    private List<String> getParams(EsprimaNode function) {
        return function.getAsNodes("params").stream()
                .map(param -> param.getAsString("name"))
                .collect(Collectors.toList());
    }

}
