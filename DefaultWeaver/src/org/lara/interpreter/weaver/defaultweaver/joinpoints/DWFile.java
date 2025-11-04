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
 * specific language governing permissions and limitations under the License.
 */
package org.lara.interpreter.weaver.defaultweaver.joinpoints;

import java.io.File;
import org.lara.interpreter.weaver.defaultweaver.abstracts.joinpoints.AFile;

public class DWFile extends AFile {

    private final File file;

    public DWFile(File f) {
        file = f;
    }

    @Override
    public String getNameImpl() {
        return file.getName();
    }

    @Override
    public String getAbsolutePathImpl() {
        return file.getAbsolutePath();
    }

    @Override
    public Object getNode() {
        return getName();
    }
}
