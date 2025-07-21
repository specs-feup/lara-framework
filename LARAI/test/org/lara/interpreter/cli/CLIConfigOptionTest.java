package org.lara.interpreter.cli;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.commons.cli.Option;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lara.interpreter.weaver.options.OptionArguments;

/**
 * Unit tests for CLIConfigOption enum.
 * Tests the configuration option enumeration functionality.
 * 
 * @author Generated Tests
 */
class CLIConfigOptionTest {

    @Test
    @DisplayName("config option should have correct properties")
    void testConfigOption() {
        // Given
        CLIConfigOption config = CLIConfigOption.config;

        // Then
        assertThat(config.getShortOpt()).isEqualTo("c");
        assertThat(config.getDescription()).isEqualTo("configuration file with defined options");
        assertThat(config.getHasArgs()).isEqualTo(OptionArguments.ONE_ARG);
        assertThat(config.getArgName()).isEqualTo("file");
    }

    @Test
    @DisplayName("config option should build valid Option")
    void testConfigOptionBuildOption() {
        // Given
        CLIConfigOption config = CLIConfigOption.config;

        // When
        Option option = config.buildOption();

        // Then
        assertThat(option).isNotNull();
        assertThat(option.getOpt()).isEqualTo("c");
        assertThat(option.getLongOpt()).isEqualTo("config");
        assertThat(option.getDescription()).isEqualTo("configuration file with defined options");
        assertThat(option.hasArg()).isTrue();
        assertThat(option.getArgName()).isEqualTo("file");
    }

    @Test
    @DisplayName("contains() should return true for valid option names")
    void testContains_ValidOptions() {
        // Then
        assertThat(CLIConfigOption.contains("config")).isTrue();
    }

    @Test
    @DisplayName("contains() should return false for invalid option names")
    void testContains_InvalidOptions() {
        // Then
        assertThat(CLIConfigOption.contains("invalid")).isFalse();
        assertThat(CLIConfigOption.contains("help")).isFalse();
        assertThat(CLIConfigOption.contains("")).isFalse();
    }

    @Test
    @DisplayName("contains() should be case sensitive")
    void testContains_CaseSensitive() {
        // Then
        assertThat(CLIConfigOption.contains("CONFIG")).isFalse();
        assertThat(CLIConfigOption.contains("Config")).isFalse();
    }

    @Test
    @DisplayName("sameAs() should return true for matching option name")
    void testSameAs_MatchingOptionName() {
        // Given
        CLIConfigOption config = CLIConfigOption.config;

        // Then
        assertThat(config.sameAs("config")).isTrue();
    }

    @Test
    @DisplayName("sameAs() should return true for matching short option")
    void testSameAs_MatchingShortOption() {
        // Given
        CLIConfigOption config = CLIConfigOption.config;

        // Then
        assertThat(config.sameAs("c")).isTrue();
    }

    @Test
    @DisplayName("sameAs() should return false for non-matching options")
    void testSameAs_NonMatchingOptions() {
        // Given
        CLIConfigOption config = CLIConfigOption.config;

        // Then
        assertThat(config.sameAs("help")).isFalse();
        assertThat(config.sameAs("h")).isFalse();
        assertThat(config.sameAs("invalid")).isFalse();
        assertThat(config.sameAs("")).isFalse();
    }

    @Test
    @DisplayName("sameAs() should be case sensitive")
    void testSameAs_CaseSensitive() {
        // Given
        CLIConfigOption config = CLIConfigOption.config;

        // Then
        assertThat(config.sameAs("CONFIG")).isFalse();
        assertThat(config.sameAs("Config")).isFalse();
        assertThat(config.sameAs("C")).isFalse();
    }

    @Test
    @DisplayName("ALLOW_GUI should be accessible")
    void testAllowGui() {
        // Then
        assertThat(CLIConfigOption.ALLOW_GUI).isTrue();
    }

    @Test
    @DisplayName("enum should have all expected values")
    void testEnumValues() {
        // When
        CLIConfigOption[] values = CLIConfigOption.values();

        // Then
        assertThat(values).hasSize(1);
        assertThat(values[0]).isEqualTo(CLIConfigOption.config);
    }

    @Test
    @DisplayName("valueOf should work correctly")
    void testValueOf() {
        // When
        CLIConfigOption config = CLIConfigOption.valueOf("config");

        // Then
        assertThat(config).isEqualTo(CLIConfigOption.config);
    }
}
