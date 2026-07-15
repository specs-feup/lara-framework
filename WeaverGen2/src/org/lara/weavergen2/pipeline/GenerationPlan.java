package org.lara.weavergen2.pipeline;

import java.util.List;

import org.lara.weavergen2.api.ConcreteSourceChange;
import org.lara.weavergen2.api.GeneratedArtifact;
import org.lara.weavergen2.api.GenerationDiagnostic;
import org.lara.weavergen2.api.WeaverGenerationResult;
import org.lara.weavergen2.source.NonConformingConcreteSource;

public record GenerationPlan(
        List<GeneratedArtifact> generatedArtifacts,
        List<ConcreteSourceChange> concreteSourceChanges,
        List<GenerationDiagnostic> diagnostics,
        List<NonConformingConcreteSource> nonConformingConcreteFiles) {

    public GenerationPlan {
        generatedArtifacts = List.copyOf(generatedArtifacts);
        concreteSourceChanges = List.copyOf(concreteSourceChanges);
        diagnostics = List.copyOf(diagnostics);
        nonConformingConcreteFiles = List.copyOf(nonConformingConcreteFiles);
    }

    public WeaverGenerationResult toResult() {
        return new WeaverGenerationResult(generatedArtifacts, concreteSourceChanges, diagnostics);
    }
}
