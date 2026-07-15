package org.lara.weavergen2.pipeline;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.lara.weavergen2.api.ConcreteSourceChange;
import org.lara.weavergen2.api.ConcreteSourceChangeKind;
import org.lara.weavergen2.api.GenerationDiagnostic;
import org.lara.weavergen2.api.WeaverGenerationRequest;
import org.lara.weavergen2.source.ConcreteJoinPointSources;
import org.lara.weavergen2.source.ConcreteSourceSync;
import org.lara.weavergen2.source.NonConformingConcreteSource;

public final class GenerationPipeline {

    private final SpecResolver specs;
    private final ArtifactPlanner artifacts;

    public GenerationPipeline() {
        this(new SpecResolver(), new ArtifactPlanner());
    }

    public GenerationPipeline(SpecResolver specs, ArtifactPlanner artifacts) {
        this.specs = specs;
        this.artifacts = artifacts;
    }

    public GenerationPlan plan(WeaverGenerationRequest request) throws IOException {
        var build = specs.resolve(request);
        var concreteSources = new ConcreteJoinPointSources(build.model(), build.config(), request.concreteSourcePolicy());
        var concreteSourceSync = concreteSources.ensureConcreteSources();

        return new GenerationPlan(
                artifacts.plan(request, build, concreteSources),
                concreteSourceChanges(concreteSourceSync),
                List.<GenerationDiagnostic>of(),
                concreteSourceSync.nonConformingFiles());
    }

    public void reportNonConformingConcreteFiles(List<NonConformingConcreteSource> nonConformingConcreteFiles) {
        if (nonConformingConcreteFiles.isEmpty()) {
            return;
        }

        System.err.println("WeaverGen2: Found non-conforming concrete joinpoint source files:");
        for (var file : nonConformingConcreteFiles) {
            System.err.println("  - " + file.path() + ": " + file.reason());
        }

        var fileList = nonConformingConcreteFiles.stream()
                .map(NonConformingConcreteSource::path)
                .toList();
        throw new IllegalStateException(
                "Found " + nonConformingConcreteFiles.size()
                        + " non-conforming concrete joinpoint source file(s): " + fileList);
    }

    private List<ConcreteSourceChange> concreteSourceChanges(ConcreteSourceSync concreteSourceSync) {
        var changes = new ArrayList<ConcreteSourceChange>();
        for (var createdPath : concreteSourceSync.createdFiles()) {
            changes.add(new ConcreteSourceChange(ConcreteSourceChangeKind.CREATED, createdPath,
                    "Created missing concrete joinpoint source"));
        }
        for (var file : concreteSourceSync.nonConformingFiles()) {
            changes.add(new ConcreteSourceChange(ConcreteSourceChangeKind.NON_CONFORMING, Path.of(file.path()),
                    file.reason()));
        }
        return changes;
    }
}
