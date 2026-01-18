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

    @Test
    @DisplayName("Empty inputs produce an empty mapping")
    void emptyInputs() {
        var sg = SourcesGatherer.build(List.of(), List.of("c"));
        assertThat(sg.getSourceFiles()).isEmpty();
    }

    @Test
    @DisplayName("Invalid (non-existent) inputs are ignored without exceptions")
    void invalidInputsIgnored() {
        File missing = new File(tmp, "does-not-exist");
        var sg = SourcesGatherer.build(List.of(missing), List.of("c"));
        assertThat(sg.getSourceFiles()).isEmpty();
    }

    @Test
    @DisplayName("Mixed file and folder inputs are mapped to their respective original roots")
    void mixedFileAndFolderInputs() throws IOException {
        // Prepare structure
        File rootDir = new File(tmp, "root");
        SpecsIo.mkdir(rootDir);
        File sub = new File(rootDir, "sub");
        SpecsIo.mkdir(sub);
        File inDir = new File(sub, "dirfile.c");
        SpecsIo.write(inDir, "int x;\n");

        File fileRoot = new File(tmp, "single.c");
        SpecsIo.write(fileRoot, "int y;\n");

        var sg = SourcesGatherer.build(List.of(fileRoot, rootDir), List.of("c"));
        var map = sg.getSourceFiles();

        // Expect both files discovered
        assertThat(map).containsKeys(fileRoot, inDir);
        // A standalone file is mapped to itself as the original root
        assertThat(map.get(fileRoot)).isEqualTo(fileRoot);
        // A file found inside a folder is mapped to that folder as original root
        assertThat(map.get(inDir)).isEqualTo(rootDir);
    }

    @Test
    @DisplayName("Multiple independent roots are gathered correctly")
    void multipleRoots() throws IOException {
        File dir1 = new File(tmp, "dir1");
        File dir2 = new File(tmp, "dir2");
        SpecsIo.mkdir(dir1);
        SpecsIo.mkdir(dir2);
        File a = new File(dir1, "a.c");
        File b = new File(dir2, "b.c");
        SpecsIo.write(a, "int a;\n");
        SpecsIo.write(b, "int b;\n");

        var sg = SourcesGatherer.build(List.of(dir1, dir2), List.of("c"));
        var map = sg.getSourceFiles();

        assertThat(map).hasSize(2);
        assertThat(map).containsEntry(a, dir1).containsEntry(b, dir2);
    }

    @Test
    @DisplayName("Deeply nested directories are recursively scanned")
    void deepRecursion() throws IOException {
        File root = new File(tmp, "deep");
        SpecsIo.mkdir(root);
        File l1 = new File(root, "l1");
        File l2 = new File(l1, "l2");
        File l3 = new File(l2, "l3");
        SpecsIo.mkdir(l1);
        SpecsIo.mkdir(l2);
        SpecsIo.mkdir(l3);
        File f1 = new File(l1, "f1.c");
        File f2 = new File(l2, "f2.c");
        File f3 = new File(l3, "f3.c");
        SpecsIo.write(f1, "int f1;\n");
        SpecsIo.write(f2, "int f2;\n");
        SpecsIo.write(f3, "int f3;\n");

        var sg = SourcesGatherer.build(List.of(root), List.of("c"));
        var map = sg.getSourceFiles();

        assertThat(map).containsKeys(f1, f2, f3);
        assertThat(map.get(f1)).isEqualTo(root);
        assertThat(map.get(f2)).isEqualTo(root);
        assertThat(map.get(f3)).isEqualTo(root);
    }

    @Test
    @DisplayName("Non-matching extensions are ignored")
    void nonMatchingExtensionsIgnored() throws IOException {
        File dir = new File(tmp, "nomatch");
        SpecsIo.mkdir(dir);
        File javaFile = new File(dir, "A.java");
        SpecsIo.write(javaFile, "class A {}\n");

        var sg = SourcesGatherer.build(List.of(dir), List.of("c"));
        assertThat(sg.getSourceFiles()).doesNotContainKey(javaFile);
    }
}
