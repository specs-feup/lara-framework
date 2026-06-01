package org.lara.weavergen2.source;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.lara.langspec2.model.JpClass;
import org.lara.langspec2.model.WeaverModel;

public final class ConcreteSourceValidator {

    private final WeaverModel model;
    private final ConcreteJoinPointNamer namer;
    private final ConcreteSourceIndex index;
    private final ConcreteSourceParser parser;

    public ConcreteSourceValidator(WeaverModel model, ConcreteJoinPointNamer namer, ConcreteSourceIndex index,
            ConcreteSourceParser parser) {
        this.model = model;
        this.namer = namer;
        this.index = index;
        this.parser = parser;
    }

    public void validateDefinition(List<NonConformingConcreteSource> nonConforming, JpClass jp, Path sourceFile)
            throws IOException {
        var concreteClassName = namer.className(jp);
        var expectedDefinition = namer.expectedClassDefinition(jp);
        var actualDefinition = parser.findClassDefinition(sourceFile, concreteClassName);
        if (actualDefinition.isEmpty()) {
            nonConforming.add(new NonConformingConcreteSource(namer.relativePath(sourceFile),
                    "does not declare " + concreteClassName));
            return;
        }

        if (!normalizeDefinition(actualDefinition.get()).equals(normalizeDefinition(expectedDefinition))) {
            nonConforming.add(new NonConformingConcreteSource(namer.relativePath(sourceFile),
                    "has declaration '" + actualDefinition.get() + "' but expected '" + expectedDefinition + "'"));
        }
    }

    public List<NonConformingConcreteSource> duplicateFiles(List<Path> sourceFiles) {
        var relativeFiles = sourceFiles.stream()
                .map(namer::relativePath)
                .toList();

        return sourceFiles.stream()
                .map(path -> new NonConformingConcreteSource(namer.relativePath(path),
                        "duplicates declared joinpoint source file set " + relativeFiles))
                .toList();
    }

    public List<NonConformingConcreteSource> unexpectedJavaFiles() throws IOException {
        var expectedFiles = expectedConcreteFiles();
        try (var paths = Files.walk(namer.root())) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".java"))
                    .filter(path -> !expectedFiles.contains(path.normalize()))
                    .sorted(Comparator.comparing(namer::relativePath))
                    .map(path -> new NonConformingConcreteSource(namer.relativePath(path), "is not declared in the spec"))
                    .toList();
        }
    }

    private Set<Path> expectedConcreteFiles() {
        var expectedFiles = new LinkedHashSet<Path>();
        for (var jp : model.getAllJpClasses()) {
            var sourceFiles = index.filesForClass(namer.className(jp));
            if (sourceFiles.isEmpty()) {
                expectedFiles.add(namer.defaultSourceFile(jp).normalize());
                continue;
            }

            sourceFiles.stream().map(Path::normalize).forEach(expectedFiles::add);
        }

        return expectedFiles;
    }

    private String normalizeDefinition(String definition) {
        return definition.trim().replaceAll("\\s+", " ");
    }
}
