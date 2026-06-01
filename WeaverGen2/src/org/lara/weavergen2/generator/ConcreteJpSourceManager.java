package org.lara.weavergen2.generator;

import org.lara.langspec2.model.JpClass;
import org.lara.langspec2.model.WeaverModel;
import org.lara.weavergen2.java.TypeMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Owns the concrete join point source files that live in the user project.
 */
public final class ConcreteJpSourceManager {

    private final WeaverModel model;
    private final GeneratorConfig config;
    private final Path joinpointsRoot;
    private final Map<String, List<Path>> sourceFilesByClassName;

    public ConcreteJpSourceManager(WeaverModel model, GeneratorConfig config) {
        this.model = model;
        this.config = config;
        this.sourceFilesByClassName = new HashMap<>();
        this.joinpointsRoot = config.sourceLookupRoot() == null ? null
                : config.sourceLookupRoot()
                        .resolve(config.basePackage().replace('.', '/'))
                        .resolve("joinpoints");
    }

    public boolean isEnabled() {
        return config.hasBaseSpec() && joinpointsRoot != null;
    }

    public String concreteClassName(JpClass jpClass) {
        return config.prefix() + TypeMapper.capitalize(jpClass.getName());
    }

    public String concretePackage() {
        return config.basePackage() + ".joinpoints";
    }

