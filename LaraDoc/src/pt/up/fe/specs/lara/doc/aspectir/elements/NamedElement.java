package pt.up.fe.specs.lara.doc.aspectir.elements;

import java.util.Optional;

import com.google.common.base.Preconditions;

import pt.up.fe.specs.lara.doc.aspectir.AspectIrElement;
import pt.up.fe.specs.lara.doc.comments.LaraDocComment;
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTag;
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTagName;
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTagProperty;

public class NamedElement extends AAspectIrElement {

    private final String fullName;
    private AspectIrElement element;
    private NamedType namedType;

    public NamedElement(String name, AspectIrElement element, NamedType namedType,
            LaraDocComment laraDocComment) {
        super(laraDocComment);

        this.fullName = name;
        this.element = element;
        this.namedType = namedType;
    }

    public NamedElement(String name, AspectIrElement element, LaraDocComment laraDocComment) {
        this(name, element, null, laraDocComment);
    }

    public String getFullName() {
        return fullName;
    }

    public AspectIrElement getElement() {
        return element;
    }

    public Optional<FunctionDeclElement> getRightFunctionDecl() {
        if (element == null) {
            return Optional.empty();
        }

        if (!(element instanceof FunctionDeclElement)) {
            return Optional.empty();
        }

        return Optional.of((FunctionDeclElement) element);
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
