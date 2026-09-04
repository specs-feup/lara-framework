package org.lara.langspec2.dsl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.lara.langspec2.validation.SpecValidationException;

class WeaverSpecSmokeTest {

    @Test
    void buildsAndValidatesSimpleSpec() {
        var model = new WeaverSpec() {
            @Override
            public void define() {
                weaverPrefix("Demo");
                packageName("demo.pkg");
                rootJoinPoint("node");
                global().attribute("id", STRING);
                joinPoint("node").attribute("name", STRING);
            }
        }.build();

        assertThat(model.getPrefix()).isEqualTo("Demo");
        assertThat(model.getBasePackage()).isEqualTo("demo.pkg");
        assertThat(model.getRoot()).isPresent();
    }

    @Test
    void rejectsUnknownRefsInTypeDefFields() {
        assertThatThrownBy(() -> new WeaverSpec() {
            @Override
            public void define() {
                weaverPrefix("Demo");
                packageName("demo.pkg");
                global();
                typeDef("T").field("x", jpRef("missing")).end();
            }
        }.build())
                .isInstanceOf(SpecValidationException.class)
                .hasMessageContaining("Unknown type reference 'missing'")
                .hasMessageContaining("typedef 'T'");
    }
}
