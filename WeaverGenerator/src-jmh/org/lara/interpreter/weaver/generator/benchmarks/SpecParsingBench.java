package org.lara.interpreter.weaver.generator.benchmarks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.lara.language.specification.dsl.LanguageSpecification;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Benchmark)
public class SpecParsingBench {

    @Param({ "0", "10", "100" })
    public int joinPoints;

    private Path specDir;

    @Setup(Level.Trial)
    public void setup() throws Exception {
        specDir = createTempDir("wg-spec-" + joinPoints);
        SpecFactory.writeSpec(specDir, joinPoints);
    }

    @TearDown(Level.Trial)
    public void tearDown() throws Exception {
        deleteRecursively(specDir);
    }

    @Benchmark
    public LanguageSpecification parse_spec_folder() {
        return LanguageSpecification.newInstance(specDir.toFile());
    }

    private static Path createTempDir(String prefix) throws IOException {
        return Files.createTempDirectory(prefix + "-" + UUID.randomUUID());
    }

    private static void deleteRecursively(Path dir) {
        if (dir == null) {
            return;
        }

        try (var walk = Files.walk(dir)) {
            walk.sorted((a, b) -> b.getNameCount() - a.getNameCount())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException ignored) {
                        }
                    });
        } catch (IOException ignored) {
        }
    }
}
