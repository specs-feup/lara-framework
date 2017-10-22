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

package pt.up.fe.specs.lara.doc.jsdocgen.basichtml;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import pt.up.fe.specs.lara.doc.aspectir.elements.AssignmentElement;
import pt.up.fe.specs.lara.doc.aspectir.elements.FunctionDeclElement;
import pt.up.fe.specs.lara.doc.comments.LaraDocComment;
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTag;
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTagName;
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTagProperty;
import pt.up.fe.specs.util.utilities.StringLines;

public class HtmlGenerators {

    public static String generate(AssignmentElement assignment, String id) {
        StringBuilder assignmentCode = new StringBuilder();
        assignmentCode.append("<p>");

        JsDocTag alias = assignment.getComment().getTag(JsDocTagName.ALIAS);
        String namePath = alias.getValue(JsDocTagProperty.NAME_PATH);

        startEmTag(id, assignmentCode);
        assignmentCode.append(namePath);

        Optional<FunctionDeclElement> functionRightHand = assignment.getRightFunctionDecl();

        String functionParameters = functionRightHand.map(HtmlGenerators::generateFunctionParams).orElse("");
        assignmentCode.append(functionParameters);

        assignmentCode.append("</em>");

        // If function, add inputs
        if (functionRightHand.isPresent()) {
            LaraDocComment comment = functionRightHand.get().getComment();
            assignmentCode.append(generateInputTags(comment));
            /*
            List<JsDocTag> params = comment.getTags(JsDocTagName.PARAM);
            for (JsDocTag param : params) {
                String name = param.getValue(JsDocTagProperty.NAME);
                String type = param.getValue(JsDocTagProperty.TYPE_NAME, "");
                String content = param.getValue(JsDocTagProperty.CONTENT, "");
            
                assignmentCode.append("<br> - ").append(name);
                if (!type.isEmpty()) {
                    assignmentCode.append(" [<strong>" + type + "</strong>] ");
                }
            
                if (!content.isEmpty()) {
                    assignmentCode.append(" : " + content);
                }
            }
            */
        }

        assignmentCode.append("</p>");

        String text = assignment.getComment().getText();
        if (!text.isEmpty()) {
            assignmentCode.append("<p>");
            String htmlText = StringLines.getLines(text).stream().collect(Collectors.joining("<br>"));
            assignmentCode.append(htmlText);
            assignmentCode.append("</p>");
            assignmentCode.append("<br>");
        }

        return assignmentCode.toString();

    }

    private static void startEmTag(String id, StringBuilder code) {
        if (id != null && !id.isEmpty()) {
            code.append("<em id='" + id + "'>");
            return;
        }

        code.append("<em>");
    }

    public static String generateFunctionParams(FunctionDeclElement assignment) {
        return assignment.getParameters().stream().collect(Collectors.joining(", ", "(", ")"));
    }

    public static String generate(FunctionDeclElement functionDecl, String id) {
        StringBuilder assignmentCode = new StringBuilder();
        assignmentCode.append("<p>");

        JsDocTag alias = functionDecl.getComment().getTag(JsDocTagName.ALIAS);
        String namePath = alias.getValue(JsDocTagProperty.NAME_PATH);
        startEmTag(id, assignmentCode);
        // assignmentCode.append("<em>" + namePath);
        assignmentCode.append(namePath);

        String functionParameters = generateFunctionParams(functionDecl);
        assignmentCode.append(functionParameters);

        assignmentCode.append("</em>");

        assignmentCode.append(generateInputTags(functionDecl.getComment()));

        assignmentCode.append("</p>");

        String text = functionDecl.getComment().getText();
        if (!text.isEmpty()) {
            assignmentCode.append("<p>");
            String htmlText = StringLines.getLines(text).stream().collect(Collectors.joining("<br>"));
            assignmentCode.append(htmlText);
            assignmentCode.append("</p>");
            assignmentCode.append("<br>");
        }

        return assignmentCode.toString();

    }

    public static String generateInputTags(LaraDocComment comment) {
        StringBuilder code = new StringBuilder();

        List<JsDocTag> params = comment.getTags(JsDocTagName.PARAM);
        for (JsDocTag param : params) {
            String name = param.getValue(JsDocTagProperty.NAME);
            String type = param.getValue(JsDocTagProperty.TYPE_NAME, "");
            String content = param.getValue(JsDocTagProperty.CONTENT, "");

            // code.append("<br> - ").append(name);

            String typeInfo = "";
            if (!type.isEmpty()) {
                typeInfo += " [<strong>" + type + "</strong>] ";
            }

            if (!content.isEmpty()) {
                typeInfo += " : " + content;
            }

            if (!typeInfo.isEmpty()) {
                code.append("<br> - ").append(name).append(typeInfo);
            }
        }

        return code.toString();
    }
}
