/**
 * Copyright 2024 SPeCS.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.lara.language.specification.ast;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import pt.up.fe.specs.util.utilities.BuilderWithIndentation;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TypeDefNode Tests")
public class TypeDefNodeTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create TypeDefNode with name")
        void testConstructorWithName() {
            TypeDefNode node = new TypeDefNode("MyType");
            
            assertEquals("MyType", node.getName());
            assertEquals(0, node.getNumChildren());
            assertFalse(node.hasChildren());
        }

        @Test
        @DisplayName("Should create TypeDefNode with empty name")
        void testConstructorWithEmptyName() {
            TypeDefNode node = new TypeDefNode("");
            
            assertEquals("", node.getName());
        }

        @Test
        @DisplayName("Should create TypeDefNode with null name")
        void testConstructorWithNullName() {
            TypeDefNode node = new TypeDefNode(null);
            
            assertNull(node.getName());
        }

        @Test
        @DisplayName("Should create TypeDefNode with complex name")
        void testConstructorWithComplexName() {
            TypeDefNode node = new TypeDefNode("ComplexType_123$WithSpecialChars");
            
            assertEquals("ComplexType_123$WithSpecialChars", node.getName());
        }
    }

    @Nested
    @DisplayName("String Representation Tests")
    class StringRepresentationTests {

        @Test
        @DisplayName("Should generate correct content string")
        void testToContentString() {
            TypeDefNode node = new TypeDefNode("MyType");
            String content = node.toContentString();
            
            assertEquals("name: MyType", content);
        }

        @Test
        @DisplayName("Should handle null name in content string")
        void testToContentStringWithNullName() {
            TypeDefNode node = new TypeDefNode(null);
            String content = node.toContentString();
            
            assertEquals("name: null", content);
        }

        @Test
        @DisplayName("Should handle empty name in content string")
        void testToContentStringWithEmptyName() {
            TypeDefNode node = new TypeDefNode("");
            String content = node.toContentString();
            
            assertEquals("name: ", content);
        }

        @Test
        @DisplayName("Should have meaningful toString")
        void testToString() {
            TypeDefNode node = new TypeDefNode("MyType");
            String str = node.toString();
            
            assertNotNull(str);
            assertTrue(str.contains("TypeDefNode"));
        }
    }

    @Nested
    @DisplayName("JSON Serialization Tests")
    class JsonSerializationTests {

        @Test
        @DisplayName("Should generate correct JSON without children")
        void testToJsonWithoutChildren() {
            TypeDefNode node = new TypeDefNode("MyType");
            BuilderWithIndentation builder = new BuilderWithIndentation();
            
            String json = node.toJson(builder);
            
            assertNotNull(json);
            assertTrue(json.contains("\"type\": \"typedef\""));
            assertTrue(json.contains("\"name\": \"MyType\""));
            assertTrue(json.startsWith("{"));
            assertTrue(json.endsWith("}"));
            assertFalse(json.contains("children"));
        }

        @Test
        @DisplayName("Should generate correct JSON with children")
        void testToJsonWithChildren() {
            TypeDefNode node = new TypeDefNode("MyType");
            DeclarationNode child = new DeclarationNode("field", "String");
            node.addChild(child);
            
            BuilderWithIndentation builder = new BuilderWithIndentation();
            String json = node.toJson(builder);
            
            assertNotNull(json);
            assertTrue(json.contains("\"type\": \"typedef\""));
            assertTrue(json.contains("\"name\": \"MyType\""));
            assertTrue(json.contains("field"));
            assertTrue(json.contains("String"));
        }

        @Test
        @DisplayName("Should handle null name in JSON")
        void testToJsonWithNullName() {
            TypeDefNode node = new TypeDefNode(null);
            BuilderWithIndentation builder = new BuilderWithIndentation();
            
            String json = node.toJson(builder);
            
            assertTrue(json.contains("\"name\": \"null\""));
        }

        @Test
        @DisplayName("Should handle empty name in JSON")
        void testToJsonWithEmptyName() {
            TypeDefNode node = new TypeDefNode("");
            BuilderWithIndentation builder = new BuilderWithIndentation();
            
            String json = node.toJson(builder);
            
            assertTrue(json.contains("\"name\": \"\""));
        }

        @Test
        @DisplayName("Should handle special characters in JSON")
        void testToJsonWithSpecialCharacters() {
            TypeDefNode node = new TypeDefNode("Type\"With\"Quotes");
            BuilderWithIndentation builder = new BuilderWithIndentation();
            
            String json = node.toJson(builder);
            
            assertNotNull(json);
            assertTrue(json.startsWith("{"));
            assertTrue(json.endsWith("}"));
        }
    }

    @Nested
    @DisplayName("HTML Generation Tests")
    class HtmlGenerationTests {

        @Test
        @DisplayName("Should generate correct HTML")
        void testToHtml() {
            TypeDefNode node = new TypeDefNode("MyType");
            String html = node.toHtml();
            
            assertNotNull(html);
            assertTrue(html.contains("MyType"));
            assertTrue(html.contains("div"));
            assertTrue(html.contains("id='toc_container'"));
            assertTrue(html.contains("class='toc_title'"));
        }

        @Test
        @DisplayName("Should handle null name in HTML")
        void testToHtmlWithNullName() {
            TypeDefNode node = new TypeDefNode(null);
            String html = node.toHtml();
            
            assertNotNull(html);
            assertTrue(html.contains("null"));
        }

        @Test
        @DisplayName("Should handle empty name in HTML")
        void testToHtmlWithEmptyName() {
            TypeDefNode node = new TypeDefNode("");
            String html = node.toHtml();
            
            assertNotNull(html);
            assertTrue(html.contains("div"));
        }

        @Test
        @DisplayName("Should handle special characters in HTML")
        void testToHtmlWithSpecialCharacters() {
            TypeDefNode node = new TypeDefNode("Type<With>SpecialChars");
            String html = node.toHtml();
            
            assertNotNull(html);
            assertTrue(html.contains("Type<With>SpecialChars"));
        }
    }

    @Nested
    @DisplayName("Getter Tests")
    class GetterTests {

        @Test
        @DisplayName("Should return correct name")
        void testGetName() {
            TypeDefNode node = new TypeDefNode("TestType");
            assertEquals("TestType", node.getName());
        }

        @Test
        @DisplayName("Should return immutable name")
        void testNameImmutability() {
            TypeDefNode node = new TypeDefNode("TestType");
            String name = node.getName();
            
            assertEquals("TestType", name);
            assertEquals("TestType", node.getName());
        }
    }

    @Nested
    @DisplayName("Child Management Tests")
    class ChildManagementTests {

        @Test
        @DisplayName("Should start with no children")
        void testInitialChildrenState() {
            TypeDefNode node = new TypeDefNode("MyType");
            
            assertEquals(0, node.getNumChildren());
            assertFalse(node.hasChildren());
        }

        @Test
        @DisplayName("Should allow adding children")
        void testAddingChildren() {
            TypeDefNode node = new TypeDefNode("MyType");
            DeclarationNode child = new DeclarationNode("field", "String");
            
            node.addChild(child);
            
            assertEquals(1, node.getNumChildren());
            assertTrue(node.hasChildren());
            assertEquals(child, node.getChild(0));
        }

        @Test
        @DisplayName("Should handle multiple children")
        void testMultipleChildren() {
            TypeDefNode node = new TypeDefNode("MyType");
            DeclarationNode child1 = new DeclarationNode("field1", "String");
            DeclarationNode child2 = new DeclarationNode("field2", "int");
            
            node.addChild(child1);
            node.addChild(child2);
            
            assertEquals(2, node.getNumChildren());
            assertTrue(node.hasChildren());
            assertEquals(child1, node.getChild(0));
            assertEquals(child2, node.getChild(1));
        }
    }

    @Nested
    @DisplayName("Inheritance Tests")
    class InheritanceTests {

        @Test
        @DisplayName("Should inherit LangSpecNode behavior")
        void testInheritedMethods() {
            TypeDefNode node = new TypeDefNode("TestType");
            
            // Test inherited methods
            assertEquals(0, node.getNumChildren());
            assertFalse(node.hasChildren());
            assertNotNull(node.toString());
        }

        @Test
        @DisplayName("Should be instance of LangSpecNode")
        void testInstanceOf() {
            TypeDefNode node = new TypeDefNode("TestType");
            
            assertTrue(node instanceof LangSpecNode);
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle very long names")
        void testVeryLongName() {
            String longName = "A".repeat(1000);
            TypeDefNode node = new TypeDefNode(longName);
            
            assertEquals(longName, node.getName());
        }

        @Test
        @DisplayName("Should handle names with Unicode characters")
        void testUnicodeNames() {
            TypeDefNode node = new TypeDefNode("Type名前");
            
            assertEquals("Type名前", node.getName());
        }

        @Test
        @DisplayName("Should handle names with newlines")
        void testNameWithNewlines() {
            TypeDefNode node = new TypeDefNode("Type\nWith\nNewlines");
            
            assertEquals("Type\nWith\nNewlines", node.getName());
        }
    }
}
