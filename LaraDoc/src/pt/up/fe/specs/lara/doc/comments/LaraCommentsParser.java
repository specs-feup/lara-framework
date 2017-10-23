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

package pt.up.fe.specs.lara.doc.comments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import pt.up.fe.specs.lara.doc.jsdoc.JsDocTag;
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTagParser;
import pt.up.fe.specs.util.SpecsCollections;
import pt.up.fe.specs.util.parsing.CommentParser;
import pt.up.fe.specs.util.parsing.comments.TextElement;
import pt.up.fe.specs.util.parsing.comments.TextElementType;
import pt.up.fe.specs.util.utilities.StringLines;

/**
 * General behaviour: parsing is done line-by-line. If a tag is found at the beginning of a line, the text following it,
 * as well as next lines are part of the text of the tag. If only stops after if finds another tag in the beginning of
 * another line.
 * 
 * @author JoaoBispo
 *
 */
public class LaraCommentsParser {

    private static final Set<Character> CHARS_TO_CLEAN = new HashSet<>(Arrays.asList('*', '/'));

    private final JsDocTagParser tagParser;

    public LaraCommentsParser() {
        this.tagParser = new JsDocTagParser();
    }

    public LaraDocComment parse(String comment) {

        // Obtain text elements
        List<TextElement> textElements = new CommentParser().parse(comment);

        // Keep only the last multi-line comment
        Optional<TextElement> multiLineComment = SpecsCollections.reverseStream(textElements)
                .filter(element -> element.getType() == TextElementType.MULTILINE_COMMENT)
                .findFirst();

        // String cleanedComment = cleanComment(comment);
        String cleanedComment = multiLineComment.map(TextElement::getText)
                .map(this::cleanComment)
                .orElse("");

        // Split into comment text and list of JsDoc tags
        List<String> commentLines = StringLines.getLines(cleanedComment);
        Collections.reverse(commentLines);
        String docText = extractElementText(commentLines).trim();
        List<String> tagsText = extractTagsText(commentLines);

        Preconditions.checkArgument(commentLines.isEmpty(), "Expected comment lines to be empty");
        /*
        if (!cleanedComment.isEmpty()) {
            System.out.println("COMMENT BEFORE:\n" + comment + "\n----");
            // System.out.println("COMMENT AFTER:\n" + cleanedComment + "\n----");
            System.out.println("COMMENT TEXT:\n" + docText + "\n----");
            System.out.println("COMMENT TAGS:\n" + tagsText + "\n----");
        }
        */
        // Parse tags
        List<JsDocTag> tags = SpecsCollections.map(tagsText, tagParser::parse);
        if (!tags.isEmpty()) {
            // System.out.println("TAGS:" + tags);
        }

        return new LaraDocComment(docText, tags);
        /*
        // Split into lines
        List<String> lines = new ArrayList<>();
        for (String line : StringLines.getLines(comment)) {
            // Clean line
            String cleanedLine = cleanLine(line);
        }
        */
    }

    private List<String> extractTagsText(List<String> commentLines) {
        List<String> tags = new ArrayList<>();
        List<String> currentTag = new ArrayList<>();

        for (int i = commentLines.size() - 1; i >= 0; i--) {
            String line = commentLines.get(i).trim();
            commentLines.remove(i);

            if (line.isEmpty()) {
                continue;
            }

            // Check if beginning of a new tag
            if (currentTag.isEmpty()) {
                Preconditions.checkArgument(line.startsWith("@"), "Expected line to start with @:" + line);
                currentTag.add(line);

                continue;
            }

            // If line start with tag, save current tag and start another one
            if (line.startsWith("@")) {
                tags.add(currentTag.stream().collect(Collectors.joining(" ")));
                currentTag = new ArrayList<>();
            }

            currentTag.add(line);
        }

        // Collect last tag
        if (!currentTag.isEmpty()) {
            tags.add(currentTag.stream().collect(Collectors.joining(" ")));
        }

        return tags;

    }

    /**
     * 
     * @param commentLines
     *            reversed-list
     * @return
     */
    private String extractElementText(List<String> commentLines) {
        List<String> elementText = new ArrayList<>();

        // Add lines until a '@' is found
        for (int i = commentLines.size() - 1; i >= 0; i--) {
            String line = commentLines.get(i);
            if (line.startsWith("@")) {
                break;
            }

            elementText.add(line);
            commentLines.remove(i);
        }

        return elementText.stream().collect(Collectors.joining("\n"));
    }

    private String cleanComment(String comment) {
        return StringLines.getLines(comment).stream()
                .map(this::cleanLine).collect(Collectors.joining("\n"));
    }

    private String cleanCommentOld(String comment) {
        String currentComment = comment.trim();

        // Remove lines empty lines and lines that start with '//'
        // LARAC currently does not associate just the last comment with the element,
        // but all comments
        // System.out.println("BEFORE:" + currentComment);
        currentComment = simplifyComment(currentComment);
        // System.out.println("AFTER:" + currentComment);

        if (currentComment.isEmpty()) {
            return "";
        }

        // Check if single line comment
        if (currentComment.startsWith("//")) {
            Preconditions.checkArgument(true, "For now, it should not land here.");
            List<String> lines = StringLines.getLines(currentComment);

            if (lines.size() == 1) {
                return cleanLine(currentComment.substring("//".length()));
            }

            return lines.stream().map(this::cleanComment).collect(Collectors.joining("\n"));
            /*
            Preconditions.checkArgument(!currentComment.contains("\n"),
                    "Did not expect multiple lines in single comment:\n" + currentComment);
            
            return cleanLine(currentComment.substring("//".length()));
            */
        }

        if (currentComment.startsWith("/*")) {
            Preconditions.checkArgument(currentComment.endsWith("*/"),
                    "Expected comment to end with */:\n" + currentComment);

            currentComment = currentComment.substring("/*".length());
            currentComment = currentComment.substring(0, currentComment.length() - "*/".length());

            return StringLines.getLines(currentComment).stream()
                    .map(this::cleanLine).collect(Collectors.joining("\n"));

        }

        throw new RuntimeException("Comment type not supported:\n" + currentComment);
    }

    private String simplifyComment(String currentComment) {
        return StringLines.getLines(currentComment).stream()
                .filter(line -> !line.trim().startsWith("//") && !line.trim().isEmpty())
                .collect(Collectors.joining("\n"));
    }

    private String cleanLine(String line) {
        String currentLine = line;
        while (true) {
            currentLine = currentLine.trim();

            // Stop if empty
            if (currentLine.isEmpty()) {
                return currentLine;
            }

            boolean cleanBegin = CHARS_TO_CLEAN.contains(currentLine.charAt(0));
            boolean cleanEnd = currentLine.length() > 1
                    ? CHARS_TO_CLEAN.contains(currentLine.charAt(currentLine.length() - 1))
                    : false;

            // Stop if there is no cleaning to do
            if (!cleanBegin && !cleanEnd) {
                return currentLine;
            }

            // Clean
            if (cleanBegin) {
                currentLine = currentLine.substring(1);
            }
            if (cleanEnd) {
                currentLine = currentLine.substring(0, currentLine.length() - 1);
            }

        }

    }

}
