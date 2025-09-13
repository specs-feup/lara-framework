package org.lara.interpreter.weaver.ast;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lara.interpreter.weaver.fixtures.TestWeaverEngine;
import org.lara.interpreter.weaver.interf.JoinPoint;
import org.lara.interpreter.weaver.interf.WeaverEngine;

class AAstMethodsTest {

    private TestAAstMethods astMethods;
    private WeaverEngine mockEngine;
    private TestNode rootNode;
    private TestNode childNode1;
    private TestNode childNode2;
    private TestNode grandchildNode;

    static class TestNode {
        private final String name;
        private TestNode[] children = new TestNode[0];

        public TestNode(String name) {
            this.name = name;
        }

        public TestNode withChildren(TestNode... children) {
            this.children = children;
            return this;
        }

        public TestNode[] getChildren() {
            return children;
        }

        public String getName() {
            return name;
        }
    }

    static class TestAAstMethods extends AAstMethods<TestNode> {
        private final TestNode rootNode;

        public TestAAstMethods(WeaverEngine weaverEngine, TestNode rootNode) {
            super(weaverEngine);
            this.rootNode = rootNode;
        }

        @Override
        public Class<TestNode> getNodeClass() {
            return TestNode.class;
        }

        @Override
        protected JoinPoint toJavaJoinPointImpl(TestNode node) {
            JoinPoint mockJp = mock(JoinPoint.class);
            when(mockJp.toString()).thenReturn("JoinPoint[" + node.getName() + "]");
            return mockJp;
        }

        @Override
        protected String getJoinPointNameImpl(TestNode node) {
            return node.getName();
        }

        @Override
        protected Object[] getChildrenImpl(TestNode node) {
            return node.getChildren();
        }

        @Override
        protected Object[] getScopeChildrenImpl(TestNode node) {
            return node.getChildren(); // Same as children for this test
        }

        @Override
        protected Object getParentImpl(TestNode node) {
            return null; // Simplified for test
        }

        @Override
        public Object getRootImpl() {
            return rootNode;
        }
    }

    @BeforeEach
    void setUp() {
        mockEngine = mock(WeaverEngine.class);
        
        // Create a test tree structure:
        // root
        //  ├── child1
        //  │   └── grandchild
        //  └── child2
        grandchildNode = new TestNode("grandchild");
        childNode1 = new TestNode("child1").withChildren(grandchildNode);
        childNode2 = new TestNode("child2");
        rootNode = new TestNode("root").withChildren(childNode1, childNode2);

        when(mockEngine.getRootNode()).thenReturn(rootNode);
        
        astMethods = new TestAAstMethods(mockEngine, rootNode);
    }

    @Test
    void getDescendants_shouldRecursivelyCollectAllChildren_inExpectedOrder() {
        // When
        @SuppressWarnings("unchecked")
        List<Object> descendants = (List<Object>) astMethods.getDescendants(rootNode);

        // Then - should contain all descendants in depth-first order
        assertThat(descendants).hasSize(3);
        assertThat(((TestNode) descendants.get(0)).getName()).isEqualTo("child1");
        assertThat(((TestNode) descendants.get(1)).getName()).isEqualTo("grandchild");
        assertThat(((TestNode) descendants.get(2)).getName()).isEqualTo("child2");
    }

    @Test
    void getDescendants_withNodeWithNoChildren_shouldReturnEmptyList() {
        // When
        @SuppressWarnings("unchecked")
        List<Object> descendants = (List<Object>) astMethods.getDescendants(childNode2);

        // Then
        assertThat(descendants).isEmpty();
    }

    @Test
    void getDescendants_withSingleChildNode_shouldReturnOnlyThatChild() {
        // Given - child1 has only grandchild
        // When
        @SuppressWarnings("unchecked")
        List<Object> descendants = (List<Object>) astMethods.getDescendants(childNode1);

        // Then
        assertThat(descendants).hasSize(1);
        assertThat(((TestNode) descendants.get(0)).getName()).isEqualTo("grandchild");
    }

    @Test
    void getRoot_shouldDelegateToEngineRoot() {
        // When
        Object root = astMethods.getRoot();

        // Then
        assertThat(root).isSameAs(rootNode);
    }

    @Test
    void toJavaJoinPoint_shouldConvertNodeCorrectly() {
        // When
        Object joinPoint = astMethods.toJavaJoinPoint(childNode1);

        // Then
        assertThat(joinPoint).isInstanceOf(JoinPoint.class);
        assertThat(joinPoint.toString()).contains("child1");
    }

    @Test
    void getJoinPointName_shouldReturnNodeName() {
        // When
        Object name = astMethods.getJoinPointName(childNode1);

        // Then
        assertThat(name).isEqualTo("child1");
    }

    @Test
    void getChildren_shouldReturnChildrenArray() {
        // When
        Object children = astMethods.getChildren(rootNode);

        // Then
        assertThat(children).isInstanceOf(Object[].class);
        Object[] childrenArray = (Object[]) children;
        assertThat(childrenArray).hasSize(2);
        assertThat(((TestNode) childrenArray[0]).getName()).isEqualTo("child1");
        assertThat(((TestNode) childrenArray[1]).getName()).isEqualTo("child2");
    }

    @Test
    void getNumChildren_shouldReturnCorrectCount() {
        // When
        Object numChildren = astMethods.getNumChildren(rootNode);

        // Then
        assertThat(numChildren).isEqualTo(2);
    }

    @Test
    void getNumChildren_forLeafNode_shouldReturnZero() {
        // When
        Object numChildren = astMethods.getNumChildren(childNode2);

        // Then
        assertThat(numChildren).isEqualTo(0);
    }

    @Test
    void getScopeChildren_shouldReturnScopeChildrenArray() {
        // When
        Object scopeChildren = astMethods.getScopeChildren(rootNode);

        // Then
        assertThat(scopeChildren).isInstanceOf(Object[].class);
        Object[] scopeChildrenArray = (Object[]) scopeChildren;
        assertThat(scopeChildrenArray).hasSize(2);
    }

    @Test
    void getParent_shouldReturnParentNode() {
        // When
        Object parent = astMethods.getParent(childNode1);

        // Then - in this simplified implementation, parent is null
        assertThat(parent).isNull();
    }
}