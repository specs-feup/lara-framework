package org.lara.interpreter.weaver.options;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Datakey.KeyFactory;

class WeaverOptionBuilderTest {

    enum Opts {
        ONE, TWO
    }

    @Test
    @DisplayName("Builder maps all fields correctly")
    void builderMapsFields() {
        DataKey<String> key = KeyFactory.string("foo").setLabel("Foo Label");

        WeaverOption opt = new WeaverOptionBuilder()
                .shortOption("-f")
                .longOption("--foo")
                .description("Foo option")
                .args(OptionArguments.ONE_ARG)
                .argName("FILE")
                .dataKey(key)
                .build();

        assertThat(opt.shortOption()).isEqualTo("-f");
        assertThat(opt.longOption()).isEqualTo("--foo");
        assertThat(opt.description()).isEqualTo("Foo option");
        assertThat(opt.args()).isEqualTo(OptionArguments.ONE_ARG);
        assertThat(opt.argName()).isEqualTo("FILE");
        assertThat(opt.dataKey()).isSameAs(key);
        assertThat(opt.toString()).isEqualTo("Foo option");
    }

    @Test
    @DisplayName("Shortcut build(DataKey) uses name and label as expected")
    void shortcutBuildFromDataKey() {
        DataKey<String> key = KeyFactory.string("bar").setLabel("Bar Label");

        WeaverOption opt = WeaverOptionBuilder.build(key);

        // short option is empty string per implementation
        assertThat(opt.shortOption()).isEmpty();
        assertThat(opt.longOption()).isEqualTo("bar");
        assertThat(opt.description()).isEqualTo("Bar Label");
        assertThat(opt.args()).isEqualTo(OptionArguments.NO_ARGS);
        assertThat(opt.dataKey()).isSameAs(key);
    }

    @Test
    @DisplayName("enum2List maps enum constants in order with given mapper")
    void enum2ListMapping() {
        DataKey<String> key = KeyFactory.string("k").setLabel("K");
        List<WeaverOption> list = WeaverOptionBuilder.enum2List(
                Opts.class,
                e -> new WeaverOptionBuilder()
                        .shortOption("-" + e.name().toLowerCase())
                        .longOption("--" + e.name().toLowerCase())
                        .description("desc:" + e.name())
                        .args(OptionArguments.NO_ARGS)
                        .dataKey(key)
                        .build());

        assertThat(list).hasSize(Opts.values().length);
        assertThat(list.get(0).longOption()).isEqualTo("--one");
        assertThat(list.get(1).longOption()).isEqualTo("--two");
    }
}
