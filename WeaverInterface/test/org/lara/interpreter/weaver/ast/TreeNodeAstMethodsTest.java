package org.lara.interpreter.weaver.ast;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lara.interpreter.weaver.fixtures.TestWeaverEngine;
import org.lara.interpreter.weaver.interf.JoinPoint;

import pt.up.fe.specs.util.treenode.ATreeNode;

class TreeNodeAstMethodsTest {

    private TreeNodeAstMethods<TestTreeNode> treeNodeAstMethods;
    private TestWeaverEngine testEngine;
    private TestTreeNode rootNode;
    private TestTreeNode childNode1;
    private TestTreeNode childNode2;
    private TestTreeNode grandchildNode;
    
    // Test tree node implementation for testing
    static class TestTreeNode extends ATreeNode<TestTreeNode> {
        private final String name;

        public TestTreeNode(String name) {
            super(null);
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        protected TestTreeNode copyPrivate() {
            return new TestTreeNode(name);
        }

        @Override
        public String toContentString() {
            return name;
        }

        @Override
        public String toString() {
            return "TestTreeNode{" + name + "}";
        }
    }

    @BeforeEach
    void setUp() {
        testEngine = new TestWeaverEngine();
        
        // Create test tree structure:
        // root
        //  ├── child1
        //  │   └── grandchild
        //  └── child2
        rootNode = new TestTreeNode("root");
        childNode1 = new TestTreeNode("child1");
        childNode2 = new TestTreeNode("child2");
        grandchildNode = new TestTreeNode("grandchild");

        rootNode.addChild(childNode1);
        rootNode.addChild(childNode2);
        childNode1.addChild(grandchildNode);

        // Create functions for TreeNodeAstMethods
        Function<TestTreeNode, JoinPoint> toJoinPointFunction = node -> {
            JoinPoint mockJp = mock(JoinPoint.class);
            when(mockJp.toString()).thenReturn("JoinPoint[" + node.getName() + "]");
            return mockJp;
        };
        
        Function<TestTreeNode, String> toJoinPointNameFunction = TestTreeNode::getName;
        
        Function<TestTreeNode, List<TestTreeNode>> scopeChildrenGetter = node -> node.getChildren();

        treeNodeAstMethods = new TreeNodeAstMethods<>(
                testEngine,
                TestTreeNode.class,
                toJoinPointFunction,
                toJoinPointNameFunction,
                scopeChildrenGetter
        );
    }

    @Test
    void getNodeClass_shouldReturnCorrectClass() {
        // When
        Class<TestTreeNode> nodeClass = treeNodeAstMethods.getNodeClass();

        // Then
        assertThat(nodeClass).isEqualTo(TestTreeNode.class);
    }

    @Test
    void toJavaJoinPoint_shouldUseProvidedFunction() {
        // When
        Object joinPoint = treeNodeAstMethods.toJavaJoinPoint(childNode1);

        // Then
        assertThat(joinPoint).isInstanceOf(JoinPoint.class);
        assertThat(joinPoint.toString()).contains("child1");
    }

    @Test
    void getJoinPointName_shouldUseProvidedNameFunction() {
        // When
        Object name = treeNodeAstMethods.getJoinPointName(childNode1);

        // Then
        assertThat(name).isEqualTo("child1");
    }

    @Test
    void getChildren_shouldReturnChildrenArray() {
        // When
        Object children = treeNodeAstMethods.getChildren(rootNode);

        // Then
        assertThat(children).isInstanceOf(Object[].class);
        Object[] childrenArray = (Object[]) children;
        assertThat(childrenArray).hasSize(2);
        assertThat(((TestTreeNode) childrenArray[0]).getName()).isEqualTo("child1");
        assertThat(((TestTreeNode) childrenArray[1]).getName()).isEqualTo("child2");
    }

