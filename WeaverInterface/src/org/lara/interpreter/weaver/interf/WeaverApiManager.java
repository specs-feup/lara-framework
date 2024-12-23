/**
 * Copyright 2023 SPeCS.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

package org.lara.interpreter.weaver.interf;

import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.SpecsStrings;
import pt.up.fe.specs.util.SpecsSystem;
import pt.up.fe.specs.util.providers.ResourceProvider;
import pt.up.fe.specs.util.utilities.StringLines;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages a set of resources as files on disk, considering they come from, or will be used by NPM packages.
 */
public class WeaverApiManager {

    private final static String CHECKSUM_FILENAME = "checksum.txt";
    private final static String NODE_MODULES_FOLDERNAME = "node_modules";
    private final static String CORE_FOLDERNAME = "core";

    private final File coreFolder;
    private final File npmFolder;

    private WeaverApiManager(File coreFolder, File npmFolder) {
        this.coreFolder = coreFolder;
        this.npmFolder = npmFolder;
    }

    private File getNpmFolder() {
        return npmFolder;
    }

    public File getNodeModulesFolder() {
        return new File(getNpmFolder(), NODE_MODULES_FOLDERNAME);
    }

    public File getCoreFolder() {
        return coreFolder;
    }

    /**
     * @return a list of folders inside the node_modules folder that correspond to the base folders that laraImport
     * should look for APIs
     */
    public List<File> getNpmApiFolders() {
        var moduleFolders = SpecsIo.getFolders(getNodeModulesFolder());

        var packageFolders = new ArrayList<File>();
        for (var folder : moduleFolders) {
            if (folder.getName().startsWith("@")) {
                // It's a scope for packages.
                // Add subfolders to packageFolders.
                packageFolders.addAll(SpecsIo.getFolders(folder));
            } else {
                packageFolders.add(folder);
            }
        }

        var apiFolders = new ArrayList<File>();
        for (var packageFolder : packageFolders) {
            var apiFolder = new File(packageFolder, "api");

            if (!apiFolder.isDirectory()) {
                SpecsLogs.info("Weaver API manager: expected to find folder 'api' inside '"
                        + packageFolder.getAbsolutePath() + "'");
                continue;
            }

            apiFolders.add(apiFolder);
        }

        return apiFolders;
    }

    /**
     * @return a list of files named core.js inside the node_modules folder that should be executed to enable the core
     * LARA environment
     */
    public List<File> getNpmCoreFiles() {

        var coreFiles = new ArrayList<File>();
        for (var apiFolder : getNpmApiFolders()) {

            var coreFile = new File(apiFolder, "core.js");

            if (!coreFile.isFile()) {
                SpecsLogs.debug(() -> "Weaver API manager: did not find 'core.js' inside '"
                        + apiFolder.getAbsolutePath() + "'");
                continue;
            }

            coreFiles.add(coreFile);
        }

        return coreFiles;
    }

    public static WeaverApiManager newInstance(WeaverEngine engine) {
        // Get weaver id, replace spaces with _
        var weaverId = SpecsStrings.sanitizePath(engine.getNameAndBuild());

        // Build temporary folder for this set of APIs
        var baseFoldername = weaverId + "_apis";
        var baseFolder = SpecsIo.getTempFolder(baseFoldername);

        // Ensure there is a core folder inside
        var coreFolder = prepareCoreFolder(baseFolder, engine);

        SpecsLogs.debug(() -> "Weaver API manager: using '" + coreFolder.getAbsolutePath() + "' as code folder");

        // For compatibility reasons with ESM and GraalVM, node_modules folder should be on the working directory
        var npmFolder = prepareNpmFolder(SpecsIo.getWorkingDir(), engine);

        SpecsLogs.debug(() -> "Weaver API manager: using '" + npmFolder.getAbsolutePath() + "' as npm folder with node_modules folder inside");

        return new WeaverApiManager(coreFolder, npmFolder);
    }

    //    private static File prepareNpmFolder(File baseFolder, WeaverEngine engine) {
    private static File prepareNpmFolder(File npmFolder, WeaverEngine engine) {
        // Ensure there is an npm folder
        //var npmFolder = SpecsIo.mkdir(baseFolder, NPM_FOLDERNAME);

        // Ensure it has a node_modules folder
        var nodeModulesFolder = SpecsIo.mkdir(npmFolder, NODE_MODULES_FOLDERNAME);

        // Order keys so that checksum is repeatable
        var apis = engine.getApis();
        var orderedKeys = new ArrayList<>(apis.keySet());
        Collections.sort(orderedKeys);

        // Collect all resources
        var resources = orderedKeys.stream()
                .flatMap(key -> apis.get(key).stream())
                .collect(Collectors.toList());

        // Check if node_modules folder is ready for use, or needs to be prepared
        // If not ready for use, contents are deleted first
        if (!isReadyForUse(nodeModulesFolder, resources)) {
            extractNpmResources(nodeModulesFolder, engine);
            SpecsLogs.msgInfo("Weaver API manager: extracting APIs");
        }

        // Ensure it has a package.json file
        SpecsIo.write(new File(npmFolder, "package.json"), "{ \"type\" : \"module\" }\n");

        File javaFolder = SpecsIo.mkdir(new File(npmFolder, "node_modules/java"));
        SpecsIo.mkdir(new File(javaFolder, "api"));
        SpecsIo.write(new File(javaFolder, "package.json"), "{ \"type\" : \"module\", \"main\": \"index.js\" }\n");
        SpecsIo.write(new File(javaFolder, "index.js"), "export default {};\n");

        return npmFolder;
    }

