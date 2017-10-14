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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.script.ScriptEngineManager;

import org.lara.interpreter.Interpreter;
import org.lara.interpreter.aspectir.Aspects;
import org.lara.interpreter.generator.stmt.AspectClassProcessor;
import org.lara.interpreter.joptions.config.interpreter.LaraiKeys;
import org.lara.interpreter.joptions.keys.FileList;
import org.lara.interpreter.weaver.MasterWeaver;
import org.lara.interpreter.weaver.defaultweaver.DefaultWeaver;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.lara.language.specification.LanguageSpecification;
import org.suikasoft.jOptions.Interfaces.DataStore;
import org.w3c.dom.Document;

import jdk.nashorn.api.scripting.NashornScriptEngine;
import larac.LaraC;
import larac.utils.output.Output;
import larai.LaraI;
import larai.larabundle.BundleType;
import larai.larabundle.LaraBundle;
import larai.larabundle.LaraBundleProperty;
import larai.lararesource.LaraResource;
import pt.up.fe.specs.lara.doc.data.LaraDocBundle;
import pt.up.fe.specs.lara.doc.data.LaraDocFiles;
import pt.up.fe.specs.util.Preconditions;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.lazy.Lazy;
import pt.up.fe.specs.util.properties.SpecsProperties;

public class LaraDoc {

    private static final Set<String> FILES_TO_COPY = new HashSet<>(Arrays.asList("lara.resource", "lara.bundle"));

    private final File inputPath;
    private final File outputFolder;
    private final Lazy<AspectClassProcessor> aspectProcessor;
    private final LanguageSpecification languageSpecification;

    private final boolean ignoreUnderscoredFolders = true;

    // public LaraDoc(WeaverEngine weaverEngine, File inputPath, File outputFolder) {
    public LaraDoc(File inputPath, File outputFolder) {
        Preconditions.checkArgument(inputPath.exists(), "Given input path '" + inputPath + "' does not exist");
        this.inputPath = inputPath;
        this.outputFolder = SpecsIo.mkdir(outputFolder);
        // this.aspectProcessor = Lazy.newInstance(() -> LaraDoc.newAspectProcessor(weaverEngine));
        this.aspectProcessor = Lazy.newInstance(LaraDoc::newAspectProcessor);
        this.languageSpecification = new DefaultWeaver().getLanguageSpecification();
    }

    // private static AspectClassProcessor newAspectProcessor(WeaverEngine weaverEngine) {
    private static AspectClassProcessor newAspectProcessor() {
        DataStore data = DataStore.newInstance("LaraDoc");
        data.add(LaraiKeys.LARA_FILE, new File(""));
        // data.add(LaraiKeys.VERBOSE, VerboseLevel.errors);
        WeaverEngine weaverEngine = new DefaultWeaver();
        LaraI larai = LaraI.newInstance(data, weaverEngine);
        NashornScriptEngine jsEngine = (NashornScriptEngine) new ScriptEngineManager().getEngineByName("nashorn");
        FileList folderApplication = FileList.newInstance();
        MasterWeaver masterWeaver = new MasterWeaver(larai, weaverEngine, folderApplication, jsEngine);
        larai.setWeaver(masterWeaver);

        Interpreter interpreter = new Interpreter(larai, jsEngine);
        larai.setInterpreter(interpreter);
        larai.getInterpreter().getImportProcessor().importAndInitialize();
        masterWeaver.begin();

        return AspectClassProcessor.newInstance(interpreter);
    }

    public void convertFilesV1() {

        List<File> allFiles = SpecsIo.getFilesRecursive(inputPath);

        // TODO: Filter contents of folders that have file lara.resource

        List<File> resourceFiles = allFiles.stream()
                .filter(file -> file.getName().equals("lara.resource"))
                .collect(Collectors.toList());

        // Set<String> resourceFolders = resourceFiles.stream()
        // .map(file -> file.getParentFile().getAbsolutePath())
        // .collect(Collectors.toSet());
        // for(File resourceFile : resourceFiles)

        List<File> filteredFiles = new ArrayList<>(allFiles);
        for (File resourceFile : resourceFiles) {
            String resourceFolder = resourceFile.getParentFile().getAbsolutePath();
            filteredFiles = filteredFiles.stream()
                    .filter(file -> !file.getAbsolutePath().startsWith(resourceFolder))
                    .collect(Collectors.toList());
        }

        // Parse LARA files
        filteredFiles.stream()
                .filter(file -> file.getName().endsWith(".lara"))
                .forEach(this::convertLara);

        System.out.println("ALL LARA FILES:" + allFiles.stream()
                .filter(file -> file.getName().endsWith(".lara"))
                .count());

        System.out.println("FILTERED LARA FILES:" + filteredFiles.stream()
                .filter(file -> file.getName().endsWith(".lara"))
                .count());
        // List<File> laraFiles = SpecsIo.getFilesRecursive(inputPath, "lara");
        // for (File laraFile : laraFiles) {
        // convertLara(laraFile);
        // }

        // Copy JS files, bundle files and resource files
        filteredFiles.stream()
                .filter(LaraDoc::copyFileFilter)
                .forEach(this::copyFile);
    }

