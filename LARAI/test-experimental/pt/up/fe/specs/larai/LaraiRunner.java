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

package pt.up.fe.specs.larai;

import java.io.File;

import org.junit.Test;

import larai.LaraI;

public class LaraiRunner {

    @Test
    public void testConfigFile() {
        File configFile = new File(
                "C:\\Users\\JoaoBispo\\Desktop\\shared\\clava-tests\\Tests\\2017-09_lara_resource\\resource_example.clava");

        LaraI.main(new String[] { "--config", configFile.getAbsolutePath() });
    }

}
