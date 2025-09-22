package org.lara.interpreter.weaver.options;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OptionArgumentsTest {

    @Test
    @DisplayName("Enum contains expected constants")
    void hasExpectedConstants() {
        assertThat(OptionArguments.valueOf("NO_ARGS")).isNotNull();
        assertThat(OptionArguments.valueOf("ONE_ARG")).isNotNull();
        assertThat(OptionArguments.valueOf("SEVERAL_ARGS")).isNotNull();
        assertThat(OptionArguments.valueOf("OPTIONAL_ARG")).isNotNull();
        assertThat(OptionArguments.valueOf("OPTIONAL_ARGS")).isNotNull();
        assertThat(OptionArguments.values()).hasSize(5);
    }
}
