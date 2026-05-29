package org.lara.weavergen2.generator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.lara.weavergen2.fixtures.specs.base.BaseSpec;
import org.lara.weavergen2.fixtures.specs.valid.ThisTypeSpec;

class SpecMergerTest {

    @Test
    void mergesBaseMembersIntoWeaverModel() {
        var baseModel = new BaseSpec().buildRaw();
        var weaverModel = new ThisTypeSpec().build();

        var merged = SpecMerger.merge(baseModel, weaverModel);

        assertThat(merged.getGlobal().getOwnAttributes().stream().map(a -> a.name())).contains("node", "dump",
                "joinPointType", "self", "children", "descendants", "scopeNodes", "root", "ancestors",
                "siblings");
        assertThat(merged.getGlobal().getOwnActions().stream().map(a -> a.name())).contains("insert",
                "selfTransform", "replaceWith", "findAll", "categorize");
    }
}
