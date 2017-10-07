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

package org.lara.interpreter.joptions.panels.editor.tabbed;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.Element;

import org.fife.ui.rsyntaxtextarea.ErrorStrip;
import org.fife.ui.rsyntaxtextarea.FileLocation;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.TextEditorPane;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.lara.interpreter.joptions.panels.editor.EditorPanel;
import org.lara.interpreter.joptions.panels.editor.components.DontAskMeAgainPanel;
import org.lara.interpreter.joptions.panels.editor.components.FileNotExistPane;
import org.lara.interpreter.joptions.panels.editor.components.ReloadPane;
import org.lara.interpreter.joptions.panels.editor.highlight.EditorConfigurer;
import org.lara.interpreter.joptions.panels.editor.listeners.EditorListener;
import org.lara.interpreter.joptions.panels.editor.listeners.FocusGainedListener;
import org.lara.interpreter.joptions.panels.editor.utils.Factory;

import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;

public class SourceTextArea extends JPanel {

    /**
     * 
     */
    private static final int NO_VALUE = -10;
    private static final long serialVersionUID = 1L;
    private final TextEditorPane textArea;
    private File sourceFile;
    private long lastModified;
    private String originalText;
    private final TabsContainerPanel tabsContainer;
    private final EditorConfigurer editorConfigurer;
    private boolean changed;
    private boolean isNew;
    private final ReloadPane reloadPane;
    private final FileNotExistPane fileNotExistsPane;
    Consumer<Integer> saveAskSaveMethod;
    private boolean asked;

    public SourceTextArea(TabsContainerPanel parent) {
        this(Factory.newFile(parent), parent, true);
    }

    public SourceTextArea(File file, TabsContainerPanel parent) {
        this(file, parent, false);

    }

    private SourceTextArea(File file, TabsContainerPanel parent, boolean isNew) {
        super(new BorderLayout());
        this.isNew = isNew;
        changed = false;
        tabsContainer = parent;
        sourceFile = file;
        saveAskSaveMethod = getEditorPanel().getSettings()::saveAskSave;
        updateLastModified();
        editorConfigurer = new EditorConfigurer();
        textArea = editorConfigurer.buildTextArea(file, isNew);

        EditorListener.newInstance(this);

        RTextScrollPane pane = Factory.standardScrollPane(textArea);

        ErrorStrip es = new ErrorStrip(textArea);
        JPanel temp = new JPanel(new BorderLayout());
        temp.add(pane);
        temp.add(es, BorderLayout.LINE_END);
        add(temp, BorderLayout.CENTER);
        reloadPane = new ReloadPane(this);
        fileNotExistsPane = new FileNotExistPane(this);
        // add(reloadPane, BorderLayout.NORTH);
        originalText = textArea.getText();
        addFocusListener(new FocusGainedListener(e -> {

            enterTab();
        }));

        //
        // System.out.println(Arrays.asList(textArea.getInputMap().allKeys()).stream()
        // .map(k -> k.toString() + "->" + textArea.getInputMap().get(k))
        // .collect(Collectors.joining("\n")));

        // textArea.getInputMap().put(StrokesAndActions.CTRL_SHIFT_C, RSyntaxTextAreaEditorKit.rstaToggleCommentAction);
    }

    private EditorPanel getEditorPanel() {
        return tabsContainer.getEditor().getEditorPanel();
    }

    public TextEditorPane getTextArea() {
        return textArea;
    }

    /**
     * Save the content of the textArea in a file.
     * <p>
     * Before saving verifies if the content is the same as the old content of the file
     * 
     * @return
     */
    public boolean save() {

        if (!(getLaraFile().exists())) {
            int result = askToSaveIfNonExistant();
            switch (result) {
            case JOptionPane.CANCEL_OPTION:
            case JOptionPane.CLOSED_OPTION:
                return false;
            case JOptionPane.NO_OPTION:
                return true;
            default:
                break;
            }
        }
        if (isNew) {
            return saveAs();
        }

        textArea.setEncoding(StandardCharsets.UTF_8.name());
        return forceSave();

    }

