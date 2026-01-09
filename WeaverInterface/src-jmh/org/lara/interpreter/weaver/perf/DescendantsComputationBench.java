package org.lara.interpreter.weaver.perf;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.lara.interpreter.weaver.interf.JoinPoint;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Thread)
public class DescendantsComputationBench {

    @Param({ "2", "4" })
    public int branching;

    @Param({ "4", "6" })
    public int depth;

    private TestTreeJoinPoint root;

    @Setup(Level.Trial)
    public void setup() {
        root = makeTree(branching, depth);
    }

    @Benchmark
    public Object computeDescendantsList() {
        return root.getJpDescendants();
    }

    @Benchmark
    public long computeDescendantsStreamCount() {
        return root.getJpDescendantsStream().count();
    }

    private static TestTreeJoinPoint makeTree(int branching, int depth) {
        WeaverEngine weaver = null; // weaver is not used in this benchmark
        TestTreeJoinPoint r = new TestTreeJoinPoint(weaver, null);
        build(r, branching, depth - 1);
        return r;
    }

    private static void build(TestTreeJoinPoint parent, int branching, int depth) {
        if (depth < 0)
            return;
        for (int i = 0; i < branching; i++) {
            var child = new TestTreeJoinPoint(parent.getWeaverEngine(), parent);
            parent.children.add(child);
            build(child, branching, depth - 1);
        }
    }

    private static final class TestTreeJoinPoint extends JoinPoint {
        private final TestTreeJoinPoint parent;
        private final List<TestTreeJoinPoint> children = new ArrayList<>();

        TestTreeJoinPoint(WeaverEngine weaver, TestTreeJoinPoint parent) {
            super(weaver);
            this.parent = parent;
        }

        @Override
        public boolean same(JoinPoint iJoinPoint) {
            return this == iJoinPoint;
        }

        @Override
        public Object getNode() {
            return this;
        }

        @Override
        public Stream<JoinPoint> getJpChildrenStream() {
            return children.stream().map(c -> (JoinPoint) c);
        }

        @Override
        public JoinPoint getJpParent() {
            return parent;
        }
    }
}
