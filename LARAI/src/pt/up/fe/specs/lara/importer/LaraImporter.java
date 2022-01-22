/**
 * Copyright 2022 SPeCS.
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

package pt.up.fe.specs.lara.importer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.lara.interpreter.Interpreter;
import org.lara.interpreter.generator.stmt.AspectClassProcessor;
import org.lara.interpreter.weaver.interf.WeaverEngine;

import larac.LaraC;
import larac.options.LaraCOptions;
import larac.utils.output.Output;
import pt.up.fe.specs.jsengine.JsFileType;
import pt.up.fe.specs.lara.LaraCompiler;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.collections.MultiMap;
import pt.up.fe.specs.util.exceptions.CaseNotDefinedException;
import pt.up.fe.specs.util.lazy.Lazy;
import pt.up.fe.specs.util.providers.ResourceProvider;

/**
 * Resolves Lara imports.
 * 
 * @author Joao Bispo
 *
 */
public class LaraImporter {

    private final Interpreter interpreter;
    private final WeaverEngine weaver;
    private final List<File> includes;
    private final List<ResourceProvider> apis;
    private final Lazy<MultiMap<String, ResourceProvider>> apisMap;
    private final LaraCompiler laraCompiler;
    private Exception laraCompilationException;

    public LaraImporter(Interpreter interpreter, WeaverEngine weaver, List<File> includes,
            List<ResourceProvider> apis) {
        this.interpreter = interpreter;
        this.weaver = weaver;
        this.includes = includes;
        this.apis = apis;
        this.apisMap = Lazy.newInstance(() -> buildIncludeResourcesMap());
        this.laraCompiler = new LaraCompiler(weaver.getLanguageSpecificationV2());
        this.laraCompilationException = null;
    }

    /**
     * Loads a LARA import, using the same format as the imports in LARA files (e.g. weaver.Query).
     * 
     * @param importName
     */
    public List<LaraImportData> getLaraImports(String importName) {

        List<LaraImportData> laraImports = new ArrayList<>();

        // Split into fileName and filePath
        // int dotIndex = importName.lastIndexOf('.');
        // var fileName = dotIndex == -1 ? importName : importName.substring(dotIndex + 1);
        // var filePath = dotIndex == -1 ? "" : importName.substring(0, dotIndex + 1);
        // filePath = filePath.replace('.', '/');
        //
        // String relativePath = filePath + fileName;

        var laraImportName = new LaraImportName(importName);

        // 1.
        // Check include folders
        for (var path : includes) {
            for (var ext : LaraC.getSupportedExtensions()) {

                var importPath = laraImportName.getFullPath() + "." + ext;
                var importingFile = new File(path, importPath);

                if (importingFile.exists()) {
                    laraImports.add(buildLaraImport(importName, importingFile));
                }
            }
        }

        // 2.
        // Check resource by filename, instead of resource name
        for (var ext : LaraC.getSupportedExtensions()) {
            var importPath = laraImportName.getFullPath() + "." + ext;
            // System.out.println("IMPORT PATH:" + importPath);
            var resources = apisMap.get().get(importPath);
            if (!resources.isEmpty()) {

                resources.forEach(resource -> laraImports.add(buildLaraImport(importName, resource)));
                // System.out.println("IMPORT PATH: " + importPath);
                // System.out.println("RESOURCE: " + resource.get(0).);
                // laraImports.add(new ResourceLaraImport(importPath, resource.get(0)));
                // System.out.println("RESOURCE: " + resource.get(0));
            }
        }

        return laraImports;

        /*
        // 1.
        // Check include folders
        for (final File path : getIncludeFolders()) {
            for (var ext : LaraC.getSupportedExtensions()) {
                var importPath = relativePath + "." + ext;
        
                final File importingFile = new File(path, importPath);
                if (importingFile.exists()) {
                    laraImports.add(new FileLaraImport(importPath, importingFile));
                    // System.out.println("FILE: " + importingFile);
                }
            }
        }
        
        // 2.
        // Check resource by filename, instead of resource name
        for (var ext : LaraC.getSupportedExtensions()) {
            var importPath = relativePath + "." + ext;
        
            var resource = getIncludeResourcesMap().get(importPath);
            if (!resource.isEmpty()) {
                laraImports.add(new ResourceLaraImport(importPath, resource.get(0)));
                // System.out.println("RESOURCE: " + resource.get(0));
            }
        }
        */

    }

    private LaraImportData buildLaraImport(String importName, ResourceProvider resource) {
        // return buildLaraImport(resource.read(), resource.getResource());
        return buildLaraImport(importName, resource.read(), resource.getFilename());
    }

    private LaraImportData buildLaraImport(String importName, File importingFile) {
        return buildLaraImport(importName, SpecsIo.read(importingFile), importingFile.getName());
    }

    private void runLaraCompiler(String code, String filename) {
        // Reset before running
        laraCompilationException = null;

        try {
            laraCompiler.compile(filename, code);
        } catch (Exception e) {
            laraCompilationException = e;
        }
    }

