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

    public static String generateAssignment(AssignmentElement assignment, String id) {
        StringBuilder assignmentCode = new StringBuilder();
        assignmentCode.append("<p>");

        JsDocTag alias = assignment.getComment().getTag(JsDocTagName.ALIAS);
        String namePath = alias.getValue(JsDocTagProperty.NAME_PATH);

        startTag("span", id, assignmentCode);
        assignmentCode.append(namePath);

        Optional<FunctionDeclElement> functionRightHand = assignment.getRightFunctionDecl();

        String functionParameters = functionRightHand
                .map(function -> HtmlGenerators.generateFunctionParams(function.getParameters()))
                .orElse("");
        assignmentCode.append(functionParameters);

        assignmentCode.append("</span>");

        // If function, add inputs
        // if (functionRightHand.isPresent()) {
        // LaraDocComment comment = functionRightHand.get().getComment();
        // assignmentCode.append(generateInputTags(comment));
        // }
        assignmentCode.append("</p>");

        assignmentCode.append("<div class='function_content'>");
        String text = assignment.getComment().getText();
        if (!text.isEmpty()) {
            assignmentCode.append("<p>");
            String htmlText = StringLines.getLines(text).stream().collect(Collectors.joining("<br>"));
            assignmentCode.append(htmlText);
            assignmentCode.append("</p>");
            // assignmentCode.append("<br>");
        }

        assignmentCode.append(generateParameters("Parameters", assignment.getComment().getTags(JsDocTagName.PARAM)));
        // assignmentCode.append(generateParameters("Returns", assignment.getComment().getTags(JsDocTagName.RETURNS)));

        assignmentCode.append("</div>");

        return assignmentCode.toString();

    }

    private static void startTag(String tag, String id, StringBuilder code) {
        if (id != null && !id.isEmpty()) {
            code.append("<" + tag + " id='" + id + "'>");
            return;
        }

        code.append("<" + tag + ">");
    }

    // public static String generateFunctionParams(FunctionDeclElement assignment) {
    public static String generateFunctionParams(List<String> parameters) {
        return parameters.stream().collect(Collectors.joining(", ", "(", ")"));
    }

    public static String generateFunctionParamsFromTags(List<JsDocTag> parameters) {
        return parameters.stream()
                .map(HtmlGenerators::parseParam)
                .collect(Collectors.joining(", ", "(", ")"));
    }

    private static String parseParam(JsDocTag param) {
        String stringParam = param.getValue(JsDocTagProperty.NAME);

        if (param.hasProperty(JsDocTagProperty.DEFAULT_VALUE)) {
            stringParam = stringParam + " = " + param.getValue(JsDocTagProperty.DEFAULT_VALUE);
        }

        if (param.hasProperty(JsDocTagProperty.OPTIONAL)) {
            stringParam = "[" + stringParam + "]";
        }

        return stringParam;
    }

    // public static String generateFunction(FunctionDeclElement functionDecl, String id) {
    public static String generateMember(String id, LaraDocComment laraComment) {
        return generateMember(id, laraComment, true, false);
    }

    public static String generateMember(String id, LaraDocComment laraComment, boolean isFunction,
            boolean isConstructor) {

        StringBuilder functionCode = new StringBuilder();

        functionCode.append("<div class='function_block'>");

        JsDocTag alias = laraComment.getTag(JsDocTagName.ALIAS);
        String namePath = alias.getValue(JsDocTagProperty.NAME_PATH);

        // add large member name
        functionCode.append("<h3 id='");
        functionCode.append(id);
        functionCode.append("'>");
        functionCode.append(namePath);
        functionCode.append("</h3>");

        startTag("pre class='signature'><code class='language-js'", "", functionCode);

        boolean isDeprecated = laraComment.hasTag(JsDocTagName.DEPRECATED);
        if (isDeprecated) {
            functionCode.append("<strike>");
        }

        if (isConstructor) {
            functionCode.append("new ");
        }

        functionCode.append("<strong>");
        functionCode.append(namePath);
        functionCode.append("</strong>");

        // String functionParameters = isFunction ? generateFunctionParams(laraComment.getParameters()) : "";
        String functionParameters = isFunction ? generateFunctionParamsFromTags(laraComment.getTags(JsDocTagName.PARAM))
                : "";

        // List<String> params = laraComment.getParameters();
        // String functionParameters = generateFunctionParams(params);
        functionCode.append(functionParameters);

        if (isDeprecated) {
            functionCode.append("</strike>");
        }
        functionCode.append("</code></pre>");

        // assignmentCode.append(generateInputTags(laraComment));

        functionCode.append("<div class='function_content'>");

        if (isDeprecated) {
            String elementType = laraComment.hasTag(JsDocTagName.CLASS) ? "class" : "function";
            String deprecatedMessage = "This " + elementType + " is deprecated.";
            String content = laraComment.getTag(JsDocTagName.DEPRECATED).getValue(JsDocTagProperty.CONTENT, "");
            if (!content.isEmpty()) {
                deprecatedMessage += " " + content;
            }

            functionCode.append("<p><em><strong>" + deprecatedMessage + "</strong></em></p>");
        }

        String text = laraComment.getText();
        if (!text.isEmpty()) {
            functionCode.append("<p>");

            List<JsDocTag> augmentTags = laraComment.getTags(JsDocTagName.AUGMENTS);
            if (!augmentTags.isEmpty()) {
                String parentClasses = augmentTags.stream().map(tag -> tag.getValue(JsDocTagProperty.NAME_PATH))
                        .collect(Collectors.joining("</em>, <em>", "<em>", "</em>"));
                functionCode.append("<div class='augments'>(Extends " + parentClasses + ")</div>");
                // functionCode.append("(Extends " + parentClasses + ") - ");
            }

            // String htmlText = StringLines.getLines(text).stream().collect(Collectors.joining("<br>"));
            String htmlText = generateTextBlock(text);
            functionCode.append(htmlText);
            functionCode.append("</p>");
            // functionCode.append("<br>");
        }

        functionCode.append(generateParameters("Parameters", laraComment.getTags(JsDocTagName.PARAM)));
        functionCode.append(generateParameters("Returns", laraComment.getTags(JsDocTagName.RETURNS)));

        functionCode.append("</div>");

        // Input parameters

        // List<JsDocTag> inputTags = laraComment.getTags(JsDocTagName.PARAM);
        // functionCode.append(generateParameters("Parameters", inputTags));

        functionCode.append("<hr>");

        functionCode.append("</div>");

        return functionCode.toString();

    }

    public static String generateAspect(String id, LaraDocComment laraComment) {
        StringBuilder aspectCode = new StringBuilder();
        // aspectCode.append("<p>");

        JsDocTag aspectTag = laraComment.getTag(JsDocTagName.ASPECT);
        String aspectName = aspectTag.getValue(JsDocTagProperty.NAME_PATH);

        // Name of the aspect
        aspectCode.append("<h3 id='" + id + "'>").append(aspectName).append("</h3>");

        // Description
        String text = laraComment.getText();
        if (!text.isEmpty()) {
            aspectCode.append("<p>");
            String htmlText = StringLines.getLines(text).stream().collect(Collectors.joining("<br>"));
            aspectCode.append(htmlText);
            aspectCode.append("</p>");
            aspectCode.append("<br>");
        }

        // Input parameters

        List<JsDocTag> inputTags = laraComment.getTags(JsDocTagName.PARAM);
        aspectCode.append(generateParameters("Inputs", inputTags));

        // Output parameters

        List<JsDocTag> outputTags = laraComment.getTags(JsDocTagName.OUTPUT);
        aspectCode.append(generateParameters("Outputs", outputTags));
        aspectCode.append("<hr>");

        return aspectCode.toString();

    }

    private static String generateParameters(String paramsName, List<JsDocTag> params) {

        if (params.isEmpty()) {
            return "";
        }

        StringBuilder code = new StringBuilder();

        code.append("<h4>" + paramsName + "</h4>");

        code.append("<div class='parameters'>");

        for (JsDocTag param : params) {
            String name = param.getValue(JsDocTagProperty.NAME);
            String type = param.getValue(JsDocTagProperty.TYPE_NAME, "");
            String content = param.getValue(JsDocTagProperty.CONTENT, "").trim();

            code.append("<span class='parameter'>");

            if (name != null) {
                code.append("<strong>" + name + "</strong>");
            }

            // code.append("<br> - ").append(name);

            String typeInfo = type.isEmpty() ? "any" : type;

            // Not adding default value here, default value was already added in function signature
            // if (param.hasProperty(JsDocTagProperty.OPTIONAL)) {
            // if (param.hasProperty(JsDocTagProperty.DEFAULT_VALUE)) {
            // typeInfo += " = " + param.getValue(JsDocTagProperty.DEFAULT_VALUE);
            // }
            // }

            code.append("(").append(typeInfo).append(")");

            code.append("</span>");

            // if (param.hasProperty(JsDocTagProperty.OPTIONAL)) {
            // content = "<em>(optional)</em> " + content;
            // // code.append("<em>(optional)</em>");
            // }

            if (!content.isEmpty()) {
                code.append(" ");
                boolean addDash = !content.startsWith("-");
                if (addDash) {
                    code.append("- ");
                }

                code.append(content);
            }

            if (param.hasProperty(JsDocTagProperty.OPTIONAL)) {
                code.append(" <em>(optional)</em>");
            }
            code.append("<br>");
        }

        code.append("</div>");

        return code.toString();
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

    public static String generateTextBlock(String text) {
        return StringLines.getLines(text).stream()
                .collect(Collectors.joining("<br>"));
    }
}
