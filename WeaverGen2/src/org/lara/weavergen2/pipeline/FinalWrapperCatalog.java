package org.lara.weavergen2.pipeline;

import java.util.LinkedHashSet;
import java.util.Set;

import org.lara.weavergen2.java.TypeMapper;

public final class FinalWrapperCatalog {

    public Set<String> inheritedFinalWrapperSignatures() {
        var fallback = Set.of(
                TypeMapper.memberAritySignature("node", 0),
                TypeMapper.memberAritySignature("dump", 0),
                TypeMapper.memberAritySignature("joinPointType", 0),
                TypeMapper.memberAritySignature("self", 0),
                TypeMapper.memberAritySignature("children", 0),
                TypeMapper.memberAritySignature("descendants", 0),
                TypeMapper.memberAritySignature("scopeNodes", 0),
                TypeMapper.memberAritySignature("parent", 0),
                TypeMapper.memberAritySignature("root", 0),
                TypeMapper.memberAritySignature("code", 0),
                TypeMapper.memberAritySignature("line", 0),
                TypeMapper.memberAritySignature("column", 0),
                TypeMapper.memberAritySignature("toString", 0),
                TypeMapper.memberAritySignature("equals", 1),
                TypeMapper.memberAritySignature("compareNodes", 1),
                TypeMapper.memberAritySignature("same", 1),
                TypeMapper.memberAritySignature("instanceOf", 1),
                TypeMapper.memberAritySignature("insert", 2));

        try {
            var type = Class.forName("org.lara.interpreter.weaver.interf.abstracts.joinpoints.ALaraJoinPoint");
            var signatures = new LinkedHashSet<String>(fallback);
            for (var method : type.getDeclaredMethods()) {
                var modifiers = method.getModifiers();
                if (java.lang.reflect.Modifier.isPublic(modifiers) && java.lang.reflect.Modifier.isFinal(modifiers)) {
                    signatures.add(TypeMapper.memberAritySignature(method.getName(), method.getParameterCount()));
                }
            }
            return signatures;
        } catch (ClassNotFoundException e) {
            return fallback;
        }
    }
}
