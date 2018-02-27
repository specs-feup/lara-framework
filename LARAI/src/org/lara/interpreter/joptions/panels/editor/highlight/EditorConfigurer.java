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

package org.lara.interpreter.joptions.panels.editor.highlight;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.text.DefaultEditorKit;

import org.fife.io.UnicodeWriter;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.ParameterizedCompletion.Parameter;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.FileLocation;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.TextEditorPane;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.folding.CurlyFoldParser;
import org.fife.ui.rsyntaxtextarea.folding.FoldParserManager;
import org.lara.interpreter.joptions.panels.editor.TextEditorDemo;
import org.lara.interpreter.joptions.panels.editor.tabbed.SourceTextArea;

import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.swing.GenericActionListener;

public class EditorConfigurer {

    static {
        System.setProperty(UnicodeWriter.PROPERTY_WRITE_UTF8_BOM, "false");
        AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance();
        atmf.putMapping(TextEditorDemo.LARA_STYLE_KEY, LaraTokenMaker.class.getCanonicalName());
        FoldParserManager.get().addFoldParserMapping(TextEditorDemo.LARA_STYLE_KEY, new CurlyFoldParser());

        try {
            theme = Theme.load(SpecsIo.resourceToStream("org/fife/ui/rsyntaxtextarea/themes/eclipse.xml"));

        } catch (IOException e) {
            SpecsLogs.msgWarn("Could not load selected theme, will use default\n", e);
        }
    }

    private static Theme theme;
    private final Color MARK_ALL_COLOR = Color.LIGHT_GRAY;

    public EditorConfigurer() {

    }

    /**
     * Build a text area according to the type of the input file
     *
     * @param inputFile
     * @param sourceTextArea
     * @return
     */
    public TextEditorPane buildTextArea(File inputFile, boolean isNew, SourceTextArea sourceTextArea) {
        TextEditorPane textArea = new TextEditorPane();
        sourceTextArea.setTextArea(textArea);
        // textArea.setFont(textArea.getFont().deriveFont(12f));
        if (theme != null) {
            theme.apply(textArea);
        }
        textArea.setMarkAllHighlightColor(MARK_ALL_COLOR);
        addPopupOptions(textArea);

        // Add folding
        textArea.setCodeFoldingEnabled(true);
        textArea.setAntiAliasingEnabled(true);

        loadFile(sourceTextArea, inputFile);

        return textArea;
    }

    private static void addPopupOptions(TextEditorPane textArea) {
        JPopupMenu popupMenu = textArea.getPopupMenu();
        popupMenu.addSeparator();
        JMenuItem copySyntax = new JMenuItem("Copy with Syntax Higlighting");
        copySyntax.addActionListener(new GenericActionListener(e -> textArea.copyAsRtf()));
        popupMenu.add(copySyntax);
    }

    public static void setLaraTextArea(SourceTextArea sourceTextArea) {
        TextEditorPane textArea = sourceTextArea.getTextArea();
        textArea.setSyntaxEditingStyle(TextEditorDemo.LARA_STYLE_KEY);

        CompletionProvider provider = EditorConfigurer.createCompletionProvider();

        AutoCompletion ac = new AutoCompletion(provider);
        ac.install(textArea);

        textArea.clearParsers();
        EditorParser parser = new EditorParser();
        textArea.addParser(parser);

        // adds a list of aspects to the aspect list pane whenever a parsing is performed
        parser.addListener(sourceTextArea::outlineAstListener);
    }

    /**
     * Create a simple provider that adds some LARA-related completions.
     */
    private static CompletionProvider createCompletionProvider() {

        // This provider has no understanding of language semantics.
        DefaultCompletionProvider provider = new DefaultCompletionProvider();

        // EditorConfigurer.addPrintlnCompletion(provider);
        List<Completion> completions = new ArrayList<>();
        completions.add(new BasicCompletion(provider, "aspectdef"));
        completions.add(new BasicCompletion(provider, "select"));
        completions.add(new BasicCompletion(provider, "apply"));
        completions.add(new BasicCompletion(provider, "condition"));
        completions.add(new BasicCompletion(provider, "end"));

        for (String method : LaraTokenMaker.predefinedMethods) {

            completions.add(new BasicCompletion(provider, method + "("));
        }

        // Add a couple of "shorthand" completions. These completions don't
        // require the input text to be the same thing as the replacement text.
        // completions.add(new ShorthandCompletion(provider, "aspect",
        // "aspectdef \n\nend", "Simple Aspect structure"));

        // completions.add(new TemplateCompletion(provider, "ff", "aff", "for()"));
        // completions.add(new ShorthandCompletion(provider, "aspect",
        // "aspectdef \n\nend", "Simple Aspect structure"));
        // completions.add(new ShorthandCompletion(provider, "select",
        // "select end", "Select structure"));
        // completions.add(new ShorthandCompletion(provider, "apply",
        // "apply end", "Apply structure"));
        // completions.add(new ShorthandCompletion(provider, "condition",
        // "condition end", "Condition structure"));

        provider.addCompletions(completions);

        // LanguageAwareCompletionProvider awareProvider = new LanguageAwareCompletionProvider(provider);
        // return awareProvider;
        return provider;

    }

