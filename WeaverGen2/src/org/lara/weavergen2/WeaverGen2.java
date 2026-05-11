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
 * Generates abstract join point classes, weaver abstract,
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
     * Creates a generator from a base spec and weaver spec.
     * <p>
     * The base spec is only used to record inherited base signatures so the generator can
     * recognize overrides in the weaver spec. It is not merged into the generated model.
     */
    public static WeaverGen2 fromSpecs(WeaverSpec baseSpec, WeaverSpec weaverSpec, String nodeType) {
        var baseModel = instantiate(baseSpec).buildRaw();
        var baseMemberSignatures = new LinkedHashSet<String>();
        for (var attr : baseModel.getGlobal().getOwnAttributes()) {
            baseMemberSignatures.add(TypeMapper.memberSignature(attr.name(), attr.parameters()));
        }
        for (var action : baseModel.getGlobal().getOwnActions()) {
            baseMemberSignatures.add(TypeMapper.memberSignature(action.name(), action.parameters()));
        }

        var model = weaverSpec.build();
        SpecValidator.validate(model);

        var config = new GeneratorConfig(
                model.getWeaverName(),
                model.getBasePackage(),
                nodeType,
                true,
                true,
                Set.copyOf(baseMemberSignatures)
        );

        return new WeaverGen2(model, config);
    }

    private static WeaverSpec instantiate(WeaverSpec spec) {
        try {
            return spec.getClass().getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Could not instantiate spec class " + spec.getClass().getName(), e);
        }
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
                true,
                false,
                Set.of()
        );

        return new WeaverGen2(model, config);
    }

    /**
     * Generates all artifacts and writes them to the output directory.
     */
    public void generate(Path outputDir, Path jsonOutPath) throws IOException {
        // Abstract join point classes
        for (var jp : model.getAllJpClasses()) {
            var gen = new AbstractJpGenerator(jp, model, config);
            var source = gen.generate();
            var fileName = TypeMapper.abstractClassName(jp.getName()) + ".java";
            writeFile(outputDir, config.joinPointPackage(), fileName, source);
        }

        // Entities and enums
        var entityGen = new EntityGenerator(model, config);
        for (var entry : entityGen.generateTypeDefs().entrySet()) {
            writeFile(outputDir, config.entitiesPackage(), TypeMapper.capitalize(entry.getKey()) + ".java", entry.getValue());
        }
        for (var entry : entityGen.generateEnumDefs().entrySet()) {
            writeFile(outputDir, config.enumsPackage(), TypeMapper.capitalize(entry.getKey()) + ".java", entry.getValue());
        }

        if (config.hasBaseSpec()) {
            // Abstract weaver
            var weaverGen = new WeaverAbstractGenerator(model, config);
            writeFile(outputDir, config.abstractWeaverPackage(), "A" + config.weaverName() + ".java", weaverGen.generate());

            // DOT
            var dot = new DotGenerator(model).generate();
            writeFile(outputDir, config.basePackage(), config.weaverName() + ".dotty", dot);
        }

        // JSON
        var json = JsonSerializer.toJson(model);
        if (jsonOutPath != null) {
            var parentDir = jsonOutPath.getParent();
            if (parentDir != null) {
                Files.createDirectories(parentDir);
            }
            Files.writeString(jsonOutPath, json);
        } else {
            writeFile(outputDir, config.basePackage(), config.weaverName() + ".json", json);
        }
    }

    /**
     * Generates a user-editable bridge class (only if it doesn't exist).
     */
    public void generateUserAbstract(Path outputDir) throws IOException {
        if (!config.hasBaseSpec()) {
            return; // Only generate user abstract if we have a base spec (otherwise there's no generated abstract weaver to extend)
        }

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
        sb.append("import ").append(config.joinPointPackage()).append(".")
            .append(TypeMapper.abstractClassName(model.getGlobal().getName())).append(";\n");
        sb.append("import ").append(config.abstractWeaverPackage()).append(".").append(config.weaverClassName()).append(";\n\n");
        sb.append("/**\n");
        sb.append(" * Abstract class which can be edited by the developer.\n");
        sb.append(" * This class will NOT be overwritten by the generator.\n");
        sb.append(" */\n");
        sb.append("public abstract class ").append(config.userAbstractClassName());
        sb.append("<Self extends ").append(config.userAbstractClassName()).append("<Self>>");
        sb.append(" extends ").append(TypeMapper.abstractClassName(model.getGlobal().getName())).append("<Self> {\n\n");
        sb.append("    public ").append(config.userAbstractClassName()).append("(").append(config.nodeType()).append(" node, ")
            .append(config.weaverClassName()).append(" weaver) {\n");
        sb.append("        super(node, weaver);\n");
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
            System.err.println("Usage: WeaverGen2 <specClassName> <outputDir> [--base <baseSpecClassName>] [--node <nodeType>] [--jsonOutPath <jsonFilePath>]");
            System.exit(1);
        }

        var specClassName = args[0];
        var outputDir = Path.of(args[1]);
        String baseSpecClassName = null;
        var nodeType = "java.lang.Object";
        Path jsonOutPath = null;

        for (int i = 2; i < args.length; i++) {
            if ("--base".equals(args[i]) && i + 1 < args.length) {
                baseSpecClassName = args[++i];
            } else if ("--node".equals(args[i]) && i + 1 < args.length) {
                nodeType = args[++i];
            } else if ("--jsonOutPath".equals(args[i]) && i + 1 < args.length) {
                jsonOutPath = Path.of(args[++i]);
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

        gen.generate(outputDir, jsonOutPath);
        gen.generateUserAbstract(outputDir);

        System.out.println("WeaverGen2: Generation complete. Output: " + outputDir);
    }
}
