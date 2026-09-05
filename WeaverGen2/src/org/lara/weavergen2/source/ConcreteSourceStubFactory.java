package org.lara.weavergen2.source;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.lara.langspec2.model.JpClass;
import org.lara.weavergen2.model.GenerationProfile;
import org.lara.weavergen2.java.TypeMapper;

public final class ConcreteSourceStubFactory {

    private final GenerationProfile config;
    private final ConcreteJoinPointNamer namer;

    public ConcreteSourceStubFactory(GenerationProfile config, ConcreteJoinPointNamer namer) {
        this.config = config;
        this.namer = namer;
    }

    public void create(JpClass jpClass, Path sourceFile, String nodeTypeImport) throws IOException {
        var concreteClassName = namer.className(jpClass);
        var source = new StringBuilder();
        source.append("package ").append(namer.packageName()).append(";\n\n");
        source.append("import ").append(config.joinPointPackage()).append(".")
                .append(TypeMapper.abstractClassName(jpClass.getName())).append(";\n");
        source.append("import ").append(config.basePackage()).append(".").append(config.weaverName()).append(";\n");
        if (nodeTypeImport.contains(".")) {
            source.append("import ").append(nodeTypeImport).append(";\n");
        }
        source.append("\n");
        source.append(namer.expectedClassDefinition(jpClass)).append(" {\n");
        source.append("    protected ").append(concreteClassName).append("(").append(simpleClassName(nodeTypeImport))
                .append(" node, ").append(config.weaverName()).append(" weaver) {\n");
        source.append("        super(node, weaver);\n");
        source.append("    }\n");
        source.append("}\n");

        Files.writeString(sourceFile, source.toString());
    }

    private String simpleClassName(String fullyQualifiedName) {
        var lastDot = fullyQualifiedName.lastIndexOf('.');
        return lastDot < 0 ? fullyQualifiedName : fullyQualifiedName.substring(lastDot + 1);
    }
}
