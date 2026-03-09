package org.lara.interpreter.weaver.generator.fixtures;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.lara.interpreter.weaver.generator.commandline.WeaverGenerator;

public final class WeaverGeneratorTestHarness {

    private static final Path SPEC_ROOT = Path.of("test-resources/spec/valid");

    private WeaverGeneratorTestHarness() {
    }

    public static Scenario scenario(String name, String packageName, String weaverName) {
        return scenario(name, packageName, weaverName, false);
    }

    public static Scenario scenario(String name, String packageName, String weaverName, boolean emitJson) {
        return new Scenario(name, SPEC_ROOT.resolve(name), packageName, weaverName, emitJson);
    }

    public static void assertSpecDirExists(Scenario scenario) {
        assertThat(Files.isDirectory(scenario.specDir()))
                .as("Missing scenario directory: %s", scenario.specDir())
                .isTrue();
    }

    public static RunResult run(Scenario scenario, Path outputDir) {
        try {
            Files.createDirectories(outputDir);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not create output directory '" + outputDir + "'", e);
        }

        String[] args = buildArgs(scenario, outputDir);
        int exitCode = WeaverGenerator.run(args);
        return new RunResult(scenario, outputDir, exitCode, args);
    }

    private static String[] buildArgs(Scenario scenario, Path outputDir) {
        if (scenario.emitJson()) {
            return new String[] {
                    "-x", scenario.specDir().toString(),
                    "-o", outputDir.toString(),
                    "-p", scenario.packageName(),
                    "-w", scenario.weaverName(),
                    "-j"
            };
        }

        return new String[] {
                "-x", scenario.specDir().toString(),
                "-o", outputDir.toString(),
                "-p", scenario.packageName(),
                "-w", scenario.weaverName()
        };
    }

    public record Scenario(String name, Path specDir, String packageName, String weaverName, boolean emitJson) {
    }

    public record RunResult(Scenario scenario, Path outputDir, int exitCode, String[] args) {
    }
}