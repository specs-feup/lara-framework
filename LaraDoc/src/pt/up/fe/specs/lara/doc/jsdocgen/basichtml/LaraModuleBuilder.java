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

import java.util.ArrayList;
import java.util.List;

import pt.up.fe.specs.lara.doc.aspectir.AspectIrDoc;
import pt.up.fe.specs.lara.doc.aspectir.AspectIrElement;
import pt.up.fe.specs.lara.doc.aspectir.elements.AspectElement;
import pt.up.fe.specs.lara.doc.aspectir.elements.AssignmentElement;
import pt.up.fe.specs.lara.doc.aspectir.elements.ClassElement;
import pt.up.fe.specs.lara.doc.aspectir.elements.FunctionDeclElement;
import pt.up.fe.specs.lara.doc.data.LaraDocModule;
import pt.up.fe.specs.util.SpecsStrings;

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
            return "";
            // return "[no module documentation found]";
        }

        AspectIrDoc doc = module.getDocumentation().get();

        // Table of Contents
        // TocBuilder tocBuilder = new TocBuilder("import " + module.getImportPath() + ";");

        // Generate HTML for Aspects
        List<AspectElement> aspects = doc.getTopLevelElements(AspectElement.class);
        AspectIrElement.sort(aspects);
        if (!aspects.isEmpty()) {
            htmlCode.append("<h2>Aspects</h2>");
            for (AspectElement aspect : aspects) {
                String globalAspectId = nextId();
                toc.addLevelOne("Aspects", globalAspectId, aspect.getAspectName());
                htmlCode.append(HtmlGenerators.generateAspect(globalAspectId, aspect.getComment()));
            }
        }

        // Generate HTML for Classes
        List<ClassElement> classes = doc.getTopLevelElements(ClassElement.class);
        AspectIrElement.sort(classes);

        for (ClassElement classElement : classes) {
            String classId = nextId();
            htmlCode.append("<h1 id='" + classId + "'>" + classElement.getClassName() + "</h1>");

            toc.addLevelOne("Classes", classId, classElement.getClassName());

            boolean isConstructor = !classElement.getInstanceElements().isEmpty();
            String classCode = HtmlGenerators.generateMember(classId, classElement.getComment(), isConstructor,
                    isConstructor);
            htmlCode.append(classCode);
            // if (!comment.getText().isEmpty()) {
            // String text = StringLines.getLines(comment.getText()).stream().collect(Collectors.joining("<br>"));
            // htmlCode.append("<p>" + text + "</p>");
            // }

            // Static members
            List<AssignmentElement> staticMembers = classElement.getStaticElements();
            AspectIrElement.sort(staticMembers);

            List<String> staticIds = new ArrayList<>();
            List<String> staticNames = new ArrayList<>();
            if (!staticMembers.isEmpty()) {

                htmlCode.append("<h2>Static Members</h2>");

                for (AssignmentElement staticMember : staticMembers) {
                    String staticId = nextId();
                    staticIds.add(staticId);
                    staticNames.add(staticMember.getNamePath());

                    boolean isFunction = staticMember.getRightFunctionDecl().isPresent();
                    htmlCode.append(
                            HtmlGenerators.generateMember(staticId, staticMember.getComment(), isFunction, false));
                    // htmlCode.append(HtmlGenerators.generateAssignment(staticMember, staticId));
                }
            }

            toc.addSubList(staticIds, staticNames, "Static Members");

            // Instance members
            List<String> instanceIds = new ArrayList<>();
            List<String> instanceNames = new ArrayList<>();
            List<AssignmentElement> instanceMembers = classElement.getInstanceElements();
            AspectIrElement.sort(instanceMembers);

            if (!instanceMembers.isEmpty()) {
                htmlCode.append("<h2>Instance Members</h2>");

                for (AssignmentElement instanceMember : instanceMembers) {
                    String instanceId = nextId();
                    instanceIds.add(instanceId);
                    instanceNames.add(instanceMember.getNamePath());
                    // htmlCode.append(HtmlGenerators.generateAssignment(instanceMember, instanceId));

                    boolean isFunction = instanceMember.getRightFunctionDecl().isPresent();
                    htmlCode.append(
                            HtmlGenerators.generateMember(instanceId, instanceMember.getComment(), isFunction, false));

                }
            }

            toc.addSubList(instanceIds, instanceNames, "Instance Members");

        }

        // Global functions
        List<FunctionDeclElement> functionDecls = doc.getTopLevelElements(FunctionDeclElement.class);
        AspectIrElement.sort(functionDecls);

        if (!functionDecls.isEmpty()) {
            htmlCode.append("<h2>Global Functions</h2>");
            for (FunctionDeclElement functionDecl : functionDecls) {
                String globalFunctionId = nextId();
                toc.addLevelOne("Global Functions", globalFunctionId, functionDecl.getFunctionName());
                htmlCode.append(HtmlGenerators.generateMember(globalFunctionId, functionDecl.getComment()));
            }
        }

        // System.out.println("MODULE:" + module.getImportPath());
        // System.out.println("TOP LEVEL:" + doc.getTopLevelElements().stream()
        // .map(elem -> elem.getClass().getSimpleName()).collect(Collectors.toSet()));
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
