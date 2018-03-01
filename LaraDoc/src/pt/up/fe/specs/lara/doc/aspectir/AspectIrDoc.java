package pt.up.fe.specs.lara.doc.aspectir;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.base.Preconditions;

import pt.up.fe.specs.lara.aspectir.Base;
import pt.up.fe.specs.lara.aspectir.CodeElem;
import pt.up.fe.specs.lara.aspectir.ExprId;
import pt.up.fe.specs.lara.aspectir.ExprLiteral;
import pt.up.fe.specs.lara.doc.aspectir.elements.AspectElement;
import pt.up.fe.specs.lara.doc.aspectir.elements.AssignmentElement;
import pt.up.fe.specs.lara.doc.aspectir.elements.AssignmentType;
import pt.up.fe.specs.lara.doc.aspectir.elements.ClassElement;
import pt.up.fe.specs.lara.doc.aspectir.elements.CodeElement;
import pt.up.fe.specs.lara.doc.aspectir.elements.FunctionDeclElement;
import pt.up.fe.specs.lara.doc.aspectir.elements.StatementElement;
import pt.up.fe.specs.lara.doc.aspectir.elements.VarDeclElement;
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTag;
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTagName;
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTagProperty;
import pt.up.fe.specs.util.SpecsCollections;
import pt.up.fe.specs.util.SpecsLogs;

public class AspectIrDoc {

    // private final Map<String, VarDeclElement> varDeclarations;
    private final List<AspectIrElement> topLevelElements;
    private final Predicate<String> nameExcluder;
    //
    // public AspectIrDoc(List<AspectIrElement> elements) {
    // this(elements, name -> name.startsWith("_"));
    // }

    public AspectIrDoc(List<AspectIrElement> elements, Predicate<String> nameExcluder) {
        this.topLevelElements = elements;
        this.nameExcluder = nameExcluder;
    }

    public static AspectIrDoc newInstance(List<AspectIrElement> aspectIrElements) {
        return newInstance(aspectIrElements, name -> name.startsWith("_"));
    }

    public static AspectIrDoc newInstance(List<AspectIrElement> aspectIrElements, Predicate<String> nameExcluder) {
        List<AspectIrElement> topLevelElements = new ArrayList<>();
        // TODO: Organize elements (e.g., separate into classes / functions, put together elements that belong to each
        // other, etc.)

        Map<String, ClassElement> classes = new LinkedHashMap<>();

        // Remove variable declarations from the list
        SpecsCollections.remove(aspectIrElements, ClassElement.class::isInstance).stream()
                .map(ClassElement.class::cast)
                .forEach(classElement -> classes.put(classElement.getClassName(), classElement));

        // vardeclarations.values().stream()
        // .forEach(vardecl -> System.out.println("VarDecl comment:" + vardecl.getComment()));
        // Add var declarations as top level elements
        topLevelElements.addAll(classes.values());

        // Aspects, var declarations, function declarations and generic statements are considered global
        SpecsCollections.remove(aspectIrElements, AspectElement.class::isInstance).stream()
                .forEach(topLevelElements::add);

        SpecsCollections.remove(aspectIrElements, FunctionDeclElement.class::isInstance).stream()
                .forEach(topLevelElements::add);

        SpecsCollections.remove(aspectIrElements, StatementElement.class::isInstance).stream()
                .forEach(topLevelElements::add);

        SpecsCollections.remove(aspectIrElements, VarDeclElement.class::isInstance).stream()
                .forEach(topLevelElements::add);

        // Bind assignments to variable declarations, whenever possible
        SpecsCollections.remove(aspectIrElements, AssignmentElement.class::isInstance).stream()
                .map(AssignmentElement.class::cast)
                .forEach(assignment -> bindAssignment(assignment, classes, topLevelElements, nameExcluder));

        Preconditions.checkArgument(aspectIrElements.isEmpty(), "Expected list of aspect elements to be empty: %s",
                aspectIrElements);

        return new AspectIrDoc(topLevelElements, nameExcluder);
    }

