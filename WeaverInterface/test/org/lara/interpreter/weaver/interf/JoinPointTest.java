package org.lara.interpreter.weaver.interf;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

import org.lara.interpreter.weaver.fixtures.TestJoinPoint;

import java.util.List;
import java.util.stream.Stream;

public class JoinPointTest {

    private TestJoinPoint rootJoinPoint;
    private TestJoinPoint childJoinPoint1;
    private TestJoinPoint childJoinPoint2;
    private TestJoinPoint grandchildJoinPoint;

    @BeforeEach
    void setUp() {
        // Create a small hierarchy for testing
        rootJoinPoint = new TestJoinPoint("root");
        childJoinPoint1 = new TestJoinPoint("child");
        childJoinPoint2 = new TestJoinPoint("child");
        grandchildJoinPoint = new TestJoinPoint("grandchild");
        
        // Build hierarchy: root -> child1 -> grandchild, root -> child2
        rootJoinPoint.addChild(childJoinPoint1);
        rootJoinPoint.addChild(childJoinPoint2);
        childJoinPoint1.addChild(grandchildJoinPoint);
    }

    @Test
    void testInstanceOfJoinPointAlwaysTrue() {
        // instanceOf("joinpoint") should always return true for any join point
        assertThat(rootJoinPoint.instanceOf("joinpoint")).isTrue();
        assertThat(childJoinPoint1.instanceOf("joinpoint")).isTrue();
        assertThat(grandchildJoinPoint.instanceOf("joinpoint")).isTrue();
    }

    @Test
    void testInstanceOfExactType() {
        // instanceOf should return true for exact type match
        assertThat(rootJoinPoint.instanceOf("root")).isTrue();
        assertThat(childJoinPoint1.instanceOf("child")).isTrue();
        assertThat(grandchildJoinPoint.instanceOf("grandchild")).isTrue();
        
        // Should return false for non-matching types
        assertThat(rootJoinPoint.instanceOf("child")).isFalse();
        assertThat(childJoinPoint1.instanceOf("root")).isFalse();
        assertThat(grandchildJoinPoint.instanceOf("child")).isFalse();
    }

    @Test
    void testInstanceOfWithSuperChain() {
        // Create a join point with a super type
        TestJoinPoint superJoinPoint = new TestJoinPoint("super") {
            @Override
            public java.util.Optional<? extends JoinPoint> getSuper() {
                return java.util.Optional.of(new TestJoinPoint("joinpoint"));
            }
        };
        
        TestJoinPoint childWithSuper = new TestJoinPoint("child") {
            @Override
            public java.util.Optional<? extends JoinPoint> getSuper() {
                return java.util.Optional.of(superJoinPoint);
            }
        };
        
        // Should be instance of its own type
        assertThat(childWithSuper.instanceOf("child")).isTrue();
        // Should be instance of super type
        assertThat(childWithSuper.instanceOf("super")).isTrue();
        // Should be instance of base joinpoint type
        assertThat(childWithSuper.instanceOf("joinpoint")).isTrue();
        
        // Should not be instance of unrelated type
        assertThat(childWithSuper.instanceOf("unrelated")).isFalse();
    }

    @Test
    void testInstanceOfWithStringArray() {
        // instanceOf with array should return true if any type matches
        String[] types1 = {"child", "root"};
        String[] types2 = {"unknown", "child"};
        String[] types3 = {"unknown", "other"};
        
        assertThat(rootJoinPoint.instanceOf(types1)).isTrue(); // matches "root"
        assertThat(childJoinPoint1.instanceOf(types1)).isTrue(); // matches "child"
        assertThat(childJoinPoint1.instanceOf(types2)).isTrue(); // matches "child"
        assertThat(rootJoinPoint.instanceOf(types3)).isFalse(); // no matches
    }

    @Test
    void testToStringFormat() {
        // toString should have expected format
        String toStringResult = rootJoinPoint.toString();
        assertThat(toStringResult).isEqualTo("Joinpoint 'root'");
        
        assertThat(childJoinPoint1.toString()).isEqualTo("Joinpoint 'child'");
        assertThat(grandchildJoinPoint.toString()).isEqualTo("Joinpoint 'grandchild'");
    }

    @Test
    void testDumpTreeText() {
        // Test dump tree functionality
        String dump = rootJoinPoint.getDump();
        
        assertThat(dump).isNotNull();
        assertThat(dump).contains("Joinpoint 'root'");
        assertThat(dump).contains("Joinpoint 'child'");
        assertThat(dump).contains("Joinpoint 'grandchild'");
        
        // Check hierarchical structure in dump
        assertThat(dump).matches("(?s).*Joinpoint 'root'.*Joinpoint 'child'.*Joinpoint 'grandchild'.*");
    }

    @Test
    void testDumpTreeTextWithSmallHierarchy() {
        // Test dump with simple parent-child relationship
        TestJoinPoint parent = new TestJoinPoint("parent");
        TestJoinPoint child = new TestJoinPoint("child");
        parent.addChild(child);
        
        String dump = parent.getDump();
        
        // Should contain both parent and child
        assertThat(dump).contains("Joinpoint 'parent'");
        assertThat(dump).contains("Joinpoint 'child'");
        
        // Check that child is indented relative to parent
        String[] lines = dump.split("\n");
        assertThat(lines[0].trim()).startsWith("Joinpoint 'parent'");
        assertThat(lines[1]).startsWith("   "); // Child should be indented
        assertThat(lines[1].trim()).startsWith("Joinpoint 'child'");
    }

