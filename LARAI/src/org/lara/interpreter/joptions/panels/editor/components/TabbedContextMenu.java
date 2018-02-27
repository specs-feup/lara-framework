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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.lara.interpreter.joptions.panels.editor.tabbed.TabsContainerPanel;

import pt.up.fe.specs.util.swing.GenericActionListener;

public class TabbedContextMenu extends JPopupMenu {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    // JMenuItem close;
    // JMenuItem closeAll;
    // JMenuItem closeLeft;
    // JMenuItem closeRight;
    TabsContainerPanel tabbedPane;

    public TabbedContextMenu(TabsContainerPanel tabbedPane) {
	this.tabbedPane = tabbedPane;
	addItem("Close", x -> tabbedPane.closeCurrent());
	addItem("Close Others", x -> tabbedPane.closeAllButCurrent());
	addItem("Close Tabs to the Left <<", x -> tabbedPane.closeLeft());
	addItem("Close Tabs to the Right >>", x -> tabbedPane.closeRight());
	addSeparator();
	addItem("Close All", x -> tabbedPane.closeAll());
    }

    public void addItem(String name, Consumer<ActionEvent> consumer) {
	JMenuItem item = new JMenuItem(name);
	item.addActionListener(new GenericActionListener(consumer));
	add(item);

    }

    // private EditorTab getCurrentTab() {
    // return tabbedPane.getCurrentTab();
    // }

    public static MouseAdapter newMouseAdapter(TabsContainerPanel tabbedPane) {
	return new MouseAdapter() {
	    @Override
	    public void mousePressed(MouseEvent e) {

		if (e.isPopupTrigger()) {

		    doPop(e);
		}
	    }

	    @Override
	    public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger()) {
		    doPop(e);
		} else {

		    if (e.getButton() == MouseEvent.BUTTON2) {

			tabbedPane.getCurrentTab().close();
		    }
		}
	    }

	    private void doPop(MouseEvent e) {
		TabbedContextMenu menu = new TabbedContextMenu(tabbedPane);
		menu.show(e.getComponent(), e.getX(), e.getY());
	    }
	};
    }
}
