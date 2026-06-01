package org.lara.weavergen2.pipeline;

import java.util.List;

import org.lara.langspec2.model.WeaverModel;
import org.lara.weavergen2.model.GenerationProfile;

public record GenerationBuildContext(
        WeaverModel model,
        WeaverModel mergedModel,
        List<String> importEnums,
        GenerationProfile config) {
}
