package org.lara.interpreter.weaver.generator.benchmarks;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
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
import org.openjdk.jmh.annotations.Warmup;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Benchmark)
public class ModelBuildBench {

    @Param({ "0", "10", "100" })
    public int joinPoints;

    private byte[] jpModel;
    private byte[] artifacts;
    private byte[] actions;

    @Setup(Level.Trial)
    public void setup() {
        var jp = new StringBuilder();
        jp.append("<?xml version=\"1.0\"?>\n");
        jp.append("<joinpoints root_alias=\"root\" root_class=\"root\">\n");
        jp.append("  <joinpoint class=\"root\"/>\n");
        for (int i = 0; i < joinPoints; i++) {
            jp.append("  <joinpoint class=\"jp").append(i).append("\" extends=\"root\"/>\n");
        }
        jp.append("</joinpoints>\n");
        jpModel = jp.toString().getBytes(StandardCharsets.UTF_8);

        var art = new StringBuilder();
        art.append("<?xml version=\"1.0\"?>\n");
        art.append("<artifacts>\n");
        art.append("  <global>\n");
        art.append("    <attribute name=\"id\" type=\"String\"/>\n");
        art.append("  </global>\n");
        art.append("</artifacts>\n");
        artifacts = art.toString().getBytes(StandardCharsets.UTF_8);

        actions = "<?xml version=\"1.0\"?>\n<actions/>\n".getBytes(StandardCharsets.UTF_8);
    }

    @Benchmark
    public LanguageSpecification build_model_from_streams() {
        return LanguageSpecification.newInstance(new ByteArrayInputStream(jpModel),
                new ByteArrayInputStream(artifacts), new ByteArrayInputStream(actions));
    }
}
