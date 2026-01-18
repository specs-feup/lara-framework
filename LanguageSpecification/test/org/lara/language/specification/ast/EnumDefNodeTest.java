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

@DisplayName("EnumDefNode Tests")
public class EnumDefNodeTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create EnumDefNode with name")
        void testConstructorWithName() {
            EnumDefNode node = new EnumDefNode("MyEnum");
            
            assertEquals("MyEnum", node.getName());
            assertEquals(0, node.getNumChildren());
            assertFalse(node.hasChildren());
        }

        @Test
        @DisplayName("Should create EnumDefNode with empty name")
        void testConstructorWithEmptyName() {
            EnumDefNode node = new EnumDefNode("");
            
            assertEquals("", node.getName());
        }

        @Test
        @DisplayName("Should create EnumDefNode with null name")
        void testConstructorWithNullName() {
            EnumDefNode node = new EnumDefNode(null);
            
            assertNull(node.getName());
        }

        @Test
        @DisplayName("Should create EnumDefNode with complex name")
        void testConstructorWithComplexName() {
            EnumDefNode node = new EnumDefNode("ComplexEnum_123$WithSpecialChars");
            
            assertEquals("ComplexEnum_123$WithSpecialChars", node.getName());
        }
    }

    @Nested
    @DisplayName("String Representation Tests")
    class StringRepresentationTests {

        @Test
        @DisplayName("Should generate correct content string")
        void testToContentString() {
            EnumDefNode node = new EnumDefNode("MyEnum");
            String content = node.toContentString();
            
            assertEquals("name: MyEnum", content);
        }

        @Test
        @DisplayName("Should handle null name in content string")
        void testToContentStringWithNullName() {
            EnumDefNode node = new EnumDefNode(null);
            String content = node.toContentString();
            
            assertEquals("name: null", content);
        }

        @Test
        @DisplayName("Should handle empty name in content string")
        void testToContentStringWithEmptyName() {
            EnumDefNode node = new EnumDefNode("");
            String content = node.toContentString();
            
            assertEquals("name: ", content);
        }

        @Test
        @DisplayName("Should have meaningful toString")
        void testToString() {
            EnumDefNode node = new EnumDefNode("MyEnum");
            String str = node.toString();
            
            assertNotNull(str);
            assertTrue(str.contains("EnumDefNode"));
        }
    }

    @Nested
    @DisplayName("JSON Serialization Tests")
    class JsonSerializationTests {

        @Test
        @DisplayName("Should generate correct JSON without children")
        void testToJsonWithoutChildren() {
            EnumDefNode node = new EnumDefNode("MyEnum");
            BuilderWithIndentation builder = new BuilderWithIndentation();
            
            String json = node.toJson(builder);
            
            assertNotNull(json);
            assertTrue(json.contains("\"type\": \"enum\""));
            assertTrue(json.contains("\"name\": \"MyEnum\""));
            assertTrue(json.startsWith("{"));
            assertTrue(json.endsWith("}"));
            assertFalse(json.contains("children"));
        }

        @Test
        @DisplayName("Should generate correct JSON with children")
        void testToJsonWithChildren() {
            EnumDefNode node = new EnumDefNode("MyEnum");
            DeclarationNode child = new DeclarationNode("VALUE1", "String");
            node.addChild(child);
            
            BuilderWithIndentation builder = new BuilderWithIndentation();
            String json = node.toJson(builder);
            
            assertNotNull(json);
            assertTrue(json.contains("\"type\": \"enum\""));
            assertTrue(json.contains("\"name\": \"MyEnum\""));
            assertTrue(json.contains("VALUE1"));
            assertTrue(json.contains("String"));
        }

        @Test
        @DisplayName("Should handle null name in JSON")
        void testToJsonWithNullName() {
            EnumDefNode node = new EnumDefNode(null);
            BuilderWithIndentation builder = new BuilderWithIndentation();
            
            String json = node.toJson(builder);
            
            assertTrue(json.contains("\"name\": \"null\""));
        }

        @Test
        @DisplayName("Should handle empty name in JSON")
        void testToJsonWithEmptyName() {
            EnumDefNode node = new EnumDefNode("");
            BuilderWithIndentation builder = new BuilderWithIndentation();
            
            String json = node.toJson(builder);
            
            assertTrue(json.contains("\"name\": \"\""));
        }

        @Test
        @DisplayName("Should handle special characters in JSON")
        void testToJsonWithSpecialCharacters() {
            EnumDefNode node = new EnumDefNode("Enum\"With\"Quotes");
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
            EnumDefNode node = new EnumDefNode("MyEnum");
            String html = node.toHtml();
            
            assertNotNull(html);
            assertTrue(html.contains("MyEnum"));
            assertTrue(html.contains("div"));
            assertTrue(html.contains("id='toc_container'"));
            assertTrue(html.contains("class='toc_title'"));
        }

        @Test
        @DisplayName("Should handle null name in HTML")
        void testToHtmlWithNullName() {
            EnumDefNode node = new EnumDefNode(null);
            String html = node.toHtml();
            
            assertNotNull(html);
            assertTrue(html.contains("null"));
        }

        @Test
        @DisplayName("Should handle empty name in HTML")
        void testToHtmlWithEmptyName() {
            EnumDefNode node = new EnumDefNode("");
            String html = node.toHtml();
            
            assertNotNull(html);
            assertTrue(html.contains("div"));
        }

        @Test
        @DisplayName("Should handle special characters in HTML")
        void testToHtmlWithSpecialCharacters() {
            EnumDefNode node = new EnumDefNode("Enum<With>SpecialChars");
            String html = node.toHtml();
            
            assertNotNull(html);
            assertTrue(html.contains("Enum<With>SpecialChars"));
        }
    }

    @Nested
    @DisplayName("Getter Tests")
    class GetterTests {

        @Test
        @DisplayName("Should return correct name")
        void testGetName() {
            EnumDefNode node = new EnumDefNode("TestEnum");
            assertEquals("TestEnum", node.getName());
        }

        @Test
        @DisplayName("Should return immutable name")
        void testNameImmutability() {
            EnumDefNode node = new EnumDefNode("TestEnum");
            String name = node.getName();
            
            assertEquals("TestEnum", name);
            assertEquals("TestEnum", node.getName());
        }
    }

    @Nested
    @DisplayName("Child Management Tests")
    class ChildManagementTests {

        @Test
        @DisplayName("Should start with no children")
        void testInitialChildrenState() {
            EnumDefNode node = new EnumDefNode("MyEnum");
            
            assertEquals(0, node.getNumChildren());
            assertFalse(node.hasChildren());
        }

        @Test
        @DisplayName("Should allow adding children")
        void testAddingChildren() {
            EnumDefNode node = new EnumDefNode("MyEnum");
            DeclarationNode child = new DeclarationNode("VALUE1", "String");
            
            node.addChild(child);
            
            assertEquals(1, node.getNumChildren());
            assertTrue(node.hasChildren());
            assertEquals(child, node.getChild(0));
        }

        @Test
        @DisplayName("Should handle multiple children (enum values)")
        void testMultipleChildren() {
            EnumDefNode node = new EnumDefNode("MyEnum");
            DeclarationNode value1 = new DeclarationNode("VALUE1", "String");
            DeclarationNode value2 = new DeclarationNode("VALUE2", "String");
            DeclarationNode value3 = new DeclarationNode("VALUE3", "String");
            
            node.addChild(value1);
            node.addChild(value2);
            node.addChild(value3);
            
            assertEquals(3, node.getNumChildren());
            assertTrue(node.hasChildren());
            assertEquals(value1, node.getChild(0));
            assertEquals(value2, node.getChild(1));
            assertEquals(value3, node.getChild(2));
        }
    }

    @Nested
    @DisplayName("Inheritance Tests")
    class InheritanceTests {

        @Test
        @DisplayName("Should inherit LangSpecNode behavior")
        void testInheritedMethods() {
            EnumDefNode node = new EnumDefNode("TestEnum");
            
            // Test inherited methods
            assertEquals(0, node.getNumChildren());
            assertFalse(node.hasChildren());
            assertNotNull(node.toString());
        }

        @Test
        @DisplayName("Should be instance of LangSpecNode")
        void testInstanceOf() {
            EnumDefNode node = new EnumDefNode("TestEnum");
            
            assertTrue(node instanceof LangSpecNode);
        }
    }

    @Nested
    @DisplayName("Enum-Specific Tests")
    class EnumSpecificTests {

        @Test
        @DisplayName("Should handle typical enum values")
        void testTypicalEnumValues() {
            EnumDefNode node = new EnumDefNode("Status");
            DeclarationNode active = new DeclarationNode("ACTIVE", "int");
            DeclarationNode inactive = new DeclarationNode("INACTIVE", "int");
            DeclarationNode pending = new DeclarationNode("PENDING", "int");
            
            node.addChild(active);
            node.addChild(inactive);
            node.addChild(pending);
            
            assertEquals("Status", node.getName());
            assertEquals(3, node.getNumChildren());
        }

        @Test
        @DisplayName("Should handle empty enum")
        void testEmptyEnum() {
            EnumDefNode node = new EnumDefNode("EmptyEnum");
            
            assertEquals("EmptyEnum", node.getName());
            assertEquals(0, node.getNumChildren());
            assertFalse(node.hasChildren());
        }

        @Test
        @DisplayName("Should differentiate from TypeDefNode")
        void testDifferenceFromTypeDef() {
            EnumDefNode enumNode = new EnumDefNode("MyEnum");
            TypeDefNode typeDefNode = new TypeDefNode("MyType");
            
            assertNotEquals(enumNode.getClass(), typeDefNode.getClass());
            
            // JSON should contain different type identifiers
            BuilderWithIndentation builder1 = new BuilderWithIndentation();
            BuilderWithIndentation builder2 = new BuilderWithIndentation();
            
            String enumJson = enumNode.toJson(builder1);
            String typeDefJson = typeDefNode.toJson(builder2);
            
            assertTrue(enumJson.contains("\"type\": \"enum\""));
            assertTrue(typeDefJson.contains("\"type\": \"typedef\""));
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle very long names")
        void testVeryLongName() {
            String longName = "A".repeat(1000);
            EnumDefNode node = new EnumDefNode(longName);
            
            assertEquals(longName, node.getName());
        }

        @Test
        @DisplayName("Should handle names with Unicode characters")
        void testUnicodeNames() {
            EnumDefNode node = new EnumDefNode("Enum名前");
            
            assertEquals("Enum名前", node.getName());
        }

        @Test
        @DisplayName("Should handle names with newlines")
        void testNameWithNewlines() {
            EnumDefNode node = new EnumDefNode("Enum\nWith\nNewlines");
            
            assertEquals("Enum\nWith\nNewlines", node.getName());
        }

        @Test
        @DisplayName("Should handle numerical enum names")
        void testNumericalEnumNames() {
            EnumDefNode node = new EnumDefNode("123Enum");
            
            assertEquals("123Enum", node.getName());
        }
    }
}
