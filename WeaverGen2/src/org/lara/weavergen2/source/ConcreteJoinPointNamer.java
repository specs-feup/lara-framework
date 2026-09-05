package org.lara.weavergen2.source;

import java.nio.file.Path;

import org.lara.langspec2.model.JpClass;
import org.lara.weavergen2.model.GenerationProfile;
import org.lara.weavergen2.java.TypeMapper;

public final class ConcreteJoinPointNamer {

    private final GenerationProfile config;
    private final Path root;

    public ConcreteJoinPointNamer(GenerationProfile config) {
        this.config = config;
        this.root = config.sourceLookupRoot() == null ? null
                : config.sourceLookupRoot()
                        .resolve(config.basePackage().replace('.', '/'))
                        .resolve("joinpoints");
    }

    public Path root() {
        return root;
    }

    public String className(JpClass jpClass) {
        return config.prefix() + TypeMapper.capitalize(jpClass.getName());
    }

    public String packageName() {
        return config.basePackage() + ".joinpoints";
    }

    public Path defaultSourceFile(JpClass jpClass) {
        return root.resolve(className(jpClass) + ".java");
    }

    public String expectedClassDefinition(JpClass jpClass) {
        var concreteClassName = className(jpClass);
        var abstractClassName = TypeMapper.abstractClassName(jpClass.getName());

        return "public class " + concreteClassName + "<Self extends " + concreteClassName
                + "<Self>> extends " + abstractClassName + "<Self>";
    }

    public String relativePath(Path path) {
        return normalizeRelativePath(root.relativize(path));
    }

    public static String normalizeRelativePath(Path path) {
        return path.toString().replace(java.io.File.separatorChar, '/');
    }
}
