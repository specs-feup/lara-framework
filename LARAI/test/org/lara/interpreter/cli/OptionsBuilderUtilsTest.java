package org.lara.interpreter.cli;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.commons.cli.Option;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lara.interpreter.weaver.options.OptionArguments;
import org.lara.interpreter.weaver.options.WeaverOption;

/**
 * Unit tests for OptionsBuilderUtils class.
 * Tests the CLI option building utility methods.
 * 
 * @author Generated Tests
 */
class OptionsBuilderUtilsTest {

    @Test
    @DisplayName("newOption() should create option with no arguments")
    void testNewOption_NoArgs() {
        // When
        Option option = OptionsBuilderUtils.newOption("h", "help", OptionArguments.NO_ARGS, "", "show help");

        // Then
        assertThat(option).isNotNull();
        assertThat(option.getOpt()).isEqualTo("h");
        assertThat(option.getLongOpt()).isEqualTo("help");
        assertThat(option.getDescription()).isEqualTo("show help");
        assertThat(option.hasArg()).isFalse();
        assertThat(option.hasArgs()).isFalse();
        assertThat(option.getArgName()).isNull();
    }

    @Test
    @DisplayName("newOption() should create option with one argument")
    void testNewOption_OneArg() {
        // When
        Option option = OptionsBuilderUtils.newOption("f", "file", OptionArguments.ONE_ARG, "filename", "input file");

        // Then
        assertThat(option).isNotNull();
        assertThat(option.getOpt()).isEqualTo("f");
        assertThat(option.getLongOpt()).isEqualTo("file");
        assertThat(option.getDescription()).isEqualTo("input file");
        assertThat(option.hasArg()).isTrue();
        assertThat(option.hasArgs()).isFalse();
        assertThat(option.getArgName()).isEqualTo("filename");
    }

    @Test
    @DisplayName("newOption() should create option with several arguments")
    void testNewOption_SeveralArgs() {
        // When
        Option option = OptionsBuilderUtils.newOption("p", "params", OptionArguments.SEVERAL_ARGS, "values",
                "parameters");

        // Then
        assertThat(option).isNotNull();
        assertThat(option.getOpt()).isEqualTo("p");
        assertThat(option.getLongOpt()).isEqualTo("params");
        assertThat(option.getDescription()).isEqualTo("parameters");
        assertThat(option.hasArgs()).isTrue();
        assertThat(option.getArgName()).isEqualTo("values");
    }

    @Test
    @DisplayName("newOption() should create option with optional argument")
    void testNewOption_OptionalArg() {
        // When
        Option option = OptionsBuilderUtils.newOption("o", "output", OptionArguments.OPTIONAL_ARG, "file",
                "output file");

        // Then
        assertThat(option).isNotNull();
        assertThat(option.getOpt()).isEqualTo("o");
        assertThat(option.getLongOpt()).isEqualTo("output");
        assertThat(option.getDescription()).isEqualTo("output file");
        assertThat(option.hasArg()).isTrue();
        assertThat(option.hasOptionalArg()).isTrue();
        assertThat(option.getArgName()).isEqualTo("file");
    }

    @Test
    @DisplayName("newOption() should create option with optional arguments")
    void testNewOption_OptionalArgs() {
        // When
        Option option = OptionsBuilderUtils.newOption("x", "extra", OptionArguments.OPTIONAL_ARGS, "items",
                "extra items");

        // Then
        assertThat(option).isNotNull();
        assertThat(option.getOpt()).isEqualTo("x");
        assertThat(option.getLongOpt()).isEqualTo("extra");
        assertThat(option.getDescription()).isEqualTo("extra items");
        assertThat(option.hasArgs()).isTrue();
        assertThat(option.hasOptionalArg()).isTrue();
        assertThat(option.getArgName()).isEqualTo("items");
    }

    @Test
    @DisplayName("newOption() should handle null short option")
    void testNewOption_NullShortOption() {
        // When
        Option option = OptionsBuilderUtils.newOption(null, "verbose", OptionArguments.NO_ARGS, "", "verbose output");

        // Then
        assertThat(option).isNotNull();
        assertThat(option.getOpt()).isNull();
        assertThat(option.getLongOpt()).isEqualTo("verbose");
        assertThat(option.getDescription()).isEqualTo("verbose output");
        assertThat(option.hasArg()).isFalse();
    }

