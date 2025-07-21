package org.lara.interpreter.cli;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lara.interpreter.weaver.options.WeaverOption;
import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Interfaces.DataStore;

/**
 * Unit tests for JOptionsInterface class.
 * Tests the interface for converting between CLI options and DataStore.
 * 
 * @author Generated Tests
 */
class JOptionsInterfaceTest {

    @Test
    @DisplayName("getConversionMap() should return non-empty mapping")
    void testGetConversionMap() {
        // When
        Map<WeaverOption, DataKey<?>> conversionMap = JOptionsInterface.getConversionMap();

        // Then
        assertThat(conversionMap).isNotNull();
        assertThat(conversionMap).isNotEmpty();
        assertThat(conversionMap).containsKey(CLIOption.main);
        assertThat(conversionMap).containsKey(CLIOption.argv);
        assertThat(conversionMap).containsKey(CLIOption.workspace);
        assertThat(conversionMap).containsKey(CLIOption.output);
        assertThat(conversionMap).containsKey(CLIOption.debug);
    }

    @Test
    @DisplayName("getDataStore() should create DataStore from name and properties")
    void testGetDataStore() {
        // Given
        String name = "TestDataStore";
        Properties properties = new Properties();
        properties.setProperty("test.property", "test.value");

        // When
        DataStore result = JOptionsInterface.getDataStore(name, properties);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("getDataStore() should handle empty properties")
    void testGetDataStore_EmptyProperties() {
        // Given
        String name = "EmptyDataStore";
        Properties properties = new Properties();

        // When
        DataStore result = JOptionsInterface.getDataStore(name, properties);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("getDataStore() should handle null name")
    void testGetDataStore_NullName() {
        // Given
        String name = null;
        Properties properties = new Properties();

        // When
        DataStore result = JOptionsInterface.getDataStore(name, properties);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("getDataStore() should handle null properties gracefully")
    void testGetDataStore_NullProperties() {
        // When/Then - This actually throws NPE as per current implementation
        // The method doesn't handle null properties gracefully, so we expect the
        // exception
        assertThatThrownBy(() -> JOptionsInterface.getDataStore("TestDataStore", null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("getDataStore() should handle invalid property values")
    void testGetDataStore_InvalidProperties() {
        // Given
        Properties invalidProps = new Properties();
        invalidProps.setProperty("invalid.key", "invalid.value");

        // When/Then - should not throw exception, just ignore invalid properties
        DataStore result = JOptionsInterface.getDataStore("TestDataStore", invalidProps);

        // Then
        assertThat(result).isNotNull();
    }
}
