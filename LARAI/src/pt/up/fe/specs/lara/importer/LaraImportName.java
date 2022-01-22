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

public class LaraImportName {

    private final String filename;
    private final String folderPath;

    /**
     * Uses the same format as the imports in LARA files (e.g. weaver.Query).
     * 
     * @param importName
     */
    public LaraImportName(String importName) {
        // Split into fileName and filePath
        int dotIndex = importName.lastIndexOf('.');

        this.filename = dotIndex == -1 ? importName : importName.substring(dotIndex + 1);
        var tempFilepath = dotIndex == -1 ? "" : importName.substring(0, dotIndex + 1);
        this.folderPath = tempFilepath.replace('.', '/');
    }

    public String getFilename() {
        return filename;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public String getFullPath() {
        return folderPath + filename;
    }

    @Override
    public String toString() {
        return "Lara Import (" + getFullPath() + ")";
    }

}
