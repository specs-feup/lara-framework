package pt.up.fe.specs.lara.doc.aspectir.elements;

import java.util.Optional;

import com.google.common.base.Preconditions;

import pt.up.fe.specs.lara.doc.aspectir.AspectIrElement;
import pt.up.fe.specs.lara.doc.comments.LaraDocComment;

public class AssignmentElement extends AAspectIrElement {

    private final String leftHand;
    private AspectIrElement rightHand;
    private AssignmentType assignmentType;

    public AssignmentElement(String leftHand, AspectIrElement rightHand, LaraDocComment laraDocComment) {
        super(laraDocComment);

        this.leftHand = leftHand;
        this.rightHand = rightHand;
        this.assignmentType = null;
    }

    public String getLeftHand() {
        return leftHand;
    }

    public AspectIrElement getRightHand() {
        return rightHand;
    }

    public Optional<FunctionDeclElement> getRightFunctionDecl() {
        if (rightHand == null) {
            return Optional.empty();
        }

        if (!(rightHand instanceof FunctionDeclElement)) {
            return Optional.empty();
        }

        return Optional.of((FunctionDeclElement) rightHand);
    }

    public void setAssignmentType(AssignmentType assignmentType) {
        this.assignmentType = assignmentType;
    }

    public AssignmentType getAssignmentType() {
        Preconditions.checkNotNull(assignmentType, "Assignment type is null");
        return assignmentType;
    }
}
