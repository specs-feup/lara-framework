package org.lara.weavergen2.generator;

import org.lara.langspec2.model.*;
import org.lara.langspec2.dsl.WeaverSpec;
import org.lara.langspec2.types.JpDataType;

import java.util.ArrayList;
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
     * The base spec's global attributes and actions are prepended to the weaver model's global.
     */
    public static WeaverModel merge(WeaverSpec baseSpec, WeaverSpec weaverSpec) {
        // Build both models raw (no validation yet)
        var baseModel = baseSpec.buildRaw();
        var weaverModel = weaverSpec.buildRaw();

        // Merge base global attributes/actions into weaver global
        var weaverGlobal = weaverModel.getGlobal();
        var baseGlobal = baseModel.getGlobal();

        var mergedAttributes = mergeAttributes(baseGlobal.getOwnAttributes(), weaverGlobal.getOwnAttributes());
        var mergedActions = mergeActions(baseGlobal.getOwnActions(), weaverGlobal.getOwnActions());

        // We need to clear and re-add in order to prepend
        // Since JpClass uses an internal mutable list, we must work around it
        // by creating a merged model manually.
        var mergedGlobal = new JpClass("joinpoint");
        for (var attr : mergedAttributes) {
            mergedGlobal.addAttribute(attr);
        }
        for (var action : mergedActions) {
            mergedGlobal.addAction(action);
        }
        if (weaverGlobal.getDefaultAttribute().isPresent()) {
            mergedGlobal.setDefaultAttribute(weaverGlobal.getDefaultAttribute().get());
        }

        // Build merged model
        var merged = new WeaverModel(
                weaverModel.getWeaverName(),
                weaverModel.getBasePackage(),
                mergedGlobal
        );

        // Re-parent all join points: those that pointed to weaverGlobal now point to mergedGlobal
        for (var jp : weaverModel.getJoinPoints().values()) {
            if (jp.getParent().orElse(null) == weaverGlobal) {
                jp.setParent(mergedGlobal);
            }
            merged.addJoinPoint(jp);
        }

        // Copy typedefs and enums
        for (var td : baseModel.getTypeDefs().values()) {
            merged.addTypeDef(td);
        }
        for (var td : weaverModel.getTypeDefs().values()) {
            if (!merged.getTypeDefs().containsKey(td.name())) {
                merged.addTypeDef(td);
            }
        }

        for (var ed : baseModel.getEnumDefs().values()) {
            merged.addEnumDef(ed);
        }
        for (var ed : weaverModel.getEnumDefs().values()) {
            if (!merged.getEnumDefs().containsKey(ed.name())) {
                merged.addEnumDef(ed);
            }
        }

        // Set root
        weaverModel.getRoot().ifPresent(merged::setRoot);

        return merged;
    }

    private static List<Attribute> mergeAttributes(List<Attribute> baseAttrs, List<Attribute> weaverAttrs) {
        var merged = new ArrayList<>(baseAttrs);

        for (var weaverAttr : weaverAttrs) {
            var baseIndex = findMatchingAttributeIndex(merged, weaverAttr);
            if (baseIndex < 0) {
                merged.add(weaverAttr);
                continue;
            }

            var baseAttr = merged.get(baseIndex);
            var effectiveTooltip = weaverAttr.tooltip() != null ? weaverAttr.tooltip() : baseAttr.tooltip();
            merged.set(baseIndex, new Attribute(weaverAttr.name(), weaverAttr.type(), weaverAttr.parameters(), effectiveTooltip));
        }

        return List.copyOf(merged);
    }

    private static List<Action> mergeActions(List<Action> baseActions, List<Action> weaverActions) {
        var merged = new ArrayList<>(baseActions);

        for (var weaverAction : weaverActions) {
            var baseIndex = findMatchingActionIndex(merged, weaverAction);
            if (baseIndex < 0) {
                merged.add(weaverAction);
                continue;
            }

            var baseAction = merged.get(baseIndex);
            var effectiveTooltip = weaverAction.tooltip() != null ? weaverAction.tooltip() : baseAction.tooltip();
            merged.set(baseIndex, new Action(
                    weaverAction.name(),
                    weaverAction.returnType(),
                    weaverAction.parameters(),
                    effectiveTooltip
            ));
        }

        return List.copyOf(merged);
    }

    private static int findMatchingAttributeIndex(List<Attribute> attributes, Attribute candidate) {
        var candidateSignature = signature(candidate.name(), candidate.parameters());
        for (int i = 0; i < attributes.size(); i++) {
            var current = attributes.get(i);
            if (signature(current.name(), current.parameters()).equals(candidateSignature)) {
                return i;
            }
        }

        return -1;
    }

    private static int findMatchingActionIndex(List<Action> actions, Action candidate) {
        var candidateSignature = signature(candidate.name(), candidate.parameters());
        for (int i = 0; i < actions.size(); i++) {
            var current = actions.get(i);
            if (signature(current.name(), current.parameters()).equals(candidateSignature)) {
                return i;
            }
        }

        return -1;
    }

    private static MemberSignature signature(String name, List<Parameter> parameters) {
        var parameterTypes = parameters.stream().map(Parameter::type).toList();
        return new MemberSignature(name, parameterTypes);
    }

    private record MemberSignature(String name, List<JpDataType> parameterTypes) {}
}
