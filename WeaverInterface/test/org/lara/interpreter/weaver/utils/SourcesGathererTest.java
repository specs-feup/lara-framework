package org.lara.interpreter.weaver.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.lara.interpreter.weaver.fixtures.FsTestUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for SourcesGatherer - Phase 6 implementation.
 * 
 * Tests validate file/source gathering logic including:
 * - Building mapping from input roots to discovered files
 * - Filtering by extensions
 * - Recursive directory traversal
 * - Handling invalid paths without exceptions
 */
public class SourcesGathererTest {

    @Test
    @DisplayName("Should handle empty inputs without failure")
    void testEmptyInputs(@TempDir File tempDir) {
        // Test with empty list of sources
        List<File> emptySources = Collections.emptyList();
        Collection<String> extensions = Arrays.asList("java", "js");
        
        SourcesGatherer gatherer = SourcesGatherer.build(emptySources, extensions);
        
        assertThat(gatherer.getSourceFiles()).isEmpty();
    }

    @Test
    @DisplayName("Should handle empty extensions without failure")
    void testEmptyExtensions(@TempDir File tempDir) throws IOException {
        // Create a test file
        File testFile = FsTestUtils.writeFile(tempDir, "test.java", "content");
        
        List<File> sources = Arrays.asList(testFile);
        Collection<String> emptyExtensions = Collections.emptyList();
        
        SourcesGatherer gatherer = SourcesGatherer.build(sources, emptyExtensions);
        
        // Should be empty because no extensions match
        assertThat(gatherer.getSourceFiles()).isEmpty();
    }

    @Test
    @DisplayName("Should discover single files with matching extensions")
    void testSingleFileDiscovery(@TempDir File tempDir) throws IOException {
        // Create test files with different extensions
        File javaFile = FsTestUtils.writeFile(tempDir, "Test.java", "public class Test {}");
        File jsFile = FsTestUtils.writeFile(tempDir, "test.js", "console.log('test')");
        File txtFile = FsTestUtils.writeFile(tempDir, "readme.txt", "readme content");
        
        // Test with java and js extensions
        List<File> sources = Arrays.asList(javaFile, jsFile, txtFile);
        Collection<String> extensions = Arrays.asList("java", "js");
        
        SourcesGatherer gatherer = SourcesGatherer.build(sources, extensions);
        Map<File, File> sourceFiles = gatherer.getSourceFiles();
        
        assertThat(sourceFiles).hasSize(2);
        assertThat(sourceFiles).containsKey(javaFile);
        assertThat(sourceFiles).containsKey(jsFile);
        assertThat(sourceFiles).doesNotContainKey(txtFile);
        
        // Each file should map to itself as the original source
        assertThat(sourceFiles.get(javaFile)).isEqualTo(javaFile);
        assertThat(sourceFiles.get(jsFile)).isEqualTo(jsFile);
    }

    @Test
    @DisplayName("Should recursively traverse directories and filter by extensions")
    void testRecursiveDirectoryTraversal(@TempDir File tempDir) throws IOException {
        // Create directory structure:
        // tempDir/
        //   ├── src/
        //   │   ├── Main.java
        //   │   └── util/
        //   │       └── Helper.java
        //   ├── test/
        //   │   └── TestMain.java
        //   ├── script.js
        //   └── README.md
        
        File srcDir = FsTestUtils.mkdir(tempDir, "src");
        File utilDir = FsTestUtils.mkdir(srcDir, "util");
        File testDir = FsTestUtils.mkdir(tempDir, "test");
        
        File mainJava = FsTestUtils.writeFile(srcDir, "Main.java", "public class Main {}");
        File helperJava = FsTestUtils.writeFile(utilDir, "Helper.java", "public class Helper {}");
        File testJava = FsTestUtils.writeFile(testDir, "TestMain.java", "public class TestMain {}");
        File scriptJs = FsTestUtils.writeFile(tempDir, "script.js", "console.log('script')");
        File readmeMd = FsTestUtils.writeFile(tempDir, "README.md", "# README");
        
        // Test recursive traversal starting from tempDir
        List<File> sources = Arrays.asList(tempDir);
        Collection<String> extensions = Arrays.asList("java", "js");
        
        SourcesGatherer gatherer = SourcesGatherer.build(sources, extensions);
        Map<File, File> sourceFiles = gatherer.getSourceFiles();
        
        // Should find all .java and .js files but not .md
        assertThat(sourceFiles).hasSize(4);
        assertThat(sourceFiles.keySet()).containsExactlyInAnyOrder(
            mainJava, helperJava, testJava, scriptJs
        );
        assertThat(sourceFiles.keySet()).doesNotContain(readmeMd);
        
        // All files should map back to the original tempDir since that was the source
        for (File originalSource : sourceFiles.values()) {
            assertThat(originalSource).isEqualTo(tempDir);
        }
    }

