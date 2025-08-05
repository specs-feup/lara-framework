package pt.up.fe.specs.lara;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import pt.up.fe.specs.util.system.ProcessOutputAsString;

/**
 * Unit tests for LaraSystemTools utility class.
 */
class LaraSystemToolsTest {

    @TempDir
    Path tempDir;

    private String workingDirectory;

    @BeforeEach
    void setUp() {
        workingDirectory = tempDir.toString();
    }

    @Test
    void testRunCommand_stringCommand_withValidCommand() {
        // Given
        String command = "echo hello";
        boolean printToConsole = false;
        Integer timeoutNanos = 500000000; // 0.5 seconds
        
        // When
        ProcessOutputAsString result = LaraSystemTools.runCommand(command, workingDirectory, printToConsole, timeoutNanos);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getReturnValue()).isEqualTo(0);
        assertThat(result.getOutput().trim()).isEqualTo("hello");
    }

    @Test
    void testRunCommand_listCommand_withValidCommand() {
        // Given
        List<String> commandList = Arrays.asList("echo", "hello");
        boolean printToConsole = false;
        Integer timeoutNanos = 500000000; // 0.5 seconds
        
        // When
        ProcessOutputAsString result = LaraSystemTools.runCommand(commandList, workingDirectory, printToConsole, timeoutNanos);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getReturnValue()).isEqualTo(0);
        assertThat(result.getOutput().trim()).isEqualTo("hello");
    }

    @Test
    void testRunCommand_stringCommandWithLongTimeout_executesSuccessfully() {
        // Given
        String command = "echo hello";
        boolean printToConsole = false;
        Long timeoutNanos = 500000000L; // 0.5 seconds
        
        // When
        ProcessOutputAsString result = LaraSystemTools.runCommand(command, workingDirectory, printToConsole, timeoutNanos);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getReturnValue()).isEqualTo(0);
        assertThat(result.getOutput().trim()).isEqualTo("hello");
    }

    @Test
    void testRunCommand_listCommandWithLongTimeout_executesSuccessfully() {
        // Given
        List<String> commandList = Arrays.asList("echo", "hello");
        boolean printToConsole = false;
        Long timeoutNanos = 500000000L; // 0.5 seconds
        
        // When
        ProcessOutputAsString result = LaraSystemTools.runCommand(commandList, workingDirectory, printToConsole, timeoutNanos);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getReturnValue()).isEqualTo(0);
        assertThat(result.getOutput().trim()).isEqualTo("hello");
    }

    @Test
    void testRunCommand_withZeroTimeout_setsTimeoutToNull() {
        // Given
        String command = "echo hello";
        boolean printToConsole = false;
        Integer timeoutNanos = 0;
        
        // When
        ProcessOutputAsString result = LaraSystemTools.runCommand(command, workingDirectory, printToConsole, timeoutNanos);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getReturnValue()).isEqualTo(0);
    }

    @Test
    void testRunCommand_withNegativeTimeout_setsTimeoutToNull() {
        // Given
        String command = "echo hello";
        boolean printToConsole = false;
        Long timeoutNanos = -1L;
        
        // When
        ProcessOutputAsString result = LaraSystemTools.runCommand(command, workingDirectory, printToConsole, timeoutNanos);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getReturnValue()).isEqualTo(0);
    }

    @Test
    void testRunCommand_withNullTimeout_handlesGracefully() {
        // Given
        String command = "echo hello";
        boolean printToConsole = false;
        Integer timeoutNanos = null;
        
        // When
        ProcessOutputAsString result = LaraSystemTools.runCommand(command, workingDirectory, printToConsole, timeoutNanos);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getReturnValue()).isEqualTo(0);
    }

    @Test
    void testRunCommand_withInvalidCommand_returnsErrorResult() {
        // Given
        String command = "nonexistentcommand123456";
        boolean printToConsole = false;
        Integer timeoutNanos = 500000000; // 0.5 seconds
        
        // When
        ProcessOutputAsString result = LaraSystemTools.runCommand(command, workingDirectory, printToConsole, timeoutNanos);
        
        // Then
        assertThat(result).isNotNull();
        // Command not found typically returns exit code 127 on Unix systems
        assertThat(result.getReturnValue()).isEqualTo(127);
        // The system may redirect stderr to stdout for command not found errors
        // So we check that either stdout or stderr contains the error information
        boolean hasErrorInfo = !result.getOutput().isEmpty() || !result.getStdErr().isEmpty();
        assertThat(hasErrorInfo).isTrue();
        // Should contain some indication of the command not being found
        String combinedOutput = result.getOutput() + result.getStdErr();
        assertThat(combinedOutput.toLowerCase()).containsAnyOf("command not found", "not found", "not recognized");
    }

    @Test
    void testRunCommand_withComplexCommand_parsesCorrectly() {
        // Given - Command with quotes and spaces
        String command = "echo \"hello world\"";
        boolean printToConsole = false;
        Integer timeoutNanos = 500000000; // 0.5 seconds
        
        // When
        ProcessOutputAsString result = LaraSystemTools.runCommand(command, workingDirectory, printToConsole, timeoutNanos);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getReturnValue()).isEqualTo(0);
        assertThat(result.getOutput().trim()).isEqualTo("hello world");
    }

    @Test
    void testRunCommand_withDifferentWorkingDirectory_usesCorrectDirectory() {
        // Given
        String command = "pwd";
        boolean printToConsole = false;
        Integer timeoutNanos = 500000000; // 0.5 seconds
        
        // When
        ProcessOutputAsString result = LaraSystemTools.runCommand(command, workingDirectory, printToConsole, timeoutNanos);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getReturnValue()).isEqualTo(0);
        assertThat(result.getOutput().trim()).isEqualTo(new File(workingDirectory).getAbsolutePath());
    }

    @Test
    void testRunCommand_printToConsoleTrue_doesNotAffectOutput() {
        // Given
        String command = "echo hello";
        boolean printToConsole = true;
        Integer timeoutNanos = 500000000; // 0.5 seconds
        
        // When
        ProcessOutputAsString result = LaraSystemTools.runCommand(command, workingDirectory, printToConsole, timeoutNanos);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getReturnValue()).isEqualTo(0);
        assertThat(result.getOutput().trim()).isEqualTo("hello");
    }

    @Test
    void testRunCommand_commandWithArguments_executesCorrectly() {
        // Given
        List<String> commandList = Arrays.asList("echo", "arg1", "arg2", "arg3");
        boolean printToConsole = false;
        Integer timeoutNanos = 500000000; // 0.5 seconds
        
        // When
        ProcessOutputAsString result = LaraSystemTools.runCommand(commandList, workingDirectory, printToConsole, timeoutNanos);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getReturnValue()).isEqualTo(0);
        assertThat(result.getOutput().trim()).isEqualTo("arg1 arg2 arg3");
    }

    @Test
    void testRunCommand_emptyCommand_handlesGracefully() {
        // Given
        String command = "";
        boolean printToConsole = false;
        Integer timeoutNanos = 500000000; // 0.5 seconds
        
        // When
        ProcessOutputAsString result = LaraSystemTools.runCommand(command, workingDirectory, printToConsole, timeoutNanos);
        
        // Then
        assertThat(result).isNotNull();
        // Result may vary depending on system, but should not throw exception
    }

    @Test
    void testRunCommand_withInvalidWorkingDirectory_handlesGracefully() {
        // Given
        String command = "echo hello";
        String invalidWorkingDir = "/nonexistent/directory/path";
        boolean printToConsole = false;
        Integer timeoutNanos = 500000000; // 0.5 seconds
        
        // When
        ProcessOutputAsString result = LaraSystemTools.runCommand(command, invalidWorkingDir, printToConsole, timeoutNanos);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getReturnValue()).isEqualTo(-1);
        assertThat(result.getStdErr()).isNotEmpty();
    }
}
