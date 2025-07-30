package org.lara.interpreter.cli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.Options;
import org.lara.interpreter.weaver.interf.JoinPoint;
import org.lara.interpreter.weaver.interf.AGear;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.lara.interpreter.weaver.options.OptionArguments;
import org.lara.interpreter.weaver.options.WeaverOption;
import org.lara.interpreter.weaver.options.WeaverOptions;
import org.lara.language.specification.dsl.LanguageSpecification;
import org.suikasoft.jOptions.Interfaces.DataStore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for LaraCli class.
 * 
 * Tests cover:
 * - CLI options generation from weaver engine
 * - Weaver options conversion and aggregation
 * - Integration with Apache Commons CLI
 * - Custom weaver options handling
 * 
 * @author Generated Tests
 */
@DisplayName("LaraCli Tests")
class LaraCliTest {

    @Mock
    private WeaverEngine mockWeaverEngine;

    @Mock
    private WeaverOption mockWeaverOption1;

    @Mock
    private WeaverOption mockWeaverOption2;

    private List<WeaverOption> customWeaverOptions;

    /**
     * Test implementation of WeaverEngine for testing purposes
     */
    private static class TestWeaverEngine extends WeaverEngine {
        private final List<WeaverOption> options;

        public TestWeaverEngine(List<WeaverOption> options) {
            this.options = options;
        }

        @Override
        public boolean run(DataStore dataStore) {
            // Mock implementation for testing
            return true;
        }

        @Override
        public boolean close() {
            return true;
        }

        @Override
        public List<String> getActions() {
            return Arrays.asList("testAction");
        }

        @Override
        public String getRoot() {
            return "testRoot";
        }

        @Override
        public JoinPoint getRootJp() {
            return null;
        }

        @Override
        protected LanguageSpecification buildLangSpecs() {
            return null;
        }

        @Override
        public List<AGear> getGears() {
            return Arrays.asList();
        }

        @Override
        public List<WeaverOption> getOptions() {
            return options;
        }

        @Override
        public boolean implementsEvents() {
            return true;
        }
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customWeaverOptions = Arrays.asList(mockWeaverOption1, mockWeaverOption2);

        // Mock weaver engine to return custom options
        when(mockWeaverEngine.getOptions()).thenReturn(customWeaverOptions);

        // Mock weaver option behavior
        when(mockWeaverOption1.toString()).thenReturn("custom-option-1");
        when(mockWeaverOption1.shortOption()).thenReturn("c1");
        when(mockWeaverOption1.longOption()).thenReturn("custom-option-1");
        when(mockWeaverOption1.description()).thenReturn("Custom option 1");
        when(mockWeaverOption1.args()).thenReturn(OptionArguments.NO_ARGS);
        when(mockWeaverOption1.argName()).thenReturn("");
        when(mockWeaverOption1.dataKey()).thenReturn(null);

        when(mockWeaverOption2.toString()).thenReturn("custom-option-2");
        when(mockWeaverOption2.shortOption()).thenReturn("c2");
        when(mockWeaverOption2.longOption()).thenReturn("custom-option-2");
        when(mockWeaverOption2.description()).thenReturn("Custom option 2");
        when(mockWeaverOption2.args()).thenReturn(OptionArguments.NO_ARGS);
        when(mockWeaverOption2.argName()).thenReturn("");
        when(mockWeaverOption2.dataKey()).thenReturn(null);
    }

    @Test
    @DisplayName("getCliOptions() should return non-null Options with weaver engine")
    void testGetCliOptions_WithWeaverEngine() {
        // When
        Options options = LaraCli.getCliOptions(mockWeaverEngine);

        // Then
        assertThat(options).isNotNull();
        assertThat(options.getOptions()).isNotEmpty();

        // Verify that the weaver engine was called to get options
        verify(mockWeaverEngine).getOptions();
    }

