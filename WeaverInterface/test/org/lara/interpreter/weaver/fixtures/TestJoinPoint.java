package org.lara.interpreter.weaver.fixtures;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Stream;

import org.lara.interpreter.weaver.interf.JoinPoint2;
import org.lara.interpreter.weaver.interf.enums.InsertPosition;

public class TestJoinPoint extends JoinPoint2<TestJoinPoint, TestJoinPoint> {

    private final String type;
    private final Object node;
    private TestJoinPoint parent;
    private final List<TestJoinPoint> children = new ArrayList<>();

    public TestJoinPoint(TestWeaverEngine weaver, String type) {
        this(weaver, type, null);
    }

    public TestJoinPoint(TestWeaverEngine weaver, String type, Object node) {
        super(weaver);
        this.type = type;
        this.node = node;
    }

    @Override
    public TestWeaverEngine getWeaverEngine() {
        return (TestWeaverEngine) super.getWeaverEngine();
    }

    @Override
    public TestJoinPoint getRootImpl() {
        return this;
    }

    public TestJoinPoint addChild(TestJoinPoint child) {
        child.parent = this;
        this.children.add(child);
        return this;
    }

    @Override
    public boolean getSameImpl(TestJoinPoint iJoinPoint) {
        return this == iJoinPoint;
    }

    @Override
    public Object getNodeImpl() {
        return node != null ? node : this;
    }

    @Override
    public String get_class() {
        return type;
    }

    @Override
    public Stream<TestJoinPoint> getJpChildrenStream() {
        return children.stream();
    }

    @Override
    public TestJoinPoint getJpParent() {
        return parent;
    }

    @Override
    public TestJoinPoint[] insertImpl(InsertPosition position, String code) {
        // For testing, return self in an array
        return new TestJoinPoint[] { this };
    }

    @Override
    public TestJoinPoint[] insertImpl(InsertPosition position, TestJoinPoint JoinPoint) {
        return new TestJoinPoint[] { JoinPoint };
    }

    @Override
    public TestJoinPoint[] getChildrenImpl() {
        return children.toArray(new TestJoinPoint[0]);
    }

    @Override
    public TestJoinPoint[] getDescendantsImpl() {
        throw new UnsupportedOperationException("Unimplemented method 'getDescendantsImpl'");
    }

    @Override
    public TestJoinPoint[] getScopeNodesImpl() {
        throw new UnsupportedOperationException("Unimplemented method 'getScopeNodesImpl'");
    }

    @Override
    public TestJoinPoint getParentImpl() {
        throw new UnsupportedOperationException("Unimplemented method 'getParentImpl'");
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
    public boolean getCompareNodesImpl(TestJoinPoint aJoinPoint) {
        throw new UnsupportedOperationException("Unimplemented method 'getCompareNodesImpl'");
    }

    @Override
    public boolean equalsImpl(TestJoinPoint jp) {
        throw new UnsupportedOperationException("Unimplemented method 'equalsImpl'");
    }

    @Override
    public boolean instanceOfImpl(String joinpointClassname) {
        throw new UnsupportedOperationException("Unimplemented method 'instanceOfImpl'");
    }

    @Override
    protected IntFunction<TestJoinPoint[]> selfTypeArrayFactory() {
        return TestJoinPoint[]::new;
    }

    @Override
    protected IntFunction<TestJoinPoint[]> jpTypeArrayFactory() {
        return TestJoinPoint[]::new;
    }
}
