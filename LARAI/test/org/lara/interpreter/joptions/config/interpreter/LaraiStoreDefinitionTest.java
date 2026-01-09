package org.lara.interpreter.joptions.config.interpreter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.suikasoft.jOptions.storedefinition.StoreDefinition;

/**
 * Unit tests for LaraiStoreDefinition class.
 * Tests the store definition provider for LARAI configuration.
 * 
 * @author Generated Tests
 */
class LaraiStoreDefinitionTest {

    private LaraiStoreDefinition storeDefinition;

    @BeforeEach
    void setUp() {
        storeDefinition = new LaraiStoreDefinition();
    }

    @Test
    @DisplayName("getStoreDefinition() should return non-null store definition")
    void testGetStoreDefinition() {
        // When
        StoreDefinition result = storeDefinition.getStoreDefinition();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getKeys()).isNotEmpty();
    }

    @Test
    @DisplayName("getDefinitionName() should return correct name")
    void testGetDefinitionName() {
        // When
        String definitionName = LaraiStoreDefinition.getDefinitionName();

        // Then
        assertThat(definitionName).isEqualTo("LaraI Options");
    }

    @Test
    @DisplayName("getStoreDefinition() should include LARAI keys")
    void testGetStoreDefinition_ContainsLaraiKeys() {
        // When
        StoreDefinition result = storeDefinition.getStoreDefinition();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getKeys()).contains(LaraiKeys.LARA_FILE);
        assertThat(result.getKeys()).contains(LaraiKeys.OUTPUT_FOLDER);
        assertThat(result.getKeys()).contains(LaraiKeys.WORKSPACE_FOLDER);
    }
}
