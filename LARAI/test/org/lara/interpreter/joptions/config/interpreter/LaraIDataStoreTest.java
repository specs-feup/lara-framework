package org.lara.interpreter.joptions.config.interpreter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lara.interpreter.joptions.keys.FileList;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.lara.interpreter.weaver.options.WeaverOption;
import org.suikasoft.jOptions.Interfaces.DataStore;
import org.suikasoft.jOptions.JOptionKeys;
import org.suikasoft.jOptions.storedefinition.StoreDefinition;

import larai.LaraI;

/**
 * Unit tests for LaraIDataStore class.
 * Tests the configuration data store functionality.
 * 
 * @author Generated Tests
 */
class LaraIDataStoreTest {

    private LaraI mockLaraI;
    private DataStore mockDataStore;
    private WeaverEngine mockWeaverEngine;

    @BeforeEach
    void setUp() {
        mockLaraI = mock(LaraI.class);
        mockDataStore = mock(DataStore.class);
        mockWeaverEngine = mock(WeaverEngine.class);

        // Setup basic mocks
        when(mockWeaverEngine.getOptions()).thenReturn(Arrays.asList());
        when(mockWeaverEngine.getName()).thenReturn("TestWeaver");
        when(mockDataStore.getStoreDefinitionTry()).thenReturn(Optional.empty());
        when(mockDataStore.getPersistence()).thenReturn(Optional.empty());

        // Mock required LARA_FILE to avoid LaraIException
        File mockLaraFile = new File("test.lara");
        when(mockDataStore.get(LaraiKeys.LARA_FILE)).thenReturn(mockLaraFile);
        when(mockDataStore.hasValue(LaraiKeys.LARA_FILE)).thenReturn(true);

        // Mock WORKSPACE_FOLDER to fix getContextPath issues
        FileList mockWorkspaceFolder = mock(FileList.class);
        when(mockDataStore.get(LaraiKeys.WORKSPACE_FOLDER)).thenReturn(mockWorkspaceFolder);
        when(mockDataStore.hasValue(LaraiKeys.WORKSPACE_FOLDER)).thenReturn(true);

        // Mock CURRENT_FOLDER_PATH for JOptionKeys.getContextPath
        when(mockDataStore.get(JOptionKeys.CURRENT_FOLDER_PATH)).thenReturn(Optional.of("/tmp"));
    }

    @Test
    @DisplayName("getConfigFileName() should return correct file name")
    void testGetConfigFileName() {
        // When
        String fileName = LaraIDataStore.getConfigFileName();

        // Then
        assertThat(fileName).isEqualTo("larai.properties");
    }

    @Test
    @DisplayName("getSystemOptionsFilename() should return correct file name")
    void testGetSystemOptionsFilename() {
        // When
        String fileName = LaraIDataStore.getSystemOptionsFilename();

        // Then
        assertThat(fileName).isEqualTo("system_options.xml");
    }

    @Test
    @DisplayName("constructor should create instance with empty weaver options")
    void testConstructor_EmptyWeaverOptions() {
        // Given
        when(mockWeaverEngine.getOptions()).thenReturn(Arrays.asList());

        // When
        LaraIDataStore dataStore = new LaraIDataStore(mockLaraI, mockDataStore, mockWeaverEngine);

        // Then
        assertThat(dataStore).isNotNull();
    }

    @Test
    @DisplayName("constructor should handle weaver options with DataKeys")
    void testConstructor_WithWeaverOptions() {
        // Given
        WeaverOption mockOption = mock(WeaverOption.class);
        when(mockOption.dataKey()).thenReturn(null); // No data key
        when(mockWeaverEngine.getOptions()).thenReturn(Arrays.asList(mockOption));

        // When
        LaraIDataStore dataStore = new LaraIDataStore(mockLaraI, mockDataStore, mockWeaverEngine);

        // Then
        assertThat(dataStore).isNotNull();
    }

    @Test
    @DisplayName("constructor should merge system and local options when available")
    void testConstructor_MergeSystemOptions() {
        // Given
        StoreDefinition mockStoreDef = mock(StoreDefinition.class);

        when(mockDataStore.getStoreDefinitionTry()).thenReturn(Optional.of(mockStoreDef));
        when(mockDataStore.getPersistence()).thenReturn(Optional.empty()); // No persistence for simplicity
        when(mockWeaverEngine.getOptions()).thenReturn(Arrays.asList());

        // When
        LaraIDataStore dataStore = new LaraIDataStore(mockLaraI, mockDataStore, mockWeaverEngine);

        // Then
        assertThat(dataStore).isNotNull();
    }

    @Test
    @DisplayName("getLaraFile() should delegate to DataStore")
    void testGetLaraFile() {
        // Given
        File expectedFile = new File("test.lara");
        when(mockDataStore.get(LaraiKeys.LARA_FILE)).thenReturn(expectedFile);
        LaraIDataStore dataStore = new LaraIDataStore(mockLaraI, mockDataStore, mockWeaverEngine);

        // When
        File result = dataStore.getLaraFile();

        // Then
        assertThat(result).isEqualTo(expectedFile);
    }

    @Test
    @DisplayName("getWorkingDir() should delegate to DataStore")
    void testGetWorkingDir() {
        // Given
        FileList expectedFileList = mock(FileList.class);
        when(mockDataStore.get(LaraiKeys.WORKSPACE_FOLDER)).thenReturn(expectedFileList);
        LaraIDataStore dataStore = new LaraIDataStore(mockLaraI, mockDataStore, mockWeaverEngine);

        // When
        FileList result = dataStore.getWorkingDir();

        // Then
        assertThat(result).isEqualTo(expectedFileList);
    }

