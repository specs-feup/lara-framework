package org.lara.weavergen2.generator;

import org.lara.langspec2.model.*;
import org.lara.weavergen2.java.JavaSourceBuilder;
import org.lara.weavergen2.java.TypeMapper;

/**
 * Generates the abstract weaver class (e.g., ACxxWeaver).
 */
public final class WeaverAbstractGenerator {

    private final WeaverModel model;
    private final GeneratorConfig config;

    public WeaverAbstractGenerator(WeaverModel model, GeneratorConfig config) {
        this.model = model;
        this.config = config;
    }

    public String generate() {
        var sb = new JavaSourceBuilder();

        sb.line("package " + config.abstractWeaverPackage() + ";");
        sb.line();
        sb.line("import " + config.joinPointPackage() + ".*;");
        sb.line("import org.lara.langspec2.model.ProviderRegistry;");
        sb.line("import org.lara.interpreter.weaver.interf.WeaverEngine;");
        sb.line();
        sb.line("import java.util.*;");
        sb.line();

        var className = "A" + config.weaverName();

        sb.line("/**");
        sb.line(" * Auto-generated abstract weaver class for " + config.weaverName() + ".");
        sb.line(" */");
        sb.openBlock("public abstract class " + className + " extends WeaverEngine");
        sb.line();

        sb.line("private final ProviderRegistry providerRegistry = new ProviderRegistry();");
        sb.line();

        // getProviderRegistry
        sb.openBlock("public ProviderRegistry getProviderRegistry()");
        sb.line("return providerRegistry;");
        sb.closeBlock();
        sb.line();

        // registerProviders - abstract, user implements
        sb.line("/**");
        sb.line(" * Override this to register all provider implementations.");
        sb.line(" */");
        sb.line("protected abstract void registerProviders(ProviderRegistry registry);");
        sb.line();

        // getJoinPointClassNames
        sb.openBlock("public List<String> getJoinPointClassNames()");
        sb.line("return List.of(");
        var allClasses = model.getAllJpClasses();
        for (int i = 0; i < allClasses.size(); i++) {
            var comma = i < allClasses.size() - 1 ? "," : "";
            sb.line("    \"" + allClasses.get(i).getName() + "\"" + comma);
        }
        sb.line(");");
        sb.closeBlock();
        sb.line();

        // getRoot
        var rootName = model.getRoot().map(JpClass::getName).orElse("joinpoint");
        sb.line("@Override");
        sb.openBlock("public String getRoot()");
        sb.line("return \"" + rootName + "\";");
        sb.closeBlock();

        sb.closeBlock(); // class

        return sb.toString();
    }
}
