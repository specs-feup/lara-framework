package pt.up.fe.specs.lara.doc.aspectir.elements;

import java.util.List;

import pt.up.fe.specs.lara.doc.comments.LaraDocComment;

public class FunctionDeclElement extends AAspectIrElement {

    private final String functionName;
    private final List<String> parameters;

    public FunctionDeclElement(String functionName, List<String> parameters, LaraDocComment laraDocComment) {
        super(laraDocComment);
        this.functionName = functionName;
        this.parameters = parameters;
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