    public String concreteClassImport(JpClass jpClass) {
        var sourceFile = primaryConcreteSourceFile(jpClass);
        if (!Files.exists(sourceFile)) {
            return concretePackage() + "." + concreteClassName(jpClass);
        }

        return resolvePackageName(sourceFile) + "." + concreteClassName(jpClass);
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

    public List<NonConformingConcreteFile> ensureConcreteSources() throws IOException {
        if (!isEnabled()) {
            return List.of();
        }

        Files.createDirectories(joinpointsRoot);
        discoverConcreteSourceFiles();

        var nonConforming = new ArrayList<NonConformingConcreteFile>();
        for (var jp : model.getAllJpClasses()) {
            var sourceFiles = concreteSourceFiles(jp);
            if (sourceFiles.isEmpty()) {
                createConcreteSourceFile(jp, defaultConcreteSourceFile(jp));
                continue;
            }

            if (sourceFiles.size() > 1) {
                nonConforming.addAll(nonConformingDuplicateFiles(sourceFiles));
            }

            var expectedDefinition = expectedClassDefinition(jp);
            for (var sourceFile : sourceFiles) {
                validateConcreteSourceDefinition(nonConforming, jp, sourceFile, expectedDefinition);
            }
        }

        nonConforming.addAll(findUnexpectedJavaFiles());

        return nonConforming.stream()
                .sorted(Comparator.comparing(NonConformingConcreteFile::path))
                .toList();
    }

    private void discoverConcreteSourceFiles() {
        sourceFilesByClassName.clear();

        if (!Files.exists(joinpointsRoot)) {
            return;
        }

        try (var paths = Files.walk(joinpointsRoot)) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".java"))
                    .sorted(Comparator.comparing(this::relativePath))
                    .forEach(path -> {
                        var className = path.getFileName().toString().replaceFirst("\\.java$", "");
                        sourceFilesByClassName
                                .computeIfAbsent(className, unused -> new ArrayList<>())
                                .add(path);
                    });
        } catch (IOException e) {
            throw new IllegalStateException("Could not search concrete joinpoint sources under '" + joinpointsRoot + "'",
                    e);
        }
    }

    private List<NonConformingConcreteFile> findUnexpectedJavaFiles() throws IOException {
        var expectedFiles = expectedConcreteFiles();
        try (var paths = Files.walk(joinpointsRoot)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".java"))
                    .filter(path -> !expectedFiles.contains(path.normalize()))
                    .sorted(Comparator.comparing(this::relativePath))
                    .map(path -> new NonConformingConcreteFile(relativePath(path), "is not declared in the spec"))
                    .toList();
        }
    }

    private Set<Path> expectedConcreteFiles() {
        var expectedFiles = new LinkedHashSet<Path>();
        for (var jp : model.getAllJpClasses()) {
            var sourceFiles = concreteSourceFiles(jp);
            if (sourceFiles.isEmpty()) {
                expectedFiles.add(defaultConcreteSourceFile(jp).normalize());
                continue;
            }

            sourceFiles.stream().map(Path::normalize).forEach(expectedFiles::add);
        }

        return expectedFiles;
    }

    private void validateConcreteSourceDefinition(List<NonConformingConcreteFile> nonConforming, JpClass jp,
            Path sourceFile, String expectedDefinition) throws IOException {
        var source = Files.readString(sourceFile);
        var actualDefinition = findClassDefinition(source, concreteClassName(jp));
        if (actualDefinition.isEmpty()) {
            nonConforming.add(new NonConformingConcreteFile(relativePath(sourceFile),
                    "does not declare " + concreteClassName(jp)));
            return;
        }

        if (!normalizeDefinition(actualDefinition.get()).equals(normalizeDefinition(expectedDefinition))) {
            nonConforming.add(new NonConformingConcreteFile(relativePath(sourceFile),
                    "has declaration '" + actualDefinition.get() + "' but expected '" + expectedDefinition + "'"));
        }
    }

    private List<NonConformingConcreteFile> nonConformingDuplicateFiles(List<Path> sourceFiles) {
        var relativeFiles = sourceFiles.stream()
                .map(this::relativePath)
                .toList();

        return sourceFiles.stream()
                .map(path -> new NonConformingConcreteFile(relativePath(path),
                        "duplicates declared joinpoint source file set " + relativeFiles))
                .toList();
    }

    private List<Path> concreteSourceFiles(JpClass jpClass) {
        return sourceFilesByClassName.getOrDefault(concreteClassName(jpClass), List.of());
    }

    private Path primaryConcreteSourceFile(JpClass jpClass) {
        var sourceFiles = concreteSourceFiles(jpClass);
        return sourceFiles.isEmpty() ? defaultConcreteSourceFile(jpClass) : sourceFiles.get(0);
    }

    private Path defaultConcreteSourceFile(JpClass jpClass) {
        return joinpointsRoot.resolve(concreteClassName(jpClass) + ".java");
    }

    private void createConcreteSourceFile(JpClass jpClass, Path sourceFile) throws IOException {
        var concreteClassName = concreteClassName(jpClass);
        var nodeTypeImport = abstractConstructorNodeTypeImport(jpClass);
        var source = new StringBuilder();
        source.append("package ").append(concretePackage()).append(";\n\n");
        source.append("import ").append(config.joinPointPackage()).append(".")
                .append(TypeMapper.abstractClassName(jpClass.getName())).append(";\n");
        source.append("import ").append(config.basePackage()).append(".").append(config.weaverName()).append(";\n");
        if (nodeTypeImport.contains(".")) {
            source.append("import ").append(nodeTypeImport).append(";\n");
        }
        source.append("\n");
        source.append(expectedClassDefinition(jpClass)).append(" {\n");
        source.append("    protected ").append(concreteClassName).append("(").append(simpleClassName(nodeTypeImport))
                .append(" node, ").append(config.weaverName()).append(" weaver) {\n");
        source.append("        super(node, weaver);\n");
        source.append("    }\n");
        source.append("}\n");

        Files.writeString(sourceFile, source.toString());
        sourceFilesByClassName.put(concreteClassName, new ArrayList<>(List.of(sourceFile)));
    }

    private String expectedClassDefinition(JpClass jpClass) {
        var concreteClassName = concreteClassName(jpClass);
        var abstractClassName = TypeMapper.abstractClassName(jpClass.getName());

        return "public class " + concreteClassName + "<Self extends " + concreteClassName
                + "<Self>> extends " + abstractClassName + "<Self>";
    }

    private Optional<String> findClassDefinition(String source, String concreteClassName) {
        var matcher = Pattern.compile("\\bpublic\\s+[^\\{]*\\bclass\\s+"
                + Pattern.quote(concreteClassName) + "\\b([^\\{]*)\\{", Pattern.DOTALL).matcher(source);

        if (!matcher.find()) {
            return Optional.empty();
        }

        return Optional.of(source.substring(matcher.start(), matcher.end() - 1).trim().replaceAll("\\s+", " "));
    }

    private String normalizeDefinition(String definition) {
        return definition.trim().replaceAll("\\s+", " ");
    }

    private String resolveConcreteConstructorNodeTypeImport(JpClass concreteJpClass) {
        var concreteClassName = concreteClassName(concreteJpClass);
        var sourceFile = primaryConcreteSourceFile(concreteJpClass);

        try {
            var source = Files.readString(sourceFile);
            var constructorMatcher = Pattern.compile("(?:public|protected)?\\s*" + Pattern.quote(concreteClassName)
                    + "\\s*\\(([^)]*)\\)", Pattern.DOTALL).matcher(source);

            if (!constructorMatcher.find()) {
                throw new IllegalStateException("Could not find constructor signature for concrete joinpoint '"
                        + concreteClassName + "' in source file '" + sourceFile + "'");
            }

            var parameters = constructorMatcher.group(1).trim();
            var firstParameter = parameters.split(",", 2)[0].trim();
            var firstSpace = firstParameter.lastIndexOf(' ');

            if (firstSpace < 0) {
                throw new IllegalStateException("Could not parse first constructor parameter of concrete joinpoint '"
                        + concreteClassName + "' from source file '" + sourceFile + "'");
            }

            var nodeTypeSimpleName = firstParameter.substring(0, firstSpace).trim();
            return resolveTypeImport(source, sourceFile, concreteClassName, nodeTypeSimpleName);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read source file for concrete joinpoint '"
                    + concreteClassName + "' while resolving constructor node type", e);
        }
    }

    private String resolveTypeImport(String source, Path sourceFile, String concreteClassName, String typeName) {
        if (typeName.contains(".")) {
            return typeName;
        }

        if (TypeMapper.isPrimitive(typeName)) {
            return typeName;
        }

        try {
            Class.forName("java.lang." + typeName);
            return "java.lang." + typeName;
        } catch (ClassNotFoundException e) {
            // Not a java.lang type, continue resolving from imports below.
        }

        var importMatcher = Pattern.compile("^import\\s+([^;]+\\." + Pattern.quote(typeName) + ");$",
                Pattern.MULTILINE).matcher(source);

        if (importMatcher.find()) {
            return importMatcher.group(1);
        }

        throw new IllegalStateException("Could not resolve import for constructor node type '" + typeName
                + "' in concrete joinpoint '" + concreteClassName + "' from source file '" + sourceFile + "'");
    }

    private String resolvePackageName(Path sourceFile) {
        try {
            var source = Files.readString(sourceFile);
            var packageMatcher = Pattern.compile("^package\\s+([^;]+);$", Pattern.MULTILINE).matcher(source);

            if (!packageMatcher.find()) {
                throw new IllegalStateException(
                        "Could not find package declaration in source file '" + sourceFile + "'");
            }

            return packageMatcher.group(1);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read source file '" + sourceFile + "'", e);
        }
    }

    private String simpleClassName(String fullyQualifiedName) {
        var lastDot = fullyQualifiedName.lastIndexOf('.');
        return lastDot < 0 ? fullyQualifiedName : fullyQualifiedName.substring(lastDot + 1);
    }

    private String relativePath(Path path) {
        return normalizeRelativePath(joinpointsRoot.relativize(path));
    }

    private static String normalizeRelativePath(Path path) {
        return path.toString().replace(File.separatorChar, '/');
    }

    public record NonConformingConcreteFile(String path, String reason) {
    }
}
