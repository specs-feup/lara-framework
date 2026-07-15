package org.lara.weavergen2.pipeline;

import java.util.ArrayList;
import java.util.List;

import org.lara.langspec2.json.JsonSerializer;
import org.lara.weavergen2.api.ArtifactKind;
import org.lara.weavergen2.api.GeneratedArtifact;
import org.lara.weavergen2.api.WeaverGenerationRequest;
import org.lara.weavergen2.generator.AbstractJpGenerator;
import org.lara.weavergen2.generator.DotGenerator;
import org.lara.weavergen2.generator.EntityGenerator;
import org.lara.weavergen2.generator.WeaverAbstractGenerator;
import org.lara.weavergen2.java.TypeMapper;
import org.lara.weavergen2.source.ConcreteJoinPointSources;

public final class ArtifactPlanner {

    private final GeneratedArtifactFactory artifacts;

    public ArtifactPlanner() {
        this(new GeneratedArtifactFactory());
    }

    public ArtifactPlanner(GeneratedArtifactFactory artifacts) {
        this.artifacts = artifacts;
    }

    public List<GeneratedArtifact> plan(WeaverGenerationRequest request, GenerationBuildContext build,
            ConcreteJoinPointSources concreteSources) {
        var plannedArtifacts = new ArrayList<GeneratedArtifact>();
        var outputModel = build.config().hasBaseSpec() ? build.mergedModel() : build.model();

        for (var jp : build.model().getAllJpClasses()) {
            var gen = new AbstractJpGenerator(jp, build.model(), build.config(), concreteSources);
            var fileName = TypeMapper.abstractClassName(jp.getName()) + ".java";
            plannedArtifacts.add(artifacts.javaArtifact(ArtifactKind.ABSTRACT_JOINPOINT,
                    build.config().joinPointPackage(), fileName,
                    gen.generate()));
        }

        var entityGen = new EntityGenerator(build.model(), build.config());
        for (var entry : entityGen.generateTypeDefs().entrySet()) {
            plannedArtifacts.add(artifacts.javaArtifact(ArtifactKind.ENTITY, build.config().entitiesPackage(),
                    TypeMapper.capitalize(entry.getKey()) + ".java", entry.getValue()));
        }
        for (var entry : entityGen.generateEnumDefs().entrySet()) {
            plannedArtifacts.add(artifacts.javaArtifact(ArtifactKind.ENUM, build.config().enumsPackage(),
                    TypeMapper.capitalize(entry.getKey()) + ".java", entry.getValue()));
        }

        if (build.config().hasBaseSpec()) {
            var weaverGen = new WeaverAbstractGenerator(outputModel, build.config());
            plannedArtifacts.add(artifacts.javaArtifact(ArtifactKind.ABSTRACT_WEAVER,
                    build.config().abstractWeaverPackage(),
                    "A" + build.config().weaverName() + ".java", weaverGen.generate()));

            var dot = new DotGenerator(outputModel).generate();
            plannedArtifacts.add(artifacts.javaArtifact(ArtifactKind.DOT_GRAPH, build.config().basePackage(),
                    build.config().weaverName() + ".dotty", dot));
        }

        var json = JsonSerializer.toJson(outputModel, build.importEnums());
        var jsonPath = request.jsonOutputPath()
                .orElseGet(() -> artifacts.packagePath(build.config().basePackage())
                        .resolve(build.config().weaverName() + ".json"));
        plannedArtifacts.add(artifacts.jsonArtifact(build.config().basePackage(), build.config().weaverName(), jsonPath,
                json));

        return plannedArtifacts;
    }
}