    @Test
    @DisplayName("newOption() should handle empty arg name")
    void testNewOption_EmptyArgName() {
        // When
        Option option = OptionsBuilderUtils.newOption("t", "test", OptionArguments.ONE_ARG, "", "test option");

        // Then
        assertThat(option).isNotNull();
        assertThat(option.getOpt()).isEqualTo("t");
        assertThat(option.getLongOpt()).isEqualTo("test");
        assertThat(option.getDescription()).isEqualTo("test option");
        assertThat(option.hasArg()).isTrue();
        assertThat(option.getArgName()).isNull(); // Empty string means no arg name is set
    }

    @Test
    @DisplayName("newOption() with CLIOption should create valid option with arguments")
    void testNewOption_CLIOption_WithArgs() {
        // Given
        CLIOption cliOption = CLIOption.help;

        // When
        Option option = OptionsBuilderUtils.newOption(cliOption, "topic", OptionArguments.ONE_ARG, "help topic");

        // Then
        assertThat(option).isNotNull();
        assertThat(option.getOpt()).isEqualTo(cliOption.shortOption());
        assertThat(option.getLongOpt()).isEqualTo(cliOption.name());
        assertThat(option.getDescription()).isEqualTo("help topic");
        assertThat(option.hasArg()).isTrue();
        assertThat(option.getArgName()).isEqualTo("topic");
    }

    @Test
    @DisplayName("newOption() with CLIOption should create valid option without arguments")
    void testNewOption_CLIOption_NoArgs() {
        // Given
        CLIOption cliOption = CLIOption.version;

        // When
        Option option = OptionsBuilderUtils.newOption(cliOption, "show version");

        // Then
        assertThat(option).isNotNull();
        assertThat(option.getOpt()).isEqualTo(cliOption.shortOption());
        assertThat(option.getLongOpt()).isEqualTo(cliOption.name());
        assertThat(option.getDescription()).isEqualTo("show version");
        assertThat(option.hasArg()).isFalse();
        assertThat(option.hasArgs()).isFalse();
    }

    @Test
    @DisplayName("newOption() with WeaverOption should create valid option")
    void testNewOption_WeaverOption() {
        // Given
        WeaverOption weaverOption = mock(WeaverOption.class);
        when(weaverOption.shortOption()).thenReturn("w");
        when(weaverOption.longOption()).thenReturn("weaver");
        when(weaverOption.args()).thenReturn(OptionArguments.ONE_ARG);
        when(weaverOption.argName()).thenReturn("name");
        when(weaverOption.description()).thenReturn("weaver option");

        // When
        Option option = OptionsBuilderUtils.newOption(weaverOption);

        // Then
        assertThat(option).isNotNull();
        assertThat(option.getOpt()).isEqualTo("w");
        assertThat(option.getLongOpt()).isEqualTo("weaver");
        assertThat(option.getDescription()).isEqualTo("weaver option");
        assertThat(option.hasArg()).isTrue();
        assertThat(option.getArgName()).isEqualTo("name");
    }

    @Test
    @DisplayName("newOption() with WeaverOption should handle null values")
    void testNewOption_WeaverOption_NullValues() {
        // Given
        WeaverOption weaverOption = mock(WeaverOption.class);
        when(weaverOption.shortOption()).thenReturn(null);
        when(weaverOption.longOption()).thenReturn("long-option");
        when(weaverOption.args()).thenReturn(OptionArguments.NO_ARGS);
        when(weaverOption.argName()).thenReturn("");
        when(weaverOption.description()).thenReturn("test description");

        // When
        Option option = OptionsBuilderUtils.newOption(weaverOption);

        // Then
        assertThat(option).isNotNull();
        assertThat(option.getOpt()).isNull();
        assertThat(option.getLongOpt()).isEqualTo("long-option");
        assertThat(option.getDescription()).isEqualTo("test description");
        assertThat(option.hasArg()).isFalse();
    }
}
