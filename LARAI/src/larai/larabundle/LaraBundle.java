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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.lara.interpreter.joptions.keys.FileList;

import com.google.common.base.Preconditions;

import pt.up.fe.specs.util.SpecsCollections;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.properties.SpecsProperties;

public class LaraBundle {

    private final static String LARA_BUNDLE_FILENAME = "lara.bundle";
    private final static String LARA_FOLDER_NAME = "lara";

    public static String getLaraBundleFilename() {
        return LARA_BUNDLE_FILENAME;
    }

    public static String getLaraFolderName() {
        return LARA_FOLDER_NAME;
    }

    public static SpecsProperties loadBundleFile(File bundlePath) {
        Preconditions.checkArgument(bundlePath.exists(), "Expected path '" + bundlePath + "' to exist");

        if (bundlePath.isDirectory()) {
            File bundleFile = new File(bundlePath, LARA_BUNDLE_FILENAME);
            if (!bundleFile.isFile()) {
                throw new RuntimeException(
                        "Could not find file '" + LARA_BUNDLE_FILENAME + "' in folder '" + bundlePath + "'");
            }

            return loadBundleFile(bundleFile);
        }

        return SpecsProperties.newInstance(bundlePath);
    }

    public static boolean isBundleFolder(File path) {
        return new File(path, LaraBundle.getLaraBundleFilename()).isFile();
    }

    // private final Set<String> languages;
    private final Set<String> weavers;
    private final Map<String, String> tagsMap;

    // public LaraBundle(Set<String> languages, Set<String> weavers) {
    // public LaraBundle(Set<String> weavers, String tagsMap) {
    // this(weavers, parseTags(tagsMap));
    // }
    //
    // private static Map<String, String> parseTags(String tagsMap) {
    // return null;
    // }

    public LaraBundle(String weaverName, Map<String, String> tagsMap) {
        this(Arrays.asList(weaverName), tagsMap);
    }

    public LaraBundle(Collection<String> names, Map<String, String> tagsMap) {
        // this.languages = languages;
        // this.weavers = weavers;
        // Make sure weaver names are in lower-case
        this.weavers = SpecsCollections.toSet(names, String::toLowerCase);
        // this.weavers = names.stream().map(String::toLowerCase).collect(Collectors.toSet());
        this.tagsMap = tagsMap;
    }

    public FileList process(FileList includeFolders) {
        List<File> unbundledFolders = new ArrayList<>();

        for (File includeFolder : includeFolders.getFiles()) {
            addFolder(includeFolder, unbundledFolders);
            // If no lara.bundle file, add and skip
            // File laraBundleFile = new File(includeFolder, "lara.bundle");
            // if (!laraBundleFile.isFile()) {
            // unbundledFolders.add(includeFolder);
            // continue;
            // }
            //
            // addBundleFolders(includeFolder, laraBundleFile, unbundledFolders);

        }
        return FileList.newInstance(unbundledFolders);

    }

    private void addFolder(File includeFolder, List<File> unbundledFolders) {
        unbundledFolders.add(includeFolder);

        // If no lara.bundle file, return
        File laraBundleFile = new File(includeFolder, "lara.bundle");
        if (!laraBundleFile.isFile()) {
            return;
        }

        addBundleFolders(includeFolder, laraBundleFile, unbundledFolders);
    }

    private void addBundleFolders(File includeFolder, File laraBundleFile, List<File> unbundledFolders) {
        SpecsProperties laraBundle = SpecsProperties.newInstance(laraBundleFile);

        BundleType bundleType = null;
        try {
            bundleType = BundleType.getHelper().valueOf(laraBundle.get(LaraBundleProperty.BUNDLE_TYPE));
        } catch (Exception e) {
            throw new RuntimeException("Problems while loading bundle folder '" + includeFolder + "'", e);
        }

        // If custom, get the tag

        Optional<Set<String>> tagValue = getTagValue(laraBundle);

        if (!tagValue.isPresent()) {
            SpecsLogs.msgInfo("No value set for bundle tag '" + laraBundle + "', ignoring bundles in folder '"
                    + includeFolder + "'");
            return;
        }

        switch (bundleType) {
        case CUSTOM:
            addBundleFolders(includeFolder, laraBundleFile, tagValue.get(), unbundledFolders);
            break;
        case WEAVER:
            addBundleFolders(includeFolder, laraBundleFile, weavers, unbundledFolders);
            break;
        default:
            throw new RuntimeException("Not implemented:" + bundleType);
        }

    }

    private Optional<Set<String>> getTagValue(SpecsProperties laraBundle) {
        String tag = laraBundle.get(LaraBundleProperty.BUNDLE_TAG);
        Preconditions.checkNotNull(tag, "Bundle has 'bundleType' property set to 'custom', but no 'tag' property");

        // Get current value of the tag
        String value = tagsMap.get(tag);

        if (value == null) {
            return Optional.empty();
        }
        // Preconditions.checkNotNull(value, "No value set for bundle tag '" + tag + "'");

        return Optional.of(new HashSet<>(Arrays.asList(value)));
    }

    private void addBundleFolders(File includeFolder, File laraBundleFile, Set<String> supportedNames,
            List<File> unbundledFolders) {

        // Each folder represents a weaver / language
        List<File> bundleFolders = SpecsIo.getFolders(includeFolder);

        // For each folder, check if name corresponds to a supported name
        boolean foundBundle = false;
        for (File bundleFolder : bundleFolders) {
            // If does not correspond, skip
            if (!isFolderSupported(bundleFolder.getName(), supportedNames)) {
                continue;
            }

            // Folder is supported, add. Calling this method
            // adds support for nested bundles
            addFolder(bundleFolder, unbundledFolders);
            // unbundledFolders.add(weaverFolder);

            if (!bundleFolder.getName().equals("lara")) {
                foundBundle = true;
            }
        }

        if (!foundBundle) {
            Preconditions.checkArgument(supportedNames.size() == 1,
                    "Expected size to be 1, it is " + supportedNames.size());
            String tag = supportedNames.stream().findFirst().get();
            SpecsLogs.msgInfo("Could not find a bundle for tag '" + tag + "' in folder '"
                    + includeFolder.getAbsolutePath() + "'");
        }
    }

    private boolean isFolderSupported(String weaverFoldername, Set<String> supportedNames) {
        // Do not mind case
        weaverFoldername = weaverFoldername.toLowerCase();

        // LARA is always supported
        if (weaverFoldername.equals("lara")) {
            return true;
        }

        if (supportedNames.contains(weaverFoldername)) {
            return true;
        }

        return false;
        /*
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
        */
    }

}
