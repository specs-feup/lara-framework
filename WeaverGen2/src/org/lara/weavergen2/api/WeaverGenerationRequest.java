package org.lara.weavergen2.api;

import java.nio.file.Path;
import java.util.Optional;

import org.lara.langspec2.dsl.WeaverSpec;

public record WeaverGenerationRequest(
        WeaverSpec weaverSpec,
        Optional<WeaverSpec> baseSpec,
        String nodeType,
        Path outputDir,
        Optional<Path> jsonOutputPath,
        Optional<Path> projectRoot,
        ConcreteSourcePolicy concreteSourcePolicy) {

    public WeaverGenerationRequest {
        if (weaverSpec == null) {
            throw new IllegalArgumentException("weaverSpec must not be null");
        }
        baseSpec = baseSpec == null ? Optional.empty() : baseSpec;
        if (nodeType == null || nodeType.isBlank()) {
            nodeType = "java.lang.Object";
        }
        if (outputDir == null) {
            throw new IllegalArgumentException("outputDir must not be null");
        }
        jsonOutputPath = jsonOutputPath == null ? Optional.empty() : jsonOutputPath;
        projectRoot = projectRoot == null ? Optional.empty() : projectRoot;
        if (concreteSourcePolicy == null) {
            concreteSourcePolicy = ConcreteSourcePolicy.CREATE_MISSING_AND_VALIDATE;
        }
    }
}
