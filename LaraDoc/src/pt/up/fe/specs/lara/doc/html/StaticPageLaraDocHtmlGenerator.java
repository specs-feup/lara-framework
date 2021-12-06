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

package pt.up.fe.specs.lara.doc.html;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.lara.language.specification.LanguageSpecification;
import org.lara.language.specification.ast.EnumDefNode;
import org.lara.language.specification.ast.JoinPointNode;
import org.lara.language.specification.ast.NodeFactory;
import org.lara.language.specification.ast.RootNode;
import org.lara.language.specification.ast.TypeDefNode;

import pt.up.fe.specs.lara.doc.LaraDocResource;
import pt.up.fe.specs.lara.doc.data.LaraDocBundle;
import pt.up.fe.specs.lara.doc.data.LaraDocFiles;
import pt.up.fe.specs.lara.doc.data.LaraDocModule;
import pt.up.fe.specs.lara.doc.data.LaraDocNode;
import pt.up.fe.specs.lara.doc.data.LaraDocPackage;
import pt.up.fe.specs.lara.doc.data.LaraDocTop;
import pt.up.fe.specs.lara.doc.jsdocgen.JsDocGenerator;
import pt.up.fe.specs.util.SpecsCollections;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.utilities.Replacer;

public class StaticPageLaraDocHtmlGenerator {

    private final JsDocGenerator generator;
    private final File outputFolder;
    private final File jsTemporaryFolder;
    private int counter;

    private final Deque<String> currentModulePath;

    public StaticPageLaraDocHtmlGenerator(JsDocGenerator generator, File outputFolder) {
        this.generator = generator;
        this.outputFolder = outputFolder;

        this.currentModulePath = new ArrayDeque<>();
        this.counter = 0;
        // Generate documentation
        this.jsTemporaryFolder = SpecsIo.mkdir(SpecsIo.getTempFolder(), "laradoc-js");
        SpecsIo.deleteFolderContents(jsTemporaryFolder);

    }

    public void generateDoc(LaraDocFiles laraDocFiles) {
        StringBuilder moduleList = new StringBuilder();

        // Add code for packages
        for (LaraDocPackage laraPackage : laraDocFiles.getPackages()) {
            moduleList.append(generateDoc(laraPackage));
        }

        // Add code for bundles
        for (LaraDocBundle bundle : laraDocFiles.getBundles()) {
            moduleList.append(generateDoc(bundle));
        }

        LaraDocResource.JQUERY.write(outputFolder);
        LaraDocResource.JS_DOC_JS.write(outputFolder);
        LaraDocResource.STYLES_CSS.write(outputFolder);

        Replacer indexContents = new Replacer(LaraDocResource.INDEX_HTML);
        indexContents.replace("[[IMPORT_COLUMN]]", moduleList.toString());
        SpecsIo.write(new File(outputFolder, "index.html"), indexContents.toString());
        // generator.generate();
        // Call generator in "batch" to all folders with generate JS
        // SpecsIo.getFolders(jsTemporaryFolder).
        // File moduleDoc = SpecsIo.mkdir(outputFolder, folderName);

        // generator.generate(SpecsIo.getFiles(jsTemporaryFolder), moduleDoc);

        // System.out.println("COLUMN HTML:\n" + moduleList);

    }

    public void generateDoc(LaraDocTop laraDocTop) {
        generateDoc(laraDocTop, null);
    }

