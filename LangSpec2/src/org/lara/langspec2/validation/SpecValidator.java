package org.lara.langspec2.validation;

import org.lara.langspec2.model.*;
import org.lara.langspec2.types.JpDataType;
import org.lara.langspec2.types.JpDataType.*;

import java.util.*;

/**
 * Validates a {@link WeaverModel} for consistency and correctness.
 */
public final class SpecValidator {

    private SpecValidator() {}

    /**
     * Validates the model, throwing {@link SpecValidationException} on the first error found.
     */
    public static void validate(WeaverModel model) {
        var errors = collectErrors(model);
        if (!errors.isEmpty()) {
            throw new SpecValidationException(errors);
        }
    }

    /**
     * Collects all validation errors without throwing.
     */
    public static List<String> collectErrors(WeaverModel model) {
        var errors = new ArrayList<String>();

        checkNoDuplicateNames(model, errors);
        checkNoInheritanceCycles(model, errors);
        checkTypeReferences(model, errors);
        checkNoSelfTypeInTypeDefs(model, errors);
        checkReservedKeywords(model, errors);
        checkDefaultAttributes(model, errors);

        return errors;
    }

    private static void checkNoDuplicateNames(WeaverModel model, List<String> errors) {
        var allNames = new HashSet<String>();
        allNames.add(model.getGlobal().getName());

        for (var jp : model.getJoinPoints().values()) {
            if (!allNames.add(jp.getName())) {
                errors.add("Duplicate join point name: " + jp.getName());
            }
        }

        // Check duplicate attribute names within same JP (own only)
        for (var jp : model.getAllJpClasses()) {
            var attrNames = new HashSet<String>();
            for (var attr : jp.getOwnAttributes()) {
                if (!attrNames.add(attr.name())) {
                    errors.add("Duplicate attribute '" + attr.name() + "' in join point '" + jp.getName() + "'");
                }
            }
            var actionNames = new HashSet<String>();
            for (var action : jp.getOwnActions()) {
                // Actions may be overloaded by parameter types, but we check by name+arity
                var key = action.name() + "/" + action.parameters().size();
                if (!actionNames.add(key)) {
                    errors.add("Duplicate action '" + action.name() + "' with " + action.parameters().size()
                            + " parameters in join point '" + jp.getName() + "'");
                }
            }
        }
    }

    private static void checkNoInheritanceCycles(WeaverModel model, List<String> errors) {
        for (var jp : model.getAllJpClasses()) {
            var visited = new HashSet<String>();
            var current = jp;
            while (current != null) {
                if (!visited.add(current.getName())) {
                    errors.add("Inheritance cycle detected involving: " + jp.getName());
                    break;
                }
                current = current.getParent().orElse(null);
            }
        }
    }

    private static void checkTypeReferences(WeaverModel model, List<String> errors) {
        var validNames = new HashSet<String>();
        validNames.add(model.getGlobal().getName());
        for (var jp : model.getJoinPoints().values()) {
            validNames.add(jp.getName());
        }
        for (var td : model.getTypeDefs().keySet()) {
            validNames.add(td);
        }
        for (var ed : model.getEnumDefs().keySet()) {
            validNames.add(ed);
        }

        for (var jp : model.getAllJpClasses()) {
            for (var attr : jp.getOwnAttributes()) {
                checkTypeRef(attr.type(), validNames, "attribute '" + attr.name() + "' of '" + jp.getName() + "'", errors);
                for (var param : attr.parameters()) {
                    checkTypeRef(param.type(), validNames, "parameter '" + param.name() + "' of attribute '" + attr.name() + "' of '" + jp.getName() + "'", errors);
                }
            }
            for (var action : jp.getOwnActions()) {
                checkTypeRef(action.returnType(), validNames, "action '" + action.name() + "' of '" + jp.getName() + "'", errors);
                for (var param : action.parameters()) {
                    checkTypeRef(param.type(), validNames, "parameter '" + param.name() + "' of action '" + action.name() + "' of '" + jp.getName() + "'", errors);
                }
            }
        }
    }

    private static void checkTypeRef(JpDataType type, Set<String> validNames, String context, List<String> errors) {
        if (type instanceof JpRefType ref) {
            if (!validNames.contains(ref.jpName())) {
                errors.add("Unknown type reference '" + ref.jpName() + "' in " + context);
            }
        } else if (type instanceof ArrayType arr) {
            checkTypeRef(arr.element(), validNames, context, errors);
        } else if (type instanceof ParameterizedType pt) {
            checkTypeRef(pt.base(), validNames, context, errors);
            for (var arg : pt.args()) {
                checkTypeRef(arg, validNames, context, errors);
            }
        } else if (type instanceof WildcardType wt) {
            if (wt.bound() != null) {
                checkTypeRef(wt.bound(), validNames, context, errors);
            }
        }
        // PrimitiveType and SelfType: no-op
    }

    private static void checkNoSelfTypeInTypeDefs(WeaverModel model, List<String> errors) {
        for (var td : model.getTypeDefs().values()) {
            for (var field : td.fields()) {
                if (containsSelfType(field.type())) {
                    errors.add("SelfType ('this') not allowed in typedef field '" + field.name()
                            + "' of typedef '" + td.name() + "'");
                }
            }
        }
    }

    private static boolean containsSelfType(JpDataType type) {
        if (type instanceof SelfType) {
            return true;
        } else if (type instanceof ArrayType arr) {
            return containsSelfType(arr.element());
        } else if (type instanceof ParameterizedType pt) {
            return containsSelfType(pt.base()) || pt.args().stream().anyMatch(SpecValidator::containsSelfType);
        } else if (type instanceof WildcardType wt) {
            return wt.bound() != null && containsSelfType(wt.bound());
        }
        return false;
    }

    private static final Set<String> JAVA_KEYWORDS = Set.of(
            "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char",
            "class", "const", "continue", "default", "do", "double", "else", "enum",
            "extends", "final", "finally", "float", "for", "goto", "if", "implements",
            "import", "instanceof", "int", "interface", "long", "native", "new",
            "package", "private", "protected", "public", "return", "short", "static",
            "strictfp", "super", "switch", "synchronized", "this", "throw", "throws",
            "transient", "try", "void", "volatile", "while"
    );

    private static void checkReservedKeywords(WeaverModel model, List<String> errors) {
        for (var jp : model.getAllJpClasses()) {
            // JP names can be Java keywords (they become string identifiers, not Java identifiers)
            // But attribute/action names will become Java method names
            for (var attr : jp.getOwnAttributes()) {
                if (JAVA_KEYWORDS.contains(attr.name())) {
                    errors.add("Attribute name '" + attr.name() + "' in '" + jp.getName()
                            + "' is a Java reserved keyword");
                }
            }
            for (var action : jp.getOwnActions()) {
                if (JAVA_KEYWORDS.contains(action.name())) {
                    errors.add("Action name '" + action.name() + "' in '" + jp.getName()
                            + "' is a Java reserved keyword");
                }
            }
        }
    }

    private static void checkDefaultAttributes(WeaverModel model, List<String> errors) {
        for (var jp : model.getAllJpClasses()) {
            jp.getDefaultAttribute().ifPresent(defAttr -> {
                var found = jp.getAllAttributes().stream()
                        .anyMatch(a -> a.name().equals(defAttr));
                if (!found) {
                    errors.add("Default attribute '" + defAttr + "' not found on join point '"
                            + jp.getName() + "' (including inherited)");
                }
            });
        }
    }
}
