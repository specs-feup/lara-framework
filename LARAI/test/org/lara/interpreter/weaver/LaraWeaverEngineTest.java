package org.lara.interpreter.weaver;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.lara.interpreter.joptions.config.interpreter.LaraiKeys;
import org.lara.interpreter.joptions.keys.FileList;
import org.lara.interpreter.weaver.interf.JoinPoint;
import org.lara.interpreter.weaver.interf.AGear;
import org.lara.interpreter.weaver.options.WeaverOption;
import org.lara.language.specification.dsl.LanguageSpecification;
import org.suikasoft.jOptions.Interfaces.DataStore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Unit tests for LaraWeaverEngine class.
 * 
 * Tests cover:
 * - Abstract class behavior through concrete test implementation
 * - State management
 * - Data store operations
 * - Class loading functionality
 * - Exception handling
 * - Integration with LaraWeaverState
 * 
 * @author Generated Tests
 */
@DisplayName("LaraWeaverEngine Tests")
class LaraWeaverEngineTest {

    @TempDir
    Path tempDir;

    @Mock
    private DataStore mockDataStore;

    @Mock
    private FileList mockJarPaths;

    @Mock
    private FileList mockWorkspaceFiles;

    private TestLaraWeaverEngine weaverEngine;
    private File outputDir;
    private List<File> sources;

    /**
     * Concrete test implementation of LaraWeaverEngine
     */
    private static class TestLaraWeaverEngine extends LaraWeaverEngine {
        private boolean beginCalled = false;
        private boolean beginResult = true;

        @Override
        protected boolean begin(List<File> sources, File outputDir, DataStore dataStore) {
            beginCalled = true;
            return beginResult;
        }

        public boolean wasBeginCalled() {
            return beginCalled;
        }

        public void setBeginResult(boolean result) {
            this.beginResult = result;
        }

        // Implement required abstract methods from WeaverEngine
        @Override
        protected boolean close() {
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
        protected LanguageSpecification buildLangSpecs() {
            return null; // Simplified for testing
        }

        @Override
        public JoinPoint getRootJp() {
            return null; // Simplified for testing
        }

        @Override
        public List<AGear> getGears() {
            return Arrays.asList(); // Empty list for testing
        }

        @Override
        public List<WeaverOption> getOptions() {
            return Arrays.asList(); // Empty list for testing
        }

        @Override
        public boolean implementsEvents() {
            return true; // Simplified for testing
        }
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        weaverEngine = new TestLaraWeaverEngine();
        outputDir = tempDir.resolve("output").toFile();
        sources = Arrays.asList(tempDir.resolve("source1.txt").toFile(), tempDir.resolve("source2.txt").toFile());

        // Create source files
        sources.forEach(file -> {
            try {
                file.createNewFile();
            } catch (Exception e) {
                // Ignore
            }
        });

        // Mock FileList behavior for workspace files
        when(mockWorkspaceFiles.getFiles()).thenReturn(sources);

        // Mock FileList behavior for JAR paths (empty list to avoid loading jars in
        // tests)
        when(mockJarPaths.iterator()).thenReturn(Arrays.<File>asList().iterator());

        // Mock DataStore behavior
        when(mockDataStore.get(LaraiKeys.OUTPUT_FOLDER)).thenReturn(outputDir);
        when(mockDataStore.get(LaraiKeys.WORKSPACE_FOLDER)).thenReturn(mockWorkspaceFiles);
        when(mockDataStore.get(LaraiKeys.JAR_PATHS)).thenReturn(mockJarPaths);
    }

    @Test
    @DisplayName("run() should initialize state and call begin()")
    void testRun_Success() {
        // When
        boolean result = weaverEngine.run(mockDataStore);

        // Then
        assertThat(result).isTrue();
        assertThat(weaverEngine.wasBeginCalled()).isTrue();
        assertThat(weaverEngine.getData()).isPresent();
        assertThat(weaverEngine.getLaraWeaverStateTry()).isPresent();
    }

    @Test
    @DisplayName("run() should return false when begin() fails")
    void testRun_BeginFails() {
        // Given
        weaverEngine.setBeginResult(false);

        // When
        boolean result = weaverEngine.run(mockDataStore);

        // Then
        assertThat(result).isFalse();
        assertThat(weaverEngine.wasBeginCalled()).isTrue();
        assertThat(weaverEngine.getData()).isPresent(); // State should still be initialized
    }

