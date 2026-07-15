package org.lara.weavergen2.source;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.regex.Pattern;

import org.lara.weavergen2.java.TypeMapper;

public final class ConcreteSourceParser {

    public Optional<String> findClassDefinition(Path sourceFile, String concreteClassName) throws IOException {
        var source = Files.readString(sourceFile);
        var matcher = Pattern.compile("\\bpublic\\s+[^\\{]*\\bclass\\s+"
                + Pattern.quote(concreteClassName) + "\\b([^\\{]*)\\{", Pattern.DOTALL).matcher(source);

        if (!matcher.find()) {
            return Optional.empty();
        }

        return Optional.of(source.substring(matcher.start(), matcher.end() - 1).trim().replaceAll("\\s+", " "));
    }

    public String resolveConstructorNodeTypeImport(Path sourceFile, String concreteClassName) {
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

    public String resolvePackageName(Path sourceFile) {
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
}
