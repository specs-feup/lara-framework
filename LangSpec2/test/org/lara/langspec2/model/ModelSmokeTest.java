package org.lara.langspec2.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.lara.langspec2.dsl.WeaverSpec;

class ModelSmokeTest {

    @Test
    void modelCopiesCollectionsAndDefaults() {
        var jp = new JpClass("node");
        jp.addAttribute(new Attribute("id", WeaverSpec.STRING));
        jp.addAction(new Action("copy", WeaverSpec.THIS));

        var model = new WeaverModel("Prefix", "example.pkg", jp);
        assertThatThrownBy(() -> model.addJoinPoint(jp)).isInstanceOf(IllegalArgumentException.class);
        model.addTypeDef(new TypeDef("Info", List.of(new Attribute("name", WeaverSpec.STRING))));
        model.addEnumDef(new EnumDef("Kind", List.of(new EnumValue("A"))));

        assertThat(model.getAllJpClasses()).hasSize(1);
        assertThat(model.getTypeDefs()).containsKey("Info");
        assertThat(model.getEnumDefs()).containsKey("Kind");
        assertThat(jp.getAllAttributes()).hasSize(1);
        assertThat(jp.getAllActions()).hasSize(1);
    }
}
