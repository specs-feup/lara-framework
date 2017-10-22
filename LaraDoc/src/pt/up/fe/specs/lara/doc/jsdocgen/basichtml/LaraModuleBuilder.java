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

package pt.up.fe.specs.lara.doc.jsdocgen.basichtml;

import java.util.List;
import java.util.stream.Collectors;

import pt.up.fe.specs.lara.doc.aspectir.AspectIrDoc;
import pt.up.fe.specs.lara.doc.aspectir.elements.AssignmentElement;
import pt.up.fe.specs.lara.doc.aspectir.elements.ClassElement;
import pt.up.fe.specs.lara.doc.aspectir.elements.FunctionDeclElement;
import pt.up.fe.specs.lara.doc.comments.LaraDocComment;
import pt.up.fe.specs.lara.doc.data.LaraDocModule;
import pt.up.fe.specs.util.SpecsStrings;
import pt.up.fe.specs.util.utilities.StringLines;

public class LaraModuleBuilder {

    private final LaraDocModule module;
    private final StringBuilder htmlCode;
    private final TocBuilder toc;

    private int currentIdCounter;

    public LaraModuleBuilder(LaraDocModule module) {
        this.module = module;
        this.htmlCode = new StringBuilder();
        this.toc = new TocBuilder("import " + module.getImportPath() + ";");

        currentIdCounter = 0;
    }

    private String nextId() {
        this.currentIdCounter++;
        return SpecsStrings.toExcelColumn(currentIdCounter);
    }

    public String getHtml() {

        if (!module.getDocumentation().isPresent()) {
            return "[no module documentation found]";
        }

        AspectIrDoc doc = module.getDocumentation().get();

        // Table of Contents
        // TocBuilder tocBuilder = new TocBuilder("import " + module.getImportPath() + ";");

        // Generate HTML for Aspects

        // Generate HTML for Classes
        for (ClassElement classElement : doc.getTopLevelElements(ClassElement.class)) {
            String id = nextId();
            htmlCode.append("<h1 id='" + id + "'>" + classElement.getClassName() + "</h1>");

            toc.addLevelOne("Classes", id, classElement.getClassName());
            // addToc(toc, classElement, id);

            LaraDocComment comment = classElement.getComment();

            if (!comment.getText().isEmpty()) {
                String text = StringLines.getLines(comment.getText()).stream().collect(Collectors.joining("<br>"));
                htmlCode.append("<p>" + text + "</p>");
            }

            // Static members
            List<AssignmentElement> staticMembers = classElement.getStaticElements();
            if (!staticMembers.isEmpty()) {
                htmlCode.append("<h2>Static Members</h2>");

                for (AssignmentElement staticMember : staticMembers) {
                    htmlCode.append(HtmlGenerators.generate(staticMember));
                }
            }

            // Instance members
            List<AssignmentElement> instanceMembers = classElement.getInstanceElements();
            if (!instanceMembers.isEmpty()) {
                htmlCode.append("<h2>Instance Members</h2>");

                for (AssignmentElement instanceMember : instanceMembers) {
                    htmlCode.append(HtmlGenerators.generate(instanceMember));
                }
            }

        }

        // Global functions
        List<FunctionDeclElement> functionDecls = doc.getTopLevelElements(FunctionDeclElement.class);
        if (!functionDecls.isEmpty()) {
            htmlCode.append("<h2>Global Functions</h2>");
            for (FunctionDeclElement functionDecl : functionDecls) {
                htmlCode.append(HtmlGenerators.generate(functionDecl));
            }
        }

        // Add Global assignments?

        // Add Global vardecls?

        // htmlCode.append("<em>Hello!</em> Elements -> " + doc);

        return toc.getHtml() + htmlCode.toString();

        // File moduleHtml = new File(outputFolder, "module.html");
        // String finalHtml = tocBuilder.getHtml() + htmlCode.toString();

        // SpecsIo.write(moduleHtml, finalHtml);
        //
        // return Optional.of(moduleHtml);
    }

}
