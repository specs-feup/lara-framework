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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.lara.interpreter.joptions.panels.editor.listeners.GenericActionListener;
import org.lara.interpreter.joptions.panels.editor.tabbed.SourceTextArea;

public class ReloadPane extends Panel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final Color BLUE = new Color(120, 169, 255);
    // private final JButton reload;
    // private final JButton overwrite;
    // private final JButton close;

    // private final JButton cancelBtn = new JButton("Ignore");
    private final JLabel messageLbl = new JLabel("This file was updated by another program");

    public ReloadPane(SourceTextArea editorTab) {
	super(new BorderLayout());
	setBackground(BLUE);

	// JLabel label = new JLabel("File content was modified");
	Font currentFont = messageLbl.getFont();
	Font newFont = currentFont.deriveFont(11f).deriveFont(Font.BOLD);

	add(messageLbl, BorderLayout.CENTER);

	JPanel buttonsPanel = new JPanel();
	buttonsPanel.setBackground(BLUE);
	JButton reload = new JButton("Reload");

	reload.setFont(newFont);
	addButtonListener(reload, x -> editorTab.reload());
	buttonsPanel.add(reload);
	// reload.addActionListener(new GenericActionListener(x -> editorTab.reload()));
	JButton overwrite = new JButton("Overwrite");
	addButtonListener(overwrite, x -> editorTab.save());
	overwrite.setFont(newFont);
	buttonsPanel.add(overwrite);
	CloseButton close = new CloseButton(x -> {
	    editorTab.updateLastModified();
	    setVisible(false);
	    editorTab.revalidate();

	});
	close.setFont(newFont);
	buttonsPanel.add(close);
	add(buttonsPanel, BorderLayout.EAST);

	setVisible(false); // true for debugging
    }

    public void addButtonListener(JButton button, Consumer<ActionEvent> consumer) {
	button.addActionListener(new GenericActionListener(x -> {
	    consumer.accept(x);
	    setVisible(false);
	    // editorTab.revalidate();

	}));
    }

}