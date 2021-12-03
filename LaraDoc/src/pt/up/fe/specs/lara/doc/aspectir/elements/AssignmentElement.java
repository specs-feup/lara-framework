package pt.up.fe.specs.lara.doc.aspectir.elements;

import java.util.Optional;

import com.google.common.base.Preconditions;

import pt.up.fe.specs.lara.doc.aspectir.AspectIrElement;
import pt.up.fe.specs.lara.doc.comments.LaraDocComment;
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTag;
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTagName;
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTagProperty;

public class AssignmentElement extends AAspectIrElement {

    private final String leftHand;
    private AspectIrElement rightHand;
    private AssignmentType assignmentType;

    public AssignmentElement(String name, AspectIrElement element, AssignmentType namedType,
            LaraDocComment laraDocComment) {
        super(laraDocComment);

        this.leftHand = name;
        this.rightHand = element;
        this.assignmentType = namedType;
    }

    public AssignmentElement(String name, AspectIrElement element, LaraDocComment laraDocComment) {
        this(name, element, null, laraDocComment);
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

    public String getNamePath() {
        JsDocTag alias = getComment().getTag(JsDocTagName.ALIAS);
        return alias.getValue(JsDocTagProperty.NAME_PATH);
    }

    @Override
    public String getName() {
        return getNamePath();
    }

}
