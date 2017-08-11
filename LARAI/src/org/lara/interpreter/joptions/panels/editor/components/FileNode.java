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

package org.lara.interpreter.joptions.panels.editor.components;

import java.io.File;

import javax.swing.tree.DefaultMutableTreeNode;

public class FileNode extends DefaultMutableTreeNode {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final File file;
    private final String comment;
    private final boolean closeable;

    public FileNode(File file) {
	this(file, "");
    }

    public FileNode(File file, String comment) {
	this(file, comment, true);
    }

    public FileNode(File file, String comment, boolean closeable) {
	// super(file);
	super(addFileAnnotation(file, comment));
	this.file = file;
	this.comment = comment;
	this.closeable = closeable;
    }

    private static Object addFileAnnotation(File file, String comment) {
	if (comment != null && !comment.isEmpty()) {
	    return new File(file.getName() + comment);
	}
	return file;
    }

    public File getFile() {
	return file;
    }

    public String getComment() {
	return comment;
    }

    public boolean isCloseable() {
	return closeable;
    }

    // @Override
    // public boolean isLeaf() {
    //
    // return !file.isDirectory() || file.listFiles().length == 0;
    // }

}
