package org.lara.interpreter.cli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.lara.interpreter.weaver.options.OptionArguments;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for CLIOption enum.
 * 
 * Tests cover:
 * - Enum value properties
 * - Option lookup methods
 * - Option argument handling
 * - Data key associations
 * 
 * @author Generated Tests
 */
@DisplayName("CLIOption Tests")
class CLIOptionTest {

    @Test
    @DisplayName("help option should have correct properties")
    void testHelpOption() {
        // Given
        CLIOption helpOption = CLIOption.help;

        // Then
        assertThat(helpOption.shortOption()).isEqualTo("h");
        assertThat(helpOption.description()).isEqualTo("print this message");
        assertThat(helpOption.args()).isEqualTo(OptionArguments.NO_ARGS);
        assertThat(helpOption.longOption()).isEqualTo("help");
        assertThat(helpOption.dataKey()).isNotNull();
    }

    @Test
    @DisplayName("version option should have correct properties")
    void testVersionOption() {
        // Given
        CLIOption versionOption = CLIOption.version;

        // Then
        assertThat(versionOption.shortOption()).isEqualTo("v");
        assertThat(versionOption.description()).isEqualTo("print version information and exit");
        assertThat(versionOption.args()).isEqualTo(OptionArguments.NO_ARGS);
        assertThat(versionOption.longOption()).isEqualTo("version");
        assertThat(versionOption.dataKey()).isNull(); // Version option has no data key
    }

    @Test
    @DisplayName("javascript option should have correct properties")
    void testJavascriptOption() {
        // Given
        CLIOption jsOption = CLIOption.javascript;

        // Then
        assertThat(jsOption.shortOption()).isEqualTo("j");
        assertThat(jsOption.description())
                .isEqualTo("show the javascript output in the same stream as the application's output");
        assertThat(jsOption.args()).isEqualTo(OptionArguments.NO_ARGS);
        assertThat(jsOption.longOption()).isEqualTo("javascript");
        assertThat(jsOption.dataKey()).isNotNull();
    }

    @Test
    @DisplayName("debug option should have correct properties")
    void testDebugOption() {
        // Given
        CLIOption debugOption = CLIOption.debug;

        // Then
        assertThat(debugOption.shortOption()).isEqualTo("d");
        assertThat(debugOption.description()).isEqualTo("show all process information");
        assertThat(debugOption.args()).isEqualTo(OptionArguments.NO_ARGS);
        assertThat(debugOption.longOption()).isEqualTo("debug");
        assertThat(debugOption.dataKey()).isNotNull();
    }

    @Test
    @DisplayName("argv option should have correct properties with arguments")
    void testArgvOption() {
        // Given
        CLIOption argvOption = CLIOption.argv;

        // Then
        assertThat(argvOption.shortOption()).isEqualTo("av");
        assertThat(argvOption.description())
                .isEqualTo("arguments for the main aspect. Supports passing a .properties file with the arguments");
        assertThat(argvOption.args()).isEqualTo(OptionArguments.ONE_ARG);
        assertThat(argvOption.longOption()).isEqualTo("argv");
        assertThat(argvOption.argName()).isEqualTo("arguments");
        assertThat(argvOption.dataKey()).isNotNull();
    }

    @Test
    @DisplayName("output option should have correct properties")
    void testOutputOption() {
        // Given
        CLIOption outputOption = CLIOption.output;

        // Then
        assertThat(outputOption.shortOption()).isEqualTo("o");
        assertThat(outputOption.description()).isEqualTo("change output dir");
        assertThat(outputOption.args()).isEqualTo(OptionArguments.ONE_ARG);
        assertThat(outputOption.longOption()).isEqualTo("output");
        assertThat(outputOption.argName()).isEqualTo("dir");
        assertThat(outputOption.dataKey()).isNotNull();
    }

    @Test
    @DisplayName("workspace option should have correct properties")
    void testWorkspaceOption() {
        // Given
        CLIOption workspaceOption = CLIOption.workspace;

        // Then
        assertThat(workspaceOption.shortOption()).isEqualTo("p");
        assertThat(workspaceOption.description()).isEqualTo("change the working directory");
        assertThat(workspaceOption.args()).isEqualTo(OptionArguments.ONE_ARG);
        assertThat(workspaceOption.longOption()).isEqualTo("workspace");
        assertThat(workspaceOption.argName()).isEqualTo("dir");
        assertThat(workspaceOption.dataKey()).isNotNull();
    }

