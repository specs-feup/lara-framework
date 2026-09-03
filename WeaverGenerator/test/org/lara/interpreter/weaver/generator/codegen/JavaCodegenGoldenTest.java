package org.lara.interpreter.weaver.generator.codegen;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.lara.interpreter.weaver.generator.fixtures.BaselineRegen;
import org.lara.interpreter.weaver.generator.fixtures.DiffUtils;
import org.lara.interpreter.weaver.generator.fixtures.GeneratedTreeUtils;
import org.lara.interpreter.weaver.generator.fixtures.JavaMethodSignatureUtils;
import org.lara.interpreter.weaver.generator.fixtures.WeaverGeneratorTestHarness;
import org.lara.interpreter.weaver.generator.fixtures.WeaverGeneratorTestHarness.RunResult;
import org.lara.interpreter.weaver.generator.fixtures.WeaverGeneratorTestHarness.Scenario;

public class JavaCodegenGoldenTest {

    @TempDir
    Path temp;

    @Test
    @DisplayName("Minimal spec generation matches golden and is deterministic")
    void minimalGolden() throws Exception {
        runAndAssertGolden("minimal");
    }

    @Test
    @DisplayName("Medium spec generation matches golden and is deterministic")
    void mediumGolden() throws Exception {
        runAndAssertGolden("medium");
    }

    @Test
    @DisplayName("Edge spec generation matches golden and is deterministic")
    void edgeGolden() throws Exception {
        runAndAssertGolden("edge");
    }

    @Test
    @DisplayName("ThisType spec generation matches golden and is deterministic")
    void thistypeGolden() throws Exception {
        runAndAssertGolden("thistype");
    }

    private void runAndAssertGolden(String scenarioName) throws Exception {
        Scenario scenario = scenario(scenarioName);
        Path outDir = temp.resolve("gen-" + scenarioName);

        WeaverGeneratorTestHarness.assertSpecDirExists(scenario);

        RunResult firstRun = WeaverGeneratorTestHarness.run(scenario, outDir);
        assertThat(firstRun.exitCode()).as("WeaverGenerator should succeed").isZero();

        List<String> before = GeneratedTreeUtils.snapshotRelativePaths(outDir);
        RunResult secondRun = WeaverGeneratorTestHarness.run(scenario, outDir);
        assertThat(secondRun.exitCode()).as("WeaverGenerator should succeed (idempotency check)").isZero();
        List<String> after = GeneratedTreeUtils.snapshotRelativePaths(outDir);
        assertThat(after).as("Idempotent generation (file listing)").containsExactlyElementsOf(before);

        Path goldenRoot = Path.of("test-resources/golden/" + scenarioName);
        Map<String, Path> generatedFiles = GeneratedTreeUtils.snapshotFiles(outDir);

        if (BaselineRegen.isEnabled()) {
            persistGolden(goldenRoot, scenarioName, generatedFiles);
            return;
        }

        Map<String, Path> goldenFiles = snapshotGolden(goldenRoot, scenarioName);

        assertThat(generatedFiles.keySet())
                .as("Generated file set for scenario '%s'", scenarioName)
                .containsExactlyElementsOf(goldenFiles.keySet());

        for (Map.Entry<String, Path> entry : goldenFiles.entrySet()) {
            String relative = entry.getKey();
            Path generatedFile = generatedFiles.get(relative);
            assertThat(generatedFile).as("Generated file exists: " + relative).isNotNull();

            String generated = GeneratedTreeUtils.readNormalized(generatedFile);
            String golden = GeneratedTreeUtils.readNormalized(entry.getValue());
            DiffUtils.assertEqualsNormalized(golden, generated);
        }

        JavaMethodSignatureUtils.assertNoMethodSignatureCollisions(outDir, scenarioName);
    }

    private static Scenario scenario(String name) {
        return WeaverGeneratorTestHarness.scenario(name, name + ".pkg", capitalize(name) + "Weaver");
    }

    private static String capitalize(String value) {
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }

    private static void persistGolden(Path goldenRoot, String scenario, Map<String, Path> generatedFiles)
            throws IOException {
        GeneratedTreeUtils.deleteTree(goldenRoot);
        Files.createDirectories(goldenRoot);

        for (Map.Entry<String, Path> entry : generatedFiles.entrySet()) {
            String relative = toGoldenRelativePath(entry.getKey(), scenario);
            Path target = goldenRoot.resolve(relative.replaceFirst("\\.java$", ".java.txt"));
            GeneratedTreeUtils.writeNormalized(target, GeneratedTreeUtils.readNormalized(entry.getValue()));
        }
    }

    private static String toGoldenRelativePath(String generatedRelativePath, String scenario) {
        String prefix = scenario + "/";
        if (!generatedRelativePath.startsWith(prefix)) {
            throw new IllegalStateException(
                    "Generated file '" + generatedRelativePath + "' is not inside scenario '" + scenario + "'");
        }

        if (!generatedRelativePath.endsWith(".java")) {
            throw new IllegalStateException("Golden regeneration only supports Java files: " + generatedRelativePath);
        }

        return generatedRelativePath.substring(prefix.length());
    }

    private static Map<String, Path> snapshotGolden(Path goldenRoot, String scenario) throws IOException {
        return GeneratedTreeUtils.listFiles(goldenRoot, Files::isRegularFile).stream()
                .filter(path -> path.getFileName().toString().endsWith(".java.txt"))
                .collect(Collectors.toMap(
                        path -> scenario + "/"
                                + GeneratedTreeUtils.normalizeRelativePath(goldenRoot.relativize(path).toString())
                                        .replaceFirst("\\.java\\.txt$", ".java"),
                        Function.identity(),
                        (left, right) -> {
                            throw new IllegalStateException("Duplicate golden path: " + left);
                        },
                        TreeMap::new));
    }
}
