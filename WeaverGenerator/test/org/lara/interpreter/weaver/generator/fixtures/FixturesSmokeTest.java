package org.lara.interpreter.weaver.generator.fixtures;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Basic smoke test to ensure fixtures work and minimal spec files can be written.
 */
public class FixturesSmokeTest {

    @TempDir
    Path temp;

    @Test
    public void writesMinimalSpecTriplet() throws Exception {
        Path specDir = temp.resolve("spec");
        Files.createDirectories(specDir);

        Path jpm = SpecXmlBuilder.writeMinimalJoinPointModel(specDir);
        Path am = SpecXmlBuilder.writeMinimalActionModel(specDir);
        Path arts = SpecXmlBuilder.writeMinimalArtifacts(specDir);

        assertThat(Files.isRegularFile(jpm)).isTrue();
        assertThat(Files.isRegularFile(am)).isTrue();
        assertThat(Files.isRegularFile(arts)).isTrue();
    }
}
