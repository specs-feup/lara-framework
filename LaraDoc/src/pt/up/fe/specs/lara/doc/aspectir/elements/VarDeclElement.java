package pt.up.fe.specs.lara.doc.aspectir.elements;

import java.util.ArrayList;
import java.util.List;

import pt.up.fe.specs.lara.doc.comments.LaraDocComment;

public class VarDeclElement extends AAspectIrElement {

    private final String varDeclName;
    private final List<AssignmentElement> staticElements;
    private final List<AssignmentElement> instanceElements;

    public VarDeclElement(String varDeclName, LaraDocComment laraDocComment) {
        super(laraDocComment);

        this.varDeclName = varDeclName;
        this.staticElements = new ArrayList<>();
        this.instanceElements = new ArrayList<>();
    }

    public String getVarDeclName() {
        return varDeclName;
    }

    public List<AssignmentElement> getStaticElements() {
        return staticElements;
    }

    public List<AssignmentElement> getInstanceElements() {
        return instanceElements;
    }

    public void addAssignment(AssignmentElement assignment) {
        switch (assignment.getAssignmentType()) {
        case STATIC:
            staticElements.add(assignment);
            return;
        case INSTANCE:
            instanceElements.add(assignment);
            return;
        default:
            throw new RuntimeException("Assignment type not supported: " + assignment.getAssignmentType());
        }
    }
}
