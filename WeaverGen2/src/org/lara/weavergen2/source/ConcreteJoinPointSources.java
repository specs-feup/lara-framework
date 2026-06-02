package org.lara.weavergen2.source;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.lara.langspec2.model.JpClass;
import org.lara.langspec2.model.WeaverModel;
import org.lara.weavergen2.api.ConcreteSourcePolicy;
import org.lara.weavergen2.model.GenerationProfile;

public final class ConcreteJoinPointSources {

    private final WeaverModel model;
    private final GenerationProfile config;
    private final ConcreteSourcePolicy policy;
    private final ConcreteJoinPointNamer namer;
    private final ConcreteSourceIndex index;
    private final ConcreteSourceParser parser;
    private final ConcreteSourceStubFactory stubFactory;
    private final ConcreteSourceValidator validator;

    public ConcreteJoinPointSources(WeaverModel model, GenerationProfile config, ConcreteSourcePolicy policy) {
        this.model = model;
        this.config = config;
        this.policy = policy;
        this.namer = new ConcreteJoinPointNamer(config);
        this.index = new ConcreteSourceIndex(namer);
        this.parser = new ConcreteSourceParser();
        this.stubFactory = new ConcreteSourceStubFactory(config, namer);
        this.validator = new ConcreteSourceValidator(model, namer, index, parser);
    }

    public boolean isEnabled() {
        return policy != ConcreteSourcePolicy.DISABLED && config.hasBaseSpec() && namer.root() != null;
    }

    public String concreteClassName(JpClass jpClass) {
        return namer.className(jpClass);
    }

    public String concretePackage() {
        return namer.packageName();
    }

    public String concreteClassImport(JpClass jpClass) {
        var sourceFile = primaryConcreteSourceFile(jpClass);
        if (!Files.exists(sourceFile)) {
            return concretePackage() + "." + concreteClassName(jpClass);
        }

        return parser.resolvePackageName(sourceFile) + "." + concreteClassName(jpClass);
    }

    public String abstractConstructorNodeType(JpClass jpClass) {
        return simpleClassName(abstractConstructorNodeTypeImport(jpClass));
    }

    public String abstractConstructorNodeTypeImport(JpClass jpClass) {
        if (jpClass == model.getGlobal()
                || jpClass.getParent().map(parent -> parent.equals(model.getGlobal())).orElse(false)) {
            return config.nodeType();
        }

        return resolveConcreteConstructorNodeTypeImport(jpClass.getParent().orElseThrow());
    }

    public ConcreteSourceSync ensureConcreteSources() throws IOException {
        if (!isEnabled()) {
            return new ConcreteSourceSync(List.of(), List.of());
        }

        var createdFiles = new ArrayList<Path>();
        Files.createDirectories(namer.root());
        index.scan();

        var nonConforming = new ArrayList<NonConformingConcreteSource>();
        for (var jp : model.getAllJpClasses()) {
            var sourceFiles = concreteSourceFiles(jp);
            if (sourceFiles.isEmpty()) {
                createMissingSource(jp, createdFiles);
                continue;
            }

            if (sourceFiles.size() > 1) {
                nonConforming.addAll(validator.duplicateFiles(sourceFiles));
            }

            for (var sourceFile : sourceFiles) {
                validator.validateDefinition(nonConforming, jp, sourceFile);
            }
        }

        nonConforming.addAll(validator.unexpectedJavaFiles());

        var sortedNonConforming = nonConforming.stream()
                .sorted(Comparator.comparing(NonConformingConcreteSource::path))
                .toList();

        return new ConcreteSourceSync(createdFiles, sortedNonConforming);
    }

    private void createMissingSource(JpClass jp, List<Path> createdFiles) throws IOException {
        if (policy != ConcreteSourcePolicy.CREATE_MISSING_AND_VALIDATE) {
            return;
        }

        var sourceFile = namer.defaultSourceFile(jp);
        stubFactory.create(jp, sourceFile, abstractConstructorNodeTypeImport(jp));
        createdFiles.add(sourceFile);
        index.putCreatedFile(concreteClassName(jp), sourceFile);
    }

    private List<Path> concreteSourceFiles(JpClass jpClass) {
        return index.filesForClass(concreteClassName(jpClass));
    }

    private Path primaryConcreteSourceFile(JpClass jpClass) {
        var sourceFiles = concreteSourceFiles(jpClass);
        return sourceFiles.isEmpty() ? namer.defaultSourceFile(jpClass) : sourceFiles.get(0);
    }

    private String resolveConcreteConstructorNodeTypeImport(JpClass concreteJpClass) {
        return parser.resolveConstructorNodeTypeImport(primaryConcreteSourceFile(concreteJpClass),
                concreteClassName(concreteJpClass));
    }

    private String simpleClassName(String fullyQualifiedName) {
        var lastDot = fullyQualifiedName.lastIndexOf('.');
        return lastDot < 0 ? fullyQualifiedName : fullyQualifiedName.substring(lastDot + 1);
    }
}
