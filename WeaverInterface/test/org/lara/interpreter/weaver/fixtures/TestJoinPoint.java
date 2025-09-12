package org.lara.interpreter.weaver.fixtures;

import org.lara.interpreter.weaver.interf.JoinPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class TestJoinPoint extends JoinPoint {

    private final String type;
    private final Object node;
    private TestJoinPoint parent;
    private final List<TestJoinPoint> children = new ArrayList<>();

    public TestJoinPoint(String type) {
        this(type, null);
    }

    public TestJoinPoint(String type, Object node) {
        this.type = type;
        this.node = node;
    }

    public TestJoinPoint addChild(TestJoinPoint child) {
        child.parent = this;
        this.children.add(child);
        return this;
    }

    @Override
    public boolean same(JoinPoint iJoinPoint) {
        return this == iJoinPoint;
    }

    @Override
    public Object getNode() {
        return node != null ? node : this;
    }

    @Override
    public String get_class() {
        return type;
    }

    @Override
    public Optional<? extends JoinPoint> getSuper() {
        return Optional.empty();
    }

    @Override
    public Stream<JoinPoint> getJpChildrenStream() {
        return children.stream().map(jp -> (JoinPoint) jp);
    }

    @Override
    public JoinPoint getJpParent() {
        return parent;
    }

    @Override
    public JoinPoint[] insertImpl(String position, String code) {
        // For testing, return self in an array
        return new JoinPoint[]{ this };
    }

    @Override
    public JoinPoint[] insertImpl(String position, JoinPoint JoinPoint) {
        return new JoinPoint[]{ JoinPoint };
    }
}
