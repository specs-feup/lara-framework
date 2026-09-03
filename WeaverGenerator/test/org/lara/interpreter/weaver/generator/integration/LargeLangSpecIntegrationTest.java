package org.lara.interpreter.weaver.generator.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.io.TempDir;
import org.lara.interpreter.weaver.generator.fixtures.BaselineRegen;
import org.lara.interpreter.weaver.generator.fixtures.GeneratedTreeUtils;
import org.lara.interpreter.weaver.generator.fixtures.WeaverGeneratorTestHarness;
import org.lara.interpreter.weaver.generator.fixtures.WeaverGeneratorTestHarness.RunResult;
import org.lara.interpreter.weaver.generator.fixtures.WeaverGeneratorTestHarness.Scenario;
import org.lara.interpreter.weaver.generator.integration.fixtures.ArtifactManifestEntry;
import org.lara.interpreter.weaver.generator.integration.fixtures.InvariantSnapshot;
import org.lara.interpreter.weaver.generator.integration.fixtures.JavaDeclarationSignature;
import org.lara.interpreter.weaver.generator.integration.fixtures.JavaInvariantUtils;
import org.lara.interpreter.weaver.generator.integration.fixtures.JavaInvariantUtils.JavaSnapshot;
import org.lara.interpreter.weaver.generator.integration.fixtures.JsonInvariantUtils;
import org.lara.interpreter.weaver.generator.integration.fixtures.JsonInvariantUtils.ParsedJson;
import org.lara.interpreter.weaver.generator.integration.fixtures.ManifestUtils;
import org.lara.language.specification.dsl.LanguageSpecification;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LargeLangSpecIntegrationTest {

    private static final String SCENARIO = "large-integration";
    private static final String PACKAGE_NAME = "large.integration.pkg";
    private static final String WEAVER_NAME = "LargeIntegrationWeaver";

    private static final Scenario LARGE_SCENARIO = WeaverGeneratorTestHarness.scenario(
            SCENARIO,
            PACKAGE_NAME,
            WEAVER_NAME,
            true);

    private static final Path EXPECTED_DIR = Path.of("test-resources/integration/" + SCENARIO);

    private static final Path EXPECTED_JAVA_MANIFEST = EXPECTED_DIR.resolve("java-manifest.tsv");
    private static final Path EXPECTED_JSON_MANIFEST = EXPECTED_DIR.resolve("json-manifest.tsv");
    private static final Path EXPECTED_AGGREGATE_HASH = EXPECTED_DIR.resolve("aggregate-hash.txt");
    private static final Path EXPECTED_JSON_CANONICAL_HASH = EXPECTED_DIR.resolve("json-canonical-hash.txt");
    private static final Path EXPECTED_JAVA_API_HASH = EXPECTED_DIR.resolve("java-api-hash.txt");
    private static final Path EXPECTED_INVARIANTS = EXPECTED_DIR.resolve("invariants.json");
    private static final Path EXPECTED_JAVA_SIGNATURES = EXPECTED_DIR.resolve("java-signatures.txt");

    @TempDir
    Path temp;

    private final AtomicInteger runCounter = new AtomicInteger();

    @Test
    @DisplayName("largeIntegration_generation_succeeds")
    void largeIntegration_generation_succeeds() throws Exception {
        RuntimeSnapshot runtime = ensureSnapshot();

        assertThat(runtime.exitCode)
                .as("WeaverGenerator exit code")
                .isZero();

        assertThat(runtime.javaManifest)
                .as("Generated Java manifest should not be empty")
                .isNotEmpty();

        assertThat(runtime.jsonManifest)
                .as("Generated JSON manifest should contain exactly one JSON file")
                .hasSize(1);
    }

    @Test
    @DisplayName("largeIntegration_manifests_match_expected")
    void largeIntegration_manifests_match_expected() throws Exception {
        RuntimeSnapshot runtime = ensureSnapshot();

        if (isRegenMode()) {
            persistExpectedArtifacts(runtime);
            return;
        }

        assertExpectedFilesExist();

        List<ArtifactManifestEntry> expectedJava = ManifestUtils.readManifest(EXPECTED_JAVA_MANIFEST);
        List<ArtifactManifestEntry> expectedJson = ManifestUtils.readManifest(EXPECTED_JSON_MANIFEST);

        ManifestUtils.assertManifestEquals("java-manifest", expectedJava, runtime.javaManifest);
        ManifestUtils.assertManifestEquals("json-manifest", expectedJson, runtime.jsonManifest);

        String expectedAggregateHash = readTrimmed(EXPECTED_AGGREGATE_HASH);
        assertThat(runtime.aggregateHash)
                .as("Aggregate artifact hash")
                .isEqualTo(expectedAggregateHash);
    }

    @Test
    @DisplayName("largeIntegration_json_is_valid")
    void largeIntegration_json_is_valid() throws Exception {
        RuntimeSnapshot runtime = ensureSnapshot();

        assertThat(runtime.parsedJson.root())
                .as("JSON root")
                .isInstanceOf(java.util.Map.class);

        String canonical = JsonInvariantUtils.toCanonicalJson(runtime.parsedJson.root());
        assertThat(canonical)
                .as("Canonical JSON should start with object marker")
                .startsWith("{");
    }

    @Test
    @DisplayName("largeIntegration_json_invariants_match")
    void largeIntegration_json_invariants_match() throws Exception {
        RuntimeSnapshot runtime = ensureSnapshot();

        if (isRegenMode()) {
            persistExpectedArtifacts(runtime);
            return;
        }

        assertExpectedFilesExist();

        InvariantSnapshot expected = JsonInvariantUtils.readInvariantSnapshot(EXPECTED_INVARIANTS);
        JsonInvariantUtils.assertSnapshotsEqual(expected, runtime.invariantSnapshot);
    }

    @Test
    @DisplayName("largeIntegration_json_canonical_hash_matches")
    void largeIntegration_json_canonical_hash_matches() throws Exception {
        RuntimeSnapshot runtime = ensureSnapshot();

        if (isRegenMode()) {
            persistExpectedArtifacts(runtime);
            return;
        }

        assertExpectedFilesExist();

        String expectedJsonHash = readTrimmed(EXPECTED_JSON_CANONICAL_HASH);
        assertThat(runtime.jsonCanonicalHash)
                .as("Canonical JSON hash")
                .isEqualTo(expectedJsonHash);
    }

    @Test
    @DisplayName("largeIntegration_java_structure_invariants_match")
    void largeIntegration_java_structure_invariants_match() throws Exception {
        RuntimeSnapshot runtime = ensureSnapshot();

        if (isRegenMode()) {
            persistExpectedArtifacts(runtime);
            return;
        }

        assertExpectedFilesExist();

        InvariantSnapshot expected = JsonInvariantUtils.readInvariantSnapshot(EXPECTED_INVARIANTS);
        assertThat(runtime.invariantSnapshot.java())
                .as("Java structure invariants")
                .containsExactlyInAnyOrderEntriesOf(expected.java());
    }

    @Test
    @DisplayName("largeIntegration_java_api_hash_matches_all_declarations")
    void largeIntegration_java_api_hash_matches_all_declarations() throws Exception {
        RuntimeSnapshot runtime = ensureSnapshot();

        if (isRegenMode()) {
            persistExpectedArtifacts(runtime);
            return;
        }

        assertExpectedFilesExist();

        String expectedApiHash = readTrimmed(EXPECTED_JAVA_API_HASH);
        assertThat(runtime.javaSnapshot.apiHash())
                .as("Java API hash")
                .isEqualTo(expectedApiHash);

        List<JavaDeclarationSignature> expectedSignatures = JavaInvariantUtils.readSignatures(EXPECTED_JAVA_SIGNATURES);
        JavaInvariantUtils.assertApiSignaturesEqual(expectedSignatures, runtime.javaSnapshot.signatures());
    }

    @Test
    @DisplayName("largeIntegration_generated_java_compiles")
    void largeIntegration_generated_java_compiles() throws Exception {
        RuntimeSnapshot runtime = ensureSnapshot();

        List<Path> javaFiles = GeneratedTreeUtils.listFiles(runtime.outputDir, path -> path.toString().endsWith(".java"));
        assertThat(javaFiles)
                .as("Generated Java files")
                .isNotEmpty();

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        assertThat(compiler)
                .as("JDK compiler available")
                .isNotNull();

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        Path classesDir = Files.createDirectories(temp.resolve("classes"));

        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null)) {
            var units = fileManager.getJavaFileObjectsFromFiles(javaFiles.stream().map(Path::toFile).toList());
            String classpath = System.getProperty("java.class.path");

            List<String> options = List.of("-d", classesDir.toString(), "-classpath", classpath);
            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, options, null, units);
            boolean success = task.call();

            if (!success) {
                String byFile = diagnostics.getDiagnostics().stream()
                        .collect(Collectors.groupingBy(
                                diagnostic -> String.valueOf(diagnostic.getSource()),
                                Collectors.mapping(Object::toString, Collectors.joining("\n"))))
                        .entrySet().stream()
                        .sorted(java.util.Map.Entry.comparingByKey())
                        .map(entry -> "== " + entry.getKey() + " ==\n" + entry.getValue())
                        .collect(Collectors.joining("\n\n"));

                fail("Generated Java compilation failed:\n" + byFile);
            }

            try (URLClassLoader classLoader = new URLClassLoader(new URL[] { classesDir.toUri().toURL() })) {
                Class<?> weaverClass = classLoader.loadClass(
                        LARGE_SCENARIO.packageName() + "." + LARGE_SCENARIO.weaverName());
                assertThat(weaverClass).isNotNull();
            }
        }
    }

    @Test
    @DisplayName("largeIntegration_generation_is_deterministic")
    void largeIntegration_generation_is_deterministic() throws Exception {
        RuntimeSnapshot first = ensureSnapshot();
        RuntimeSnapshot second = runPipeline(temp.resolve("output-second"));

        ManifestUtils.assertManifestEquals("java-manifest deterministic run", first.javaManifest, second.javaManifest);
        ManifestUtils.assertManifestEquals("json-manifest deterministic run", first.jsonManifest, second.jsonManifest);

        assertThat(second.aggregateHash)
                .as("Aggregate hash across deterministic runs")
                .isEqualTo(first.aggregateHash);

        assertThat(second.jsonCanonicalHash)
                .as("Canonical JSON hash across deterministic runs")
                .isEqualTo(first.jsonCanonicalHash);

        assertThat(second.javaSnapshot.apiHash())
                .as("Java API hash across deterministic runs")
                .isEqualTo(first.javaSnapshot.apiHash());
    }

    private RuntimeSnapshot ensureSnapshot() throws Exception {
        WeaverGeneratorTestHarness.assertSpecDirExists(LARGE_SCENARIO);
        return runPipeline(temp.resolve("output-" + runCounter.incrementAndGet()));
    }

    private RuntimeSnapshot runPipeline(Path outputDir) throws Exception {
        RunResult result = WeaverGeneratorTestHarness.run(LARGE_SCENARIO, outputDir);
        int exitCode = result.exitCode();
        if (exitCode != 0) {
            String parseDiagnostics = diagnoseLanguageSpecification(LARGE_SCENARIO.specDir());
            fail("WeaverGenerator failed for scenario '" + SCENARIO + "' with exit code " + exitCode
                    + ". LanguageSpecification diagnostics:\n" + parseDiagnostics);
        }

        assertThat(exitCode)
                .as("WeaverGenerator should succeed")
                .isZero();

        Predicate<Path> javaFilter = path -> path.getFileName().toString().endsWith(".java");
        Predicate<Path> jsonFilter = path -> path.getFileName().toString().endsWith(".json");

        List<ArtifactManifestEntry> javaManifest = ManifestUtils.computeManifest(outputDir, javaFilter);
        List<ArtifactManifestEntry> jsonManifest = ManifestUtils.computeManifest(outputDir, jsonFilter);

        assertThat(jsonManifest)
                .as("Expected single generated JSON file")
                .hasSize(1);

        List<ArtifactManifestEntry> allEntries = Stream.concat(javaManifest.stream(), jsonManifest.stream())
                .sorted(java.util.Comparator.comparing(ArtifactManifestEntry::path))
                .toList();
        String aggregateHash = ManifestUtils.computeAggregateHash(allEntries);

        Path jsonPath = outputDir.resolve(jsonManifest.get(0).path());
        ParsedJson parsedJson = JsonInvariantUtils.parseJson(jsonPath);
        String jsonCanonicalHash = JsonInvariantUtils.canonicalHash(parsedJson.root());

        JavaSnapshot javaSnapshot = JavaInvariantUtils.computeSnapshot(
                outputDir,
                LARGE_SCENARIO.packageName(),
                LARGE_SCENARIO.weaverName());
        InvariantSnapshot invariantSnapshot = JsonInvariantUtils.computeSnapshot(parsedJson, javaSnapshot.structure());

        return new RuntimeSnapshot(exitCode, outputDir, javaManifest, jsonManifest, aggregateHash, parsedJson,
                jsonCanonicalHash, javaSnapshot, invariantSnapshot);
    }

    private void persistExpectedArtifacts(RuntimeSnapshot runtime) throws IOException {
        ManifestUtils.writeManifest(EXPECTED_JAVA_MANIFEST, runtime.javaManifest);
        ManifestUtils.writeManifest(EXPECTED_JSON_MANIFEST, runtime.jsonManifest);

        Files.createDirectories(EXPECTED_DIR);
        Files.writeString(EXPECTED_AGGREGATE_HASH, runtime.aggregateHash + "\n", StandardCharsets.UTF_8);
        Files.writeString(EXPECTED_JSON_CANONICAL_HASH, runtime.jsonCanonicalHash + "\n", StandardCharsets.UTF_8);
        Files.writeString(EXPECTED_JAVA_API_HASH, runtime.javaSnapshot.apiHash() + "\n", StandardCharsets.UTF_8);

        JsonInvariantUtils.writeInvariantSnapshot(EXPECTED_INVARIANTS, runtime.invariantSnapshot);
        JavaInvariantUtils.writeSignatures(EXPECTED_JAVA_SIGNATURES, runtime.javaSnapshot.signatures());
    }

    private static void assertExpectedFilesExist() {
        List<Path> expectedFiles = List.of(
                EXPECTED_JAVA_MANIFEST,
                EXPECTED_JSON_MANIFEST,
                EXPECTED_AGGREGATE_HASH,
                EXPECTED_JSON_CANONICAL_HASH,
                EXPECTED_JAVA_API_HASH,
                EXPECTED_INVARIANTS,
                EXPECTED_JAVA_SIGNATURES);

        List<Path> missing = expectedFiles.stream().filter(path -> !Files.isRegularFile(path)).toList();

        assertThat(missing)
                .as("Missing expected baseline files. Run 'gradle regenLargeIntegrationBaselines' to regenerate")
                .isEmpty();
    }

    private static boolean isRegenMode() {
        return BaselineRegen.isEnabled();
    }

    private static String readTrimmed(Path file) throws IOException {
        return Files.readString(file, StandardCharsets.UTF_8).trim();
    }

    private static String diagnoseLanguageSpecification(Path specDir) {
        try {
            LanguageSpecification.newInstance(specDir.toFile());
            return "LanguageSpecification parsed successfully (unexpected: generation still failed)";
        } catch (Throwable throwable) {
            StringBuilder builder = new StringBuilder();
            Throwable current = throwable;
            int depth = 0;
            while (current != null && depth < 16) {
                builder.append("Cause[").append(depth).append("] ")
                        .append(current.getClass().getName())
                        .append(": ")
                        .append(current.getMessage())
                        .append('\n');
                current = current.getCause();
                depth++;
            }
            return builder.toString();
        }
    }

    private record RuntimeSnapshot(int exitCode, Path outputDir, List<ArtifactManifestEntry> javaManifest,
            List<ArtifactManifestEntry> jsonManifest, String aggregateHash, ParsedJson parsedJson,
            String jsonCanonicalHash, JavaSnapshot javaSnapshot, InvariantSnapshot invariantSnapshot) {
    }
}
