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

import java.io.File;

import larac.LaraC;
import pt.up.fe.specs.util.SpecsCheck;

public class FileLaraImport extends LaraImport {

    private final File importFile;

    public FileLaraImport(String importPath, File importFile) {
        super(importPath);

        SpecsCheck.checkArgument(importFile.exists(), () -> "Expected import file to exist: " + importFile);
        this.importFile = importFile;
    }

    @Override
    public void resolveImport(LaraC lara) {
        lara.printSubTopic("Importing " + importFile);
        LaraImports.importLaraFile(lara, getImportPath(), importFile);
    }

}
