package org.lara.weavergen2.generator;

import org.lara.langspec2.model.*;
import org.lara.langspec2.dsl.WeaverSpec;
import org.lara.langspec2.validation.SpecValidationException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

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

        var mergeErrors = new ArrayList<String>();
        var mergedAttributes = mergeAttributes(baseGlobal.getOwnAttributes(), weaverGlobal.getOwnAttributes(), mergeErrors);
        var mergedActions = mergeActions(baseGlobal.getOwnActions(), weaverGlobal.getOwnActions(), mergeErrors);

        if (!mergeErrors.isEmpty()) {
            throw new SpecValidationException(mergeErrors);
        }

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

    private static List<Attribute> mergeAttributes(List<Attribute> baseAttrs, List<Attribute> weaverAttrs, List<String> errors) {
        var mergedByName = new LinkedHashMap<String, Attribute>();
        for (var attr : baseAttrs) {
            mergedByName.put(attr.name(), attr);
        }

        for (var weaverAttr : weaverAttrs) {
            var baseAttr = mergedByName.get(weaverAttr.name());
            if (baseAttr == null) {
                mergedByName.put(weaverAttr.name(), weaverAttr);
                continue;
            }

            var effectiveTooltip = weaverAttr.tooltip() != null ? weaverAttr.tooltip() : baseAttr.tooltip();
            var mergedAttr = new Attribute(weaverAttr.name(), weaverAttr.type(), weaverAttr.parameters(), effectiveTooltip);

            if (isTrueDuplicate(baseAttr, mergedAttr)) {
                errors.add("Duplicate attribute '" + weaverAttr.name() + "' in join point 'joinpoint'");
                continue;
            }

            mergedByName.put(weaverAttr.name(), mergedAttr);
        }

        return List.copyOf(mergedByName.values());
    }

    private static List<Action> mergeActions(List<Action> baseActions, List<Action> weaverActions, List<String> errors) {
        var mergedByName = new LinkedHashMap<String, Action>();
        for (var action : baseActions) {
            mergedByName.put(action.name(), action);
        }

        for (var weaverAction : weaverActions) {
            var baseAction = mergedByName.get(weaverAction.name());
            if (baseAction == null) {
                mergedByName.put(weaverAction.name(), weaverAction);
                continue;
            }

            var effectiveTooltip = weaverAction.tooltip() != null ? weaverAction.tooltip() : baseAction.tooltip();
            var mergedAction = new Action(
                    weaverAction.name(),
                    weaverAction.returnType(),
                    weaverAction.parameters(),
                    effectiveTooltip
            );

            if (isTrueDuplicate(baseAction, mergedAction)) {
                errors.add("Duplicate action '" + weaverAction.name() + "' in join point 'joinpoint'");
                continue;
            }

            mergedByName.put(weaverAction.name(), mergedAction);
        }

        return List.copyOf(mergedByName.values());
    }

    private static boolean isTrueDuplicate(Attribute baseAttr, Attribute mergedAttr) {
        return Objects.equals(baseAttr.type(), mergedAttr.type())
                && Objects.equals(baseAttr.parameters(), mergedAttr.parameters())
                && Objects.equals(baseAttr.tooltip(), mergedAttr.tooltip());
    }

    private static boolean isTrueDuplicate(Action baseAction, Action mergedAction) {
        return Objects.equals(baseAction.returnType(), mergedAction.returnType())
                && Objects.equals(baseAction.parameters(), mergedAction.parameters())
                && Objects.equals(baseAction.tooltip(), mergedAction.tooltip());
    }
}
