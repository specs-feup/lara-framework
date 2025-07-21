package org.lara.interpreter.weaver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.lara.interpreter.joptions.config.interpreter.LaraiKeys;
import org.lara.interpreter.joptions.keys.FileList;
import org.suikasoft.jOptions.Interfaces.DataStore;

/**
 * Unit tests for LaraWeaverState class.
 * Tests the state management and JAR loading functionality.
 * 
 * @author Generated Tests
 */
class LaraWeaverStateTest {

    @TempDir
    Path tempDir;

    private DataStore mockDataStore;
    private FileList mockFileList;

    @BeforeEach
    void setUp() {
        mockDataStore = mock(DataStore.class);
        mockFileList = mock(FileList.class);

        // Mock the DataStore to return our FileList
        when(mockDataStore.get(LaraiKeys.JAR_PATHS)).thenReturn(mockFileList);
    }

    @Test
    @DisplayName("constructor should initialize with DataStore")
    void testConstructor() {
        // Given
        when(mockFileList.iterator()).thenReturn(Arrays.<File>asList().iterator());

        // When
        LaraWeaverState state = new LaraWeaverState(mockDataStore);

        // Then
        assertThat(state).isNotNull();
        assertThat(state.getData()).isEqualTo(mockDataStore);
        assertThat(state.getClassLoader()).isNotNull();
        assertThat(state.getClassLoader()).isInstanceOf(URLClassLoader.class);
    }

    @Test
    @DisplayName("constructor should handle empty JAR paths")
    void testConstructor_EmptyJarPaths() {
        // Given
        when(mockFileList.iterator()).thenReturn(Arrays.<File>asList().iterator());

        // When
        LaraWeaverState state = new LaraWeaverState(mockDataStore);

        // Then
        assertThat(state.getClassLoader()).isNotNull();
        assertThat(state.getClassLoader().getURLs()).isEmpty();
    }

    @Test
    @DisplayName("constructor should handle non-existent JAR files")
    void testConstructor_NonExistentJarFiles() {
        // Given
        File nonExistentFile = new File(tempDir.toFile(), "nonexistent.jar");
        when(mockFileList.iterator()).thenReturn(Arrays.asList(nonExistentFile).iterator());

        // When
        LaraWeaverState state = new LaraWeaverState(mockDataStore);

        // Then
        assertThat(state.getClassLoader()).isNotNull();
        assertThat(state.getClassLoader().getURLs()).isEmpty();
    }

    @Test
    @DisplayName("constructor should load existing JAR files")
    void testConstructor_ExistingJarFiles() throws IOException {
        // Given
        Path jarFile = tempDir.resolve("test.jar");
        Files.createFile(jarFile);

        when(mockFileList.iterator()).thenReturn(Arrays.asList(jarFile.toFile()).iterator());

        // When
        LaraWeaverState state = new LaraWeaverState(mockDataStore);

        // Then
        assertThat(state.getClassLoader()).isNotNull();
        assertThat(state.getClassLoader().getURLs()).hasSize(1);
        assertThat(state.getClassLoader().getURLs()[0].getPath()).contains("test.jar");
    }

    @Test
    @DisplayName("constructor should handle JAR directories")
    void testConstructor_JarDirectories() throws IOException {
        // Given
        Path jarDir = tempDir.resolve("jars");
        Files.createDirectories(jarDir);
        Path jarFile1 = jarDir.resolve("lib1.jar");
        Path jarFile2 = jarDir.resolve("lib2.jar");
        Files.createFile(jarFile1);
        Files.createFile(jarFile2);

        when(mockFileList.iterator()).thenReturn(Arrays.asList(jarDir.toFile()).iterator());

        // When
        LaraWeaverState state = new LaraWeaverState(mockDataStore);

        // Then
        assertThat(state.getClassLoader()).isNotNull();
        assertThat(state.getClassLoader().getURLs()).hasSize(2);
    }

    @Test
    @DisplayName("constructor should ignore non-JAR files in directories")
    void testConstructor_NonJarFilesInDirectories() throws IOException {
        // Given
        Path jarDir = tempDir.resolve("mixed");
        Files.createDirectories(jarDir);
        Path jarFile = jarDir.resolve("library.jar");
        Path txtFile = jarDir.resolve("readme.txt");
        Files.createFile(jarFile);
        Files.createFile(txtFile);

        when(mockFileList.iterator()).thenReturn(Arrays.asList(jarDir.toFile()).iterator());

        // When
        LaraWeaverState state = new LaraWeaverState(mockDataStore);

        // Then
        assertThat(state.getClassLoader()).isNotNull();
        assertThat(state.getClassLoader().getURLs()).hasSize(1);
        assertThat(state.getClassLoader().getURLs()[0].getPath()).contains("library.jar");
    }

    @Test
    @DisplayName("close() should close the class loader")
    void testClose() {
        // Given
        when(mockFileList.iterator()).thenReturn(Arrays.<File>asList().iterator());
        LaraWeaverState state = new LaraWeaverState(mockDataStore);

        // When
        state.close();

        // Then - no exception should be thrown, and subsequent calls should not fail
        state.close(); // Should be safe to call multiple times
    }

    @Test
    @DisplayName("getData() should return the original DataStore")
    void testGetData() {
        // Given
        when(mockFileList.iterator()).thenReturn(Arrays.<File>asList().iterator());
        LaraWeaverState state = new LaraWeaverState(mockDataStore);

        // When
        DataStore result = state.getData();

        // Then
        assertThat(result).isSameAs(mockDataStore);
    }

    @Test
    @DisplayName("getClassLoader() should return the same instance")
    void testGetClassLoader() {
        // Given
        when(mockFileList.iterator()).thenReturn(Arrays.<File>asList().iterator());
        LaraWeaverState state = new LaraWeaverState(mockDataStore);

        // When
        URLClassLoader classLoader1 = state.getClassLoader();
        URLClassLoader classLoader2 = state.getClassLoader();

        // Then
        assertThat(classLoader1).isSameAs(classLoader2);
    }

    @Test
    @DisplayName("constructor should handle mixed file types and directories")
    void testConstructor_MixedInputs() throws IOException {
        // Given
        Path jarFile = tempDir.resolve("single.jar");
        Files.createFile(jarFile);

        Path jarDir = tempDir.resolve("jars");
        Files.createDirectories(jarDir);
        Path nestedJar = jarDir.resolve("nested.jar");
        Files.createFile(nestedJar);

        List<File> jarPaths = Arrays.asList(jarFile.toFile(), jarDir.toFile());
        when(mockFileList.iterator()).thenReturn(jarPaths.iterator());

        // When
        LaraWeaverState state = new LaraWeaverState(mockDataStore);

        // Then
        assertThat(state.getClassLoader()).isNotNull();
        assertThat(state.getClassLoader().getURLs()).hasSize(2);
    }
}
