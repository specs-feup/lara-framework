package org.lara.weavergen2.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.lara.langspec2.dsl.WeaverSpec;
import org.lara.weavergen2.fixtures.BaselineRegen;
import org.lara.weavergen2.fixtures.DiffUtils;
import org.lara.weavergen2.fixtures.GeneratedTreeUtils;
import org.lara.weavergen2.fixtures.WeaverGen2TestHarness;
import org.lara.weavergen2.fixtures.specs.base.BaseSpec;
import org.lara.weavergen2.fixtures.specs.valid.EdgeSpec;
import org.lara.weavergen2.fixtures.specs.valid.MinimalSpec;
import org.lara.weavergen2.fixtures.specs.valid.MediumSpec;
import org.lara.weavergen2.fixtures.specs.valid.ThisTypeSpec;

class WeaverGen2GenerationTest {

    private static final Path GOLDEN_ROOT = Path.of("test-resources/golden");

    @TempDir
    Path temp;

    private final Set<String> regeneratedScenarios = new TreeSet<>();

    @Test
    @DisplayName("minimal matches golden and is deterministic")
    void minimalGeneratesAndCompiles() throws Exception {
        assertScenario("minimal", MinimalSpec.class);
    }

    @Test
    @DisplayName("medium matches golden and is deterministic")
    void mediumGeneratesAndCompiles() throws Exception {
        assertScenario("medium", MediumSpec.class);
    }

    @Test
    @DisplayName("edge matches golden and is deterministic")
    void edgeGeneratesAndCompiles() throws Exception {
        assertScenario("edge", EdgeSpec.class);
    }

    @Test
    @DisplayName("thistype matches golden and is deterministic")
    void thisTypeGeneratesAndCompiles() throws Exception {
        assertScenario("thistype", ThisTypeSpec.class);
    }

    private void assertScenario(String scenarioName, Class<? extends WeaverSpec> specClass)
            throws Exception {
        var scenario = WeaverGen2TestHarness.scenario(BaseSpec.class, specClass);
        var outputDir = temp.resolve(specClass.getSimpleName());

        WeaverGen2TestHarness.assertSpecCanBuild(scenario);
        var firstRun = WeaverGen2TestHarness.run(scenario, outputDir);

        assertThat(firstRun.error()).isNull();
        assertThat(Files.exists(outputDir)).isTrue();
        assertOutputMatchesGolden(scenarioName, outputDir);

        var secondRun = WeaverGen2TestHarness.run(scenario, outputDir);
        assertThat(secondRun.error()).isNull();
        assertOutputMatchesGolden(scenarioName, outputDir);
    }

    private void assertOutputMatchesGolden(String scenarioName, Path outputDir) throws Exception {
        var generatedFiles = GeneratedTreeUtils.snapshotFiles(outputDir).entrySet().stream()
                .filter(entry -> entry.getKey().endsWith(".java"))
                .collect(java.util.stream.Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (left, right) -> {
                            throw new IllegalStateException("Duplicate generated path: " + left);
                        },
                        java.util.TreeMap::new));
        var goldenRoot = GOLDEN_ROOT.resolve(scenarioName);

        BaselineRegen.runOrVerify(
                () -> persistGoldenOnce(goldenRoot, scenarioName, generatedFiles),
                () -> verifyGolden(scenarioName, generatedFiles, goldenRoot));
    }

    private void persistGoldenOnce(Path goldenRoot, String scenarioName, Map<String, Path> generatedFiles)
            throws IOException {
        if (regeneratedScenarios.add(scenarioName)) {
            persistGolden(goldenRoot, scenarioName, generatedFiles);
        }
    }

    private static void verifyGolden(String scenarioName, Map<String, Path> generatedFiles, Path goldenRoot)
            throws IOException {
        var goldenFiles = snapshotGolden(goldenRoot, scenarioName);

        assertThat(generatedFiles.keySet())
                .as("Generated file set for scenario '%s'", scenarioName)
                .containsExactlyElementsOf(goldenFiles.keySet());

        for (var entry : goldenFiles.entrySet()) {
            var relative = entry.getKey();
            var generatedFile = generatedFiles.get(relative);
            assertThat(generatedFile).as("Generated file exists: " + relative).isNotNull();

            var generated = GeneratedTreeUtils.readNormalized(generatedFile);
            var golden = GeneratedTreeUtils.readNormalized(entry.getValue());
            DiffUtils.assertEqualsNormalized(golden, generated);
        }
    }

    private static void persistGolden(Path goldenRoot, String scenarioName, Map<String, Path> generatedFiles)
            throws IOException {
        GeneratedTreeUtils.deleteTree(goldenRoot);
        Files.createDirectories(goldenRoot);

        for (var entry : generatedFiles.entrySet()) {
            var relative = toGoldenRelativePath(entry.getKey(), scenarioName);
            var target = goldenRoot.resolve(relative.replaceFirst("\\.java$", ".java.txt"));
            GeneratedTreeUtils.writeNormalized(target, GeneratedTreeUtils.readNormalized(entry.getValue()));
        }
    }

    private static String toGoldenRelativePath(String generatedRelativePath, String scenarioName) {
        String prefix = scenarioName + "/";
        if (!generatedRelativePath.startsWith(prefix)) {
            throw new IllegalStateException(
                    "Generated file '" + generatedRelativePath + "' is not inside scenario '" + scenarioName + "'");
        }

        return generatedRelativePath.substring(prefix.length());
    }

    private static Map<String, Path> snapshotGolden(Path goldenRoot, String scenarioName) throws IOException {
        return GeneratedTreeUtils.listFiles(goldenRoot, Files::isRegularFile).stream()
                .filter(path -> path.getFileName().toString().endsWith(".txt"))
                .collect(java.util.stream.Collectors.toMap(
                        path -> scenarioName + "/"
                                + GeneratedTreeUtils.normalizeRelativePath(goldenRoot.relativize(path).toString())
                                        .replaceFirst("\\.txt$", ""),
                        java.util.function.Function.identity(),
                        (left, right) -> {
                            throw new IllegalStateException("Duplicate golden path: " + left);
                        },
                        java.util.TreeMap::new));
    }
}
