package org.lara.interpreter.weaver.generator.perf;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Thread)
public class NoopBench {

    @Benchmark
    public void noop() {
        // Intentionally empty; ensures jmh task succeeds in CI until real benches are
        // added
    }
}
