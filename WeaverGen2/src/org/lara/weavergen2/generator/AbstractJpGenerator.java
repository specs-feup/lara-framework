package org.lara.weavergen2.generator;

import org.lara.langspec2.model.*;
import org.lara.langspec2.types.JpDataType;
import org.lara.weavergen2.emit.JoinPointClassDeclarationEmitter;
import org.lara.weavergen2.emit.JoinPointConstructorEmitter;
import org.lara.weavergen2.emit.JoinPointHierarchy;
import org.lara.weavergen2.emit.JoinPointImportEmitter;
import org.lara.weavergen2.emit.JoinPointTypeRenderer;
import org.lara.weavergen2.emit.PublicWrapperEmitter;
import org.lara.weavergen2.java.JavaSourceBuilder;
import org.lara.weavergen2.java.TypeMapper;
import org.lara.weavergen2.model.GenerationProfile;
import org.lara.weavergen2.source.ConcreteJoinPointSources;

import java.util.*;

/**
 * Generates an abstract join point class from a {@link JpClass}.
 * <p>
 * This generates abstract classes where inherited methods are resolved through
 * the Java class hierarchy.
 */
public final class AbstractJpGenerator {

    private final JpClass jpClass;
    private final WeaverModel model;
    private final GenerationProfile config;
    private final JoinPointTypeRenderer types;
    private final PublicWrapperEmitter wrappers;
    private final JoinPointHierarchy hierarchy;
    private final JoinPointClassDeclarationEmitter declaration;

    public AbstractJpGenerator(JpClass jpClass, WeaverModel model, GenerationProfile config) {
        this(jpClass, model, config, new ConcreteJoinPointSources(model, config,
                org.lara.weavergen2.api.ConcreteSourcePolicy.CREATE_MISSING_AND_VALIDATE));
    }

    public AbstractJpGenerator(JpClass jpClass, WeaverModel model, GenerationProfile config,
            ConcreteJoinPointSources concreteSources) {
        this.jpClass = jpClass;
        this.model = model;
        this.config = config;
        this.types = new JoinPointTypeRenderer(model, config);
        this.wrappers = new PublicWrapperEmitter(types);
        this.hierarchy = new JoinPointHierarchy(jpClass, model, config, concreteSources);
        this.declaration = new JoinPointClassDeclarationEmitter(jpClass, model, config, hierarchy);
    }

    public String generate() {
        var sb = new JavaSourceBuilder();
        var standaloneMode = !config.hasBaseSpec();

        // Package
        sb.line("package " + config.joinPointPackage() + ";");
        sb.line();

        new JoinPointImportEmitter(jpClass, model, config, hierarchy).emit(sb);
        declaration.open(sb);
        new JoinPointConstructorEmitter(jpClass, model, config, declaration, hierarchy).emit(sb);

        // Own attributes
        for (var attr : jpClass.getOwnAttributes()) {
            generateOwnAttribute(sb, attr);
        }

        // Own actions
        for (var action : jpClass.getOwnActions()) {
            generateOwnAction(sb, action);
        }

        // get_class()
        if (!standaloneMode) {
            sb.line("@Override");
        }
        sb.openBlock("public String get_class()");
        sb.line("return \"" + jpClass.getName() + "\";");
        sb.closeBlock();
        sb.line();

        // instanceOf()
        if (!standaloneMode) {
            generateInstanceOf(sb);
        }

        // Array factories
        if (standaloneMode) {
            sb.line("protected abstract IntFunction<Self[]> selfTypeArrayFactory();");
            sb.line();
            sb.line("protected abstract IntFunction<Jp[]> jpTypeArrayFactory();");
        } else {
            sb.line();
            sb.line("@SuppressWarnings(\"unchecked\")");
            sb.line("@Override");
            sb.openBlock("protected IntFunction<Self[]> selfTypeArrayFactory()");
            sb.line("return size -> (Self[]) new " + declaration.className() + "[size];");
            sb.closeBlock();
            if (jpClass == model.getGlobal()) {
                sb.line();
                sb.line("@Override");
                sb.openBlock("protected final IntFunction<" + declaration.className() + "<?>[]> jpTypeArrayFactory()");
                sb.line("return size -> new " + declaration.className() + "[size];");
                sb.closeBlock();
            }
        }

        sb.closeBlock(); // class

        return sb.toString();
    }

    private void generateOwnAttribute(JavaSourceBuilder sb, Attribute attr) {
        generateOwnMethod(sb, attr.name(), attr.type(), attr.parameters(), true);
    }

    private void generateOwnAction(JavaSourceBuilder sb, Action action) {
        generateOwnMethod(sb, action.name(), action.returnType(), action.parameters(), false);
    }

    private void generateOwnMethod(JavaSourceBuilder sb, String methodName, JpDataType returnType,
            List<Parameter> parameters, boolean isAttribute) {
        var javaRetType = types.mapReturnType(returnType);
        var finalName = isAttribute ? ("get" + TypeMapper.capitalize(methodName)) : methodName;
        var implMethodName = finalName + "Impl";

        var params = types.formatImplParams(parameters);

        sb.line("public abstract " + javaRetType + " " + implMethodName + "(" + params + ");");
        sb.line();

        if (shouldGenerateWrapper(methodName, parameters)) {
            wrappers.emit(sb, methodName, finalName, returnType, parameters, isAttribute);
        }
    }

    private void generateInstanceOf(JavaSourceBuilder sb) {
        var chain = jpClass.getAncestorChain();

        sb.line("@Override");
        sb.openBlock("public boolean getInstanceOfImpl(String joinpointClassname)");

        var checks = chain.stream()
                .map(jp -> "\"" + jp.getName() + "\".equals(joinpointClassname)")
                .toList();

        sb.line("return " + String.join("\n" + sb.getIndentStr() + "    || ", checks) + ";");
        sb.closeBlock();
    }

    private boolean shouldGenerateWrapper(String name, List<Parameter> params) {
        if (config.hasBaseSpec()
                && config.inheritedFinalWrapperSignatures()
                .contains(TypeMapper.memberAritySignature(TypeMapper.sanitizeJavaIdentifier(name), params.size()))) {
            return false;
        }

        var signature = TypeMapper.memberSignature(name, params);

        if (config.baseMemberSignatures().contains(signature)) {
            return false;
        }

        var parent = jpClass.getParent().orElse(null);
        while (parent != null) {
            if (hasMatchingSignature(parent.getOwnAttributes(), signature)
                    || hasMatchingSignature(parent.getOwnActions(), signature)) {
                return false;
            }

            parent = parent.getParent().orElse(null);
        }

        return true;
    }

    private boolean hasMatchingSignature(List<?> members, String signature) {
        for (var member : members) {
            if (member instanceof Attribute attr) {
                if (TypeMapper.memberSignature(attr.name(), attr.parameters()).equals(signature)) {
                    return true;
                }
                continue;
            }

            if (member instanceof Action action
                    && TypeMapper.memberSignature(action.name(), action.parameters()).equals(signature)) {
                return true;
            }
        }

        return false;
    }
}
