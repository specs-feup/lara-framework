package org.lara.weavergen2.cli;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemErr;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Files;
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

    @Test
    void reportsExtraConcreteJoinpointFilesAfterGeneration() throws Exception {
        Path projectRoot = temp.resolve("project");
        Path outputDir = temp.resolve("out-extra");
        Path staleFile = projectRoot.resolve("minimal/pkg/joinpoints/MinimalGhost.java");
        Files.createDirectories(staleFile.getParent());
        Files.writeString(staleFile, "package minimal.pkg.joinpoints;\n\npublic class MinimalGhost { }\n");

        var generator = WeaverGen2.fromSpecs(BaseSpec.class, MinimalSpec.class, "java.lang.Object", projectRoot);

        String stderr = tapSystemErr(() -> {
            Throwable thrown = catchThrowable(() -> generator.generate(outputDir, outputDir.resolve("spec.json")));

            assertThat(thrown)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("not declared in the spec");
            assertThat(outputDir.resolve("minimal/pkg/abstracts/joinpoints/ARoot.java")).exists();
            assertThat(outputDir.resolve("spec.json")).exists();
        });

        assertThat(stderr)
                .contains("WeaverGen2: Found concrete joinpoint source files not declared in the spec:")
                .contains("MinimalGhost.java");
    }

    @Test
    void createsMissingConcreteJoinpointFilesAndAcceptsThem() throws Exception {
        Path projectRoot = temp.resolve("project-missing");
        Path outputDir = temp.resolve("out-missing");
        var generator = WeaverGen2.fromSpecs(BaseSpec.class, MinimalSpec.class, "java.lang.Object", projectRoot);

        generator.generate(outputDir, outputDir.resolve("spec.json"));

        assertThat(projectRoot.resolve("minimal/pkg/joinpoints/MinimalJoinpoint.java")).exists();
        assertThat(outputDir.resolve("minimal/pkg/abstracts/joinpoints/ARoot.java")).exists();
    }

    @Test
    void ignoresNonJavaFilesInConcreteJoinpointDirectory() throws Exception {
        Path projectRoot = temp.resolve("project-non-java");
        Path outputDir = temp.resolve("out-non-java");
        Path readme = projectRoot.resolve("minimal/pkg/joinpoints/README.md");
        Files.createDirectories(readme.getParent());
        Files.writeString(readme, "notes\n");

        var generator = WeaverGen2.fromSpecs(BaseSpec.class, MinimalSpec.class, "java.lang.Object", projectRoot);

        generator.generate(outputDir, outputDir.resolve("spec.json"));

        assertThat(outputDir.resolve("minimal/pkg/abstracts/joinpoints/ARoot.java")).exists();
    }

    @Test
    void reportsNestedExtraConcreteJoinpointFiles() throws Exception {
        Path projectRoot = temp.resolve("project-nested");
        Path outputDir = temp.resolve("out-nested");
        Path staleFile = projectRoot.resolve("minimal/pkg/joinpoints/legacy/MinimalOld.java");
        Files.createDirectories(staleFile.getParent());
        Files.writeString(staleFile, "package minimal.pkg.joinpoints.legacy;\n\npublic class MinimalOld { }\n");

        var generator = WeaverGen2.fromSpecs(BaseSpec.class, MinimalSpec.class, "java.lang.Object", projectRoot);

        String stderr = tapSystemErr(() -> {
            Throwable thrown = catchThrowable(() -> generator.generate(outputDir, outputDir.resolve("spec.json")));

            assertThat(thrown)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("not declared in the spec");
        });

        assertThat(stderr).contains("legacy/MinimalOld.java");
    }
}
