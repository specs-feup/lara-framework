package org.lara.weavergen2.pipeline;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import org.lara.weavergen2.api.WeaverGenerationRequest;
import org.lara.weavergen2.model.GenerationProfile;
import org.lara.weavergen2.model.JoinPointMember;
import org.lara.weavergen2.model.MemberSignature;
import org.lara.weavergen2.model.SpecModelMerger;

public final class SpecResolver {

    private final FinalWrapperCatalog finalWrappers;

    public SpecResolver() {
        this(new FinalWrapperCatalog());
    }

    public SpecResolver(FinalWrapperCatalog finalWrappers) {
        this.finalWrappers = finalWrappers;
    }

    public GenerationBuildContext resolve(WeaverGenerationRequest request) {
        var weaverModel = request.weaverSpec().build();

        if (request.baseSpec().isEmpty()) {
            var config = new GenerationProfile(
                    weaverModel.getPrefix(),
                    weaverModel.getBasePackage(),
                    request.nodeType(),
                    true,
                    false,
                    Set.of(),
                    finalWrappers.inheritedFinalWrapperSignatures(),
                    request.projectRoot().orElse(null));
            return new GenerationBuildContext(weaverModel, weaverModel, java.util.List.of(), config);
        }

        var baseModel = request.baseSpec().orElseThrow().buildRaw();
        var baseMemberSignatures = new LinkedHashSet<MemberSignature>();
        for (var attr : baseModel.getGlobal().getOwnAttributes()) {
            baseMemberSignatures.add(JoinPointMember.attribute(attr).signature());
        }
        for (var action : baseModel.getGlobal().getOwnActions()) {
            baseMemberSignatures.add(JoinPointMember.action(action).signature());
        }

        var mergedModel = SpecModelMerger.merge(baseModel, weaverModel);

        var config = new GenerationProfile(
                weaverModel.getPrefix(),
                weaverModel.getBasePackage(),
                request.nodeType(),
                true,
                true,
                Set.copyOf(baseMemberSignatures),
                finalWrappers.inheritedFinalWrapperSignatures(),
                request.projectRoot().orElse(null));

        return new GenerationBuildContext(
                weaverModel,
                mergedModel,
                new ArrayList<>(baseModel.getEnumDefs().keySet()),
                config);
    }
}
