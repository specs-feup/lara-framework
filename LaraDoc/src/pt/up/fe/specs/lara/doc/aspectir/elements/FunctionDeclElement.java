package pt.up.fe.specs.lara.doc.aspectir.elements;

import java.util.List;

import pt.up.fe.specs.lara.doc.comments.LaraDocComment;
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTag;
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTagName;
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTagProperty;

public class FunctionDeclElement extends AAspectIrElement {

    private final String functionName;
    private final List<String> parameters;

    public FunctionDeclElement(String functionName, List<String> parameters, LaraDocComment laraDocComment) {
        super(laraDocComment);
        this.functionName = functionName;
        this.parameters = parameters;

        // Add tags in not present
        if (!parameters.isEmpty() && !laraDocComment.hasTag(JsDocTagName.PARAM)) {

            for (var param : parameters) {
                var tag = new JsDocTag(JsDocTagName.PARAM);
                tag.setValue(JsDocTagProperty.NAME, param);
                tag.setValue(JsDocTagProperty.TYPE_NAME, "unknown");
                laraDocComment.addTag(tag);
            }
        }
    }

    public String getFunctionName() {
        return functionName;
    }

    public List<String> getParameters() {
        return parameters;
    }

    @Override
    public String getName() {
        return functionName;
    }
}
