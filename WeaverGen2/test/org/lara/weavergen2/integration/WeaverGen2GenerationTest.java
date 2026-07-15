package org.lara.weavergen2.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        assertOutputMatchesGolden(scenarioName, firstRun);

        var secondRun = WeaverGen2TestHarness.run(scenario, outputDir);
        assertThat(secondRun.error()).isNull();
        assertOutputMatchesGolden(scenarioName, secondRun);
    }

    private void assertOutputMatchesGolden(String scenarioName, WeaverGen2TestHarness.RunResult run) throws Exception {
        var generatedFiles = javaSnapshot(run.outputDir());
        javaSnapshot(run.projectRoot()).forEach((relative, path) -> {
            if (relative.startsWith("src/")) {
                return;
            }

            var previous = generatedFiles.putIfAbsent(relative, path);
            if (previous != null) {
                throw new IllegalStateException("Duplicate generated path: " + relative);
            }
        });
        var goldenRoot = GOLDEN_ROOT.resolve(scenarioName);

        BaselineRegen.runOrVerify(
                () -> persistGoldenOnce(scenarioName, goldenRoot, generatedFiles),
                () -> verifyGolden(scenarioName, goldenRoot, generatedFiles));
    }

    private static Map<String, Path> javaSnapshot(Path root) throws IOException {
        return GeneratedTreeUtils.snapshotFiles(root).entrySet().stream()
                .filter(entry -> entry.getKey().endsWith(".java"))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (left, right) -> {
                            throw new IllegalStateException("Duplicate generated path: " + left);
                        },
                        TreeMap::new));
    }

    private void persistGoldenOnce(String scenarioName, Path goldenRoot, Map<String, Path> generatedFiles)
            throws IOException {
        if (regeneratedScenarios.add(scenarioName)) {
            persistGolden(scenarioName, goldenRoot, generatedFiles);
        }
    }

    private static void verifyGolden(String scenarioName, Path goldenRoot, Map<String, Path> generatedFiles)
            throws IOException {
        var goldenFiles = snapshotGolden(scenarioName, goldenRoot);

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

    private static void persistGolden(String scenarioName, Path goldenRoot, Map<String, Path> generatedFiles)
            throws IOException {
        GeneratedTreeUtils.deleteTree(goldenRoot);
        Files.createDirectories(goldenRoot);

        for (var entry : generatedFiles.entrySet()) {
            var relative = toGoldenRelativePath(scenarioName, entry.getKey());
            var target = goldenRoot.resolve(relative.replaceFirst("\\.java$", ".java.txt"));
            GeneratedTreeUtils.writeNormalized(target, GeneratedTreeUtils.readNormalized(entry.getValue()));
        }
    }

    private static String toGoldenRelativePath(String scenarioName, String generatedRelativePath) {
        String prefix = scenarioName + "/";
        if (!generatedRelativePath.startsWith(prefix)) {
            throw new IllegalStateException(
                    "Generated file '" + generatedRelativePath + "' is not inside scenario '" + scenarioName + "'");
        }

        return generatedRelativePath.substring(prefix.length());
    }

    private static Map<String, Path> snapshotGolden(String scenarioName, Path goldenRoot) throws IOException {
        return GeneratedTreeUtils.listFiles(goldenRoot, Files::isRegularFile).stream()
                .filter(path -> path.getFileName().toString().endsWith(".txt"))
                .collect(Collectors.toMap(
                        path -> scenarioName + "/"
                                + GeneratedTreeUtils.normalizeRelativePath(goldenRoot.relativize(path).toString())
                                        .replaceFirst("\\.txt$", ""),
                        Function.identity(),
                        (left, right) -> {
                            throw new IllegalStateException("Duplicate golden path: " + left);
                        },
                        TreeMap::new));
    }
}
