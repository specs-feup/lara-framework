/**
 * Copyright 2017 SPeCS.
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
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.function.Consumer;
import java.util.prefs.Preferences;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class DontAskMeAgainPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = -4536122924262111427L;

    private JCheckBox dontAskMeAgain;

    public DontAskMeAgainPanel(Object message) {
        setLayout(new BorderLayout());
        if (message instanceof Component) {
            add((Component) message);
        } else if (message != null) {
            add(new JLabel(message.toString()));
        }
        dontAskMeAgain = new JCheckBox("Don't ask me again");
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.add(dontAskMeAgain);
        add(panel, BorderLayout.SOUTH);
    }

    public boolean dontAskMeAgain() {
        return dontAskMeAgain.isSelected();
    }

    // private static Properties settings;

    // protected static void loadProperties() {
    // if (settings != null) {
    // settings = new Properties();
    // File source = new File("...");
    // if (source.exists()) {
    // try (Reader r = new FileReader(source)) {
    // settings.load(r);
    // } catch (IOException exp) {
    // exp.printStackTrace();
    // }
    // }
    // }
    // }
    //
    // String askSaveSetting = getEditor().getEditorPanel().getAskSaveSetting();
    // boolean askSave = true;
    // try {
    // askSave = !Preferences.userRoot().nodeExists(askSaveSetting);
    // } catch (BackingStoreException e) {
    // SpecsLogs.warn("Could not get 'ask to save' setting:\n", e);
    // }
    // if (askSave) {
    // for (int i = 0; i < tabbedPane.getTabCount(); i++) {
    //
    // SourceTextArea editorTab = getTab(i);
    // boolean success = editorTab.askSave();
    // if (!success) {
    // return false;
    // }
    // }
    // return true;
    // }
    // boolean save = Preferences.userRoot().getBoolean(askSaveSetting, true);
    // if (save) {
    // for (int i = 0; i < tabbedPane.getTabCount(); i++) {
    // SourceTextArea editorTab = getTab(i);
    // boolean wasSaved = editorTab.save();
    // if (!wasSaved) {
    // SpecsLogs.warn("Could not save file " + editorTab.getLaraFile());
    // }
    // }
    // return true;
    // }

    protected static void saveProperties(String prefKey, int result) {
        // if (settings != null) {
        // settings = new Properties();
        // File source = new File("...");
        // try (Writer w = new FileWriter(source)) {
        // settings.store(w, "Don't prompt for settings");
        // } catch (IOException exp) {
        // exp.printStackTrace();
        // }
        // }
        Preferences.userRoot().putInt(prefKey, result);
    }

    public static int showConfirmDialog(Component parent, Object message, String title, int optionType,
            Consumer<Integer> saveProperty) {

        // if (result != NO_VALUE) {
        // return result;
        // }
        int result = JOptionPane.NO_OPTION;

        // if (settings.containsKey(key + ".prompt") && !Boolean.parseBoolean(settings.getProperty(key + ".value"))) {
        // result = Integer.parseInt(settings.getProperty(key + ".value"));
        // } else {
        DontAskMeAgainPanel panel = new DontAskMeAgainPanel(message);
        result = JOptionPane.showConfirmDialog(parent, panel, title, optionType);
        if (panel.dontAskMeAgain() && (result != JOptionPane.CANCEL_OPTION && result != JOptionPane.CLOSED_OPTION)) {
            // settings.put(key + ".prompt", "false");
            // settings.put(key + ".value", Integer.toString(result));
            saveProperty.accept(result);
            // saveProperties(key, result);
            // }
        }
        return result;
    }
}
