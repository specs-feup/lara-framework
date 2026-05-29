package org.lara.weavergen2.integration.fixtures;

import static org.assertj.core.api.Assertions.fail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ManifestUtils {

    private ManifestUtils() {
    }

    public static List<ArtifactManifestEntry> computeManifest(Path root, Predicate<Path> filter) throws IOException {
        try (Stream<Path> walk = Files.walk(root)) {
            return walk.filter(Files::isRegularFile)
                    .filter(filter)
                    .map(path -> toEntry(root, path))
                    .sorted(Comparator.comparing(ArtifactManifestEntry::path))
                    .collect(Collectors.toList());
        }
    }

    public static String computeAggregateHash(List<ArtifactManifestEntry> entries) {
        String joined = entries.stream()
                .map(ArtifactManifestEntry::toTsv)
                .collect(Collectors.joining("\n"));
        return sha256Hex(joined.getBytes(StandardCharsets.UTF_8));
    }

    public static void writeManifest(Path outFile, List<ArtifactManifestEntry> entries) throws IOException {
        Files.createDirectories(outFile.getParent());

        List<String> lines = new ArrayList<>();
        lines.add("path\tsha256\tbytes\tlines");
        entries.stream().map(ArtifactManifestEntry::toTsv).forEach(lines::add);

        Files.write(outFile, lines, StandardCharsets.UTF_8);
    }

    public static List<ArtifactManifestEntry> readManifest(Path file) throws IOException {
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        if (lines.isEmpty()) {
            return List.of();
        }

        int startIndex = lines.get(0).startsWith("path\t") ? 1 : 0;
        List<ArtifactManifestEntry> entries = new ArrayList<>();
        for (int i = startIndex; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) {
                continue;
            }
            entries.add(ArtifactManifestEntry.fromTsv(line));
        }

        return entries.stream()
                .sorted(Comparator.comparing(ArtifactManifestEntry::path))
                .collect(Collectors.toList());
    }

    public static void assertManifestEquals(String label, List<ArtifactManifestEntry> expected,
            List<ArtifactManifestEntry> actual) {

        Map<String, ArtifactManifestEntry> expectedMap = expected.stream()
                .collect(Collectors.toMap(ArtifactManifestEntry::path, e -> e, (a, b) -> a, LinkedHashMap::new));
        Map<String, ArtifactManifestEntry> actualMap = actual.stream()
                .collect(Collectors.toMap(ArtifactManifestEntry::path, e -> e, (a, b) -> a, LinkedHashMap::new));

        TreeSet<String> allPaths = new TreeSet<>();
        allPaths.addAll(expectedMap.keySet());
        allPaths.addAll(actualMap.keySet());

        List<String> added = new ArrayList<>();
        List<String> removed = new ArrayList<>();
        List<String> changed = new ArrayList<>();

        for (String path : allPaths) {
            ArtifactManifestEntry e = expectedMap.get(path);
            ArtifactManifestEntry a = actualMap.get(path);

            if (e == null) {
                added.add(path + " -> " + a.toTsv());
                continue;
            }

            if (a == null) {
                removed.add(path + " -> " + e.toTsv());
                continue;
            }

            if (!e.equals(a)) {
                changed.add(path + "\n  expected: " + e.toTsv() + "\n  actual:   " + a.toTsv());
            }
        }

        if (!added.isEmpty() || !removed.isEmpty() || !changed.isEmpty()) {
            String message = "Manifest mismatch for " + label + "\n"
                    + "Added:\n" + (added.isEmpty() ? "  <none>" : indent(added)) + "\n"
                    + "Removed:\n" + (removed.isEmpty() ? "  <none>" : indent(removed)) + "\n"
                    + "Changed:\n" + (changed.isEmpty() ? "  <none>" : indent(changed));
            fail(message);
        }
    }

    public static String sha256Hex(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(bytes));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }

    private static ArtifactManifestEntry toEntry(Path root, Path file) {
        try {
            String normalizedPath = normalize(root.relativize(file).toString());
            byte[] bytes = Files.readAllBytes(file);
            String normalizedContents = normalizeEol(new String(bytes, StandardCharsets.UTF_8));
            long lines = normalizedContents.isEmpty() ? 0L : normalizedContents.split("\n", -1).length;

            return new ArtifactManifestEntry(normalizedPath, sha256Hex(bytes), bytes.length, lines);
        } catch (IOException e) {
            throw new RuntimeException("Could not build manifest entry for file '" + file + "'", e);
        }
    }

    private static String indent(List<String> lines) {
        return lines.stream().map(line -> "  " + line).collect(Collectors.joining("\n"));
    }

    private static String normalize(String path) {
        return path.replace('\\', '/');
    }

    private static String normalizeEol(String text) {
        return text.replace("\r\n", "\n").replace('\r', '\n');
    }
}