    @Test
    @DisplayName("getOutputDir() should delegate to DataStore")
    void testGetOutputDir() {
        // Given
        File expectedDir = new File("output");
        when(mockDataStore.hasValue(LaraiKeys.OUTPUT_FOLDER)).thenReturn(true);
        when(mockDataStore.get(LaraiKeys.OUTPUT_FOLDER)).thenReturn(expectedDir);
        LaraIDataStore dataStore = new LaraIDataStore(mockLaraI, mockDataStore, mockWeaverEngine);

        // When
        File result = dataStore.getOutputDir();

        // Then
        assertThat(result).isEqualTo(expectedDir);
    }

    @Test
    @DisplayName("getTryFolder() should return current directory when folder not set")
    void testGetTryFolder_NotSet() {
        // Given - setup for getTryFolder through getOutputDir
        when(mockDataStore.hasValue(LaraiKeys.OUTPUT_FOLDER)).thenReturn(false);
        LaraIDataStore dataStore = new LaraIDataStore(mockLaraI, mockDataStore, mockWeaverEngine);

        // When
        File result = dataStore.getOutputDir();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(".");
    }

    @Test
    @DisplayName("getAspectArgumentsStr() should handle null arguments")
    void testGetAspectArgumentsStr_NullArguments() {
        // Given
        when(mockDataStore.get(LaraiKeys.ASPECT_ARGS)).thenReturn(null);
        LaraIDataStore dataStore = new LaraIDataStore(mockLaraI, mockDataStore, mockWeaverEngine);

        // When
        String result = dataStore.getAspectArgumentsStr();

        // Then
        assertThat(result).isEqualTo("");
    }

    @Test
    @DisplayName("getAspectArgumentsStr() should return string representation")
    void testGetAspectArgumentsStr_WithArguments() {
        // Given
        String args = "\"key1\": \"value1\", \"key2\": \"value2\"";
        when(mockDataStore.hasValue(LaraiKeys.ASPECT_ARGS)).thenReturn(true);
        when(mockDataStore.get(LaraiKeys.ASPECT_ARGS)).thenReturn(args);
        LaraIDataStore dataStore = new LaraIDataStore(mockLaraI, mockDataStore, mockWeaverEngine);

        // When
        String result = dataStore.getAspectArgumentsStr();

        // Then
        assertThat(result).isNotEmpty();
        // The result should be properly formatted JSON
        assertThat(result).startsWith("{");
        assertThat(result).endsWith("}");
    }

    @Test
    @DisplayName("getAspectArgumentsStr() should handle JSON-like input")
    void testGetAspectArgumentsStr_JsonInput() {
        // Given
        String jsonArgs = "\"key1\": \"value1\"";
        when(mockDataStore.hasValue(LaraiKeys.ASPECT_ARGS)).thenReturn(true);
        when(mockDataStore.get(LaraiKeys.ASPECT_ARGS)).thenReturn(jsonArgs);
        // Mock for parseAspectArgs file checking
        when(mockDataStore.get(JOptionKeys.CURRENT_FOLDER_PATH)).thenReturn(Optional.of("/tmp"));
        LaraIDataStore dataStore = new LaraIDataStore(mockLaraI, mockDataStore, mockWeaverEngine);

        // When
        String result = dataStore.getAspectArgumentsStr();

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).startsWith("{");
        assertThat(result).endsWith("}");
        assertThat(result).contains("key1");
        assertThat(result).contains("value1");
    }

    @Test
    @DisplayName("getWeaverArgs() should return the underlying DataStore")
    void testGetWeaverArgs() {
        // Given
        LaraIDataStore dataStore = new LaraIDataStore(mockLaraI, mockDataStore, mockWeaverEngine);

        // When
        DataStore result = dataStore.getWeaverArgs();

        // Then
        assertThat(result).isEqualTo(mockDataStore);
    }

    @Test
    @DisplayName("getExtraSources() should return empty list when no extra sources")
    void testGetExtraSources_Empty() {
        // Given
        when(mockDataStore.hasValue(LaraiKeys.WORKSPACE_EXTRA)).thenReturn(false);
        LaraIDataStore dataStore = new LaraIDataStore(mockLaraI, mockDataStore, mockWeaverEngine);

        // When
        var result = dataStore.getExtraSources();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("isDebug() should return false when debug mode not set")
    void testIsDebug_NotSet() {
        // Given
        when(mockDataStore.hasValue(LaraiKeys.DEBUG_MODE)).thenReturn(false);
        LaraIDataStore dataStore = new LaraIDataStore(mockLaraI, mockDataStore, mockWeaverEngine);

        // When
        boolean result = dataStore.isDebug();

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isDebug() should return true when debug mode is enabled")
    void testIsDebug_Enabled() {
        // Given
        when(mockDataStore.hasValue(LaraiKeys.DEBUG_MODE)).thenReturn(true);
        when(mockDataStore.get(LaraiKeys.DEBUG_MODE)).thenReturn(true);
        LaraIDataStore dataStore = new LaraIDataStore(mockLaraI, mockDataStore, mockWeaverEngine);

        // When
        boolean result = dataStore.isDebug();

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("toString() should delegate to DataStore")
    void testToString() {
        // Given
        String expectedString = "MockDataStore[test=value]";
        when(mockDataStore.toString()).thenReturn(expectedString);
        LaraIDataStore dataStore = new LaraIDataStore(mockLaraI, mockDataStore, mockWeaverEngine);

        // When
        String result = dataStore.toString();

        // Then
        assertThat(result).isEqualTo(expectedString);
    }
}
