package org.lara.interpreter.weaver.interf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mockStatic;

import java.io.File;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lara.interpreter.weaver.fixtures.TestWeaverEngine;
import org.mockito.MockedStatic;

import pt.up.fe.specs.util.SpecsSystem;
import pt.up.fe.specs.util.exceptions.NotImplementedException;

class WeaverEngineTest {

    @AfterEach
    void cleanupThreadLocal() {
        // Ensure we don't leak thread-local state across tests
        if (WeaverEngine.isWeaverSet()) {
            WeaverEngine.removeWeaver();
        }
    }

    @Test
    @DisplayName("getName() returns simple class name by default")
    void testGetNameDefault() {
        var engine = new TestWeaverEngine();
        assertThat(engine.getName()).isEqualTo("TestWeaverEngine");
    }

    @Test
    @DisplayName("getNameAndBuild() appends build number when available")
    void testGetNameAndBuild_withBuildNumber() {
        try (MockedStatic<SpecsSystem> mocked = mockStatic(SpecsSystem.class)) {
            mocked.when(SpecsSystem::getBuildNumber).thenReturn("1234");

            var engine = new TestWeaverEngine();
            assertThat(engine.getNameAndBuild()).isEqualTo("TestWeaverEngine (build 1234)");
        }
    }

    @Test
    @DisplayName("getNameAndBuild() returns name when build number is null")
    void testGetNameAndBuild_withoutBuildNumber() {
        try (MockedStatic<SpecsSystem> mocked = mockStatic(SpecsSystem.class)) {
            mocked.when(SpecsSystem::getBuildNumber).thenReturn(null);

            var engine = new TestWeaverEngine();
            assertThat(engine.getNameAndBuild()).isEqualTo("TestWeaverEngine");
        }
    }

    @Test
    @DisplayName("StoreDefinition contains keys from getOptions() and uses weaver name")
    void testStoreDefinitionContainsKeysFromGetOptions() {
        var engine = new TestWeaverEngine();
        var storeDef = engine.getStoreDefinition();

        assertThat(storeDef.getName()).isEqualTo("TestWeaverEngine");
        assertThat(storeDef.hasKey("verbose")).isTrue();
        assertThat(storeDef.hasKey("target")).isTrue();
    }

    @Test
    @DisplayName("Temporary weaver folder lazy-creates, caches, and is unique per instance")
    void testTemporaryWeaverFolderBehavior() {
        var engine1 = new TestWeaverEngine();
        var engine2 = new TestWeaverEngine();

        assertThat(engine1.hasTemporaryWeaverFolder()).isFalse();
        File f1a = engine1.getTemporaryWeaverFolder();
        assertThat(f1a).exists().isDirectory();
        assertThat(engine1.hasTemporaryWeaverFolder()).isTrue();

        File f1b = engine1.getTemporaryWeaverFolder();
        assertThat(f1b.getAbsolutePath()).isEqualTo(f1a.getAbsolutePath());

        assertThat(engine2.hasTemporaryWeaverFolder()).isFalse();
        File f2 = engine2.getTemporaryWeaverFolder();
        assertThat(f2).exists().isDirectory();
        assertThat(f2.getAbsolutePath()).isNotEqualTo(f1a.getAbsolutePath());
    }

    @Test
    @DisplayName("Thread-local weaver: set, idempotent set, conflict on different instance, remove")
    void testThreadLocalWeaverManagement() {
        var engine1 = new TestWeaverEngine();
        var engine2 = new TestWeaverEngine();

        // Initially not set
        assertThat(WeaverEngine.isWeaverSet()).isFalse();
        assertThatThrownBy(WeaverEngine::getThreadLocalWeaver)
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Tried to get");

        // Set and verify
        engine1.setWeaver();
        assertThat(WeaverEngine.isWeaverSet()).isTrue();
        assertThat(WeaverEngine.getThreadLocalWeaver()).isSameAs(engine1);

        // Idempotent set should not throw
        engine1.setWeaver();

        // Setting a different instance should throw
        assertThatThrownBy(engine2::setWeaver)
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Trying to set a different thread-local weaver");

        // Remove and verify
        WeaverEngine.removeWeaver();
        assertThat(WeaverEngine.isWeaverSet()).isFalse();
        assertThatThrownBy(WeaverEngine::getThreadLocalWeaver)
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Tried to get");
    }

    @Test
    @DisplayName("writeCode() throws NotImplementedException by default")
    void testWriteCodeThrowsNotImplemented() {
        var engine = new TestWeaverEngine();
        assertThatExceptionOfType(NotImplementedException.class)
                .isThrownBy(() -> engine.writeCode(new File("/tmp/nonexistent_out")));
    }

    @Test
    @DisplayName("getDefaultAttribute returns default for supported JP and throws for unsupported")
    void testGetDefaultAttribute() {
        var engine = new TestWeaverEngine();
        // Root JP is named "root" in the test spec with default attribute "dump"
        assertThat(engine.getDefaultAttribute("root")).isEqualTo("dump");

        assertThatThrownBy(() -> engine.getDefaultAttribute("unsupported"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Used unsupported join point 'unsupported'");
    }
}
