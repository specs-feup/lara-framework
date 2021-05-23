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

import java.util.function.Supplier;

import javax.swing.JComponent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaEditorKit;
import org.fife.ui.rsyntaxtextarea.TextEditorPane;
import org.lara.interpreter.joptions.panels.editor.tabbed.SourceTextArea;
import org.lara.interpreter.joptions.panels.editor.tabbed.TabsContainerPanel;

public class EditorListener {

    private final SourceTextArea tab;

    public static EditorListener newInstance(SourceTextArea laraTab) {
	return new EditorListener(laraTab);
    }

    public EditorListener(SourceTextArea laraTab) {
	tab = laraTab;
	TabsContainerPanel tabParent = tab.getTabbedParent();
	mapActions(laraTab.getTextArea(), () -> laraTab, tabParent);

	TextEditorPane textArea = tab.getTextArea();

	textArea.getDocument().addDocumentListener(new ChangeListener());
	// textArea.setTransferHandler(new FileTransferHandler(tabParent::open));
    }

    /**
     * Maps common actions to the given component
     * 
     * @param component
     *            the component in which we are going to add the action
     * @param laraTab
     *            the target lara tab in which we are going to work
     * @param tabbedPane
     *            the pane we are working with
     */
    static void mapActions(JComponent component, Supplier<SourceTextArea> laraTab,
	    TabsContainerPanel tabbedPane) {

	ListenerUtils.mapKeyStroke(component, StrokesAndActions.CTRL_SHIFT_C,
		RSyntaxTextAreaEditorKit.rstaToggleCommentAction);

	// These are being used by the menus so are simply duplicated!
	// ListenerUtils.mapAction(component, StrokesAndActions.CTRL_S_STROKE, StrokesAndActions.SAVE_ACTION,
	// x -> laraTab.get().save());
	// ListenerUtils.mapAction(component, StrokesAndActions.CTRL_SHIFT_S_STROKE,
	// StrokesAndActions.SAVE_AS_ACTION,
	// x -> tabbedPane.saveAll());

	// ListenerUtils.mapAction(component, StrokesAndActions.CTRL_O_STROKE, StrokesAndActions.OPEN_ACTION,
	// x -> laraTab.get().open());
	// ListenerUtils.mapAction(component, StrokesAndActions.CTRL_W_STROKE, StrokesAndActions.CLOSE_ACTION,
	// x -> laraTab.get().close());

	// ListenerUtils.mapAction(component, StrokesAndActions.F5_STROKE, StrokesAndActions.REFRESH,
	// x -> laraTab.get().refresh());
	/**
	 * Actions that are related to the tabbed pane
	 */
	// ListenerUtils.mapAction(component, StrokesAndActions.CTRL_N_STROKE, StrokesAndActions.NEW_TAB_ACTION,
	// x -> tabbedPane.addTab());
	// ListenerUtils.mapAction(component, StrokesAndActions.CTRL_SHIFT_N_STROKE,
	// StrokesAndActions.NEW_TAB_OPEN_ACTION,
	// openInNewTab(tabbedPane));

	ListenerUtils.removeTraversalKeys(component);

	ListenerUtils.mapAction(component, StrokesAndActions.CTRL_SHIFT_TAB, StrokesAndActions.PREVIOUS_ACTION,
		x -> tabbedPane.navigatePrevious());
	ListenerUtils.mapAction(component, StrokesAndActions.CTRL_TAB, StrokesAndActions.NEXT_ACTION,
		x -> tabbedPane.navigateNext());
    }

    // public static Consumer<ActionEvent> openInNewTab(TabsContainerPanel tabbedPane) {
    // return x -> {
    // SourceEditorTab newTab = tabbedPane.addTab();
    // boolean wasOpenned = newTab.open();
    // if (!wasOpenned) {
    // newTab.getTabbedParent().closeTab(newTab);
    // }
    // };
    // }

    public class ChangeListener implements DocumentListener {

	@Override
	public void changedUpdate(DocumentEvent e) {

	    if (!tab.getOriginalText().equals(tab.getTextArea().getText())) {
	        // when the text area changes, reparse the folds
	        tab.getTextArea().getFoldManager().reparse();
		if (!tab.isChanged()) {
		    tab.getTabbedParent().setChanged(tab);
		    tab.setChanged(true);
		}
	    } else {
		if (tab.isChanged()) {
		    tab.getTabbedParent().setTabTitle(tab);
		    tab.setChanged(false);
		}
	    }
	}

	@Override
	public void insertUpdate(DocumentEvent e) {

	}

	@Override
	public void removeUpdate(DocumentEvent e) {

	}

    }

    /**
     * Perform a given action
     * 
     * @author Tiago
     *
     *
     *         interface ActionPerformer { void perform(ActionEvent e);
     * 
     *         default Action toAction() { return new GenericAction(this::perform); } }
     */

}
