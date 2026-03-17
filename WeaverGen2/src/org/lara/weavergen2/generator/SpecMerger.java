package org.lara.weavergen2.generator;

import org.lara.langspec2.model.*;
import org.lara.langspec2.dsl.WeaverSpec;

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

        // Prepend base attributes (they should come before weaver-specific ones)
        var baseAttrs = baseGlobal.getOwnAttributes();
        var existingAttrs = new java.util.ArrayList<>(weaverGlobal.getOwnAttributes());

        // We need to clear and re-add in order to prepend
        // Since JpClass uses an internal mutable list, we must work around it
        // by creating a merged model manually.
        var mergedGlobal = new JpClass("joinpoint");
        for (var attr : baseAttrs) {
            mergedGlobal.addAttribute(attr);
        }
        for (var attr : existingAttrs) {
            mergedGlobal.addAttribute(attr);
        }
        for (var action : baseGlobal.getOwnActions()) {
            mergedGlobal.addAction(action);
        }
        for (var action : weaverGlobal.getOwnActions()) {
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
}
