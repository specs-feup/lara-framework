package pt.up.fe.specs.lara.doc.jsdocgen;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import pt.up.fe.specs.lara.doc.aspectir.AspectIrDoc;
import pt.up.fe.specs.lara.doc.aspectir.elements.AssignmentElement;
import pt.up.fe.specs.lara.doc.aspectir.elements.FunctionDeclElement;
import pt.up.fe.specs.lara.doc.aspectir.elements.VarDeclElement;
import pt.up.fe.specs.lara.doc.comments.LaraDocComment;
import pt.up.fe.specs.lara.doc.data.LaraDocModule;
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTag;
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTagName;
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTagProperty;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.utilities.StringLines;

public class BasicHtmlGenerator implements JsDocGenerator {

    @Override
    public Optional<File> generate(LaraDocModule module, File outputFolder) {
        StringBuilder htmlCode = new StringBuilder();

        if (!module.getDocumentation().isPresent()) {
            return Optional.empty();
        }

        AspectIrDoc doc = module.getDocumentation().get();

        for (VarDeclElement varDecl : doc.getTopLevelElements(VarDeclElement.class)) {
            htmlCode.append("<h1>" + varDecl.getVarDeclName() + "</h1>");
            LaraDocComment comment = varDecl.getComment();

            if (!comment.getText().isEmpty()) {
                String text = StringLines.getLines(comment.getText()).stream().collect(Collectors.joining("<br>"));
                htmlCode.append("<p>" + text + "</p>");
            }

            // Static members
            List<AssignmentElement> staticMembers = varDecl.getStaticElements();
            if (!staticMembers.isEmpty()) {
                htmlCode.append("<h2>Static Members</h2>");

                for (AssignmentElement staticMember : staticMembers) {
                    htmlCode.append(generate(staticMember));
                }
            }

            // Instance members
            List<AssignmentElement> instanceMembers = varDecl.getInstanceElements();
            if (!instanceMembers.isEmpty()) {
                htmlCode.append("<h2>Instance Members</h2>");

                for (AssignmentElement instanceMember : instanceMembers) {
                    htmlCode.append(generate(instanceMember));
                }
            }

        }

        // Global functions
        List<FunctionDeclElement> functionDecls = doc.getTopLevelElements(FunctionDeclElement.class);
        if (!functionDecls.isEmpty()) {
            htmlCode.append("<h2>Global Functions</h2>");
            for (FunctionDeclElement functionDecl : functionDecls) {
                htmlCode.append(generate(functionDecl));
            }
        }

        // Ad Global assignments?

        // htmlCode.append("<em>Hello!</em> Elements -> " + doc);

        File moduleHtml = new File(outputFolder, "module.html");
        SpecsIo.write(moduleHtml, htmlCode.toString());

        return Optional.of(moduleHtml);
    }

    private String generate(FunctionDeclElement functionDecl) {
        StringBuilder assignmentCode = new StringBuilder();
        assignmentCode.append("<p>");

        JsDocTag alias = functionDecl.getComment().getTag(JsDocTagName.ALIAS);
        String namePath = alias.getValue(JsDocTagProperty.NAME_PATH);
        assignmentCode.append("<em>" + namePath);

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

    private String generate(AssignmentElement assignment) {
        StringBuilder assignmentCode = new StringBuilder();
        assignmentCode.append("<p>");

        JsDocTag alias = assignment.getComment().getTag(JsDocTagName.ALIAS);
        String namePath = alias.getValue(JsDocTagProperty.NAME_PATH);
        assignmentCode.append("<em>" + namePath);

        Optional<FunctionDeclElement> functionRightHand = assignment.getRightFunctionDecl();

        String functionParameters = functionRightHand.map(this::generateFunctionParams).orElse("");
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

    private String generateFunctionParams(FunctionDeclElement assignment) {
        return assignment.getParameters().stream().collect(Collectors.joining(", ", "(", ")"));
    }

    private String generateInputTags(LaraDocComment comment) {
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
