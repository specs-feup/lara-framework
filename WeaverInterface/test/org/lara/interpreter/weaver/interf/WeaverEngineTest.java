package org.lara.interpreter.weaver.interf;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.lara.interpreter.weaver.fixtures.TestWeaverEngine;
import org.lara.interpreter.weaver.options.WeaverOption;
import org.suikasoft.jOptions.storedefinition.StoreDefinition;
import pt.up.fe.specs.util.SpecsSystem;
import pt.up.fe.specs.util.exceptions.NotImplementedException;

import java.io.File;
import java.util.List;

public class WeaverEngineTest {

    private TestWeaverEngine weaverEngine;
    
    @TempDir
    File tempDir;

    @BeforeEach
    void setUp() {
        weaverEngine = new TestWeaverEngine();
        // Clean up any leftover thread-local state
        if (WeaverEngine.isWeaverSet()) {
            WeaverEngine.removeWeaver();
        }
    }

    @AfterEach
    void tearDown() {
        // Always clean up thread-local state
        if (WeaverEngine.isWeaverSet()) {
            WeaverEngine.removeWeaver();
        }
    }

    @Test
    void testGetNameDefault() {
        // getName() should return the simple name of the class by default
        String name = weaverEngine.getName();
        assertThat(name).isEqualTo("TestWeaverEngine");
    }

    @Test
    void testGetNameAndBuildWithoutBuildNumber() {
        // Mock SpecsSystem.getBuildNumber() to return null
        try (MockedStatic<SpecsSystem> mockedSpecsSystem = Mockito.mockStatic(SpecsSystem.class)) {
            mockedSpecsSystem.when(SpecsSystem::getBuildNumber).thenReturn(null);
            
            String nameAndBuild = weaverEngine.getNameAndBuild();
            assertThat(nameAndBuild).isEqualTo("TestWeaverEngine");
        }
    }

    @Test
    void testGetNameAndBuildWithBuildNumber() {
        // Mock SpecsSystem.getBuildNumber() to return a build number
        try (MockedStatic<SpecsSystem> mockedSpecsSystem = Mockito.mockStatic(SpecsSystem.class)) {
            mockedSpecsSystem.when(SpecsSystem::getBuildNumber).thenReturn("1.2.3-SNAPSHOT");
            
            String nameAndBuild = weaverEngine.getNameAndBuild();
            assertThat(nameAndBuild).isEqualTo("TestWeaverEngine (build 1.2.3-SNAPSHOT)");
        }
    }

    @Test
    void testStoreDefinitionContainsKeysFromGetOptions() {
        // Get the StoreDefinition and verify it contains keys from getOptions()
        StoreDefinition storeDefinition = weaverEngine.getStoreDefinition();
        List<WeaverOption> options = weaverEngine.getOptions();
        
        assertThat(storeDefinition).isNotNull();
        assertThat(storeDefinition.getName()).isEqualTo("TestWeaverEngine");
        
        // Verify that all keys from getOptions() are in the store definition
        for (WeaverOption option : options) {
            assertThat(storeDefinition.hasKey(option.dataKey().getName()))
                .as("Store definition should contain key: %s", option.dataKey().getName())
                .isTrue();
        }
    }

    @Test
    void testTemporaryWeaverFolderCreation() {
        // Initially, no temporary folder should be created
        assertThat(weaverEngine.hasTemporaryWeaverFolder()).isFalse();
        
        // Access the temporary folder to trigger creation
        File tempFolder1 = weaverEngine.getTemporaryWeaverFolder();
        
        // Now it should exist
        assertThat(weaverEngine.hasTemporaryWeaverFolder()).isTrue();
        assertThat(tempFolder1).isNotNull();
        assertThat(tempFolder1.exists()).isTrue();
        assertThat(tempFolder1.isDirectory()).isTrue();
        assertThat(tempFolder1.getName()).startsWith("lara_weaver_");
        
        // Multiple calls should return the same folder (caching)
        File tempFolder2 = weaverEngine.getTemporaryWeaverFolder();
        assertThat(tempFolder2).isSameAs(tempFolder1);
    }

    @Test
    void testTemporaryWeaverFolderUnicity() {
        // Create two different weaver engines and verify they get different temp folders
        TestWeaverEngine weaver1 = new TestWeaverEngine();
        TestWeaverEngine weaver2 = new TestWeaverEngine();
        
        File tempFolder1 = weaver1.getTemporaryWeaverFolder();
        File tempFolder2 = weaver2.getTemporaryWeaverFolder();
        
        assertThat(tempFolder1).isNotEqualTo(tempFolder2);
        assertThat(tempFolder1.getName()).isNotEqualTo(tempFolder2.getName());
    }

    @Test
    void testThreadLocalSetWeaver() {
        // Initially no weaver should be set
        assertThat(WeaverEngine.isWeaverSet()).isFalse();
        
        // Set the weaver
        weaverEngine.setWeaver();
        
        assertThat(WeaverEngine.isWeaverSet()).isTrue();
        assertThat(WeaverEngine.getThreadLocalWeaver()).isSameAs(weaverEngine);
    }

    @Test
    void testThreadLocalIdempotentSet() {
        // Setting the same weaver multiple times should work
        weaverEngine.setWeaver();
        assertThat(WeaverEngine.getThreadLocalWeaver()).isSameAs(weaverEngine);
        
        // Setting again should work (idempotent)
        weaverEngine.setWeaver();
        assertThat(WeaverEngine.getThreadLocalWeaver()).isSameAs(weaverEngine);
    }

    @Test
    void testThreadLocalConflictOnDifferentInstance() {
        // Set first weaver
        weaverEngine.setWeaver();
        
        // Try to set different weaver - should throw exception
        TestWeaverEngine differentWeaver = new TestWeaverEngine();
        
        assertThatThrownBy(() -> differentWeaver.setWeaver())
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Trying to set a different thread-local weaver");
    }

    @Test
    void testThreadLocalRemoveWeaver() {
        // Set weaver
        weaverEngine.setWeaver();
        assertThat(WeaverEngine.isWeaverSet()).isTrue();
        
        // Remove weaver
        WeaverEngine.removeWeaver();
        assertThat(WeaverEngine.isWeaverSet()).isFalse();
        
        // Removing again should not cause issues
        WeaverEngine.removeWeaver();
        assertThat(WeaverEngine.isWeaverSet()).isFalse();
    }

    @Test
    void testWriteCodeThrowsNotImplementedException() {
        // writeCode() should throw NotImplementedException by default
        assertThatThrownBy(() -> weaverEngine.writeCode(tempDir))
            .isInstanceOf(NotImplementedException.class)
            .hasMessageContaining("TestWeaverEngine.writeCode() not yet implemented!");
    }

    @Test
    void testGetDefaultAttributeForSupportedJoinPoint() {
        // Get default attribute for supported join point "root"
        String defaultAttribute = weaverEngine.getDefaultAttribute("root");
        assertThat(defaultAttribute).isEqualTo("dump");
    }

    @Test
    void testGetDefaultAttributeThrowsForUnsupportedJoinPoint() {
        // Get default attribute for unsupported join point should throw
        assertThatThrownBy(() -> weaverEngine.getDefaultAttribute("unsupported"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Used unsupported join point 'unsupported'");
    }
}