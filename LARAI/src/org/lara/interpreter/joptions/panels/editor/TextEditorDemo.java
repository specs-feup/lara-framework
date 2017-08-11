package org.lara.interpreter.joptions.panels.editor;

import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.lara.interpreter.joptions.panels.editor.tabbed.TabsContainerPanel;

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

public class TextEditorDemo extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public static final String LARA_STYLE_KEY = "text/lara";

    // private final List<RSyntaxTextArea> textAreas;
    private final TabsContainerPanel tabbedPane;

    public TextEditorDemo() {
	tabbedPane = new TabsContainerPanel(null);
	setPreferredSize(new Dimension(600, 400));
	setContentPane(tabbedPane);
	setTitle("Text Editor Demo");
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	addWindowFocusListener(new WindowFocusListener() {
	    @Override
	    public void windowGainedFocus(WindowEvent e) {
		tabbedPane.getCurrentTab().refresh();
	    }

	    @Override
	    public void windowLostFocus(WindowEvent e) {

	    }
	});
	pack();
	setLocationRelativeTo(null);

    }

    public static void main(String[] args) throws Exception {
	UIManager.setLookAndFeel(
		UIManager.getSystemLookAndFeelClassName());
	// Start all Swing applications on the EDT.
	SwingUtilities.invokeLater(() -> new TextEditorDemo().setVisible(true));
    }

}