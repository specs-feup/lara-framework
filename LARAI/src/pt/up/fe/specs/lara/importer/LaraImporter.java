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
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import org.lara.interpreter.generator.stmt.AspectClassProcessor;
import org.lara.interpreter.joptions.config.interpreter.LaraiKeys;

import larac.LaraC;
import larac.options.LaraCOptions;
import larac.utils.output.Output;
import larai.LaraI;
import pt.up.fe.specs.jsengine.JsFileType;
import pt.up.fe.specs.util.SpecsCheck;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.SpecsStrings;
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

    public LaraImporter(LaraI larai, List<File> includes) {
        this(larai, includes, new ArrayList<>());
    }

    private LaraImporter(LaraI larai, List<File> includes, List<ResourceProvider> apis) {
        this.larai = larai;
        this.includes = includes;
        this.apis = apis;
        this.apisMap = Lazy.newInstance(() -> buildIncludeResourcesMap());
    }

    public List<File> getIncludes() {
        return includes;
    }

    /**
     * Loads a LARA import, using the same format as the imports in LARA files (e.g. weaver.Query).
     * 
     * @param importName
     */
    public List<LaraImportData> getLaraImports(String importName) {
        List<LaraImportData> laraImports = new ArrayList<>();

        var laraImportName = new LaraImportName(importName);

        var includePaths = getIncludePaths();

        ext: for (var ext : LaraC.getSupportedExtensions()) {

            // 1.
            // Check include paths
            for (var path : includePaths) {

                var importPath = laraImportName.getFullPath() + "." + ext;
                var importingFile = new File(path, importPath);

                if (importingFile.exists()) {
                    laraImports.add(buildLaraImport(importingFile));
                    SpecsLogs.debug(() -> "Adding file '" + importingFile.getAbsolutePath() + "' for import '"
                            + importName + "'");
                    continue ext;
                }
            }

            // 2.
            // Check resource by filename, instead of resource name
            var importPath = laraImportName.getFullPath() + "." + ext;

            var resources = apisMap.get().get(importPath);
            if (!resources.isEmpty()) {
                for (var resource : resources) {
                    SpecsLogs.debug(
                            () -> "Adding resource '" + resource.getResource() + "' for import '" + importName + "'");
                    laraImports.add(buildLaraImport(resource));
                }

                continue ext;
            }
        }

        return laraImports;

    }

    private ArrayList<File> getIncludePaths() {
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

        // Add include folders
        includePaths.addAll(includes);
        return includePaths;
    }

    private LaraImportData buildLaraImport(ResourceProvider resource) {
        var code = resource.read();
        SpecsCheck.checkNotNull(code, () -> "laraImport: could not read resource '" + resource.getResource() + "'");
        return buildLaraImport(code, resource.getFilename());
    }

    private LaraImportData buildLaraImport(File importingFile) {
        var code = SpecsIo.read(importingFile);
        SpecsCheck.checkNotNull(code, () -> "laraImport: could not read file '" + importingFile + "'");
        return buildLaraImport(code, importingFile.getName());
    }

    private LaraImportData buildLaraImport(String code, String filename) {
        var ext = SpecsIo.getExtension(filename);

        switch (ext) {
        case "js":
            var jsCode = processCode(code, filename);
            return new LaraImportData(filename, jsCode, JsFileType.NORMAL);
        case "mjs":
            var mjsCode = processCode(code, filename);
            return new LaraImportData(filename, mjsCode, JsFileType.MODULE);
        case "lara":
            // Compile LARA file
            var args = new ArrayList<>();
            args.add(LaraCOptions.getSkipArgs());
            var lara = new LaraC(args.toArray(new String[0]),
                    larai.getWeaverEngine().getLanguageSpecificationV2(), new Output(1));
            lara.setLaraPath(filename);
            lara.setLaraStreamProvider(() -> SpecsIo.toInputStream(code));

            var aspectIr = lara.compile();

            var processor = AspectClassProcessor.newInstance(larai.getInterpreter());
            try {
                var aspectJsCode = processor.toSimpleJs(aspectIr);
                return new LaraImportData(filename, aspectJsCode, JsFileType.NORMAL);
            } catch (Exception e) {
                throw new RuntimeException("Error during LARA compilation", e);
            }

        default:
            throw new CaseNotDefinedException(ext);
        }

    }

    /**
     * Processes JS code that is going to be loaded.
     * 
     * Currently adds code to guarantee that the declaring variable of the laraImport is in the global scope.
     * 
     * @param code
     * @param filename
     * @return
     */
    private String processCode(String code, String filename) {
        var template = "if(typeof <VARNAME> === 'undefined') {\r\n"
                + "    println(\"Warning: using laraImport() for file '<FILE>', however it does not define a variable or class '<VARNAME>'\");\r\n"
                + "} else {\r\n"
                + "    globalThis.<VARNAME> = <VARNAME>;\r\n"
                + "}";

        // Get varname
        var varName = SpecsStrings.escapeJson(SpecsIo.removeExtension(new File(filename).getName()));
        var escapedFilename = SpecsStrings.escapeJson(filename);
        var globalizeCode = template.replace("<VARNAME>", varName).replace("<FILE>", escapedFilename);

        return code + "\n\n" + globalizeCode;
    }

    private MultiMap<String, ResourceProvider> buildIncludeResourcesMap() {
        var resourcesMap = new MultiMap<String, ResourceProvider>();

        for (var resource : apis) {
            resourcesMap.put(resource.getFileLocation(), resource);
        }

        return resourcesMap;
    }

    public Collection<String> getImportsFromPackage(String packageName) {
        SpecsLogs.debug(() -> "Searching for imports in package '" + packageName + "'");

        var packageNameAsPath = packageName.replace('.', '/');
        var packageNameAsPathWithSlash = packageNameAsPath + "/";

        var imports = new LinkedHashSet<String>();

        var includePaths = getIncludePaths();

        for (var ext : LaraC.getSupportedExtensions()) {

            // 1.
            // Check include paths
            for (var path : includePaths) {

                // Get all files in path
                var candidateFiles = SpecsIo.getFilesRecursive(path, ext);

                for (var candidateFile : candidateFiles) {

                    // Equivalent package name
                    var relativePath = SpecsIo.getRelativePath(candidateFile.getParentFile(), path);

                    // Ignore files outside of package name
                    if (!relativePath.equals(packageNameAsPath)) {
                        continue;
                    }

                    // Found valid import
                    var importName = SpecsIo.removeExtension(candidateFile);

                    var importPath = relativePath + "/" + importName;
                    imports.add(importPath.replace("/", "."));
                }

            }

            // 2.
            // Built-in resources

            for (var resource : apis) {

                // Consider resource only if location is the same as the package name
                if (!resource.getResourceLocation().equals(packageNameAsPathWithSlash)) {
                    continue;
                }

                // Found valid import
                var importPath = SpecsIo.removeExtension(resource.getFileLocation()).replace('/', '.');
                imports.add(importPath);
            }

        }

        SpecsLogs.debug(() -> "Found imports: " + imports + "");

        return imports;
    }
}
