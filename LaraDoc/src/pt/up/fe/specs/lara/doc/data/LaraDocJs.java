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
import java.util.Optional;

import pt.up.fe.specs.lara.doc.aspectir.AspectIrDoc;

/**
 * Represents a JavaScript file.
 * 
 * <p>
 * This is a leaf node, and has no children.
 * 
 * 
 * @author JoaoBispo
 *
 */
public class LaraDocJs extends LaraDocNode {

    private final File mainFile;
    private AspectIrDoc documentation;

    public LaraDocJs(File mainFile) {
        this.mainFile = mainFile;
        documentation = null;
    }

    @Override
    public String getId() {
        return mainFile.getName();
    }

    @Override
    public String toContentString() {
        StringBuilder builder = new StringBuilder();

        builder.append("JS '" + mainFile.getName() + "'");

        return builder.toString();
    }

    public File getJsFile() {
        return mainFile;
    }

    public void setDocumentation(AspectIrDoc documentation) {
        this.documentation = documentation;
    }

    public Optional<AspectIrDoc> getDocumentation() {
        return Optional.ofNullable(documentation);
    }

}
