package org.lara.weavergen2.api;

import java.util.List;

public record WeaverGenerationResult(
        List<GeneratedArtifact> generatedArtifacts,
        List<ConcreteSourceChange> concreteSourceChanges,
        List<GenerationDiagnostic> diagnostics) {

    public WeaverGenerationResult {
        generatedArtifacts = List.copyOf(generatedArtifacts);
        concreteSourceChanges = List.copyOf(concreteSourceChanges);
        diagnostics = List.copyOf(diagnostics);
    }
}
