package org.lara.interpreter.weaver.perf;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.lara.interpreter.weaver.utils.SourcesGatherer;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

@State(Scope.Thread)
public class SourcesGathererBench {

    @Param({ "200", "1000" })
    public int files;

    @Param({ "1", "3" })
    public int extCount;

    private Path root;
    private List<File> sources;
    private Collection<String> extensions;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        root = Files.createTempDirectory("sg_bench_" + UUID.randomUUID());
        // Create a shallow directory with files
        for (int i = 0; i < files; i++) {
            String ext = switch (i % 3) {
                case 0 -> "c";
                case 1 -> "h";
                default -> "txt";
            };
            Files.writeString(root.resolve("file_" + i + "." + ext), "x");
        }

        sources = List.of(root.toFile());

        extensions = switch (extCount) {
            case 1 -> List.of("c");
            default -> List.of("c", "h", "txt");
        };
    }

    @TearDown(Level.Trial)
    public void tearDown() throws IOException {
        if (root != null) {
            // Recursively delete
            Files.walk(root)
                    .sorted((a, b) -> b.getNameCount() - a.getNameCount())
                    .forEach(p -> {
                        try {
                            Files.deleteIfExists(p);
                        } catch (IOException ignored) {
                        }
                    });
        }
    }

    @Benchmark
    public Object gather() {
        return SourcesGatherer.build(sources, extensions).getSourceFiles();
    }
}