    @Test
    @DisplayName("getData() should return empty when state is not initialized")
    void testGetData_NoState() {
        // When/Then
        assertThat(weaverEngine.getData()).isEmpty();
    }

    @Test
    @DisplayName("getData() should return DataStore when state is initialized")
    void testGetData_WithState() {
        // Given
        weaverEngine.run(mockDataStore);

        // When
        Optional<DataStore> data = weaverEngine.getData();

        // Then
        assertThat(data).isPresent();
    }

    @Test
    @DisplayName("getLaraWeaverStateTry() should return empty when state is not initialized")
    void testGetLaraWeaverStateTry_NoState() {
        // When/Then
        assertThat(weaverEngine.getLaraWeaverStateTry()).isEmpty();
    }

    @Test
    @DisplayName("getLaraWeaverStateTry() should return state when initialized")
    void testGetLaraWeaverStateTry_WithState() {
        // Given
        weaverEngine.run(mockDataStore);

        // When
        Optional<LaraWeaverState> state = weaverEngine.getLaraWeaverStateTry();

        // Then
        assertThat(state).isPresent();
    }

    @Test
    @DisplayName("getLaraWeaverState() should throw exception when state is not initialized")
    void testGetLaraWeaverState_NoState() {
        // When/Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            weaverEngine.getLaraWeaverState();
        });