    public void generateDoc(LaraDocTop laraDocTop, LanguageSpecification languageSpecification) {

        // Get direct children
        List<LaraDocNode> laraDocNodes = new ArrayList<>(laraDocTop.getChildren());

        // Add code for packages
        List<LaraDocPackage> laraDocPackages = SpecsCollections.remove(laraDocNodes, LaraDocPackage.class);

        // Sort packages alphabetically
        Collections.sort(laraDocPackages);

        var moduleList = buildImportsSidebar(languageSpecification, laraDocPackages);

        // System.out.println("MODULE IMPORTS: " + moduleList);

        // // Add code for bundles
        // List<LaraDocBundle> laraDocBundles = SpecsCollections.remove(laraDocNodes, LaraDocBundle.class);
        //
        // for (LaraDocBundle bundle : laraDocBundles) {
        // moduleList.append(generateDoc(bundle));
        // }

        // Check if there is any class missing
        if (!laraDocNodes.isEmpty()) {
            SpecsLogs.warn("List of documentation nodes is not empty, check this: " + laraDocNodes);
        }

        Replacer indexContents = new Replacer(LaraDocResource.INDEX_HTML);
        indexContents.replace("[[IMPORT_COLUMN]]", moduleList.toString());
        var basePage = indexContents.toString();

        // Write index HTML and resources
        SpecsIo.write(new File(outputFolder, "index.html"), basePage);

        LaraDocResource.JQUERY.write(outputFolder);
        LaraDocResource.JS_DOC_JS.write(outputFolder);
        LaraDocResource.STYLES_CSS.write(outputFolder);

        // Write HTML page for each module
        writeStaticPages(basePage, languageSpecification, laraDocPackages);

    }

    private void writeStaticPages(String basePage, LanguageSpecification languageSpecification,
            List<LaraDocPackage> laraDocPackages) {

        for (LaraDocPackage laraPackage : laraDocPackages) {
            if (!generator.getNameFilter().test(laraPackage.getPackageName())) {
                continue;
            }

            writeStaticPages(basePage, laraPackage);
        }

        // Generate base static page

        // // Add language specification, if present
        // if (languageSpecification != null) {
        // RootNode rootNode = NodeFactory.toNode(languageSpecification);
        // moduleList.append(buildImportSidebar(rootNode));
        // }

    }

    private void writeStaticPages(String basePage, LaraDocNode laraDocNode) {
        // if (!laraDocNode.hasChildren()) {
        // return;
        // }

        if (!generator.getNameFilter().test(laraDocNode.getId())) {
            return;
        }

        if (laraDocNode instanceof LaraDocModule) {
            writeStaticPage(basePage, (LaraDocModule) laraDocNode);
            return;
        }

        if (laraDocNode instanceof LaraDocPackage) {
            laraDocNode.getChildren().stream()
                    .forEach(child -> writeStaticPages(basePage, child));
            return;
        }

        System.out.println("writeStaticPages: Not yet implemented for " + laraDocNode.getClass());
    }

    private void writeStaticPage(String basePage, LaraDocModule laraDocNode) {

        var moduleHtml = generator.generate(laraDocNode);
        var staticPage = basePage.replace("<div id=\"doc_content\"></div>",
                "<div id=\"doc_content\">" + moduleHtml + "</div>");

        var filename = getModuleHtmlFilename(laraDocNode);
        var outputFile = new File(outputFolder, filename);
        SpecsIo.write(outputFile, staticPage);
    }

    private StringBuilder buildImportsSidebar(LanguageSpecification languageSpecification,
            List<LaraDocPackage> laraDocPackages) {

        // Generate import list
        StringBuilder moduleList = new StringBuilder();

        for (LaraDocPackage laraPackage : laraDocPackages) {
            if (!generator.getNameFilter().test(laraPackage.getPackageName())) {
                continue;
            }

            moduleList.append(buildImportSidebar(laraPackage));
        }

        // Generate base static page

        // Add language specification, if present
        if (languageSpecification != null) {
            RootNode rootNode = NodeFactory.toNode(languageSpecification);
            moduleList.append(buildImportSidebar(rootNode));
        }
        return moduleList;
    }

