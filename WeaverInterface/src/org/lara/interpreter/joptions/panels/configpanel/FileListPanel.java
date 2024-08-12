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

package org.lara.interpreter.joptions.panels.configpanel;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.*;

import org.lara.interpreter.joptions.keys.FileList;
import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Interfaces.DataStore;
import org.suikasoft.jOptions.gui.KeyPanel;
import org.suikasoft.jOptions.gui.panels.option.FilePanel;

import pt.up.fe.specs.util.SpecsSwing;

public class FileListPanel extends KeyPanel<FileList> {

    private static final long serialVersionUID = 1L;

    /**
     * INSTANCE VARIABLES
     */
    // private JComboBox<String> comboBoxValues;
    private final JList<File> listValues;
    private final FilePanel file;
    private final DataKey<File> fileKey;
    private final DefaultListModel<File> files;
    // private final DataStore fileStore;
    private final JButton removeButton;
    private final JButton addButton;
    // private final boolean foldersOnly;

    /**
     * 
     * @param key
     * @param fileKey
     * @param data
     * @param fileSelectionMode
     *            See {@link JFileChooser#setFileSelectionMode(int)}
     * @param extensions
     */
    public FileListPanel(DataKey<FileList> key, DataKey<File> fileKey, DataStore data, int fileSelectionMode, Collection<String> extensions) {
        super(key, data);
        // JFileChooser.DIRECTORIES_ONLY, Collections.emptyList());
        // this.foldersOnly = foldersOnly;

        // comboBoxValues = new JComboBox<String>();
        listValues = new JList<>();
        listValues.setModel(files = new DefaultListModel<>());
        listValues.setCellRenderer(new CellRenderer());
        removeButton = new JButton("Remove");
        addButton = new JButton("Add");

        //fileKey = LaraIKeyFactory.file("", fileSelectionMode, false, extensions);
        this.fileKey = fileKey;
        file = new FilePanel(fileKey, data, fileSelectionMode, extensions);

        file.setOnFileOpened(this::onFileOpen);
        // file = new FilePanel(fileKey, fileStore = DataStore.newInstance("dummy"));

        addButton.addActionListener(this::addButtonActionPerformed);
        removeButton.addActionListener(this::removeButtonActionPerformed);

        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(file, c);

        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        c.gridheight = 2;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        add(new JScrollPane(listValues), c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridheight = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.gridx = 1;
        c.gridy = 0;
        add(addButton, c);
        c.gridy = 1;
        add(removeButton, c);
    }

    static class CellRenderer extends JLabel implements ListCellRenderer<File> {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public CellRenderer() {
            setVisible(true);
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends File> list,
                File value, int index,
                boolean isSelected, boolean cellHasFocus) {

            setText(value.getAbsolutePath());
            setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
            return this;
        }

    }

    /**
     * Adds the text in the textfield to the combo box
     * 
     * @param evt
     */
    private void addButtonActionPerformed(ActionEvent evt) {
        // System.out.println("Current item number:"+values.getSelectedIndex());
        // Check if there is text in the textfield
        File newValue = file.getValue();

        addFile(newValue);
    }

    private void addFile(File newValue) {
        // files.addElement(SpecsIo.normalizePath(newValue));
        files.addElement(newValue);
        listValues.setSelectedIndex(files.size() - 1);
    }

    /**
     * Removes the currently selected element of the list.
     * 
     * @param evt
     */
    private void removeButtonActionPerformed(ActionEvent evt) {
        int valueIndex = listValues.getSelectedIndex();
        if (valueIndex == -1) {
            return;
        }

        files.remove(valueIndex);
        if (files.size() > valueIndex) {
            listValues.setSelectedIndex(valueIndex);
        } else {
            if (!files.isEmpty()) {
                listValues.setSelectedIndex(files.size() - 1);
            }
        }
    }

    @Override
    public FileList getValue() {
        List<File> newValues = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            newValues.add(files.getElementAt(i));
        }

        return FileList.newInstance(newValues);
        /*
        FileList newFileList = FileList.newInstance(newValues);
        
        Optional<String> currentFolderPath = getData().getTry(JOptionKeys.CURRENT_FOLDER_PATH);
        if (!currentFolderPath.isPresent()) {
        // LoggingUtils.msgWarn("CHECK THIS CASE, WHEN CONFIG IS NOT DEFINED");
        return newFileList;
        }
        
        DataStore tempData = DataStore.newInstance("FileListPanelTemp", getData());
        // When reading a value from the GUI to the user DataStore, use absolute path
        
        tempData.set(JOptionKeys.CURRENT_FOLDER_PATH, currentFolderPath.get());
        tempData.set(JOptionKeys.USE_RELATIVE_PATHS, false);
        tempData.setString(getKey(), newFileList.encode());
        
        FileList value = tempData.get(getKey());
        
        return value;
        */

    }

    @Override
    public <ET extends FileList> void setValue(ET fileList) {

        SpecsSwing.runOnSwing(() -> {
            files.clear();
            for (File v : fileList) {
                // String path = FilePanel.processFile(getData(), v);
                files.addElement(v);
                // files.addElement(SpecsIo.normalizePath(v));
            }
        });
    }

    public void onFileOpen(File f) {
        addFile(f);
    }
}
