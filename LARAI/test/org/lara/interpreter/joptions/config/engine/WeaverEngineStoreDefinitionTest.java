package org.lara.interpreter.joptions.config.engine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.suikasoft.jOptions.storedefinition.StoreDefinition;

/**
 * Unit tests for WeaverEngineStoreDefinition class.
 * Tests the store definition provider for weaver engine configuration.
 * 
 * @author Generated Tests
 */
class WeaverEngineStoreDefinitionTest {

    private WeaverEngine mockWeaverEngine;
    private WeaverEngineStoreDefinition storeDefinition;

    @BeforeEach
    void setUp() {
        mockWeaverEngine = mock(WeaverEngine.class);
        when(mockWeaverEngine.getOptions()).thenReturn(Arrays.asList());
        when(mockWeaverEngine.getName()).thenReturn("TestWeaver");

        storeDefinition = new WeaverEngineStoreDefinition(mockWeaverEngine);
    }

    @Test
    @DisplayName("constructor should accept WeaverEngine")
    void testConstructor() {
        // When/Then - should not throw exception
        WeaverEngineStoreDefinition definition = new WeaverEngineStoreDefinition(mockWeaverEngine);

        assertThat(definition).isNotNull();
    }

    @Test
    @DisplayName("getStoreDefinition() should return non-null store definition")
    void testGetStoreDefinition() {
        // When
        StoreDefinition result = storeDefinition.getStoreDefinition();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).contains("Options");
        // The name includes the mock class name, which contains "WeaverEngine"
        assertThat(result.getName()).contains("WeaverEngine");
    }

    @Test
    @DisplayName("getStoreDefinition() should handle empty weaver options")
    void testGetStoreDefinition_EmptyOptions() {
        // Given
        when(mockWeaverEngine.getOptions()).thenReturn(Arrays.asList());

        // When
        StoreDefinition result = storeDefinition.getStoreDefinition();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getKeys()).isEmpty();
    }

    @Test
    @DisplayName("getDefinitionName() should return correct name")
    void testGetDefinitionName() {
        // When
        String definitionName = WeaverEngineStoreDefinition.getDefinitionName();

        // Then
        assertThat(definitionName).isEqualTo("Weaver Engine");
    }
}
