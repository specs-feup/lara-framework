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
import org.lara.weavergen2.fixtures.specs.valid.EdgeSpec;
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
                    .hasMessageContaining("non-conforming concrete joinpoint source file");
            assertThat(outputDir.resolve("minimal/pkg/abstracts/joinpoints/ARoot.java")).exists();
            assertThat(outputDir.resolve("spec.json")).exists();
        });

        assertThat(stderr)
                .contains("WeaverGen2: Found non-conforming concrete joinpoint source files:")
                .contains("MinimalGhost.java")
                .contains("is not declared in the spec");
    }

    @Test
    void createsMissingConcreteJoinpointFilesAndAcceptsThem() throws Exception {
        Path projectRoot = temp.resolve("project-missing");
        Path outputDir = temp.resolve("out-missing");
        var generator = WeaverGen2.fromSpecs(BaseSpec.class, MinimalSpec.class, "java.lang.Object", projectRoot);

        generator.generate(outputDir, outputDir.resolve("spec.json"));

        assertThat(projectRoot.resolve("minimal/pkg/joinpoints/MinimalJoinpoint.java")).exists();
        assertThat(projectRoot.resolve("minimal/pkg/joinpoints/MinimalRoot.java")).exists();
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
                    .hasMessageContaining("non-conforming concrete joinpoint source file");
        });

        assertThat(stderr).contains("legacy/MinimalOld.java");
    }

    @Test
    void acceptsExpectedConcreteJoinpointFilesInNestedDirectories() throws Exception {
        Path projectRoot = temp.resolve("project-nested-expected");
        Path outputDir = temp.resolve("out-nested-expected");
        Path level2File = projectRoot.resolve("edge/pkg/joinpoints/nested/EdgeLevel2.java");
        Files.createDirectories(level2File.getParent());
        Files.writeString(level2File, "package edge.pkg.joinpoints.nested;\n\n"
                + "import edge.pkg.EdgeWeaver;\n"
                + "import edge.pkg.abstracts.joinpoints.ALevel2;\n\n"
                + "public class EdgeLevel2<Self extends EdgeLevel2<Self>> extends ALevel2<Self> {\n"
                + "    protected EdgeLevel2(Object node, EdgeWeaver weaver) {\n"
                + "        super(node, weaver);\n"
                + "    }\n"
                + "}\n");

        var generator = WeaverGen2.fromSpecs(BaseSpec.class, EdgeSpec.class, "java.lang.Object", projectRoot);

        generator.generate(outputDir, outputDir.resolve("spec.json"));

        assertThat(Files.readString(outputDir.resolve("edge/pkg/abstracts/joinpoints/AReservedKeyword.java")))
                .contains("import edge.pkg.joinpoints.nested.EdgeLevel2;");
    }

    @Test
    void reportsDuplicateExpectedConcreteJoinpointFiles() throws Exception {
        Path projectRoot = temp.resolve("project-duplicate-expected");
        Path outputDir = temp.resolve("out-duplicate-expected");
        Path rootFile = projectRoot.resolve("edge/pkg/joinpoints/EdgeLevel2.java");
        Path nestedFile = projectRoot.resolve("edge/pkg/joinpoints/nested/EdgeLevel2.java");
        Files.createDirectories(nestedFile.getParent());
        Files.writeString(rootFile, "package edge.pkg.joinpoints;\n\n"
                + "import edge.pkg.EdgeWeaver;\n"
                + "import edge.pkg.abstracts.joinpoints.ALevel2;\n\n"
                + "public class EdgeLevel2<Self extends EdgeLevel2<Self>> extends ALevel2<Self> {\n"
                + "    protected EdgeLevel2(Object node, EdgeWeaver weaver) {\n"
                + "        super(node, weaver);\n"
                + "    }\n"
                + "}\n");
        Files.writeString(nestedFile, "package edge.pkg.joinpoints.nested;\n\n"
                + "import edge.pkg.EdgeWeaver;\n"
                + "import edge.pkg.abstracts.joinpoints.ALevel2;\n\n"
                + "public class EdgeLevel2<Self extends EdgeLevel2<Self>> extends ALevel2<Self> {\n"
                + "    protected EdgeLevel2(Object node, EdgeWeaver weaver) {\n"
                + "        super(node, weaver);\n"
                + "    }\n"
                + "}\n");

        var generator = WeaverGen2.fromSpecs(BaseSpec.class, EdgeSpec.class, "java.lang.Object", projectRoot);

        String stderr = tapSystemErr(() -> {
            Throwable thrown = catchThrowable(() -> generator.generate(outputDir, outputDir.resolve("spec.json")));

            assertThat(thrown)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("non-conforming concrete joinpoint source file");
            assertThat(outputDir.resolve("edge/pkg/abstracts/joinpoints/ALevel2.java")).exists();
        });

        assertThat(stderr)
                .contains("EdgeLevel2.java: duplicates declared joinpoint source file set")
                .contains("nested/EdgeLevel2.java: duplicates declared joinpoint source file set");
    }

    @Test
    void reportsWrongConcreteJoinpointDeclarationAfterGeneration() throws Exception {
        Path projectRoot = temp.resolve("project-wrong-declaration");
        Path outputDir = temp.resolve("out-wrong-declaration");
        Path rootFile = projectRoot.resolve("minimal/pkg/joinpoints/MinimalRoot.java");
        Files.createDirectories(rootFile.getParent());
        Files.writeString(rootFile, "package minimal.pkg.joinpoints;\n\n"
                + "public class MinimalRoot {\n"
                + "}\n");

        var generator = WeaverGen2.fromSpecs(BaseSpec.class, MinimalSpec.class, "java.lang.Object", projectRoot);

        String stderr = tapSystemErr(() -> {
            Throwable thrown = catchThrowable(() -> generator.generate(outputDir, outputDir.resolve("spec.json")));

            assertThat(thrown)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("non-conforming concrete joinpoint source file");
            assertThat(outputDir.resolve("minimal/pkg/abstracts/joinpoints/ARoot.java")).exists();
            assertThat(outputDir.resolve("spec.json")).exists();
        });

        assertThat(stderr)
                .contains("MinimalRoot.java")
                .contains("has declaration 'public class MinimalRoot'")
                .contains("public class MinimalRoot<Self extends MinimalRoot<Self>> extends ARoot<Self>");

        assertThat(Files.readString(rootFile)).contains("public class MinimalRoot");
    }

    @Test
    void usesParentConcreteConstructorNodeTypeForChildAbstractJoinpoint() throws Exception {
        Path projectRoot = temp.resolve("project-parent-node-type");
        Path outputDir = temp.resolve("out-parent-node-type");
        Path level1File = projectRoot.resolve("edge/pkg/joinpoints/EdgeLevel1.java");
        Files.createDirectories(level1File.getParent());
        Files.writeString(level1File, "package edge.pkg.joinpoints;\n\n"
                + "import edge.pkg.EdgeWeaver;\n"
                + "import edge.pkg.abstracts.joinpoints.ALevel1;\n\n"
                + "public class EdgeLevel1<Self extends EdgeLevel1<Self>> extends ALevel1<Self> {\n"
                + "    protected EdgeLevel1(String node, EdgeWeaver weaver) {\n"
                + "        super(node, weaver);\n"
                + "    }\n"
                + "}\n");

        var generator = WeaverGen2.fromSpecs(BaseSpec.class, EdgeSpec.class, "java.lang.Object", projectRoot);

        generator.generate(outputDir, outputDir.resolve("spec.json"));

        assertThat(Files.readString(outputDir.resolve("edge/pkg/abstracts/joinpoints/ALevel2.java")))
                .contains("public ALevel2(String node, EdgeWeaver weaver)")
                .contains("super(node, weaver);");
        assertThat(Files.readString(projectRoot.resolve("edge/pkg/joinpoints/EdgeLevel2.java")))
                .contains("public class EdgeLevel2<Self extends EdgeLevel2<Self>> extends ALevel2<Self>")
                .contains("protected EdgeLevel2(String node, EdgeWeaver weaver)");
    }
}