    static void addPrintlnCompletion(DefaultCompletionProvider provider) {
        FunctionCompletion printlnCompletion = new FunctionCompletion(provider, "println", "void");
        List<Parameter> params = new ArrayList<>();
        Parameter messageParam = new Parameter(null, "message", true);
        messageParam.setDescription("The message to print");
        params.add(messageParam);
        printlnCompletion.setParams(params);
        printlnCompletion.setShortDescription("Print a message");
        printlnCompletion.setSummary("Print a message");
        provider.addCompletion(printlnCompletion);
    }

    public static boolean loadFile(SourceTextArea sourceTextArea, File inputFile) {
        TextEditorPane textArea = sourceTextArea.getTextArea();
        if (inputFile.isDirectory()) {
            SpecsLogs.msgWarn(
                    "Input file cannot be a directory: '" + SpecsIo.getCanonicalPath(inputFile) + "'\n");
            return false;
        }
        FileLocation location = FileLocation.create(inputFile);
        try {
            textArea.load(location, null);
            textArea.setEncoding(StandardCharsets.UTF_8.name());
            textArea.getDocument().putProperty(DefaultEditorKit.EndOfLineStringProperty, System.lineSeparator());

            setSyntaxEditingStyle(sourceTextArea, inputFile);

            return true;
        } catch (IOException e) {
            SpecsLogs.msgWarn("Error message:\n", e);
        }
        return false;
    }

    public static void setSyntaxEditingStyle(SourceTextArea sourceTextArea, File inputFile) {
        TextEditorPane textArea = sourceTextArea.getTextArea();
        // System.out.println(textArea.getDocument());
        String extension = SpecsIo.getExtension(inputFile);
        if (extension.isEmpty()) {
            textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
        } else if (extension.equals("lara")) {
            setLaraTextArea(sourceTextArea);
        } else if (extension.equals("js")) {
            textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
        } else if ((extension.equals("h"))) {
            textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_C);
        } else if ((extension.equals("hpp"))) {
            textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS);
        } else {
            textArea.setSyntaxEditingStyle("text/" + extension);
        }

    }

    /*    private static void addToolBar(JPanel cp) {
    // Create a toolbar with searching options.
    JToolBar toolBar = new JToolBar();
    searchField = new JTextField(30);
    toolBar.add(searchField);
    final JButton nextButton = new JButton("Find Next");
    nextButton.setActionCommand("FindNext");
    nextButton.addActionListener(this);
    toolBar.add(nextButton);
    searchField.addActionListener(evt -> nextButton.doClick(0));

    JButton prevButton = new JButton(
    	"Find Previous");
    prevButton.setActionCommand("FindPrev");
    prevButton.addActionListener(this);
    toolBar.add(prevButton);
    regexCB = new JCheckBox("Regex");
    toolBar.add(regexCB);
    matchCaseCB = new JCheckBox("Match Case");
    toolBar.add(matchCaseCB);
    cp.add(toolBar, BorderLayout.SOUTH);

    }

        @Override
    public void actionPerformed(ActionEvent e) {

    // "FindNext" => search forward, "FindPrev" => search backward
    String command = e.getActionCommand();
    boolean forward = "FindNext".equals(command);

    // Create an object defining our search parameters.
    SearchContext context = new SearchContext();
    String text = searchField.getText();
    if (text.length() == 0) {
        return;
    }
    context.setSearchFor(text);
    context.setMatchCase(matchCaseCB.isSelected());
    context.setRegularExpression(regexCB.isSelected());
    context.setSearchForward(forward);
    context.setWholeWord(false);

    SearchResult find = SearchEngine.find(textArea, context);
    boolean found = find.wasFound();
    if (!found) {
        JOptionPane.showMessageDialog(this, "Text not found");
    }

    }
    *
    *
    */
}
