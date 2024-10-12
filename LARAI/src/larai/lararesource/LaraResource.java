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

package larai.lararesource;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.lara.interpreter.joptions.keys.FileList;
import org.lara.interpreter.weaver.interf.WeaverEngine;

import com.google.common.base.Preconditions;

import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.SpecsStrings;
import pt.up.fe.specs.util.utilities.LineStream;

public class LaraResource {

    private static final String LARA_RESOURCE_FOLDER = "laraResources";
    private static final String LARA_RESOURCE_FILE = "lara.resource";

    private final WeaverEngine weaver;

    public LaraResource(WeaverEngine weaver) {
        this.weaver = weaver;
    }

    public FileList process(FileList includeFolders) {
        Set<File> processedFolders = new HashSet<>();
        boolean addedResourceFolder = false;
        for (File includeFolder : includeFolders.getFiles()) {
            // System.out.println("ADDING FOLDER: " + includeFolder);
            // If no resource folder, add it
            // Add resource folders recursively, unless it is a bundle folder
            // Check if folder as a 'resources' folder
            if (!isLaraResource(includeFolder)) {
                processedFolders.add(includeFolder);

                // if (isFromBundle(includeFolder)) {
                // System.out.println("SKIPPING: " + includeFolder);
                // continue;
                // }
                // If lara.bundle file, skip folder
                File laraBundleFile = new File(includeFolder, "lara.bundle");
                if (laraBundleFile.isFile()) {
                    // System.out.println("LARA RESOURCE SKIPPING FOLDER " + includeFolder);
                    continue;
                }

                // File resourceFolder = new File(includeFolder, "resources");
                // if (isLaraResource(resourceFolder)) {
                // addedResourceFolder = addResourceFolder(resourceFolder, processedFolders, addedResourceFolder);
                // }

                for (File subfolder : getFoldersRecursiveExludingBundles(includeFolder)) {
                    // if (isFromBundle(subfolder)) {
                    // System.out.println("SKIPPING: " + subfolder);
                    // continue;
                    // }

                    if (isLaraResource(subfolder)) {
                        addedResourceFolder = addResourceFolder(subfolder, processedFolders, addedResourceFolder);

                        // File laraResourceFolderLARA RESOURCE FOLDER: = processLaraFolder(subfolder);
                        //
                        // if (!addedResourceFolder) {
                        // processedFolders.add(laraResourceFolder);
                        // addedResourceFolder = true;
                        // }

                    }
                }

                continue;
            }

            addedResourceFolder = addResourceFolder(includeFolder, processedFolders, addedResourceFolder);
            /*
            // Process folder. Returns include resource folder,
            // which is the same for all Lara resources, add it
            // only once.
            
            File laraResourceFolder = processLaraFolder(includeFolder);
            
            if (!addedResourceFolder) {
                processedFolders.add(laraResourceFolder);
                addedResourceFolder = true;
            }
            */
        }

        return FileList.newInstance(processedFolders);

    }

    private List<File> getFoldersRecursiveExludingBundles(File includeFolder) {

        Predicate<File> exclude = bundleFolder -> new File(bundleFolder, "lara.bundle").isFile();
        List<File> collecterFolders = new ArrayList<>();

        getFoldersRecursiveExludingBundles(includeFolder, collecterFolders, exclude);

        return collecterFolders;
    }

    private void getFoldersRecursiveExludingBundles(File baseFolder, List<File> collecterFolders,
            Predicate<File> exclude) {

        // Check if subfolder should be considered
        if (exclude.test(baseFolder)) {
            return;
        }

        collecterFolders.add(baseFolder);

        // Process children
        List<File> subFolders = SpecsIo.getFolders(baseFolder);
        for (File subFolder : subFolders) {
            getFoldersRecursiveExludingBundles(subFolder, collecterFolders, exclude);
        }

    }

    private boolean addResourceFolder(File includeFolder, Set<File> processedFolders, boolean addedResourceFolder) {
        // Process folder. Returns include resource folder,
        // which is the same for all Lara resources, add it
        // only once.

        File laraResourceFolder = processLaraFolder(includeFolder);

        if (!addedResourceFolder) {
            processedFolders.add(laraResourceFolder);
            addedResourceFolder = true;
        }
        // System.out.println("LARA RESOURCE FOLDER: " + includeFolder);
        return addedResourceFolder;
    }

    private File processLaraFolder(File includeFolder) {
        // Get import path
        String importPath = getImportPath(includeFolder);

        // Get resource name
        String resourceName = getResourceName(importPath);

        String laraFileContents = buildLaraFileContents(includeFolder, resourceName);

        File laraFile = getLaraFile(importPath);
        SpecsLogs.debug(() -> "Creating LARA resource helper file in " + laraFile.getAbsolutePath());
        SpecsIo.write(laraFile, laraFileContents);

        return getLaraResourceFolder();
    }

    private String buildLaraFileContents(File includeFolder, String resourceName) {
        StringBuilder code = new StringBuilder();

        code.append("import lara.Io;\n");
        code.append("import lara.util.LocalFolder;\n\n");

        String escapedPath = SpecsStrings.escapeJson(includeFolder.getAbsolutePath());

        code.append("var " + resourceName + " = new LocalFolder(\"" + escapedPath + "\");\n\n");

        // code.append(resourceName + ".getFileList = function() {\n" +
        // " var files = SpecsIo.getFilesRecursive(this.baseFolder);\n" +
        // " var resourceFile = Io.getPath(this.getBaseFolder(), \"" + LARA_RESOURCE_FILE + "\");\n" +
        // " files.remove(resourceFile);\n" +
        // " return files;\n" +
        // "}");

        code.append(resourceName + ".getFileList = function(path) {\n" +
                "    var files = this._getFileListPrivate(path);\n" +
                "    var resourceFile = Io.getPath(this.getBaseFolder(), \"" + LARA_RESOURCE_FILE + "\");\n" +
                "    files.remove(resourceFile);\n" +
                "    return files;\n" +
                "}");
        return code.toString();

    }

    private String getResourceName(String importPath) {
        int lastDotIndex = importPath.lastIndexOf('.');

        if (lastDotIndex == -1) {
            return importPath;
        }

        Preconditions.checkArgument(!importPath.endsWith("."),
                "Import path of lara resource must not end with dot (.): " + importPath);

        return importPath.substring(lastDotIndex + 1);
    }

    public static File getLaraResourceFile(File includeFolder) {
        return new File(includeFolder, LARA_RESOURCE_FILE);
    }

    private String getImportPath(File includeFolder) {
        try (LineStream lines = LineStream.newInstance(getLaraResourceFile(includeFolder))) {
            while (lines.hasNextLine()) {
                String line = lines.nextLine().trim();

                // Ignore lines starting with #
                if (line.startsWith("#")) {
                    continue;
                }

                return line;
            }
        }

        throw new RuntimeException("Could not find an import path inside the lara.resource '"
                + getLaraResourceFile(includeFolder).getAbsolutePath() + "'");
    }

    private boolean isLaraResource(File includeFolder) {
        return getLaraResourceFile(includeFolder).isFile();
    }

    private File getLaraResourceFolder() {
        return new File(weaver.getTemporaryWeaverFolder(), LARA_RESOURCE_FOLDER);
    }

    private File getLaraFile(String importPath) {
        String laraFile = importPath.replace('.', '/') + ".lara";
        return new File(getLaraResourceFolder(), laraFile);
    }

}
