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

package org.lara.interpreter.joptions.panels.editor;

import java.awt.BorderLayout;
import java.awt.Font;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import org.lara.interpreter.joptions.config.interpreter.LaraiKeys;
import org.lara.interpreter.joptions.config.interpreter.VerboseLevel;
import org.lara.interpreter.joptions.keys.FileList;
import org.lara.interpreter.joptions.panels.editor.components.EditorToolBar;
import org.lara.interpreter.joptions.panels.editor.components.Explorer;
import org.lara.interpreter.joptions.panels.editor.components.LanguageSpecificationSideBar;
import org.lara.interpreter.joptions.panels.editor.components.SearchPanel;
import org.lara.interpreter.joptions.panels.editor.listeners.FocusGainedListener;
import org.lara.interpreter.joptions.panels.editor.tabbed.MainLaraTab;
import org.lara.interpreter.joptions.panels.editor.tabbed.TabsContainerPanel;
import org.lara.interpreter.joptions.panels.editor.utils.LaraWorker;
import org.lara.interpreter.joptions.panels.editor.utils.SettingsManager;
import org.lara.language.specification.LanguageSpecification;
import org.suikasoft.jOptions.Interfaces.DataStore;
import org.suikasoft.jOptions.app.AppPersistence;
import org.suikasoft.jOptions.gui.panels.app.AppKeys;
import org.suikasoft.jOptions.gui.panels.app.GuiTab;
import org.suikasoft.jOptions.gui.panels.app.TabbedPane;

import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.logging.TextAreaHandler;

/**
 * THis is the complete tab containing all the panels of the editor
 *
 * @author Tiago
 *
 */
public class EditorPanel extends GuiTab {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private final SettingsManager settings;
    private final TabsContainerPanel tabsContainer;
    // private final LanguageSpecificationSideBar langSpecSideBar;
    private final AppPersistence persistence;
    private File canonicalAspectFile;
    private DataStore optionsDataStore;
    private File outputFile;
    private final SearchPanel searchPanel;
    private final JTextArea outputArea;
    private final JScrollPane consolePanel;
    private final JSplitPane splitterConsole;
    // private boolean init = true;
    private final LanguageSpecificationSideBar langSpecSideBar;
    private double lasSplitSize = 0.75;
    private Explorer explorer;
    private final LaraWorker worker;
    private final EditorToolBar menu;
    // private boolean requiresUpdate = false;

    private boolean runDebug = false;

    private JSplitPane splitterExplorer;
    public static final int DEFAULT_FONT = 12;

    // public static EditorPanel newInstance(DataStore dataStore) {
    // return new EditorPanel(dataStore);
    // }
    //
    // public EditorPanel(DataStore dataStore) {
    // this(dataStore, null, null);
    // }

    public static EditorPanel newInstance(DataStore dataStore, AppPersistence persistence,
            LanguageSpecification langSpec) {
        return new EditorPanel(dataStore, persistence, langSpec);
    }

