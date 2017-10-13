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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import pt.up.fe.specs.util.SpecsLogs;

/**
 * Represents one or more LARA modules.
 * 
 * @author JoaoBispo
 *
 */
public class LaraDocPackage {

    private final String packageName;
    private final Map<String, LaraDocModule> packageModules;

    public LaraDocPackage(String packageName) {
        this.packageName = packageName;
        this.packageModules = new HashMap<>();
    }

    @Override
    public String toString() {
        return "Package '" + packageName + "' -> " + packageModules;
    }

    public void add(String importPath, File laraFile) {
        LaraDocModule module = new LaraDocModule(importPath, laraFile);
        LaraDocModule previousModule = packageModules.put(importPath, module);
        if (previousModule != null) {
            SpecsLogs.msgInfo("Replacing LARA module with import path '" + importPath
                    + "': current file is ' " + laraFile + " ' and previous file is '" + previousModule.getMainLara()
                    + " '");
        }

    }

    public Optional<LaraDocModule> getModule(String importPath) {
        return Optional.ofNullable(packageModules.get(importPath));
    }

}
