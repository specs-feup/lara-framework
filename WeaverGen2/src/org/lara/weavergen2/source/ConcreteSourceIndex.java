package org.lara.weavergen2.source;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ConcreteSourceIndex {

    private final ConcreteJoinPointNamer namer;
    private final Map<String, List<Path>> filesByClassName;

    public ConcreteSourceIndex(ConcreteJoinPointNamer namer) {
        this.namer = namer;
        this.filesByClassName = new HashMap<>();
    }

    public void scan() {
        filesByClassName.clear();

        if (!Files.exists(namer.root())) {
            return;
        }

        try (var paths = Files.walk(namer.root())) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".java"))
                    .sorted(Comparator.comparing(namer::relativePath))
                    .forEach(path -> {
                        var className = path.getFileName().toString().replaceFirst("\\.java$", "");
                        filesByClassName
                                .computeIfAbsent(className, unused -> new ArrayList<>())
                                .add(path);
                    });
        } catch (IOException e) {
            throw new IllegalStateException("Could not search concrete joinpoint sources under '" + namer.root() + "'",
                    e);
        }
    }

    public List<Path> filesForClass(String className) {
        return filesByClassName.getOrDefault(className, List.of());
    }

    public void putCreatedFile(String className, Path path) {
        filesByClassName.put(className, new ArrayList<>(List.of(path)));
    }
}
