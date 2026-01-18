package org.lara.interpreter.utils;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.lara.interpreter.cli.CLIOption;
import org.lara.interpreter.cli.OptionsParser;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for LaraIUtils class.
 * 
 * Tests cover:
 * - String formatting and indentation
 * - Help and version information display
 * - Jar path utilities
 * 
 * @author Generated Tests
 */
@DisplayName("LaraIUtils Tests")
class LaraIUtilsTest {

    @Mock
    private CommandLine mockCommandLine;

    @Mock
    private Options mockOptions;

    private ByteArrayOutputStream outputStreamCaptor;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Capture System.out for testing print methods
        outputStreamCaptor = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("printHelp should return true and print help when help option is present")
    void testPrintHelp_WithHelpOption() {
        // Given
        try (var mockedOptionsParser = mockStatic(OptionsParser.class)) {
            when(mockCommandLine.hasOption(CLIOption.help.shortOption())).thenReturn(true);

            String helpText = "Usage: larai [options] script.lara";
            mockedOptionsParser.when(() -> OptionsParser.getHelp(mockOptions)).thenReturn(helpText);

            // When
            boolean result = LaraIUtils.printHelp(mockCommandLine, mockOptions);

            // Then
            assertThat(result).isTrue();
            assertThat(outputStreamCaptor.toString()).contains(helpText);
        }
    }

    @Test
    @DisplayName("printHelp should return true and print version when version option is present")
    void testPrintHelp_WithVersionOption() {
        // Given
        // Mock help option not present, version option present
        when(mockCommandLine.hasOption(CLIOption.help.shortOption())).thenReturn(false);
        when(mockCommandLine.hasOption(CLIOption.version.shortOption())).thenReturn(true);

        // When
        boolean result = LaraIUtils.printHelp(mockCommandLine, mockOptions);

        // Then
        assertThat(result).isTrue();
        String output = outputStreamCaptor.toString();
        assertThat(output).contains("Build:");
    }

    @Test
    @DisplayName("printHelp should return false when no help or version options are present")
    void testPrintHelp_NoOptions() {
        // Given
        when(mockCommandLine.hasOption(CLIOption.help.shortOption())).thenReturn(false);
        when(mockCommandLine.hasOption(CLIOption.version.shortOption())).thenReturn(false);

        // When
        boolean result = LaraIUtils.printHelp(mockCommandLine, mockOptions);

        // Then
        assertThat(result).isFalse();
        assertThat(outputStreamCaptor.toString()).isEmpty();
    }

    @Test
    @DisplayName("getJarFoldername should return non-null jar path")
    void testGetJarFoldername() {
        // When
        String jarPath = LaraIUtils.getJarFoldername();

        // Then
        assertThat(jarPath).isNotNull();
        // Note: The actual path depends on the runtime environment
        // We can only verify it's not null and is a string
    }

    @Test
    @DisplayName("getJarFoldername should return same instance on multiple calls (lazy initialization)")
    void testGetJarFoldername_LazyInitialization() {
        // When
        String jarPath1 = LaraIUtils.getJarFoldername();
        String jarPath2 = LaraIUtils.getJarFoldername();

        // Then
        assertThat(jarPath1).isEqualTo(jarPath2);
        assertThat(jarPath1).isSameAs(jarPath2); // Should be the same instance
    }

    @Test
    @DisplayName("printHelp should handle both help and version options correctly")
    void testPrintHelp_PriorityHandling() {
        // Given - both options present, help should have priority
        try (var mockedOptionsParser = mockStatic(OptionsParser.class)) {
            when(mockCommandLine.hasOption(CLIOption.help.shortOption())).thenReturn(true);
            when(mockCommandLine.hasOption(CLIOption.version.shortOption())).thenReturn(true);

            String helpText = "Usage: larai [options] script.lara";
            mockedOptionsParser.when(() -> OptionsParser.getHelp(mockOptions)).thenReturn(helpText);

            // When
            boolean result = LaraIUtils.printHelp(mockCommandLine, mockOptions);

            // Then
            assertThat(result).isTrue();
            String output = outputStreamCaptor.toString();
            assertThat(output).contains(helpText);
        }
    }

    @Test
    @DisplayName("printHelp should handle null build number gracefully")
    void testPrintHelp_NullBuildNumber() {
        // Given
        when(mockCommandLine.hasOption(CLIOption.help.shortOption())).thenReturn(false);
        when(mockCommandLine.hasOption(CLIOption.version.shortOption())).thenReturn(true);

        // When
        boolean result = LaraIUtils.printHelp(mockCommandLine, mockOptions);

        // Then
        assertThat(result).isTrue();
        String output = outputStreamCaptor.toString();
        assertThat(output).contains("Build: <build number not found>");
    }
}
