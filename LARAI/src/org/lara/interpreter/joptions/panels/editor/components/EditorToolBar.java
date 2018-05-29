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
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.function.Consumer;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.lara.interpreter.cli.LaraCli;
import org.lara.interpreter.joptions.panels.editor.EditorPanel;
import org.lara.interpreter.joptions.panels.editor.listeners.FocusGainedListener;
import org.lara.interpreter.joptions.panels.editor.listeners.StrokesAndActions;
import org.lara.interpreter.joptions.panels.editor.tabbed.SourceTextArea;
import org.lara.interpreter.joptions.panels.editor.tabbed.TabsContainerPanel;
import org.suikasoft.jOptions.Interfaces.DataStore;
import org.suikasoft.jOptions.gui.panels.app.AppKeys;

import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.swing.GenericActionListener;
import pt.up.fe.specs.util.utilities.heapwindow.HeapBar;

public class EditorToolBar extends JPanel {

    private static final String RUN_LARA_IN_DEBUG_MODE_TEXT = "Run LARA in debug mode";
    private static final String DEBUG_LARA_TEXT = "Debug LARA";
    private static final String RUN_LARA_TEXT = "Run LARA";
    private static final String TEST_LARA_TEXT = "Test LARA";
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public static final String NO_CONFIG_MESSAGE = "Please open a configuration file in 'Program' tab or save into a new configuration in 'Options' tab";
    private final EditorPanel editor;

    // private final JToolBar menuBar;

    private final JToolBar toolBar;

    private JButton execButton;
    private boolean running;
    private JButton debugButton;
    private JButton testButton;
    private JComboBox<Integer> editorCombo;
    private JComboBox<Integer> consoleCombo;
    private HeapBar heapBar;

    public EditorToolBar(EditorPanel editorPanel) {
        // super(new FlowLayout(FlowLayout.LEFT));
        super(new BorderLayout());

        // FlowLayout leftArea = new FlowLayout(FlowLayout.LEFT);
        // setBackground(Colors.BLUE_GREY);
        editor = editorPanel;
        // menuBar = new JToolBar();
        // add(menuBar);
        toolBar = newLeftToolBar("Tool Bar");
        // toolBar.setBackground(Colors.BLUE_GREY);
        addFileButtons();
        toolBar.addSeparator();
        addSearchButtons();
        toolBar.addSeparator();
        addRunButtons();
        toolBar.addSeparator();
        addFontControls();

        addFocusListener(new FocusGainedListener(e -> getCurrentTab().requestFocus()));
        add(new JLabel(), BorderLayout.CENTER);
        heapBar = new HeapBar();
        add(heapBar, BorderLayout.EAST);
        heapBar.run();
    }

    private void addFontControls() {

        Integer[] values = { 10, 12, 14, 16, 18, 20 };

        editorCombo = addSizeCombo(values, 12, "Editor Font Size", " Editor Font Size ", editor::setTabsFont);

        consoleCombo = addSizeCombo(values, EditorPanel.DEFAULT_FONT, "Output Font Size", " Output Font Size ",
                editor::setOutputAreaFont);

    }

    public void setSelectedEditorFont(float value) {
        editorCombo.setSelectedItem((int) value);
        // editorCombo.revalidate();
        // editorCombo.repaint();
    }

    public void setSelectedConsoleFont(float value) {
        consoleCombo.setSelectedItem((int) value);
        // consoleCombo.revalidate();
        // consoleCombo.repaint();
    }

