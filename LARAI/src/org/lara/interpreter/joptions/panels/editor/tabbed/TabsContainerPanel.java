/*
 * Copyright 2010 SPeCS Research Group.
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

package org.lara.interpreter.joptions.panels.editor.tabbed;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.lara.interpreter.joptions.panels.editor.components.Explorer;
import org.lara.interpreter.joptions.panels.editor.listeners.FileTransferHandler;
import org.lara.interpreter.joptions.panels.editor.listeners.TabbedListener;
import org.lara.interpreter.joptions.panels.editor.utils.Factory;

/**
 * This source files panel contains a tabbed pane that will contain the editor tabs. The main tab, which is the main
 * aspect file, cannot be closed.
 *
 * @author Tiago
 */
public class TabsContainerPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    // private final List<LaraTab> tabs;
    private SourceTextArea currentTab;
    private final MainLaraTab mainTab;
    private File lastOpenedFolder;
    private final JTabbedPane tabbedPane;
    private Explorer explorer;

    public TabsContainerPanel(Explorer explorer) {
	super(new GridLayout(1, 1));
	setExplorer(explorer);
	// tabs = new ArrayList<>();
	lastOpenedFolder = new File(".");
	tabbedPane = new JTabbedPane();
	mainTab = new MainLaraTab(this);
	addTab(mainTab);

	// ButtonTabComponent tabComp = (ButtonTabComponent) tabbedPane.getTabComponentAt(0);
	// tabComp.remove(1); // remove button!
	setCurrentTab(mainTab);

	TabbedListener.newInstance(this);
	// Set program panel as currentTab

	// currentTab = mainTab;
	// currentTab.enterTab();

	// Add the tabbed pane to this panel.
	add(tabbedPane);

	// tabbedPane.setSelectedIndex(0);

	// The following line enables to use scrolling tabs.
	tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
	tabbedPane.setTransferHandler(new FileTransferHandler(this::open));
    }

    public void loadMainAspectFile(File aspectFile) {
	mainTab.load(aspectFile);
	lastOpenedFolder = aspectFile.getParentFile();
	explorer.setMainWorkspace(aspectFile);
	removeDuplicatedLaraFiles();
    }

    private void removeDuplicatedLaraFiles() {

	File laraFile = mainTab.getLaraFile();
	int index = 1;
	while (index < tabbedPane.getTabCount()) {

	    SourceTextArea editorTab = getTab(index);

	    File laraFile2 = editorTab.getLaraFile();
	    if (laraFile2.equals(laraFile)) {
		editorTab.close();
	    }
	    index++;
	}
	tabbedPane.setSelectedIndex(0);
    }

    /**
     * Create a new tab with an empty file with the default name
     */
    public SourceTextArea addTab() {
	SourceTextArea newTab = new SourceTextArea(this); // unnamed file
	addTab(newTab);
	return newTab;
    }

    public SourceTextArea addTab(File file) {
	SourceTextArea newTab = new SourceTextArea(file, this); // unnamed file
	addTab(newTab);
	return newTab;
    }

    // /**
    // * Create a new tab with the given file
    // *
    // * @param file
    // * @return
    // */
    // private EditorTab addTab(File file) {
    // EditorTab newTab = new EditorTab(file, this);
    // addTab(newTab);
    // return newTab;
    // }

    /**
     * Adds a tab to the tabbed pane
     *
     * @param tab
     * @return
     */
    private int addTab(SourceTextArea tab) {
	int baseMnemonic = KeyEvent.VK_1;
	// RTextScrollPane sp = new RTextScrollPane(tab);
	tabbedPane.addTab(tab.getTabName(), tab);
	int currentIndex = getTabIndex(tab);
	tabbedPane.setMnemonicAt(currentIndex, baseMnemonic + currentIndex);
	tabbedPane.setSelectedIndex(currentIndex);
	// tabbedPane.setTitleAt(currentIndex, "TEST");

	// Component comp = tabbedPane.getTabComponentAt(currentIndex);
	// System.out.println(comp);
	// tabbedPane.setTabComponentAt(currentIndex, new ButtonTabComponent(tab));

	return currentIndex;
    }

    public void setTabTitle(SourceTextArea tab) {
	setTabTitle(getTabIndex(tab), tab.getTabName());
    }

    public void setChanged(SourceTextArea tab) {
	setTabTitle(getTabIndex(tab), "*" + tab.getTabName());
    }

    private void setTabTitle(int tabIndex, String tabName) {
	tabbedPane.setTitleAt(tabIndex, tabName);
	// ButtonTabComponent tabTitle = (ButtonTabComponent) tabbedPane.getTabComponentAt(tabIndex);
	// tabTitle.setTitle(tabName);
    }

    public void closeTab(SourceTextArea tab) {

	int pos = getTabIndex(tab);
	closeAndRemoveTab(pos);
	if (!currentTab.equals(tab) && pos >= tabbedPane.getTabCount()) {
	    tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
	}
	updateMnemonics();
    }

    private void closeAndRemoveTab(int pos) {
	tabbedPane.removeTabAt(pos);
    }

    public SourceTextArea getCurrentTab() {
	return currentTab;
    }

    public void setCurrentTab(SourceTextArea currentTab) {
	this.currentTab = currentTab;
    }

    public JTabbedPane getTabbedPane() {
	return tabbedPane;
    }

    public void navigatePrevious() {
	int tabCount = tabbedPane.getTabCount();
	if (tabCount == 1) {
	    return;
	}
	int index = getTabIndex(currentTab);
	if (index == 0) { // If is the first, then go to the last
	    tabbedPane.setSelectedIndex(tabCount - 1);
	} else {
	    tabbedPane.setSelectedIndex(index - 1);
	}
    }

    public void navigateNext() {
	int tabCount = tabbedPane.getTabCount();
	if (tabCount == 1) {
	    return;
	}
	int index = getTabIndex(currentTab);
	if (index == tabbedPane.getTabCount() - 1) { // If is the last, then go to the first
	    tabbedPane.setSelectedIndex(0);
	} else {
	    tabbedPane.setSelectedIndex(index + 1);
	}
    }

    /**
     * Close current tab
     */
    public void closeCurrent() {
	currentTab.close();
    }

    /**
     * Close all tabs to the left and right of the current tab (except for the main tab)
     */
    public void closeAllButCurrent() {
	closeLeft();
	closeRight();
    }

    /**
     * Close all tabs except main tab
     */
    public boolean closeAll() {
	tabbedPane.setSelectedIndex(0);
	return closeRight();
    }

    public boolean closeLeft() {
	int refIndex = getTabIndex(currentTab);
	if (refIndex <= 1) { // is the main tab or the one next to the main tab////////currentTab.equals(mainTab)
	    return true;
	}
	int removingIndex = 1; // Start after the main tab
	while (removingIndex < refIndex) {
	    SourceTextArea editorTab = getTab(removingIndex);
	    boolean closed = editorTab.close();
	    if (!closed) { // i.e., the save request was made but user cancel closing this file
		// removingIndex++; // jump to next closing file
		return false;
	    }
	    // else {
	    // refIndex--; // update reference index
	    // }
	}
	return true;
	// tabbedPane.removeTabAt(tabbedPane.getTabCount() - 1);
	// }
    }

    public boolean closeRight() {
	int index = getTabIndex(currentTab);

	int removingIndex = index + 1;
	while (removingIndex < tabbedPane.getTabCount()) { // Think better!

	    SourceTextArea editorTab = getTab(removingIndex);
	    boolean closed = editorTab.close();
	    if (!closed) { // i.e., the save request was made but user cancel closing this file
		// removingIndex++;
		return false;
	    }
	}
	return true;
    }

    /**
     * Get a tab based on the given file
     *
     * @param f
     * @return
     */
    public SourceTextArea getTab(File f) {

	int tabPos = getTabIndex(f);
	return tabPos > -1 ? getTab(tabPos) : null;
    }

    /**
     * Get a tab by its index
     *
     * @param i
     * @return
     */
    private SourceTextArea getTab(int i) {
	return (SourceTextArea) tabbedPane.getComponentAt(i);

    }

    /**
     * Get tab index based on a file
     *
     * @param f
     * @return
     */
    public int getTabIndex(File f) {
	String absolutePath = f.getAbsolutePath();

	for (int i = 0; i < tabbedPane.getTabCount(); i++) {
	    // System.out.println(tabbedPane.getComponent(i));
	    SourceTextArea tab = getTab(i);
	    if (tab.getTextArea().getFileFullPath().equals(absolutePath)) {

		return i;
	    }
	}
	return -1;
    }

    /**
     * Get tab index based on a EditorTab instance
     *
     * @param tab
     * @return
     */
    private int getTabIndex(SourceTextArea tab) {
	return tabbedPane.indexOfComponent(tab);
    }

    private void updateMnemonics() {
	for (int i = 0; i < tabbedPane.getTabCount() && i < 10; i++) {
	    tabbedPane.setMnemonicAt(i, KeyEvent.VK_1 + i);
	}
    }

    public MainLaraTab getMainTab() {
	return mainTab;
    }

    public void saveAll() {
	for (int i = 0; i < tabbedPane.getTabCount(); i++) {

	    SourceTextArea editorTab = getTab(i);
	    editorTab.save();
	}
    }

    public boolean askSave() {
	for (int i = 0; i < tabbedPane.getTabCount(); i++) {

	    SourceTextArea editorTab = getTab(i);
	    boolean success = editorTab.askSave();
	    if (!success) {
		return false;
	    }
	}
	return true;
    }

    public boolean saveBeforeClose() {

	for (int i = 0; i < tabbedPane.getTabCount(); i++) {
	    SourceTextArea editorTab = getTab(i);
	    boolean cancelled = !editorTab.saveBeforeClose();
	    if (cancelled) {
		return false;
	    }
	}
	return true;
    }

    public File getLastOpenedFolder() {
	return lastOpenedFolder;
    }

    public void setLastOpenedFolder(File lastOpenedFolder) {
	this.lastOpenedFolder = lastOpenedFolder;
    }

    public void setTabToolTipText(SourceTextArea tab, String text) {

	// ButtonTabComponent tabTitle = (ButtonTabComponent) tabbedPane.getTabComponentAt(getTabIndex(tab));
	tabbedPane.setToolTipTextAt(getTabIndex(tab), text);
	// tabTitle.setToolTipText(text);
    }

    public void moveTab(SourceTextArea nextTab) {
	// Exit current tab
	SourceTextArea curTab = getCurrentTab();

	// If already in the tab don't do anything
	if (curTab.equals(nextTab)) {
	    return;
	}

	curTab.exitTab();
	// Update current tab
	// currentTab = tabs.get(sel);
	curTab = nextTab;
	setCurrentTab(curTab);
	// Enter current tab
	curTab.enterTab();
    }

    public void open() {
	File refFile = getCurrentTab().getLaraFile();
	if (!refFile.exists()) {
	    refFile = lastOpenedFolder;
	}
	JFileChooser fc = Factory.newFileChooser(refFile);

	int returnVal = fc.showOpenDialog(this);

	if (returnVal == JFileChooser.APPROVE_OPTION) {
	    File file = fc.getSelectedFile();
	    open(file);
	}
    }

    public void open(File file) {
	int tabIndex = getTabIndex(file);

	if (tabIndex > -1) {// If the file already exists
	    // change the focus to the corresponding tab
	    tabbedPane.setSelectedIndex(tabIndex);
	} else {
	    addTab(file);
	    // tab.load(file);
	    setLastOpenedFolder(file.getParentFile());
	}
    }

    public void openMain() {
	tabbedPane.setSelectedIndex(0);
	boolean open = mainTab.open();
	if (open) {
	    File laraFile = mainTab.getLaraFile();
	    explorer.setMainWorkspace(laraFile);
	    removeDuplicatedLaraFiles();
	}
    }

    public void selectTab(SourceTextArea sourceTextArea) {
	int index = getTabIndex(sourceTextArea);
	tabbedPane.setSelectedIndex(index);
    }

    public Explorer getEditor() {
	return explorer;
    }

    public void setExplorer(Explorer explorer) {
	this.explorer = explorer;
    }

    /**
     * TODO: update new tabs
     *
     * @param font
     */
    public void setTabsFont(Float size) {

	// mainTab.getTextArea().setFont(mainTab.getTextArea().getFont().deriveFont(size));
	for (int i = 0; i < tabbedPane.getTabCount(); i++) {
	    SourceTextArea editorTab = getTab(i);
	    Font deriveFont = editorTab.getTextArea().getFont().deriveFont(size);
	    editorTab.getTextArea().setFont(deriveFont);

	    // editorTab.revalidate();
	    // editorTab.repaint();
	}
    }
}
