package org.lara.interpreter.weaver.generator.fixtures;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class GeneratedTreeUtils {

    private GeneratedTreeUtils() {
    }

    public static List<String> snapshotRelativePaths(Path root) throws IOException {
        try (Stream<Path> walk = Files.walk(root)) {
            return walk.filter(Files::isRegularFile)
                    .map(root::relativize)
                    .map(Path::toString)
                    .map(GeneratedTreeUtils::normalizeRelativePath)
                    .sorted()
                    .collect(Collectors.toList());
        }
    }

    public static Map<String, Path> snapshotFiles(Path root) throws IOException {
        try (Stream<Path> walk = Files.walk(root)) {
            return walk.filter(Files::isRegularFile)
                    .collect(Collectors.toMap(
                            path -> normalizeRelativePath(root.relativize(path).toString()),
                            Function.identity(),
                            (left, right) -> {
                                throw new IllegalStateException("Duplicate generated path: " + left);
                            },
                            TreeMap::new));
        }
    }

    public static List<Path> listFiles(Path root, Predicate<Path> filter) throws IOException {
        try (Stream<Path> walk = Files.walk(root)) {
            return walk.filter(Files::isRegularFile)
                    .filter(filter)
                    .sorted()
                    .collect(Collectors.toList());
        }
    }

    public static String readNormalized(Path file) throws IOException {
        return normalizeEol(Files.readString(file, StandardCharsets.UTF_8));
    }

    public static void writeNormalized(Path file, String contents) throws IOException {
        Files.createDirectories(file.getParent());
        Files.writeString(file, normalizeEol(contents), StandardCharsets.UTF_8);
    }

    public static void deleteTree(Path root) throws IOException {
        if (!Files.exists(root)) {
            return;
        }

        List<Path> paths;
        try (Stream<Path> walk = Files.walk(root)) {
            paths = walk.sorted(Comparator.reverseOrder()).collect(Collectors.toCollection(ArrayList::new));
        }

        for (Path path : paths) {
            Files.deleteIfExists(path);
        }
    }

    public static String normalizeRelativePath(String path) {
        return path.replace('\\', '/');
    }

    public static String normalizeEol(String text) {
        return text.replace("\r\n", "\n").replace('\r', '\n');
    }
}