    private String generateDoc(RootNode languageSpecificationRoot) {
        if (languageSpecificationRoot == null) {
            return "";
        }

        StringBuilder languageSpecCode = new StringBuilder();

        StringBuilder joinPoints = getJoinpointsList(languageSpecificationRoot);
        StringBuilder typeDefs = getTypeDefsList(languageSpecificationRoot);
        StringBuilder enumDefs = getEnumDefsList(languageSpecificationRoot);

        // Add join points
        if (joinPoints.length() > 0) {
            languageSpecCode.append("<h2>Join Points</h2>");
            languageSpecCode.append("<ul>");
            languageSpecCode.append(joinPoints);
            languageSpecCode.append("</ul>");

        }
        // Add join points
        if (typeDefs.length() > 0) {
            languageSpecCode.append("<h2>TypeDefs</h2>");
            languageSpecCode.append("<ul>");
            languageSpecCode.append(typeDefs);
            languageSpecCode.append("</ul>");

        }
        // Add join points
        if (enumDefs.length() > 0) {
            languageSpecCode.append("<h2>Enums</h2>");
            languageSpecCode.append("<ul>");
            languageSpecCode.append(enumDefs);
            languageSpecCode.append("</ul>");

        }

        return languageSpecCode.toString();

        /*
        String folderName = Integer.toString(counter);
        counter++;
        
        File moduleDocFolder = SpecsIo.mkdir(outputFolder, folderName);
        
        Optional<File> indexFile = generator.generate(module, moduleDocFolder);
        
        if (!indexFile.isPresent()) {
            return module.getImportPath();
        }
        
        String indexRelativePath = SpecsIo.getRelativePath(indexFile.get(), outputFolder);
        
        String moduleTemplate = "<a onclick=\"update_doc('" + indexRelativePath + " ')\" href=\"#\">"
                + module.getImportPath() + "</a>";
        
        return moduleTemplate;
        */
    }

    private String buildImportSidebar(RootNode languageSpecificationRoot) {
        if (languageSpecificationRoot == null) {
            return "";
        }

        StringBuilder languageSpecCode = new StringBuilder();

        StringBuilder joinPoints = getJoinpointsList(languageSpecificationRoot);
        StringBuilder typeDefs = getTypeDefsList(languageSpecificationRoot);
        StringBuilder enumDefs = getEnumDefsList(languageSpecificationRoot);

        // Add join points
        if (joinPoints.length() > 0) {
            languageSpecCode.append("<h2>Join Points</h2>");
            languageSpecCode.append("<ul>");
            languageSpecCode.append(joinPoints);
            languageSpecCode.append("</ul>");

        }
        // Add join points
        if (typeDefs.length() > 0) {
            languageSpecCode.append("<h2>TypeDefs</h2>");
            languageSpecCode.append("<ul>");
            languageSpecCode.append(typeDefs);
            languageSpecCode.append("</ul>");

        }
        // Add join points
        if (enumDefs.length() > 0) {
            languageSpecCode.append("<h2>Enums</h2>");
            languageSpecCode.append("<ul>");
            languageSpecCode.append(enumDefs);
            languageSpecCode.append("</ul>");

        }

        return languageSpecCode.toString();

        /*
        String folderName = Integer.toString(counter);
        counter++;
        
        File moduleDocFolder = SpecsIo.mkdir(outputFolder, folderName);
        
        Optional<File> indexFile = generator.generate(module, moduleDocFolder);
        
        if (!indexFile.isPresent()) {
            return module.getImportPath();
        }
        
        String indexRelativePath = SpecsIo.getRelativePath(indexFile.get(), outputFolder);
        
        String moduleTemplate = "<a onclick=\"update_doc('" + indexRelativePath + " ')\" href=\"#\">"
                + module.getImportPath() + "</a>";
        
        return moduleTemplate;
        */
    }

    public StringBuilder getJoinpointsList(RootNode languageSpecificationRoot) {
        StringBuilder joinPoints = new StringBuilder();
        for (JoinPointNode jp : languageSpecificationRoot.getChildrenOf(JoinPointNode.class)) {
            String folderName = Integer.toString(counter);
            counter++;

            File jpFolder = SpecsIo.mkdir(outputFolder, folderName);

            Optional<File> indexFile = generator.generate(jp, jpFolder);
            if (!indexFile.isPresent()) {
                continue;
            }

            String indexRelativePath = SpecsIo.getRelativePath(indexFile.get(), outputFolder);

            joinPoints.append("<li><a onclick=\"update_doc('" + indexRelativePath + " ')\" href=\"#\">"
                    + jp.getName() + "</a></li>");
        }
        return joinPoints;
    }

    public StringBuilder getTypeDefsList(RootNode languageSpecificationRoot) {
        StringBuilder joinPoints = new StringBuilder();
        for (TypeDefNode tdef : languageSpecificationRoot.getChildrenOf(TypeDefNode.class)) {
            String folderName = Integer.toString(counter);
            counter++;

            File jpFolder = SpecsIo.mkdir(outputFolder, folderName);

            Optional<File> indexFile = generator.generate(tdef, jpFolder);
            if (!indexFile.isPresent()) {
                continue;
            }

            String indexRelativePath = SpecsIo.getRelativePath(indexFile.get(), outputFolder);

            joinPoints.append("<li><a onclick=\"update_doc('" + indexRelativePath + " ')\" href=\"#\">"
                    + tdef.getName() + "</a></li>");
        }
        return joinPoints;
    }