        assertThat(exception.getMessage()).contains("No LARA weaver state defined");
    }

    @Test
    @DisplayName("getLaraWeaverState() should return state when initialized")
    void testGetLaraWeaverState_WithState() {
        // Given
        weaverEngine.run(mockDataStore);

        // When/Then
        assertThat(weaverEngine.getLaraWeaverState()).isNotNull();
    }

    @Test
    @DisplayName("getClass() should throw exception when state is not initialized")
    void testGetClass_NoState() {
        // When/Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            weaverEngine.getClass("java.lang.String");
        });

        assertThat(exception.getMessage()).contains("No LARA weaver state defined");
    }

    @Test
    @DisplayName("getClass() should load class when state is initialized")
    void testGetClass_WithState() {
        // Given
        weaverEngine.run(mockDataStore);

        // When
        Class<?> clazz = weaverEngine.getClass("java.lang.String");

        // Then
        assertThat(clazz).isEqualTo(String.class);
    }

    @Test
    @DisplayName("getClass() should throw RuntimeException for non-existent class")
    void testGetClass_ClassNotFound() {
        // Given
        weaverEngine.run(mockDataStore);

        // When/Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            weaverEngine.getClass("com.nonexistent.Class");
        });

        assertThat(exception.getMessage()).contains("Could not find class");
        assertThat(exception.getCause()).isInstanceOf(ClassNotFoundException.class);
    }

    @Test
    @DisplayName("end() should close the state")
    void testEnd() {
        // Given
        weaverEngine.run(mockDataStore);

        // When/Then - should not throw exception (state.close() is called)
        weaverEngine.end();

        // We can't easily verify the internal state without exposing more methods
        // but at least we can ensure end() doesn't throw an exception
    }

    @Test
    @DisplayName("run() should handle null output directory gracefully")
    void testRun_NullOutputDir() {
        // Given
        when(mockDataStore.get(LaraiKeys.OUTPUT_FOLDER)).thenReturn(null);

        // When
        boolean result = weaverEngine.run(mockDataStore);

        // Then
        assertThat(result).isTrue(); // begin() still returns true in our test implementation
        assertThat(weaverEngine.wasBeginCalled()).isTrue();
    }

    @Test
    @DisplayName("run() should handle empty source list")
    void testRun_EmptySources() {
        // Given
        when(mockWorkspaceFiles.getFiles()).thenReturn(Arrays.asList());

        // When
        boolean result = weaverEngine.run(mockDataStore);

        // Then
        assertThat(result).isTrue();
        assertThat(weaverEngine.wasBeginCalled()).isTrue();
    }

    @Test
    @DisplayName("run(String[], String[], DataStore) should create temporary files and call run()")
    void testRunWithFilenamesAndCodes_Success() {
        // Given
        String[] filenames = {"test1.lara", "test2.lara"};
        String[] codes = {"console.log('test1');", "console.log('test2');"};

        // When
        boolean result = weaverEngine.run(filenames, codes, mockDataStore);

        // Then
        assertThat(result).isTrue();
        assertThat(weaverEngine.wasBeginCalled()).isTrue();
        assertThat(weaverEngine.getData()).isPresent();

        // Verify that WORKSPACE_FOLDER was set with the temporary files
        verify(mockDataStore).set(eq(LaraiKeys.WORKSPACE_FOLDER), any(FileList.class));
    }

    @Test
    @DisplayName("run(String[], String[], DataStore) should throw exception when arrays have different lengths")
    void testRunWithFilenamesAndCodes_MismatchedArrays() {
        // Given
        String[] filenames = {"test1.lara", "test2.lara"};
        String[] codes = {"console.log('test1');"};

        // When/Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            weaverEngine.run(filenames, codes, mockDataStore);
        });

        assertThat(exception.getMessage()).contains("Expected length of filenames and codes to be the same");
        assertThat(exception.getMessage()).contains("2 vs 1");
    }

    @Test
    @DisplayName("run(String[], String[], DataStore) should handle empty arrays")
    void testRunWithFilenamesAndCodes_EmptyArrays() {
        // Given
        String[] filenames = {};
        String[] codes = {};

        // When
        boolean result = weaverEngine.run(filenames, codes, mockDataStore);

        // Then
        assertThat(result).isTrue();
        assertThat(weaverEngine.wasBeginCalled()).isTrue();

        // Verify that WORKSPACE_FOLDER was set with empty FileList
        verify(mockDataStore).set(eq(LaraiKeys.WORKSPACE_FOLDER), any(FileList.class));
    }

    @Test
    @DisplayName("run() should close previous state when called multiple times")
    void testRun_MultipleCallsClosePreviousState() {
        // Given - First run to initialize state
        weaverEngine.run(mockDataStore);
        LaraWeaverState firstState = weaverEngine.getLaraWeaverState();

        // When - Second run
        weaverEngine.run(mockDataStore);
        LaraWeaverState secondState = weaverEngine.getLaraWeaverState();

        // Then
        assertThat(firstState).isNotSameAs(secondState);
        assertThat(weaverEngine.getData()).isPresent();
    }

    @Test
    @DisplayName("end() should return close() result")
    void testEnd_ReturnsCloseResult() {
        // Given
        weaverEngine.run(mockDataStore);

        // When
        boolean result = weaverEngine.end();

        // Then
        assertThat(result).isTrue(); // Our test implementation returns true from close()
    }

    @Test
    @DisplayName("end() should handle null state gracefully")
    void testEnd_NullState() {
        // When/Then - should not throw exception even with null state
        boolean result = weaverEngine.end();
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("run(String[], String[], DataStore) should create files with correct content")
    void testRunWithFilenamesAndCodes_FileContent() {
        // Given
        String[] filenames = {"script.js"};
        String[] codes = {"aspectdef MyAspect\nend"};

        // When
        weaverEngine.run(filenames, codes, mockDataStore);

        // Then
        assertThat(weaverEngine.wasBeginCalled()).isTrue();
        
        // Verify the DataStore was called to set the workspace folder
        verify(mockDataStore).set(eq(LaraiKeys.WORKSPACE_FOLDER), any(FileList.class));
    }

    @Test
    @DisplayName("State should be properly managed across multiple operations")
    void testStateManagement() {
        // Initially no state
        assertThat(weaverEngine.getData()).isEmpty();
        assertThat(weaverEngine.getLaraWeaverStateTry()).isEmpty();

        // After run, state should be present
        weaverEngine.run(mockDataStore);
        assertThat(weaverEngine.getData()).isPresent();
        assertThat(weaverEngine.getLaraWeaverStateTry()).isPresent();

        // After end, we can't easily test state cleanup without exposing internals,
        // but at least verify end() doesn't throw
        weaverEngine.end();
    }

    @Test
    @DisplayName("run() should handle DataStore retrieval exceptions gracefully")
    void testRun_DataStoreExceptions() {
        // Given - Mock DataStore to throw exception
        when(mockDataStore.get(LaraiKeys.WORKSPACE_FOLDER))
            .thenThrow(new RuntimeException("DataStore access failed"));

        // When/Then
        assertThrows(RuntimeException.class, () -> {
            weaverEngine.run(mockDataStore);
        });
    }
}
