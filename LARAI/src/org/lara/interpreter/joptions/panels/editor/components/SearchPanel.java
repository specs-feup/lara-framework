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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.fife.ui.rsyntaxtextarea.TextEditorPane;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.lara.interpreter.joptions.panels.editor.EditorPanel;
import org.lara.interpreter.joptions.panels.editor.listeners.ListenerUtils;
import org.lara.interpreter.joptions.panels.editor.listeners.StrokesAndActions;
import org.lara.interpreter.joptions.panels.editor.utils.Colors;

public class SearchPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final EditorPanel editorPanel;
    private final JTextField searchField;
    private final JButton upButton;
    private final JButton downButton;
    private final JCheckBox matchCaseCB;
    private final JCheckBox markAllCB;
    private final JCheckBox regexCB;
    private final JLabel infoLabel;
    private final JButton closeButton;

    public SearchPanel(EditorPanel editorPanel) {
        super(new BorderLayout());

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(Colors.BLUE_GREY);

        closeButton = new JButton("Close");
        closeButton.addActionListener(e -> setVisible(false));
        add(panel, BorderLayout.CENTER);
        add(closeButton, BorderLayout.EAST);

        this.editorPanel = editorPanel;
        searchField = new JTextField(20);

        ListenerUtils.mapAction(searchField, StrokesAndActions.ENTER, "FindNext", x -> search(true));
        ListenerUtils.mapAction(searchField, StrokesAndActions.SHIFT_ENTER, "FindPrevious", x -> search(false));

        panel.add(searchField);

        upButton = new JButton("\u25B2");
        upButton.setActionCommand("FindPrevious");
        upButton.addActionListener(this::actionPerformed);
        panel.add(upButton);

        downButton = new JButton("\u25BC");
        downButton.setActionCommand("FindNext");
        downButton.addActionListener(this::actionPerformed);
        panel.add(downButton);

        markAllCB = new JCheckBox("Highlight All");
        panel.add(markAllCB);
        matchCaseCB = new JCheckBox("Match Case");
        panel.add(matchCaseCB);
        regexCB = new JCheckBox("Regex");
        panel.add(regexCB);
        infoLabel = new JLabel("");
        // infoLabel.setBorder(new Border);
        panel.add(infoLabel);

        setVisible(false);
    }

    public void actionPerformed(ActionEvent e) {

        // "FindNext" => search forward, "FindPrev" => search backward
        String command = e.getActionCommand();
        boolean forward = "FindNext".equals(command);

        search(forward);

    }

    private void search(boolean forward) {
        // Create an object defining our search parameters.
        SearchContext context = new SearchContext();
        String text = searchField.getText();
        // if (text.length() == 0) {
        // return;
        // }
        context.setSearchFor(text);
        context.setMatchCase(matchCaseCB.isSelected());
        context.setRegularExpression(regexCB.isSelected());
        context.setSearchForward(forward);
        context.setWholeWord(false);

        RTextArea textArea = getTextArea();

        boolean found = SearchEngine.find(textArea, context).wasFound();
        if (!found) {

            textArea.setCaretPosition(0);
            found = SearchEngine.find(textArea, context).wasFound();
            if (found) {
                infoLabel.setText("Wrapped Search");
            } else {
                infoLabel.setText("Text not found");
            }
            return;

        }
        infoLabel.setText("");
    }

    private TextEditorPane getTextArea() {
        return editorPanel.getTabsContainer().getCurrentTab().getTextArea();
    }

    public void getFocus() {
        setVisible(true);
        searchField.requestFocus();
    }

}
