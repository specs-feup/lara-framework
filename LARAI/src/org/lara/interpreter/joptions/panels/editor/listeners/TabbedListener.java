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

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;

import org.lara.interpreter.joptions.panels.editor.components.TabbedContextMenu;
import org.lara.interpreter.joptions.panels.editor.tabbed.SourceTextArea;
import org.lara.interpreter.joptions.panels.editor.tabbed.TabsContainerPanel;

public class TabbedListener {

    private final TabsContainerPanel tabbedLaraFiles;

    public TabbedListener(TabsContainerPanel tabsContainer) {
	tabbedLaraFiles = tabsContainer;
	// Register a change listener
	tabsContainer.getTabbedPane().addChangeListener(this::changeTabAction);

	tabsContainer.getTabbedPane()
		.addFocusListener(new FocusGainedListener(e -> tabsContainer.getCurrentTab().requestFocus()));
	tabsContainer.getTabbedPane().addMouseListener(TabbedContextMenu.newMouseAdapter(tabsContainer));

	mapActions(tabsContainer);
    }

    /*
    private EditorTab getCurrentTab() {
    	return tabbedLaraFiles.getCurrentTab();
    }
    */
    private void changeTabAction(ChangeEvent evt) {
	JTabbedPane pane = (JTabbedPane) evt.getSource();
	// Get selected tab
	int sel = pane.getSelectedIndex();
	if (sel < 0) {
	    return;
	}
	SourceTextArea nextTab = (SourceTextArea) pane.getComponentAt(sel);

	tabbedLaraFiles.moveTab(nextTab);
    }

    public TabsContainerPanel getTabbedLaraFiles() {
	return tabbedLaraFiles;
    }

    public static TabbedListener newInstance(TabsContainerPanel tabbedLaraFiles) {
	return new TabbedListener(tabbedLaraFiles);

    }

    private void mapActions(TabsContainerPanel tabbedLaraFiles) {
	// TODO add right click option to remove: all, all but this, all to the left and all to the right

	/*
	 * These are no longer needed as the focus are always given to the current text area
	mapAction(StrokesAndActions.CTRL_S_STROKE, StrokesAndActions.SAVE_ACTION,
	x -> getCurrentTab().save());
	mapAction(StrokesAndActions.CTRL_SHIFT_A_STROKE, StrokesAndActions.SAVE_AS_ACTION,
	x -> getCurrentTab().saveAs());
	mapAction(StrokesAndActions.CTRL_O_STROKE, StrokesAndActions.OPEN_ACTION,
	x -> getCurrentTab().open());
	
	mapAction(StrokesAndActions.CTRL_N_STROKE, StrokesAndActions.NEW_TAB_ACTION, x -> tabbedLaraFiles.addTab());
	mapAction(StrokesAndActions.CTRL_SHIFT_N_STROKE, StrokesAndActions.NEW_TAB_OPEN_ACTION,
	EditorListener.newTabAndOpen(tabbedLaraFiles));
	
	mapAction(StrokesAndActions.CTRL_W_STROKE, StrokesAndActions.CLOSE_ACTION,
	x -> getCurrentTab().close());
	
	EditorListener.removeTraversalKeys(tabbedLaraFiles);
	
	mapAction(StrokesAndActions.CTRL_SHIFT_TAB_STROKE, StrokesAndActions.PREVIOUS_ACTION,
	x -> tabbedLaraFiles.navigatePrevious());
	mapAction(StrokesAndActions.CTRL_TAB_STROKE, StrokesAndActions.NEXT_ACTION,
	x -> tabbedLaraFiles.navigateNext());
	    */

    }

}
