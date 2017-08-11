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

package org.lara.interpreter.joptions.panels.editor.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.ScrollPaneConstants;
import javax.swing.filechooser.FileFilter;

import org.fife.ui.rsyntaxtextarea.TextEditorPane;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.lara.interpreter.joptions.panels.editor.tabbed.TabsContainerPanel;

public class Factory {

    public static JFileChooser newFileChooser(File currentDir) {
	return new JFileChooser(currentDir);
    }

    /**
     * Create a file chooser using a list of filters;
     * 
     * @param filters
     * @return
     */
    public static JFileChooser newFileChooser(Collection<FileFilter> filters, File currentDir) {
	return newFileChooser(null, JFileChooser.FILES_ONLY, null, filters, currentDir);

    }

    public static JFileChooser newFileChooser(String title, String confirmText, File currentDir) {
	return newFileChooser(title, confirmText, Collections.emptyList(), currentDir);
    }

    public static JFileChooser newFileChooser(String title, String confirmText, FileFilter filter, File currentDir) {
	List<FileFilter> coll = new ArrayList<>();
	coll.add(filter);
	return newFileChooser(title, confirmText, coll, currentDir);
    }

    /**
     * Create a file chooser using a list of filters;
     * 
     * @param filters
     * @param currentDir
     * @return
     */
    public static JFileChooser newFileChooser(String title, String confirmText, Collection<FileFilter> filters,
	    File currentDir) {
	return newFileChooser(title, JFileChooser.FILES_ONLY, confirmText, filters, currentDir);

    }

    /**
     * Create new file chooser with the given information
     * 
     * @param title
     * @param selectMode
     * @param confirmText
     * @param filters
     * @return
     */
    public static JFileChooser newFileChooser(String title, int selectMode, String confirmText,
	    Collection<FileFilter> filters, File currentDir) {
	JFileChooser fc = newFileChooser(currentDir);
	fc.setFileSelectionMode(selectMode);
	if (title != null) {
	    fc.setDialogTitle(title);
	}
	if (confirmText != null) {
	    fc.setApproveButtonText(confirmText);
	}

	if (!filters.isEmpty()) {
	    for (FileFilter fileFilter : filters) {

		fc.addChoosableFileFilter(fileFilter);
	    }
	    fc.setFileFilter(filters.iterator().next());
	}

	return fc;
    }

    private static final String DEFAULT_FILE_NAME = "new ";
    private static int UID = 1;

    public static File newFile(TabsContainerPanel tabbedLaraFiles) {
	int uid = Factory.UID;

	do {
	    File f = new File(Factory.DEFAULT_FILE_NAME + (uid++));
	    if (!f.exists()) {
		int tabIndex = tabbedLaraFiles.getTabIndex(f);
		if (tabIndex == -1) {
		    return f;
		}
	    }

	} while (true);
    }

    /**
     * Build a standard scroll pane for the given text area that shows the vertical/horizontal scroll bar when needed
     * 
     * @param textArea
     * @return
     */
    public static RTextScrollPane standardScrollPane(TextEditorPane textArea) {
	RTextScrollPane pane = new RTextScrollPane(textArea);
	pane.setWheelScrollingEnabled(true);

	pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
	pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	return pane;
    }
}
