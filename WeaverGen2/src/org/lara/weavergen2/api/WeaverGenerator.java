package org.lara.weavergen2.api;

import java.io.IOException;

import org.lara.weavergen2.io.ArtifactWriter;
import org.lara.weavergen2.pipeline.GenerationPipeline;

public final class WeaverGenerator {

    private final GenerationPipeline pipeline;
    private final ArtifactWriter writer;

    public WeaverGenerator() {
        this(new GenerationPipeline(), new ArtifactWriter());
    }

    public WeaverGenerator(GenerationPipeline pipeline, ArtifactWriter writer) {
        this.pipeline = pipeline;
        this.writer = writer;
    }

    public WeaverGenerationResult generate(WeaverGenerationRequest request) throws IOException {
        var plan = pipeline.plan(request);
        writer.write(request.outputDir(), plan.generatedArtifacts());
        pipeline.reportNonConformingConcreteFiles(plan.nonConformingConcreteFiles());
        return plan.toResult();
    }
}
