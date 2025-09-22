package org.lara.interpreter.weaver.generator.codegen;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.lara.interpreter.weaver.generator.commandline.WeaverGenerator;
import org.lara.interpreter.weaver.generator.fixtures.DiffUtils;

/**
 * Golden tests for Java code generation.
 *
 */
public class JavaCodegenGoldenTest {

    @TempDir
    Path temp;

    private Path projectRoot;

    @BeforeEach
    void setupRoot() throws IOException {
        // Find WeaverGenerator module root by traversing upwards until we find
        // build.gradle
        projectRoot = Path.of(".");
        // heuristic: ensure we are inside WeaverGenerator
        assertThat(projectRoot.resolve("build.gradle")).exists();
    }

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

    private void runAndAssertGolden(String scenario) throws Exception {
        Path specDir = projectRoot.resolve("test-resources/spec/valid/" + scenario);
        Path outDir = temp.resolve("gen-" + scenario);

        String weaverName = capitalize(scenario) + "Weaver";
        String pkg = scenario + ".pkg";

        String[] args = new String[] {
                "-x", specDir.toString(),
                "-o", outDir.toString(),
                "-p", pkg,
                "-w", weaverName
        };

        WeaverGenerator.main(args);

        // Determinism: run again into same folder should not change contents.
        List<String> before = snapshot(outDir);
        WeaverGenerator.main(args);
        List<String> after = snapshot(outDir);
        assertThat(after).as("Idempotent generation (file listing)").containsExactlyElementsOf(before);

        Path goldenRoot = projectRoot.resolve("test-resources/golden/" + scenario);
        Map<String, Path> generatedFiles = snapshotFiles(outDir);
        Map<String, Path> goldenFiles = snapshotGolden(goldenRoot, scenario);

        assertThat(generatedFiles.keySet())
                .as("Generated file set for scenario '%s'", scenario)
                .containsExactlyElementsOf(goldenFiles.keySet());

        for (Map.Entry<String, Path> entry : goldenFiles.entrySet()) {
            String relative = entry.getKey();
            Path generatedFile = generatedFiles.get(relative);
            assertThat(generatedFile).as("Generated file exists: " + relative).isNotNull();

            String gen = read(generatedFile);
            String gold = read(entry.getValue());

            DiffUtils.assertEqualsNormalized(gold, gen);
        }
    }

    private static List<String> snapshot(Path dir) throws IOException {
        try (Stream<Path> walk = Files.walk(dir)) {
            return walk.filter(Files::isRegularFile)
                    .map(dir::relativize)
                    .map(Path::toString)
                    .sorted()
                    .collect(Collectors.toList());
        }
    }

    private static String read(Path file) throws IOException {
        return Files.readString(file, StandardCharsets.UTF_8).replace("\r\n", "\n").replace('\r', '\n');
    }

    private static String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private static Map<String, Path> snapshotFiles(Path root) throws IOException {
        try (Stream<Path> walk = Files.walk(root)) {
            return walk.filter(Files::isRegularFile)
                    .collect(Collectors.toMap(
                            path -> normalize(root.relativize(path).toString()),
                            Function.identity(),
                            (a, b) -> {
                                throw new IllegalStateException("Duplicate generated path: " + a);
                            },
                            TreeMap::new));
        }
    }

    private static Map<String, Path> snapshotGolden(Path goldenRoot, String scenario) throws IOException {
        try (Stream<Path> walk = Files.walk(goldenRoot)) {
            return walk.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".java.txt"))
                    .collect(Collectors.toMap(
                            path -> scenario + "/" + normalize(goldenRoot.relativize(path).toString())
                                    .replaceFirst("\\.java\\.txt$", ".java"),
                            Function.identity(),
                            (a, b) -> {
                                throw new IllegalStateException("Duplicate golden path: " + a);
                            },
                            TreeMap::new));
        }
    }

    private static String normalize(String relativePath) {
        return relativePath.replace('\\', '/');
    }
}
