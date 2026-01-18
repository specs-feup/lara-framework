package org.lara.interpreter.weaver.options;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Datakey.KeyFactory;
import org.suikasoft.jOptions.storedefinition.StoreDefinition;

class WeaverOptionUtilsTest {

    @Test
    @DisplayName("toWeaverOption(StoreDefinition, StoreDefinition...) should convert multiple store definitions to weaver options")
    void testToWeaverOptionWithVarArgs() {
        // Given
        DataKey<String> key1 = KeyFactory.string("key1").setLabel("Key 1 Label");
        DataKey<Boolean> key2 = KeyFactory.bool("key2").setLabel("Key 2 Label");
        DataKey<Integer> key3 = KeyFactory.integer("key3").setLabel("Key 3 Label");

        StoreDefinition storeDef1 = mock(StoreDefinition.class);
        when(storeDef1.getKeys()).thenReturn(Arrays.asList(key1));

        StoreDefinition storeDef2 = mock(StoreDefinition.class);
        when(storeDef2.getKeys()).thenReturn(Arrays.asList(key2));

        StoreDefinition storeDef3 = mock(StoreDefinition.class);
        when(storeDef3.getKeys()).thenReturn(Arrays.asList(key3));

        // When
        List<WeaverOption> options = WeaverOptionUtils.toWeaverOption(storeDef1, storeDef2, storeDef3);

        // Then
        assertThat(options).hasSize(3);

        // Check first option (string key)
        WeaverOption opt1 = options.get(0);
        assertThat(opt1.longOption()).isEqualTo("key1");
        assertThat(opt1.description()).isEqualTo("Key 1 Label");
        assertThat(opt1.args()).isEqualTo(OptionArguments.ONE_ARG);
        assertThat(opt1.dataKey()).isSameAs(key1);

        // Check second option (boolean key)
        WeaverOption opt2 = options.get(1);
        assertThat(opt2.longOption()).isEqualTo("key2");
        assertThat(opt2.description()).isEqualTo("Key 2 Label");
        assertThat(opt2.args()).isEqualTo(OptionArguments.NO_ARGS);
        assertThat(opt2.dataKey()).isSameAs(key2);

        // Check third option (integer key)
        WeaverOption opt3 = options.get(2);
        assertThat(opt3.longOption()).isEqualTo("key3");
        assertThat(opt3.description()).isEqualTo("Key 3 Label");
        assertThat(opt3.args()).isEqualTo(OptionArguments.ONE_ARG);
        assertThat(opt3.dataKey()).isSameAs(key3);
    }

    @Test
    @DisplayName("toWeaverOption(List<StoreDefinition>) should convert list of store definitions to weaver options")
    void testToWeaverOptionWithList() {
        // Given
        DataKey<String> key1 = KeyFactory.string("option1").setLabel("Option 1");
        DataKey<Boolean> key2 = KeyFactory.bool("flag").setLabel("Flag Option");

        StoreDefinition storeDef1 = mock(StoreDefinition.class);
        when(storeDef1.getKeys()).thenReturn(Arrays.asList(key1));

        StoreDefinition storeDef2 = mock(StoreDefinition.class);
        when(storeDef2.getKeys()).thenReturn(Arrays.asList(key2));

        List<StoreDefinition> storeDefinitions = Arrays.asList(storeDef1, storeDef2);

        // When
        List<WeaverOption> options = WeaverOptionUtils.toWeaverOption(storeDefinitions);

        // Then
        assertThat(options).hasSize(2);
        assertThat(options.get(0).longOption()).isEqualTo("option1");
        assertThat(options.get(0).args()).isEqualTo(OptionArguments.ONE_ARG);
        assertThat(options.get(1).longOption()).isEqualTo("flag");
        assertThat(options.get(1).args()).isEqualTo(OptionArguments.NO_ARGS);
    }

    @Test
    @DisplayName("toWeaverOption() should handle empty store definitions")
    void testToWeaverOptionWithEmptyStoreDefinitions() {
        // Given
        StoreDefinition emptyStoreDef = mock(StoreDefinition.class);
        when(emptyStoreDef.getKeys()).thenReturn(Arrays.asList());

        List<StoreDefinition> storeDefinitions = Arrays.asList(emptyStoreDef);

        // When
        List<WeaverOption> options = WeaverOptionUtils.toWeaverOption(storeDefinitions);

        // Then
        assertThat(options).isEmpty();
    }

    @Test
    @DisplayName("toWeaverOption() should handle store definitions with multiple keys")
    void testToWeaverOptionWithMultipleKeysPerDefinition() {
        // Given
        DataKey<String> key1 = KeyFactory.string("str").setLabel("String Key");
        DataKey<Boolean> key2 = KeyFactory.bool("bool").setLabel("Boolean Key");
        DataKey<Integer> key3 = KeyFactory.integer("int").setLabel("Integer Key");

        StoreDefinition storeDef = mock(StoreDefinition.class);
        when(storeDef.getKeys()).thenReturn(Arrays.asList(key1, key2, key3));

        // When
        List<WeaverOption> options = WeaverOptionUtils.toWeaverOption(Arrays.asList(storeDef));

        // Then
        assertThat(options).hasSize(3);
        assertThat(options.get(0).longOption()).isEqualTo("str");
        assertThat(options.get(1).longOption()).isEqualTo("bool");
        assertThat(options.get(2).longOption()).isEqualTo("int");
    }