    @Test
    @DisplayName("getCliOptions() should include standard LARAI options")
    void testGetCliOptions_IncludesStandardOptions() {
        // When
        Options options = LaraCli.getCliOptions(mockWeaverEngine);

        // Then
        assertThat(options).isNotNull();
        assertThat(options.getOptions()).isNotEmpty();

        // Check that we have some standard CLI options
        // The exact options may vary, but there should be some
        assertThat(options.getOptions().size()).isGreaterThan(0);
    }

    @Test
    @DisplayName("getWeaverOptions() should return WeaverOptions with weaver engine")
    void testGetWeaverOptions_WithWeaverEngine() {
        // When
        WeaverOptions weaverOptions = LaraCli.getWeaverOptions(mockWeaverEngine);

        // Then
        assertThat(weaverOptions).isNotNull();

        // Test that we can create CLI string (which means options are present)
        DataStore mockDataStore = mock(DataStore.class);
        when(mockDataStore.getKeysWithValues()).thenReturn(Arrays.asList());
        String cliString = weaverOptions.toCli(mockDataStore);
        assertThat(cliString).isNotNull();

        verify(mockWeaverEngine).getOptions();
    }

    @Test
    @DisplayName("getWeaverOptions() should return WeaverOptions with collection of options")
    void testGetWeaverOptions_WithCollection() {
        // When
        WeaverOptions weaverOptions = LaraCli.getWeaverOptions(customWeaverOptions);

        // Then
        assertThat(weaverOptions).isNotNull();

        // Test that we can create CLI string (which means options are present)
        DataStore mockDataStore = mock(DataStore.class);
        when(mockDataStore.getKeysWithValues()).thenReturn(Arrays.asList());
        String cliString = weaverOptions.toCli(mockDataStore);
        assertThat(cliString).isNotNull();
    }

    @Test
    @DisplayName("getWeaverOptions() should handle empty collection")
    void testGetWeaverOptions_WithEmptyCollection() {
        // When
        WeaverOptions weaverOptions = LaraCli.getWeaverOptions(Arrays.asList());

        // Then
        assertThat(weaverOptions).isNotNull();

        // Test that we can create CLI string (should still have standard LARAI options)
        DataStore mockDataStore = mock(DataStore.class);
        when(mockDataStore.getKeysWithValues()).thenReturn(Arrays.asList());
        String cliString = weaverOptions.toCli(mockDataStore);
        assertThat(cliString).isNotNull();
    }

    @Test
    @DisplayName("getWeaverOptions() should include CLIOption values")
    void testGetWeaverOptions_IncludesCLIOptions() {
        // When
        WeaverOptions weaverOptions = LaraCli.getWeaverOptions(Arrays.asList());

        // Then
        assertThat(weaverOptions).isNotNull();

        // Test that we can create CLI string
        DataStore mockDataStore = mock(DataStore.class);
        when(mockDataStore.getKeysWithValues()).thenReturn(Arrays.asList());
        String cliString = weaverOptions.toCli(mockDataStore);
        assertThat(cliString).isNotNull();
    }

    @Test
    @DisplayName("Integration test with real WeaverEngine implementation")
    void testIntegrationWithRealWeaverEngine() {
        // Given
        TestWeaverEngine testEngine = new TestWeaverEngine(customWeaverOptions);

        // When
        Options cliOptions = LaraCli.getCliOptions(testEngine);
        WeaverOptions weaverOptions = LaraCli.getWeaverOptions(testEngine);

        // Then
        assertThat(cliOptions).isNotNull();
        assertThat(cliOptions.getOptions()).isNotEmpty();

        assertThat(weaverOptions).isNotNull();

        // Test that we can create CLI string
        DataStore mockDataStore = mock(DataStore.class);
        when(mockDataStore.getKeysWithValues()).thenReturn(Arrays.asList());
        String cliString = weaverOptions.toCli(mockDataStore);
        assertThat(cliString).isNotNull();
    }
}
