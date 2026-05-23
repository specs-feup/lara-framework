package org.lara.weavergen2;

import org.lara.langspec2.dsl.WeaverSpec;
import org.lara.langspec2.json.JsonSerializer;
import org.lara.langspec2.model.*;
import org.lara.weavergen2.generator.*;
import org.lara.weavergen2.java.TypeMapper;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Main entry point for the WeaverGen2 code generator.
 * <p>
 * Generates abstract join point classes, weaver abstract,
 * entities/enums, JSON spec, and DOT hierarchy diagram from a weaver
 * specification.
 */
public final class WeaverGen2 {

    private final WeaverModel model;
    private final WeaverModel mergedModel;
    private final GeneratorConfig config;
    private final List<String> importEnums;

    public WeaverGen2(WeaverModel model, WeaverModel mergedModel, List<String> importEnums, GeneratorConfig config) {
        this.model = model;
        this.mergedModel = mergedModel;
        this.importEnums = List.copyOf(importEnums);
        this.config = config;
    }

    /**
     * Creates a generator from a base spec and weaver spec.
     */
    public static WeaverGen2 fromSpecs(WeaverSpec baseSpec, WeaverSpec weaverSpec, String nodeType) {
        var baseModel = instantiate(baseSpec).buildRaw();
        var weaverModel = weaverSpec.build();

        var baseMemberSignatures = new LinkedHashSet<String>();
        for (var attr : baseModel.getGlobal().getOwnAttributes()) {
            baseMemberSignatures.add(TypeMapper.memberSignature(attr.name(), attr.parameters()));
        }
        for (var action : baseModel.getGlobal().getOwnActions()) {
            baseMemberSignatures.add(TypeMapper.memberSignature(action.name(), action.parameters()));
        }

        var mergedModel = SpecMerger.merge(baseModel, weaverModel);

        var config = new GeneratorConfig(
                weaverModel.getWeaverName(),
                weaverModel.getBasePackage(),
                nodeType,
                true,
                true,
                Set.copyOf(baseMemberSignatures));

        var importEnums = new ArrayList<>(baseModel.getEnumDefs().keySet());

        return new WeaverGen2(weaverModel, mergedModel, importEnums, config);
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
                Set.of());

        return new WeaverGen2(model, model, List.of(), config);
    }

    /**
     * Generates all artifacts and writes them to the output directory.
     */
    public void generate(Path outputDir, Path jsonOutPath) throws IOException {
        var outputModel = config.hasBaseSpec() ? mergedModel : model;

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
            writeFile(outputDir, config.entitiesPackage(), TypeMapper.capitalize(entry.getKey()) + ".java",
                    entry.getValue());
        }
        for (var entry : entityGen.generateEnumDefs().entrySet()) {
            writeFile(outputDir, config.enumsPackage(), TypeMapper.capitalize(entry.getKey()) + ".java",
                    entry.getValue());
        }

        if (config.hasBaseSpec()) {
            // Abstract weaver
            var weaverGen = new WeaverAbstractGenerator(outputModel, config);
            writeFile(outputDir, config.abstractWeaverPackage(), "A" + config.weaverName() + ".java",
                    weaverGen.generate());

            // DOT
            var dot = new DotGenerator(outputModel).generate();
            writeFile(outputDir, config.basePackage(), config.weaverName() + ".dotty", dot);
        }

        // JSON
        var json = JsonSerializer.toJson(outputModel, importEnums);
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

    private void writeFile(Path outputDir, String pkg, String fileName, String content) throws IOException {
        var dir = outputDir.resolve(pkg.replace('.', '/'));
        Files.createDirectories(dir);
        Files.writeString(dir.resolve(fileName), content);
    }

    // ----- Command-line entry point -----

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println(
                    "Usage: WeaverGen2 <specClassName> <outputDir> [--base <baseSpecClassName>] [--node <nodeType>] [--jsonOutPath <jsonFilePath>]");
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

        System.out.println("WeaverGen2: Generation complete. Output: " + outputDir);
    }
}
