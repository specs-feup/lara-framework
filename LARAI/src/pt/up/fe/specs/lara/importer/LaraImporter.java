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

import org.lara.interpreter.generator.stmt.AspectClassProcessor;
import org.lara.interpreter.joptions.config.interpreter.LaraiKeys;

import larac.LaraC;
import larac.options.LaraCOptions;
import larac.utils.output.Output;
import larai.LaraI;
import pt.up.fe.specs.jsengine.JsFileType;
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

    private final LaraI larai;
    private final List<File> includes;
    private final List<ResourceProvider> apis;
    private final Lazy<MultiMap<String, ResourceProvider>> apisMap;

    public LaraImporter(LaraI larai, List<File> includes, List<ResourceProvider> apis) {
        this.larai = larai;
        this.includes = includes;
        this.apis = apis;
        this.apisMap = Lazy.newInstance(() -> buildIncludeResourcesMap());
    }

    /**
     * Loads a LARA import, using the same format as the imports in LARA files (e.g. weaver.Query).
     * 
     * @param importName
     */
    public List<LaraImportData> getLaraImports(String importName) {

        List<LaraImportData> laraImports = new ArrayList<>();

        var laraImportName = new LaraImportName(importName);

        // Prepare include paths
        var includePaths = new ArrayList<File>();

        // Add workspace folder to include paths
        if (larai.getWeaverArgs().hasValue(LaraiKeys.WORKSPACE_FOLDER)) {
            var workspace = larai.getWeaverArgs().get(LaraiKeys.WORKSPACE_FOLDER).getFiles();
            for (var workspacePath : workspace) {
                if (!workspacePath.isDirectory()) {
                    continue;
                }

                includePaths.add(workspacePath);
            }
        }
        // System.out.println("WORKSPACE: " + larai.getWeaverArgs().get(LaraiKeys.WORKSPACE_FOLDER));
        // for (var file : larai.getWeaverArgs().get(LaraiKeys.WORKSPACE_FOLDER).getFiles()) {
        //
        // System.out.println("IMPORTER COMPLETE PATH: " + file.getAbsolutePath());
        // System.out.println("IMPORTER FILES IN PATH: " + SpecsIo.getFilesRecursive(file));
        //
        // }

        // includePaths.add(SpecsIo.getWorkingDir().getAbsoluteFile());

        // Add include folders
        includePaths.addAll(includes);

        // 1.
        // Check include paths
        for (var path : includePaths) {
            // System.out.println("PATH: " + path);
            for (var ext : LaraC.getSupportedExtensions()) {

                var importPath = laraImportName.getFullPath() + "." + ext;
                var importingFile = new File(path, importPath);

                if (importingFile.exists()) {
                    laraImports.add(buildLaraImport(importingFile));
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

                resources.forEach(resource -> laraImports.add(buildLaraImport(resource)));
                // System.out.println("IMPORT PATH: " + importPath);
                // System.out.println("RESOURCE: " + resource.get(0).);
                // laraImports.add(new ResourceLaraImport(importPath, resource.get(0)));
                // System.out.println("RESOURCE: " + resource.get(0));
            }
        }

        return laraImports;

    }

    private LaraImportData buildLaraImport(ResourceProvider resource) {
        // return buildLaraImport(resource.read(), resource.getResource());
        return buildLaraImport(resource.read(), resource.getFilename());
    }

    private LaraImportData buildLaraImport(File importingFile) {
        return buildLaraImport(SpecsIo.read(importingFile), importingFile.getName());
    }

    private LaraImportData buildLaraImport(String code, String filename) {
        var ext = SpecsIo.getExtension(filename);

        switch (ext) {
        case "js":
            return new LaraImportData(filename, code, JsFileType.NORMAL);
        case "mjs":
            return new LaraImportData(filename, code, JsFileType.MODULE);
        case "lara":
            // Compile LARA file
            var args = new ArrayList<>();
            args.add(LaraCOptions.getSkipArgs());
            var lara = new LaraC(args.toArray(new String[0]),
                    larai.getWeaverEngine().getLanguageSpecificationV2(), new Output(1));
            lara.setLaraPath(filename);
            lara.setLaraStreamProvider(() -> SpecsIo.toInputStream(code));

            var aspectIr = lara.compile();

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

            var processor = AspectClassProcessor.newInstance(larai.getInterpreter());
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
