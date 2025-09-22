package org.lara.interpreter.joptions.config.interpreter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import javax.swing.JFileChooser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.lara.interpreter.joptions.keys.FileList;
import org.lara.interpreter.joptions.keys.OptionalFile;
import org.lara.interpreter.weaver.fixtures.TestDataStores;
import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Interfaces.DataStore;

import pt.up.fe.specs.util.SpecsIo;

class LaraIKeyFactoryTest {

    @TempDir
    File tmp;

    @Test
    @DisplayName("fileList/customGetterFileList enforces selection mode and processes items")
    void fileListProcessing() throws IOException {
        File f = new File(tmp, "a.txt");
        Files.writeString(f.toPath(), "a");
        File d = new File(tmp, "dir");
        // noinspection ResultOfMethodCallIgnored
        d.mkdirs();

        DataKey<FileList> keyFilesOnly = LaraIKeyFactory.fileList("filesOnly", JFileChooser.FILES_ONLY, List.of());
        DataKey<FileList> keyDirsOnly = LaraIKeyFactory.fileList("dirsOnly", JFileChooser.DIRECTORIES_ONLY, List.of());

        DataStore ds = TestDataStores.withWorkingFolder(tmp, false);

        // Files only: directories are not allowed -> expect exception
        assertThatThrownBy(() -> keyFilesOnly.getCustomGetter().get().get(FileList.newInstance(f, d), ds))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("directory");

        // Dirs only: files are not allowed -> expect exception
        assertThatThrownBy(() -> keyDirsOnly.getCustomGetter().get().get(FileList.newInstance(d, f), ds))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("file");
    }

    @Test
    @DisplayName("file(selectionMode, create=true) creates folder for directories and handles relative/absolute")
    void fileCreateAndRelativeBehavior() throws IOException {
        // Working folder setup
        DataStore dsRel = TestDataStores.withWorkingFolder(tmp, true);
        DataStore dsAbs = TestDataStores.withWorkingFolder(tmp, false);

        // Directory creation when create=true
        DataKey<File> dirKey = LaraIKeyFactory.file("out", JFileChooser.DIRECTORIES_ONLY, true, List.of());
        File target = new File(tmp, "created/sub");
        File produced = dirKey.getCustomGetter().get().get(target, dsAbs);
        assertThat(produced).isDirectory();

        // FILES_ONLY with empty path and create=false should return the same empty path
        DataKey<File> fileKey = LaraIKeyFactory.file("file", JFileChooser.FILES_ONLY, false, List.of());
        File empty = new File("");
        File processedEmpty = fileKey.getCustomGetter().get().get(empty, dsAbs);
        assertThat(processedEmpty.getPath()).isEmpty();

        // Relative paths when enabled
        File absFile = new File(tmp, "hello.txt");
        SpecsIo.write(absFile, "hi");
        File relProduced = fileKey.getCustomGetter().get().get(absFile, dsRel);
        assertThat(relProduced.getPath()).isEqualTo("hello.txt");

        // Absolute/canonical when relative disabled
        File absProduced = fileKey.getCustomGetter().get().get(absFile, dsAbs);
        assertThat(absProduced.isAbsolute()).isTrue();
        assertThat(absProduced).isEqualTo(SpecsIo.getCanonicalFile(absProduced));
    }

    @Test
    @DisplayName("optionalFile custom getter resolves against working folder when present")
    void optionalFileProcessing() {
        DataKey<OptionalFile> optKey = LaraIKeyFactory.optionalFile("opt", false);
        DataStore ds = TestDataStores.withWorkingFolder(tmp, false);

        OptionalFile of = OptionalFile.newInstance("x.txt");
        OptionalFile processed = optKey.getCustomGetter().get().get(of, ds);
        // With relative paths disabled, the file is resolved against
        // CURRENT_FOLDER_PATH and canonicalized
        assertThat(processed.getFile()).isEqualTo(SpecsIo.getCanonicalFile(new File(tmp, "x.txt")));

        OptionalFile none = OptionalFile.newInstance("");
        OptionalFile processedNone = optKey.getCustomGetter().get().get(none, ds);
        assertThat(processedNone.getFile().getPath()).isEmpty();
    }

    @Test
    @DisplayName("customGetterLaraArgs trims whitespace")
    void customGetterLaraArgsTrims() {
        assertThat(LaraIKeyFactory.customGetterLaraArgs("  a b  ", TestDataStores.empty())).isEqualTo("a b");
        assertThat(LaraIKeyFactory.customGetterLaraArgs("\t\na\n", TestDataStores.empty())).isEqualTo("a");
    }
}