    @Test
    @DisplayName("log option should have optional arguments")
    void testLogOption() {
        // Given
        CLIOption logOption = CLIOption.log;

        // Then
        assertThat(logOption.shortOption()).isEqualTo("l");
        assertThat(logOption.description())
                .isEqualTo("outputs to a log file. If file ends in .zip, compresses the file");
        assertThat(logOption.args()).isEqualTo(OptionArguments.OPTIONAL_ARG);
        assertThat(logOption.longOption()).isEqualTo("log");
        assertThat(logOption.argName()).isEqualTo("fileName");
        assertThat(logOption.dataKey()).isNotNull();
    }

    @Test
    @DisplayName("restrict option should have correct properties")
    void testRestrictOption() {
        // Given
        CLIOption restrictOption = CLIOption.restrict;

        // Then
        assertThat(restrictOption.shortOption()).isEqualTo("rm");
        assertThat(restrictOption.description()).isEqualTo("Restrict mode (some Java classes are not allowed)");
        assertThat(restrictOption.args()).isEqualTo(OptionArguments.NO_ARGS);
        assertThat(restrictOption.longOption()).isEqualTo("restrict");
        assertThat(restrictOption.argName()).isEqualTo("restrict");
        assertThat(restrictOption.dataKey()).isNotNull();
    }

    @Test
    @DisplayName("getArgumentByShortName should return correct option")
    void testGetArgumentByShortName() {
        // When/Then
        assertThat(CLIOption.getArgumentByShortName("h")).isEqualTo(CLIOption.help);
        assertThat(CLIOption.getArgumentByShortName("v")).isEqualTo(CLIOption.version);
        assertThat(CLIOption.getArgumentByShortName("j")).isEqualTo(CLIOption.javascript);
        assertThat(CLIOption.getArgumentByShortName("d")).isEqualTo(CLIOption.debug);
        assertThat(CLIOption.getArgumentByShortName("o")).isEqualTo(CLIOption.output);
        assertThat(CLIOption.getArgumentByShortName("p")).isEqualTo(CLIOption.workspace);
        assertThat(CLIOption.getArgumentByShortName("l")).isEqualTo(CLIOption.log);
        assertThat(CLIOption.getArgumentByShortName("av")).isEqualTo(CLIOption.argv);
        assertThat(CLIOption.getArgumentByShortName("rm")).isEqualTo(CLIOption.restrict);
        assertThat(CLIOption.getArgumentByShortName("jp")).isEqualTo(CLIOption.jarpaths);
    }

    @Test
    @DisplayName("getArgumentByShortName should return null for unknown short name")
    void testGetArgumentByShortName_Unknown() {
        // When/Then
        assertThat(CLIOption.getArgumentByShortName("unknown")).isNull();
        assertThat(CLIOption.getArgumentByShortName("xyz")).isNull();
        assertThat(CLIOption.getArgumentByShortName("")).isNull();
    }

    @Test
    @DisplayName("contains should return true for valid option names")
    void testContains() {
        // When/Then
        assertThat(CLIOption.contains("help")).isTrue();
        assertThat(CLIOption.contains("version")).isTrue();
        assertThat(CLIOption.contains("javascript")).isTrue();
        assertThat(CLIOption.contains("debug")).isTrue();
        assertThat(CLIOption.contains("argv")).isTrue();
        assertThat(CLIOption.contains("output")).isTrue();
        assertThat(CLIOption.contains("workspace")).isTrue();
        assertThat(CLIOption.contains("workspace_extra")).isTrue();
        assertThat(CLIOption.contains("main")).isTrue();
        assertThat(CLIOption.contains("log")).isTrue();
        assertThat(CLIOption.contains("restrict")).isTrue();
        assertThat(CLIOption.contains("jarpaths")).isTrue();
    }

    @Test
    @DisplayName("contains should return false for invalid option names")
    void testContains_Invalid() {
        // When/Then
        assertThat(CLIOption.contains("unknown")).isFalse();
        assertThat(CLIOption.contains("invalid")).isFalse();
        assertThat(CLIOption.contains("")).isFalse();
        assertThat(CLIOption.contains("h")).isFalse(); // Short names are not valid long names
        assertThat(CLIOption.contains("v")).isFalse();
    }

