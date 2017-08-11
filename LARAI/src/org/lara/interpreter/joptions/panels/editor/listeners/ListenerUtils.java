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

import java.awt.AWTKeyStroke;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

public class ListenerUtils {

    // tab.getTextArea()
    /**
     * Maps a keystroke to a new action in the given component
     * 
     * @param component
     * @param stroke
     * @param actionName
     * @param performer
     */
    public static void mapAction(JComponent component, KeyStroke stroke, String actionName,
	    Consumer<ActionEvent> performer) {
	InputMap im = component.getInputMap();
	ActionMap am = component.getActionMap();

	im.put(stroke, actionName);
	am.put(actionName, GenericActionListener.newInstance(performer));
    }

    /**
     * Map a keystroke to an action
     * 
     * @param component
     * @param stroke
     * @param actionName
     */
    public static void mapKeyStroke(JComponent component, KeyStroke stroke, String actionName) {
	InputMap im = component.getInputMap();
	im.put(stroke, actionName);
    }

    /**
     * need to remove these strokes when we want to use CTRL+[SHIFT+]TAB
     * 
     * @param component
     */
    static void removeTraversalKeys(JComponent component) {
	Set<AWTKeyStroke> forwardKeys = new HashSet<>(
		component.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
	forwardKeys.remove(StrokesAndActions.CTRL_TAB);
	component.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardKeys);
	Set<AWTKeyStroke> backwardKeys = new HashSet<>(
		component.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
	backwardKeys.remove(StrokesAndActions.CTRL_SHIFT_TAB);
	component.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backwardKeys);
    }

}
