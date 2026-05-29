package org.lara.weavergen2.fixtures;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.lara.langspec2.dsl.WeaverSpec;
import org.lara.langspec2.model.JpClass;
import org.lara.langspec2.model.WeaverModel;
import org.lara.weavergen2.WeaverGen2;
import org.lara.weavergen2.java.TypeMapper;

public final class WeaverGen2TestHarness {

    private WeaverGen2TestHarness() {
    }

    public static Scenario scenario(Class<? extends WeaverSpec> baseSpecClass,
            Class<? extends WeaverSpec> specClass) {
        return scenario(baseSpecClass, specClass, true);
    }

    public static Scenario scenario(Class<? extends WeaverSpec> baseSpecClass,
            Class<? extends WeaverSpec> specClass,
            boolean emitJson) {
        return new Scenario(baseSpecClass, specClass, emitJson);
    }

    public static Scenario scenario(Class<? extends WeaverSpec> specClass) {
        return new Scenario(null, specClass, true);
    }

    public static void assertSpecCanBuild(Scenario scenario) {
        assertThat(scenario.buildSpec()).isNotNull();
    }

    public static RunResult run(Scenario scenario, Path outputDir) {
        try {
            Files.createDirectories(outputDir);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not create output directory '" + outputDir + "'", e);
        }

        Path projectRoot;
        try {
            projectRoot = Files.createTempDirectory(outputDir.getParent(), outputDir.getFileName().toString() + "-project-");
        } catch (IOException e) {
            throw new UncheckedIOException("Could not create project sandbox for '" + outputDir + "'", e);
        }

        var model = scenario.buildModel();
        try {
            writeConcreteJoinPointStubs(projectRoot, model);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not prepare join point stubs for '" + scenario.specClass().getName() + "'", e);
        }

        var generator = scenario.createGenerator(projectRoot);
        var jsonOutPath = scenario.emitJson() ? outputDir.resolve("spec.json") : null;

        try {
            generator.generate(outputDir, jsonOutPath);
            return new RunResult(scenario, projectRoot, outputDir, jsonOutPath, null);
        } catch (Exception e) {
            return new RunResult(scenario, projectRoot, outputDir, jsonOutPath, e);
        }
    }

    private static void writeConcreteJoinPointStubs(Path projectRoot, WeaverModel model) throws IOException {
        Path srcRoot = projectRoot.resolve("src");
        String basePackagePath = model.getBasePackage().replace('.', '/');
        Path joinpointsRoot = srcRoot.resolve(basePackagePath).resolve("joinpoints");
        Files.createDirectories(joinpointsRoot);

        for (JpClass jp : model.getAllJpClasses()) {
            String className = model.getPrefix() + TypeMapper.capitalize(jp.getName());
            Path file = joinpointsRoot.resolve(className + ".java");
            String source = "package " + model.getBasePackage() + ".joinpoints;\n\n"
                    + "public class " + className + " {\n"
                    + "    public " + className + "(Object node, Object weaver) { }\n"
                    + "}\n";
            Files.writeString(file, source, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }
    }

    public record Scenario(Class<? extends WeaverSpec> baseSpecClass, Class<? extends WeaverSpec> specClass,
            boolean emitJson) {
        WeaverSpec buildSpec() {
            return instantiate(specClass);
        }

        org.lara.langspec2.model.WeaverModel buildModel() {
            return instantiate(specClass).build();
        }

        WeaverGen2 createGenerator(Path projectRoot) {
            if (baseSpecClass != null) {
                return WeaverGen2.fromSpecs(baseSpecClass, specClass, "java.lang.Object", projectRoot);
            }

            return WeaverGen2.fromSpec(specClass, "java.lang.Object", projectRoot);
        }

        private static WeaverSpec instantiate(Class<? extends WeaverSpec> specClass) {
            try {
                return specClass.getDeclaredConstructor().newInstance();
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("Could not instantiate spec class " + specClass.getName(), e);
            }
        }
    }

    public record RunResult(Scenario scenario, Path projectRoot, Path outputDir, Path jsonOutPath, Exception error) {
    }
}
