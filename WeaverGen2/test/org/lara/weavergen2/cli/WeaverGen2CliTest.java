package org.lara.weavergen2.cli;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.lara.weavergen2.WeaverGen2;
import org.lara.weavergen2.fixtures.specs.base.BaseSpec;
import org.lara.weavergen2.fixtures.specs.valid.MinimalSpec;

class WeaverGen2CliTest {

    @TempDir
    Path temp;

    @Test
    void rejectsMissingSpecClass() {
        assertThatThrownBy(() -> WeaverGen2.main(new String[] {
                "no.such.Spec",
                temp.resolve("out").toString()
        }))
                .isInstanceOf(Exception.class);
    }

    @Test
    void runsWithBaseSpecAndSpecClasses() throws Exception {
        Path outputDir = temp.resolve("out");
        WeaverGen2.main(new String[] {
                MinimalSpec.class.getName(),
                outputDir.toString(),
                "--base",
                BaseSpec.class.getName(),
                "--node",
                "java.lang.Object"
        });

        assertThat(outputDir).exists();
    }
}
