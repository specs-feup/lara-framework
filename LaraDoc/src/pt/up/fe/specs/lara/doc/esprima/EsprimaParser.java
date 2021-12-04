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
import java.util.Set;
import java.util.stream.Collectors;

import pt.up.fe.specs.jsengine.libs.EsprimaNode;
import pt.up.fe.specs.lara.doc.aspectir.AspectIrElement;
import pt.up.fe.specs.lara.doc.aspectir.elements.ClassElement;
import pt.up.fe.specs.lara.doc.aspectir.elements.FunctionDeclElement;
import pt.up.fe.specs.lara.doc.aspectir.elements.NamedElement;
import pt.up.fe.specs.lara.doc.aspectir.elements.NamedType;
import pt.up.fe.specs.lara.doc.aspectir.elements.StatementElement;
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

    public AspectIrElement parse(EsprimaNode node, LaraDocComment laraComment) {
        var type = node.getType();
        // Check type of node
        switch (type) {
        case "ClassDeclaration":
            return parseClassDeclaration(node, laraComment);
        default:
            if (!seenUnsupportedNodes.contains(type)) {
                SpecsLogs.msgInfo("EsprimaParser does not support yet node of type '" + type + "'");
                seenUnsupportedNodes.add(type);
            }

            return new StatementElement(laraComment);
        }
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

            var element = parseClassMember(classMember);

            if (element != null) {
                classElement.addAssignment(element);
            }

        }

        return classElement;
    }

    private NamedElement parseClassMember(EsprimaNode classMember) {
        var type = classMember.getType();

        switch (type) {
        case "MethodDefinition":
            return parseMethodDefinition(classMember);
        default:
            SpecsLogs.info("parseClassMember not implemented for type '" + type + "'");
            return null;
        }

    }

    private NamedElement parseMethodDefinition(EsprimaNode method) {
        var memberLaraComment = commentParser.parse(method.getComment().getCode());

        var name = method.getAsNode("key").getAsString("name");
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

        // return null;
        return new NamedElement(name, functionElement, memberType, memberLaraComment);
    }

    private FunctionDeclElement parseFunctionExpression(EsprimaNode function, String name,
            LaraDocComment memberLaraComment) {

        var paramNames = function.getAsNodes("params").stream()
                .map(param -> param.getAsString("name"))
                .collect(Collectors.toList());

        var functionElement = new FunctionDeclElement(name, paramNames, memberLaraComment);
        return functionElement;
    }

}
