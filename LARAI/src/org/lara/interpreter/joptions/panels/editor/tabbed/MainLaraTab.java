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

package org.lara.interpreter.joptions.panels.editor.tabbed;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.lara.interpreter.joptions.panels.editor.highlight.EditorConfigurer;
import org.lara.interpreter.joptions.panels.editor.utils.Factory;

public class MainLaraTab extends SourceTextArea {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public MainLaraTab(TabsContainerPanel parent) {
        super(parent);
    }

    public MainLaraTab(File file, TabsContainerPanel parent) {
        super(file, parent);
    }

    public boolean open() {

        FileFilter filter = new FileNameExtensionFilter("Lara files (*.lara)", "lara");
        ArrayList<FileFilter> filters = new ArrayList<>();
        filters.add(filter);

        // File lastOpenedFolder = getTabbedParent().getLastOpenedFolder();
        // Open dialog in the same place as the current main lara file
        JFileChooser fc = Factory.newFileChooser(filters, getLaraFile());

        int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            load(file);
            getTabbedParent().setLastOpenedFolder(file.getParentFile());
            return true;
        }
        return false;

    }

    @Override
    public boolean saveAs() {

        FileFilter fileFilter = new FileNameExtensionFilter("Lara files (*.lara)", "lara");
        JFileChooser fc = Factory.newFileChooser("Save as...", "Save", fileFilter,
                getTabbedParent().getLastOpenedFolder());
        fc.setApproveButtonMnemonic('s');

        int returnVal = fc.showOpenDialog(this);
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return false;
        }
        File file = fc.getSelectedFile();
        return saveAs(file);

    }

    @Override
    public boolean close() {
        JOptionPane.showMessageDialog(this, "The main aspect file cannot be closed!");
        return false;
    }

    //
    // public boolean forceCloseRequest() {
    //
    // return super.close();
    // }

    public void requestSave() {
        // TODO Auto-generated method stub

    }
}
