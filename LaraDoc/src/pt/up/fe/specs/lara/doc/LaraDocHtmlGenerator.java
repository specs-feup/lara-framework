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

package pt.up.fe.specs.lara.doc;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

import pt.up.fe.specs.lara.doc.data.LaraDocBundle;
import pt.up.fe.specs.lara.doc.data.LaraDocFiles;
import pt.up.fe.specs.lara.doc.data.LaraDocModule;
import pt.up.fe.specs.lara.doc.data.LaraDocPackage;
import pt.up.fe.specs.lara.doc.jsdocgen.JsDocGenerator;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.utilities.Replacer;

public class LaraDocHtmlGenerator {

    private final JsDocGenerator generator;
    private final File outputFolder;
    private final File jsTemporaryFolder;
    private final LaraToJs converter;
    private int counter;

    private final Deque<String> currentModulePath;

    public LaraDocHtmlGenerator(JsDocGenerator generator, File outputFolder) {
        this.generator = generator;
        this.outputFolder = outputFolder;
        this.currentModulePath = new ArrayDeque<>();
        this.counter = 0;
        // Generate documentation
        this.jsTemporaryFolder = SpecsIo.mkdir(SpecsIo.getTempFolder(), "laradoc-js");
        SpecsIo.deleteFolderContents(jsTemporaryFolder);

        this.converter = new LaraToJs(jsTemporaryFolder);
    }

    public void generateDoc(LaraDocFiles laraDocFiles) {
        StringBuilder moduleList = new StringBuilder();

        // Add code for bundles
        for (LaraDocBundle bundle : laraDocFiles.getBundles()) {
            moduleList.append(generateDoc(bundle));
        }

        // Add code for packages
        for (LaraDocPackage laraPackage : laraDocFiles.getPackages()) {
            moduleList.append(generateDoc(laraPackage));
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

    private String generateDoc(LaraDocBundle bundle) {
        StringBuilder bundleCode = new StringBuilder();

        if (bundle.getPackages().isEmpty()) {
            return "";
        }

        bundleCode.append("<h1>Bundle <em>" + bundle.getBundleName() + "</em></h1>\n");
        bundleCode.append("<ul>\n");

        currentModulePath.push("bundle_" + bundle.getBundleName());
        for (LaraDocPackage laraPackage : bundle.getPackages()) {
            bundleCode.append("<li>\n");
            bundleCode.append(generateDoc(laraPackage));
            bundleCode.append("</li>\n");
        }
        currentModulePath.pop();

        bundleCode.append("</ul>\n");

        return bundleCode.toString();
    }

    private String generateDoc(LaraDocPackage laraPackage) {
        StringBuilder packageCode = new StringBuilder();

        packageCode.append("<h2>" + laraPackage.getPackageName() + "</h2>");
        packageCode.append("<ul>");

        currentModulePath.push("package_" + laraPackage.getPackageName());

        // Order modules by import name

        List<LaraDocModule> modules = new ArrayList<>(laraPackage.getModules());
        Collections.sort(modules, (o1, o2) -> o1.getImportPath().compareTo(o2.getImportPath()));

        // Map<String, LaraDocModule> modules = laraPackage.getModules().stream()
        // .collect(Collectors.toMap(LaraDocModule::getImportPath, module -> module));
        //
        // List<String> importPaths = new ArrayList<>(modules.keySet());
        // Collections.sort(importPaths);

        for (LaraDocModule module : modules) {
            // for (LaraDocModule module : laraPackage.getModules()) {
            // for (String importPath : importPaths) {
            // LaraDocModule module = modules.get(importPath);
            packageCode.append("<li>\n");
            packageCode.append(generateDoc(module));
            packageCode.append("</li>\n");
        }
        currentModulePath.pop();

        packageCode.append("</ul>");
        return packageCode.toString();
    }

    private String generateDoc(LaraDocModule module) {

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
    }

}
