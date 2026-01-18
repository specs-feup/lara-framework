package org.lara.interpreter.weaver.generator.e2e;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.lara.interpreter.weaver.generator.generator.java.JavaAbstractsGenerator;

/**
 * End-to-end smoke tests focused on minimally valid generation and failure
 * diagnostics.
 */
public class GeneratorE2ETest {

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
    @DisplayName("Minimal spec -> generate -> compile")
    void minimalPipelineCompiles() throws Exception {
        assertScenario(scenario("minimal", "minimal.pkg", "MinimalWeaver", Expectation.COMPILE_SUCCESS));
    }

    @Test
    @DisplayName("Medium spec -> generate -> compile")
    void mediumPipelineCompiles() throws Exception {
        assertScenario(scenario("medium", "medium.pkg", "MediumWeaver", Expectation.COMPILE_SUCCESS));
    }

    @Test
    @DisplayName("Edge spec -> generate -> compile")
    void edgePipelineCompiles() throws Exception {
        assertScenario(scenario("edge", "edge.pkg", "EdgeWeaver", Expectation.COMPILATION_FAILURE)
                .withExpectedMessage("getClass()"));
    }

    private void assertScenario(Scenario scenario) throws Exception {
        PipelineResult result = runPipeline(scenario);

        switch (scenario.expectation) {
            case COMPILE_SUCCESS:
                assertThat(result.generationError).isEmpty();
                assertThat(result.compilationDiagnostics).isEmpty();
                assertThat(result.loadedWeaverClass).as("Generated class for %s", scenario.name).isPresent();
                assertThat(result.loadedWeaverClass.get().getSimpleName()).isEqualTo(scenario.weaverName);
                break;
            case COMPILATION_FAILURE:
                assertThat(result.generationError)
                        .as("Expected generation to succeed for %s", scenario.name)
                        .isEmpty();
                assertThat(result.compilationDiagnostics)
                        .as("Expected compilation to fail for %s", scenario.name)
                        .isPresent();
                scenario.expectedMessage.ifPresent(message -> assertThat(result.compilationDiagnostics.get())
                        .contains(message));
                break;
            case GENERATION_FAILURE:
                assertThat(result.generationError)
                        .as("Expected generation to fail for %s", scenario.name)
                        .isPresent();
                scenario.expectedMessage.ifPresent(message -> assertThat(result.generationError.get().getMessage())
                        .contains(message));
                break;
            default:
                throw new IllegalStateException("Unhandled expectation: " + scenario.expectation);
        }
    }

    private PipelineResult runPipeline(Scenario scenario) throws Exception {
        Path specDir = projectRoot.resolve("test-resources/spec/valid/" + scenario.name);
        Path outDir = temp.resolve("gen-" + scenario.name);

        JavaAbstractsGenerator generator = new JavaAbstractsGenerator(specDir.toFile());
        generator.setWeaverName(scenario.weaverName);
        generator.setOutPackage(scenario.pkg);
        generator.setOutDir(outDir.toFile());

        Optional<Throwable> generationError = runGeneratorPhase(generator::generate);
        if (generationError.isEmpty()) {
            generationError = runGeneratorPhase(generator::print);
        }

        if (generationError.isPresent()) {
            return new PipelineResult(scenario, generationError, Optional.empty(), Optional.empty());
        }

        List<Path> javaFiles = listJava(outDir);
        assertThat(javaFiles).as("Generated Java sources for %s", scenario.name).isNotEmpty();

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        assertThat(compiler).as("JDK compiler available").isNotNull();

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        Path classes = Files.createDirectories(temp.resolve("classes-" + scenario.name));
        String classpath = System.getProperty("java.class.path");
        assertThat(classpath).as("Test classpath available").isNotBlank();

        try (StandardJavaFileManager fm = compiler.getStandardFileManager(diagnostics, null, null)) {
            var units = fm.getJavaFileObjectsFromFiles(javaFiles.stream().map(Path::toFile)
                    .collect(Collectors.toList()));

            List<String> options = List.of("-d", classes.toString(), "-classpath", classpath);
            JavaCompiler.CompilationTask task = compiler.getTask(null, fm, diagnostics, options, null, units);
            boolean success = task.call();
            if (!success) {
                String diag = diagnostics.getDiagnostics().stream().map(Object::toString)
                        .collect(Collectors.joining("\n"));
                return new PipelineResult(scenario, Optional.empty(), Optional.of(diag), Optional.empty());
            }

            try (URLClassLoader cl = new URLClassLoader(new URL[] { classes.toUri().toURL() })) {
                Class<?> weaver = cl.loadClass(scenario.pkg + "." + scenario.weaverName);
                return new PipelineResult(scenario, Optional.empty(), Optional.empty(), Optional.of(weaver));
            }
        }
    }

    private Optional<Throwable> runGeneratorPhase(GeneratorAction action) {
        try {
            action.run();
            return Optional.empty();
        } catch (Throwable t) {
            return Optional.of(t);
        }
    }

    private static Scenario scenario(String name, String pkg, String weaverName, Expectation expectation) {
        return new Scenario(name, pkg, weaverName, expectation);
    }

    private static final class Scenario {
        private final String name;
        private final String pkg;
        private final String weaverName;
        private final Expectation expectation;
        private Optional<String> expectedMessage = Optional.empty();

        private Scenario(String name, String pkg, String weaverName, Expectation expectation) {
            this.name = name;
            this.pkg = pkg;
            this.weaverName = weaverName;
            this.expectation = expectation;
        }

        private Scenario withExpectedMessage(String message) {
            this.expectedMessage = Optional.of(message);
            return this;
        }
    }

    private enum Expectation {
        COMPILE_SUCCESS,
        COMPILATION_FAILURE,
        GENERATION_FAILURE
    }

    private record PipelineResult(Scenario scenario, Optional<Throwable> generationError,
            Optional<String> compilationDiagnostics, Optional<Class<?>> loadedWeaverClass) {
    }

    @FunctionalInterface
    private interface GeneratorAction {
        void run() throws Exception;
    }

    private static List<Path> listJava(Path root) throws IOException {
        try (Stream<Path> walk = Files.walk(root)) {
            return walk.filter(p -> p.toString().endsWith(".java")).collect(Collectors.toList());
        }
    }
}
