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

package pt.up.fe.specs.lara.doc.parser;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import org.lara.interpreter.weaver.defaultweaver.DefaultWeaver;
import org.lara.language.specification.LanguageSpecification;

import larai.larabundle.LaraBundle;
import larai.lararesource.LaraResource;
import pt.up.fe.specs.lara.aspectir.Aspects;
import pt.up.fe.specs.lara.doc.LaraDocs;
import pt.up.fe.specs.lara.doc.LaraToJs;
import pt.up.fe.specs.lara.doc.aspectir.AspectIrDocBuilder;
import pt.up.fe.specs.lara.doc.data.LaraDocBundle;
import pt.up.fe.specs.lara.doc.data.LaraDocJs;
import pt.up.fe.specs.lara.doc.data.LaraDocModule;
import pt.up.fe.specs.lara.doc.data.LaraDocNode;
import pt.up.fe.specs.lara.doc.data.LaraDocPackage;
import pt.up.fe.specs.lara.doc.data.LaraDocTop;
import pt.up.fe.specs.util.Preconditions;
import pt.up.fe.specs.util.SpecsCollections;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.collections.MultiMap;
import pt.up.fe.specs.util.properties.SpecsProperties;

/**
 * Parses documentation from .lara and .js files.
 * 
 * @author JoaoBispo
 *
 */
