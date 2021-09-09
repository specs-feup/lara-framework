/**
 * Copyright 2021 SPeCS.
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

package larac.imports;

import larac.LaraC;

public abstract class LaraImport {

    private final String importPath;

    public LaraImport(String importPath) {
        this.importPath = importPath;
    }

    public String getImportPath() {
        return importPath;
    }

    public abstract void resolveImport(LaraC lara);

    /*  
    // 1.
    // Check include folders
    for (final File path : lara.getOptions().getIncludeFolders()) {
        for (var ext : LaraC.getSupportedExtensions()) {
            var importPath = relativePath + "." + ext;
    
            final File importingFile = new File(path, importPath);
            if (importingFile.exists()) {
                lara.printSubTopic("Importing " + importingFile);
                importLaraFile(lara, importPath, importingFile);
                foundImport = true;
            }
        }
    }
    
    // 2.
    // Check resource by filename, instead of resource name
    for (var ext : LaraC.getSupportedExtensions()) {
        var importPath = relativePath + "." + ext;
    
        var resource = lara.getOptions().getIncludeResourcesMap().get(importPath);
        if (!resource.isEmpty()) {
            importLaraResource(lara, importPath, resource.get(0));
    
            // importScriptFile(lara, filepath, findFirst.get());
            foundImport = true;
        }
    
        // Optional<LaraResourceProvider> findFirst = lara.getOptions().getIncludeResources().stream()
        // .filter(r -> r.getFileLocation().replace("/", File.separator).equals(importPath))
        // .findFirst();
        //
        // if (findFirst.isPresent()) {
        // importLaraResource(lara, importPath, findFirst.get());
        //
        // // importScriptFile(lara, filepath, findFirst.get());
        // foundImport = true;
        // }
    
    }
    */
}