    @Test
    @DisplayName("containsShort should return true for valid short options")
    void testContainsShort() {
        // When/Then
        assertThat(CLIOption.containsShort("h")).isTrue();
        assertThat(CLIOption.containsShort("v")).isTrue();
        assertThat(CLIOption.containsShort("j")).isTrue();
        assertThat(CLIOption.containsShort("d")).isTrue();
        assertThat(CLIOption.containsShort("av")).isTrue();
        assertThat(CLIOption.containsShort("o")).isTrue();
        assertThat(CLIOption.containsShort("p")).isTrue();
        assertThat(CLIOption.containsShort("pe")).isTrue();
        assertThat(CLIOption.containsShort("m")).isTrue();
        assertThat(CLIOption.containsShort("l")).isTrue();
        assertThat(CLIOption.containsShort("rm")).isTrue();
        assertThat(CLIOption.containsShort("jp")).isTrue();
    }

    @Test
    @DisplayName("containsShort should return false for invalid short options")
    void testContainsShort_Invalid() {
        // When/Then
        assertThat(CLIOption.containsShort("unknown")).isFalse();
        assertThat(CLIOption.containsShort("xyz")).isFalse();
        assertThat(CLIOption.containsShort("")).isFalse();
        assertThat(CLIOption.containsShort("help")).isFalse(); // Long names are not valid short names
        assertThat(CLIOption.containsShort("version")).isFalse();
    }

    @Test
    @DisplayName("all enum values should have non-null descriptions")
    void testAllOptionsHaveDescriptions() {
        // When/Then
        for (CLIOption option : CLIOption.values()) {
            assertThat(option.description()).isNotNull();
            assertThat(option.description()).isNotEmpty();
        }
    }

    @Test
    @DisplayName("all enum values should have valid short options")
    void testAllOptionsHaveShortOptions() {
        // When/Then
        for (CLIOption option : CLIOption.values()) {
            assertThat(option.shortOption()).isNotNull();
            assertThat(option.shortOption()).isNotEmpty();
        }
    }

    @Test
    @DisplayName("all enum values should have valid long options")
    void testAllOptionsHaveLongOptions() {
        // When/Then
        for (CLIOption option : CLIOption.values()) {
            assertThat(option.longOption()).isNotNull();
            assertThat(option.longOption()).isNotEmpty();
            assertThat(option.longOption()).isEqualTo(option.name());
        }
    }

    @Test
    @DisplayName("all enum values should have valid argument specifications")
    void testAllOptionsHaveValidArgs() {
        // When/Then
        for (CLIOption option : CLIOption.values()) {
            assertThat(option.args()).isNotNull();
            assertThat(option.args()).isIn(
                    OptionArguments.NO_ARGS,
                    OptionArguments.ONE_ARG,
                    OptionArguments.OPTIONAL_ARG);
        }
    }

    @Test
    @DisplayName("short options should be unique")
    void testShortOptionsAreUnique() {
        // Given
        CLIOption[] options = CLIOption.values();

        // When/Then
        for (int i = 0; i < options.length; i++) {
            for (int j = i + 1; j < options.length; j++) {
                assertThat(options[i].shortOption())
                        .as("Short options should be unique: %s and %s", options[i], options[j])
                        .isNotEqualTo(options[j].shortOption());
            }
        }
    }

    @Test
    @DisplayName("jarpaths option should have correct complex argument name")
    void testJarPathsOption() {
        // Given
        CLIOption jarPathsOption = CLIOption.jarpaths;

        // Then
        assertThat(jarPathsOption.shortOption()).isEqualTo("jp");
        assertThat(jarPathsOption.description())
                .isEqualTo("JAR files that will be added to a separate classpath and will be accessible in scripts");
        assertThat(jarPathsOption.args()).isEqualTo(OptionArguments.ONE_ARG);
        assertThat(jarPathsOption.longOption()).isEqualTo("jarpaths");
        assertThat(jarPathsOption.argName()).isEqualTo("dir1/file1[;dir2/file2]*");
        assertThat(jarPathsOption.dataKey()).isNotNull();
    }
}
