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

import java.awt.event.ActionEvent;
import java.util.function.Consumer;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.lara.interpreter.joptions.panels.editor.listeners.GenericActionListener;

public class ExplorerPopup extends JPopupMenu {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public ExplorerPopup(Explorer explorer) {
	// JPopupMenu popupMenu = new JPopupMenu("POPUP");
	newItem("Open File", explorer::open);
	newItem("Open Main Lara File", explorer::openMainLara);
	addSeparator();
	newItem("New Project", explorer::newProject);
	newItem("Remove Project", explorer::removeProject);
	addSeparator();
	newItem("Expand", explorer::expand);
	newItem("Collapse", explorer::collapse);
	newItem("Refresh", e -> explorer.refresh());
    }

    private JMenuItem newItem(String name, Consumer<ActionEvent> listener) {

	JMenuItem newItem = new JMenuItem(name);

	// newItem.setMnemonic(keyEvent);
	newItem.addActionListener(new GenericActionListener(listener));

	return add(newItem);
    }
}
