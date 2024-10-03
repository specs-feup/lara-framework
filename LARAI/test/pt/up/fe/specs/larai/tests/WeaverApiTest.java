/**
 * Copyright 2016 SPeCS.
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

package pt.up.fe.specs.larai.tests;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fe.specs.larai.GenericWeaverTester;
import pt.up.fe.specs.util.SpecsSystem;

public class WeaverApiTest {

    @BeforeClass
    public static void setupOnce() {
        SpecsSystem.programStandardInit();
        GenericWeaverTester.clean();
    }

    @After
    public void tearDown() {
        GenericWeaverTester.clean();
    }

    private static GenericWeaverTester newTester() {
        return new GenericWeaverTester("larai/test/weaver/")
                .setResultPackage("results/");

    }

    @Test
    public void testWeaverOptions() {
        newTester().test("WeaverOptionsTest.js");
    }

    @Test
    public void testWeaver() {
        newTester().test("WeaverTest.lara");
    }

    /**
     * TODO: Disable feature where more than one file is imported, with the same name?
     */
    @Test
    public void testLaraImportInJs() {
        newTester().test("LaraImportFromJsTest.js", "LaraImportFromJsJs.js",
                "LaraImportFromJsMjs.mjs");
    }

    @Test
    public void testLaraImportInMjs() {
        newTester().test("LaraImportFromMjsTest.mjs", "LaraImportFromMjsJs.js",
                "LaraImportFromMjsMjs.mjs");
    }

    @Test
    public void testLaraImportStar() {
        newTester().test("LaraImportStar.js");
    }

}
