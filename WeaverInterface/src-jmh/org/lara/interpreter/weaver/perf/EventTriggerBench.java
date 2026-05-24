package org.lara.interpreter.weaver.perf;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.IntFunction;

import org.lara.interpreter.weaver.events.EventTrigger;
import org.lara.interpreter.weaver.interf.AGear;
import org.lara.interpreter.weaver.interf.JoinPoint2;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.lara.interpreter.weaver.interf.enums.InsertPosition;
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
    private JoinPoint2<?, ?> jp;

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

        WeaverEngine weaver = null; // weaver is not used in this benchmark
        jp = new BenchJoinPoint(weaver);
    }

    @Benchmark
    public void triggerActionBegin(Blackhole bh) {
        trigger.triggerAction(Stage.BEGIN, jp, "benchAction", emptyResult, params);
        bh.consume(jp);
    }

    @Benchmark
    public void triggerActionEnd(Blackhole bh) {
        trigger.triggerAction(Stage.END, jp, "benchAction", someResult, params);
        bh.consume(jp);
    }

    // Minimal JoinPoint for benchmarks
    private static final class BenchJoinPoint extends JoinPoint2<BenchJoinPoint, BenchJoinPoint> {
        public BenchJoinPoint(WeaverEngine weaver) {
            super(weaver);
        }

        @Override
        public boolean getSameImpl(BenchJoinPoint iJoinPoint) {
            return this == iJoinPoint;
        }

        @Override
        public Object getNodeImpl() {
            return this;
        }
        // Use defaults for tree and actions; not needed in this benchmark

        @Override
        public BenchJoinPoint[] getChildrenImpl() {
            throw new UnsupportedOperationException("Unimplemented method 'getChildrenImpl'");
        }

        @Override
        public BenchJoinPoint[] getDescendantsImpl() {
            throw new UnsupportedOperationException("Unimplemented method 'getDescendantsImpl'");
        }

        @Override
        public BenchJoinPoint[] getScopeNodesImpl() {
            throw new UnsupportedOperationException("Unimplemented method 'getScopeNodesImpl'");
        }

        @Override
        public BenchJoinPoint getParentImpl() {
            throw new UnsupportedOperationException("Unimplemented method 'getParentImpl'");
        }

        @Override
        public BenchJoinPoint getRootImpl() {
            throw new UnsupportedOperationException("Unimplemented method 'getRootImpl'");
        }

        @Override
        public String getCodeImpl() {
            throw new UnsupportedOperationException("Unimplemented method 'getCodeImpl'");
        }

        @Override
        public Integer getLineImpl() {
            throw new UnsupportedOperationException("Unimplemented method 'getLineImpl'");
        }

        @Override
        public Integer getColumnImpl() {
            throw new UnsupportedOperationException("Unimplemented method 'getColumnImpl'");
        }

        @Override
        public boolean getCompareNodesImpl(BenchJoinPoint aJoinPoint) {
            throw new UnsupportedOperationException("Unimplemented method 'getCompareNodesImpl'");
        }

        @Override
        public boolean equalsImpl(BenchJoinPoint jp) {
            throw new UnsupportedOperationException("Unimplemented method 'equalsImpl'");
        }

        @Override
        public boolean instanceOfImpl(String joinpointClassname) {
            throw new UnsupportedOperationException("Unimplemented method 'instanceOfImpl'");
        }

        @Override
        public BenchJoinPoint[] insertImpl(InsertPosition position, String code) {
            throw new UnsupportedOperationException("Unimplemented method 'insertImpl'");
        }

        @Override
        public BenchJoinPoint[] insertImpl(InsertPosition position, BenchJoinPoint joinpoint) {
            throw new UnsupportedOperationException("Unimplemented method 'insertImpl'");
        }

        @Override
        protected IntFunction<BenchJoinPoint[]> selfTypeArrayFactory() {
            throw new UnsupportedOperationException("Unimplemented method 'selfTypeArrayFactory'");
        }

        @Override
        protected IntFunction<BenchJoinPoint[]> jpTypeArrayFactory() {
            throw new UnsupportedOperationException("Unimplemented method 'jpTypeArrayFactory'");
        }
    }
}
