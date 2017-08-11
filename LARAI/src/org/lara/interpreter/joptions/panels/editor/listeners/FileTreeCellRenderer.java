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

package org.lara.interpreter.joptions.panels.editor.listeners;

import java.awt.Component;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.lara.interpreter.joptions.panels.editor.components.Explorer;
import org.lara.interpreter.joptions.panels.editor.components.FileNode;
import org.lara.interpreter.joptions.panels.editor.components.WorkDirNode;

/** A TreeCellRenderer for a File. */
public class FileTreeCellRenderer extends DefaultTreeCellRenderer {

    /**
     * 
     */
    private final Explorer fileTreeCellRenderer;

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final JLabel label;

    public FileTreeCellRenderer(Explorer explorer) {
	fileTreeCellRenderer = explorer;
	label = new JLabel();
	label.setOpaque(true);
    }

    @Override
    public Component getTreeCellRendererComponent(
	    JTree tree,
	    Object value,
	    boolean selected,
	    boolean expanded,
	    boolean leaf,
	    int row,
	    boolean hasFocus) {

	DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
	FileSystemView fileSystemView = fileTreeCellRenderer.getFileSystemView();
	if (node instanceof FileNode) {

	    FileNode fileNode = (FileNode) node;
	    File file = fileNode.getFile();
	    String comment = fileNode.getComment();
	    // File file = (File) node.getUserObject();
	    label.setIcon(fileSystemView.getSystemIcon(file));
	    String systemDisplayName = fileSystemView.getSystemDisplayName(file);
	    if (comment != null && !comment.isEmpty()) {
		systemDisplayName = "<html>" + systemDisplayName + " <font color=\"#d3d3d3\">" + comment
			+ "</font></html>";
	    }
	    label.setText(systemDisplayName);
	    label.setToolTipText(file.getPath());
	} else if (node instanceof WorkDirNode) {

	    label.setText(node.getUserObject().toString());
	    label.setIcon(fileSystemView.getSystemIcon(new File(".")));
	} else {

	    label.setText(node.getUserObject().toString());
	    label.setIcon(UIManager.getIcon("FileView.fileIcon"));
	}
	if (selected) { // && !file.isDirectory()) {
	    label.setBackground(backgroundSelectionColor);
	    label.setForeground(textSelectionColor);
	} else {
	    label.setForeground(textNonSelectionColor);

	    label.setBackground(backgroundNonSelectionColor);
	}

	return label;
    }
}