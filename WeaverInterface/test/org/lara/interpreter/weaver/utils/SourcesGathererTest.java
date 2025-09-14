package org.lara.interpreter.weaver.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import pt.up.fe.specs.util.SpecsIo;

class SourcesGathererTest {

    @TempDir
    File tmp;

    @Test
    @DisplayName("SourcesGatherer builds mapping from files and directories, filtering by extension")
    void gathersSources() throws IOException {
        File dir = new File(tmp, "src");
        File sub = new File(dir, "sub");
        SpecsIo.mkdir(dir);
        SpecsIo.mkdir(sub);
        File a = new File(dir, "a.c");
        File b = new File(sub, "b.c");
        File other = new File(sub, "ignored.txt");
        SpecsIo.write(a, "int a;\n");
        SpecsIo.write(b, "int b;\n");
        SpecsIo.write(other, "nope\n");

        var sg = SourcesGatherer.build(List.of(dir), List.of("c"));
        var map = sg.getSourceFiles();

        assertThat(map).containsKeys(a, b);
        // All mapped files should point to the original root directory
        assertThat(map.get(a)).isEqualTo(dir);
        assertThat(map.get(b)).isEqualTo(dir);
        // ignored.txt not present
        assertThat(map).doesNotContainKey(other);
    }
}
