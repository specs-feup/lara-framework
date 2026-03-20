package org.lara.weavergen2;

import org.lara.langspec2.dsl.WeaverSpec;
import org.lara.langspec2.json.JsonSerializer;
import org.lara.langspec2.model.*;
import org.lara.langspec2.validation.SpecValidator;
import org.lara.weavergen2.generator.*;
import org.lara.weavergen2.java.TypeMapper;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Main entry point for the WeaverGen2 code generator.
 * <p>
 * Generates abstract join point classes, provider interfaces, registry, weaver abstract,
 * entities/enums, JSON spec, and DOT hierarchy diagram from a weaver specification.
 */
public final class WeaverGen2 {

    private final WeaverModel model;
    private final GeneratorConfig config;

    public WeaverGen2(WeaverModel model, GeneratorConfig config) {
        this.model = model;
        this.config = config;
    }

    /**
     * Creates a generator from a base spec and weaver spec, merging them.
     */
    public static WeaverGen2 fromSpecs(WeaverSpec baseSpec, WeaverSpec weaverSpec, String nodeType) {
        var merged = SpecMerger.merge(baseSpec, weaverSpec);
        SpecValidator.validate(merged);

        var config = new GeneratorConfig(
                merged.getWeaverName(),
                merged.getBasePackage(),
                nodeType,
                true
        );

        return new WeaverGen2(merged, config);
    }

    /**
     * Creates a generator from a single weaver spec (no base spec).
     */
    public static WeaverGen2 fromSpec(WeaverSpec weaverSpec, String nodeType) {
        var model = weaverSpec.build();

        var config = new GeneratorConfig(
                model.getWeaverName(),
                model.getBasePackage(),
                nodeType,
                true
        );

        return new WeaverGen2(model, config);
    }

    /**
     * Generates all artifacts and writes them to the output directory.
     */
    public void generate(Path outputDir) throws IOException {
        // Abstract join point classes
        for (var jp : model.getAllJpClasses()) {
            var gen = new AbstractJpGenerator(jp, model, config);
            var source = gen.generate();
            var fileName = TypeMapper.abstractClassName(jp.getName()) + ".java";
            writeFile(outputDir, config.joinPointPackage(), fileName, source);
        }

        // Provider definition interfaces
        for (var jp : model.getAllJpClasses()) {
            var gen = new ProviderDefGenerator(jp, model, config);
            if (gen.hasContent()) {
                var source = gen.generate();
                var fileName = TypeMapper.providerDefName(jp.getName()) + ".java";
                writeFile(outputDir, config.providerPackage(), fileName, source);
            }
        }

        // Provider registry
        var registryGen = new RegistryGenerator(config);
        writeFile(outputDir, config.registryPackage(), "ProviderRegistry.java", registryGen.generate());

        // Abstract weaver
        var weaverGen = new WeaverAbstractGenerator(model, config);
        writeFile(outputDir, config.abstractWeaverPackage(), "A" + config.weaverName() + ".java", weaverGen.generate());

        // Entities and enums
        var entityGen = new EntityGenerator(model, config);
        for (var entry : entityGen.generateTypeDefs().entrySet()) {
            writeFile(outputDir, config.entitiesPackage(), TypeMapper.capitalize(entry.getKey()) + ".java", entry.getValue());
        }
        for (var entry : entityGen.generateEnumDefs().entrySet()) {
            writeFile(outputDir, config.enumsPackage(), TypeMapper.capitalize(entry.getKey()) + ".java", entry.getValue());
        }

        // JSON
        var json = JsonSerializer.toJson(model);
        var jsonPackage = config.basePackage();
        writeFile(outputDir, jsonPackage, config.weaverName() + ".json", json);

        // DOT
        var dot = new DotGenerator(model).generate();
        writeFile(outputDir, jsonPackage, config.weaverName() + ".dotty", dot);
    }

    /**
     * Generates a user-editable bridge class (only if it doesn't exist).
     */
    public void generateUserAbstract(Path outputDir) throws IOException {
        var pkg = config.basePackage() + ".abstracts";
        var fileName = config.userAbstractClassName() + ".java";
        var filePath = outputDir.resolve(pkg.replace('.', '/')).resolve(fileName);

        if (Files.exists(filePath)) {
            return; // Don't overwrite user-editable class
        }

        var source = generateUserAbstractSource(pkg);
        writeFile(outputDir, pkg, fileName, source);
    }

    private String generateUserAbstractSource(String pkg) {
        var sb = new StringBuilder();
        sb.append("package ").append(pkg).append(";\n\n");
        sb.append("import ").append(config.joinPointPackage()).append(".*;\n");
        sb.append("import ").append(config.abstractWeaverPackage()).append(".").append(config.weaverClassName()).append(";\n");
        sb.append("import ").append(config.registryPackage()).append(".*;\n\n");
        sb.append("/**\n");
        sb.append(" * Abstract class which can be edited by the developer.\n");
        sb.append(" * This class will NOT be overwritten by the generator.\n");
        sb.append(" */\n");
        sb.append("public abstract class ").append(config.userAbstractClassName());
        sb.append("<Self extends ").append(config.userAbstractClassName()).append("<Self>>");
        sb.append(" extends ").append(TypeMapper.abstractClassName(model.getGlobal().getName())).append("<Self> {\n\n");
        sb.append("    public ").append(config.userAbstractClassName()).append("(").append(config.weaverClassName()).append(" weaver) {\n");
        sb.append("        super(weaver);\n");
        sb.append("    }\n");
        sb.append("}\n");
        return sb.toString();
    }

    private void writeFile(Path outputDir, String pkg, String fileName, String content) throws IOException {
        var dir = outputDir.resolve(pkg.replace('.', '/'));
        Files.createDirectories(dir);
        Files.writeString(dir.resolve(fileName), content);
    }

    // ----- Command-line entry point -----

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: WeaverGen2 <specClassName> <outputDir> [--base <baseSpecClassName>] [--node <nodeType>]");
            System.exit(1);
        }

        var specClassName = args[0];
        var outputDir = Path.of(args[1]);
        String baseSpecClassName = null;
        var nodeType = "java.lang.Object";

        for (int i = 2; i < args.length; i++) {
            if ("--base".equals(args[i]) && i + 1 < args.length) {
                baseSpecClassName = args[++i];
            } else if ("--node".equals(args[i]) && i + 1 < args.length) {
                nodeType = args[++i];
            }
        }

        var specClass = Class.forName(specClassName);
        var weaverSpec = (WeaverSpec) specClass.getDeclaredConstructor().newInstance();

        WeaverGen2 gen;
        if (baseSpecClassName != null) {
            var baseClass = Class.forName(baseSpecClassName);
            var baseSpec = (WeaverSpec) baseClass.getDeclaredConstructor().newInstance();
            gen = fromSpecs(baseSpec, weaverSpec, nodeType);
        } else {
            gen = fromSpec(weaverSpec, nodeType);
        }

        gen.generate(outputDir);
        gen.generateUserAbstract(outputDir);

        System.out.println("WeaverGen2: Generation complete. Output: " + outputDir);
    }
}
