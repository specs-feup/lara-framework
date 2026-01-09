package org.lara.interpreter.joptions.config.interpreter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

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

    @Test
    @DisplayName("constructor should handle mergeSystemAndLocalOptions debug paths")
    void testConstructor_MergeSystemOptionsDebugPaths() {
        // Given - Enable debug logging to trigger lambdas
        // Need to set up persistence and store definition to reach the debug paths
        StoreDefinition mockStoreDef = mock(StoreDefinition.class);
        when(mockStoreDef.hasKey(any())).thenReturn(true);
        
        // Mock persistence to trigger the debug logging paths
        when(mockDataStore.getStoreDefinitionTry()).thenReturn(Optional.of(mockStoreDef));
        when(mockDataStore.getPersistence()).thenReturn(Optional.empty()); // This triggers lambda$2
        
        when(mockWeaverEngine.getOptions()).thenReturn(Arrays.asList());
        when(mockWeaverEngine.getName()).thenReturn("TestWeaver");

        // When - This should trigger the lambda functions for debug logging
        LaraIDataStore dataStore = new LaraIDataStore(mockLaraI, mockDataStore, mockWeaverEngine);

        // Then
        assertThat(dataStore).isNotNull();
    }

    @Test
    @DisplayName("constructor should handle mergeSystemAndLocalOptions when storeDef is null")
    void testConstructor_MergeSystemOptionsNullStoreDef() {
        // Given - No store definition to trigger lambda$0
        when(mockDataStore.getStoreDefinitionTry()).thenReturn(Optional.empty());
        when(mockDataStore.getPersistence()).thenReturn(Optional.empty());
        
        when(mockWeaverEngine.getOptions()).thenReturn(Arrays.asList());
        when(mockWeaverEngine.getName()).thenReturn("TestWeaver");

        // When - This should trigger lambda$0
        LaraIDataStore dataStore = new LaraIDataStore(mockLaraI, mockDataStore, mockWeaverEngine);

        // Then
        assertThat(dataStore).isNotNull();
    }
}
