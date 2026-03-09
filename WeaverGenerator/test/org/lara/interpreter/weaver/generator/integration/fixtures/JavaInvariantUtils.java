package org.lara.interpreter.weaver.generator.integration.fixtures;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.lara.interpreter.weaver.generator.fixtures.JavaMethodSignatureUtils;

public final class JavaInvariantUtils {

    private static final Pattern TOP_LEVEL_TYPE_PATTERN = Pattern.compile(
            "(?m)^\\s*(?:public|protected|private|abstract|final|static|strictfp|sealed|non-sealed\\s+)*\\s*(class|interface|enum|record)\\s+([A-Za-z_][A-Za-z0-9_]*)\\s*([^\\{;]*)\\{");

    private JavaInvariantUtils() {
    }

    public static JavaSnapshot computeSnapshot(Path root, String scenarioPackage, String weaverName) throws IOException {
        List<Path> javaFiles;
        try (Stream<Path> walk = Files.walk(root)) {
            javaFiles = walk.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".java"))
                    .sorted()
                    .collect(Collectors.toList());
        }

        Map<String, Long> structure = new TreeMap<>();
        structure.put("totalJavaFiles", (long) javaFiles.size());
        structure.put("abstractJoinpointFiles", countContains(javaFiles, "/abstracts/joinpoints/"));
        structure.put("enumFiles", countContains(javaFiles, "/enums/"));
        structure.put("entityFiles", countContains(javaFiles, "/entities/"));
        structure.put("exceptionFiles", countContains(javaFiles, "/exceptions/"));
        structure.put("weaverFiles", countFileName(javaFiles, weaverName + ".java"));
        structure.put("dottyFiles", countContains(listAllFiles(root), ".dotty"));
        structure.put("jsonFiles", countContains(listAllFiles(root), ".json"));

        List<JavaDeclarationSignature> signatures = new ArrayList<>();
        for (Path file : javaFiles) {
            String relative = normalize(root.relativize(file).toString());
            String source = Files.readString(file, StandardCharsets.UTF_8);
            signatures.addAll(extractDeclarations(relative, source));
        }

        signatures.sort(Comparator.comparing(JavaDeclarationSignature::normalized));
        String signatureText = signatures.stream()
                .map(JavaDeclarationSignature::normalized)
                .collect(Collectors.joining("\n"));
        String apiHash = ManifestUtils.sha256Hex(signatureText.getBytes(StandardCharsets.UTF_8));

        JavaMethodSignatureUtils.assertNoMethodSignatureCollisions(root, "large-integration");

        assertThat(signatures)
                .as("Java declaration signatures should not be empty")
                .isNotEmpty();

        assertThat(signatures.stream().anyMatch(sig -> sig.signature().contains(scenarioPackage + "." + weaverName)
                || sig.signature().contains(weaverName)))
                .as("Signatures should include the generated weaver class")
                .isTrue();

        return new JavaSnapshot(structure, signatures, apiHash);
    }

    public static void assertApiSignaturesEqual(List<JavaDeclarationSignature> expected,
            List<JavaDeclarationSignature> actual) {

        Set<String> expectedSet = expected.stream()
                .map(JavaDeclarationSignature::normalized)
                .collect(Collectors.toCollection(HashSet::new));
        Set<String> actualSet = actual.stream()
                .map(JavaDeclarationSignature::normalized)
                .collect(Collectors.toCollection(HashSet::new));

        List<String> added = actualSet.stream().filter(sig -> !expectedSet.contains(sig)).sorted().toList();
        List<String> removed = expectedSet.stream().filter(sig -> !actualSet.contains(sig)).sorted().toList();

        assertThat(added)
                .as("Added Java declaration signatures")
                .isEmpty();

        assertThat(removed)
                .as("Removed Java declaration signatures")
                .isEmpty();
    }

    public static void writeSignatures(Path file, List<JavaDeclarationSignature> signatures) throws IOException {
        Files.createDirectories(file.getParent());
        List<String> lines = signatures.stream()
                .map(JavaDeclarationSignature::normalized)
                .sorted()
                .collect(Collectors.toList());
        Files.write(file, lines, StandardCharsets.UTF_8);
    }

    public static List<JavaDeclarationSignature> readSignatures(Path file) throws IOException {
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        List<JavaDeclarationSignature> result = new ArrayList<>();

        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }

            String[] parts = line.split("::", 3);
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid signature line: " + line);
            }
            result.add(new JavaDeclarationSignature(parts[0], parts[1], parts[2]));
        }

        result.sort(Comparator.comparing(JavaDeclarationSignature::normalized));
        return result;
    }

    private static List<JavaDeclarationSignature> extractDeclarations(String relativeFile, String source) {
        String withoutBlockComments = source.replaceAll("(?s)/\\*.*?\\*/", "");
        String sanitized = withoutBlockComments.replaceAll("(?m)//.*$", "");

        List<JavaDeclarationSignature> signatures = new ArrayList<>();

        Matcher typeMatcher = TOP_LEVEL_TYPE_PATTERN.matcher(sanitized);
        while (typeMatcher.find()) {
            String kind = typeMatcher.group(1).toLowerCase(Locale.ROOT);
            String declaration = collapse(typeMatcher.group().replace("{", ""));
            signatures.add(new JavaDeclarationSignature(relativeFile, "type:" + kind, declaration));
        }

        List<String> members = extractTopLevelMembers(sanitized);
        for (String member : members) {
            String collapsed = collapse(member);
            String normalizedLower = collapsed.toLowerCase(Locale.ROOT);

            if (collapsed.contains("(")) {
                signatures.add(new JavaDeclarationSignature(relativeFile, "member:method", collapsed));
            } else if (normalizedLower.startsWith("class ") || normalizedLower.contains(" class ")
                    || normalizedLower.startsWith("interface ") || normalizedLower.contains(" interface ")
                    || normalizedLower.startsWith("enum ") || normalizedLower.contains(" enum ")
                    || normalizedLower.startsWith("record ") || normalizedLower.contains(" record ")) {
                signatures.add(new JavaDeclarationSignature(relativeFile, "member:type", collapsed));
            } else {
                signatures.add(new JavaDeclarationSignature(relativeFile, "member:field", collapsed));
            }
        }

        return signatures;
    }

    private static List<String> extractTopLevelMembers(String source) {
        List<String> result = new ArrayList<>();

        int depth = 0;
        StringBuilder pending = new StringBuilder();

        for (String rawLine : source.split("\\R")) {
            String line = rawLine.strip();
            if (line.isEmpty() || line.startsWith("package ") || line.startsWith("import ")) {
                depth = updateDepth(depth, rawLine);
                continue;
            }

            int depthBefore = depth;
            depth = updateDepth(depth, rawLine);

            if (depthBefore != 1) {
                continue;
            }

            if (line.startsWith("@")) {
                pending.append(line).append(' ');
                continue;
            }

            pending.append(line).append(' ');

            if (line.endsWith(";") || line.endsWith("{") || line.endsWith("}")) {
                String declaration = pending.toString().trim();
                pending.setLength(0);

                if (looksLikeDeclaration(declaration)) {
                    result.add(trimTrailingBodyMarker(declaration));
                }
            }
        }

        return result;
    }

    private static boolean looksLikeDeclaration(String declaration) {
        String text = declaration.trim();

        if (text.startsWith("if ") || text.startsWith("for ") || text.startsWith("while ")
                || text.startsWith("switch ") || text.startsWith("return ")
                || text.startsWith("throw ") || text.startsWith("catch ")
                || text.startsWith("do ") || text.startsWith("try ")) {
            return false;
        }

        return text.endsWith(";") || text.endsWith("{");
    }

    private static String trimTrailingBodyMarker(String declaration) {
        String trimmed = declaration.trim();
        if (trimmed.endsWith("{")) {
            return trimmed.substring(0, trimmed.length() - 1).trim();
        }
        return trimmed;
    }

    private static int updateDepth(int depth, String line) {
        boolean inString = false;
        char stringDelimiter = '\0';
        boolean escaping = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);

            if (inString) {
                if (escaping) {
                    escaping = false;
                    continue;
                }
                if (ch == '\\') {
                    escaping = true;
                    continue;
                }
                if (ch == stringDelimiter) {
                    inString = false;
                }
                continue;
            }

            if (ch == '"' || ch == '\'') {
                inString = true;
                stringDelimiter = ch;
                continue;
            }

            if (ch == '{') {
                depth++;
            } else if (ch == '}') {
                depth = Math.max(0, depth - 1);
            }
        }

        return depth;
    }

    private static long countContains(Collection<Path> files, String token) {
        return files.stream().map(path -> normalize(path.toString())).filter(path -> path.contains(token)).count();
    }

    private static long countFileName(Collection<Path> files, String name) {
        return files.stream().filter(path -> path.getFileName().toString().equals(name)).count();
    }

    private static List<Path> listAllFiles(Path root) throws IOException {
        try (Stream<Path> walk = Files.walk(root)) {
            return walk.filter(Files::isRegularFile).collect(Collectors.toList());
        }
    }

    private static String normalize(String path) {
        return path.replace('\\', '/');
    }

    private static String collapse(String text) {
        return text.replaceAll("\\s+", " ").trim();
    }

    public record JavaSnapshot(Map<String, Long> structure, List<JavaDeclarationSignature> signatures, String apiHash) {
    }
}
