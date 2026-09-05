package org.lara.weavergen2.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class GenerationProfileTest {

    @TempDir
    Path tempDir;

    @Test
    void derivesWeaverNamesAndPackagesForMergedBaseMode() {
        var config = new GenerationProfile("Cxx", "pt.up.fe.specs.clava.weaver", "java.lang.Object", true, true,
                Set.<MemberSignature>of(), Set.<WrapperSignature>of(), tempDir);

        assertThat(config.weaverName()).isEqualTo("CxxWeaver");
        assertThat(config.abstractWeaverPackage()).isEqualTo("pt.up.fe.specs.clava.weaver.abstracts.weaver");
        assertThat(config.joinPointPackage()).isEqualTo("pt.up.fe.specs.clava.weaver.abstracts.joinpoints");
    }

    @Test
    void derivesStandaloneWeaverNamesAndPackages() {
        var config = new GenerationProfile("Cxx", "pt.up.fe.specs.clava.weaver", "java.lang.Object", true, false,
                Set.<MemberSignature>of(), Set.<WrapperSignature>of(), tempDir);

        assertThat(config.weaverName()).isEqualTo("WeaverEngine");
        assertThat(config.abstractWeaverPackage()).isEqualTo("org.lara.interpreter.weaver.interf");
    }
}
