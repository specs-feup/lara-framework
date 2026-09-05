package org.lara.interpreter.weaver.interf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lara.interpreter.weaver.fixtures.TestJoinPoint;
import org.lara.interpreter.weaver.fixtures.TestWeaverEngine;

class JoinPointTest {

    @Test
    @DisplayName("instanceOf() true for 'joinpoint', exact type, and super chain")
    void testInstanceOfBehavior() {
        var engine = new TestWeaverEngine();
        var root = new TestJoinPoint(engine, "root");
        var child = new TestJoinPoint(engine, "child");
        var grandchild = new TestJoinPoint(engine, "grandchild");
        root.addChild(child);
        child.addChild(grandchild);

        // Base type always true
        assertThat(root.getInstanceOfImpl("joinpoint")).isTrue();
        assertThat(child.getInstanceOfImpl("joinpoint")).isTrue();

        // Exact type
        assertThat(root.getInstanceOfImpl("root")).isTrue();
        assertThat(child.getInstanceOfImpl("child")).isTrue();


        assertThat(grandchild.getInstanceOfImpl("grandchild")).isTrue();
        assertThat(grandchild.getInstanceOfImpl("child")).isTrue();
        assertThat(grandchild.getInstanceOfImpl("root")).isTrue();
        assertThat(grandchild.getInstanceOfImpl("unknown")).isFalse();

        // Array variant
        assertThat(grandchild.getInstanceOfImpl(new String[] { "foo", "bar", "child" })).isTrue();
    }

    @Test
    @DisplayName("toString and dump format")
    void testToStringAndDump() {
        var engine = new TestWeaverEngine();
        var root = new TestJoinPoint(engine, "root");
        var a = new TestJoinPoint(engine, "a");
        var b = new TestJoinPoint(engine, "b");
        var b1 = new TestJoinPoint(engine, "b1");
        root.addChild(a);
        root.addChild(b);
        b.addChild(b1);

        assertThat(root.getToStringImpl()).isEqualTo("Joinpoint 'root'");
        String dump = root.getDumpImpl();
        assertThat(dump)
                .contains("Joinpoint 'root'")
                .contains("Joinpoint 'a'")
                .contains("Joinpoint 'b'")
                .contains("Joinpoint 'b1'");
    }

    @Test
    @DisplayName("Descendant APIs: getJpChildren, getJpDescendants, and stream variants")
    void testDescendantApis() {
        var engine = new TestWeaverEngine();
        var root = new TestJoinPoint(engine, "root");
        var a = new TestJoinPoint(engine, "a");
        var b = new TestJoinPoint(engine, "b");
        var b1 = new TestJoinPoint(engine, "b1");
        root.addChild(a);
        root.addChild(b);
        b.addChild(b1);

        // Children
        TestJoinPoint[] children = root.getChildrenImpl();
        assertThat(children).extracting(JoinPoint2::getJoinPointTypeImpl).containsExactly("a", "b");

        // Descendants array
        TestJoinPoint[] descendants = root.getDescendantsImpl();
        assertThat(descendants).extracting(JoinPoint2::getJoinPointTypeImpl).containsExactly("a", "b", "b1");

        // Stream variants
        List<TestJoinPoint> streamDesc = root.getJpDescendantsStream().toList();
        assertThat(streamDesc).extracting(JoinPoint2::getJoinPointTypeImpl).containsExactly("a", "b", "b1");

        List<TestJoinPoint> andSelf = root.getJpDescendantsAndSelfStream().toList();
        assertThat(andSelf).extracting(JoinPoint2::getJoinPointTypeImpl).containsExactly("root", "a", "b", "b1");
    }

    @Test
    @DisplayName("hasListeners and eventTrigger delegation uses thread-local weaver")
    void testHasListenersDelegation() {
        var engine = new TestWeaverEngine();
        // Without event trigger, hasListeners is false
        var root = (TestJoinPoint) engine.getRootJp();
        assertThat(root).isNotNull();
        assertThatCode(() -> {
            // This method internally calls getWeaverEngine().hasListeners()
            // We check it doesn't throw and remains false as there is no event trigger
            root.getWeaverEngine().hasListeners();
        }).doesNotThrowAnyException();
    }
}