    /**
     * Save the content of the textArea in a file, regardless the old content.
     * 
     * @param savingText
     * @return
     */
    private boolean forceSave() {

        try {
            textArea.save();
            updateLastModified();
            setChanged(false);
            setAsked(false);
            updateOldText();
            closeReloadPane();
            return true;
        } catch (IOException e) {
            SpecsLogs.msgWarn("Error message:\n", e);
            JOptionPane.showMessageDialog(this, e.getMessage(), "Save Exception", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    private void updateOldText() {
        originalText = textArea.getText();
        setTabText();
    }

    public boolean saveAs() {

        JFileChooser fc = Factory.newFileChooser("Save as...", "Save", getTabbedParent().getLastOpenedFolder());
        fc.setApproveButtonMnemonic('s');

        int returnVal = fc.showOpenDialog(this);
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return false;
        }

        File file = fc.getSelectedFile();
        return saveAs(file);
    }

    protected boolean saveAs(File file) {
        try {
            textArea.setEncoding(StandardCharsets.UTF_8.name());

            textArea.saveAs(FileLocation.create(file));
            isNew = false;
            setSourceFile(file);
            updateOldText();
            EditorConfigurer.setSyntaxEditingStyle(textArea, file);
            getTabbedParent().setLastOpenedFolder(file.getParentFile());
            tabsContainer.updateOpenedFiles();
            return true;
        } catch (IOException e) {
            SpecsLogs.msgWarn("Error message:\n", e);
        }
        return false;
    }

    public String getTabName() {
        return sourceFile.getName();
    }

    public void exitTab() {

    }

    public void enterTab() {
        textArea.requestFocus();
        refresh();
    }

    public File getLaraFile() {
        return sourceFile;
    }

    public void setSourceFile(File sourceFile) {
        this.sourceFile = sourceFile;
        updateLastModified();
        setTabText();
        tabsContainer.setTabToolTipText(this, SpecsIo.getCanonicalPath(sourceFile));
    }

    /*
    public boolean open() {
    
    	JFileChooser fc = Factory.newFileChooser(getTabbedParent().getLastOpennedFolder());
    
    	int returnVal = fc.showOpenDialog(this);
    
    	if (returnVal == JFileChooser.APPROVE_OPTION) {
    	    File file = fc.getSelectedFile();
    	    int tabIndex = tabsContainer.getTabIndex(file);
    
    	    if (tabIndex > -1) {// If the file already exists
    		// change the focus to the corresponding tab
    		tabsContainer.getTabbedPane().setSelectedIndex(tabIndex);
    	    } else {
    
    		load(file);
    		getTabbedParent().setLastOpennedFolder(file.getParentFile());
    	    }
    	    return true;
    	}
    	return false;
    
    }
    */
    public void refresh() {

        if (isNew || asked) {
            return;
        }
        // System.out.println("refresh?" + lastModified + " vs " + sourceFile.lastModified());
        // if local timestamp is different from the file timestamp
        if (!getLaraFile().exists()) {
            openFileNotExistsPane();
            return;
        }
        if (lastModified != sourceFile.lastModified()) {
            // System.out.println("update!");
            openReloadPane();
        }

    }

    public void openReloadPane() {
        add(reloadPane, BorderLayout.NORTH);
        revalidate();
        repaint();
        // reloadPane.ativate(true);
        // revalidate(); //looks like it is this parent's that has to be revalidated!
        // getParent().
        // validate();
    }

    public void openFileNotExistsPane() {
        add(fileNotExistsPane, BorderLayout.NORTH);
        revalidate();
        repaint();
        // reloadPane.ativate(true);
        // revalidate(); //looks like it is this parent's that has to be revalidated!
        // getParent().
        // validate();
    }

    public void closeReloadPane() {
        remove(reloadPane);
        remove(fileNotExistsPane);
        revalidate();
        repaint();
        // reloadPane.ativate(false);
        // getParent().
        // validate();
    }

    public int askToSaveIfNonExistant() {
        int choice = JOptionPane.showConfirmDialog(this,
                sourceFile.getAbsolutePath()
                        + "\n\nThis file does not exist or was removed. Do you wan to create?",
                // + "\nTimeStamps: editor=" + lastModified + ", file=" + sourceFile.lastModified()
                "File does not exist",
                JOptionPane.YES_NO_OPTION);
        return choice;
    }

    /**
     * Reload the texarea with the file content
     */
    public void reload() {
        try {
            int lastCaretPosition = textArea.getCaretPosition();
            setChanged(true);
            textArea.reload();
            setChanged(false);
            if (lastCaretPosition < textArea.getText().length()) {
                textArea.setCaretPosition(lastCaretPosition);
            }
            originalText = textArea.getText();
            updateLastModified();
            // tabbedParent.setTabTitle(this);
        } catch (IOException e) {
            SpecsLogs.msgWarn("Could not reload file:\n", e);
        }
    }

    // private void showTestDialog() {
    // new ReloadDialog(textArea);
    // }

    protected void load(File file) {
        boolean loaded = EditorConfigurer.loadFile(textArea, file);

        if (loaded) {
            setSourceFile(file);
            isNew = false;
            updateOldText();
            updateLastModified();
            closeReloadPane();
            setChanged(false);
        }
    }

    private void setTabText() {
        // if (changed) {
        //
        // } else {
        tabsContainer.setTabTitle(this);
        // }
    }

    /**
     * Closes this document. If the files is 'dirty', then asks the user if he wants to save.
     * 
     * @return false if during save request the user cancels the process; true otherwise
     */
    public boolean close() {

        boolean closeTab = saveBeforeClose();

        if (closeTab) {
            tabsContainer.closeTab(this);
        }
        return closeTab;
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public TabsContainerPanel getTabbedParent() {
        return tabsContainer;
    }

    @Override
    public String toString() {
        return "Lara Tab with file: " + sourceFile.getName();
    }

    // boolean correctName = true;

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public void updateLastModified() {
        setLastModified(sourceFile.lastModified());
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public boolean isNew() {
        return isNew;
    }

    public boolean saveBeforeClose() {

        if (!getLaraFile().exists() || isNew) {
            return save();
        }

        if (!originalText.equals(textArea.getText())) {
            if (!tabsContainer.getCurrentTab().equals(this)) {
                // System.out.println("Requesting focus!");
                tabsContainer.selectTab(this);
                // textArea.requestFocus();
            }

            int choice = JOptionPane.showConfirmDialog(this, "Save file " + sourceFile.getName() + "?", "Save",
                    JOptionPane.YES_NO_CANCEL_OPTION);
            switch (choice) {
            case JOptionPane.CANCEL_OPTION:
                return false;
            case JOptionPane.NO_OPTION:
                return true;
            case JOptionPane.YES_OPTION:
                return save();
            default:
                break;
            }
        }
        return true;
    }

    // RSTA.ToggleCommentAction
    public void commentSelection(ActionEvent event) {

        if ((!textArea.isEditable()) || (!textArea.isEnabled())) {
            UIManager.getLookAndFeel().provideErrorFeedback(textArea);
            return;
        }

        RSyntaxDocument localRSyntaxDocument = (RSyntaxDocument) textArea.getDocument();
        String[] arrayOfString = localRSyntaxDocument.getLineCommentStartAndEnd(0);
        if (arrayOfString == null) {
            UIManager.getLookAndFeel().provideErrorFeedback(textArea);
            return;
        }
        Element localElement1 = localRSyntaxDocument.getDefaultRootElement();
        Caret localCaret = textArea.getCaret();
        int i = localCaret.getDot();
        int j = localCaret.getMark();
        int k = localElement1.getElementIndex(i);
        int m = localElement1.getElementIndex(j);
        int n = Math.min(k, m);
        int i1 = Math.max(k, m);
        if (n != i1) {
            Element localElement2 = localElement1.getElement(i1);
            if (Math.max(i, j) == localElement2.getStartOffset()) {
                i1--;
            }
        }
        textArea.beginAtomicEdit();
        try {
            boolean bool = getDoAdd(localRSyntaxDocument, localElement1, n, i1, arrayOfString);
            for (k = n; k <= i1; k++) {
                Element localElement3 = localElement1.getElement(k);
                handleToggleComment(localElement3, localRSyntaxDocument, arrayOfString, bool);
            }
        } catch (BadLocationException localBadLocationException) {
            localBadLocationException.printStackTrace();
            UIManager.getLookAndFeel().provideErrorFeedback(textArea);
        } finally {
            textArea.endAtomicEdit();
        }
    }

    private static boolean getDoAdd(Document paramDocument, Element paramElement, int paramInt1, int paramInt2,
            String[] paramArrayOfString)
            throws BadLocationException {
        boolean bool = false;
        for (int i = paramInt1; i <= paramInt2; i++) {
            Element localElement = paramElement.getElement(i);
            int j = localElement.getStartOffset();
            String str = paramDocument.getText(j, localElement.getEndOffset() - j - 1);
            if ((!str.startsWith(paramArrayOfString[0]))
                    || ((paramArrayOfString[1] != null) && (!str.endsWith(paramArrayOfString[1])))) {
                bool = true;
                break;
            }
        }
        return bool;
    }

    private static void handleToggleComment(Element paramElement, Document paramDocument, String[] paramArrayOfString,
            boolean paramBoolean)
            throws BadLocationException {
        int i = paramElement.getStartOffset();
        int j = paramElement.getEndOffset() - 1;
        if (paramBoolean) {
            paramDocument.insertString(i, paramArrayOfString[0], null);
            if (paramArrayOfString[1] != null) {
                paramDocument.insertString(j + paramArrayOfString[0].length(), paramArrayOfString[1], null);
            }
        } else {
            paramDocument.remove(i, paramArrayOfString[0].length());
            if (paramArrayOfString[1] != null) {
                int k = paramArrayOfString[1].length();
                paramDocument.remove(j - paramArrayOfString[0].length() - k, k);
            }
        }
    }

    /**
     * If the file is "dirty", ask user if he wants to save
     */
    public boolean askSave() {
        if (!originalText.equals(textArea.getText())) {
            if (!tabsContainer.getCurrentTab().equals(this)) {
                // System.out.println("Requesting focus!");
                tabsContainer.selectTab(this);
                // textArea.requestFocus();
            }

            // int choice = JOptionPane.showConfirmDialog(this, "Save file " + sourceFile.getName() + "?", "Save",
            // JOptionPane.YES_NO_CANCEL_OPTION);

            int choice = getEditorPanel().getSettings().loadAskSave(NO_VALUE);
            if (choice == NO_VALUE) {
                choice = DontAskMeAgainPanel.showConfirmDialog(this, "Save file " + sourceFile.getName() + "?",
                        "Save", JOptionPane.YES_NO_CANCEL_OPTION, saveAskSaveMethod);
            }
            switch (choice) {
            case JOptionPane.CANCEL_OPTION:
                return false;
            case JOptionPane.NO_OPTION:
                return true;
            case JOptionPane.YES_OPTION:
                return save();
            default:
                break;
            }
        }
        return true;
    }

    public void setAsked(boolean b) {
        asked = true;
    }
}
