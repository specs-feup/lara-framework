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

package org.lara.interpreter.joptions.gui;

import java.awt.Dimension;
import java.awt.Frame;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import org.apache.commons.cli.CommandLine;
import org.lara.interpreter.cli.OptionsParser;
import org.lara.interpreter.exception.LaraIException;
import org.lara.interpreter.joptions.config.interpreter.LaraiKeys;
import org.lara.interpreter.joptions.panels.editor.EditorPanel;
import org.lara.interpreter.joptions.panels.editor.listeners.WindowsFocusGainedListener;
import org.lara.interpreter.joptions.panels.editor.utils.SearchUtils;
import org.lara.interpreter.weaver.defaultweaver.DefaultWeaver;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.suikasoft.jOptions.Interfaces.DataStore;
import org.suikasoft.jOptions.app.App;
import org.suikasoft.jOptions.app.AppKernel;
import org.suikasoft.jOptions.app.AppPersistence;
import org.suikasoft.jOptions.gui.SimpleGui;
import org.suikasoft.jOptions.gui.panels.app.ProgramPanel;
import org.suikasoft.jOptions.gui.panels.app.TabProvider;
import org.suikasoft.jOptions.gui.panels.option.StringListPanel;
import org.suikasoft.jOptions.persistence.XmlPersistence;
import org.suikasoft.jOptions.storedefinition.StoreDefinition;

import larai.LaraI;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.SpecsSystem;

public class LaraLauncher {

    public static void main(String[] args) {
        SpecsSystem.programStandardInit();

        launch(args, new DefaultWeaver());
    }

    /**
     * Launch LaraI with the given engine and the input arguments. If no arguments are given a GUI is launched
     *
     * <p>
     * Can throw exceptions.
     *
     * @param args
     * @param engine
     */
    public static boolean launch(String[] args, WeaverEngine engine) {

        // SpecsSystem.programStandardInit();

        return LaraI.exec(args, engine);
        //
        // try {
        // return LaraI.exec(args, engine);
        // // if (!sucess) {
        // // LoggingUtils.msgInfo("LARAI execution returned false");
        // // System.exit(1);
        // // }
        // } catch (RuntimeException e) {
        // LoggingUtils.msgInfo("Exception while running weaver: " + e.getMessage());
        // return false;
        // }
        //
    }

    public static void launchGUI(WeaverEngine engine, Optional<File> configFile) {
        long time = System.currentTimeMillis();
        AppKernel kernel = new LaraiLauncherKernel(engine);
        SpecsLogs.msgInfo("Starting GUI..."); // replace this with a splash screen

        StoreDefinition laraiDefinition = OptionsParser.getLaraStoreDefinition(engine);

        String appName = engine.getNameAndBuild();
        // String appName = engine.getName();
        //
        // var implVersion = SpecsSystem.getBuildNumber();
        // if (implVersion != null) {
        // appName += " (build " + implVersion + ")";
        // }

        List<TabProvider> otherTabs = new ArrayList<>();
        XmlPersistence persistence = OptionsParser.getXmlPersistence(laraiDefinition);

        otherTabs.add(dataStore -> EditorPanel.newInstance(dataStore, persistence, engine));

        App app = App.newInstance(appName, laraiDefinition, persistence, kernel)
                .setOtherTabs(otherTabs)
                .setNodeClass(engine.getClass())
                .setIcon(engine.getIcon());

        SimpleGui gui = new SimpleGui(app);

        JFrame guiFrame = gui.getFrame();

        guiFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);// <- prevent closing
        guiFrame.setPreferredSize(new Dimension(1200, 750));
        guiFrame.setExtendedState(guiFrame.getExtendedState() | Frame.MAXIMIZED_BOTH);
        guiFrame.addWindowFocusListener(new WindowsFocusGainedListener(e -> refreshCurrentTab(guiFrame)));
        guiFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                EditorPanel editorPanel = SearchUtils.findFirstComponentOfType(guiFrame, EditorPanel.class);
                JTabbedPane parent = (JTabbedPane) editorPanel.getParent();
                parent.setSelectedComponent(editorPanel);

