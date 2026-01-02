package org.lara.interpreter.joptions.panels;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.lara.interpreter.joptions.keys.OptionalFile;
import org.lara.interpreter.weaver.fixtures.TestDataStores;
import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Datakey.KeyFactory;

@DisplayName("FileWithCheckBoxPanel")
class FileWithCheckBoxPanelTest {

    @TempDir
    File tmp;

    @Test
    @DisplayName("panel accepts FILES_ONLY mode")
    void panelAcceptsFilesOnlyMode() {
        DataKey<OptionalFile> key = KeyFactory.object("optFile", OptionalFile.class)
                .setDecoder(OptionalFile.getCodec())
                .setDefault(() -> OptionalFile.newInstance(null));

        // Should not throw exception
        FileWithCheckBoxPanel panel = new FileWithCheckBoxPanel(key,
                TestDataStores.withWorkingFolder(tmp, false),
                JFileChooser.FILES_ONLY, List.of("txt", "java"));

        assertThat(panel).isNotNull();
    }

    @Test
    @DisplayName("panel accepts DIRECTORIES_ONLY mode")
    void panelAcceptsDirectoriesOnlyMode() {
        DataKey<OptionalFile> key = KeyFactory.object("optDir", OptionalFile.class)
                .setDecoder(OptionalFile.getCodec())
                .setDefault(() -> OptionalFile.newInstance(null));

        FileWithCheckBoxPanel panel = new FileWithCheckBoxPanel(key,
                TestDataStores.withWorkingFolder(tmp, false),
                JFileChooser.DIRECTORIES_ONLY, List.of());

        assertThat(panel).isNotNull();
    }

    @Test
    @DisplayName("panel handles empty extensions list")
    void panelHandlesEmptyExtensions() {
        DataKey<OptionalFile> key = KeyFactory.object("optFile", OptionalFile.class)
                .setDecoder(OptionalFile.getCodec())
                .setDefault(() -> OptionalFile.newInstance(null));

        // Should not throw exception with empty extensions
        FileWithCheckBoxPanel panel = new FileWithCheckBoxPanel(key,
                TestDataStores.withWorkingFolder(tmp, false),
                JFileChooser.FILES_AND_DIRECTORIES, List.of());

        assertThat(panel).isNotNull();
    }

    @Test
    @DisplayName("panel with specific extensions initializes correctly")
    void panelWithSpecificExtensions() {
        DataKey<OptionalFile> key = KeyFactory.object("optFile", OptionalFile.class)
                .setDecoder(OptionalFile.getCodec())
                .setDefault(() -> OptionalFile.newInstance(null));

        FileWithCheckBoxPanel panel = new FileWithCheckBoxPanel(key,
                TestDataStores.withWorkingFolder(tmp, false),
                JFileChooser.FILES_ONLY, List.of("txt", "md", "java"));

        assertThat(panel).isNotNull();
    }

    @Test
    @DisplayName("getText and setText work correctly")
    void getTextAndSetTextWork() {
        DataKey<OptionalFile> key = KeyFactory.object("optFile", OptionalFile.class)
                .setDecoder(OptionalFile.getCodec())
                .setDefault(() -> OptionalFile.newInstance(null));

        FileWithCheckBoxPanel panel = new FileWithCheckBoxPanel(key,
                TestDataStores.withWorkingFolder(tmp, false));

        String testPath = "/path/to/file.txt";
        panel.setText(testPath);

        assertThat(panel.getText()).isEqualTo(testPath);
    }

    @Test
    @DisplayName("getCheckBox returns non-null checkbox")
    void getCheckBoxReturnsNonNull() {
        DataKey<OptionalFile> key = KeyFactory.object("optFile", OptionalFile.class)
                .setDecoder(OptionalFile.getCodec())
                .setDefault(() -> OptionalFile.newInstance(null));

        FileWithCheckBoxPanel panel = new FileWithCheckBoxPanel(key,
                TestDataStores.withWorkingFolder(tmp, false));

        assertThat(panel.getCheckBox()).isNotNull();
    }

    @Test
    @DisplayName("panel initializes with default checkbox state")
    void panelInitializesWithDefaultCheckboxState() {
        DataKey<OptionalFile> key = KeyFactory.object("optFile", OptionalFile.class)
                .setDecoder(OptionalFile.getCodec())
                .setDefault(() -> OptionalFile.newInstance(null));

        FileWithCheckBoxPanel panel = new FileWithCheckBoxPanel(key,
                TestDataStores.withWorkingFolder(tmp, false));

        // Checkbox should be unchecked by default
        assertThat(panel.getCheckBox().isSelected()).isFalse();
    }

    @Test
    @DisplayName("panel with multiple file extensions initializes")
    void panelWithMultipleExtensions() {
        DataKey<OptionalFile> key = KeyFactory.object("optFile", OptionalFile.class)
                .setDecoder(OptionalFile.getCodec())
                .setDefault(() -> OptionalFile.newInstance(null));

        FileWithCheckBoxPanel panel = new FileWithCheckBoxPanel(key,
                TestDataStores.withWorkingFolder(tmp, false),
                JFileChooser.FILES_ONLY, List.of("txt", "md", "java", "xml", "json"));

        assertThat(panel).isNotNull();
    }
}
