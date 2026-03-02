package org.lara.interpreter.weaver.generator.codegen;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private static final Pattern METHOD_SIGNATURE_PATTERN = Pattern.compile(
            "(?m)^\\s*(?:public|protected|private)\\s+(?:final\\s+|static\\s+|abstract\\s+|synchronized\\s+|native\\s+)*[^\\s(]+(?:\\s*<[^>]+>)?(?:\\s*\\[\\])*(?:\\s+[^\\s(]+(?:\\s*<[^>]+>)?(?:\\s*\\[\\])*)*\\s+(\\w+)\\s*\\(([^)]*)\\)\\s*(?:throws\\s+[^\\{]+)?\\{");

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

    private void runAndAssertGolden(String scenario) throws Exception {
        Path specDir = Path.of("test-resources/spec/valid/" + scenario);
        Path outDir = temp.resolve("gen-" + scenario);

        String weaverName = capitalize(scenario) + "Weaver";
        String pkg = scenario + ".pkg";

        String[] args = new String[] {
                "-x", specDir.toString(),
                "-o", outDir.toString(),
                "-p", pkg,
                "-w", weaverName
        };

        int exitCode = WeaverGenerator.run(args);
        assertThat(exitCode).as("WeaverGenerator should succeed").isZero();

        // Determinism: run again into same folder should not change contents.
        List<String> before = snapshot(outDir);
        int exitCode2 = WeaverGenerator.run(args);
        assertThat(exitCode2).as("WeaverGenerator should succeed (idempotency check)").isZero();
        List<String> after = snapshot(outDir);
        assertThat(after).as("Idempotent generation (file listing)").containsExactlyElementsOf(before);

        Path goldenRoot = Path.of("test-resources/golden/" + scenario);
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

        assertNoMethodSignatureCollisions(outDir, scenario);
    }

    private static void assertNoMethodSignatureCollisions(Path outDir, String scenario) throws IOException {
        Map<String, Integer> signatureCounts = new HashMap<>();

        try (Stream<Path> walk = Files.walk(outDir)) {
            List<Path> javaFiles = walk.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".java"))
                    .collect(Collectors.toList());

            for (Path javaFile : javaFiles) {
                String source = read(javaFile);
                Matcher matcher = METHOD_SIGNATURE_PATTERN.matcher(source);
                while (matcher.find()) {
                    String methodName = matcher.group(1);
                    String normalizedParams = normalizeParameterTypes(matcher.group(2));
                    String signature = normalize(outDir.relativize(javaFile).toString()) + "::" + methodName + "("
                            + normalizedParams + ")";
                    signatureCounts.put(signature, signatureCounts.getOrDefault(signature, 0) + 1);
                }
            }
        }

        List<String> collisions = signatureCounts.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(entry -> entry.getKey() + " x" + entry.getValue())
                .sorted()
                .collect(Collectors.toList());

        assertThat(collisions)
                .as("Generated Java should not contain duplicate method signatures for scenario '%s'", scenario)
                .isEmpty();
    }

    private static String normalizeParameterTypes(String params) {
        String trimmed = params == null ? "" : params.trim();
        if (trimmed.isEmpty()) {
            return "";
        }

        List<String> parameterChunks = splitParameters(trimmed);
        return parameterChunks.stream()
                .map(JavaCodegenGoldenTest::normalizeSingleParameterType)
                .collect(Collectors.joining(","));
    }

    private static List<String> splitParameters(String params) {
        List<String> chunks = new ArrayList<>();
        int genericDepth = 0;
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < params.length(); i++) {
            char ch = params.charAt(i);
            if (ch == '<') {
                genericDepth++;
            } else if (ch == '>') {
                genericDepth = Math.max(0, genericDepth - 1);
            }

            if (ch == ',' && genericDepth == 0) {
                chunks.add(current.toString().trim());
                current.setLength(0);
                continue;
            }

            current.append(ch);
        }

        String last = current.toString().trim();
        if (!last.isEmpty()) {
            chunks.add(last);
        }

        return chunks;
    }

    private static String normalizeSingleParameterType(String parameter) {
        String normalized = parameter
                .replaceAll("@[A-Za-z_][A-Za-z0-9_$.]*(\\([^)]*\\))?\\s*", "")
                .replaceAll("\\bfinal\\b\\s*", "")
                .trim();

        int lastSpace = normalized.lastIndexOf(' ');
        if (lastSpace >= 0) {
            normalized = normalized.substring(0, lastSpace).trim();
        }

        return normalized.replaceAll("\\s+", "");
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
