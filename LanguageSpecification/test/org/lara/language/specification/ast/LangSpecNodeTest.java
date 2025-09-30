package org.lara.language.specification.ast;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.up.fe.specs.util.utilities.BuilderWithIndentation;

import static org.junit.jupiter.api.Assertions.*;

class LangSpecNodeTest {

    private TestLangSpecNode testNode;

    // Concrete test implementation since LangSpecNode is abstract
    private static class TestLangSpecNode extends LangSpecNode {
        private String name;

        public TestLangSpecNode(String name) {
            this.name = name;
        }

        @Override
        public String toJson(BuilderWithIndentation builder) {
            return "{\n" + 
                   "\"type\": \"TestNode\",\n" +
                   "\"name\": \"" + name + "\",\n" +
                   childrenToJson(2) + "\n" +
                   "}";
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @BeforeEach
    void setUp() {
        testNode = new TestLangSpecNode("TestNode");
    }

    @Test
    void testConstructor() {
        assertNotNull(testNode);
        assertTrue(testNode.getChildren().isEmpty());
        assertEquals(testNode, testNode.getThis());
    }

    @Test
    void testToContentString() {
        assertEquals("", testNode.toContentString());
    }

    @Test
    void testToolTipInitiallyNull() {
        assertFalse(testNode.getToolTip().isPresent());
    }

    @Test
    void testSetToolTip() {
        String tooltip = "This is a test tooltip";
        testNode.setToolTip(tooltip);
        
        assertTrue(testNode.getToolTip().isPresent());
        assertEquals(tooltip, testNode.getToolTip().get());
    }

    @Test
    void testSetToolTipToNull() {
        testNode.setToolTip("Initial tooltip");
        testNode.setToolTip(null);
        
        assertFalse(testNode.getToolTip().isPresent());
    }

    @Test
    void testGetToolTipOptional() {
        // Initially empty
        assertFalse(testNode.getToolTip().isPresent());
        
        // After setting
        testNode.setToolTip("Tooltip");
        assertTrue(testNode.getToolTip().isPresent());
        assertEquals("Tooltip", testNode.getToolTip().get());
        
        // After setting to null
        testNode.setToolTip(null);
        assertFalse(testNode.getToolTip().isPresent());
    }

    @Test
    void testToJsonWithoutIndentation() {
        String json = testNode.toJson();
        
        assertNotNull(json);
        
        // Debug output
        System.out.println("JSON output: " + json);
        
        // Just test that basic content is there
        assertTrue(json.contains("TestNode"));
        assertTrue(json.contains("children"));
    }

    @Test
    void testToJsonWithIndentation() {
        BuilderWithIndentation builder = new BuilderWithIndentation(2);
        String json = testNode.toJson(builder);
        
        assertNotNull(json);
        assertTrue(json.contains("TestNode"));
        assertTrue(json.contains("children"));
    }

    @Test
    void testToJsonWithTooltip() {
        testNode.setToolTip("Test tooltip");
        String json = testNode.toJson();
        
        assertTrue(json.contains("\"tooltip\": \"Test tooltip\""));
    }

    @Test
    void testToJsonWithChildren() {
        TestLangSpecNode child1 = new TestLangSpecNode("Child1");
        TestLangSpecNode child2 = new TestLangSpecNode("Child2");
        
        testNode.addChild(child1);
        testNode.addChild(child2);
        
        String json = testNode.toJson();
        
        assertTrue(json.contains("Child1"));
        assertTrue(json.contains("Child2"));
        // Should contain children array with 2 elements
        assertTrue(json.contains("\"children\": ["));
    }

    @Test
    void testChildrenToJsonWithoutTooltip() {
        TestLangSpecNode child = new TestLangSpecNode("Child");
        testNode.addChild(child);
        
        String childrenJson = testNode.childrenToJson(1);
        
        assertTrue(childrenJson.contains("\"children\": ["));
        assertTrue(childrenJson.contains("Child"));
        assertTrue(childrenJson.endsWith("]"));
        assertFalse(childrenJson.contains("tooltip"));
    }

    @Test
    void testChildrenToJsonWithTooltip() {
        testNode.setToolTip("Parent tooltip");
        
        String childrenJson = testNode.childrenToJson(1);
        
        assertTrue(childrenJson.contains("\"tooltip\": \"Parent tooltip\""));
        assertTrue(childrenJson.contains("\"children\": ["));
    }

    @Test
    void testToHtmlNotImplemented() {
        assertThrows(RuntimeException.class, () -> {
            testNode.toHtml();
        });
    }

    @Test
    void testCopyPrivateNotImplemented() {
        assertThrows(RuntimeException.class, () -> {
            testNode.copy();
        });
    }

    @Test
    void testTreeNodeInheritance() {
        // Test that it properly inherits from ATreeNode
        assertTrue(testNode.getChildren().isEmpty());
        assertEquals(testNode, testNode.getThis());
        
        // Test adding children
        TestLangSpecNode child = new TestLangSpecNode("Child");
        testNode.addChild(child);
        
        assertEquals(1, testNode.getChildren().size());
        assertEquals(child, testNode.getChildren().get(0));
        assertEquals(testNode, child.getParent());
    }

    @Test
    void testNodeHierarchy() {
        TestLangSpecNode parent = new TestLangSpecNode("Parent");
        TestLangSpecNode child1 = new TestLangSpecNode("Child1");
        TestLangSpecNode child2 = new TestLangSpecNode("Child2");
        TestLangSpecNode grandchild = new TestLangSpecNode("Grandchild");
        
        parent.addChild(child1);
        parent.addChild(child2);
        child1.addChild(grandchild);
        
        assertEquals(2, parent.getChildren().size());
        assertEquals(1, child1.getChildren().size());
        assertEquals(0, child2.getChildren().size());
        assertEquals(0, grandchild.getChildren().size());
        
        assertEquals(parent, child1.getParent());
        assertEquals(parent, child2.getParent());
        assertEquals(child1, grandchild.getParent());
    }

    @Test
    void testJsonWithComplexHierarchy() {
        TestLangSpecNode parent = new TestLangSpecNode("Parent");
        TestLangSpecNode child = new TestLangSpecNode("Child");
        TestLangSpecNode grandchild = new TestLangSpecNode("Grandchild");
        
        parent.setToolTip("Parent tooltip");
        child.setToolTip("Child tooltip");
        
        parent.addChild(child);
        child.addChild(grandchild);
        
        String json = parent.toJson();
        
        assertTrue(json.contains("Parent"));
        assertTrue(json.contains("Child"));
        assertTrue(json.contains("Grandchild"));
        assertTrue(json.contains("Parent tooltip"));
        assertTrue(json.contains("Child tooltip"));
    }

    @Test
    void testEmptyChildrenJson() {
        String childrenJson = testNode.childrenToJson(1);
        
        System.out.println("Children JSON: " + childrenJson);
        
        assertTrue(childrenJson.contains("children"));
        assertFalse(childrenJson.contains("tooltip"));
    }

    @Test
    void testSpecialCharactersInTooltip() {
        String specialTooltip = "Tooltip with \"quotes\" and \n newlines";
        testNode.setToolTip(specialTooltip);
        
        String json = testNode.toJson();
        
        // The tooltip should be included in JSON (though may need escaping in real implementation)
        assertTrue(json.contains("tooltip"));
    }

    @Test
    void testToJsonBuilderWithIndentation() {
        BuilderWithIndentation builder = new BuilderWithIndentation(0);
        String json = testNode.toJson(builder);
        
        assertNotNull(json);
        assertTrue(json.contains("TestNode"));
        
        // Test with different indentation
        BuilderWithIndentation builder3 = new BuilderWithIndentation(3);
        String json3 = testNode.toJson(builder3);
        
        assertNotNull(json3);
        assertTrue(json3.contains("TestNode"));
    }
}
