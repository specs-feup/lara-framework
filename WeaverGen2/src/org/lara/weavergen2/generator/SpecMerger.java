package org.lara.weavergen2.generator;

import org.lara.langspec2.model.*;
import org.lara.langspec2.types.JpDataType;
import org.lara.langspec2.types.JpDataType.ArrayType;
import org.lara.langspec2.types.JpDataType.JpRefType;
import org.lara.langspec2.types.JpDataType.ParameterizedType;
import org.lara.langspec2.types.JpDataType.WildcardType;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Merges a base spec (from WeaverInterface) with a weaver-specific spec.
 * <p>
 * The base spec's global attributes/actions are added to the weaver spec's global.
 * The weaver spec provides the JP hierarchy, weaver name, and package.
 */
public final class SpecMerger {

    private SpecMerger() {}

    /**
     * Merges the base spec into the weaver spec model.
     * The base spec's global attributes and actions are copied into the weaver model's global,
     * preserving the weaver definition when both specs declare the same member signature.
     */
    public static WeaverModel merge(WeaverModel baseModel, WeaverModel weaverModel) {
        var mergedGlobal = copyJoinPoint(weaverModel.getGlobal());
        mergeBaseGlobalMembers(baseModel.getGlobal(), mergedGlobal, weaverModel.getGlobal().getName());

        var merged = new WeaverModel(
                weaverModel.getPrefix(),
                weaverModel.getBasePackage(),
                mergedGlobal
        );

        var copiedJoinPoints = new LinkedHashMap<String, JpClass>();
        copiedJoinPoints.put(mergedGlobal.getName(), mergedGlobal);

        for (var jp : weaverModel.getJoinPoints().values()) {
            copiedJoinPoints.put(jp.getName(), copyJoinPoint(jp));
        }

        for (var jp : weaverModel.getJoinPoints().values()) {
            var copiedJp = copiedJoinPoints.get(jp.getName());
            jp.getParent().ifPresent(parent -> copiedJp.setParent(copiedJoinPoints.get(parent.getName())));
            merged.addJoinPoint(copiedJp);
        }

        for (var td : weaverModel.getTypeDefs().values()) {
            merged.addTypeDef(copyTypeDef(td));
        }
        for (var td : baseModel.getTypeDefs().values()) {
            if (!merged.getTypeDefs().containsKey(td.name())) {
                merged.addTypeDef(copyTypeDef(td));
            }
        }

        for (var ed : weaverModel.getEnumDefs().values()) {
            merged.addEnumDef(copyEnumDef(ed));
        }
        for (var ed : baseModel.getEnumDefs().values()) {
            if (!merged.getEnumDefs().containsKey(ed.name())) {
                merged.addEnumDef(copyEnumDef(ed));
            }
        }

        weaverModel.getRoot().ifPresent(root -> {
            var copiedRoot = copiedJoinPoints.get(root.getName());
            if (copiedRoot != null) {
                merged.setRoot(copiedRoot);
            }
        });

        if (merged.getRoot().isEmpty()) {
            baseModel.getRoot().ifPresent(root -> {
                var copiedRoot = copiedJoinPoints.get(root.getName());
                if (copiedRoot != null) {
                    merged.setRoot(copiedRoot);
                } else if (root.equals(baseModel.getGlobal())) {
                    merged.setRoot(mergedGlobal);
                }
            });
        }

        return merged;
    }

