package org.lara.interpreter.joptions.keys;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link FileList} - construction variants, encode/decode round trip using SpecsIo separators,
 * iterator and toString; equals/hashCode behavior.
 */
class FileListTest {

    @TempDir
    File tempDir;

    @Test
    void testDefaultConstructor() {
        FileList fileList = FileList.newInstance();
        assertThat(fileList.isEmpty()).isTrue();
        assertThat(fileList.getFiles()).isEmpty();
    }

    @Test
    void testConstructorWithFiles() {
        File file1 = new File(tempDir, "file1.txt");
        File file2 = new File(tempDir, "file2.txt");
        
        FileList fileList = FileList.newInstance(file1, file2);
        assertThat(fileList.isEmpty()).isFalse();
        assertThat(fileList.getFiles()).containsExactly(file1, file2);
    }

    @Test
    void testConstructorWithCollection() {
        File file1 = new File(tempDir, "file1.txt");
        File file2 = new File(tempDir, "file2.txt");
        List<File> files = Arrays.asList(file1, file2);
        
        FileList fileList = FileList.newInstance(files);
        assertThat(fileList.isEmpty()).isFalse();
        assertThat(fileList.getFiles()).containsExactly(file1, file2);
    }

    @Test
    void testConstructorWithEmptyCollection() {
        FileList fileList = FileList.newInstance(Collections.emptyList());
        assertThat(fileList.isEmpty()).isTrue();
        assertThat(fileList.getFiles()).isEmpty();
    }

    @Test
    void testConstructorWithNullString() {
        FileList fileList = FileList.newInstance((String) null);
        assertThat(fileList.isEmpty()).isTrue();
        assertThat(fileList.getFiles()).isEmpty();
    }

    @Test
    void testEncodeDecode() {
        File file1 = new File(tempDir, "file1.txt");
        File file2 = new File(tempDir, "file2.txt");
        FileList original = FileList.newInstance(file1, file2);
        
        // Encode to string
        String encoded = original.encode();
        assertThat(encoded).isNotEmpty();
        
        // Decode back to FileList
        FileList decoded = FileList.newInstance(encoded);
        
        // Should have same number of files
        assertThat(decoded.getFiles()).hasSameSizeAs(original.getFiles());
        
        // Files should be equivalent (by canonical path)
        List<String> originalPaths = original.getFiles().stream()
            .map(File::getAbsolutePath)
            .toList();
        List<String> decodedPaths = decoded.getFiles().stream()
            .map(File::getAbsolutePath)
            .toList();
        
        assertThat(decodedPaths).containsExactlyElementsOf(originalPaths);
    }

    @Test
    void testEncodeDecodeEmpty() {
        FileList empty = FileList.newInstance();
        
        String encoded = empty.encode();
        FileList decoded = FileList.newInstance(encoded);
        
        assertThat(decoded.isEmpty()).isTrue();
        assertThat(decoded.getFiles()).isEmpty();
    }

    @Test
    void testIterator() {
        File file1 = new File(tempDir, "file1.txt");
        File file2 = new File(tempDir, "file2.txt");
        FileList fileList = FileList.newInstance(file1, file2);
        
        Iterator<File> iterator = fileList.iterator();
        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.next()).isEqualTo(file1);
        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.next()).isEqualTo(file2);
        assertThat(iterator.hasNext()).isFalse();
    }

    @Test
    void testIteratorEmpty() {
        FileList empty = FileList.newInstance();
        Iterator<File> iterator = empty.iterator();
        assertThat(iterator.hasNext()).isFalse();
    }

    @Test
    void testToString() {
        File file1 = new File(tempDir, "file1.txt");
        File file2 = new File(tempDir, "file2.txt");
        FileList fileList = FileList.newInstance(file1, file2);
        
        String result = fileList.toString();
        assertThat(result).contains("file1.txt");
        assertThat(result).contains("file2.txt");
        // Should use universal path separator
        assertThat(result).contains(";"); // SpecsIo.getUniversalPathSeparator() is typically ';'
    }

    @Test
    void testToStringEmpty() {
        FileList empty = FileList.newInstance();
        String result = empty.toString();
        assertThat(result).isEmpty();
    }

    @Test
    void testEqualsAndHashCode() {
        File file1 = new File(tempDir, "file1.txt");
        File file2 = new File(tempDir, "file2.txt");
        
        FileList fileList1 = FileList.newInstance(file1, file2);
        FileList fileList2 = FileList.newInstance(file1, file2);
        FileList fileList3 = FileList.newInstance(file2, file1); // Different order
        FileList fileList4 = FileList.newInstance(file1);
        
        // Equals
        assertThat(fileList1).isEqualTo(fileList1); // Reflexive
        assertThat(fileList1).isEqualTo(fileList2); // Symmetric
        assertThat(fileList2).isEqualTo(fileList1);
        assertThat(fileList1).isNotEqualTo(fileList3); // Order matters
        assertThat(fileList1).isNotEqualTo(fileList4); // Different content
        assertThat(fileList1).isNotEqualTo(null);
        assertThat(fileList1).isNotEqualTo("not a FileList");
        
        // HashCode
        assertThat(fileList1.hashCode()).isEqualTo(fileList2.hashCode());
        // Note: Different objects may have same hashCode, but equal objects must have same hashCode
    }

    @Test
    void testEqualsWithEmptyLists() {
        FileList empty1 = FileList.newInstance();
        FileList empty2 = FileList.newInstance();
        
        assertThat(empty1).isEqualTo(empty2);
        assertThat(empty1.hashCode()).isEqualTo(empty2.hashCode());
    }
}