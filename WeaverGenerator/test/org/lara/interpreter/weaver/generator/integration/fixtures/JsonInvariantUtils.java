package org.lara.interpreter.weaver.generator.integration.fixtures;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

public final class JsonInvariantUtils {

    private JsonInvariantUtils() {
    }

    public static ParsedJson parseJson(Path jsonFile) throws IOException {
        String text = Files.readString(jsonFile, StandardCharsets.UTF_8);
        JsonParser parser = new JsonParser(text);
        Object root = parser.parseValue();
        parser.skipWhitespace();
        if (!parser.isAtEnd()) {
            throw new IllegalArgumentException("Unexpected trailing JSON contents at position " + parser.pos);
        }

        return new ParsedJson(root, text);
    }

    public static InvariantSnapshot computeSnapshot(ParsedJson parsed, Map<String, Long> javaInvariants) {
        Object root = parsed.root();
        if (!(root instanceof Map<?, ?>)) {
            fail("JSON root must be an object");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> rootMap = (Map<String, Object>) root;
        assertThat(rootMap).containsKeys("root", "rootAlias", "children");
        assertThat(rootMap.get("root")).isInstanceOf(String.class);
        assertThat(rootMap.get("rootAlias")).isInstanceOf(String.class);
        assertThat(rootMap.get("children")).isInstanceOf(List.class);

        var jsonInvariants = new TreeMap<String, Long>();

        JsonStats stats = new JsonStats();
        visit(rootMap, 0, stats);

        jsonInvariants.put("totalObjects", stats.totalObjects);
        jsonInvariants.put("totalArrays", stats.totalArrays);
        jsonInvariants.put("totalChildrenEdges", stats.totalChildrenEdges);
        jsonInvariants.put("maxDepth", stats.maxDepth);
        jsonInvariants.put("nodesWithChildren", stats.nodesWithChildrenKey);

        jsonInvariants.put("joinpointCount", stats.typeCounts.getOrDefault("joinpoint", 0L));
        jsonInvariants.put("actionCount", stats.typeCounts.getOrDefault("action", 0L));
        jsonInvariants.put("attributeCount", stats.typeCounts.getOrDefault("attribute", 0L));
        jsonInvariants.put("typedefCount", stats.typeCounts.getOrDefault("typedef", 0L));
        jsonInvariants.put("enumCount", stats.typeCounts.getOrDefault("enum", 0L));

        jsonInvariants.put("nameKeyCount", stats.keyCounts.getOrDefault("name", 0L));
        jsonInvariants.put("typeKeyCount", stats.keyCounts.getOrDefault("type", 0L));
        jsonInvariants.put("childrenKeyCount", stats.keyCounts.getOrDefault("children", 0L));
        jsonInvariants.put("extendsKeyCount", stats.keyCounts.getOrDefault("extends", 0L));

        validateJoinPointHierarchy(stats.joinPoints);

        return new InvariantSnapshot(jsonInvariants, javaInvariants);
    }

    public static String canonicalHash(Object jsonRoot) {
        String canonical = toCanonicalJson(jsonRoot);
        return ManifestUtils.sha256Hex(canonical.getBytes(StandardCharsets.UTF_8));
    }

    public static String toCanonicalJson(Object value) {
        if (value == null) {
            return "null";
        }

        if (value instanceof String stringValue) {
            return quote(stringValue);
        }

        if (value instanceof Boolean boolValue) {
            return boolValue ? "true" : "false";
        }

        if (value instanceof Number numberValue) {
            return numberValue.toString();
        }

        if (value instanceof JsonNumberLiteral numberLiteral) {
            return numberLiteral.literal();
        }

        if (value instanceof List<?> listValue) {
            return listValue.stream()
                    .map(JsonInvariantUtils::toCanonicalJson)
                    .collect(Collectors.joining(",", "[", "]"));
        }

        if (value instanceof Map<?, ?> mapValue) {
            @SuppressWarnings("unchecked")
            Map<String, Object> castMap = (Map<String, Object>) mapValue;
            return castMap.entrySet().stream()
                    .sorted(Comparator.comparing(Map.Entry::getKey))
                    .map(entry -> quote(entry.getKey()) + ":" + toCanonicalJson(entry.getValue()))
                    .collect(Collectors.joining(",", "{", "}"));
        }

        throw new IllegalArgumentException("Unsupported JSON value type: " + value.getClass().getName());
    }

    public static void writeInvariantSnapshot(Path file, InvariantSnapshot snapshot) throws IOException {
        Files.createDirectories(file.getParent());

        Map<String, Object> root = new LinkedHashMap<>();
        root.put("json", new TreeMap<>(snapshot.json()));
        root.put("java", new TreeMap<>(snapshot.java()));

        Files.writeString(file, toPrettyJson(root) + "\n", StandardCharsets.UTF_8);
    }

    public static InvariantSnapshot readInvariantSnapshot(Path file) throws IOException {
        ParsedJson parsed = parseJson(file);
        Object rootValue = parsed.root();
        if (!(rootValue instanceof Map<?, ?> rootMapRaw)) {
            throw new IllegalArgumentException("Invariant snapshot must be a JSON object");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> rootMap = (Map<String, Object>) rootMapRaw;

        return new InvariantSnapshot(readLongMap(rootMap, "json"), readLongMap(rootMap, "java"));
    }

    public static void assertSnapshotsEqual(InvariantSnapshot expected, InvariantSnapshot actual) {
        if (!Objects.equals(expected.json(), actual.json()) || !Objects.equals(expected.java(), actual.java())) {
            String message = "Invariant mismatch\n"
                    + "Expected JSON invariants:\n" + formatMap(expected.json()) + "\n"
                    + "Actual JSON invariants:\n" + formatMap(actual.json()) + "\n"
                    + "Expected Java invariants:\n" + formatMap(expected.java()) + "\n"
                    + "Actual Java invariants:\n" + formatMap(actual.java());
            fail(message);
        }
    }

    private static void visit(Object node, int depth, JsonStats stats) {
        stats.maxDepth = Math.max(stats.maxDepth, depth);

        if (node instanceof Map<?, ?> rawMap) {
            stats.totalObjects++;

            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) rawMap;

            for (Map.Entry<String, Object> entry : map.entrySet()) {
                stats.keyCounts.merge(entry.getKey(), 1L, Long::sum);
            }

            Object type = map.get("type");
            if (type instanceof String typeName) {
                String normalized = typeName.toLowerCase(Locale.ROOT);
                stats.typeCounts.merge(normalized, 1L, Long::sum);

                if ("joinpoint".equals(normalized)) {
                    String name = asString(map.get("name"));
                    String extend = asString(map.get("extends"));
                    if (name != null && extend != null) {
                        stats.joinPoints.put(name, extend);
                    }
                }
            }

            if (map.containsKey("children") && map.get("children") instanceof List<?> children) {
                stats.nodesWithChildrenKey++;
                stats.totalChildrenEdges += children.size();
            }

            for (Object value : map.values()) {
                visit(value, depth + 1, stats);
            }

            return;
        }

        if (node instanceof List<?> list) {
            stats.totalArrays++;
            for (Object value : list) {
                visit(value, depth + 1, stats);
            }
        }
    }

    private static String asString(Object value) {
        return value instanceof String s ? s : null;
    }

    private static void validateJoinPointHierarchy(Map<String, String> joinPoints) {
        assertThat(joinPoints).as("Joinpoint set should not be empty").isNotEmpty();

        List<String> invalidExtends = joinPoints.entrySet().stream()
                .filter(entry -> !entry.getValue().isBlank() && !joinPoints.containsKey(entry.getValue()))
                .map(entry -> entry.getKey() + " extends missing '" + entry.getValue() + "'")
                .sorted()
                .collect(Collectors.toList());
        assertThat(invalidExtends)
                .as("All joinpoint 'extends' references should exist")
                .isEmpty();

        List<String> roots = joinPoints.entrySet().stream()
                .filter(entry -> entry.getValue().isBlank())
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toList());
        assertThat(roots)
                .as("At least one root joinpoint (empty extends)")
                .isNotEmpty();

        List<String> cycles = new ArrayList<>();
        for (String joinpoint : joinPoints.keySet()) {
            ArrayDeque<String> stack = new ArrayDeque<>();
            String current = joinpoint;
            while (current != null && !current.isBlank()) {
                if (stack.contains(current)) {
                    cycles.add(joinpoint + " cycle at " + current);
                    break;
                }
                stack.push(current);
                current = joinPoints.getOrDefault(current, "");
            }
        }

        assertThat(cycles)
                .as("Joinpoint hierarchy should not contain cycles")
                .isEmpty();
    }

