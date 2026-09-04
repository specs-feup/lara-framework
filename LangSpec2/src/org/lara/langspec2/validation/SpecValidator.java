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
            var attributeSignatures = new HashSet<MemberSignature>();
            for (var attr : jp.getOwnAttributes()) {
                var signature = memberSignature(attr.name(), attr.parameters());
                if (!attributeSignatures.add(signature)) {
                    errors.add("Duplicate attribute signature '" + formatSignature(signature)
                            + "' in join point '" + jp.getName() + "'");
                }
            }
            var actionSignatures = new HashSet<MemberSignature>();
            for (var action : jp.getOwnActions()) {
                var signature = memberSignature(action.name(), action.parameters());
                if (!actionSignatures.add(signature)) {
                    errors.add("Duplicate action signature '" + formatSignature(signature)
                            + "' in join point '" + jp.getName() + "'");
                }
            }
        }
    }

    private static MemberSignature memberSignature(String name, List<Parameter> parameters) {
        var paramTypes = parameters.stream().map(Parameter::type).toList();
        return new MemberSignature(name, paramTypes);
    }

    private static String formatSignature(MemberSignature signature) {
        var params = signature.paramTypes().stream()
                .map(Object::toString)
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
        return signature.name() + "(" + params + ")";
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
        var validJpNames = new HashSet<String>();
        validJpNames.add(model.getGlobal().getName());
        for (var jp : model.getJoinPoints().values()) {
            validJpNames.add(jp.getName());
        }

        var validTypeDefs = Set.copyOf(model.getTypeDefs().keySet());
        var validEnums = Set.copyOf(model.getEnumDefs().keySet());

        for (var jp : model.getAllJpClasses()) {
            for (var attr : jp.getOwnAttributes()) {
                checkTypeRef(attr.type(), validJpNames, validTypeDefs, validEnums,
                        "attribute '" + attr.name() + "' of '" + jp.getName() + "'", errors);
                for (var param : attr.parameters()) {
                    checkTypeRef(param.type(), validJpNames, validTypeDefs, validEnums,
                            "parameter '" + param.name() + "' of attribute '" + attr.name() + "' of '" + jp.getName() + "'", errors);
                }
            }
            for (var action : jp.getOwnActions()) {
                checkTypeRef(action.returnType(), validJpNames, validTypeDefs, validEnums,
                        "action '" + action.name() + "' of '" + jp.getName() + "'", errors);
                for (var param : action.parameters()) {
                    checkTypeRef(param.type(), validJpNames, validTypeDefs, validEnums,
                            "parameter '" + param.name() + "' of action '" + action.name() + "' of '" + jp.getName() + "'", errors);
                }
            }
        }
        for (var td : model.getTypeDefs().values()) {
            for (var field : td.fields()) {
                checkTypeRef(field.type(), validJpNames, validTypeDefs, validEnums,
                        "field '" + field.name() + "' of typedef '" + td.name() + "'", errors);
            }
        }
    }

    private static void checkTypeRef(JpDataType type,
                                     Set<String> validJpNames,
                                     Set<String> validTypeDefs,
                                     Set<String> validEnums,
                                     String context,
                                     List<String> errors) {
        if (type instanceof JpRefType ref) {
            if (!validJpNames.contains(ref.jpName())) {
                errors.add("Unknown type reference '" + ref.jpName() + "' in " + context);
            }
        } else if (type instanceof TypeDefRefType ref) {
            if (!validTypeDefs.contains(ref.typeDefName())) {
                errors.add("Unknown typedef reference '" + ref.typeDefName() + "' in " + context);
            }
        } else if (type instanceof EnumRefType ref) {
            if (!validEnums.contains(ref.enumName())) {
                errors.add("Unknown enum reference '" + ref.enumName() + "' in " + context);
            }
        } else if (type instanceof ArrayType arr) {
            checkTypeRef(arr.element(), validJpNames, validTypeDefs, validEnums, context, errors);
        } else if (type instanceof ParameterizedType pt) {
            checkTypeRef(pt.base(), validJpNames, validTypeDefs, validEnums, context, errors);
            for (var arg : pt.args()) {
                checkTypeRef(arg, validJpNames, validTypeDefs, validEnums, context, errors);
            }
        } else if (type instanceof WildcardType wt) {
            if (wt.bound() != null) {
                checkTypeRef(wt.bound(), validJpNames, validTypeDefs, validEnums, context, errors);
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

    // Reserved words can be used as object/class property and method names in TypeScript,
    // so no keywords need to be restricted for attribute/action names
    private static void checkReservedKeywords(WeaverModel model, List<String> errors) {
        // Currently no validation needed - reserved words are allowed as class members in TypeScript
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

    private record MemberSignature(String name, List<JpDataType> paramTypes) {}
}
