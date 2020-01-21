/**
 * Copyright 2020 SPeCS.
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

package pt.up.fe.specs.lara.parser;

import org.junit.BeforeClass;

import pt.up.fe.specs.lara.parser.test.ParserTester;
import pt.up.fe.specs.util.SpecsSystem;

public class LaraParserTester extends ParserTester {

    private static final String BASE_PACKAGE = "pt/up/fe/specs/lara/parser/test/lara/";

    public LaraParserTester() {
        super(BASE_PACKAGE, new JavaCCLaraParser());
    }

    @BeforeClass
    public static void setupOnce() {
        SpecsSystem.programStandardInit();
    }

    // @Test
    // public void test() {
    // var laraFiles = SpecsIo.getFilesRecursive(new File(
    // "C:\\Users\\JoaoBispo\\Desktop\\shared\\repositories-programming\\lara-framework\\LaraApi\\src-lara\\lara"),
    // Arrays.asList("lara"));
    //
    // var parser = new JavaCCLaraParser();
    //
    // for (var laraFile : laraFiles) {
    // System.out.println("PARSING " + laraFile);
    // parser.parse(laraFile);
    // }
    // }

}
