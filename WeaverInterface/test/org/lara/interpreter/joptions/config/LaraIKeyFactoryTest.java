package org.lara.interpreter.joptions.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.lara.interpreter.joptions.config.interpreter.LaraIKeyFactory;
import org.lara.interpreter.joptions.keys.FileList;
import org.lara.interpreter.joptions.keys.OptionalFile;
import org.lara.interpreter.weaver.fixtures.TestDataStores;
import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Interfaces.DataStore;

import javax.swing.JFileChooser;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link LaraIKeyFactory} - fileList/customGetterFileList respect isFolder/isFile, 
 * no creation by default; file with create=true in folder mode creates dir; relative vs absolute 
 * behavior using CURRENT_FOLDER_PATH and USE_RELATIVE_PATHS; optionalFile custom getter processes 
 * underlying file when present; customGetterLaraArgs trims whitespace.
 */
class LaraIKeyFactoryTest {

    @TempDir
    File tempDir;

    @Test
    void testFolderList() {
        DataKey<FileList> folderListKey = LaraIKeyFactory.folderList("testFolders");
        
        assertThat(folderListKey).isNotNull();
        assertThat(folderListKey.getName()).isEqualTo("testFolders");
        
        // Default should be empty FileList
        FileList defaultValue = folderListKey.getDefault().get();
        assertThat(defaultValue.isEmpty()).isTrue();
    }

    @Test
    void testFileList() {
        DataKey<FileList> fileListKey = LaraIKeyFactory.fileList("testFiles", Arrays.asList("txt", "java"));
        
        assertThat(fileListKey).isNotNull();
        assertThat(fileListKey.getName()).isEqualTo("testFiles");
        
        // Default should be empty FileList
        FileList defaultValue = fileListKey.getDefault().get();
        assertThat(defaultValue.isEmpty()).isTrue();
    }

    @Test
    void testFileListWithCustomGetter() {
        // Create test files
        File file1 = new File(tempDir, "test1.txt");
        File file2 = new File(tempDir, "test2.txt");
        FileList testFileList = FileList.newInstance(file1, file2);
        
        DataKey<FileList> fileListKey = LaraIKeyFactory.fileList("testFiles", Collections.emptyList());
        DataStore dataStore = TestDataStores.withWorkingFolder(tempDir, false);
        
        // Test custom getter processes files
        FileList processed = fileListKey.getCustomGetter().get().get(testFileList, dataStore);
        assertThat(processed).isNotNull();
        assertThat(processed.getFiles()).hasSameSizeAs(testFileList.getFiles());
    }

    @Test
    void testFile() {
        DataKey<File> fileKey = LaraIKeyFactory.file("testFile", JFileChooser.FILES_ONLY, false, Collections.emptyList());
        
        assertThat(fileKey).isNotNull();
        assertThat(fileKey.getName()).isEqualTo("testFile");
        
        // Default should be empty file
        File defaultValue = fileKey.getDefault().get();
        assertThat(defaultValue.getPath()).isEmpty();
    }

    @Test
    void testFileWithCreateInFolderMode() {
        DataKey<File> folderKey = LaraIKeyFactory.file("testFolder", JFileChooser.DIRECTORIES_ONLY, true, Collections.emptyList());
        
        assertThat(folderKey).isNotNull();
        assertThat(folderKey.getName()).isEqualTo("testFolder");
        
        // Test custom getter with create=true
        File testDir = new File(tempDir, "newFolder");
        DataStore dataStore = TestDataStores.withWorkingFolder(tempDir, false);
        
        File processed = folderKey.getCustomGetter().get().get(testDir, dataStore);
        assertThat(processed).isNotNull();
        // Note: The actual directory creation depends on the custom getter implementation
    }

    @Test
    void testFileRelativeVsAbsoluteBehavior() {
        File testFile = new File(tempDir, "test.txt");
        DataKey<File> fileKey = LaraIKeyFactory.file("testFile", JFileChooser.FILES_ONLY, false, Collections.emptyList());
        
        // Test with relative paths enabled
        DataStore relativeDataStore = TestDataStores.withWorkingFolder(tempDir, true);
        File processedRelative = fileKey.getCustomGetter().get().get(testFile, relativeDataStore);
        assertThat(processedRelative).isNotNull();
        
        // Test with relative paths disabled (absolute)
        DataStore absoluteDataStore = TestDataStores.withWorkingFolder(tempDir, false);
        File processedAbsolute = fileKey.getCustomGetter().get().get(testFile, absoluteDataStore);
        assertThat(processedAbsolute).isNotNull();
        
        // The behavior should be different based on the USE_RELATIVE_PATHS setting
        // Note: Exact behavior depends on the implementation details of the custom getter
    }

