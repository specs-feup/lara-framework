package org.lara.weavergen2.emit;

import org.lara.langspec2.model.JpClass;
import org.lara.langspec2.model.WeaverModel;
import org.lara.weavergen2.java.JavaSourceBuilder;
import org.lara.weavergen2.model.GenerationProfile;

public final class JoinPointImportEmitter {

    private final JpClass jpClass;
    private final WeaverModel model;
    private final GenerationProfile profile;
    private final JoinPointHierarchy hierarchy;

    public JoinPointImportEmitter(JpClass jpClass, WeaverModel model, GenerationProfile profile,
            JoinPointHierarchy hierarchy) {
        this.jpClass = jpClass;
        this.model = model;
        this.profile = profile;
        this.hierarchy = hierarchy;
    }

    public void emit(JavaSourceBuilder sb) {
        var standaloneMode = !profile.hasBaseSpec();

        if (standaloneMode) {
            sb.line("import " + profile.abstractWeaverPackage() + "." + profile.weaverName() + ";");
        }
        if (!standaloneMode) {
            sb.line("import " + profile.baseJoinPointPackage() + ".*;");
            sb.line("import " + profile.basePackage() + "." + profile.weaverName() + ";");
            hierarchy.concreteSuperclassImport().ifPresent(importName -> sb.line("import " + importName + ";"));
            sb.line("import " + profile.nodeType() + ";");
            var constructorNodeImport = hierarchy.constructorNodeTypeImport();
            if (constructorNodeImport.isPresent() && !constructorNodeImport.get().equals(profile.nodeType())) {
                sb.line("import " + constructorNodeImport.get() + ";");
            }
        }
        if (!model.getTypeDefs().isEmpty()) {
            sb.line("import " + profile.entitiesPackage() + ".*;");
        }
        if (!model.getEnumDefs().isEmpty()) {
            sb.line("import " + profile.enumsPackage() + ".*;");
        }
        if (!jpClass.getOwnActions().isEmpty()) {
            sb.line("import org.lara.interpreter.exception.ActionException;");
        }
        if (!jpClass.getOwnAttributes().isEmpty()) {
            sb.line("import org.lara.interpreter.exception.AttributeException;");
        }
        if (!jpClass.getOwnActions().isEmpty() || !jpClass.getOwnAttributes().isEmpty()) {
            sb.line("import org.lara.interpreter.weaver.interf.events.Stage;");
            sb.line("import java.util.Optional;");
        }
        sb.line();
        sb.line("import java.util.*;");
        sb.line("import java.util.function.IntFunction;");
        sb.line();
    }
}