    /**
     * @return
     * @return
     */
    private JComboBox<Integer> addSizeCombo(Integer[] values, int defaultValue, String tooltip, String label,
            Consumer<Float> c) {

        JLabel sizeLabel = new JLabel(label);
        toolBar.add(sizeLabel);

        JComboBox<Integer> combo = new JComboBox<>(values);

        combo.setSelectedItem(defaultValue);
        combo.setPreferredSize(new Dimension(60, 25));
        combo.setToolTipText(tooltip);

        combo.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {

                if (e.getStateChange() == ItemEvent.SELECTED) {

                    Float value = Float.parseFloat(e.getItem().toString());

                    c.accept(value);
                }
            }
        });

        toolBar.add(combo);
        return combo;
    }

    private JToolBar newLeftToolBar(String name) {
        JToolBar toolBar = new JToolBar(name);
        // toolBar.setLayout(new FlowLayout(FlowLayout.LEFT));
        toolBar.setFloatable(false);

        add(toolBar, BorderLayout.WEST);
        return toolBar;
    }

    private void addFileButtons() {

        addNewItem("New", "new", KeyEvent.VK_N, StrokesAndActions.CTRL_N, o -> getTabsContainer().addTab());

        addNewItem("Open File", "open", KeyEvent.VK_O, StrokesAndActions.CTRL_O, o -> getTabsContainer().open());
        addNewItem("Open Main Lara File", "open_lara", KeyEvent.VK_T, StrokesAndActions.CTRL_M,
                o -> getTabsContainer().openMain());

        toolBar.addSeparator();

        addNewItem("Close", "close", KeyEvent.VK_W, StrokesAndActions.CTRL_W,
                o -> getCurrentTab().close());
        addNewItem("Close All", "close_all", 0, StrokesAndActions.CTRL_SHIFT_W,
                o -> getTabsContainer().closeAll());

        toolBar.addSeparator();

        addNewItem("Save", "save", KeyEvent.VK_S, StrokesAndActions.CTRL_S,
                o -> getCurrentTab().save());
        addNewItem("Save As", "save_as", 0, null,
                o -> getCurrentTab().saveAs());
        addNewItem("Save All", "save_all", KeyEvent.VK_A, StrokesAndActions.CTRL_SHIFT_S,
                o -> editor.getTabsContainer().saveAll());

        // newItem( "Comment", "comment", 0, StrokesAndActions.CTRL_SHIFT_C, getCurrentTab()::commentSelection);

        // newItem(file, "Refresh", KeyEvent.VK_R, StrokesAndActions.F5,
        // o -> getCurrentTab().refresh());

    }

    private void addSearchButtons() {
        addNewItem("Find", "find", 0, StrokesAndActions.CTRL_F, a -> editor.getSearchPanel().getFocus());
        addNewItem("Clear Markup", "mark", 0, StrokesAndActions.CTRL_SHIFT_M,
                a -> {
                    RTextArea textArea = getCurrentTab().getTextArea();

                    SearchEngine.find(textArea, new SearchContext());
                });
    }

    private void addRunButtons() {
        addExecButton();
        addDebugButton();
        addTestButton();
        addNewItem("Clear Console", "clear", 0, null, o -> editor.getConsoleArea().setText(""));
        addNewItem("Show/Hide Console", "console", 0, StrokesAndActions.CTRL_SHIFT_O,
                o -> editor.swapConsoleVisibility());
        addNewItem("Show/Hide Language Specification Bar", "sidebar", 0, StrokesAndActions.CTRL_SHIFT_B,
                o -> showLS());
        addNewItem("Show Equivalent Command-Line", "command_prompt", 0, StrokesAndActions.CTRL_SHIFT_C,
                o -> showCLI());
    }

    private void showLS() {
        JComponent sideBar = editor.getLsScrollBar();
        sideBar.setVisible(!sideBar.isVisible());
        editor.getSettings().saveShowLangSpec(sideBar.isVisible());
        // Preferences.userRoot().putBoolean(editor.getShowLangSpecSetting(), sideBar.isVisible());
        // editor.revalidate();
        editor.updateUI();
    }

    private void showCLI() {
        // Get config file
        File configFile = editor.getData().get(AppKeys.CONFIG_FILE);

        if (configFile == null) {
            JOptionPane.showMessageDialog(null, "Config file not defined");
            return;
        }

        if (!configFile.isFile()) {
            JOptionPane.showMessageDialog(null, "Could not find config file '" + configFile + "'");
            return;
        }

        DataStore dataStore = editor.getPersistence().loadData(configFile);

        String string = "clava " + LaraCli.getWeaverOptions(editor.getCustomWeaverOptions()).toCli(dataStore);

        // Show in the console
        // editor.getConsoleArea().append(string + "\n");

        // Show in a dialog
        JTextArea text = new JTextArea(string);
        text.setEditable(false);
        // text.setLineWrap(true);
        JOptionPane.showMessageDialog(null, text);
        // JOptionPane.showMessageDialog(null, text, "Equivalent Command-Line Arguments", JOptionPane.OK_OPTION);
    }

    private JButton addExecButton() {

        execButton = new JButton();
        setIcon(execButton, "run", EditorToolBar.RUN_LARA_TEXT);
        // try {
        // Image img = ImageIO.read(IoUtils.resourceToStream("larai/resources/img/cancel.gif"));
        // execButton.setSelectedIcon(new ImageIcon(img));
        // } catch (Exception ex) {
        // }
        running = false;
        // execButton.setSelected(false);
        // execButton.setMnemonic(StrokesAndActions.F11);

        Consumer<ActionEvent> listener = e -> {

            if (!running) {
                // if (execButton.isSelected()) {
                editor.execute();
                // setIcon(newItem, "run", "Run LARA");
            } else {
                // setIcon(newItem, "cancel", "Cancel");
                editor.cancelExecution();

            }
        };
        execButton.addActionListener(new GenericActionListener(listener));
        String replace = StrokesAndActions.prettyString(StrokesAndActions.F11);
        execButton.setToolTipText(EditorToolBar.RUN_LARA_TEXT + " (" + replace + ")");
        execButton.registerKeyboardAction(new GenericActionListener(listener), StrokesAndActions.F11,
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        // ListenerUtils.mapAction(newItem, stroke, actionName,
        // listener);
        execButton.addFocusListener(new FocusGainedListener(e -> getCurrentTab().requestFocus()));
        toolBar.add(execButton);
        return execButton;
    }

    private JButton addDebugButton() {

        debugButton = new JButton();
        setIcon(debugButton, "debug", EditorToolBar.DEBUG_LARA_TEXT);
        // try {
        // Image img = ImageIO.read(IoUtils.resourceToStream("larai/resources/img/cancel.gif"));
        // execButton.setSelectedIcon(new ImageIcon(img));
        // } catch (Exception ex) {
        // }
        running = false;
        // execButton.setSelected(false);
        // execButton.setMnemonic(StrokesAndActions.F11);

        Consumer<ActionEvent> listener = e -> {

            if (!running) {
                // if (execButton.isSelected()) {
                editor.runDebug();
                // setIcon(newItem, "run", "Run LARA");
            } else {
                // setIcon(newItem, "cancel", "Cancel");
                editor.cancelExecution();
            }
        };
        debugButton.addActionListener(new GenericActionListener(listener));
        String replace = StrokesAndActions.prettyString(StrokesAndActions.CTRL_F11);
        debugButton.setToolTipText(EditorToolBar.RUN_LARA_IN_DEBUG_MODE_TEXT + " (" + replace + ")");
        debugButton.registerKeyboardAction(new GenericActionListener(listener), StrokesAndActions.CTRL_F11,
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        // ListenerUtils.mapAction(newItem, stroke, actionName,
        // listener);
        debugButton.addFocusListener(new FocusGainedListener(e -> getCurrentTab().requestFocus()));
        toolBar.add(debugButton);
        return debugButton;
    }

    private JButton addTestButton() {

        testButton = new JButton();
        setIcon(testButton, "test", EditorToolBar.RUN_LARA_TEXT);

        running = false;

        Consumer<ActionEvent> listener = e -> {

            if (!running) {
                editor.test();
                ;
            } else {
                editor.cancelExecution();

            }
        };
        testButton.addActionListener(new GenericActionListener(listener));
        // String replace = StrokesAndActions.prettyString(StrokesAndActions.F11);
        // testButton.setToolTipText(EditorToolBar.TEST_LARA_TEXT + " (" + replace + ")");
        testButton.setToolTipText(EditorToolBar.TEST_LARA_TEXT);
        // testButton.registerKeyboardAction(new GenericActionListener(listener), StrokesAndActions.F11,
        // JComponent.WHEN_IN_FOCUSED_WINDOW);

        testButton.addFocusListener(new FocusGainedListener(e -> getCurrentTab().requestFocus()));
        toolBar.add(testButton);
        return testButton;
    }

    public static void setIcon(JButton execButton2, String icon, String fallbackText) {
        try {
            Image img = ImageIO.read(SpecsIo.resourceToStream("larai/resources/img/" + icon + ".gif"));
            execButton2.setIcon(new ImageIcon(img));
            execButton2.setText("");
        } catch (Exception ex) {
            execButton2.setIcon(null);
            execButton2.setText(fallbackText);
        }
    }

    private JButton addNewItem(String toolTip, String icon, int keyEvent, KeyStroke stroke,
            Consumer<ActionEvent> listener) {

        JButton newItem = new JButton();
        try {
            Image img = ImageIO.read(SpecsIo.resourceToStream("larai/resources/img/" + icon + ".gif"));
            newItem.setIcon(new ImageIcon(img));
        } catch (Exception ex) {

            newItem.setText(toolTip);
        }

        // newItem.setMnemonic(keyEvent);
        newItem.addActionListener(new GenericActionListener(listener));
        newItem.setToolTipText(toolTip);
        if (stroke != null) {
            String replace = StrokesAndActions.prettyString(stroke);
            newItem.setToolTipText(toolTip + " (" + replace + ")");
            newItem.registerKeyboardAction(new GenericActionListener(listener), stroke,
                    JComponent.WHEN_IN_FOCUSED_WINDOW);
            // ListenerUtils.mapAction(newItem, stroke, actionName,
            // listener);
        }
        newItem.addFocusListener(new FocusGainedListener(e -> getCurrentTab().requestFocus()));
        toolBar.add(newItem);
        return newItem;
    }

    // private void cancel() {
    // editor.updateDataStore();
    // TabbedPane tabbedPane = (TabbedPane) SwingUtilities.getAncestorOfClass(TabbedPane.class, editor);
    // ProgramPanel program = SearchUtils.findFirstComponentOfType(tabbedPane, ProgramPanel.class);
    // System.err.println("TODO CANCEL");
    // System.err.println("Create new worker based on " + ApplicationWorker.class.getCanonicalName());
    // // program.cancel();
    //
    // }

    private TabsContainerPanel getTabsContainer() {
        return editor.getTabsContainer();
    }

    private SourceTextArea getCurrentTab() {
        return getTabsContainer().getCurrentTab();
    }

    public void setExecSelected(boolean bool) {
        execButton.setSelected(bool);
    }

    public void ativateExecButton() {
        if (!execButton.isEnabled()) {
            execButton.setEnabled(true);
            execButton.setToolTipText(EditorToolBar.RUN_LARA_TEXT);
        }
    }

    public void deativateExecButton() {
        if (execButton.isEnabled()) {
            execButton.setEnabled(false);
            execButton.setToolTipText(EditorToolBar.NO_CONFIG_MESSAGE);
        }
    }

    public void setStopRun() {
        setIcon(execButton, "cancel", "Terminate");
        execButton.setToolTipText("Terminate");
        execButton.setEnabled(true);

        setIcon(debugButton, "debug", EditorToolBar.DEBUG_LARA_TEXT);
        debugButton.setToolTipText(EditorToolBar.RUN_LARA_IN_DEBUG_MODE_TEXT);
        debugButton.setEnabled(false);
        running = true;
    }

    public void setStopDebug() {
        setIcon(execButton, "run", EditorToolBar.RUN_LARA_TEXT);
        execButton.setToolTipText(EditorToolBar.RUN_LARA_TEXT);
        execButton.setEnabled(false);

        setIcon(debugButton, "cancel", "Terminate");
        debugButton.setToolTipText("Terminate");
        debugButton.setEnabled(true);
        running = true;
    }

    public void setPlay2() {
        setIcon(execButton, "run", EditorToolBar.RUN_LARA_TEXT);
        execButton.setToolTipText(EditorToolBar.RUN_LARA_TEXT);
        execButton.setEnabled(true);

        setIcon(debugButton, "debug", EditorToolBar.DEBUG_LARA_TEXT);
        debugButton.setToolTipText(EditorToolBar.RUN_LARA_IN_DEBUG_MODE_TEXT);
        debugButton.setEnabled(true);
        running = false;
    }

    public void setPlay() {
        setExecButton("run", EditorToolBar.RUN_LARA_TEXT, false);
    }

    public void setStop() {
        setExecButton("cancel", "Terminate", true);
    }

    public void setExecButton(String icon, String text, boolean isRunning) {
        setIcon(execButton, icon, text);
        execButton.setToolTipText(text);
        running = isRunning;
    }

}
