package org.lara.weavergen2.emit;

import java.util.Optional;

import org.lara.langspec2.model.JpClass;
import org.lara.langspec2.model.WeaverModel;
import org.lara.weavergen2.model.GenerationProfile;
import org.lara.weavergen2.source.ConcreteJoinPointSources;

public final class JoinPointHierarchy {

    private final JpClass jpClass;
    private final WeaverModel model;
    private final GenerationProfile profile;
    private final ConcreteJoinPointSources concreteSources;

    public JoinPointHierarchy(JpClass jpClass, WeaverModel model, GenerationProfile profile,
            ConcreteJoinPointSources concreteSources) {
        this.jpClass = jpClass;
        this.model = model;
        this.profile = profile;
        this.concreteSources = concreteSources;
    }

    public String concreteSuperclass() {
        if (jpClass == model.getGlobal()) {
            return profile.baseJoinPointClass();
        }

        var parent = jpClass.getParent().orElseThrow();
        return concreteSources.concreteClassName(parent);
    }

    public Optional<String> concreteSuperclassImport() {
        if (jpClass == model.getGlobal()) {
            return Optional.empty();
        }

        var parent = jpClass.getParent().orElseThrow();
        return Optional.of(concreteSources.concreteClassImport(parent));
    }

    public String constructorNodeType() {
        if (hasConcreteSuperclassNodeTypeCast()) {
            return concreteSources.abstractConstructorNodeType(jpClass);
        }

        return simpleClassName(profile.nodeType());
    }

    public Optional<String> constructorNodeTypeImport() {
        return Optional.of(concreteSources.abstractConstructorNodeTypeImport(jpClass));
    }

    public boolean hasConcreteSuperclassNodeTypeCast() {
        return jpClass.getParent().map(parent -> !parent.equals(model.getGlobal())).orElse(false);
    }

    private String simpleClassName(String fullyQualifiedName) {
        var lastDot = fullyQualifiedName.lastIndexOf('.');
        return lastDot < 0 ? fullyQualifiedName : fullyQualifiedName.substring(lastDot + 1);
    }
}