    public StringBuilder getEnumDefsList(RootNode languageSpecificationRoot) {
        StringBuilder joinPoints = new StringBuilder();
        for (EnumDefNode tdef : languageSpecificationRoot.getChildrenOf(EnumDefNode.class)) {
            String folderName = Integer.toString(counter);
            counter++;

            File jpFolder = SpecsIo.mkdir(outputFolder, folderName);

            Optional<File> indexFile = generator.generate(tdef, jpFolder);
            if (!indexFile.isPresent()) {
                continue;
            }

            String indexRelativePath = SpecsIo.getRelativePath(indexFile.get(), outputFolder);

            joinPoints.append("<li><a onclick=\"update_doc('" + indexRelativePath + " ')\" href=\"#\">"
                    + tdef.getName() + "</a></li>");
        }
        return joinPoints;
    }

    private String generateDoc(LaraDocBundle bundle) {
        if (!bundle.hasChildren()) {
            return "";
        }

        StringBuilder bundleCode = new StringBuilder();

        bundleCode.append("<h2>Bundle <em>" + bundle.getBundleName() + "</em></h2>\n");
        bundleCode.append("<ul>\n");

        currentModulePath.push("bundle_" + bundle.getBundleName());

        processChildren(bundle, bundleCode);
        // for (LaraDocPackage laraPackage : bundle.getPackages()) {
        // bundleCode.append("<li>\n");
        // bundleCode.append(generateDoc(laraPackage));
        // bundleCode.append("</li>\n");
        // }
        currentModulePath.pop();

        bundleCode.append("</ul>\n");

        return bundleCode.toString();
    }

    private String generateDoc(LaraDocPackage laraPackage) {
        if (!laraPackage.hasChildren()) {
            return "";
        }

        StringBuilder packageCode = new StringBuilder();

        packageCode.append("<h2>" + laraPackage.getPackageName() + "</h2>");
        packageCode.append("<ul>");

        currentModulePath.push("package_" + laraPackage.getPackageName());

        processChildren(laraPackage, packageCode);

        currentModulePath.pop();

        packageCode.append("</ul>");

        return packageCode.toString();
    }

    private String buildImportSidebar(LaraDocPackage laraPackage) {
        if (!laraPackage.hasChildren()) {
            return "";
        }

        StringBuilder packageCode = new StringBuilder();

        packageCode.append("<h2>" + laraPackage.getPackageName() + "</h2>");
        packageCode.append("<ul>");

        currentModulePath.push("package_" + laraPackage.getPackageName());

        packageCode.append(getImportList(laraPackage));
        // processChildren(laraPackage, packageCode);

        currentModulePath.pop();

        packageCode.append("</ul>");

        return packageCode.toString();
    }

    private String getImportList(LaraDocNode laraNode) {

        // Get direct children of node that pass the name filter
        List<LaraDocNode> nodeChildren = laraNode.getChildrenStream()
                .filter(child -> generator.getNameFilter().test(child.getId()))
                .collect(Collectors.toList());

        // var modules = SpecsCollections.get(nodeChildren, LaraDocModule.class);
        var modules = SpecsCollections.remove(nodeChildren, LaraDocModule.class);

        // Order modules by import name
        Collections.sort(modules);

        // String moduleTemplate = "<a href=\"" + indexRelativePath + "\">"
        // + module.getImportPath() + "</a>";

        var htmlCode = new StringBuilder();

        for (LaraDocModule module : modules) {

            htmlCode.append("<li>\n");
            htmlCode.append(getImportLink(module));
            htmlCode.append("</li>\n");
        }

        // // Get bundles
        // List<LaraDocBundle> bundles = SpecsCollections.remove(nodeChildren, LaraDocBundle.class);
        //
        // // Order bundles by name
        // Collections.sort(bundles);
        //
        // for (LaraDocBundle bundle : bundles) {
        // // packageCode.append("<li>\n");
        // htmlCode.append(generateDoc(bundle));
        // // packageCode.append("</li>\n");
        // }
        //
        // // Get packages
        // List<LaraDocPackage> packages = SpecsCollections.remove(nodeChildren, LaraDocPackage.class);
        //
        // // Order packages by name
        // Collections.sort(packages);
        //
        // for (LaraDocPackage laraPackage : packages) {
        // htmlCode.append(generateDoc(laraPackage));
        // }

        // Check
        if (!nodeChildren.isEmpty()) {
            SpecsLogs.warn("Nodes not supported: "
                    + nodeChildren.stream().map(child -> child.getClass().getName()).collect(Collectors.toSet()));
        }

        return htmlCode.toString();
    }

