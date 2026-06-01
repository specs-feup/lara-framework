package org.lara.weavergen2.integration;

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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.io.TempDir;
import org.lara.weavergen2.WeaverGen2;
import org.lara.weavergen2.fixtures.BaselineRegen;
import org.lara.weavergen2.fixtures.specs.base.BaseSpec;
import org.lara.weavergen2.fixtures.specs.integration.LargeIntegration;
import org.lara.weavergen2.integration.fixtures.ArtifactManifestEntry;
import org.lara.weavergen2.integration.fixtures.GeneratedTreeUtils;
import org.lara.weavergen2.integration.fixtures.InvariantSnapshot;
import org.lara.weavergen2.integration.fixtures.JavaDeclarationSignature;
import org.lara.weavergen2.integration.fixtures.JavaInvariantUtils;
import org.lara.weavergen2.integration.fixtures.JavaMethodSignatureUtils;
import org.lara.weavergen2.integration.fixtures.JsonInvariantUtils;
import org.lara.weavergen2.integration.fixtures.JsonInvariantUtils.ParsedJson;
import org.lara.weavergen2.integration.fixtures.ManifestUtils;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LargeLangSpecIntegrationTest {

    private static final String SCENARIO = "large-integration";
    private static final String PACKAGE_NAME = "large.integration.pkg";
    private static final String WEAVER_NAME = "LargeIntegrationWeaver";

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
    private RuntimeSnapshot cachedSnapshot;
    private Path cachedTempRoot;

    @Test
    @DisplayName("largeIntegration_generation_succeeds")
    void largeIntegration_generation_succeeds() throws Exception {
        RuntimeSnapshot runtime = snapshot();

        assertThat(runtime.exitCode).isZero();
        assertThat(runtime.javaManifest).isNotEmpty();
        assertThat(runtime.jsonManifest).hasSize(1);
    }

    @Test
    @DisplayName("largeIntegration_manifests_match_expected")
    void largeIntegration_manifests_match_expected() throws Exception {
        RuntimeSnapshot runtime = snapshot();

        BaselineRegen.runOrVerify(
                () -> {
                },
                () -> {
                    assertExpectedFilesExist();

                    List<ArtifactManifestEntry> expectedJava = ManifestUtils.readManifest(EXPECTED_JAVA_MANIFEST);
                    List<ArtifactManifestEntry> expectedJson = ManifestUtils.readManifest(EXPECTED_JSON_MANIFEST);

                    ManifestUtils.assertManifestEquals("java-manifest", expectedJava, runtime.javaManifest);
                    ManifestUtils.assertManifestEquals("json-manifest", expectedJson, runtime.jsonManifest);

                    String expectedAggregateHash = readTrimmed(EXPECTED_AGGREGATE_HASH);
                    assertThat(runtime.aggregateHash).isEqualTo(expectedAggregateHash);
                });
    }

    @Test
    @DisplayName("largeIntegration_json_is_valid")
    void largeIntegration_json_is_valid() throws Exception {
        RuntimeSnapshot runtime = snapshot();
        assertThat(runtime.parsedJson.root()).isInstanceOf(java.util.Map.class);

        String canonical = JsonInvariantUtils.toCanonicalJson(runtime.parsedJson.root());
        assertThat(canonical).startsWith("{");
    }

    @Test
    @DisplayName("largeIntegration_json_invariants_match")
    void largeIntegration_json_invariants_match() throws Exception {
        RuntimeSnapshot runtime = snapshot();

        BaselineRegen.runOrVerify(
                () -> {
                },
                () -> {
                    assertExpectedFilesExist();

                    InvariantSnapshot expected = JsonInvariantUtils.readInvariantSnapshot(EXPECTED_INVARIANTS);
                    JsonInvariantUtils.assertSnapshotsEqual(expected, runtime.invariantSnapshot);
                });
    }

    @Test
    @DisplayName("largeIntegration_json_canonical_hash_matches")
    void largeIntegration_json_canonical_hash_matches() throws Exception {
        RuntimeSnapshot runtime = snapshot();

        BaselineRegen.runOrVerify(
                () -> {
                },
                () -> {
                    assertExpectedFilesExist();

                    String expectedJsonHash = readTrimmed(EXPECTED_JSON_CANONICAL_HASH);
                    assertThat(runtime.jsonCanonicalHash).isEqualTo(expectedJsonHash);
                });
    }

    @Test
    @DisplayName("largeIntegration_java_api_hash_matches_all_declarations")
    void largeIntegration_java_api_hash_matches_all_declarations() throws Exception {
        RuntimeSnapshot runtime = snapshot();

        BaselineRegen.runOrVerify(
                () -> {
                },
                () -> {
                    assertExpectedFilesExist();

                    String expectedApiHash = readTrimmed(EXPECTED_JAVA_API_HASH);
                    assertThat(runtime.javaSnapshot.apiHash()).isEqualTo(expectedApiHash);

                    List<JavaDeclarationSignature> expectedSignatures = JavaInvariantUtils
                            .readSignatures(EXPECTED_JAVA_SIGNATURES);
                    JavaInvariantUtils.assertApiSignaturesEqual(expectedSignatures, runtime.javaSnapshot.signatures());
                });
    }

    @Test
    @DisplayName("largeIntegration_generated_java_compiles")
    void largeIntegration_generated_java_compiles() throws Exception {
        RuntimeSnapshot runtime = snapshot();

        BaselineRegen.runOrVerify(
                () -> {
                },
                () -> assertGeneratedJavaCompiles(runtime));
    }

    @Test
    @DisplayName("largeIntegration_generation_is_deterministic")
    void largeIntegration_generation_is_deterministic() throws Exception {
        RuntimeSnapshot first = snapshot();
        RuntimeSnapshot second = runPipeline(temp.resolve("output-second"));

        BaselineRegen.runOrVerify(
                () -> {
                },
                () -> {
                    ManifestUtils.assertManifestEquals("java-manifest deterministic run", first.javaManifest,
                            second.javaManifest);
                    ManifestUtils.assertManifestEquals("json-manifest deterministic run", first.jsonManifest,
                            second.jsonManifest);

                    assertThat(second.aggregateHash).isEqualTo(first.aggregateHash);
                    assertThat(second.jsonCanonicalHash).isEqualTo(first.jsonCanonicalHash);
                    assertThat(second.javaSnapshot.apiHash()).isEqualTo(first.javaSnapshot.apiHash());
                });
    }

    private RuntimeSnapshot snapshot() throws Exception {
        if (cachedSnapshot == null) {
            cachedSnapshot = createSnapshot();
            BaselineRegen.ifEnabled(() -> persistExpectedArtifacts(cachedSnapshot));
        }

        return cachedSnapshot;
    }

    private RuntimeSnapshot createSnapshot() throws Exception {
        if (cachedTempRoot == null) {
            cachedTempRoot = Files.createTempDirectory("large-integration-cache-");
        }

        Path projectRoot = Files.createTempDirectory(cachedTempRoot, "large-integration-project-");
        writeConcreteWeaverStub(projectRoot);

        WeaverGen2 generator = WeaverGen2.fromSpecs(BaseSpec.class, LargeIntegration.class, "java.lang.Object",
                projectRoot);
        return runPipeline(cachedTempRoot.resolve("output-" + runCounter.incrementAndGet()), generator, projectRoot);
    }

    private void assertGeneratedJavaCompiles(RuntimeSnapshot runtime) throws Exception {
        List<Path> javaFiles = GeneratedTreeUtils.listFiles(runtime.outputDir, path -> path.toString().endsWith(".java"));
        assertThat(javaFiles).isNotEmpty();

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        assertThat(compiler).isNotNull();

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        Path classesDir = Files.createDirectories(temp.resolve("classes"));

        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null)) {
            var units = fileManager.getJavaFileObjectsFromFiles(javaFiles.stream().map(Path::toFile).toList());
            String classpath = System.getProperty("java.class.path");
            String sourcepath = generatedCompileSourcePath(runtime.projectRoot);

            List<String> options = List.of("-d", classesDir.toString(), "-classpath", classpath, "-sourcepath",
                    sourcepath);
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
                Class<?> weaverClass = classLoader.loadClass(PACKAGE_NAME + "." + WEAVER_NAME);
                assertThat(weaverClass).isNotNull();
            }
        }
    }

    private RuntimeSnapshot runPipeline(Path outputDir) throws Exception {
        Path projectRoot = Files.createTempDirectory(temp, "large-integration-project-");
        writeConcreteWeaverStub(projectRoot);

        WeaverGen2 generator = WeaverGen2.fromSpecs(BaseSpec.class, LargeIntegration.class, "java.lang.Object",
                projectRoot);
        return runPipeline(outputDir, generator, projectRoot);
    }

    private RuntimeSnapshot runPipeline(Path outputDir, WeaverGen2 generator, Path projectRoot) throws Exception {
        Files.createDirectories(outputDir);
        Path jsonPath = outputDir.resolve("spec.json");

        generator.generate(outputDir, jsonPath);

        Predicate<Path> javaFilter = path -> path.getFileName().toString().endsWith(".java");
        Predicate<Path> jsonFilter = path -> path.getFileName().toString().endsWith(".json");

        List<ArtifactManifestEntry> javaManifest = ManifestUtils.computeManifest(outputDir, javaFilter);
        List<ArtifactManifestEntry> jsonManifest = ManifestUtils.computeManifest(outputDir, jsonFilter);
        assertThat(jsonManifest).hasSize(1);

        List<ArtifactManifestEntry> allEntries = Stream.concat(javaManifest.stream(), jsonManifest.stream())
                .sorted(java.util.Comparator.comparing(ArtifactManifestEntry::path))
                .toList();
        String aggregateHash = ManifestUtils.computeAggregateHash(allEntries);

        ParsedJson parsedJson = JsonInvariantUtils.parseJson(outputDir.resolve(jsonManifest.get(0).path()));
        String jsonCanonicalHash = JsonInvariantUtils.canonicalHash(parsedJson.root());
        JavaMethodSignatureUtils.assertNoMethodSignatureCollisions(outputDir, SCENARIO);
        JavaInvariantUtils.JavaSnapshot javaSnapshot = JavaInvariantUtils.computeSnapshot(outputDir, PACKAGE_NAME,
                WEAVER_NAME);
        InvariantSnapshot invariantSnapshot = JsonInvariantUtils.computeSnapshot(parsedJson, javaSnapshot.structure());

        return new RuntimeSnapshot(0, projectRoot, outputDir, javaManifest, jsonManifest, aggregateHash, parsedJson,
                jsonCanonicalHash, javaSnapshot, invariantSnapshot);
    }

    private static String generatedCompileSourcePath(Path projectRoot) {
        return String.join(System.getProperty("path.separator"), List.of(
                projectRoot.toString(),
                sourceRoot("../LaraUtils", "src"),
                sourceRoot("../WeaverInterface", "src"),
                sourceRoot("../WeaverInterface", "src-spec"),
                sourceRoot("../LARAI", "src"),
                sourceRoot("../../specs-java-libs/SpecsUtils", "src"),
                sourceRoot("../../specs-java-libs/jOptions", "src"),
                sourceRoot("../../specs-java-libs/tdrcLibrary", "src")));
    }

    private static String sourceRoot(String relativeProject, String sourceDir) {
        return Path.of(System.getProperty("user.dir")).resolve(relativeProject).resolve(sourceDir).normalize()
                .toString();
    }

    private static void writeConcreteWeaverStub(Path projectRoot) throws IOException {
        Path packageDir = projectRoot.resolve(PACKAGE_NAME.replace('.', '/'));
        Files.createDirectories(packageDir);
        Files.writeString(packageDir.resolve(WEAVER_NAME + ".java"),
                "package " + PACKAGE_NAME + ";\n\n"
                        + "import " + PACKAGE_NAME + ".abstracts.weaver.A" + WEAVER_NAME + ";\n\n"
                        + "public abstract class " + WEAVER_NAME + " extends A" + WEAVER_NAME + " {\n"
                        + "}\n",
                StandardCharsets.UTF_8);
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
                .as("Missing expected baseline files")
                .isEmpty();
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

    @AfterAll
    void deleteCachedSnapshot() throws IOException {
        if (cachedTempRoot != null) {
            GeneratedTreeUtils.deleteTree(cachedTempRoot);
        }
    }

    private static String readTrimmed(Path file) throws IOException {
        return Files.readString(file, StandardCharsets.UTF_8).trim();
    }

    private record RuntimeSnapshot(int exitCode, Path projectRoot, Path outputDir, List<ArtifactManifestEntry> javaManifest,
            List<ArtifactManifestEntry> jsonManifest, String aggregateHash, ParsedJson parsedJson,
            String jsonCanonicalHash, JavaInvariantUtils.JavaSnapshot javaSnapshot,
            InvariantSnapshot invariantSnapshot) {
    }
}
