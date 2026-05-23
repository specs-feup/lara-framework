package org.lara.weavergen2.generator;

import java.util.Set;

/**
 * Configuration for the code generator.
 */
public record GeneratorConfig(
        String weaverName,
        String basePackage,
        String nodeType,
        boolean generateEvents,
        boolean hasBaseSpec,
        Set<String> baseMemberSignatures
) {
    public String weaverClassName() {
        return hasBaseSpec ? "A" + weaverName : "WeaverEngine";
    }

    public String abstractsPackage() {
        return basePackage + ".abstracts";
    }

    public String joinPointPackage() {
        return abstractsPackage() + ".joinpoints";
    }

    public String abstractWeaverPackage() {
        return hasBaseSpec ? (abstractsPackage() + ".weaver") : "org.lara.interpreter.weaver.interf";
    }

    public String entitiesPackage() {
        return basePackage + ".entities";
    }

    public String enumsPackage() {
        return basePackage + ".enums";
    }

    public String baseJoinPointClass() {
        return "JoinPoint2";
    }

    public String baseJoinPointPackage() {
        return "org.lara.interpreter.weaver.interf";
    }

    public String abstractJpClassName() {
        return "AJoinPoint";
    }
}