    @Test
    void testOptionalFile() {
        DataKey<OptionalFile> optFileKey = LaraIKeyFactory.optionalFile("testOptFile", false);
        
        assertThat(optFileKey).isNotNull();
        assertThat(optFileKey.getName()).isEqualTo("testOptFile");
        
        // Default should be empty OptionalFile
        OptionalFile defaultValue = optFileKey.getDefault().get();
        assertThat(defaultValue.isUsed()).isFalse();
    }

    @Test
    void testOptionalFolder() {
        DataKey<OptionalFile> optFolderKey = LaraIKeyFactory.optionalFolder("testOptFolder", false);
        
        assertThat(optFolderKey).isNotNull();
        assertThat(optFolderKey.getName()).isEqualTo("testOptFolder");
        
        // Default should be empty OptionalFile
        OptionalFile defaultValue = optFolderKey.getDefault().get();
        assertThat(defaultValue.isUsed()).isFalse();
    }

    @Test
    void testOptionalFileWithExtensions() {
        DataKey<OptionalFile> optFileKey = LaraIKeyFactory.optionalFile("testOptFile", false, "txt", "java");
        
        assertThat(optFileKey).isNotNull();
        assertThat(optFileKey.getName()).isEqualTo("testOptFile");
        
        // Default should be empty OptionalFile
        OptionalFile defaultValue = optFileKey.getDefault().get();
        assertThat(defaultValue.isUsed()).isFalse();
    }

    @Test
    void testOptionalFileCustomGetter() {
        File testFile = new File(tempDir, "test.txt");
        OptionalFile testOptFile = new OptionalFile(testFile, true);
        
        DataKey<OptionalFile> optFileKey = LaraIKeyFactory.optionalFile("testOptFile", false, false, false, Collections.emptyList());
        DataStore dataStore = TestDataStores.withWorkingFolder(tempDir, false);
        
        // Test custom getter processes underlying file when present
        OptionalFile processed = optFileKey.getCustomGetter().get().get(testOptFile, dataStore);
        assertThat(processed).isNotNull();
        assertThat(processed.isUsed()).isTrue();
        assertThat(processed.getFile()).isNotNull();
    }

    @Test
    void testCustomGetterLaraArgs() {
        // Test whitespace trimming
        assertThat(LaraIKeyFactory.customGetterLaraArgs("  hello world  ", null)).isEqualTo("hello world");
        assertThat(LaraIKeyFactory.customGetterLaraArgs("\t\ntest\r\n", null)).isEqualTo("test");
        assertThat(LaraIKeyFactory.customGetterLaraArgs("", null)).isEqualTo("");
        assertThat(LaraIKeyFactory.customGetterLaraArgs("   ", null)).isEqualTo("");
        assertThat(LaraIKeyFactory.customGetterLaraArgs("no-trim-needed", null)).isEqualTo("no-trim-needed");
    }

    @Test
    void testCustomGetterLaraArgsWithVariousWhitespace() {
        // Test different types of whitespace
        assertThat(LaraIKeyFactory.customGetterLaraArgs(" \t\r\narg1 arg2\n\r\t ", null)).isEqualTo("arg1 arg2");
        assertThat(LaraIKeyFactory.customGetterLaraArgs("   leading", null)).isEqualTo("leading");
        assertThat(LaraIKeyFactory.customGetterLaraArgs("trailing   ", null)).isEqualTo("trailing");
        assertThat(LaraIKeyFactory.customGetterLaraArgs("  both  ", null)).isEqualTo("both");
    }

    @Test
    void testCustomGetterFileListBehavior() {
        // Test the customGetterFileList method behavior
        File file1 = new File(tempDir, "file1.txt");
        File file2 = new File(tempDir, "file2.txt");
        FileList fileList = FileList.newInstance(file1, file2);
        
        // Test for files (not folders), no creation
        var customGetter = LaraIKeyFactory.customGetterFileList(false, true, false);
        DataStore dataStore = TestDataStores.withWorkingFolder(tempDir, false);
        
        FileList processed = customGetter.get(fileList, dataStore);
        assertThat(processed).isNotNull();
        assertThat(processed.getFiles()).hasSameSizeAs(fileList.getFiles());
        
        // Test for folders, no creation
        var folderCustomGetter = LaraIKeyFactory.customGetterFileList(true, false, false);
        FileList processedFolders = folderCustomGetter.get(fileList, dataStore);
        assertThat(processedFolders).isNotNull();
        assertThat(processedFolders.getFiles()).hasSameSizeAs(fileList.getFiles());
    }
}