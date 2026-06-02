package org.lara.weavergen2.generator;

import org.lara.langspec2.model.JpClass;
import org.lara.langspec2.model.WeaverModel;
import org.lara.weavergen2.emit.JoinPointClassDeclarationEmitter;
import org.lara.weavergen2.emit.JoinPointConstructorEmitter;
import org.lara.weavergen2.emit.JoinPointHierarchy;
import org.lara.weavergen2.emit.JoinPointImportEmitter;
import org.lara.weavergen2.emit.JoinPointMemberEmitter;
import org.lara.weavergen2.emit.JoinPointTypeRenderer;
import org.lara.weavergen2.java.JavaSourceBuilder;
import org.lara.weavergen2.model.GenerationProfile;
import org.lara.weavergen2.source.ConcreteJoinPointSources;

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
    private final JoinPointHierarchy hierarchy;
    private final JoinPointClassDeclarationEmitter declaration;
    private final JoinPointMemberEmitter members;

    public AbstractJpGenerator(JpClass jpClass, WeaverModel model, GenerationProfile config) {
        this(jpClass, model, config, new ConcreteJoinPointSources(model, config,
                org.lara.weavergen2.api.ConcreteSourcePolicy.CREATE_MISSING_AND_VALIDATE));
    }

    public AbstractJpGenerator(JpClass jpClass, WeaverModel model, GenerationProfile config,
            ConcreteJoinPointSources concreteSources) {
        this.jpClass = jpClass;
        this.model = model;
        this.config = config;
        var types = new JoinPointTypeRenderer(model, config);
        this.hierarchy = new JoinPointHierarchy(jpClass, model, config, concreteSources);
        this.declaration = new JoinPointClassDeclarationEmitter(jpClass, model, config, hierarchy);
        this.members = new JoinPointMemberEmitter(jpClass, config, types);
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

        members.emit(sb);

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

    private void generateInstanceOf(JavaSourceBuilder sb) {
        var chain = jpClass.getAncestorChain();

        sb.line("@Override");
        sb.openBlock("public boolean getInstanceOfImpl(String joinpointClassname)");

        var checks = chain.stream()
                .map(jp -> "\"" + jp.getName() + "\".equals(joinpointClassname)")
                .toList();

        sb.line("return " + String.join("\n" + sb.indentStr() + "    || ", checks) + ";");
        sb.closeBlock();
    }
}
