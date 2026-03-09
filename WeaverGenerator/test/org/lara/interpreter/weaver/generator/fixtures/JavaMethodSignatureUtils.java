package org.lara.interpreter.weaver.generator.fixtures;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class JavaMethodSignatureUtils {

    private static final Pattern METHOD_SIGNATURE_PATTERN = Pattern.compile(
            "(?m)^\\s*(?:public|protected|private)\\s+(?:final\\s+|static\\s+|abstract\\s+|synchronized\\s+|native\\s+)*[^\\s(]+(?:\\s*<[^>]+>)?(?:\\s*\\[\\])*(?:\\s+[^\\s(]+(?:\\s*<[^>]+>)?(?:\\s*\\[\\])*)*\\s+(\\w+)\\s*\\(([^)]*)\\)\\s*(?:throws\\s+[^\\{]+)?\\{");

    private JavaMethodSignatureUtils() {
    }

    public static void assertNoMethodSignatureCollisions(Path outDir, String scenario) throws IOException {
        Map<String, Integer> signatureCounts = new HashMap<>();

        for (Path javaFile : GeneratedTreeUtils.listFiles(outDir, path -> path.getFileName().toString().endsWith(".java"))) {
            String source = GeneratedTreeUtils.readNormalized(javaFile);
            Matcher matcher = METHOD_SIGNATURE_PATTERN.matcher(source);
            while (matcher.find()) {
                String methodName = matcher.group(1);
                String normalizedParams = normalizeParameterTypes(matcher.group(2));
                String signature = GeneratedTreeUtils.normalizeRelativePath(outDir.relativize(javaFile).toString())
                        + "::" + methodName + "(" + normalizedParams + ")";
                signatureCounts.put(signature, signatureCounts.getOrDefault(signature, 0) + 1);
            }
        }

        List<String> collisions = signatureCounts.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(entry -> entry.getKey() + " x" + entry.getValue())
                .sorted()
                .toList();

        assertThat(collisions)
                .as("Generated Java should not contain duplicate method signatures for scenario '%s'", scenario)
                .isEmpty();
    }

    private static String normalizeParameterTypes(String params) {
        String trimmed = params == null ? "" : params.trim();
        if (trimmed.isEmpty()) {
            return "";
        }

        List<String> parameterChunks = splitParameters(trimmed);
        return parameterChunks.stream()
                .map(JavaMethodSignatureUtils::normalizeSingleParameterType)
                .collect(java.util.stream.Collectors.joining(","));
    }

    private static List<String> splitParameters(String params) {
        List<String> chunks = new ArrayList<>();
        int genericDepth = 0;
        StringBuilder current = new StringBuilder();
        for (int index = 0; index < params.length(); index++) {
            char ch = params.charAt(index);
            if (ch == '<') {
                genericDepth++;
            } else if (ch == '>') {
                genericDepth = Math.max(0, genericDepth - 1);
            }

            if (ch == ',' && genericDepth == 0) {
                chunks.add(current.toString().trim());
                current.setLength(0);
                continue;
            }

            current.append(ch);
        }

        String last = current.toString().trim();
        if (!last.isEmpty()) {
            chunks.add(last);
        }

        return chunks;
    }

    private static String normalizeSingleParameterType(String parameter) {
        String normalized = parameter
                .replaceAll("@[A-Za-z_][A-Za-z0-9_$.]*(\\([^)]*\\))?\\s*", "")
                .replaceAll("\\bfinal\\b\\s*", "")
                .trim();

        int lastSpace = normalized.lastIndexOf(' ');
        if (lastSpace >= 0) {
            normalized = normalized.substring(0, lastSpace).trim();
        }

        return normalized.replaceAll("\\s+", "");
    }
}