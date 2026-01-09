package org.lara.interpreter.weaver.perf;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.lara.interpreter.weaver.events.EventTrigger;
import org.lara.interpreter.weaver.interf.AGear;
import org.lara.interpreter.weaver.interf.JoinPoint;
import org.lara.interpreter.weaver.interf.events.Stage;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

/**
 * Measures throughput of EventTrigger.triggerAction for BEGIN and END with
 * varying
 * number of registered gears and payload sizes.
 */
@State(Scope.Thread)
public class EventTriggerBench {

    @Param({ "1", "5", "10" })
    public int gears;

    @Param({ "0", "5", "20" })
    public int payloadSize;

    private EventTrigger trigger;
    private List<Object> params;
    private Optional<Object> emptyResult;
    private Optional<Object> someResult;
    private JoinPoint jp;

    @Setup(Level.Trial)
    public void setup() {
        trigger = new EventTrigger();

        // Register N no-op gears
        var gearList = new ArrayList<AGear>(gears);
        for (int i = 0; i < gears; i++) {
            gearList.add(new AGear() {
            });
        }
        trigger.registerReceivers(gearList);

        // Prebuild params payload
        params = new ArrayList<>(payloadSize);
        for (int i = 0; i < payloadSize; i++) {
            params.add(i);
        }
        emptyResult = Optional.empty();
        someResult = Optional.of("ok");
        jp = new BenchJoinPoint();
    }

    @Benchmark
    public void triggerActionBegin(Blackhole bh) {
        trigger.triggerAction(Stage.BEGIN, "benchAction", jp, params, emptyResult);
        bh.consume(jp);
    }

    @Benchmark
    public void triggerActionEnd(Blackhole bh) {
        trigger.triggerAction(Stage.END, "benchAction", jp, params, someResult);
        bh.consume(jp);
    }

    // Minimal JoinPoint for benchmarks
    private static final class BenchJoinPoint extends JoinPoint {
        @Override
        public boolean same(JoinPoint iJoinPoint) {
            return this == iJoinPoint;
        }

        @Override
        public Object getNode() {
            return this;
        }
        // Use defaults for tree and actions; not needed in this benchmark
    }
}
