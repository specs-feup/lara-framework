package org.lara.weavergen2.pipeline;

import java.util.LinkedHashSet;
import java.util.Set;

import org.lara.weavergen2.model.WrapperSignature;

public final class FinalWrapperCatalog {

    public Set<WrapperSignature> inheritedFinalWrapperSignatures() {
        var fallback = Set.of(
                new WrapperSignature("node", 0),
                new WrapperSignature("dump", 0),
                new WrapperSignature("joinPointType", 0),
                new WrapperSignature("self", 0),
                new WrapperSignature("children", 0),
                new WrapperSignature("descendants", 0),
                new WrapperSignature("scopeNodes", 0),
                new WrapperSignature("parent", 0),
                new WrapperSignature("root", 0),
                new WrapperSignature("code", 0),
                new WrapperSignature("line", 0),
                new WrapperSignature("column", 0),
                new WrapperSignature("toString", 0),
                new WrapperSignature("equals", 1),
                new WrapperSignature("compareNodes", 1),
                new WrapperSignature("same", 1),
                new WrapperSignature("instanceOf", 1),
                new WrapperSignature("insert", 2));

        try {
            var type = Class.forName("org.lara.interpreter.weaver.interf.abstracts.joinpoints.ALaraJoinPoint");
            var signatures = new LinkedHashSet<WrapperSignature>(fallback);
            for (var method : type.getDeclaredMethods()) {
                var modifiers = method.getModifiers();
                if (java.lang.reflect.Modifier.isPublic(modifiers) && java.lang.reflect.Modifier.isFinal(modifiers)) {
                    signatures.add(new WrapperSignature(method.getName(), method.getParameterCount()));
                }
            }
            return signatures;
        } catch (ClassNotFoundException e) {
            return fallback;
        }
    }
}
