package pt.up.fe.specs.lara.doc.aspectir.elements;

import pt.up.fe.specs.lara.doc.aspectir.AspectIrElement;
import pt.up.fe.specs.lara.doc.comments.LaraDocComment;
import pt.up.fe.specs.util.SpecsStrings;

public abstract class AAspectIrElement implements AspectIrElement {

    private final LaraDocComment laraDocComment;

    public AAspectIrElement(LaraDocComment laraDocComment) {
        this.laraDocComment = laraDocComment;
    }

    @Override
    public String getType() {
        String className = getClass().getSimpleName();
        if (className.endsWith("Element")) {
            className = SpecsStrings.removeSuffix(className, "Element");
        }

        return className;
    }

    @Override
    public LaraDocComment getComment() {
        return laraDocComment;
    }

    @Override
    public String toString() {
        return "Comment:" + laraDocComment;
    }
}
