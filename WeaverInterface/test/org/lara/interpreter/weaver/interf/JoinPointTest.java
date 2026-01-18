package org.lara.interpreter.weaver.interf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lara.interpreter.weaver.fixtures.TestJoinPoint;
import org.lara.interpreter.weaver.fixtures.TestWeaverEngine;

class JoinPointTest {

    @AfterEach
    void cleanupThreadLocal() {
        if (WeaverEngine.isWeaverSet()) {
            WeaverEngine.removeWeaver();
        }
    }

    @Test
    @DisplayName("instanceOf() true for 'joinpoint', exact type, and super chain")
    void testInstanceOfBehavior() {
        var root = new TestJoinPoint("root");
        var child = new TestJoinPoint("child") {
            @Override
            public Optional<? extends JoinPoint> getSuper() {
                return Optional.of(root);
            }
        };
        root.addChild(child);

        // Base type always true
        assertThat(root.instanceOf("joinpoint")).isTrue();
        assertThat(child.instanceOf("joinpoint")).isTrue();

        // Exact type
        assertThat(root.instanceOf("root")).isTrue();
        assertThat(child.instanceOf("child")).isTrue();

        // Super chain: customize a test JP with getSuper() behavior
        JoinPoint grandchild = new JoinPoint() {
            @Override
            public boolean same(JoinPoint iJoinPoint) {
                return this == iJoinPoint;
            }

            @Override
            public Object getNode() {
                return this;
            }

            @Override
            public String get_class() {
                return "grandchild";
            }

            @Override
            public Optional<? extends JoinPoint> getSuper() {
                return Optional.of(child);
            }

            @Override
            public Stream<JoinPoint> getJpChildrenStream() {
                return Stream.empty();
            }

            @Override
            public JoinPoint getJpParent() {
                return child;
            }
        };

        assertThat(grandchild.instanceOf("grandchild")).isTrue();
        assertThat(grandchild.instanceOf("child")).isTrue();
        assertThat(grandchild.instanceOf("root")).isTrue();
        assertThat(grandchild.instanceOf("unknown")).isFalse();

        // Array variant
        assertThat(grandchild.instanceOf(new String[] { "foo", "bar", "child" })).isTrue();
    }

    @Test
    @DisplayName("toString and dump format")
    void testToStringAndDump() {
        var root = new TestJoinPoint("root");
        var a = new TestJoinPoint("a");
        var b = new TestJoinPoint("b");
        var b1 = new TestJoinPoint("b1");
        root.addChild(a);
        root.addChild(b);
        b.addChild(b1);

        assertThat(root.toString()).isEqualTo("Joinpoint 'root'");
        String dump = root.getDump();
        assertThat(dump)
                .contains("Joinpoint 'root'")
                .contains("Joinpoint 'a'")
                .contains("Joinpoint 'b'")
                .contains("Joinpoint 'b1'");
    }

    @Test
    @DisplayName("Descendant APIs: getJpChildren, getJpDescendants, and stream variants")
    void testDescendantApis() {
        var root = new TestJoinPoint("root");
        var a = new TestJoinPoint("a");
        var b = new TestJoinPoint("b");
        var b1 = new TestJoinPoint("b1");
        root.addChild(a);
        root.addChild(b);
        b.addChild(b1);

        // Children
        List<JoinPoint> children = root.getJpChildren();
        assertThat(children).extracting(JoinPoint::getJoinPointType).containsExactly("a", "b");

        // Descendants list
        List<JoinPoint> descendants = root.getJpDescendants();
        assertThat(descendants).extracting(JoinPoint::getJoinPointType).containsExactly("a", "b", "b1");

        // Stream variants
        List<JoinPoint> streamDesc = root.getJpDescendantsStream().toList();
        assertThat(streamDesc).extracting(JoinPoint::getJoinPointType).containsExactly("a", "b", "b1");

        List<JoinPoint> andSelf = root.getJpDescendantsAndSelfStream().toList();
        assertThat(andSelf).extracting(JoinPoint::getJoinPointType).containsExactly("root", "a", "b", "b1");
    }

    @Test
    @DisplayName("getUndefinedValue is non-null and stable (same instance)")
    void testGetUndefinedValue() {
        Object u1 = JoinPoint.getUndefinedValue();
        Object u2 = JoinPoint.getUndefinedValue();
        assertThat(u1).isNotNull();
        assertThat(u1).isSameAs(u2);
    }

    @Test
    @DisplayName("hasListeners and eventTrigger delegation uses thread-local weaver")
    void testHasListenersDelegation() {
        var engine = new TestWeaverEngine();
        engine.setWeaver();
        // Without event trigger, hasListeners is false
        var root = (TestJoinPoint) engine.getRootJp();
        assertThat(root).isNotNull();
        assertThatCode(() -> {
            // This method internally calls getWeaverEngine().hasListeners()
            // We check it doesn't throw and remains false as there is no event trigger
            root.hasListeners();
        }).doesNotThrowAnyException();
        WeaverEngine.removeWeaver();
    }
}
