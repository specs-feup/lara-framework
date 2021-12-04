package pt.up.fe.specs.lara.doc.aspectir.elements;

import java.util.Optional;

import com.google.common.base.Preconditions;

import pt.up.fe.specs.lara.doc.aspectir.AspectIrElement;
import pt.up.fe.specs.lara.doc.comments.LaraDocComment;
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTag;
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTagName;
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTagProperty;

public class AssignmentElement extends AAspectIrElement {

    private final String fullName;
    private FunctionDeclElement element;
    private NamedType namedType;
    private String parentClass;

    public AssignmentElement(String name, FunctionDeclElement element, NamedType namedType,
            LaraDocComment laraDocComment) {
        super(laraDocComment);

        this.fullName = name;
        this.element = element;
        this.namedType = namedType;
        this.parentClass = null;
    }

    public AssignmentElement(String name, FunctionDeclElement element, LaraDocComment laraDocComment) {
        this(name, element, null, laraDocComment);
    }

    public void setParentClass(String parentClass) {
        this.parentClass = parentClass;
    }

    public Optional<String> getParentClass() {
        return Optional.ofNullable(parentClass);
    }

    public String getLeftHand() {
        return fullName;
    }

    public AspectIrElement getElement() {
        return element;
    }

    public Optional<FunctionDeclElement> getRightFunctionDecl() {
        return Optional.ofNullable(element);
        // if (element == null) {
        // return Optional.empty();
        // }
        //
        // if (!(element instanceof FunctionDeclElement)) {
        // return Optional.empty();
        // }
        //
        // return Optional.of(element);
    }

    public void setAssignmentType(NamedType assignmentType) {
        this.namedType = assignmentType;
    }

    public NamedType getAssignmentType() {
        Preconditions.checkNotNull(namedType, "Assignment type is null");
        return namedType;
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
