package pt.up.fe.specs.lara.doc.aspectir.elements;

import pt.up.fe.specs.lara.doc.comments.LaraDocComment;

public class VarDeclElement extends AAspectIrElement {

    private final String varDeclName;

    public VarDeclElement(String varDeclName, LaraDocComment laraDocComment) {
        super(laraDocComment);

        this.varDeclName = varDeclName;
    }

    public String getVarDeclName() {
        return varDeclName;
    }
}
