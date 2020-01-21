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

package pt.up.fe.specs.lara.parser.test;

import static org.junit.Assert.*;

import pt.up.fe.specs.lara.parser.LaraParser;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.SpecsStrings;

public class ParserTester {

    private final String basePackage;
    private final LaraParser parser;

    public ParserTester(String basePackage, LaraParser parser) {
        this.basePackage = basePackage;
        this.parser = parser;
    }

    protected void testFile(String filename) {
        var resource = basePackage + filename;
        var inputCode = SpecsIo.getResource(resource);

        if (inputCode == null) {
            throw new RuntimeException("Could not find resource " + resource);
        }

        var rootNode = parser.parse(inputCode, resource);
        var parsedCode = rootNode.getCode();

        var expectedResultsResource = basePackage + filename + ".txt";
        if (!SpecsIo.hasResource(expectedResultsResource)) {
            SpecsLogs.msgInfo("Could not find resource '" + expectedResultsResource
                    + "', skipping verification. Actual output:\n" + parsedCode);
            return;
        }

        var expectedOutput = SpecsIo.getResource(expectedResultsResource);

        var normalizedExpected = SpecsStrings.normalizeFileContents(expectedOutput, true);
        var normalizedOutput = SpecsStrings.normalizeFileContents(parsedCode, true);

        assertEquals(normalizedExpected, normalizedOutput);
    }

}