    @Test
    @DisplayName("Should handle mixed files and directories as input roots")
    void testMixedFilesAndDirectories(@TempDir File tempDir) throws IOException {
        // Create structure:
        // tempDir/
        //   ├── direct.java (will be passed as individual file)
        //   └── subdir/
        //       ├── indirect.java
        //       └── script.js
        
        File directFile = FsTestUtils.writeFile(tempDir, "direct.java", "class Direct {}");
        File subDir = FsTestUtils.mkdir(tempDir, "subdir");
        File indirectFile = FsTestUtils.writeFile(subDir, "indirect.java", "class Indirect {}");
        File scriptFile = FsTestUtils.writeFile(subDir, "script.js", "console.log('script')");
        
        // Mix individual files and directories
        List<File> sources = Arrays.asList(directFile, subDir);
        Collection<String> extensions = Arrays.asList("java", "js");
        
        SourcesGatherer gatherer = SourcesGatherer.build(sources, extensions);
        Map<File, File> sourceFiles = gatherer.getSourceFiles();
        
        assertThat(sourceFiles).hasSize(3);
        assertThat(sourceFiles.keySet()).containsExactlyInAnyOrder(
            directFile, indirectFile, scriptFile
        );
        
        // directFile should map to itself
        assertThat(sourceFiles.get(directFile)).isEqualTo(directFile);
        
        // Files from subDir should map to subDir
        assertThat(sourceFiles.get(indirectFile)).isEqualTo(subDir);
        assertThat(sourceFiles.get(scriptFile)).isEqualTo(subDir);
    }

    @Test
    @DisplayName("Should handle multiple root sources correctly")
    void testMultipleRootSources(@TempDir File tempDir) throws IOException {
        // Create two separate source directories
        File src1 = FsTestUtils.mkdir(tempDir, "src1");
        File src2 = FsTestUtils.mkdir(tempDir, "src2");
        
        File file1 = FsTestUtils.writeFile(src1, "File1.java", "class File1 {}");
        File file2 = FsTestUtils.writeFile(src2, "File2.java", "class File2 {}");
        
        List<File> sources = Arrays.asList(src1, src2);
        Collection<String> extensions = Arrays.asList("java");
        
        SourcesGatherer gatherer = SourcesGatherer.build(sources, extensions);
        Map<File, File> sourceFiles = gatherer.getSourceFiles();
        
        assertThat(sourceFiles).hasSize(2);
        
        // Each file should map to its respective original source directory
        assertThat(sourceFiles.get(file1)).isEqualTo(src1);
        assertThat(sourceFiles.get(file2)).isEqualTo(src2);
    }

