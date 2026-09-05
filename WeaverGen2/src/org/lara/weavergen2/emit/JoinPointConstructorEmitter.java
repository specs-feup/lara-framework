package org.lara.weavergen2.emit;

import org.lara.langspec2.model.JpClass;
import org.lara.langspec2.model.WeaverModel;
import org.lara.weavergen2.java.JavaSourceBuilder;
import org.lara.weavergen2.model.GenerationProfile;

public final class JoinPointConstructorEmitter {

    private final JpClass jpClass;
    private final WeaverModel model;
    private final GenerationProfile profile;
    private final JoinPointClassDeclarationEmitter declaration;
    private final JoinPointHierarchy hierarchy;

    public JoinPointConstructorEmitter(JpClass jpClass, WeaverModel model, GenerationProfile profile,
            JoinPointClassDeclarationEmitter declaration, JoinPointHierarchy hierarchy) {
        this.jpClass = jpClass;
        this.model = model;
        this.profile = profile;
        this.declaration = declaration;
        this.hierarchy = hierarchy;
    }

    public void emit(JavaSourceBuilder sb) {
        if (!profile.hasBaseSpec()) {
            emitStandalone(sb);
            return;
        }

        emitBaseSpecMode(sb);
    }

    private void emitStandalone(JavaSourceBuilder sb) {
        sb.line("private final WeaverEngine weaver;");
        sb.line();
        sb.openBlock("protected " + declaration.className() + "(WeaverEngine weaver)");
        sb.line("this.weaver = weaver;");
        sb.closeBlock();
        sb.line();
        sb.openBlock("public WeaverEngine getWeaverEngine()");
        sb.line("return weaver;");
        sb.closeBlock();
        sb.line();
    }

    private void emitBaseSpecMode(JavaSourceBuilder sb) {
        var nodeType = hierarchy.constructorNodeType();

        if (jpClass == model.getGlobal()) {
            sb.line("/**");
            sb.line(" *  FIXME: This should be a private field");
            sb.line(" */ ");
            sb.line("@Deprecated");
            sb.line("protected " + nodeType + " node;");
            sb.line();
            sb.openBlock("public " + declaration.className() + "(" + nodeType + " node, "
                    + profile.weaverName() + " weaver)");
            sb.line("super(weaver);");
            sb.line("this.node = node;");
            sb.closeBlock();
            sb.line();

            sb.openBlock("public " + nodeType + " getNodeImpl()");
            sb.line("return node;");
            sb.closeBlock();
            sb.line();
            return;
        }

        sb.openBlock("public " + declaration.className() + "(" + nodeType + " node, " + profile.weaverName()
                + " weaver)");
        sb.line("super(node, weaver);");
        sb.closeBlock();
        sb.line();
    }
}
