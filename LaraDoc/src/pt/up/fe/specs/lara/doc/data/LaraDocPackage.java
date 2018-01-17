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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import pt.up.fe.specs.util.SpecsLogs;

/**
 * Represents one or more LARA modules.
 * 
 * <p>
 * Represents a set of LARA functionality (e.g., modules, bundles...).
 * 
 * @author JoaoBispo
 *
 */
public class LaraDocPackage extends LaraDocNode implements Comparable<LaraDocPackage> {

    private final String packageName;
    private final Map<String, LaraDocModule> packageModules;

    public LaraDocPackage(String packageName) {
        this.packageName = packageName;
        this.packageModules = new HashMap<>();
    }

    /*
    @Override
    public String toString() {
        return "Package '" + packageName + "' -> " + packageModules;
    }
    */

    @Override
    public String getId() {
        return getPackageName();
    }

    @Override
    public String toContentString() {
        return "Package '" + packageName + "'";
    }

    public String getPackageName() {
        return packageName;
    }

    public Collection<LaraDocModule> getModules() {
        return getChildrenOf(LaraDocModule.class);
        // return packageModules.values();
    }

    @Override
    public boolean addChild(LaraDocNode child) {
        if (child instanceof LaraDocModule) {
            LaraDocModule module = (LaraDocModule) child;
            LaraDocModule previousModule = packageModules.put(module.getImportPath(), module);
            if (previousModule != null) {
                SpecsLogs.msgInfo("Replacing LARA module with import path '" + module.getImportPath()
                        + "': current file is ' " + module.getMainLara() + " ' and previous file is '"
                        + previousModule.getMainLara()
                        + " '");

                // Remove from tree
                removeChild(previousModule);
            }

        }

        return super.addChild(child);
    }

    /*
    public void addModule(String importPath, File laraFile) {
        LaraDocModule module = new LaraDocModule(importPath, laraFile);
        LaraDocModule previousModule = packageModules.put(importPath, module);
        if (previousModule != null) {
            SpecsLogs.msgInfo("Replacing LARA module with import path '" + importPath
                    + "': current file is ' " + laraFile + " ' and previous file is '" + previousModule.getMainLara()
                    + " '");
        }
    
    }
    */

    // public Map<String, LaraDocModule> getModulesMap() {
    // return getModules().stream().collect(Collectors.toMap(LaraDocModule::getImportPath, module -> module));
    // }

    public Optional<LaraDocModule> getModule(String importPath) {
        return Optional.ofNullable(packageModules.get(importPath));
    }

    @Override
    public int compareTo(LaraDocPackage o) {
        return packageName.compareTo(o.packageName);
    }

}