    private static void bindAssignment(AssignmentElement assignment, Map<String, ClassElement> classes,
            List<AspectIrElement> topLevelElements, Predicate<String> nameExcluder) {

        String leftHand = assignment.getLeftHand();

        // Split by '.'
        String[] parts = leftHand.split("\\.");

        // If only one part
        if (parts.length == 1) {

            if (nameExcluder.test(parts[0])) {
                return;
            }

            // If name already exists, is a redefinition; otherwise is a global assignment
            AssignmentType type = classes.containsKey(parts[0]) ? AssignmentType.REDEFINITION
                    : AssignmentType.GLOBAL;
            assignment.setAssignmentType(type);

            // In both cases, they are added to top-level elements
            topLevelElements.add(assignment);

            // Add alias
            // assignment.getComment()
            // .addTagIfMissing(new JsDocTag(JsDocTagName.ALIAS).setValue(JsDocTagProperty.NAME_PATH, parts[0]));
            assignment.getComment().getTag(JsDocTagName.ALIAS).setValueIfMissing(JsDocTagProperty.NAME_PATH, parts[0]);

            return;
        }

        // Try to find first part in the table
        ClassElement classElement = classes.get(parts[0]);

        // If no varDecl, add as global assignment
        if (classElement == null) {
            if (nameExcluder.test(parts[0])) {
                return;
            }

            assignment.setAssignmentType(AssignmentType.GLOBAL);
            // In both cases, they are added to top-level elements
            topLevelElements.add(assignment);
            // Add alias
            // assignment.getComment()
            // .addTagIfMissing(new JsDocTag(JsDocTagName.ALIAS).setValue(JsDocTagProperty.NAME_PATH, parts[0]));
            assignment.getComment().getTag(JsDocTagName.ALIAS).setValueIfMissing(JsDocTagProperty.NAME_PATH, parts[0]);
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

        // // If instance and member name is empty, means prototype inheritance
        if (type == AssignmentType.INSTANCE && memberName.isEmpty()) {
            // Determine from where it inherits from
            String parentClass = extractParentClass(assignment.getRightHand());

            // Check if tag already exist for this value
            boolean hasTag = classElement.getComment().getTags(JsDocTagName.AUGMENTS).stream()
                    .filter(tag -> tag.hasProperty(JsDocTagProperty.NAME_PATH))
                    .map(tag -> tag.getValue(JsDocTagProperty.NAME_PATH))
                    .filter(namepath -> namepath.equals(parentClass))
                    .findAny()
                    .isPresent();

            if (hasTag) {
                return;
            }

            // Add tag
            JsDocTag augmentsTag = new JsDocTag(JsDocTagName.AUGMENTS)
                    .setValue(JsDocTagProperty.NAME_PATH, parentClass);

            classElement.getComment().addTag(augmentsTag);
            return;
        }

        // Add assignment to varDecl
        if (!nameExcluder.test(memberName)) {
            classElement.addAssignment(assignment);
        }

        // Add alias
        // assignment.getComment()
        // .addTagIfMissing(new JsDocTag(JsDocTagName.ALIAS).setValue(JsDocTagProperty.NAME_PATH, memberName));
        assignment.getComment().getTag(JsDocTagName.ALIAS).setValueIfMissing(JsDocTagProperty.NAME_PATH, memberName);
    }

    private static String extractParentClass(AspectIrElement rightHand) {
        // Confirm it is a CodeElement
        Preconditions.checkArgument(rightHand instanceof CodeElement, "Case not defined: " + rightHand.getClass());

        CodeElem codeElem = ((CodeElement) rightHand).getCodeElement();

        // Look for a property that has a literal 'prototype' and an id
        List<ExprLiteral> prototypeLiterals = BaseNodes.toStream(codeElem)
                .filter(ExprLiteral.class::isInstance)
                .map(ExprLiteral.class::cast)
                .filter(literal -> literal.value.equals("prototype"))
                .collect(Collectors.toList());

        if (prototypeLiterals.size() != 1) {
            SpecsLogs.msgWarn("Expected to find one 'prototype' literal, found "
                    + prototypeLiterals.size() + ":\n " + CodeElems.toXml(codeElem));
            return "";
        }

        ExprLiteral prototypeLiteral = prototypeLiterals.get(0);

        Base parent = (Base) prototypeLiteral.getParent();

        // Look for ID node
        List<ExprId> idNodes = BaseNodes.toStream(parent)
                .filter(ExprId.class::isInstance)
                .map(ExprId.class::cast)
                .collect(Collectors.toList());

        if (idNodes.size() != 1) {
            SpecsLogs.msgWarn("Expected to find one id node, found "
                    + idNodes.size() + ":\n " + BaseNodes.toXml(parent));
            return "";
        }

        ExprId id = idNodes.get(0);
        return id.name;
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
