package org.lara.weavergen2.model;

import java.util.Set;
import java.nio.file.Path;

/**
 * Configuration for the code generator.
 */
public record GenerationProfile(
        String prefix,
        String basePackage,
        String nodeType,
        boolean generateEvents,
        boolean hasBaseSpec,
        Set<MemberSignature> baseMemberSignatures,
        Set<WrapperSignature> inheritedFinalWrapperSignatures,
        Path projectRoot
) {
    public String weaverName() {
        return hasBaseSpec ? prefix + "Weaver" : "WeaverEngine";
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

    public Path sourceLookupRoot() {
        return projectRoot;
    }
}