                boolean success = editorPanel.getTabsContainer().getMainTab().saveBeforeClose();
                if (!success) {
                    return;
                }
                success = editorPanel.closingProgram();
                // success = editorPanel.getTabsContainer().closeAll();
                if (!success) {
                    return;
                }

                // guiFrame.dispose(); <-- this does not close the JVM instance, i.e., the window is closed but the
                // process is still executing
                System.exit(0); // TODO - replace with correct close method

            }
        });

        if (configFile.isPresent()) {
            ProgramPanel programPanel = SearchUtils.findFirstComponentOfType(guiFrame, ProgramPanel.class);
            programPanel.getFilenameTextField().setSelectedItem(configFile.get());
        }

        // Initialize External Dependencies predefined values

        var externalDependenciesPanel = (StringListPanel) gui.getAppFrame().getTabbedPane().getOptionsPanel()
                .getPanel(LaraiKeys.EXTERNAL_DEPENDENCIES);
        externalDependenciesPanel.setPredefinedValues(engine.getPredefinedExternalDependencies());

        // externalDependencies.getDefaultSeparator()

        // System.out.println("NAME: " + LaraiKeys.EXTERNAL_DEPENDENCIES.getName());
        // var externalDependenciesPanel = (StringListPanel) LaraiKeys.EXTERNAL_DEPENDENCIES.getPanel(null);
        // externalDependenciesPanel.setPredefinedValues(engine.getPredefinedExternalDependencies());
        // guiFrame.revalidate();
        // guiFrame.repaint();

        SpecsLogs.debug("Lara GUI load time: " + (System.currentTimeMillis() - time));

        // This code is making the console area to use all window
        // JTabbedPane tabbedPane = SearchUtils.findFirstComponentOfType(guiFrame, JTabbedPane.class);
        // tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1); // So it opens in the editor tab
        gui.execute();

    }

    private static void refreshCurrentTab(JFrame guiFrame) {
        EditorPanel editorPanel = SearchUtils.findFirstComponentOfType(guiFrame, EditorPanel.class);
        if (editorPanel != null) {

            editorPanel.getTabsContainer().getCurrentTab().refresh();
        }
    }

    /**
     * Execute LaraI with the given configuration file and other options
     *
     * @param cmd
     */
    public static void launch(WeaverEngine weaverEngine, File configFile, boolean guiMode) {
        // System.out.println(
        // "running config " + configFile + " in gui mode? " + guiMode + ". weaver: "
        // + weaverEngine.getClass().getName());

        if (!guiMode) {
            StoreDefinition laraiDefinition = OptionsParser.getLaraStoreDefinition(weaverEngine);
            AppPersistence persistence = OptionsParser.getXmlPersistence(laraiDefinition);
            DataStore laraiStore = persistence.loadData(configFile);
            LaraI.exec(laraiStore, weaverEngine);
        } else {
            launchGUI(weaverEngine, Optional.of(configFile));
        }

    }

    /**
     * Execute LaraI with the given configuration file and other options
     *
     * @param cmd
     */
    public static void launch(WeaverEngine weaverEngine, File configFile, boolean guiMode, String fileName,
            CommandLine cmd) {

        if (!guiMode) {
            StoreDefinition laraiDefinition = OptionsParser.getLaraStoreDefinition(weaverEngine);
            AppPersistence persistence = OptionsParser.getXmlPersistence(laraiDefinition);
            DataStore laraiStore = persistence.loadData(configFile);
            // OptionsConverter.commandLine2DataStore(cmd.get, cmd, weaverOptions)
            LaraI.exec(laraiStore, weaverEngine);
        } else {
            throw new LaraIException(
                    "Cannot use configuration file and GUI mode and overriding options at the same time. Alternatives: Config + GUI or Config + <overriding options>");
            // launchGUI(weaverEngine, Optional.of(configFile));
        }

    }
}