    private LaraImportData buildLaraImport(String importName, String code, String filename) {
        var ext = SpecsIo.getExtension(filename);

        switch (ext) {
        case "js":
            return new LaraImportData(filename, code, JsFileType.NORMAL);
        case "mjs":
            return new LaraImportData(filename, code, JsFileType.MODULE);
        case "lara":
            /*
            // If LARA, let LaraC take care of importing
            
            var larac = interpreter.getLaraI().getLaraC();
            Document aspectIr = larac.importLara(importName);
            */
            /*
            // System.out.println("LARAC: " + interpreter.getLaraI().getLaraC());
            var larac = interpreter.getLaraI().getLaraC();
            
            
            
            // var lara = new LaraC(args.toArray(new String[0]),
            // interpreter.getLaraI().getWeaverEngine().getLanguageSpecificationV2(), new Output(1));
            larac.addImportedLARA(importName, null);
            var importingLara = LaraC.newImporter(filename, code, larac.getOptions(), larac.languageSpec(),
                    larac.getPrint(), larac.getImportedLARA());
            LaraImports.rearrangeImportedLaraAndImportAspects(larac, filename, importingLara);
            larac.setImportedLARA(importingLara.getImportedLARA());
            larac.addImportedLARA(importName, importingLara);
            
            var aspectIr = importingLara.getAspectIR().toXML();
            */
            // Compile LARA file
            var args = new ArrayList<>();
            args.add(LaraCOptions.getSkipArgs());
            var lara = new LaraC(args.toArray(new String[0]),
                    interpreter.getLaraI().getWeaverEngine().getLanguageSpecificationV2(), new Output(1));
            // lara.setLaraFile(new File(filename));
            lara.setLaraPath(filename);
            lara.setLaraStreamProvider(() -> SpecsIo.toInputStream(code));

            // String prefix = filename.replace(".lara", MessageConstants.NAME_SEPARATOR);
            // prefix = prefix.replace("/", MessageConstants.NAME_SEPARATOR);
            // lara.setPrefix(prefix);

            // lara.addImportedLARA(filename, null);
            // System.out.println("Filename: " + filename);
            // System.out.println("Prefix: " + prefix);
            // if (filename.equals("clava/Clava.lara")) {
            // lara.getOptions().setDebug(true);
            // lara.getOptions().setShowAspectIR(true);
            // }

            // Enable parsing directly to JS (e.g. transforms imports into scriptImports)
            // lara.setToJsMode(true, filename, code);

            var aspectIr = lara.compile();
            // LaraImports.rearrangeImportedLaraAndImportAspects(larac, filename, importingLara);

            // if (true) {
            // // if (filename.equals("Clava.lara")) {
            // try {
            // System.out.println("PRINTING ASPECT IR");
            // System.out.println(StringUtils.xmlToStringBuffer(aspectIr, MessageConstants.INDENT).toString());
            // } catch (Exception e) {
            // throw new RuntimeException("Could not print AspectIR", e);
            // }
            // }

            // System.out.println("FILENAME: " + filename);

            var processor = AspectClassProcessor.newInstance(interpreter);
            try {
                var jsCode = processor.toSimpleJs(aspectIr);

                // if (true) {
                // // if (filename.equals("clava/clava/Clava.lara")) {
                // System.out.println("LARA FILE: " + filename);
                // System.out.println("Lara to Js Begin:\n" + jsCode);
                // System.out.println("Lara to Js End");
                // }

                // System.out.println("COmpiled code:\n" + laraCompiler.getLastCompilation());
                return new LaraImportData(filename, jsCode, JsFileType.NORMAL);
            } catch (Exception e) {
                throw new RuntimeException("Error during LARA compilation", e);
            }
            /*
            var executor = Executors.newSingleThreadExecutor();
            // System.out.println("CODE: " + code);
            executor.execute(() -> runLaraCompiler(code, filename));
            executor.shutdown();
            
            var timeout = 1000l;
            var timeunit = TimeUnit.SECONDS;
            try {
                executor.awaitTermination(timeout, timeunit);
            } catch (InterruptedException e) {
                // Thread.currentThread().interrupt();
                throw new RuntimeException("Could not compile the LARA file under the alot time", e);
            }
            
            // Check if there is any exception
            if (laraCompilationException != null) {
                throw new RuntimeException("Error during LARA compilation", laraCompilationException);
            }
            
            System.out.println("Lara to Js Begin:\n" + laraCompiler.getLastCompilation());
            System.out.println("Lara to Js End");
            // System.out.println("COmpiled code:\n" + laraCompiler.getLastCompilation());
            return new LaraImportData(filename, laraCompiler.getLastCompilation(), JsFileType.NORMAL);
            */
        default:
            throw new CaseNotDefinedException(ext);
        }

    }

    private MultiMap<String, ResourceProvider> buildIncludeResourcesMap() {
        var resourcesMap = new MultiMap<String, ResourceProvider>();

        for (var resource : apis) {
            resourcesMap.put(resource.getFileLocation(), resource);
        }
        // System.out.println("RESOURCE MAP: " + resourcesMap);
        return resourcesMap;
    }
}
