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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents a LARA module which can be imported.
 * 
 * @author JoaoBispo
 *
 */
public class LaraDocModule {

    private final String importPath;
    private final File mainLara;
    private File baseLara;

    public LaraDocModule(String importPath, File mainLara) {
        this.importPath = importPath;
        this.mainLara = mainLara;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append(mainLara);
        if (baseLara != null) {
            builder.append(" + ").append(baseLara);
        }

        return builder.toString();
    }

    public String getImportPath() {
        return importPath;
    }

    public File getMainLara() {
        return mainLara;
    }

    public Optional<File> getBaseLara() {
        return Optional.ofNullable(baseLara);
    }

    public void setBaseLara(File baseLara) {
        this.baseLara = baseLara;
    }

    public List<File> getLaraFiles() {
        List<File> laraFiles = new ArrayList<>();

        laraFiles.add(mainLara);
        if (baseLara != null) {
            laraFiles.add(baseLara);
        }

        return laraFiles;
    }

}