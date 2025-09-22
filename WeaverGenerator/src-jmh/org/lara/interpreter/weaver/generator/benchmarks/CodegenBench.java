package org.lara.interpreter.weaver.generator.benchmarks;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.lara.interpreter.weaver.generator.generator.BaseGenerator;
import org.lara.interpreter.weaver.generator.generator.java.JavaAbstractsGenerator;
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
public class CodegenBench {

    @Param({ "0", "10", "100" })
    public int joinPoints;

    private Path specDir;
    private Path outDir;
    private BaseGenerator generator;

    @Setup(Level.Trial)
    public void setup() throws Exception {
        specDir = Files.createTempDirectory("wg-spec-" + joinPoints + "-");
        outDir = Files.createTempDirectory("wg-out-" + joinPoints + "-");
        SpecFactory.writeSpec(specDir, joinPoints);

        generator = new JavaAbstractsGenerator(specDir.toFile());
        generator.outputDir(outDir.toFile());
        generator.setPackage("bench.generated");
        generator.weaverName("BenchWeaver");
    }

    @TearDown(Level.Trial)
    public void tearDown() throws Exception {
        // Best-effort cleanup
        if (specDir != null) {
            Files.walk(specDir)
                    .sorted((a, b) -> b.getNameCount() - a.getNameCount())
                    .forEach(p -> p.toFile().delete());
        }
        if (outDir != null) {
            Files.walk(outDir)
                    .sorted((a, b) -> b.getNameCount() - a.getNameCount())
                    .forEach(p -> p.toFile().delete());
        }
    }

    @Benchmark
    public void generate_only() {
        generator.generate();
    }

    @Benchmark
    public void print_only() {
        // Ensure code is generated first
        if (!generator.isGenerated()) {
            generator.generate();
        }
        generator.print();
    }
}
