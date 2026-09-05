package org.lara.interpreter.weaver.interf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.io.File;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lara.interpreter.weaver.fixtures.TestWeaverEngine;

import pt.up.fe.specs.util.exceptions.NotImplementedException;

class WeaverEngineTest {

    @Test
    @DisplayName("getName() returns simple class name by default")
    void testGetNameDefault() {
        var engine = new TestWeaverEngine();
        assertThat(engine.getName()).isEqualTo("TestWeaverEngine");
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
    @DisplayName("writeCode() throws NotImplementedException by default")
    void testWriteCodeThrowsNotImplemented() {
        var engine = new TestWeaverEngine();
        assertThatExceptionOfType(NotImplementedException.class)
                .isThrownBy(() -> engine.writeCode(new File("/tmp/nonexistent_out")));
    }
}
