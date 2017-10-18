package pt.up.fe.specs.lara.doc.aspectir.elements;

import pt.up.fe.specs.lara.doc.comments.LaraDocComment;

public class AssignmentElement extends AAspectIrElement {

    private final String leftHand;

    public AssignmentElement(String leftHand, LaraDocComment laraDocComment) {
        super(laraDocComment);

        this.leftHand = leftHand;
    }

    public String getLeftHand() {
        return leftHand;
    }

}
