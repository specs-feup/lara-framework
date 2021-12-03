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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.lara.interpreter.Interpreter;
import org.lara.interpreter.generator.stmt.AspectClassProcessor;
import org.lara.interpreter.joptions.config.interpreter.LaraiKeys;
import org.lara.interpreter.joptions.keys.FileList;
import org.lara.interpreter.weaver.MasterWeaver;
import org.lara.interpreter.weaver.defaultweaver.DefaultWeaver;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.lara.language.specification.LanguageSpecification;
import org.suikasoft.jOptions.Interfaces.DataStore;

import larac.LaraC;
import larai.LaraI;
import larai.larabundle.BundleType;
import larai.larabundle.LaraBundle;
import larai.larabundle.LaraBundleProperty;
import larai.lararesource.LaraResource;
import pt.up.fe.specs.jsengine.JsEngine;
import pt.up.fe.specs.jsengine.graal.GraalvmJsEngine;
import pt.up.fe.specs.lara.aspectir.Aspects;
import pt.up.fe.specs.lara.doc.aspectir.AspectIrDocBuilder;
import pt.up.fe.specs.lara.doc.data.LaraDocBundle;
import pt.up.fe.specs.lara.doc.data.LaraDocFiles;
import pt.up.fe.specs.lara.doc.data.LaraDocModule;
import pt.up.fe.specs.util.Preconditions;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.collections.MultiMap;
import pt.up.fe.specs.util.lazy.Lazy;
import pt.up.fe.specs.util.properties.SpecsProperties;

/**
 * @deprecated Replaced by LaraDocParser
 * @author JBispo
 *
 */
@Deprecated
public class LaraDoc {

    private static final Set<String> FILES_TO_COPY = new HashSet<>(Arrays.asList("lara.resource", "lara.bundle"));

    private final MultiMap<String, File> packagesPaths;
    // private final File inputPath;
    // private final File outputFolder;
    private final Lazy<AspectClassProcessor> aspectProcessor;
    private final LanguageSpecification languageSpecification;

    private final boolean ignoreUnderscoredFolders = true;

    // public LaraDoc(WeaverEngine weaverEngine, File inputPath, File outputFolder) {
    // public LaraDoc(File inputPath) {
    public LaraDoc() {
        packagesPaths = new MultiMap<>();

        // Preconditions.checkArgument(inputPath.exists(), "Given input path '" + inputPath + "' does not exist");
        // this.inputPath = inputPath;
        // this.outputFolder = SpecsIo.mkdir(outputFolder);
        // this.aspectProcessor = Lazy.newInstance(() -> LaraDoc.newAspectProcessor(weaverEngine));
        this.aspectProcessor = Lazy.newInstance(LaraDoc::newAspectProcessor);
        this.languageSpecification = new DefaultWeaver().getLanguageSpecification();
    }

    public LaraDoc addPath(String packageName, File path) {
        if (!path.exists()) {
            SpecsLogs.msgInfo("Given input path '" + path + "' for package '" + packageName + "' does not exist");
            return this;
        }

        packagesPaths.put(packageName, path);

        return this;
    }

    // private static AspectClassProcessor newAspectProcessor(WeaverEngine weaverEngine) {
    private static AspectClassProcessor newAspectProcessor() {
        DataStore data = DataStore.newInstance("LaraDoc");
        data.add(LaraiKeys.LARA_FILE, new File(""));
        // data.add(LaraiKeys.VERBOSE, VerboseLevel.errors);
        WeaverEngine weaverEngine = new DefaultWeaver();
        LaraI larai = LaraI.newInstance(data, weaverEngine);
        // JsEngine jsEngine = new NashornEngine();
        JsEngine jsEngine = new GraalvmJsEngine();
        FileList folderApplication = FileList.newInstance();
        MasterWeaver masterWeaver = new MasterWeaver(larai, weaverEngine, folderApplication, jsEngine);
        larai.setWeaver(masterWeaver);

        Interpreter interpreter = new Interpreter(larai, jsEngine);
        larai.setInterpreter(interpreter);
        larai.getInterpreter().getImportProcessor().importAndInitialize();
        masterWeaver.begin();

        return AspectClassProcessor.newInstance(interpreter);
    }

    // private static boolean copyFileFilter(File file) {
    // String filename = file.getName().toLowerCase();
    //
    // if (filename.endsWith(".js")) {
    // return true;
    // }
    //
    // if (FILES_TO_COPY.contains(filename)) {
    // return true;
    // }
    //
    // return false;
    // }

    public LaraDocFiles buildLaraDoc() {
        // Collect information
        LaraDocFiles laraDocFiles = collectInformation();

        // Merge base files information
        laraDocFiles.mergeBaseFiles();

        // Build documentation information
        buildDocumentationTags(laraDocFiles);

        return laraDocFiles;
    }