public class LaraDocParser {

    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList("lara"));

    // private static boolean defaultNameFilter(String name) {
    // if (name.startsWith("_")) {
    // return false;
    // }
    //
    // return true;
    // }

    private final MultiMap<String, File> packagesPaths;
    // Filters folders/files based on name
    private final Predicate<String> nameFilter;
    private final LanguageSpecification languageSpecification;

    public LaraDocParser() {
        // this(LaraDocParser::defaultNameFilter);
        this(null, new DefaultWeaver().getLanguageSpecification());
    }

    public LaraDocParser(Predicate<String> nameFilter, LanguageSpecification languageSpecification) {
        this.packagesPaths = new MultiMap<>();
        this.nameFilter = nameFilter;
        this.languageSpecification = languageSpecification;
    }

    public LaraDocParser addPath(String packageName, File path) {
        if (!path.exists()) {
            SpecsLogs.msgInfo("Given input path '" + path + "' for package '" + packageName + "' does not exist");
            return this;
        }

        packagesPaths.put(packageName, path);

        return this;
    }

    public LaraDocTop buildLaraDoc() {

        // Collect information
        LaraDocTop laraDocumentation = collectInformation();

        // Clean tree
        clean(laraDocumentation);

        // Build documentation information
        buildDocumentationTags(laraDocumentation);

        return laraDocumentation;
    }

    private void clean(LaraDocTop laraDocumentation) {
        // Remove modules that do not have main file
        List<LaraDocModule> modules = SpecsCollections.toList(laraDocumentation.getDescendantsStream(),
                LaraDocModule.class);

        for (LaraDocModule module : modules) {
            if (module.hasMainLara()) {
                continue;
            }

            // Remove module
            module.getParent().remove(module);
        }

    }

    /**
     * Collects information about LARA files.
     */
    public LaraDocTop collectInformation() {
        LaraDocTop laraDocumentation = new LaraDocTop();

        for (Entry<String, List<File>> entry : packagesPaths.entrySet()) {
            String packageName = entry.getKey();

            // Create package
            LaraDocPackage laraPackage = new LaraDocPackage(packageName);

            // Add package to top node
            laraDocumentation.add(laraPackage);

            for (File path : entry.getValue()) {
                collectInformation(path, path, laraPackage);
            }

        }

        // collectInformation(inputPath, inputPath, laraDocFiles);

        return laraDocumentation;
    }

    private void collectInformation(File currentPath, File basePath, LaraDocNode currentNode) {

        // If name does not pass the filter, ignore
        if (nameFilter != null && !nameFilter.test(currentPath.getName())) {
            return;
        }

        // When it is a file
        if (currentPath.isFile()) {
            collectInformationFile(currentPath, basePath, currentNode);
            return;
        }

        // When it is a folder
        Preconditions.checkArgument(currentPath.isDirectory(), "Expected path to be a folder: '" + currentPath + "'");

        // Bundle folder
        if (LaraBundle.isBundleFolder(currentPath)) {
            collectInformationBundle(currentPath, currentNode);
            return;
        }

        // Resource folder
        if (LaraResource.getLaraResourceFile(currentPath).isFile()) {
            // Ignoring for now
            // TODO: Create temporary file for the LocalResource
            return;
        }

        // Normal folder

        // Check all LARA files
        SpecsIo.getFiles(currentPath).stream().forEach(file -> collectInformationFile(file, basePath, currentNode));

        // Call function recursively for all folders
        SpecsIo.getFolders(currentPath).stream().forEach(folder -> collectInformation(folder, basePath, currentNode));
    }

    private void collectInformationFile(File laraFile, File baseFolder, LaraDocNode currentNode) {

        // If not a LARA file, ignore
        // String filenameLowercase = filename.toLowerCase();
        String extension = SpecsIo.getExtension(laraFile).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            return;
        }

        // String laraExtension = ".lara";
        // if (!filenameLowercase.endsWith(laraExtension)) {
        // return;
        // }

        String filename = laraFile.getName();

        switch (extension) {
        case "lara":
            String importPath = LaraDocs.getImportPath(laraFile, baseFolder);

            /*
            LaraDocModule module = currentNode.getNode(LaraDocModule.class, importPath)
                    .orElse(new LaraDocModule(importPath));
            
            // Add module if not present
            currentNode.addIfNotPresent(module);
            */
            // Check if base file
            boolean isBaseFile = filename.substring(0, filename.length() - ".lara".length()).endsWith("Base");

            // Get correct module import
            String moduleImport = isBaseFile ? importPath.substring(0, importPath.length() - "Base".length())
                    : importPath;

            // Get or create module from current node
            LaraDocModule module = currentNode.getOrCreateNode(LaraDocModule.class, moduleImport,
                    () -> new LaraDocModule(moduleImport));

            if (isBaseFile) {
                module.setBaseLara(laraFile);
            } else {
                module.setMainLara(laraFile);
            }

            break;

        case "js":
            currentNode.getOrCreateNode(LaraDocJs.class, laraFile.getName(),
                    () -> new LaraDocJs(laraFile));

            // LaraDocJs jsNode = new LaraDocJs(laraFile);
            // currentNode.add(jsNode);
            break;

        default:
            throw new RuntimeException("Not implemented for files with extension '" + extension + "'");
        }

    }

    private void collectInformationBundle(File bundleFolder, LaraDocNode currentNode) {

        // Parse bundle information
        SpecsProperties laraBundle = LaraBundle.loadBundleFile(bundleFolder);

        String bundleName = LaraDocs.getBundleName(laraBundle);

        List<File> bundleRootFiles = SpecsIo.getFiles(bundleFolder);

        File bundleLaraFolder = new File(bundleFolder, LaraBundle.getLaraFolderName());

        // Files in the root of the folder and in folder 'lara' belong to all packages in the bundle

        LaraDocBundle laraDocBundle = currentNode.getOrCreateNode(LaraDocBundle.class, bundleName,
                () -> new LaraDocBundle(bundleName));

        // Each folder in the root represents a package of the bundle (or a bundle itself)
        List<File> packageFolders = SpecsIo.getFolders(bundleFolder);
        for (File packageFolder : packageFolders) {

            if (nameFilter != null && !nameFilter.test(packageFolder.getName())) {
                continue;
            }

            // If folder is a bundle, call function recursively
            // if (LaraBundle.isBundleFolder(packageFolder)) {
            // collectInformation(packageFolder, packageFolder, laraDocBundle);
            // continue;
            // }

            // Ignore lara folder
            if (packageFolder.getName().equals(LaraBundle.getLaraFolderName())) {
                continue;
            }

            String packageName = packageFolder.getName();
            LaraDocPackage currentPackage = laraDocBundle.getOrCreateNode(LaraDocPackage.class, packageName,
                    () -> new LaraDocPackage(packageName));

            // Add folder
            collectInformation(packageFolder, packageFolder, currentPackage);

            // Add common root files
            bundleRootFiles.stream().forEach(file -> collectInformationFile(file, bundleFolder, currentPackage));

            // Add lara folder
            if (bundleLaraFolder.isDirectory()) {
                collectInformation(bundleLaraFolder, bundleLaraFolder, currentPackage);
            }
        }
    }

    private void buildDocumentationTags(LaraDocTop laraDoc) {
        // Add documentation to modules
        List<LaraDocModule> modules = SpecsCollections.toList(laraDoc.getDescendantsStream(), LaraDocModule.class);

        for (LaraDocModule module : modules) {
            // Add info about a module to the same AspectIrDoc
            AspectIrDocBuilder laraDocBuilder = new AspectIrDocBuilder();

            // Parse files
            for (File laraFile : module.getLaraFiles()) {
                Optional<Aspects> aspectIr = LaraToJs.parseLara(laraFile, languageSpecification);
                if (!aspectIr.isPresent()) {
                    continue;
                }
                laraDocBuilder.parse(aspectIr.get());
            }

            // Add information about import path to class files in module
            laraDocBuilder.addImportPath(module.getImportPath());

            // Build AspectIrDoc and associate with module
            module.setDocumentation(laraDocBuilder.build());
        }
    }
}