    private static File prepareCoreFolder(File baseFolder, WeaverEngine engine) {
        // Ensure there is a core folder
        var coreFolder = SpecsIo.mkdir(baseFolder, CORE_FOLDERNAME);

        // Check if core folder is ready for use, or needs to be prepared
        if (!isReadyForUse(coreFolder, engine.getLaraCore())) {
            extractCoreResources(coreFolder, engine);
            SpecsLogs.msgInfo("Weaver API manager: extracting core");
        }

        return coreFolder;
    }

    private static void extractCoreResources(File destination, WeaverEngine engine) {
        // Clean folder
        SpecsIo.deleteFolderContents(destination, true);

        var resources = engine.getLaraCore();

        var numResources = resources.size();

        // Just extract the files to the folder
        resources.stream()
                .forEach(resource -> SpecsIo.resourceCopy(resource, destination));

        // Write checksum file
        var checksumContents = numResources + "\n"
                + calculateChecksum(resources);
        SpecsIo.write(new File(destination, CHECKSUM_FILENAME), checksumContents);
    }

    private static void extractNpmResources(File destination, WeaverEngine engine) {
        // Clean folder
        SpecsIo.deleteFolderContents(destination, true);

        var apis = engine.getApis();

        var numResources = 0;

        // For each API, follow the structure <package_name>/api/<resources>
        for (var packageName : apis.keySet()) {
            // Create package folder
            var packageFolder = SpecsIo.mkdir(destination, packageName);
            var apiFolder = SpecsIo.mkdir(packageFolder, "api");

            // Copy all resources
            var resources = apis.get(packageName);
            numResources += resources.size();

            for (var resource : resources) {
                // Manually sets destination folder to take into account that
                // LaraResourceProvider can have extra folders to avoid classpath collision
                var destinationFolder = new File(apiFolder, resource.getResourceLocation());
                SpecsIo.resourceCopy(resource.getResource(), destinationFolder, false);
            }
        }

        // Order keys so that checksum is repeatable
        var orderedKeys = new ArrayList<>(apis.keySet());
        Collections.sort(orderedKeys);

        // Collect all resources
        var resources = orderedKeys.stream()
                .flatMap(key -> apis.get(key).stream())
                .collect(Collectors.toList());

        // Write checksum file
        var checksumContents = numResources + "\n"
                + calculateChecksum(resources);
        SpecsIo.write(new File(destination, CHECKSUM_FILENAME), checksumContents);
    }

    /**
     * @param resourcesFolder
     * @return if true, means that resources need to be extracted to files, false means that folder can be reused as-is
     */
    private static boolean isReadyForUse(File resourcesFolder, Collection<ResourceProvider> resources) {
        // Check if checksum file exists
        var checksumFile = new File(resourcesFolder, CHECKSUM_FILENAME);

        // If no checksum file, needs to extract resources
        if (!checksumFile.isFile()) {
            return false;
        }

        // Check if this is a unique build, or just a testing build
        // If unique, checksum will always be the same, no need for extra checks
        // The presence of the file is enough
        var isIdUnique = SpecsSystem.getBuildNumber() != null;
        if (isIdUnique) {
            return true;
        }

        // Checksum file has two lines, number of resources and checksum
        var lines = StringLines.getLines(checksumFile);

        // If less than two lines there is a problem with checksum file
        if (lines.size() < 2) {
            return false;
        }

        var numberOfResources = Integer.parseInt(lines.get(0));

        var currentNumberOfResources = resources.size();

        // Number of resources changed
        if (numberOfResources != currentNumberOfResources) {
            return false;
        }

        // Calculate checksum of current resources and check if corresponds to the
        // checksum in the file
        var savedChecksum = lines.get(1);
        var currentChecksum = calculateChecksum(resources);

        if (!savedChecksum.equals(currentChecksum)) {
            return false;
        }

        // Same checksum, can reuse folder
        return true;
    }

    private static String calculateChecksum(Collection<ResourceProvider> resources) {
        var concatenatedChecksums = resources.stream()
                .map(resource -> SpecsIo.getMd5(SpecsIo.getResource(resource)))
                .collect(Collectors.joining());

        // Return the checksum of the concatenated checksums
        return SpecsIo.getMd5(concatenatedChecksums);
    }

}
