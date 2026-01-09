package org.lara.language.specification.ast;

import org.junit.jupiter.api.Test;
import pt.up.fe.specs.util.utilities.BuilderWithIndentation;

class SimpleJsonTest {

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

    @Test
    void testActualJsonOutput() {
        TestLangSpecNode testNode = new TestLangSpecNode("TestNode");
        
        String json = testNode.toJson();
        System.out.println("Actual JSON output:");
        System.out.println(json);
        
        String childrenJson = testNode.childrenToJson(1);
        System.out.println("Actual children JSON:");
        System.out.println(childrenJson);
        
        // Just check that they're not null
        assert json != null;
        assert childrenJson != null;
    }
}
