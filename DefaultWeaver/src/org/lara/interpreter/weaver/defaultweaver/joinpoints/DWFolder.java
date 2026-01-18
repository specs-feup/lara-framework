/*
 * Copyright 2013 SPeCS.
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
package org.lara.interpreter.weaver.defaultweaver.joinpoints;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.lara.interpreter.weaver.defaultweaver.abstracts.joinpoints.AFolder;

public class DWFolder extends AFolder {

    private final boolean getFilesRecursively = true;
    private final List<DWFile> files;
    private final String path;

    public DWFolder(File source) {
        path = source.getAbsolutePath();
        files = new ArrayList<>();
        createFiles(source);
    }

    public void createFiles(File folder) {

        for (final File f : folder.listFiles()) {
            if (f.isDirectory() && getFilesRecursively) {
                createFiles(f);
            } else if (f.getName().endsWith(".c")) {
                files.add(new DWFile(f));
            }
        }
    }

    @Override
    public Object getNode() {
        return path;
    }

    @Override
    public String getPathImpl() {
        return path;
    }
}