    private static void mergeBaseGlobalMembers(JpClass baseGlobal, JpClass mergedGlobal, String targetGlobalName) {
        var attributeSignatures = new LinkedHashSet<MemberSignature>();
        for (var attr : mergedGlobal.getOwnAttributes()) {
            attributeSignatures.add(signature(attr.name(), attr.parameters()));
        }

        for (var baseAttr : baseGlobal.getOwnAttributes()) {
            var rewrittenAttr = copyAttribute(baseAttr, baseGlobal.getName(), targetGlobalName);
            var signature = signature(rewrittenAttr.name(), rewrittenAttr.parameters());
            if (attributeSignatures.contains(signature)) {
                continue;
            }

            mergedGlobal.addAttribute(rewrittenAttr);
            attributeSignatures.add(signature);
        }

        var actionSignatures = new LinkedHashSet<MemberSignature>();
        for (var action : mergedGlobal.getOwnActions()) {
            actionSignatures.add(signature(action.name(), action.parameters()));
        }

        for (var baseAction : baseGlobal.getOwnActions()) {
            var rewrittenAction = copyAction(baseAction, baseGlobal.getName(), targetGlobalName);
            var signature = signature(rewrittenAction.name(), rewrittenAction.parameters());
            if (actionSignatures.contains(signature)) {
                continue;
            }

            mergedGlobal.addAction(rewrittenAction);
            actionSignatures.add(signature);
        }


        baseGlobal.getDefaultAttribute().ifPresent(defAttr -> {
            if (mergedGlobal.getDefaultAttribute().isEmpty()) {
                mergedGlobal.setDefaultAttribute(defAttr);
            }
        });

        baseGlobal.getTooltip().ifPresent(tt -> {
            if (mergedGlobal.getTooltip().isEmpty()) {
                mergedGlobal.setTooltip(tt);
            }
        });
    }

    private static JpClass copyJoinPoint(JpClass source) {
        var copy = new JpClass(source.getName());
        source.getTooltip().ifPresent(copy::setTooltip);
        source.getDefaultAttribute().ifPresent(copy::setDefaultAttribute);

        for (var attr : source.getOwnAttributes()) {
            copy.addAttribute(attr);
        }
        for (var action : source.getOwnActions()) {
            copy.addAction(action);
        }

        return copy;
    }

    private static Attribute copyAttribute(Attribute attr, String fromGlobalName, String toGlobalName) {
        return new Attribute(
                attr.name(),
                rewriteBaseJoinPointRefs(attr.type(), fromGlobalName, toGlobalName),
                rewriteParameters(attr.parameters(), fromGlobalName, toGlobalName),
                attr.tooltip()
        );
    }

    private static Action copyAction(Action action, String fromGlobalName, String toGlobalName) {
        return new Action(
                action.name(),
                rewriteBaseJoinPointRefs(action.returnType(), fromGlobalName, toGlobalName),
                rewriteParameters(action.parameters(), fromGlobalName, toGlobalName),
                action.tooltip()
        );
    }

    private static TypeDef copyTypeDef(TypeDef typeDef) {
        return new TypeDef(typeDef.name(), typeDef.fields(), typeDef.tooltip());
    }

    private static EnumDef copyEnumDef(EnumDef enumDef) {
        return new EnumDef(enumDef.name(), enumDef.values(), enumDef.tooltip());
    }

    private static List<Parameter> rewriteParameters(List<Parameter> parameters, String fromGlobalName, String toGlobalName) {
        return parameters.stream()
                .map(parameter -> new Parameter(
                        parameter.name(),
                        rewriteBaseJoinPointRefs(parameter.type(), fromGlobalName, toGlobalName),
                        parameter.defaultValue()))
                .toList();
    }

    private static JpDataType rewriteBaseJoinPointRefs(JpDataType type, String fromGlobalName, String toGlobalName) {
        if (type instanceof JpRefType ref) {
            if (ref.jpName().equals(fromGlobalName)) {
                return new JpRefType(toGlobalName);
            }
            return type;
        }

        if (type instanceof ArrayType arr) {
            return new ArrayType(rewriteBaseJoinPointRefs(arr.element(), fromGlobalName, toGlobalName));
        }

        if (type instanceof ParameterizedType pt) {
            var base = rewriteBaseJoinPointRefs(pt.base(), fromGlobalName, toGlobalName);
            var args = pt.args().stream()
                    .map(arg -> rewriteBaseJoinPointRefs(arg, fromGlobalName, toGlobalName))
                    .toList();
            return new ParameterizedType(base, args);
        }

        if (type instanceof WildcardType wt) {
            var bound = wt.bound() == null ? null : rewriteBaseJoinPointRefs(wt.bound(), fromGlobalName, toGlobalName);
            return new WildcardType(wt.kind(), bound);
        }

        return type;
    }

    private static MemberSignature signature(String name, List<Parameter> parameters) {
        var parameterTypes = parameters.stream().map(Parameter::type).toList();
        return new MemberSignature(name, parameterTypes);
    }

    private record MemberSignature(String name, List<JpDataType> parameterTypes) {}
}
