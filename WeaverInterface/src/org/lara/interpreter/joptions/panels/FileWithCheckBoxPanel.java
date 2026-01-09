/**
 * Copyright 2016 SPeCS.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

package org.lara.interpreter.joptions.panels;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.lara.interpreter.joptions.keys.OptionalFile;
import org.suikasoft.jOptions.JOptionKeys;
import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Interfaces.DataStore;
import org.suikasoft.jOptions.gui.KeyPanel;

import pt.up.fe.specs.util.SpecsIo;

public class FileWithCheckBoxPanel extends KeyPanel<OptionalFile> {

    private static final long serialVersionUID = 1L;
    private JCheckBox checkBox;
    private JFileChooser fc;
    private JTextField textField;
    private JButton browseButton;

    public FileWithCheckBoxPanel(DataKey<OptionalFile> key, DataStore data) {
        this(key, data, JFileChooser.FILES_AND_DIRECTORIES, Collections.emptyList());
    }

    public FileWithCheckBoxPanel(DataKey<OptionalFile> key, DataStore data, int fileChooserMode,
            Collection<String> extensions) {
        super(key, data);

        setLayout(new BorderLayout());
        addCheckBox();
        addFileField(key, fileChooserMode, extensions);
    }

    private void addFileField(DataKey<OptionalFile> key, int fileChooserMode, Collection<String> extensions) {
        textField = new JTextField();
        browseButton = new JButton();

        // Init file chooser
        fc = new JFileChooser();
        fc.setFileSelectionMode(fileChooserMode);

        // Set extensions
        if (!extensions.isEmpty()) {
            String extensionsString = extensions.stream().collect(Collectors.joining(", *.", "*.", ""));
            FileFilter filter = new FileNameExtensionFilter(key.getLabel() + " files (" + extensionsString + ")",
                    extensions.toArray(new String[0]));

            fc.setFileFilter(filter);
        }

        // Init browse button
        browseButton.setText("Browse...");
        browseButton.addActionListener(this::browseButtonActionPerformed);
        checkBox.addActionListener(this::checkBoxActionPerformed);
        redefineCheckBox(false);
        add(textField, BorderLayout.CENTER);
        add(browseButton, BorderLayout.EAST);
    }

    private void redefineCheckBox(boolean state) {
        checkBox.setSelected(state);
        textField.setEnabled(state);
        browseButton.setEnabled(state);
    }

    private void addCheckBox() {
        checkBox = new JCheckBox();
        add(checkBox, BorderLayout.WEST);
    }

    public JCheckBox getCheckBox() {
        return checkBox;
    }

    public void setText(String text) {
        textField.setText(text);
    }

    public String getText() {
        return textField.getText();
    }

    private void checkBoxActionPerformed(ActionEvent evt) {
        boolean enabled = checkBox.isSelected();
        textField.setEnabled(enabled);
        browseButton.setEnabled(enabled);

    }

    private void browseButtonActionPerformed(ActionEvent evt) {

        fc.setCurrentDirectory(getOptionFile());
        int returnVal = fc.showOpenDialog(this);

        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = fc.getSelectedFile();

        textField.setText(processFile(file));
    }

    private File getOptionFile() {
        String fieldValue = textField.getText();

        DataStore tempData = DataStore.newInstance("FilePanelTemp", getData());
        // When reading a value from the GUI to the user DataStore, use absolute path
        tempData.set(JOptionKeys.USE_RELATIVE_PATHS, false);
        tempData.setString(getKey(), fieldValue);

        File value = tempData.get(getKey()).getFile();

        return value;

    }

    @Override
    public OptionalFile getValue() {
        return new OptionalFile(getOptionFile(), checkBox.isSelected());
    }

    @Override
    public <ET extends OptionalFile> void setValue(ET value) {
        String fileStr = processFile(value.getFile());
        setText(fileStr);

        redefineCheckBox(value.isUsed());
    }

    private String processFile(File file) {
        // If empty path, set empty text field
        if (file.getPath().isEmpty()) {

            return "";
        }

        File currentValue = file;

        // When showing the path in the GUI, make it relative to the current setup file

        Optional<String> currentFolder = getData().get(JOptionKeys.CURRENT_FOLDER_PATH);
        if (currentFolder.isPresent()) {
            String relativePath = SpecsIo.getRelativePath(currentValue, new File(currentFolder.get()));
            currentValue = new File(relativePath);
        }

        // If path is absolute, make it canonical
        if (currentValue.isAbsolute()) {
            return SpecsIo.getCanonicalFile(currentValue).getPath();
        }
        return currentValue.getPath();

    }

}