    @Test
    @DisplayName("toOption(DataKey) should create option using key name and label")
    void testToOptionWithDataKeyOnly() {
        // Given
        DataKey<String> stringKey = KeyFactory.string("testKey").setLabel("Test Label");

        // When
        WeaverOption option = WeaverOptionUtils.toOption(stringKey);

        // Then
        assertThat(option.shortOption()).isNull();
        assertThat(option.longOption()).isEqualTo("testKey");
        assertThat(option.description()).isEqualTo("Test Label");
        assertThat(option.args()).isEqualTo(OptionArguments.ONE_ARG);
        assertThat(option.argName()).isEqualTo("<arg>");
        assertThat(option.dataKey()).isSameAs(stringKey);
    }

    @Test
    @DisplayName("toOption(DataKey) should create boolean option with NO_ARGS for Boolean DataKey")
    void testToOptionWithBooleanDataKey() {
        // Given
        DataKey<Boolean> boolKey = KeyFactory.bool("enable").setLabel("Enable feature");

        // When
        WeaverOption option = WeaverOptionUtils.toOption(boolKey);

        // Then
        assertThat(option.shortOption()).isNull();
        assertThat(option.longOption()).isEqualTo("enable");
        assertThat(option.description()).isEqualTo("Enable feature");
        assertThat(option.args()).isEqualTo(OptionArguments.NO_ARGS);
        assertThat(option.dataKey()).isSameAs(boolKey);
    }

    @Test
    @DisplayName("toOption(DataKey, String, String, String) should create option with all specified parameters")
    void testToOptionWithAllParameters() {
        // Given
        DataKey<String> key = KeyFactory.string("verbose").setLabel("Original Label");
        String shortOption = "-v";
        String longOption = "--verbose";
        String description = "Enable verbose output";

        // When
        WeaverOption option = WeaverOptionUtils.toOption(key, shortOption, longOption, description);

        // Then
        assertThat(option.shortOption()).isEqualTo("-v");
        assertThat(option.longOption()).isEqualTo("--verbose");
        assertThat(option.description()).isEqualTo("Enable verbose output");
        assertThat(option.args()).isEqualTo(OptionArguments.ONE_ARG);
        assertThat(option.argName()).isEqualTo("<arg>");
        assertThat(option.dataKey()).isSameAs(key);
    }

    @Test
    @DisplayName("toOption() with Boolean key should create NO_ARGS option regardless of other parameters")
    void testToOptionWithBooleanKeyAndParameters() {
        // Given
        DataKey<Boolean> boolKey = KeyFactory.bool("debug").setLabel("Original Label");
        String shortOption = "-d";
        String longOption = "--debug";
        String description = "Enable debug mode";

        // When
        WeaverOption option = WeaverOptionUtils.toOption(boolKey, shortOption, longOption, description);

        // Then
        assertThat(option.shortOption()).isEqualTo("-d");
        assertThat(option.longOption()).isEqualTo("--debug");
        assertThat(option.description()).isEqualTo("Enable debug mode");
        assertThat(option.args()).isEqualTo(OptionArguments.NO_ARGS);
        assertThat(option.dataKey()).isSameAs(boolKey);
    }

    @Test
    @DisplayName("toOption() should handle null parameters gracefully")
    void testToOptionWithNullParameters() {
        // Given
        DataKey<String> key = KeyFactory.string("test").setLabel("Test Label");

        // When
        WeaverOption option = WeaverOptionUtils.toOption(key, null, null, null);

        // Then
        assertThat(option.shortOption()).isNull();
        assertThat(option.longOption()).isNull();
        assertThat(option.description()).isNull();
        assertThat(option.args()).isEqualTo(OptionArguments.ONE_ARG);
        assertThat(option.argName()).isEqualTo("<arg>");
        assertThat(option.dataKey()).isSameAs(key);
    }

    @Test
    @DisplayName("toOption() should work with different primitive wrapper types")
    void testToOptionWithDifferentPrimitiveTypes() {
        // Given
        DataKey<Integer> intKey = KeyFactory.integer("port");
        DataKey<Double> doubleKey = KeyFactory.double64("threshold");
        DataKey<Long> longKey = KeyFactory.longInt("timeout");

        // When
        WeaverOption intOption = WeaverOptionUtils.toOption(intKey);
        WeaverOption doubleOption = WeaverOptionUtils.toOption(doubleKey);
        WeaverOption longOption = WeaverOptionUtils.toOption(longKey);

        // Then
        assertThat(intOption.args()).isEqualTo(OptionArguments.ONE_ARG);
        assertThat(doubleOption.args()).isEqualTo(OptionArguments.ONE_ARG);
        assertThat(longOption.args()).isEqualTo(OptionArguments.ONE_ARG);
    }
}