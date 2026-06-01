package org.lara.weavergen2.cli;

import java.nio.file.Path;
import java.util.Optional;

import org.lara.langspec2.dsl.WeaverSpec;
import org.lara.weavergen2.api.ConcreteSourcePolicy;
import org.lara.weavergen2.api.WeaverGenerationRequest;
import org.lara.weavergen2.api.WeaverGenerator;

public final class WeaverGen2Cli {

    private WeaverGen2Cli() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println(
                    "Usage: WeaverGen2 <specClassName> <outputDir> [--base <baseSpecClassName>] [--node <nodeType>] [--jsonOutPath <jsonFilePath>] [--concrete-source-policy <disabled|validate-only|create-missing-and-validate>]");
            System.exit(1);
        }

        var parsed = parse(args);
        var request = new WeaverGenerationRequest(
                parsed.weaverSpec(),
                parsed.baseSpec(),
                parsed.nodeType(),
                parsed.outputDir(),
                parsed.jsonOutPath(),
                Optional.of(parsed.outputDir()),
                parsed.concreteSourcePolicy());

        new WeaverGenerator().generate(request);

        System.out.println("WeaverGen2: Generation complete. Output: " + parsed.outputDir());
    }

    private static ParsedArgs parse(String[] args) throws Exception {
        var specClassName = args[0];
        var outputDir = Path.of(args[1]);
        String baseSpecClassName = null;
        var nodeType = "java.lang.Object";
        Path jsonOutPath = null;
        var concreteSourcePolicy = ConcreteSourcePolicy.CREATE_MISSING_AND_VALIDATE;

        for (int i = 2; i < args.length; i++) {
            if ("--base".equals(args[i]) && i + 1 < args.length) {
                baseSpecClassName = args[++i];
            } else if ("--node".equals(args[i]) && i + 1 < args.length) {
                nodeType = args[++i];
            } else if ("--jsonOutPath".equals(args[i]) && i + 1 < args.length) {
                jsonOutPath = Path.of(args[++i]);
            } else if ("--concrete-source-policy".equals(args[i]) && i + 1 < args.length) {
                concreteSourcePolicy = ConcreteSourcePolicy.parse(args[++i]);
            }
        }

        var weaverSpec = instantiateSpec(specClassName);
        Optional<WeaverSpec> baseSpec = baseSpecClassName == null
                ? Optional.empty()
                : Optional.of(instantiateSpec(baseSpecClassName));

        return new ParsedArgs(weaverSpec, baseSpec, outputDir, nodeType, Optional.ofNullable(jsonOutPath),
                concreteSourcePolicy);
    }

    private static WeaverSpec instantiateSpec(String specClassName) throws Exception {
        var specClass = Class.forName(specClassName);
        return (WeaverSpec) specClass.getDeclaredConstructor().newInstance();
    }

    private record ParsedArgs(
            WeaverSpec weaverSpec,
            Optional<WeaverSpec> baseSpec,
            Path outputDir,
            String nodeType,
            Optional<Path> jsonOutPath,
            ConcreteSourcePolicy concreteSourcePolicy) {
    }
}
