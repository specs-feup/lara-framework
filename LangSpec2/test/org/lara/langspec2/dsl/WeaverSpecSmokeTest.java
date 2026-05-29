package org.lara.langspec2.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

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
}