    @Test
    @DisplayName("Should ignore invalid paths without throwing exceptions")
    void testInvalidPaths(@TempDir File tempDir) throws IOException {
        // Create a valid file and a non-existent file
        File validFile = FsTestUtils.writeFile(tempDir, "valid.java", "class Valid {}");
        File nonExistentFile = new File(tempDir, "nonexistent.java");
        
        // Include both valid and invalid paths
        List<File> sources = Arrays.asList(validFile, nonExistentFile);
        Collection<String> extensions = Arrays.asList("java");
        
        // Should not throw exception
        assertThatCode(() -> {
            SourcesGatherer gatherer = SourcesGatherer.build(sources, extensions);
            Map<File, File> sourceFiles = gatherer.getSourceFiles();
            
            // Should only contain the valid file
            assertThat(sourceFiles).hasSize(1);
            assertThat(sourceFiles).containsKey(validFile);
            assertThat(sourceFiles).doesNotContainKey(nonExistentFile);
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should handle case-sensitive extension matching")
    void testExtensionCaseSensitivity(@TempDir File tempDir) throws IOException {
        File javaFile = FsTestUtils.writeFile(tempDir, "Test.java", "class Test {}");
        File JAVAFile = FsTestUtils.writeFile(tempDir, "Test.JAVA", "class Test {}");
        
        List<File> sources = Arrays.asList(javaFile, JAVAFile);
        Collection<String> extensions = Arrays.asList("java"); // lowercase
        
        SourcesGatherer gatherer = SourcesGatherer.build(sources, extensions);
        Map<File, File> sourceFiles = gatherer.getSourceFiles();
        
        // Should only match the lowercase extension
        assertThat(sourceFiles).hasSize(1);
        assertThat(sourceFiles).containsKey(javaFile);
        assertThat(sourceFiles).doesNotContainKey(JAVAFile);
    }

    @Test
    @DisplayName("Should preserve order of discovered files")
    void testPreservesOrder(@TempDir File tempDir) throws IOException {
        // SourcesGatherer uses LinkedHashMap, so order should be preserved
        File file1 = FsTestUtils.writeFile(tempDir, "A.java", "class A {}");
        File file2 = FsTestUtils.writeFile(tempDir, "B.java", "class B {}");
        File file3 = FsTestUtils.writeFile(tempDir, "C.java", "class C {}");
        
        // Pass files in specific order
        List<File> sources = Arrays.asList(file1, file3, file2);
        Collection<String> extensions = Arrays.asList("java");
        
        SourcesGatherer gatherer = SourcesGatherer.build(sources, extensions);
        Map<File, File> sourceFiles = gatherer.getSourceFiles();
        
        // Should preserve the order they were added
        List<File> keyOrder = new ArrayList<>(sourceFiles.keySet());
        assertThat(keyOrder).containsExactly(file1, file3, file2);
    }

    @Test
    @DisplayName("Should handle files with no extension")
    void testFilesWithNoExtension(@TempDir File tempDir) throws IOException {
        File fileWithExt = FsTestUtils.writeFile(tempDir, "with.java", "class With {}");
        File fileWithoutExt = FsTestUtils.writeFile(tempDir, "without", "content");
        
        List<File> sources = Arrays.asList(fileWithExt, fileWithoutExt);
        Collection<String> extensions = Arrays.asList("java");
        
        SourcesGatherer gatherer = SourcesGatherer.build(sources, extensions);
        Map<File, File> sourceFiles = gatherer.getSourceFiles();
        
        // Should only include file with matching extension
        assertThat(sourceFiles).hasSize(1);
        assertThat(sourceFiles).containsKey(fileWithExt);
        assertThat(sourceFiles).doesNotContainKey(fileWithoutExt);
    }

    @Test
    @DisplayName("Should handle empty directories")
    void testEmptyDirectories(@TempDir File tempDir) {
        File emptyDir = FsTestUtils.mkdir(tempDir, "emptyDir");
        
        List<File> sources = Arrays.asList(emptyDir);
        Collection<String> extensions = Arrays.asList("java");
        
        SourcesGatherer gatherer = SourcesGatherer.build(sources, extensions);
        Map<File, File> sourceFiles = gatherer.getSourceFiles();
        
        // Should handle empty directories without issues
        assertThat(sourceFiles).isEmpty();
    }
}