    public EditorPanel(DataStore dataStore, AppPersistence persistence, LanguageSpecification langSpec) {
        super(dataStore);
        setLayout(new BorderLayout());
        this.settings = new SettingsManager(this, getAppName());
        this.persistence = persistence;
        canonicalAspectFile = null;
        explorer = new Explorer(this);
        worker = new LaraWorker(this);

        tabsContainer = new TabsContainerPanel(explorer);
        menu = new EditorToolBar(this);
        searchPanel = new SearchPanel(this);
        outputArea = new JTextArea();
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, EditorPanel.DEFAULT_FONT));
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        // outputArea.setColumns(20);
        // outputArea.setRows(5);
        consolePanel = new javax.swing.JScrollPane(outputArea);
        // consolePanel.setPreferredSize(new Dimension(200, 200));
        TextAreaHandler jTextAreaHandler = new TextAreaHandler(outputArea);
        SpecsLogs.addHandler(jTextAreaHandler);

        DefaultCaret caret = (DefaultCaret) outputArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        langSpecSideBar = new LanguageSpecificationSideBar(this, langSpec);
        add(menu, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());

        centerPanel.add(tabsContainer, BorderLayout.CENTER);

        // lsScrollBar = new JScrollPane(langSpecSideBar);
        // lsScrollBar = langSpecSideBar;

        centerPanel.add(langSpecSideBar, BorderLayout.EAST);

        splitterConsole = new JSplitPane(JSplitPane.VERTICAL_SPLIT, centerPanel, consolePanel);
        splitterConsole.addPropertyChangeListener("dividerLocation", p -> {
            // System.out.println("changed: " + p.getNewValue());
            // Only save split factor if the console panel is visible (hiding the console panel makes split factor to be
            // almost 1.0
            if (consolePanel.isVisible()) {
                // System.out.println("Changing size when console is not visible");
                settings.saveConsoleSplitFactor(getDividerProportion(splitterConsole));
            }
        });
        // splitterConsole.setDividerLocation(this.lasSplitSize);
        // splitterConsole.add(centerPanel);
        // splitterConsole.add(consolePanel);
        // add(splitterConsole, BorderLayout.CENTER);

        splitterExplorer = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitterExplorer.add(explorer);
        splitterExplorer.add(splitterConsole);
        splitterExplorer.addPropertyChangeListener("dividerLocation", p -> {
            // System.out.println("changed: " + p.getNewValue());
            double dividerProportion = getDividerProportion(splitterExplorer);
            settings.saveExplorerSplitFactor(dividerProportion);
            // System.out.println("Exp. SAVE: " + dividerProportion);
        });

        add(splitterExplorer, BorderLayout.CENTER);

        add(searchPanel, BorderLayout.SOUTH);

        addFocusListener(new FocusGainedListener(x -> tabsContainer.requestFocus()));
    }

    public void setOutputAreaFont(Float size) {

        outputArea.setFont(outputArea.getFont().deriveFont(size));
    }

    public void setTabsFont(Float size) {

        tabsContainer.setTabsFont(size);
    }

    @Override
    public void enterTab() {

        loadEditorPreferences();

        optionsDataStore = null;
        outputFile = null;

        if (!getData().hasValue(AppKeys.CONFIG_FILE)) {
            menu.deativateExecButton();
            return;
        }
        outputFile = getData().get(AppKeys.CONFIG_FILE);

        optionsDataStore = extractDataStore(outputFile);

        if (optionsDataStore == null) {
            menu.deativateExecButton();
            return;
        }
        menu.ativateExecButton();

        // DataStore setup = application.getPersistence().loadData(file);
        updateProjects(optionsDataStore);
        // }
        explorer.refreshAllExceptMain();
    }

    private void loadEditorPreferences() {

        lasSplitSize = settings.loadConsoleSplitFactor(0.75);
        splitterConsole.setDividerLocation(lasSplitSize);
        double newSplitFactor = settings.loadExplorerSplitFactor(0.25);
        // System.out.println("Exp. LOAD: " + newSplitFactor);
        splitterExplorer.setDividerLocation(newSplitFactor);
        boolean showConsole = settings.loadShowConsole(true);
        consolePanel.setVisible(showConsole);
        boolean showLangSpec = settings.loadShowLangSpec(true);
        langSpecSideBar.setVisible(showLangSpec);
        revalidate();
    }

    //
    // private void loadEditorProperties() {
    // if (optionsDataStore.hasValue(EditorKeys.splitSize)) {
    // Double proportionalLocation = optionsDataStore.get(EditorKeys.splitSize);
    // System.out.println("LAST: " + proportionalLocation);
    // splitterConsole.setDividerLocation(proportionalLocation);
    // // System.out.println("SPLIT: " + proportionalLocation);
    // } else {
    // if (init) {
    // splitterConsole.setDividerLocation(lasSplitSize);
    // init = false;
    // }
    // }
    //
    // if (optionsDataStore.hasValue(EditorKeys.isBarShown)) {
    // lsScrollBar.setVisible(optionsDataStore.get(EditorKeys.isBarShown));
    // revalidate();
    // }
    // if (optionsDataStore.hasValue(EditorKeys.isOutputShown)) {
    // lsScrollBar.setVisible(optionsDataStore.get(EditorKeys.isOutputShown));
    // revalidate();
    // }
    // }

    private DataStore extractDataStore(File file) {

        if (!file.isFile()) {
            SpecsLogs.getLogger().warning("Configuration file does not exist: '" + file + "'");
            return null;
        }
        try {

            return persistence.loadData(file);
        } catch (Exception e) {
            SpecsLogs
                    .msgWarn("Configuration file '" + file + "' is not a compatible options file: " + e.getMessage());

        }
        return null;
    }

    private void updateProjects(DataStore dataStore) {

        if (dataStore.hasValue(LaraiKeys.LARA_FILE)) {
            File inFile = dataStore.get(LaraiKeys.LARA_FILE);
            File newCanonFile = SpecsIo.getCanonicalFile(inFile);
            // System.out.println("Options Lara file: " + inFile + "(" + newCanonFile + ")");
            if (canonicalAspectFile != null
                    && SpecsIo.getCanonicalPath(newCanonFile).equals(SpecsIo.getCanonicalPath(canonicalAspectFile))) {
                return; // It is still the same file so we do not want to update
            }
            tabsContainer.loadMainAspectFile(newCanonFile);
            canonicalAspectFile = newCanonFile;
        } else {
            // System.out.println("Options data store has no lara file");
        }

        if (dataStore.hasValue(LaraiKeys.WORKSPACE_FOLDER)) {
            FileList inFile = dataStore.get(LaraiKeys.WORKSPACE_FOLDER);

            // System.out.println("Options Lara file: " + inFile + "(" + newCanonFile + ")");
            // if (canonicalAspectFile != null
            // && IoUtils.getCanonicalPath(newCanonFile).equals(IoUtils.getCanonicalPath(canonicalAspectFile))) {
            // return; // It is still the same file so we do not want to update
            // }
            explorer.setWorkspaces(inFile);
        }
        if (dataStore.hasValue(LaraiKeys.OUTPUT_FOLDER)) {
            File outputDir = dataStore.get(LaraiKeys.OUTPUT_FOLDER);
            explorer.setOutputDir(outputDir);
        }
    }

    @Override
    public void exitTab() {

        if (outputFile == null || optionsDataStore == null) {
            MainLaraTab mainTab = tabsContainer.getMainTab();
            if (!mainTab.isNew()) {
                File laraFile = mainTab.getLaraFile();
                canonicalAspectFile = SpecsIo.getCanonicalFile(laraFile);
            }
            return;
        }

        // saveEditorPreferences();
        updateDataStore();
    }

    // private void saveEditorPreferences() {
    // String appName = getAppName();
    // double lasSplitSize = getDividerProportion();
    // Preferences.userRoot().putDouble(CONSOLE_SPLIT_FACTOR_PREFIX + appName, lasSplitSize);
    // }

    private String getAppName() {
        return getData().get(TabbedPane.getAppNameKey());
    }

    public void updateDataStore() {
        // saveEditorProperties();

        MainLaraTab mainTab = tabsContainer.getMainTab();
        if (mainTab.isNew()) {
            return; // Do nothing
        }
        File laraFile = mainTab.getLaraFile();
        File canonFile = SpecsIo.getCanonicalFile(laraFile);

        if (canonicalAspectFile == null
                || SpecsIo.getCanonicalPath(canonFile).equals(SpecsIo.getCanonicalPath(canonicalAspectFile))) {
            return;
        }
        canonicalAspectFile = canonFile;
        optionsDataStore.setRaw(LaraiKeys.LARA_FILE, canonicalAspectFile);

        persistence.saveData(outputFile, optionsDataStore);

    }

    // private void saveEditorProperties() {
    // optionsDataStore.setRaw(EditorKeys.splitSize, getDividerProportion());
    // optionsDataStore.setRaw(EditorKeys.isBarShown, langSpecSideBar.isVisible());
    // optionsDataStore.setRaw(EditorKeys.isOutputShown, outputArea.isVisible());
    // }

    // public double getConsoleDividerProportion() {
    // return getDividerProportion(splitterConsole);
    // }
    //
    // public double getConsoleDividerProportion() {
    // return getDividerProportion(splitterConsole);
    // }

    public double getDividerProportion(JSplitPane splitter) {
        int orientation = splitter.getOrientation();
        int location = splitter.getDividerLocation();
        if (orientation == JSplitPane.HORIZONTAL_SPLIT) {
            int width = splitter.getWidth();
            int divSize = splitter.getDividerSize();
            return location / (double) (width - divSize);
        }
        int height = splitter.getHeight();
        int divSize = splitter.getDividerSize();
        return location / (double) (height - divSize);

    }

    @Override
    public String getTabName() {
        return "Lara Editor";
    }

    public TabsContainerPanel getTabsContainer() {
        return tabsContainer;
    }

    public JScrollPane getConsoleScroll() {
        return consolePanel;
    }

    public JTextArea getConsoleArea() {
        return outputArea;
    }

    // public LanguageSpecificationSideBar getLangSpecSideBar() {
    // return langSpecSideBar;
    // }

    public JComponent getLsScrollBar() {
        return langSpecSideBar;
    }

    // public double getLasSplitSize() {
    // return lasSplitSize;
    // }

    // public void setLasSplitSize(double lasSplitSize) {
    // this.lasSplitSize = lasSplitSize;
    // }

    // public JSplitPane getSplitter() {
    // return splitterConsole;
    // }

    public void swapConsoleVisibility() {
        // consolePanel.setVisible(!consolePanel.isVisible());
        if (consolePanel.isVisible()) {
            lasSplitSize = getDividerProportion(splitterConsole);
            consolePanel.setVisible(false);
        } else {
            consolePanel.setVisible(true);
            splitterConsole.setDividerLocation(lasSplitSize);
        }
        settings.saveShowConsole(consolePanel.isVisible());
        // Preferences.userRoot().putBoolean(getShowConsoleSetting(), consolePanel.isVisible());
        revalidate();
    }

    public SearchPanel getSearchPanel() {
        return searchPanel;
    }

    public Explorer getExplorer() {
        return explorer;
    }

    public void setExplorer(Explorer explorer) {
        this.explorer = explorer;
    }

    public void execute() {

        getConsoleArea().setText("");
        if (optionsDataStore == null) {
            JOptionPane.showMessageDialog(this, EditorToolBar.NO_CONFIG_MESSAGE);
            return;
        }

        boolean success = getTabsContainer().askSave();
        if (!success) {
            return;
        }

        updateDataStore();
        runDebug = false;
        worker.execute(optionsDataStore);
    }

    public void runDebug() {

        getConsoleArea().setText("");
        if (optionsDataStore == null) {
            JOptionPane.showMessageDialog(this, EditorToolBar.NO_CONFIG_MESSAGE);
            return;
        }

        boolean success = getTabsContainer().askSave();
        if (!success) {
            return;
        }

        updateDataStore();
        // boolean originalDebug = optionsDataStore.get(LaraiKeys.DEBUG_MODE);
        // VerboseLevel originalVerbose = optionsDataStore.get(LaraiKeys.VERBOSE);
        // System.out.println(optionsDataStore);

        DataStore tempDS = DataStore.newInstance(
                optionsDataStore.getStoreDefinition().map(sd -> sd.getName()).orElse(optionsDataStore.getName()),
                optionsDataStore);
        tempDS.setRaw(LaraiKeys.DEBUG_MODE, true);
        tempDS.setRaw(LaraiKeys.TRACE_MODE, true);
        tempDS.setRaw(LaraiKeys.VERBOSE, VerboseLevel.all);
        runDebug = true;
        executeLARA(tempDS);

    }

    private void executeLARA(DataStore setup) {
        worker.execute(setup);
    }

    public void cancelExecution() {
        worker.shutdown();
    }

    public void setPlayButton() {
        menu.setPlay2();
    }

    public void setStopButton() {
        if (runDebug) {
            menu.setStopDebug();
        } else {
            menu.setStopRun();
        }
    }

    public SettingsManager getSettings() {
        return settings;
    }

}
