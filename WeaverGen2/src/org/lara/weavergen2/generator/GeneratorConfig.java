package org.lara.weavergen2.generator;

/**
 * Configuration for the code generator.
 */
public record GeneratorConfig(
        String weaverName,
        String basePackage,
        String nodeType,
        boolean generateEvents
) {
    public String weaverClassName() {
        return "A" + weaverName;
    }

    public String abstractsPackage() {
        return basePackage + ".abstracts";
    }

    public String joinPointPackage() {
        return abstractsPackage() + ".joinpoints";
    }

    public String providerPackage() {
        return basePackage + ".providers";
    }

    public String abstractWeaverPackage() {
        return abstractsPackage() + ".weaver";
    }

    public String entitiesPackage() {
        return basePackage + ".entities";
    }

    public String enumsPackage() {
        return basePackage + ".enums";
    }

    public String registryPackage() {
        return basePackage + ".registry";
    }

    public String userAbstractClassName() {
        return "A" + weaverName.replace("Weaver", "") + "WeaverJoinPoint";
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
