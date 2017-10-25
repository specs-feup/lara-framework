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

import java.io.BufferedReader;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.swing.text.Element;

import org.dojo.jsl.parser.ast.ASTAspectDef;
import org.dojo.jsl.parser.ast.ASTStart;
import org.dojo.jsl.parser.ast.ParseException;
import org.dojo.jsl.parser.ast.Token;
import org.dojo.jsl.parser.ast.TokenMgrError;
import org.fife.io.DocumentReader;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.parser.AbstractParser;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParseResult;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParserNotice;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;

import larac.LaraC;
import larac.exceptions.SyntaxException;
import pt.up.fe.specs.util.SpecsCollections;

public class EditorParser extends AbstractParser {

    private static final int MAX_NUM_TOKENS = 6;
    private ASTStart javaCCParser;
    private List<Consumer<ASTStart>> listeners;

    public EditorParser() {
        listeners = SpecsCollections.newArrayList();
    }

    public Optional<ASTStart> getParserResult() {
        return Optional.ofNullable(javaCCParser);
    }

    public List<ASTAspectDef> getAspectList() {

        if (javaCCParser == null) {
            return Collections.emptyList();
        }

        return javaCCParser.getDescendantsOfType(ASTAspectDef.class);
    }

    /**
     * Adds listener to be executed when the code is parsed successfully
     */
    public void addListener(Consumer<ASTStart> ast) {
        listeners.add(ast);
    }

    @Override
    public ParseResult parse(RSyntaxDocument doc, String style) {
        // lines = new ArrayList<>();
        DefaultParseResult result = new DefaultParseResult(this);

        Element root = doc.getDefaultRootElement();
        result.setParsedLines(0, root.getElementCount() - 1);

        if (doc.getLength() == 0) {
            return result;
        }

        try {
            DocumentReader docReader = new DocumentReader(doc);
            try (BufferedReader br = new BufferedReader(docReader)) {
                javaCCParser = LaraC.javaCCParser(br);

                // List<ASTAspectDef> descendantsOfType = javaCCParser.getDescendantsOfType(ASTAspectDef.class);
                // Handler handler = new Handler(doc);
                // CInputSource input = new InputSource(r);
                // sp.parse(input, handler);
                // r.close();
            }
        } catch (SyntaxException e) {
            if (e.getExceptions().isEmpty()) {
                addException(result, e);
            } else {
                for (Throwable exception : e.getExceptions()) {

                    addException(result, exception);
                }
            }

        }
        // catch (Error e) {
        // addException(result, e);
        // }
        catch (Throwable e) {

            addException(result, e);
        }

        if (javaCCParser != null) {
            listeners.forEach(l -> l.accept(javaCCParser));
        }

        return result;
    }

    // private List<Integer> lines;

    private void addException(DefaultParseResult result, Throwable exception) {

        if (exception instanceof ParseException) {
            ParseException parseException = (ParseException) exception;
            Token token = parseException.getNextToken();
            int line = 0;
            if (token != null) {

                line = token.beginLine - 1;
            }
            // if (lines.contains(line)) {
            // return;
            // }
            // int col = token.beginColumn - 1;
            // int colEnd = token.endColumn + 1;
            // colEnd = colEnd < 1 ? 1 : colEnd;
            // String errorMessage = parseException.getErrorMessage();
            // errorMessage = StringUtils.firstCharToUpper(errorMessage);

            String errorMessage = parseException.getErrorMessage();
            if (errorMessage != null) {
                errorMessage = errorMessage.substring(0, errorMessage.indexOf(':') + 1);
            }
            List<String> expectedTokenSet = parseException.expectedTokenSet;
            if (expectedTokenSet != null) {
                if (expectedTokenSet.size() > MAX_NUM_TOKENS) {
                    expectedTokenSet = expectedTokenSet.subList(0, MAX_NUM_TOKENS);
                }
                errorMessage += expectedTokenSet.stream().map(s -> s.replace("\"", "'"))
                        .collect(Collectors.joining("\n", "\n", ""));
            }
            result.addNotice(new DefaultParserNotice(this, errorMessage, line
            // , col,colEnd
            ));
            // lines.add(line);
        } else if (exception instanceof TokenMgrError) {

            TokenMgrError tokenError = (TokenMgrError) exception;
            int line = tokenError.getErrorLine() - 1;
            String errorMessage = tokenError.getLexicalError();
            result.addNotice(new DefaultParserNotice(this, errorMessage, line));
        } else {
            System.out.println("Unhandled exception type: " + exception);
            result.addNotice(new DefaultParserNotice(this,
                    "Error when parsing: " + exception.getMessage(), 0, -1, -1));
        }
    }

}
