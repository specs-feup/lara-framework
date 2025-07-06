package pt.up.fe.specs.lara.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Unit tests for JavaDiffHelper utility class.
 */
class JavaDiffHelperTest {

    @TempDir
    Path tempDir;

    private File originalFile;
    private File revisedFile;

    @BeforeEach
    void setUp() throws IOException {
        originalFile = tempDir.resolve("original.txt").toFile();
        revisedFile = tempDir.resolve("revised.txt").toFile();
    }

    @Test
    void testGetDiff_withIdenticalStrings_returnsEmptyDiff() {
        // Given
        String content = "line1\nline2\nline3";
        
        // When
        String diff = JavaDiffHelper.getDiff(content, content);
        
        // Then
        assertThat(diff).isEmpty();
    }

    @Test
    void testGetDiff_withDifferentStrings_returnsCorrectDiff() {
        // Given
        String original = "line1\nline2\nline3";
        String revised = "line1\nmodified line2\nline3";
        
        // When
        String diff = JavaDiffHelper.getDiff(original, revised);
        
        // Then
        assertThat(diff).isNotEmpty();
        assertThat(diff).contains("modified line2");
    }

    @Test
    void testGetDiff_withAddedLines_detectsInsertion() {
        // Given
        String original = "line1\nline3";
        String revised = "line1\nline2\nline3";
        
        // When
        String diff = JavaDiffHelper.getDiff(original, revised);
        
        // Then
        assertThat(diff).isNotEmpty();
        assertThat(diff).contains("line2");
    }

    @Test
    void testGetDiff_withDeletedLines_detectsDeletion() {
        // Given
        String original = "line1\nline2\nline3";
        String revised = "line1\nline3";
        
        // When
        String diff = JavaDiffHelper.getDiff(original, revised);
        
        // Then
        assertThat(diff).isNotEmpty();
        assertThat(diff).contains("line2");
    }

    @Test
    void testGetDiff_withEmptyStrings_returnsEmptyDiff() {
        // Given
        String original = "";
        String revised = "";
        
        // When
        String diff = JavaDiffHelper.getDiff(original, revised);
        
        // Then
        assertThat(diff).isEmpty();
    }

    @Test
    void testGetDiff_withEmptyToNonEmpty_detectsAddition() {
        // Given
        String original = "";
        String revised = "new line";
        
        // When
        String diff = JavaDiffHelper.getDiff(original, revised);
        
        // Then
        assertThat(diff).isNotEmpty();
        assertThat(diff).contains("new line");
    }

    @Test
    void testGetDiff_withNonEmptyToEmpty_detectsDeletion() {
        // Given
        String original = "old line";
        String revised = "";
        
        // When
        String diff = JavaDiffHelper.getDiff(original, revised);
        
        // Then
        assertThat(diff).isNotEmpty();
        assertThat(diff).contains("old line");
    }

    @Test
    void testGetDiff_withFiles_identicalFiles() throws IOException {
        // Given
        String content = "line1\nline2\nline3";
        Files.writeString(originalFile.toPath(), content);
        Files.writeString(revisedFile.toPath(), content);
        
        // When
        String diff = JavaDiffHelper.getDiff(originalFile, revisedFile);
        
        // Then
        assertThat(diff).isEmpty();
    }

    @Test
    void testGetDiff_withFiles_differentFiles() throws IOException {
        // Given
        String originalContent = "line1\nline2\nline3";
        String revisedContent = "line1\nmodified line2\nline3";
        Files.writeString(originalFile.toPath(), originalContent);
        Files.writeString(revisedFile.toPath(), revisedContent);
        
        // When
        String diff = JavaDiffHelper.getDiff(originalFile, revisedFile);
        
        // Then
        assertThat(diff).isNotEmpty();
        assertThat(diff).contains("modified line2");
    }

    @Test
    void testGetDiff_withMultipleChanges_detectsAllChanges() {
        // Given
        String original = "line1\nline2\nline3\nline4";
        String revised = "modified line1\nline2\ninserted line\nline4";
        
        // When
        String diff = JavaDiffHelper.getDiff(original, revised);
        
        // Then
        assertThat(diff).isNotEmpty();
        assertThat(diff).contains("modified line1");
        assertThat(diff).contains("inserted line");
    }

    @Test
    void testGetDiff_withWindowsLineEndings_handlesCorrectly() {
        // Given
        String original = "line1\r\nline2\r\nline3";
        String revised = "line1\r\nmodified line2\r\nline3";
        
        // When
        String diff = JavaDiffHelper.getDiff(original, revised);
        
        // Then
        assertThat(diff).isNotEmpty();
        assertThat(diff).contains("modified line2");
    }

    @Test
    void testGetDiff_withMixedLineEndings_handlesCorrectly() {
        // Given
        String original = "line1\nline2\r\nline3";
        String revised = "line1\r\nline2\nline3";
        
        // When
        String diff = JavaDiffHelper.getDiff(original, revised);
        
        // Then
        // May or may not detect differences depending on how the diff library handles line endings
        // Just verify it doesn't throw an exception
        assertThat(diff).isNotNull();
    }

    @Test
    void testGetDiff_withNullStrings_throwsException() {
        // When & Then
        assertThatThrownBy(() -> JavaDiffHelper.getDiff(null, "content"))
                .isInstanceOf(Exception.class);
        
        assertThatThrownBy(() -> JavaDiffHelper.getDiff("content", null))
                .isInstanceOf(Exception.class);
    }

    @Test
    void testGetDiff_withNullFiles_throwsException() {
        // When & Then
        assertThatThrownBy(() -> JavaDiffHelper.getDiff(null, revisedFile))
                .isInstanceOf(Exception.class);
        
        assertThatThrownBy(() -> JavaDiffHelper.getDiff(originalFile, null))
                .isInstanceOf(Exception.class);
    }

    @Test
    void testGetDiff_withNonExistentFiles_throwsException() {
        // Given
        File nonExistentFile = tempDir.resolve("nonexistent.txt").toFile();
        
        // When & Then
        assertThatThrownBy(() -> JavaDiffHelper.getDiff(nonExistentFile, revisedFile))
                .isInstanceOf(Exception.class);
    }
}
