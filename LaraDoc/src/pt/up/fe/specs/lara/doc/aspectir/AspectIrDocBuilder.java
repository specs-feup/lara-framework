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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import pt.up.fe.specs.jsengine.libs.EsprimaNode;
import pt.up.fe.specs.jsengine.libs.JsEsprima;
import pt.up.fe.specs.lara.aspectir.Aspect;
import pt.up.fe.specs.lara.aspectir.Aspects;
import pt.up.fe.specs.lara.aspectir.Statement;
import pt.up.fe.specs.lara.doc.aspectir.elements.ClassElement;
import pt.up.fe.specs.lara.doc.comments.LaraCommentsParser;
import pt.up.fe.specs.lara.doc.comments.LaraDocComment;
import pt.up.fe.specs.lara.doc.esprima.EsprimaParser;
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTagName;
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTagProperty;
import pt.up.fe.specs.util.SpecsIo;

/**
 * Incrementally builds an AspectIrDoc.
 * 
 * @author joaobispo
 *
 */
public class AspectIrDocBuilder {

    private final LaraCommentsParser commentParser;
    private final AspectIrParser aspectIrParser;
    private final EsprimaParser esprimaParser;
    private final List<AspectIrElement> aspectIrElements;

    public AspectIrDocBuilder() {
        this.commentParser = new LaraCommentsParser();
        this.aspectIrParser = new AspectIrParser();
        this.esprimaParser = new EsprimaParser();

        aspectIrElements = new ArrayList<>();
    }

    public List<AspectIrElement> getAspectIrElements() {
        return aspectIrElements;
    }

    public void parse(Aspects aspects) {

        // For each element, create LaraDocComment

        for (Aspect aspect : aspects.aspects.values()) {
            LaraDocComment laraComment = commentParser.parse(aspect.comment);

            AspectIrElement aspectIrElement = aspectIrParser.parse(aspect, laraComment);
            aspectIrElements.add(aspectIrElement);
        }

        for (Statement declaration : aspects.declarations) {
            // System.out.println("Declaration class:" +
            // declaration.getClass().getSimpleName());
            // if (declaration.name.equals("expr")) {
            // System.out.println("Declaration Name:" + declaration.name);
            // }
            // System.out.println("Declaration Name:" + declaration.name);

            LaraDocComment laraComment = commentParser.parse(declaration.comment);

            AspectIrElement aspectIrElement = aspectIrParser.parse(declaration, laraComment);
            aspectIrElements.add(aspectIrElement);
            // System.out.println("Declaration Comment:" + declaration.comment);
            // System.out.println("Declaration Desc:" + declaration.desc);

            // Extract info from the comment

            // Extract info from the declaration node
            // e.g., if function, extract param names?
        }

    }

    public AspectIrDoc build() {
        return AspectIrDoc.newInstance(aspectIrElements);
    }

    public void addImportPath(String importPath) {
        // JsDocTag importTag = new JsDocTag(JsDocTagName.IMPORT).setValue(JsDocTagProperty.NAME_PATH, importPath);
        // Add import tag to class elements
        aspectIrElements.stream()
                .filter(ClassElement.class::isInstance)
                // .forEach(classElement -> classElement.getComment().addTagIfMissing(importTag));
                .forEach(classElement -> classElement.getComment()
                        .getTag(JsDocTagName.IMPORT)
                        .setValueIfMissing(JsDocTagProperty.NAME_PATH, importPath));

    }

    public void parseJs(String jsCode) {
        var program = JsEsprima.parse(jsCode);
        SpecsIo.write(new File("output.txt"), new Gson().toJson(program.getNode()));
        // System.out.println(program.getChildren().size());

        // Collect nodes that should be documented
        List<EsprimaNode> documentedNodes = new ArrayList<>();

        getDocumentedNodes(program, documentedNodes);

        // Add documentation for top-level elements
        for (var node : documentedNodes) {
            // System.out.println("NODE: " + node.getType());

            // One declaration can have one or more declarators
            if (node.getType().equals("VariableDeclaration")) {
                for (var declarator : node.getAsNodes("declarations")) {
                    // Use comment from declaration as base
                    LaraDocComment laraComment = commentParser.parse(node.getComment().getCode());

                    // Add information to the comment based on the node
                    esprimaParser.parseTry(declarator, laraComment).ifPresent(aspectIrElements::add);
                }

                continue;
            }

            // First create and populate a comment object based on an existing comment, if present
            LaraDocComment laraComment = commentParser.parse(node.getComment().getCode());
            // System.out.println("NODE: " + node);
            // System.out.println("COMMENT: " + node.getComment().getCode());
            // System.out.println("LARA DOC COMMENT: " + laraComment);

            // Add information to the comment based on the node
            esprimaParser.parseTry(node, laraComment).ifPresent(aspectIrElements::add);
            // AspectIrElement aspectIrElement = esprimaParser.parse(node, laraComment);
            // aspectIrElements.add(aspectIrElement);

        }

        // if (true) {
        // throw new RuntimeException("STOP");
        // }

        // // For each element, create LaraDocComment
        // for (Aspect aspect : aspects.aspects.values()) {
        // LaraDocComment laraComment = commentParser.parse(aspect.comment);
        //
        // AspectIrElement aspectIrElement = aspectIrParser.parse(aspect, laraComment);
        // aspectIrElements.add(aspectIrElement);
        // }
        //
        // for (Statement declaration : aspects.declarations) {
        // LaraDocComment laraComment = commentParser.parse(declaration.comment);
        //
        // AspectIrElement aspectIrElement = aspectIrParser.parse(declaration, laraComment);
        // aspectIrElements.add(aspectIrElement);
        // }

    }

    private void getDocumentedNodes(EsprimaNode node, List<EsprimaNode> documentedNodes) {

        switch (node.getType()) {
        case "Program":
            node.getAsNodes("body").stream().forEach(child -> getDocumentedNodes(child, documentedNodes));
            return;
        case "ClassDeclaration":
        case "FunctionDeclaration":
        case "VariableDeclaration":
        case "ExpressionStatement":
            // Add self
            documentedNodes.add(node);

            // NOT: They are handled by the parser
            // Add elements of the class
            // node.getAsNode("body").getAsNodes("body").forEach(child -> getDocumentedNodes(child, documentedNodes));
            return;

        // Add self
        // documentedNodes.add(node);
        // return;
        default:
            System.out.println("JS node not being handled: " + node.getType());
            // Do nothing
        }

    }
}
