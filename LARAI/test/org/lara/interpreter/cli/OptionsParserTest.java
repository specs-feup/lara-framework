package org.lara.interpreter.cli;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.lara.interpreter.cli.OptionsParser.ExecutionMode;
import org.lara.interpreter.exception.LaraIException;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.lara.interpreter.weaver.options.OptionArguments;
import org.lara.interpreter.weaver.options.WeaverOption;
import org.suikasoft.jOptions.persistence.XmlPersistence;
import org.suikasoft.jOptions.storedefinition.StoreDefinition;

/**
 * Unit tests for OptionsParser class.
 * Tests the command-line option parsing functionality.
 * 
 * @author Generated Tests
 */
class OptionsParserTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("buildLaraIOptionGroup() should return collection of options")
    void testBuildLaraIOptionGroup() {
        // When
        Collection<Option> options = OptionsParser.buildLaraIOptionGroup();

        // Then
        assertThat(options).isNotNull();
        assertThat(options).isNotEmpty();

        // Should contain basic LARAI options
        assertThat(options.stream().anyMatch(opt -> "h".equals(opt.getOpt()))).isTrue(); // help
        assertThat(options.stream().anyMatch(opt -> "v".equals(opt.getOpt()))).isTrue(); // version
    }

    @Test
    @DisplayName("buildConfigOptions() should return collection of config options")
    void testBuildConfigOptions() {
        // When
        Collection<Option> options = OptionsParser.buildConfigOptions();

        // Then
        assertThat(options).isNotNull();
        assertThat(options).isNotEmpty();

        // Should contain config option
        assertThat(options.stream().anyMatch(opt -> "c".equals(opt.getOpt()))).isTrue(); // config
    }

    @Test
    @DisplayName("parse() should parse valid arguments")
    void testParse_ValidArguments() throws ParseException {
        // Given
        String[] args = { "-h" };
        Options options = new Options();
        options.addOption("h", "help", false, "show help");

        // When
        CommandLine cmd = OptionsParser.parse(args, options);

        // Then
        assertThat(cmd).isNotNull();
        assertThat(cmd.hasOption("h")).isTrue();
    }

    @Test
    @DisplayName("parse() should throw exception for invalid arguments")
    void testParse_InvalidArguments() {
        // Given
        String[] args = { "-invalid" };
        Options options = new Options();

        // Then
        assertThatThrownBy(() -> OptionsParser.parse(args, options))
                .isInstanceOf(LaraIException.class)
                .hasMessageContaining("Unrecognized option");
    }

    @Test
    @DisplayName("parse() should throw exception for empty arguments")
    void testParse_EmptyArguments() {
        // Given
        String[] args = {};
        Options options = new Options();

        // Then
        assertThatThrownBy(() -> OptionsParser.parse(args, options))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("LARA aspect file is required");
    }

    @Test
    @DisplayName("addExtraOptions() should add weaver options to main options")
    void testAddExtraOptions() {
        // Given
        Collection<Option> mainOptions = OptionsParser.buildLaraIOptionGroup();
        int originalSize = mainOptions.size();

        WeaverOption mockOption = mock(WeaverOption.class);
        when(mockOption.shortOption()).thenReturn("w");
        when(mockOption.longOption()).thenReturn("weaver");
        when(mockOption.args()).thenReturn(OptionArguments.NO_ARGS);
        when(mockOption.argName()).thenReturn("");
        when(mockOption.description()).thenReturn("weaver option");

        List<WeaverOption> extraOptions = Arrays.asList(mockOption);

        // When
        OptionsParser.addExtraOptions(mainOptions, extraOptions);

        // Then
        assertThat(mainOptions).hasSize(originalSize + 1);
        assertThat(mainOptions.stream().anyMatch(opt -> "w".equals(opt.getOpt()))).isTrue();
    }

    @Test
    @DisplayName("getHelp() should return help string")
    void testGetHelp() {
        // Given
        Options options = new Options();
        options.addOption("h", "help", false, "show help");
        options.addOption("v", "version", false, "show version");

        // When
        String help = OptionsParser.getHelp(options);

        // Then
        assertThat(help).isNotNull();
        assertThat(help).isNotEmpty();
        assertThat(help).contains("help");
        assertThat(help).contains("version");
    }

    @Test
    @DisplayName("getHelp() with padding should format correctly")
    void testGetHelp_WithPadding() {
        // Given
        Options options = new Options();
        options.addOption("h", "help", false, "show help");
        int padding = 10;

        // When
        String help = OptionsParser.getHelp(options, padding);

        // Then
        assertThat(help).isNotNull();
        assertThat(help).isNotEmpty();
        assertThat(help).contains("help");
    }

    @Test
    @DisplayName("getExecMode() should return OPTIONS for help option")
    void testGetExecMode_HelpOption() throws ParseException {
        // Given
        String[] args = { "-h" };
        Options options = new Options();
        options.addOption("h", "help", false, "show help");
        CommandLine cmd = OptionsParser.parse(args, options);

        // When
        ExecutionMode mode = OptionsParser.getExecMode("", cmd, options);

        // Then
        assertThat(mode).isEqualTo(ExecutionMode.OPTIONS);
    }

    @Test
    @DisplayName("getExecMode() should return CONFIG_OPTIONS for config with options")
    void testGetExecMode_ConfigWithOptions() throws ParseException, IOException {
        // Given
        Path configFile = tempDir.resolve("test.config");
        Files.createFile(configFile);

        String[] args = { "-c", configFile.toString(), "-v" };
        Options options = new Options();
        options.addOption("c", "config", true, "config file");
        options.addOption("v", "version", false, "show version");
        CommandLine cmd = OptionsParser.parse(args, options);

        // When
        ExecutionMode mode = OptionsParser.getExecMode("", cmd, options);

        // Then
        assertThat(mode).isEqualTo(ExecutionMode.CONFIG_OPTIONS);
    }

    @Test
    @DisplayName("getExecMode() should return CONFIG_OPTIONS when config option is detected")
    void testGetExecMode_ConfigWithOption() throws ParseException, IOException {
        // Given
        Path configFile = tempDir.resolve("test.config");
        Files.createFile(configFile);

        String[] args = { "-c", configFile.toString() };
        Options options = new Options();
        options.addOption("c", "config", true, "config file");
        CommandLine cmd = OptionsParser.parse(args, options);

        // When - the method detects config option presence and returns CONFIG_OPTIONS
        ExecutionMode mode = OptionsParser.getExecMode("-c", cmd, options);

        // Then
        assertThat(mode).isEqualTo(ExecutionMode.CONFIG_OPTIONS);
    }

    @Test
    @DisplayName("getConfigFile() should return existing file")
    void testGetConfigFile_ExistingFile() throws ParseException, IOException {
        // Given
        Path configFile = tempDir.resolve("test.config");
        Files.createFile(configFile);

        String[] args = { "-c", configFile.toString() };
        Options options = new Options();
        options.addOption("c", "config", true, "config file");
        CommandLine cmd = OptionsParser.parse(args, options);

        // When
        File result = OptionsParser.getConfigFile(cmd);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.exists()).isTrue();
        assertThat(result.getAbsolutePath()).isEqualTo(configFile.toAbsolutePath().toString());
    }

    @Test
    @DisplayName("getConfigFile() should throw exception for non-existing file")
    void testGetConfigFile_NonExistingFile() throws ParseException {
        // Given
        String[] args = { "-c", "nonexistent.config" };
        Options options = new Options();
        options.addOption("c", "config", true, "config file");
        CommandLine cmd = OptionsParser.parse(args, options);

        // Then
        assertThatThrownBy(() -> OptionsParser.getConfigFile(cmd))
                .isInstanceOf(LaraIException.class)
                .hasMessageContaining("does not exist");
    }

    @Test
    @DisplayName("getLaraStoreDefinition() should return store definition")
    void testGetLaraStoreDefinition() {
        // Given
        WeaverEngine mockEngine = mock(WeaverEngine.class);
        when(mockEngine.getOptions()).thenReturn(Arrays.asList());

        // When
        StoreDefinition result = OptionsParser.getLaraStoreDefinition(mockEngine);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("getXmlPersistence() should return XmlPersistence instance")
    void testGetXmlPersistence() {
        // Given
        StoreDefinition mockDefinition = mock(StoreDefinition.class);

        // When
        XmlPersistence result = OptionsParser.getXmlPersistence(mockDefinition);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("ExecutionMode enum should have all expected values")
    void testExecutionModeEnum() {
        // When
        ExecutionMode[] modes = ExecutionMode.values();

        // Then
        assertThat(modes).hasSize(3);
        assertThat(modes).contains(ExecutionMode.OPTIONS, ExecutionMode.CONFIG, ExecutionMode.CONFIG_OPTIONS);
    }

    @Test
    @DisplayName("ExecutionMode valueOf should work correctly")
    void testExecutionModeValueOf() {
        // When & Then
        assertThat(ExecutionMode.valueOf("OPTIONS")).isEqualTo(ExecutionMode.OPTIONS);
        assertThat(ExecutionMode.valueOf("CONFIG")).isEqualTo(ExecutionMode.CONFIG);
        assertThat(ExecutionMode.valueOf("CONFIG_OPTIONS")).isEqualTo(ExecutionMode.CONFIG_OPTIONS);
    }
}