    @Test
    void testGetJpChildren() {
        // Test getJpChildren() list variant
        List<JoinPoint> rootChildren = rootJoinPoint.getJpChildren();
        assertThat(rootChildren).hasSize(2);
        assertThat(rootChildren).contains(childJoinPoint1, childJoinPoint2);
        
        List<JoinPoint> child1Children = childJoinPoint1.getJpChildren();
        assertThat(child1Children).hasSize(1);
        assertThat(child1Children).contains(grandchildJoinPoint);
        
        List<JoinPoint> child2Children = childJoinPoint2.getJpChildren();
        assertThat(child2Children).isEmpty();
    }

    @Test
    void testGetJpChildrenStream() {
        // Test getJpChildrenStream() stream variant
        Stream<JoinPoint> rootChildrenStream = rootJoinPoint.getJpChildrenStream();
        assertThat(rootChildrenStream.toList()).hasSize(2);
        
        Stream<JoinPoint> child1ChildrenStream = childJoinPoint1.getJpChildrenStream();
        assertThat(child1ChildrenStream.toList()).hasSize(1);
        
        Stream<JoinPoint> child2ChildrenStream = childJoinPoint2.getJpChildrenStream();
        assertThat(child2ChildrenStream.toList()).isEmpty();
    }

    @Test
    void testGetJpDescendants() {
        // Test getJpDescendants() list variant
        List<JoinPoint> rootDescendants = rootJoinPoint.getJpDescendants();
        
        // Should contain all descendants: child1, child2, grandchild
        assertThat(rootDescendants).hasSize(3);
        assertThat(rootDescendants).contains(childJoinPoint1, childJoinPoint2, grandchildJoinPoint);
        
        List<JoinPoint> child1Descendants = childJoinPoint1.getJpDescendants();
        assertThat(child1Descendants).hasSize(1);
        assertThat(child1Descendants).contains(grandchildJoinPoint);
        
        List<JoinPoint> grandchildDescendants = grandchildJoinPoint.getJpDescendants();
        assertThat(grandchildDescendants).isEmpty();
    }

    @Test
    void testGetJpDescendantsStream() {
        // Test getJpDescendantsStream() stream variant
        Stream<JoinPoint> rootDescendantsStream = rootJoinPoint.getJpDescendantsStream();
        List<JoinPoint> rootDescendants = rootDescendantsStream.toList();
        
        assertThat(rootDescendants).hasSize(3);
        assertThat(rootDescendants).contains(childJoinPoint1, childJoinPoint2, grandchildJoinPoint);
    }

    @Test
    void testGetJpDescendantsAndSelfStream() {
        // Test getJpDescendantsAndSelfStream() includes self
        Stream<JoinPoint> rootDescendantsAndSelfStream = rootJoinPoint.getJpDescendantsAndSelfStream();
        List<JoinPoint> rootDescendantsAndSelf = rootDescendantsAndSelfStream.toList();
        
        // Should contain self + all descendants
        assertThat(rootDescendantsAndSelf).hasSize(4);
        assertThat(rootDescendantsAndSelf).contains(rootJoinPoint, childJoinPoint1, childJoinPoint2, grandchildJoinPoint);
        
        // First element should be self
        assertThat(rootDescendantsAndSelf.get(0)).isSameAs(rootJoinPoint);
    }

    @Test
    void testGetUndefinedValueIsNotNull() {
        // getUndefinedValue() should not return null
        Object undefinedValue = JoinPoint.getUndefinedValue();
        assertThat(undefinedValue).isNotNull();
    }

    @Test
    void testGetUndefinedValueIsStable() {
        // getUndefinedValue() should return the same instance each time
        Object undefinedValue1 = JoinPoint.getUndefinedValue();
        Object undefinedValue2 = JoinPoint.getUndefinedValue();
        
        assertThat(undefinedValue1).isSameAs(undefinedValue2);
    }

    @Test
    void testGetSelf() {
        // getSelf() should return the same instance
        JoinPoint self = rootJoinPoint.getSelf();
        assertThat(self).isSameAs(rootJoinPoint);
        
        assertThat(childJoinPoint1.getSelf()).isSameAs(childJoinPoint1);
    }

    @Test
    void testStaticIsJoinPoint() {
        // isJoinPoint should return true for JoinPoint instances
        assertThat(JoinPoint.isJoinPoint(rootJoinPoint)).isTrue();
        assertThat(JoinPoint.isJoinPoint(childJoinPoint1)).isTrue();
        
        // Should return false for non-JoinPoint objects
        assertThat(JoinPoint.isJoinPoint("string")).isFalse();
        assertThat(JoinPoint.isJoinPoint(123)).isFalse();
        assertThat(JoinPoint.isJoinPoint(null)).isFalse();
    }
}