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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.TransferHandler;

public class FileTransferHandler extends TransferHandler {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final Consumer<File> handler;

    public FileTransferHandler(Consumer<File> handler) {
	super();
	this.handler = handler;
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport info) {
	// we only import FileList
	if (!info.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
	    return false;
	}
	return true;
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport info) {
	if (!info.isDrop()) {
	    return false;
	}

	// Check for FileList flavor
	if (!info.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
	    displayDropLocation("The editor doesn't accept a drop of this type.");
	    return false;
	}

	// Get the fileList that is being dropped.
	Transferable t = info.getTransferable();

	try {
	    @SuppressWarnings("unchecked")
	    List<File> data = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
	    for (File file : data) {

		handler.accept(file);
	    }
	    return true;
	} catch (Exception e) {
	    displayDropLocation("Problem when importing files: " + e.getMessage());
	}
	return false;

    }

    private static void displayDropLocation(String string) {
	System.out.println(string);
    }
}
