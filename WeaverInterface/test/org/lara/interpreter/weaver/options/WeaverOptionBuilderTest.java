package org.lara.interpreter.weaver.options;

import org.junit.jupiter.api.Test;
import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Datakey.KeyFactory;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link WeaverOptionBuilder} - short/long/description/args/argName/dataKey mapping; 
 * build(DataKey<?>) shortcut; enum2List mapping.
 */
class WeaverOptionBuilderTest {

    @Test
    void testBuilderPattern() {
        DataKey<String> testKey = KeyFactory.string("test").setLabel("Test Label");
        
        WeaverOption option = new WeaverOptionBuilder()
                .shortOption("s")
                .longOption("long")
                .description("Test description")
                .args(OptionArguments.ONE_ARG)
                .argName("testArg")
                .dataKey(testKey)
                .build();
        
        assertThat(option.shortOption()).isEqualTo("s");
        assertThat(option.longOption()).isEqualTo("long");
        assertThat(option.description()).isEqualTo("Test description");
        assertThat(option.args()).isEqualTo(OptionArguments.ONE_ARG);
        assertThat(option.argName()).isEqualTo("testArg");
        assertThat(option.dataKey()).isEqualTo(testKey);
    }

    @Test
    void testBuilderDefaults() {
        WeaverOption option = new WeaverOptionBuilder().build();
        
        assertThat(option.shortOption()).isEmpty();
        assertThat(option.longOption()).isEmpty();
        assertThat(option.description()).isEmpty();
        assertThat(option.args()).isEqualTo(OptionArguments.NO_ARGS);
        assertThat(option.argName()).isEqualTo("arg");
        assertThat(option.dataKey()).isNull();
    }

    @Test
    void testStaticBuildWithoutArgs() {
        DataKey<String> testKey = KeyFactory.string("test").setLabel("Test Label");
        
        WeaverOption option = WeaverOptionBuilder.build("s", "long", "Test description", testKey);
        
        assertThat(option.shortOption()).isEqualTo("s");
        assertThat(option.longOption()).isEqualTo("long");
        assertThat(option.description()).isEqualTo("Test description");
        assertThat(option.args()).isEqualTo(OptionArguments.NO_ARGS);
        assertThat(option.argName()).isEqualTo("arg"); // Default
        assertThat(option.dataKey()).isEqualTo(testKey);
    }

    @Test
    void testStaticBuildWithArgs() {
        DataKey<String> testKey = KeyFactory.string("test").setLabel("Test Label");
        
        WeaverOption option = WeaverOptionBuilder.build("s", "long", OptionArguments.SEVERAL_ARGS, 
                "files", "Test description", testKey);
        
        assertThat(option.shortOption()).isEqualTo("s");
        assertThat(option.longOption()).isEqualTo("long");
        assertThat(option.args()).isEqualTo(OptionArguments.SEVERAL_ARGS);
        assertThat(option.argName()).isEqualTo("files");
        assertThat(option.description()).isEqualTo("Test description");
        assertThat(option.dataKey()).isEqualTo(testKey);
    }

    @Test
    void testBuildFromDataKeyShortcut() {
        DataKey<String> testKey = KeyFactory.string("testKey").setLabel("Test Key Label");
        
        WeaverOption option = WeaverOptionBuilder.build(testKey);
        
        assertThat(option.shortOption()).isEmpty(); // Default from build(DataKey)
        assertThat(option.longOption()).isEqualTo("testKey"); // From dataKey.getName()
        assertThat(option.description()).isEqualTo("Test Key Label"); // From dataKey.getLabel()
        assertThat(option.args()).isEqualTo(OptionArguments.NO_ARGS); // Default
        assertThat(option.argName()).isEqualTo("arg"); // Default
        assertThat(option.dataKey()).isEqualTo(testKey);
    }

    @Test
    void testEnum2List() {
        enum TestEnum {
            OPTION_A, OPTION_B, OPTION_C
        }
        
        List<WeaverOption> options = WeaverOptionBuilder.enum2List(TestEnum.class, enumValue -> {
            DataKey<String> key = KeyFactory.string(enumValue.name().toLowerCase());
            return WeaverOptionBuilder.build("", enumValue.name().toLowerCase(), 
                    "Description for " + enumValue.name(), key);
        });
        
        assertThat(options).hasSize(3);
        
        WeaverOption optionA = options.get(0);
        assertThat(optionA.longOption()).isEqualTo("option_a");
        assertThat(optionA.description()).isEqualTo("Description for OPTION_A");
        
        WeaverOption optionB = options.get(1);
        assertThat(optionB.longOption()).isEqualTo("option_b");
        assertThat(optionB.description()).isEqualTo("Description for OPTION_B");
        
        WeaverOption optionC = options.get(2);
        assertThat(optionC.longOption()).isEqualTo("option_c");
        assertThat(optionC.description()).isEqualTo("Description for OPTION_C");
    }

    @Test
    void testEnum2ListWithEmptyEnum() {
        enum EmptyEnum {}
        
        List<WeaverOption> options = WeaverOptionBuilder.enum2List(EmptyEnum.class, enumValue -> {
            DataKey<String> key = KeyFactory.string(enumValue.name());
            return WeaverOptionBuilder.build("", enumValue.name(), "Description", key);
        });
        
        assertThat(options).isEmpty();
    }

    @Test
    void testBuilderChaining() {
        DataKey<Boolean> testKey = KeyFactory.bool("debug");
        
        // Test that builder methods return the builder for chaining
        WeaverOptionBuilder builder = new WeaverOptionBuilder();
        WeaverOptionBuilder result = builder
                .shortOption("d")
                .longOption("debug")
                .description("Enable debug mode")
                .args(OptionArguments.NO_ARGS)
                .argName("debug")
                .dataKey(testKey);
        
        assertThat(result).isSameAs(builder); // Chaining should return same instance
        
        WeaverOption option = result.build();
        assertThat(option.shortOption()).isEqualTo("d");
        assertThat(option.longOption()).isEqualTo("debug");
        assertThat(option.description()).isEqualTo("Enable debug mode");
        assertThat(option.args()).isEqualTo(OptionArguments.NO_ARGS);
        assertThat(option.argName()).isEqualTo("debug");
        assertThat(option.dataKey()).isEqualTo(testKey);
    }

    @Test
    void testToStringOnBuiltOption() {
        WeaverOption option = new WeaverOptionBuilder()
                .description("Test description")
                .build();
        
        // The internal DefaultWeaverOption toString should return the description
        assertThat(option.toString()).isEqualTo("Test description");
    }

    @Test
    void testAllOptionArgumentsTypes() {
        DataKey<String> testKey = KeyFactory.string("test");
        
        // Test all OptionArguments variants can be set
        for (OptionArguments argType : OptionArguments.values()) {
            WeaverOption option = new WeaverOptionBuilder()
                    .args(argType)
                    .dataKey(testKey)
                    .build();
            
            assertThat(option.args()).isEqualTo(argType);
        }
    }
}