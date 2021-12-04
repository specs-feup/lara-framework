package pt.up.fe.specs.lara.doc.aspectir.elements;

import pt.up.fe.specs.lara.doc.comments.LaraDocComment;

public class StatementElement extends AAspectIrElement {

    public StatementElement(LaraDocComment laraDocComment) {
        super(laraDocComment);
    }

    @Override
    public String getName() {
        return "<statement with undefined name>";
    }

}
