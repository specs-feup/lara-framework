package pt.up.fe.specs.lara.doc.aspectir;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.base.Preconditions;

import pt.up.fe.specs.lara.doc.aspectir.elements.AssignmentElement;
import pt.up.fe.specs.lara.doc.aspectir.elements.AssignmentType;
import pt.up.fe.specs.lara.doc.aspectir.elements.FunctionDeclElement;
import pt.up.fe.specs.lara.doc.aspectir.elements.StatementElement;
import pt.up.fe.specs.lara.doc.aspectir.elements.VarDeclElement;
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTag;
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTagName;
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTagProperty;
import pt.up.fe.specs.util.SpecsCollections;

public class AspectIrDoc {

    // private final Map<String, VarDeclElement> varDeclarations;
    private final List<AspectIrElement> topLevelElements;

    public AspectIrDoc(List<AspectIrElement> elements) {

        this.topLevelElements = elements;
    }

    public static AspectIrDoc newInstance(List<AspectIrElement> aspectIrElements) {
        List<AspectIrElement> topLevelElements = new ArrayList<>();
        // TODO: Organize elements (e.g., separate into classes / functions, put together elements that belong to each
        // other, etc.)

        Map<String, VarDeclElement> vardeclarations = new LinkedHashMap<>();

        // Remove variable declarations from the list
        SpecsCollections.remove(aspectIrElements, VarDeclElement.class::isInstance).stream()
                .map(VarDeclElement.class::cast)
                .forEach(varDecl -> vardeclarations.put(varDecl.getVarDeclName(), varDecl));

        // Add var declarations as top level elements
        topLevelElements.addAll(vardeclarations.values());

        // Function declarations and generic statements are considered global
        SpecsCollections.remove(aspectIrElements, FunctionDeclElement.class::isInstance).stream()
                .forEach(topLevelElements::add);

        SpecsCollections.remove(aspectIrElements, StatementElement.class::isInstance).stream()
                .forEach(topLevelElements::add);

        // Bind assignments to variable declarations, whenever possible
        SpecsCollections.remove(aspectIrElements, AssignmentElement.class::isInstance).stream()
                .map(AssignmentElement.class::cast)
                .forEach(assignment -> bindAssignment(assignment, vardeclarations, topLevelElements));

        Preconditions.checkArgument(aspectIrElements.isEmpty(), "Expected list of aspect elements to be empty: %s",
                aspectIrElements);

        return new AspectIrDoc(topLevelElements);
    }

    private static void bindAssignment(AssignmentElement assignment, Map<String, VarDeclElement> vardeclarations,
            List<AspectIrElement> topLevelElements) {

        String leftHand = assignment.getLeftHand();

        // Split by '.'
        String[] parts = leftHand.split("\\.");

        // If only one part
        if (parts.length == 1) {

            // If name already exists, is a redefinition; otherwise is a global assignment
            AssignmentType type = vardeclarations.containsKey(parts[0]) ? AssignmentType.REDEFINITION
                    : AssignmentType.GLOBAL;
            assignment.setAssignmentType(type);

            // In both cases, they are added to top-level elements
            topLevelElements.add(assignment);

            // Add alias
            assignment.getComment()
                    .addTagIfMissing(new JsDocTag(JsDocTagName.ALIAS).setValue(JsDocTagProperty.NAME_PATH, parts[0]));

            return;
        }

        // Try to find first part in the table
        VarDeclElement varDecl = vardeclarations.get(parts[0]);

        // If no varDecl, add has global assignment
        if (varDecl == null) {
            assignment.setAssignmentType(AssignmentType.GLOBAL);
            // In both cases, they are added to top-level elements
            topLevelElements.add(assignment);
            // Add alias
            assignment.getComment()
                    .addTagIfMissing(new JsDocTag(JsDocTagName.ALIAS).setValue(JsDocTagProperty.NAME_PATH, parts[0]));
            return;
        }

        // Check if instance or static member
        AssignmentType type = parts[1].equals("prototype") ? AssignmentType.INSTANCE : AssignmentType.STATIC;
        assignment.setAssignmentType(type);

        // If static, start from index 1. Otherwise means that index 1 is prototype, start from index 2
        int startingIndex = type == AssignmentType.STATIC ? 1 : 2;
        String memberName = IntStream.range(startingIndex, parts.length)
                .mapToObj(i -> parts[i])
                .collect(Collectors.joining("."));

        // Add assignment to varDecl
        varDecl.addAssignment(assignment);
        // Add alias
        assignment.getComment()
                .addTagIfMissing(new JsDocTag(JsDocTagName.ALIAS).setValue(JsDocTagProperty.NAME_PATH, memberName));
    }

    public List<AspectIrElement> getTopLevelElements() {
        return topLevelElements;
    }

    public <T extends AspectIrElement> List<T> getTopLevelElements(Class<T> elementClass) {
        return topLevelElements.stream()
                .filter(elementClass::isInstance)
                .map(elementClass::cast)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return topLevelElements.toString();
    }

}
