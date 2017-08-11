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

package larai.larabundle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.lara.interpreter.joptions.keys.FileList;

import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.properties.SpecsProperties;

public class LaraBundle {

    private final Set<String> languages;
    private final Set<String> weavers;

    public LaraBundle(Set<String> languages, Set<String> weavers) {
        this.languages = languages;
        this.weavers = weavers;
    }

    public FileList process(FileList includeFolders) {
        List<File> unbundledFolders = new ArrayList<>();

        for (File includeFolder : includeFolders.getFiles()) {

            // If no lara.bundle file, add and skip
            File laraBundleFile = new File(includeFolder, "lara.bundle");
            if (!laraBundleFile.isFile()) {
                unbundledFolders.add(includeFolder);
                continue;
            }

            addBundleFolders(includeFolder, laraBundleFile, unbundledFolders);

        }
        return FileList.newInstance(unbundledFolders);

    }

    private void addBundleFolders(File includeFolder, File laraBundleFile, List<File> unbundledFolders) {
        SpecsProperties laraBundle = SpecsProperties.newInstance(laraBundleFile);

        BundleType bundleType = BundleType.getHelper().valueOf(laraBundle.get(LaraBundleProperty.BUNDLE_TYPE));

        switch (bundleType) {
        case LANGUAGE:
            addBundleFolders(includeFolder, laraBundleFile, languages, unbundledFolders);
            break;
        case WEAVER:
            addBundleFolders(includeFolder, laraBundleFile, weavers, unbundledFolders);
            break;
        default:
            throw new RuntimeException("Not implemented:" + bundleType);
        }

    }

    private void addBundleFolders(File includeFolder, File laraBundleFile, Set<String> supportedNames,
            List<File> unbundledFolders) {

        // Each folder represents a weaver / language
        List<File> weaverFolders = SpecsIo.getFolders(includeFolder);

        // For each folder, check if name of current weaver corresponds with folder name
        for (File weaverFolder : weaverFolders) {

            // If does not correspond, skip
            if (!isFolderSupported(weaverFolder.getName(), supportedNames)) {
                continue;
            }

            // Folder is supported, add
            unbundledFolders.add(weaverFolder);
        }
    }

    private boolean isFolderSupported(String weaverFoldername, Set<String> supportedNames) {
        // Split name
        String[] names = weaverFoldername.toLowerCase().split("_");

        for (String name : names) {
            // LARA is always supported
            if (name.equals("lara")) {
                return true;
            }

            if (supportedNames.contains(name)) {
                return true;
            }
        }

        return false;
    }

}
