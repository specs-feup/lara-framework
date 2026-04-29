package org.lara.weavergen2.generator;

import org.lara.langspec2.model.*;
import org.lara.weavergen2.java.JavaSourceBuilder;

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
        sb.line("import org.lara.interpreter.weaver.LaraWeaverEngine;");
        sb.line();
        sb.line("import java.util.*;");
        sb.line();

        var className = "A" + config.weaverName();

        sb.line("/**");
        sb.line(" * Auto-generated abstract weaver class for " + config.weaverName() + ".");
        sb.line(" */");
        sb.openBlock("public abstract class " + className + " extends LaraWeaverEngine");
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
        sb.line();

        sb.openBlock("public boolean implementsEvents()");
        sb.line("return true;");
        sb.closeBlock();

        sb.closeBlock(); // class

        return sb.toString();
    }
}