    private void convertLara(File laraFile) {
        // Pass through LaraC
        System.out.println("COMPILING FILE " + laraFile);
        List<String> args = new ArrayList<>();

        args.add(laraFile.getAbsolutePath());
        args.add("--doc");
        args.add("--verbose");
        args.add("0");
        // args.add("-d");
        // preprocess.add("-o");
        // preprocess.add(path);
        // if (!encodedIncludes.trim().isEmpty()) {
        // preprocess.add("-i");
        // preprocess.add(encodedIncludes);
        // }

        // lara files as resources
        // List<ResourceProvider> laraAPIs = new ArrayList<>(ResourceProvider.getResources(LaraApiResource.class));
        // System.out.println("LARA APIS :" + IoUtils.getResource(laraAPIs2.get(0)));
        // laraAPIs.addAll(options.getLaraAPIs());
        /*
        List<ResourceProvider> laraAPIs = options.getLaraAPIs();
        if (!laraAPIs.isEmpty()) {
            preprocess.add("-r");
            String resources = laraAPIs.stream().map(LaraI::getOriginalResource)
                    .collect(Collectors.joining(File.pathSeparator));
            preprocess.add(resources);
        }
        */
        /*
        if (options.isDebug()) {
            preprocess.add("-d");
        }
        */
        LaraC larac = new LaraC(args.toArray(new String[0]), languageSpecification, new Output());
        Document aspectIr = null;

        try {
            aspectIr = larac.compile();
        } catch (Exception e) {
            SpecsLogs.msgInfo("Could not compile file '" + laraFile + "'");
            return;
        }

        // String aspectXml = toXml(aspectIr);

        // LaraI.main(args);
        Aspects asps = null;
        try {
            asps = new Aspects(aspectIr, "");
            // System.out.println("--- IR BEFORE ---");
            // lara.printAspectIR();
            // System.out.println("--- IR AFTER ---");

        } catch (Exception e) {
            SpecsLogs.msgInfo("Could not create aspects: " + e.getMessage());
            return;
            // throw new RuntimeException("Could not create aspects", e);
        }

        // Pass through LaraI
        AspectClassProcessor aspectClassProcessor = aspectProcessor.get();
        StringBuilder jsCode = aspectClassProcessor.generateJavaScriptDoc(asps);

        // Save js to the same relative location as the original file
        String relativePath = SpecsIo.getRelativePath(laraFile, inputPath);
        File jsFile = new File(outputFolder, SpecsIo.removeExtension(relativePath) + ".js");

        SpecsIo.write(jsFile, jsCode.toString());
    }

    private static boolean copyFileFilter(File file) {
        String filename = file.getName().toLowerCase();

        if (filename.endsWith(".js")) {
            return true;
        }

        if (FILES_TO_COPY.contains(filename)) {
            return true;
        }

        return false;
    }

    private void copyFile(File file) {
        // Save to the same relative location as the original file
        String relativePath = SpecsIo.getRelativePath(file, inputPath);
        File newFile = new File(outputFolder, relativePath);

        SpecsIo.copy(file, newFile);
    }

    public LaraDocFiles buildLaraDoc() {
        // Collect information
        LaraDocFiles laraDocFiles = collectInformation();

        // Merge base files information
        laraDocFiles.mergeBaseFiles();

        return laraDocFiles;
    }

    /**
     * Collects information about LARA files.
     */
    public LaraDocFiles collectInformation() {
        LaraDocFiles laraDocFiles = new LaraDocFiles();

        collectInformation(inputPath, inputPath, laraDocFiles);

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
        if (new File(currentPath, LaraBundle.getLaraBundleFilename()).isFile()) {
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

    private void collectInformationFile(File laraFile, File baseFolder, LaraDocFiles laraDocFiles) {
        // If not a LARA file, ignore
        String filename = laraFile.getName();
        String filenameLowercase = filename.toLowerCase();
        String laraExtension = ".lara";
        if (!filenameLowercase.endsWith(laraExtension)) {
            return;
        }

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

        // Each folder in the root represents a package of the bundle
        List<File> packageFolders = SpecsIo.getFolders(bundleFolder);
        for (File packageFolder : packageFolders) {

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
        BundleType bundleType = BundleType.getHelper().valueOf(laraBundle.get(LaraBundleProperty.BUNDLE_TYPE));

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
        Preconditions.checkArgument(relativePath.endsWith(".lara"), "Expected file to end in '.lara': " + laraFile);

        return relativePath.replace('/', '.').substring(0, relativePath.length() - ".lara".length());
    }

}