    private void buildDocumentationTags(LaraDocFiles laraDocFiles) {
        // Add documentation to modules
        for (LaraDocModule module : laraDocFiles.getModules()) {
            // Add info about a module to the same AspectIrDoc
            AspectIrDocBuilder laraDocBuilder = new AspectIrDocBuilder();

            // Parse files
            for (File laraFile : module.getLaraFiles()) {
                Optional<Aspects> aspectIr = LaraToJs.parseLara(laraFile);
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

    /**
     * Collects information about LARA files.
     */
    public LaraDocFiles collectInformation() {

        LaraDocFiles laraDocFiles = new LaraDocFiles();

        for (Entry<String, List<File>> entry : packagesPaths.entrySet()) {
            String packageName = entry.getKey();
            laraDocFiles.pushPackageName(packageName);

            for (File path : entry.getValue()) {
                collectInformation(path, path, laraDocFiles);
            }

            laraDocFiles.popPackageName();

        }

        // collectInformation(inputPath, inputPath, laraDocFiles);

        return laraDocFiles;
    }

    private void collectInformation(File currentPath, File basePath, LaraDocFiles laraDocFiles) {

        // When it is a file
        if (currentPath.isFile()) {
            collectInformationFile(currentPath, basePath, laraDocFiles);
            return;
        }

        // When it is a folder
        Preconditions.checkArgument(currentPath.isDirectory(), "Expected path to be a folder: '" + currentPath + "'");

        if (ignoreUnderscoredFolders) {
            if (currentPath.getName().startsWith("_")) {
                return;
            }
        }

        // Bundle folder
        if (isBundleFolder(currentPath)) {
            collectInformationBundle(currentPath, laraDocFiles);
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
        SpecsIo.getFiles(currentPath).stream().forEach(file -> collectInformationFile(file, basePath, laraDocFiles));

        // Call function recursively for all folders
        SpecsIo.getFolders(currentPath).stream().forEach(folder -> collectInformation(folder, basePath, laraDocFiles));
    }

    private boolean isBundleFolder(File currentPath) {
        return new File(currentPath, LaraBundle.getLaraBundleFilename()).isFile();
    }

    private void collectInformationFile(File laraFile, File baseFolder, LaraDocFiles laraDocFiles) {

        // If not a LARA file, ignore
        String filename = laraFile.getName();
        String filenameLowercase = filename.toLowerCase();
        var filenameExtension = SpecsIo.getExtension(filenameLowercase);

        if (!LaraC.isSupportedExtension(filenameExtension)) {
            return;
        }

        // String laraExtension = ".lara";
        // if (!filenameLowercase.endsWith(laraExtension)) {
        // return;
        // }

        String importPath = getImportPath(laraFile, baseFolder);

        // Check if base file
        boolean isBaseFile = filename.substring(0, filename.length() - ".lara".length()).endsWith("Base");

        if (isBaseFile) {
            laraDocFiles.addBaseFile(importPath, laraFile);
        } else {
            laraDocFiles.addLaraModule(importPath, laraFile);
        }
    }

    private void collectInformationBundle(File bundleFolder, LaraDocFiles laraDocFiles) {
        // Parse bundle information
        SpecsProperties laraBundle = LaraBundle.loadBundleFile(bundleFolder);

        String bundleName = getBundleName(laraBundle);

        List<File> bundleRootFiles = SpecsIo.getFiles(bundleFolder);

        File bundleLaraFolder = new File(bundleFolder, LaraBundle.getLaraFolderName());

        // Files in the root of the folder and in folder 'lara' belong to all packages in the bundle
        // List<LaraFileInfo> commonFiles = getBundleCommonFiles(bundleFolder);

        LaraDocBundle laraDocBundle = laraDocFiles.getOrCreateBundle(bundleName);
        laraDocFiles.pushBundle(laraDocBundle);

        // Each folder in the root represents a package of the bundle (or a bundle itself)
        List<File> packageFolders = SpecsIo.getFolders(bundleFolder);
        for (File packageFolder : packageFolders) {

            // If folder is a bundle, call function recursively
            if (isBundleFolder(packageFolder)) {
                collectInformationBundle(packageFolder, laraDocFiles);
                continue;
            }

            // Ignore lara folder
            if (packageFolder.getName().equals(LaraBundle.getLaraFolderName())) {
                continue;
            }

            String packageName = packageFolder.getName();
            laraDocFiles.pushPackageName(packageName);

            // Add folder
            collectInformation(packageFolder, packageFolder, laraDocFiles);

            // Add common root files
            bundleRootFiles.stream().forEach(file -> collectInformationFile(file, bundleFolder, laraDocFiles));

            // Add lara folder
            if (bundleLaraFolder.isDirectory()) {
                collectInformation(bundleLaraFolder, bundleLaraFolder, laraDocFiles);
            }

            laraDocFiles.popPackageName();
        }

        laraDocFiles.popBundle();
    }

    /*
    private List<LaraFileInfo> getBundleCommonFiles(File bundleFolder) {
        List<LaraFileInfo> commonFiles = new ArrayList<>();
    
        // Add root folder files
        SpecsIo.getFiles(bundleFolder).stream()
                .map(file -> new LaraFileInfo(file, bundleFolder))
                .forEach(commonFiles::add);
    
        // Add lara folder files
        File laraFolder = new File(bundleFolder, LaraBundle.getLaraFolderName());
        if (laraFolder.isDirectory()) {
            SpecsIo.getFiles(laraFolder).stream()
                    .map(file -> new LaraFileInfo(file, laraFolder))
                    .forEach(commonFiles::add);
        }
    
        return commonFiles;
    }
    */
    private String getBundleName(SpecsProperties laraBundle) {
        BundleType bundleType = BundleType.getHelper().fromValue(laraBundle.get(LaraBundleProperty.BUNDLE_TYPE));

        switch (bundleType) {
        case WEAVER:
            return "-- Weaver --";
        case CUSTOM:
            return laraBundle.get(LaraBundleProperty.BUNDLE_TAG);
        default:
            throw new RuntimeException("Case not defined:" + bundleType);
        }
    }

    public static String getImportPath(File laraFile, File baseFolder) {
        String relativePath = SpecsIo.getRelativePath(laraFile, baseFolder);

        // Relative paths are always normalized
        // Preconditions.checkArgument(relativePath.endsWith(".lara"), "Expected file to end in '.lara': " + laraFile);
        Preconditions.checkArgument(LaraC.isSupportedExtension(SpecsIo.getExtension(relativePath)),
                "Expected file to end in '.lara': " + laraFile);
        return relativePath.replace('/', '.').substring(0, relativePath.length() - ".lara".length());
    }

}
