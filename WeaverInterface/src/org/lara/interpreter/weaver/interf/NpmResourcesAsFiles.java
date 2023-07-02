/**
 * Copyright 2023 SPeCS.
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

package org.lara.interpreter.weaver.interf;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.io.ResourceCollection;
import pt.up.fe.specs.util.providers.ResourceProvider;
import pt.up.fe.specs.util.utilities.StringLines;

/**
 * Java resources are a very convenient way of distributing files. However resources are not files, and sometimes it is
 * useful for resources to be available as files.
 * <p>
 * This class makes a collection of resources available as files in a folder, and manages its lifetime and update.
 * 
 * @author JBispo
 *
 */
public class NpmResourcesAsFiles {

    private final static String CHECKSUM_FILENAME = "checksum.txt";

    private final Map<String, File> resourceFolders;
    private final String subpath;

    public NpmResourcesAsFiles() {
        this.resourceFolders = new HashMap<>();
        this.subpath = "node_modules";
    }

    public File getApiFolder(ResourceCollection resourceCollection) {
        var key = resourceCollection.getId();
        var apisFolder = resourceFolders.get(key);

        // Build folder
        if (apisFolder == null) {
            apisFolder = buildFolder(resourceCollection);

            // Create package.json in parent folder, because we are using node_modules subpath
            SpecsIo.write(new File(apisFolder.getParent(), "package.json"), "{ \"type\" : \"module\" }");

            resourceFolders.put(key, apisFolder);
            SpecsLogs.info("Associated key '" + key + "' to folder '" + apisFolder.getAbsolutePath() + "'");
        }

        return apisFolder;

    }

    private File buildFolder(ResourceCollection resourceCollection) {

        var folder = getResourcesFolder(resourceCollection);
        var extractResources = checkExtractResources(folder, resourceCollection);

        if (extractResources) {
            extractResources(resourceCollection.getResources(), folder);
        }

        return folder;
    }

    /**
     * 
     * @param resourcesFolder
     * @return if true, means that resources need to be extracted to files, false means that folder can be reuses as-is
     */
    private boolean checkExtractResources(File resourcesFolder, ResourceCollection resourceCollection) {

        // Check if checksum file exists
        var checksumFile = new File(resourcesFolder, CHECKSUM_FILENAME);

        // If no checksum file, needs to extract resources
        if (!checksumFile.isFile()) {
            return true;
        }

        // Check if id is unique. In this case, checksum will always be the same, no need to check
        if (resourceCollection.isIdUnique()) {
            return false;
        }

        // Checksum file has two lines, number of resources and checksum
        var lines = StringLines.getLines(checksumFile);

        // If less than two lines there is a problem with checksum file
        if (lines.size() < 2) {
            return true;
        }

        var numberOfResources = Integer.parseInt(lines.get(0));

        var resources = resourceCollection.getResources();

        // Number of resources changed
        if (numberOfResources != resources.size()) {
            return true;
        }

        // Calculate checksum of current resources and check if corresponds to the checksum in the file
        // var savedChecksum = lines.subList(1, lines.size());
        // var currentChecksum = calculateChecksums(resources);
        var savedChecksum = lines.get(1);
        var currentChecksum = mergeChecksums(calculateChecksums(resources));

        if (!savedChecksum.equals(currentChecksum)) {
            return true;
        }

        // Same checksum, can reuse folder
        return false;
    }

    private List<String> calculateChecksums(Collection<ResourceProvider> resources) {
        var checksums = resources.stream()
                .map(resource -> SpecsIo.getMd5(SpecsIo.getResource(resource)))
                .collect(Collectors.toList());

        return checksums;
        // return mergeChecksums(checksums);
        // return resources.stream()
        // .map(resource -> SpecsIo.getMd5(SpecsIo.getResource(resource)))
        // .reduce((s1, s2) -> SpecsIo.getMd5(s1 + s2))
        // .orElseThrow(() -> new RuntimeException("Could not calculate checksum"));
    }

    private String mergeChecksums(List<String> checksums) {
        return SpecsIo.getMd5(checksums.stream().collect(Collectors.joining()));
    }

    private File getResourcesFolder(ResourceCollection resourceCollection) {
        var baseFolder = SpecsIo.getTempFolder(resourceCollection.getId());

        if (subpath != null) {
            baseFolder = SpecsIo.mkdir(baseFolder, subpath);
        }

        return baseFolder;
    }

    private void extractResources(Collection<ResourceProvider> resources, File destination) {

        // Clean folder
        SpecsIo.deleteFolderContents(destination, true);

        // Extract resources
        for (var resource : resources) {
            SpecsIo.resourceCopy(resource, destination);
        }

        // Write checksum file
        var checksumContents = resources.size() + "\n"
        // + calculateChecksums(resources).stream().collect(Collectors.joining("\n"));
                + mergeChecksums(calculateChecksums(resources));
        SpecsIo.write(new File(destination, CHECKSUM_FILENAME), checksumContents);
        // SpecsIo.write(new File(destination, CHECKSUM_FILENAME),
        // "Extracted:\n" + resources.stream().map(r -> r.getResource()).collect(Collectors.joining("\n")));
    }
}
