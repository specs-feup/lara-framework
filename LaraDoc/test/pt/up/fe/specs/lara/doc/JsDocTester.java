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

package pt.up.fe.specs.lara.doc;

import org.junit.Test;

import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.SpecsSystem;

public class JsDocTester {

    @Test
    public void test() {
        SpecsSystem.programStandardInit();

        var tempFolder = SpecsIo.getTempFolder("laradoc_test/src-lara");
        SpecsLogs.info("Cleaning temporary folder " + tempFolder.getAbsolutePath());
        SpecsIo.deleteFolderContents(tempFolder);

        SpecsIo.resourceCopy("pt/up/fe/specs/lara/doc/docExample.js", tempFolder);
        SpecsIo.resourceCopy("pt/up/fe/specs/lara/doc/docExample2.js", tempFolder);

        String[] args = { "--weaver", "org.lara.interpreter.weaver.defaultweaver.DefaultWeaver", "--output",
                "./run/doctest", "--clean", "--exclude", "_", "--packages",
                "{'Test API': ['" + tempFolder.getAbsolutePath().replace('\\', '/') + "']}" };
        LaraDocLauncher.main(args);
    }

}
