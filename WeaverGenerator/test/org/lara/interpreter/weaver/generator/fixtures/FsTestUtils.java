package org.lara.interpreter.weaver.generator.fixtures;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public final class FsTestUtils {
    private FsTestUtils() {
    }

    public static Path mkdirs(Path root, String... segments) throws IOException {
        Path dir = root;
        for (String s : segments) {
            dir = dir.resolve(s);
        }
        return Files.createDirectories(dir);
    }

    public static void write(Path file, String content) throws IOException {
        Files.createDirectories(file.getParent());
        Files.writeString(file, normalize(content), StandardCharsets.UTF_8);
    }

    public static String normalize(String s) {
        return s.replace("\r\n", "\n").replace('\r', '\n');
    }

    public static void rmrf(Path dir) throws IOException {
        if (!Files.exists(dir)) {
            return;
        }
        try (var walk = Files.walk(dir)) {
            walk.sorted(Comparator.reverseOrder()).forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException ignored) {
                }
            });
        }
    }
}