    private static Map<String, Long> readLongMap(Map<String, Object> root, String key) {
        Object value = root.get(key);
        if (!(value instanceof Map<?, ?> rawMap)) {
            throw new IllegalArgumentException("Missing object field '" + key + "' in invariant snapshot");
        }

        Map<String, Long> result = new HashMap<>();
        for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
            if (!(entry.getKey() instanceof String mapKey)) {
                throw new IllegalArgumentException("Invalid key type in '" + key + "': " + entry.getKey());
            }

            Object mapValue = entry.getValue();
            long longValue;
            if (mapValue instanceof Number number) {
                longValue = number.longValue();
            } else if (mapValue instanceof JsonNumberLiteral numberLiteral) {
                longValue = Long.parseLong(numberLiteral.literal());
            } else {
                throw new IllegalArgumentException("Invalid numeric value for '" + mapKey + "': " + mapValue);
            }

            result.put(mapKey, longValue);
        }

        return result;
    }

    private static String formatMap(Map<String, Long> map) {
        return map.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> "  " + entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("\n"));
    }

    private static String toPrettyJson(Object node) {
        StringBuilder builder = new StringBuilder();
        writePretty(node, builder, 0);
        return builder.toString();
    }

    private static void writePretty(Object node, StringBuilder builder, int depth) {
        if (node instanceof Map<?, ?> rawMap) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) rawMap;
            builder.append("{\n");

            List<Map.Entry<String, Object>> entries = map.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .collect(Collectors.toList());

            for (int i = 0; i < entries.size(); i++) {
                Map.Entry<String, Object> entry = entries.get(i);
                indent(builder, depth + 1);
                builder.append(quote(entry.getKey())).append(": ");
                writePretty(entry.getValue(), builder, depth + 1);
                if (i + 1 < entries.size()) {
                    builder.append(',');
                }
                builder.append('\n');
            }

            indent(builder, depth);
            builder.append('}');
            return;
        }

        if (node instanceof List<?> list) {
            builder.append("[\n");
            for (int i = 0; i < list.size(); i++) {
                indent(builder, depth + 1);
                writePretty(list.get(i), builder, depth + 1);
                if (i + 1 < list.size()) {
                    builder.append(',');
                }
                builder.append('\n');
            }
            indent(builder, depth);
            builder.append(']');
            return;
        }

        builder.append(toCanonicalJson(node));
    }

    private static void indent(StringBuilder builder, int depth) {
        for (int i = 0; i < depth; i++) {
            builder.append("  ");
        }
    }

    private static String quote(String string) {
        String escaped = string
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
        return "\"" + escaped + "\"";
    }

    public record ParsedJson(Object root, String originalText) {
    }

    private record JsonNumberLiteral(String literal) {
    }

    private static final class JsonStats {
        private long totalObjects;
        private long totalArrays;
        private long totalChildrenEdges;
        private long nodesWithChildrenKey;
        private long maxDepth;
        private final Map<String, Long> typeCounts = new HashMap<>();
        private final Map<String, Long> keyCounts = new HashMap<>();
        private final Map<String, String> joinPoints = new HashMap<>();
    }

    private static final class JsonParser {
        private final String input;
        private int pos;

        private JsonParser(String input) {
            this.input = input;
        }

        private boolean isAtEnd() {
            return pos >= input.length();
        }

        private void skipWhitespace() {
            while (!isAtEnd() && Character.isWhitespace(input.charAt(pos))) {
                pos++;
            }
        }

        private Object parseValue() {
            skipWhitespace();
            if (isAtEnd()) {
                throw error("Unexpected end of input while parsing JSON value");
            }

            char current = input.charAt(pos);
            return switch (current) {
                case '{' -> parseObject();
                case '[' -> parseArray();
                case '"' -> parseString();
                case 't' -> parseLiteral("true", Boolean.TRUE);
                case 'f' -> parseLiteral("false", Boolean.FALSE);
                case 'n' -> parseLiteral("null", null);
                default -> {
                    if (current == '-' || Character.isDigit(current)) {
                        yield parseNumber();
                    }
                    throw error("Unexpected character '" + current + "' while parsing JSON value");
                }
            };
        }

        private Map<String, Object> parseObject() {
            expect('{');
            skipWhitespace();

            Map<String, Object> object = new LinkedHashMap<>();
            if (peek('}')) {
                expect('}');
                return object;
            }

            while (true) {
                skipWhitespace();
                String key = parseString();
                skipWhitespace();
                expect(':');
                Object value = parseValue();
                object.put(key, value);
                skipWhitespace();

                if (peek('}')) {
                    expect('}');
                    return object;
                }

                expect(',');
            }
        }

        private List<Object> parseArray() {
            expect('[');
            skipWhitespace();

            List<Object> array = new ArrayList<>();
            if (peek(']')) {
                expect(']');
                return array;
            }

            while (true) {
                array.add(parseValue());
                skipWhitespace();

                if (peek(']')) {
                    expect(']');
                    return array;
                }

                expect(',');
            }
        }

        private String parseString() {
            expect('"');
            StringBuilder builder = new StringBuilder();
            while (!isAtEnd()) {
                char c = input.charAt(pos++);
                if (c == '"') {
                    return builder.toString();
                }

                if (c == '\\') {
                    if (isAtEnd()) {
                        throw error("Unexpected end of input in escape sequence");
                    }

                    char escaped = input.charAt(pos++);
                    switch (escaped) {
                        case '"' -> builder.append('"');
                        case '\\' -> builder.append('\\');
                        case '/' -> builder.append('/');
                        case 'b' -> builder.append('\b');
                        case 'f' -> builder.append('\f');
                        case 'n' -> builder.append('\n');
                        case 'r' -> builder.append('\r');
                        case 't' -> builder.append('\t');
                        case 'u' -> builder.append(parseUnicodeEscape());
                        default -> throw error("Invalid escape sequence '\\" + escaped + "'");
                    }
                    continue;
                }

                builder.append(c);
            }

            throw error("Unexpected end of input while parsing JSON string");
        }

        private char parseUnicodeEscape() {
            if (pos + 4 > input.length()) {
                throw error("Incomplete unicode escape sequence");
            }

            String code = input.substring(pos, pos + 4);
            pos += 4;
            try {
                return (char) Integer.parseInt(code, 16);
            } catch (NumberFormatException e) {
                throw error("Invalid unicode escape sequence: " + code);
            }
        }

        private Object parseLiteral(String literal, Object value) {
            if (!input.startsWith(literal, pos)) {
                throw error("Expected literal '" + literal + "'");
            }

            pos += literal.length();
            return value;
        }

        private JsonNumberLiteral parseNumber() {
            int start = pos;

            if (peek('-')) {
                pos++;
            }

            if (peek('0')) {
                pos++;
            } else {
                consumeDigits();
            }

            if (peek('.')) {
                pos++;
                consumeDigits();
            }

            if (peek('e') || peek('E')) {
                pos++;
                if (peek('+') || peek('-')) {
                    pos++;
                }
                consumeDigits();
            }

            return new JsonNumberLiteral(input.substring(start, pos));
        }

        private void consumeDigits() {
            int start = pos;
            while (!isAtEnd() && Character.isDigit(input.charAt(pos))) {
                pos++;
            }

            if (start == pos) {
                throw error("Expected one or more digits");
            }
        }

        private void expect(char expected) {
            skipWhitespace();
            if (isAtEnd() || input.charAt(pos) != expected) {
                throw error("Expected '" + expected + "'");
            }
            pos++;
        }

        private boolean peek(char expected) {
            return !isAtEnd() && input.charAt(pos) == expected;
        }

        private IllegalArgumentException error(String message) {
            return new IllegalArgumentException(message + " at position " + pos);
        }
    }
}
