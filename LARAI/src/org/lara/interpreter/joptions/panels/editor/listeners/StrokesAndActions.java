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

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

public interface StrokesAndActions {

    // Utilities
    final int CTRL = InputEvent.CTRL_MASK;
    final int SHIFT = InputEvent.SHIFT_MASK;
    final int CTRL_SHIFT = StrokesAndActions.CTRL | StrokesAndActions.SHIFT;

    // Actions key
    // public final String NEW_TAB_ACTION = "new_tab";
    // public final String NEW_TAB_OPEN_ACTION = "new_tab_and_open";
    // public final String SAVE_ACTION = "save_tab";
    // public final String SAVE_AS_ACTION = "save_as_tab";
    // public final String SAVE_ALL_ACTION = "save_all_tab";
    // public final String OPEN_ACTION = "open_tab";
    // public final String CLOSE_ACTION = "close_tab";
    // public final String CLOSE_ALL_ACTION = "close_all_tab";
    // public final String REFRESH = "refreshTab";
    public final String PREVIOUS_ACTION = "navigatePrevious";
    public final String NEXT_ACTION = "navigateNext";

    // KeyStrokes
    public final KeyStroke F5 = KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0);
    public final KeyStroke F11 = KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0);
    public final KeyStroke ENTER = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
    public final KeyStroke CTRL_F = KeyStroke.getKeyStroke(KeyEvent.VK_F, StrokesAndActions.CTRL);
    public final KeyStroke CTRL_N = KeyStroke.getKeyStroke(KeyEvent.VK_N, StrokesAndActions.CTRL);
    public final KeyStroke CTRL_M = KeyStroke.getKeyStroke(KeyEvent.VK_M, StrokesAndActions.CTRL);
    public final KeyStroke CTRL_O = KeyStroke.getKeyStroke(KeyEvent.VK_O, StrokesAndActions.CTRL);
    public final KeyStroke CTRL_S = KeyStroke.getKeyStroke(KeyEvent.VK_S, StrokesAndActions.CTRL);
    public final KeyStroke CTRL_T = KeyStroke.getKeyStroke(KeyEvent.VK_T, StrokesAndActions.CTRL);
    public final KeyStroke CTRL_W = KeyStroke.getKeyStroke(KeyEvent.VK_W, StrokesAndActions.CTRL);
    public final KeyStroke CTRL_F11 = KeyStroke.getKeyStroke(KeyEvent.VK_F11, StrokesAndActions.CTRL);

    public final KeyStroke SHIFT_ENTER = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, StrokesAndActions.SHIFT);
    public final KeyStroke CTRL_TAB = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, StrokesAndActions.CTRL);
    public final KeyStroke CTRL_SHIFT_TAB = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, StrokesAndActions.CTRL_SHIFT);
    public final KeyStroke CTRL_SHIFT_B = KeyStroke.getKeyStroke(KeyEvent.VK_B, StrokesAndActions.CTRL_SHIFT);
    public final KeyStroke CTRL_SHIFT_C = KeyStroke.getKeyStroke(KeyEvent.VK_C, StrokesAndActions.CTRL_SHIFT);
    public final KeyStroke CTRL_SHIFT_M = KeyStroke.getKeyStroke(KeyEvent.VK_M, StrokesAndActions.CTRL_SHIFT);
    public final KeyStroke CTRL_SHIFT_N = KeyStroke.getKeyStroke(KeyEvent.VK_N, StrokesAndActions.CTRL_SHIFT);
    public final KeyStroke CTRL_SHIFT_O = KeyStroke.getKeyStroke(KeyEvent.VK_O, StrokesAndActions.CTRL_SHIFT);
    public final KeyStroke CTRL_SHIFT_S = KeyStroke.getKeyStroke(KeyEvent.VK_S, StrokesAndActions.CTRL_SHIFT);
    public final KeyStroke CTRL_SHIFT_W = KeyStroke.getKeyStroke(KeyEvent.VK_W, StrokesAndActions.CTRL_SHIFT);

    public static String prettyString(KeyStroke stroke) {
	// System.out.println("before " + stroke);
	String string = stroke.toString().replace("pressed ", "");
	string = string.replace("ctrl", "Ctrl");
	string = string.replace("shift", "Shift");
	string = string.replace(" ", "+");
	// System.out.println("after " + string);
	return string;
    }

}
