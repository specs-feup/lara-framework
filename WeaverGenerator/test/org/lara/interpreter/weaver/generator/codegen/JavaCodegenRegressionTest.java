/**
 * Copyright 2026 SPeCS.
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

package org.lara.interpreter.weaver.generator.codegen;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.lara.interpreter.weaver.generator.commandline.WeaverGenerator;

class JavaCodegenRegressionTest {

    @TempDir
    Path temp;

    @Test
    void superGetterDelegationUsesCommaSeparatedArguments() throws Exception {
        Path outDir = generateThistype();
        Path binaryExpr = outDir.resolve("thistype/pkg/abstracts/joinpoints/ABinaryExpr.java");

        String source = Files.readString(binaryExpr, StandardCharsets.UTF_8);
        assertThat(source)
                .as("delegated call should keep comma between multi-parameter arguments")
                .contains("findBetweenImpl(start, end)")
                .doesNotContain("findBetweenImpl(startend)");
    }

    @Test
    void generatedJoinpointDoesNotContainDuplicateMethodSignatures() throws Exception {
        Path outDir = generateThistype();
        Path node = outDir.resolve("thistype/pkg/abstracts/joinpoints/ANode.java");

        String source = Files.readString(node, StandardCharsets.UTF_8);
        assertThat(count(source, "List<String> getTagsImpl("))
                .as("getTagsImpl signature should be generated only once")
                .isEqualTo(1);
        assertThat(count(source, "final Object getTags("))
                .as("getTags wrapper signature should be generated only once")
                .isEqualTo(1);
    }

    private Path generateThistype() {
        Path specDir = Path.of("test-resources/spec/valid/thistype");
        Path outDir = temp.resolve("gen-thistype");

        String[] args = new String[] {
                "-x", specDir.toString(),
                "-o", outDir.toString(),
                "-p", "thistype.pkg",
                "-w", "ThistypeWeaver"
        };

        int exitCode = WeaverGenerator.run(args);
        assertThat(exitCode).as("WeaverGenerator should succeed").isZero();

        return outDir;
    }

    private static int count(String source, String needle) {
        int total = 0;
        int index = 0;
        while ((index = source.indexOf(needle, index)) >= 0) {
            total++;
            index += needle.length();
        }
        return total;
    }
}
