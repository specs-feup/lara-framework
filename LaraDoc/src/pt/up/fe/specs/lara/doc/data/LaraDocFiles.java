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

package pt.up.fe.specs.lara.doc.data;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Preconditions;

import pt.up.fe.specs.util.SpecsCollections;
import pt.up.fe.specs.util.SpecsLogs;

/**
 * Information about a set of LARA files, organized into modules, packages and bundles.
 * 
 * @author JoaoBispo
 *
 */
public class LaraDocFiles {

    private static final String DEFAULT_PACKAGE_ID = "Default Package";

    private final Map<String, LaraDocBundle> bundles;
    private final Map<String, LaraDocPackage> packages;
    private final Map<String, File> baseFiles;
    private final Deque<LaraDocBundle> bundleStack;
    private final Deque<String> packageNameStack;

    // Tree-based code
    // private final LaraDocPackage topLevelPackage;
    // private final Deque<LaraDocPackage> packageStack;

    public LaraDocFiles() {
        this.bundles = new HashMap<>();
        this.packages = new HashMap<>();
        this.baseFiles = new HashMap<>();
        this.bundleStack = new ArrayDeque<>();
        this.packageNameStack = new ArrayDeque<>();

        // Tree-based code
        // this.topLevelPackage = getOrCreatePackage("Top-level package");
        // pushPackage(topLevelPackage.getPackageName());
    }

    // public LaraDocPackage getTopLevelPackage() {
    // return topLevelPackage;
    // }

    public void pushPackage(String packageName) {
        packageNameStack.push(packageName);
    }

    public String popPackage() {
        return packageNameStack.pop();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("Bundles:\n").append(bundles).append("\n");
        builder.append("Packages:\n").append(packages).append("\n");
        builder.append("Base files:\n").append(baseFiles).append("\n");

        return builder.toString();
    }

    public void pushBundle(LaraDocBundle bundle) {
        this.bundleStack.push(bundle);
    }

    public void popBundle() {
        this.bundleStack.pop();
    }

    public void pushPackageName(String packageName) {
        this.packageNameStack.push(packageName);
    }

    public void popPackageName() {
        this.packageNameStack.pop();
    }

    public void addBaseFile(String importPath, File laraFile) {
        File previousLaraFile = baseFiles.put(importPath, laraFile);
        if (previousLaraFile != null && !previousLaraFile.equals(laraFile)) {
            SpecsLogs.msgInfo("Replacing previous base file '" + previousLaraFile + "' with import path '" + importPath
                    + "' with new base file '" + laraFile + "'");
        }
    }

    public void addLaraModule(String importPath, File laraFile) {
        getCurrentPackage().addChild(new LaraDocModule(importPath, laraFile));
        // getCurrentPackage().addModule(importPath, laraFile);
    }

    private LaraDocPackage getCurrentPackage() {
        if (packageNameStack.isEmpty()) {
            SpecsLogs.warn("Package name stack is empty, using default name");
        }

        String packageName = !packageNameStack.isEmpty() ? packageNameStack.peek() : DEFAULT_PACKAGE_ID;

        // Give priority to bundle stack
        LaraDocPackage laraPackage = !bundleStack.isEmpty() ? bundleStack.peek().getOrCreate(packageName)
                : getOrCreatePackage(packageName);
        return laraPackage;
    }

    private LaraDocPackage getOrCreatePackage(String packageName) {
        LaraDocPackage laraPackage = packages.get(packageName);
        if (laraPackage == null) {
            laraPackage = new LaraDocPackage(packageName);
            packages.put(packageName, laraPackage);
        }

        return laraPackage;
    }

    public LaraDocBundle getOrCreateBundle(String bundleName) {
        LaraDocBundle laraBundle = bundles.get(bundleName);
        if (laraBundle == null) {
            laraBundle = new LaraDocBundle(bundleName);
            bundles.put(bundleName, laraBundle);
        }

        return laraBundle;
    }

    /**
     * Merges current base files into all current packages and bundles
     */
    public void mergeBaseFiles() {

        List<LaraDocPackage> allPackages = getAllPackages();

        // for each base file, set itself when it finds identical import paths
        for (Entry<String, File> baseFileEntry : baseFiles.entrySet()) {
            // setBaseFile(baseFileEntry.getKey(), baseFileEntry.getValue());
            String baseImportPath = baseFileEntry.getKey();
            Preconditions.checkArgument(baseImportPath.endsWith("Base"));
            String importPath = baseImportPath.substring(0, baseImportPath.length() - "Base".length());

            File baseFile = baseFileEntry.getValue();

            allPackages.stream()
                    .flatMap(laraPackage -> SpecsCollections.toStream(laraPackage.getModule(importPath)))
                    .forEach(module -> module.setBaseLara(baseFile));
        }

    }

    /*
    private void setBaseFile(String importPath, File baseFile) {
    
        // Look in bundles
        for (LaraDocBundle bundle : bundles.values()) {
    
            bundle.getPackages().stream()
                    .flatMap(laraPackage -> SpecsCollections.toStream(laraPackage.getModule(importPath)))
                    .forEach(module -> module.setBaseLara(baseFile));
        }
    
        // Look in packages
    
        // return null;
    }
    */
    private List<LaraDocPackage> getAllPackages() {
        List<LaraDocPackage> allPackages = new ArrayList<>();

        // Look in bundles
        bundles.values().stream().forEach(bundle -> allPackages.addAll(bundle.getPackages()));

        // Look in packages
        allPackages.addAll(packages.values());

        return allPackages;
    }

    public Collection<LaraDocBundle> getBundles() {
        return bundles.values();
    }

    public Collection<LaraDocPackage> getPackages() {
        return packages.values();
    }

    /**
     * 
     * @return all the modules
     */
    public Collection<LaraDocModule> getModules() {
        List<LaraDocModule> modules = new ArrayList<>();

        // Add modules from bundles
        for (LaraDocBundle bundle : getBundles()) {
            for (LaraDocPackage laraPackage : bundle.getPackages()) {
                modules.addAll(laraPackage.getModules());
            }
        }

        // Add modules from packages
        for (LaraDocPackage laraPackage : getPackages()) {
            modules.addAll(laraPackage.getModules());
        }

        return modules;
    }
}
