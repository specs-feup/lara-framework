package org.lara.interpreter.weaver.options;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link OptionArguments} enum - sanity check for enum values and presence.
 */
class OptionArgumentsTest {

    @Test
    void testEnumValues() {
        // Test all expected enum values are present
        OptionArguments[] values = OptionArguments.values();
        
        assertThat(values).hasSize(5);
        assertThat(values).containsExactlyInAnyOrder(
            OptionArguments.NO_ARGS,
            OptionArguments.ONE_ARG,
            OptionArguments.SEVERAL_ARGS,
            OptionArguments.OPTIONAL_ARG,
            OptionArguments.OPTIONAL_ARGS
        );
    }

    @Test
    void testEnumValueOf() {
        // Test that valueOf works correctly for each enum constant
        assertThat(OptionArguments.valueOf("NO_ARGS")).isEqualTo(OptionArguments.NO_ARGS);
        assertThat(OptionArguments.valueOf("ONE_ARG")).isEqualTo(OptionArguments.ONE_ARG);
        assertThat(OptionArguments.valueOf("SEVERAL_ARGS")).isEqualTo(OptionArguments.SEVERAL_ARGS);
        assertThat(OptionArguments.valueOf("OPTIONAL_ARG")).isEqualTo(OptionArguments.OPTIONAL_ARG);
        assertThat(OptionArguments.valueOf("OPTIONAL_ARGS")).isEqualTo(OptionArguments.OPTIONAL_ARGS);
    }

    @Test
    void testEnumToString() {
        // Test toString() for each enum value (should return name)
        assertThat(OptionArguments.NO_ARGS.toString()).isEqualTo("NO_ARGS");
        assertThat(OptionArguments.ONE_ARG.toString()).isEqualTo("ONE_ARG");
        assertThat(OptionArguments.SEVERAL_ARGS.toString()).isEqualTo("SEVERAL_ARGS");
        assertThat(OptionArguments.OPTIONAL_ARG.toString()).isEqualTo("OPTIONAL_ARG");
        assertThat(OptionArguments.OPTIONAL_ARGS.toString()).isEqualTo("OPTIONAL_ARGS");
    }
}