    @Test
    void getChildren_forLeafNode_shouldReturnEmptyArray() {
        // When
        Object children = treeNodeAstMethods.getChildren(childNode2);

        // Then
        assertThat(children).isInstanceOf(Object[].class);
        Object[] childrenArray = (Object[]) children;
        assertThat(childrenArray).isEmpty();
    }

    @Test
    void getNumChildren_shouldReturnCorrectCount() {
        // When
        Object numChildren = treeNodeAstMethods.getNumChildren(rootNode);

        // Then
        assertThat(numChildren).isEqualTo(2);
    }

    @Test
    void getNumChildren_shouldUseTreeNodeMethod() {
        // When
        Object numChildrenRoot = treeNodeAstMethods.getNumChildren(rootNode);
        Object numChildrenChild1 = treeNodeAstMethods.getNumChildren(childNode1);
        Object numChildrenLeaf = treeNodeAstMethods.getNumChildren(childNode2);

        // Then
        assertThat(numChildrenRoot).isEqualTo(2); // root has 2 children
        assertThat(numChildrenChild1).isEqualTo(1); // child1 has 1 child (grandchild)
        assertThat(numChildrenLeaf).isEqualTo(0); // child2 is leaf
    }

    @Test
    void getScopeChildren_shouldUseProvidedFunction() {
        // When
        Object scopeChildren = treeNodeAstMethods.getScopeChildren(rootNode);

        // Then
        assertThat(scopeChildren).isInstanceOf(Object[].class);
        Object[] scopeChildrenArray = (Object[]) scopeChildren;
        assertThat(scopeChildrenArray).hasSize(2);
        assertThat(((TestTreeNode) scopeChildrenArray[0]).getName()).isEqualTo("child1");
        assertThat(((TestTreeNode) scopeChildrenArray[1]).getName()).isEqualTo("child2");
    }

    @Test
    void getParent_shouldReturnTreeNodeParent() {
        // When
        Object parent = treeNodeAstMethods.getParent(childNode1);

        // Then
        assertThat(parent).isSameAs(rootNode);
    }

    @Test
    void getParent_forRootNode_shouldReturnNull() {
        // When
        Object parent = treeNodeAstMethods.getParent(rootNode);

        // Then
        assertThat(parent).isNull();
    }

    @Test
    void getDescendants_shouldReturnAllDescendantsInOrder() {
        // When
        @SuppressWarnings("unchecked")
        List<Object> descendants = (List<Object>) treeNodeAstMethods.getDescendants(rootNode);

        // Then
        assertThat(descendants).hasSize(3);
        assertThat(((TestTreeNode) descendants.get(0)).getName()).isEqualTo("child1");
        assertThat(((TestTreeNode) descendants.get(1)).getName()).isEqualTo("grandchild");
        assertThat(((TestTreeNode) descendants.get(2)).getName()).isEqualTo("child2");
    }

    @Test
    void getRoot_shouldDelegateToEngine() {
        // When
        Object root = treeNodeAstMethods.getRoot();

        // Then
        assertThat(root).isSameAs(testEngine.getRootNode());
    }

    @Test
    void integration_allMethodsWorkTogether() {
        // Test that all methods work correctly together
        
        // Get children
        Object[] children = (Object[]) treeNodeAstMethods.getChildren(rootNode);
        assertThat(children).hasSize(2);
        
        // Get first child and verify its properties
        TestTreeNode firstChild = (TestTreeNode) children[0];
        assertThat(treeNodeAstMethods.getJoinPointName(firstChild)).isEqualTo("child1");
        assertThat(treeNodeAstMethods.getNumChildren(firstChild)).isEqualTo(1);
        assertThat(treeNodeAstMethods.getParent(firstChild)).isSameAs(rootNode);
        
        // Verify join point creation
        JoinPoint jp = (JoinPoint) treeNodeAstMethods.toJavaJoinPoint(firstChild);
        assertThat(jp.toString()).contains("child1");
    }
}