package org.lara.interpreter.joptions.panels;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.lara.interpreter.joptions.keys.FileList;
import org.lara.interpreter.weaver.fixtures.TestDataStores;
import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Datakey.KeyFactory;

@DisplayName("FileListPanel")
class FileListPanelTest {

    @TempDir
    File tmp;

    @Test
    @DisplayName("getValue returns empty FileList when no files added")
    void getValueReturnsEmptyWhenNoFiles() {
        DataKey<FileList> fileListKey = KeyFactory.object("files", FileList.class)
                .setDefault(FileList::newInstance);
        DataKey<File> fileKey = KeyFactory.file("file");

        FileListPanel panel = new FileListPanel(fileListKey, fileKey, TestDataStores.empty(),
                JFileChooser.FILES_ONLY, List.of());

        FileList result = panel.getValue();
        assertThat(result.getFiles()).isEmpty();
    }

    @Test
    @DisplayName("panel accepts FILES_ONLY selection mode")
    void panelAcceptsFilesOnlyMode() {
        DataKey<FileList> fileListKey = KeyFactory.object("files", FileList.class)
                .setDefault(FileList::newInstance);
        DataKey<File> fileKey = KeyFactory.file("file");

        // Should not throw exception
        FileListPanel panel = new FileListPanel(fileListKey, fileKey, TestDataStores.empty(),
                JFileChooser.FILES_ONLY, List.of("txt", "java"));

        assertThat(panel).isNotNull();
    }

    @Test
    @DisplayName("panel accepts DIRECTORIES_ONLY selection mode")
    void panelAcceptsDirectoriesOnlyMode() {
        DataKey<FileList> fileListKey = KeyFactory.object("dirs", FileList.class)
                .setDefault(FileList::newInstance);
        DataKey<File> fileKey = KeyFactory.file("dir");

        FileListPanel panel = new FileListPanel(fileListKey, fileKey, TestDataStores.empty(),
                JFileChooser.DIRECTORIES_ONLY, List.of());

        assertThat(panel).isNotNull();
    }

    @Test
    @DisplayName("panel handles empty extensions list")
    void panelHandlesEmptyExtensions() {
        DataKey<FileList> fileListKey = KeyFactory.object("files", FileList.class)
                .setDefault(FileList::newInstance);
        DataKey<File> fileKey = KeyFactory.file("file");

        // Should not throw exception with empty extensions
        FileListPanel panel = new FileListPanel(fileListKey, fileKey, TestDataStores.empty(),
                JFileChooser.FILES_ONLY, List.of());

        assertThat(panel).isNotNull();
    }

    @Test
    @DisplayName("panel initializes with non-null components")
    void panelInitializesWithNonNullComponents() {
        DataKey<FileList> fileListKey = KeyFactory.object("files", FileList.class)
                .setDefault(FileList::newInstance);
        DataKey<File> fileKey = KeyFactory.file("file");

        FileListPanel panel = new FileListPanel(fileListKey, fileKey, TestDataStores.empty(),
                JFileChooser.FILES_ONLY, List.of("txt"));

        // Panel should initialize successfully
        assertThat(panel).isNotNull();
        assertThat(panel.getValue()).isNotNull();
        assertThat(panel.getValue().getFiles()).isEmpty();
    }

    @Test
    @DisplayName("panel with multiple extensions initializes correctly")
    void panelWithMultipleExtensions() {
        DataKey<FileList> fileListKey = KeyFactory.object("files", FileList.class)
                .setDefault(FileList::newInstance);
        DataKey<File> fileKey = KeyFactory.file("file");

        FileListPanel panel = new FileListPanel(fileListKey, fileKey, TestDataStores.empty(),
                JFileChooser.FILES_ONLY, List.of("txt", "md", "java", "xml"));

        assertThat(panel).isNotNull();
        assertThat(panel.getValue().getFiles()).isEmpty();
    }
}
