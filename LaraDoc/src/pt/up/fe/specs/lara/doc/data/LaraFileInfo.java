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

import pt.up.fe.specs.lara.doc.LaraDoc;

public class LaraFileInfo {

    private final File laraFile;
    private final File baseFolder;

    public LaraFileInfo(File laraFile, File baseFolder) {
        this.laraFile = laraFile;
        this.baseFolder = baseFolder;
    }

    public File getBaseFolder() {
        return baseFolder;
    }

    public File getLaraFile() {
        return laraFile;
    }

    public String getImportPath() {
        return LaraDoc.getImportPath(laraFile, baseFolder);
    }

}
