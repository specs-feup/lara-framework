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
import java.util.stream.Collectors;

import pt.up.fe.specs.lara.doc.aspectir.AspectIrDoc;

/**
 * Represents a LARA module which can be imported.
 * 
 * <p>
 * This is a leaf node, and has no children.
 * 
 * 
 * @author JoaoBispo
 *
 */
public class LaraDocModule extends LaraDocNode {

    private final String importPath;
    private File mainLara;
    private File baseLara;
    private File mainJs;

    private AspectIrDoc documentation;

    public LaraDocModule(String importPath) {
        this(importPath, null);
    }

    public LaraDocModule(String importPath, File mainLara) {
        this.importPath = importPath;
        this.mainLara = mainLara;
        documentation = null;
    }

    @Override
    public String getId() {
        return getImportPath();
    }

    @Override
    public String toContentString() {
        StringBuilder builder = new StringBuilder();

        builder.append("Module '" + importPath + "'");

        List<File> sources = new ArrayList<>();
        if (mainLara != null) {
            sources.add(mainLara);
        }
        if (mainJs != null) {
            sources.add(mainJs);
        }
        if (baseLara != null) {
            sources.add(baseLara);
        }

        var sourcesString = sources.stream()
                .map(File::getName)
                .collect(Collectors.joining(" + ", "(", ")"));

        builder.append(" ").append(sourcesString);
        /*
        builder.append(" (");
        
        builder.append(mainLara);
        if (baseLara != null) {
            builder.append(" + ").append(baseLara);
        }
        
        builder.append(")");
        */
        return builder.toString();
    }

    /*
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
    
        builder.append(mainLara);
        if (baseLara != null) {
            builder.append(" + ").append(baseLara);
        }
    
        return builder.toString();
    }
    */

    public String getImportPath() {
        return importPath;
    }

    public File getMainLara() {
        return mainLara;
    }

    public boolean hasMainLara() {
        return mainLara != null;
    }

    public boolean hasMainFile() {
        return mainLara != null || mainJs != null;
    }

    public void setMainLara(File mainLara) {
        this.mainLara = mainLara;
    }

    public void setMainJs(File mainJs) {
        this.mainJs = mainJs;
    }

    public Optional<File> getBaseLara() {
        return Optional.ofNullable(baseLara);
    }

    public void setBaseLara(File baseLara) {
        this.baseLara = baseLara;
    }

    public List<File> getLaraFiles() {
        List<File> laraFiles = new ArrayList<>();

        // laraFiles.add(mainLara);
        if (mainLara != null) {
            laraFiles.add(mainLara);
        }

        if (baseLara != null) {
            laraFiles.add(baseLara);
        }

        return laraFiles;
    }

    public File getMainJs() {
        return mainJs;
    }

    public void setDocumentation(AspectIrDoc documentation) {
        this.documentation = documentation;
    }

    public Optional<AspectIrDoc> getDocumentationTry() {
        return Optional.ofNullable(documentation);
    }

    public AspectIrDoc getDocumentation() {
        return getDocumentationTry().orElseThrow(() -> new RuntimeException("Documentation not set"));
    }

}
