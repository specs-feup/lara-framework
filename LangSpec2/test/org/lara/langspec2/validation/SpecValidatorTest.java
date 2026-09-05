package org.lara.langspec2.validation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.lara.langspec2.model.EnumDef;
import org.lara.langspec2.model.EnumValue;
import org.lara.langspec2.model.JpClass;
import org.lara.langspec2.model.WeaverModel;

class SpecValidatorTest {

    private static WeaverModel newModel() {
        return new WeaverModel("Prefix", "example.pkg", new JpClass("global"));
    }

    @Test
    void duplicateDisplaysInEnumAreReported() {
        var model = newModel();
        model.addEnumDef(new EnumDef("Kind", List.of(
                new EnumValue("A"),
                new EnumValue("B"),
                new EnumValue("C", "A"))));

        var errors = SpecValidator.collectErrors(model);

        assertThat(errors).anyMatch(error -> error.contains("Duplicate display 'A' in enum 'Kind'"));
    }

    @Test
    void distinctDisplaysInEnumAreAccepted() {
        var model = newModel();
        model.addEnumDef(new EnumDef("Kind", List.of(
                new EnumValue("A"),
                new EnumValue("B"),
                new EnumValue("C", "c"))));

        var errors = SpecValidator.collectErrors(model);

        assertThat(errors).isEmpty();
    }
}