    private List<LaraDocNode> processChildren(LaraDocNode laraNode, StringBuilder htmlCode) {

        // Get direct children of node that pass the name filter
        List<LaraDocNode> nodeChildren = laraNode.getChildrenStream()
                .filter(child -> generator.getNameFilter().test(child.getId()))
                .collect(Collectors.toList());

        // Get modules
        List<LaraDocModule> modules = SpecsCollections.remove(nodeChildren, LaraDocModule.class);

        // Order modules by import name
        // Collections.sort(modules, (o1, o2) -> o1.getImportPath().compareTo(o2.getImportPath()));
        Collections.sort(modules);

        // var sidebar = new StringBuilder();
        // for (LaraDocModule module : modules) {
        //
        // sidebar.append("<li>\n");
        // sidebar.append(generateDoc(module));
        // sidebar.append("</li>\n");
        // }

        // String moduleTemplate = "<a href=\"" + indexRelativePath + "\">"
        // + module.getImportPath() + "</a>";

        for (LaraDocModule module : modules) {

            htmlCode.append("<li>\n");
            htmlCode.append(generateDoc(module));
            htmlCode.append("</li>\n");
        }

        // Get bundles
        List<LaraDocBundle> bundles = SpecsCollections.remove(nodeChildren, LaraDocBundle.class);

        // Order bundles by name
        // Collections.sort(bundles, (o1, o2) -> o1.getBundleName().compareTo(o2.getBundleName()));
        Collections.sort(bundles);

        for (LaraDocBundle bundle : bundles) {
            // packageCode.append("<li>\n");
            htmlCode.append(generateDoc(bundle));
            // packageCode.append("</li>\n");
        }

        // Get packages
        List<LaraDocPackage> packages = SpecsCollections.remove(nodeChildren, LaraDocPackage.class);

        // Order packages by name
        // Collections.sort(packages, (o1, o2) -> o1.getPackageName().compareTo(o2.getPackageName()));
        Collections.sort(packages);

        for (LaraDocPackage laraPackage : packages) {
            htmlCode.append(generateDoc(laraPackage));
        }

        // Check
        if (!nodeChildren.isEmpty()) {
            SpecsLogs.warn("Nodes not supported: "
                    + nodeChildren.stream().map(child -> child.getClass().getName()).collect(Collectors.toSet()));
        }

        return nodeChildren;
    }

    private String generateDoc(LaraDocModule module) {

        // String folderName = Integer.toString(counter);
        // counter++;

        var folderName = module.getImportPath();

        File moduleDocFolder = SpecsIo.mkdir(outputFolder, folderName);

        Optional<File> indexFile = generator.generate(module, moduleDocFolder);

        if (!indexFile.isPresent()) {
            return module.getImportPath();
        }

        String indexRelativePath = SpecsIo.getRelativePath(indexFile.get(), outputFolder);

        String moduleTemplate = "<a href=\"" + indexRelativePath + "\">"
                + module.getImportPath() + "</a>";

        return moduleTemplate;
    }

    private String getImportLink(LaraDocModule module) {

        var moduleHtmlFilename = getModuleHtmlFilename(module);

        String moduleTemplate = "<a onclick=\"saveScroll()\"  href=\"" + moduleHtmlFilename + "\">"
                + module.getImportPath() + "</a>";

        return moduleTemplate;
    }

    private String getModuleHtmlFilename(LaraDocModule module) {
        return module.getImportPath() + ".html";
    }

}
