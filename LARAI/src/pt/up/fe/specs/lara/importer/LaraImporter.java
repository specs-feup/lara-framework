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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.lara.interpreter.generator.stmt.AspectClassProcessor;
import org.lara.interpreter.joptions.config.interpreter.LaraiKeys;
import org.lara.interpreter.weaver.utils.LaraResourceProvider;

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
    private final Set<String> npmImports;
    private final Set<File> npmApiFolders;

    public LaraImporter(LaraI larai, List<File> includes) {
        this(larai, includes, new ArrayList<>());
    }

    private LaraImporter(LaraI larai, List<File> includes, List<ResourceProvider> apis) {
        this.larai = larai;
        this.includes = includes;
        this.apis = apis;
        this.apisMap = Lazy.newInstance(() -> buildIncludeResourcesMap());
        this.npmImports = buildNpmImports();
        this.npmApiFolders = new HashSet<>(larai.getWeaverEngine().getApiManager().getNpmApiFolders());
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
                    boolean isNpmImport = false;
                    // Check if inside APIs folder, and if it is an automatically generated resource
                    // System.out.println(npmImports);
                    // System.out.println("Is " + importPath + " a NPM import? " + npmImports.contains(importPath));

                    // if (path.equals(larai.getWeaverEngine().getApisFolder()) && npmImports.contains(importPath)) {
                    if (npmApiFolders.contains(path) && npmImports.contains(importPath)) {
                        SpecsLogs.debug("Detected import '" + importName + "' as NPM import");
                        // System.out.println("TESTE: " + importPath);
                        isNpmImport = true;
                    }

                    laraImports.add(buildLaraImport(importingFile, isNpmImport));
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

    /**
     * 
     * @return
     */
    private Set<String> buildNpmImports() {
        return larai.getWeaverEngine().getNpmResources().stream()
                .map(LaraResourceProvider::getResource)
                .collect(Collectors.toSet());
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

    // private LaraImportData buildLaraImport(File importingFile) {
    // return buildLaraImport(importingFile, null);
    // }

    private LaraImportData buildLaraImport(File importingFile, boolean isNpmImport) {
        // NPM imports are always accessed from file
        var code = SpecsIo.read(importingFile);
        var ext = isNpmImport ? "mjs" : null;
        // SpecsCheck.checkNotNull(code, () -> "laraImport: could not read file '" + importingFile + "'");
        return buildLaraImport(code, importingFile.getName(), ext, importingFile, isNpmImport);

        // Creating from file, no need to read the code
        // return buildLaraImport(null, importingFile.getName(), forcedExtension, importingFile);

    }

    private LaraImportData buildLaraImport(String code, String filename) {
        return buildLaraImport(code, filename, null, null, false);
    }

    private LaraImportData buildLaraImport(String code, String filename, String ext, File jsFile, boolean isNpmImport) {
        if (isNpmImport) {
            SpecsCheck.checkNotNull(jsFile, () -> "jsFile is null");
            return new LaraImportData(filename, processCode(code, jsFile), JsFileType.MODULE, jsFile);
        }

        if (ext == null) {
            ext = SpecsIo.getExtension(filename);
        }

        switch (ext) {
        case "js":
            var jsLaraImport = new LaraImportData(filename, processCodeOld(code, filename), JsFileType.NORMAL, jsFile);
            // var jsLaraImport = new LaraImportData(filename, processCode(code, jsFile), JsFileType.NORMAL, jsFile);
            return jsLaraImport;
        case "mjs":
            var mjsLaraImport = new LaraImportData(filename, processCodeOld(code, filename), JsFileType.MODULE, jsFile);
            // var mjsLaraImport = new LaraImportData(filename, processCode(code, jsFile), JsFileType.MODULE, jsFile);
            return mjsLaraImport;
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
                // return new LaraImportData(filename, aspectJsCode, JsFileType.NORMAL, jsFile);
                // LARA files need to be transformed, will never use the file to load,
                // but directly the processed source code
                return new LaraImportData(filename, aspectJsCode, JsFileType.NORMAL, null);
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
    private String processCodeOld(String code, String filename) {
        var template = "if(typeof <VARNAME> === 'undefined') {\n"
                + "    println(\"Warning: using laraImport() for file '<FILE>', however it does not define a variable or class '<VARNAME>'\");\n"
                + "} else {\n"
                + "    globalThis.<VARNAME> = <VARNAME>;\n"
                + "}";

        // Get varname
        var varName = SpecsStrings.escapeJson(SpecsIo.removeExtension(new File(filename).getName()));
        var escapedFilename = SpecsStrings.escapeJson(filename);
        var globalizeCode = template.replace("<VARNAME>", varName).replace("<FILE>", escapedFilename);

        var processedCode = code + "\n\n" + globalizeCode;

        return processedCode;
    }

    /**
     * Generates code that needs to be evaluated after import is loaded.
     * 
     * Currently adds code to guarantee that the declaring variable of the laraImport is in the global scope.
     * 
     * @param filename
     * @return
     */
    private String processCode(String filename, File jsFile) {

        var template2 = "import * as Foo from \"<IMPORT_NAME>\"\n"
                + "\n"
                + "const foo = Object.entries(Foo);\n"
                + "foo.forEach(([key, value]) => {\n"
                + "\n"
                + "    if (key === \"default\") {\n"
                + "        // Get the name of the class from the file path.\n"
                + "        key = \"<MODULE_NAME>\";\n"
                + "    }\n"
                + "\n"
                + "    // @ts-ignore\n"
                + "    globalThis[key] = value;\n"
                + "});\n"
                + "";

        // Find node modules folder
        var filepath = jsFile.getAbsolutePath();
        var index = filepath.indexOf("node_modules");

        // +1 for the slash
        var importName = filepath.substring(index + "node_modules".length() + 1).replace('\\', '/');

        return template2.replace("<IMPORT_NAME>", importName).replace("<MODULE_NAME>", SpecsIo.removeExtension(jsFile));
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
