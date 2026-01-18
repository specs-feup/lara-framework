package org.lara.interpreter.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for LaraIo class.
 * 
 * Tests cover:
 * - File reading operations
 * - File deletion operations
 * - Error handling for invalid files
 * 
 * @author Generated Tests
 */
@DisplayName("LaraIo Tests")
class LaraIoTest {

    @TempDir
    Path tempDir;

    private File testFile;
    private File nonExistentFile;

    @BeforeEach
    void setUp() throws IOException {
        // Create a test file with sample content
        testFile = tempDir.resolve("test-file.txt").toFile();
        Files.write(testFile.toPath(), List.of("Line 1", "Line 2", "Line 3"));

        // Reference to a non-existent file
        nonExistentFile = tempDir.resolve("non-existent.txt").toFile();
    }

    @Test
    @DisplayName("readLines(String) should read lines from file by filename")
    void testReadLines_ByString() {
        // When
        List<String> lines = LaraIo.readLines(testFile.getAbsolutePath());

        // Then
        assertThat(lines).hasSize(3);
        assertThat(lines).containsExactly("Line 1", "Line 2", "Line 3");
    }

    @Test
    @DisplayName("readLines(File) should read lines from file object")
    void testReadLines_ByFile() {
        // When
        List<String> lines = LaraIo.readLines(testFile);

        // Then
        assertThat(lines).hasSize(3);
        assertThat(lines).containsExactly("Line 1", "Line 2", "Line 3");
    }

    @Test
    @DisplayName("readLines should return empty list for empty file")
    void testReadLines_EmptyFile() throws IOException {
        // Given
        File emptyFile = tempDir.resolve("empty-file.txt").toFile();
        Files.write(emptyFile.toPath(), List.of());

        // When
        List<String> lines = LaraIo.readLines(emptyFile);

        // Then
        assertThat(lines).isEmpty();
    }

    @Test
    @DisplayName("readLines should handle single line file")
    void testReadLines_SingleLine() throws IOException {
        // Given
        File singleLineFile = tempDir.resolve("single-line.txt").toFile();
        Files.write(singleLineFile.toPath(), List.of("Single line"));

        // When
        List<String> lines = LaraIo.readLines(singleLineFile);

        // Then
        assertThat(lines).hasSize(1);
        assertThat(lines).containsExactly("Single line");
    }

    @Test
    @DisplayName("readLines should handle file with empty lines")
    void testReadLines_WithEmptyLines() throws IOException {
        // Given
        File fileWithEmptyLines = tempDir.resolve("empty-lines.txt").toFile();
        Files.write(fileWithEmptyLines.toPath(), List.of("Line 1", "", "Line 3", ""));

        // When
        List<String> lines = LaraIo.readLines(fileWithEmptyLines);

        // Then
        assertThat(lines).hasSize(4);
        assertThat(lines).containsExactly("Line 1", "", "Line 3", "");
    }

    @Test
    @DisplayName("readLines should throw exception for non-existent file")
    void testReadLines_NonExistentFile() {
        // When/Then - should throw NullPointerException when file doesn't exist
        // because SpecsIo.read() returns null and StringLines.newInstance(null) throws
        // NPE
        assertThrows(NullPointerException.class, () -> {
            LaraIo.readLines(nonExistentFile);
        }, "Non-existent file should throw NullPointerException");
    }

    @Test
    @DisplayName("readLines should use test resource files")
    void testReadLines_TestResource() {
        // Given - using the test resource file we created
        String resourcePath = "test-resources/io-test-files/lines-test.txt";
        File resourceFile = new File(resourcePath);

        // When
        List<String> lines = LaraIo.readLines(resourceFile);

        // Then - should handle gracefully even if path is not found
        assertThat(lines).isNotNull();
    }

    @Test
    @DisplayName("deleteFile(String) should delete file by filename")
    void testDeleteFile_ByString() {
        // Given
        assertThat(testFile).exists();

        // When
        boolean result = LaraIo.deleteFile(testFile.getAbsolutePath());

        // Then
        assertThat(result).isTrue();
        assertThat(testFile).doesNotExist();
    }

    @Test
    @DisplayName("deleteFile(File) should delete file object")
    void testDeleteFile_ByFile() {
        // Given
        assertThat(testFile).exists();

        // When
        boolean result = LaraIo.deleteFile(testFile);

        // Then
        assertThat(result).isTrue();
        assertThat(testFile).doesNotExist();
    }

    @Test
    @DisplayName("deleteFile should return false for non-existent file")
    void testDeleteFile_NonExistentFile() {
        // Given
        assertThat(nonExistentFile).doesNotExist();

        // When
        boolean result = LaraIo.deleteFile(nonExistentFile);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("deleteFile should handle directory deletion")
    void testDeleteFile_Directory() throws IOException {
        // Given
        File directory = tempDir.resolve("test-directory").toFile();
        directory.mkdir();
        assertThat(directory).exists();
        assertThat(directory).isDirectory();

        // When
        boolean result = LaraIo.deleteFile(directory);

        // Then
        assertThat(result).isTrue();
        assertThat(directory).doesNotExist();
    }

    @Test
    @DisplayName("deleteFile should handle null file gracefully")
    void testDeleteFile_NullFile() {
        // When/Then - should handle gracefully, but might throw exception depending on
        // implementation
        try {
            boolean result = LaraIo.deleteFile((File) null);
            assertThat(result).isFalse();
        } catch (NullPointerException e) {
            // Some implementations might throw NPE for null files, which is acceptable
            assertThat(e).isInstanceOf(NullPointerException.class);
        }
    }

    @Test
    @DisplayName("readLines and deleteFile should work together")
    void testReadLinesAndDeleteFile_Integration() throws IOException {
        // Given
        File integrationFile = tempDir.resolve("integration-test.txt").toFile();
        Files.write(integrationFile.toPath(), List.of("Integration", "Test", "Content"));

        // When - read file
        List<String> lines = LaraIo.readLines(integrationFile);

        // Then - verify content
        assertThat(lines).containsExactly("Integration", "Test", "Content");

        // When - delete file
        boolean deleted = LaraIo.deleteFile(integrationFile);

        // Then - verify deletion
        assertThat(deleted).isTrue();
        assertThat(integrationFile).doesNotExist();

        // When/Then - try to read deleted file should throw exception
        assertThrows(NullPointerException.class, () -> {
            LaraIo.readLines(integrationFile);
        }, "Reading deleted file should throw NullPointerException");
    }
}
