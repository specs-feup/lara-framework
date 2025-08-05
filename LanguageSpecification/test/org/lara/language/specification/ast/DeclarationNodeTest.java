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
 * specific language governing permissions and limitations under the License. under the License.
 */

package org.lara.language.specification.ast;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.lara.language.specification.dsl.Declaration;
import org.lara.language.specification.dsl.types.Primitive;
import pt.up.fe.specs.util.utilities.BuilderWithIndentation;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DeclarationNode Tests")
public class DeclarationNodeTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create DeclarationNode with name and type")
        void testConstructorWithNameAndType() {
            DeclarationNode node = new DeclarationNode("testName", "String");
            
            assertEquals("testName", node.getName());
            assertEquals("String", node.getType());
            assertEquals(0, node.getNumChildren());
            assertFalse(node.hasChildren());
        }

        @Test
        @DisplayName("Should create DeclarationNode from Declaration object")
        void testConstructorWithDeclaration() {
            Declaration declaration = new Declaration(Primitive.INT, "testDecl");
            DeclarationNode node = new DeclarationNode(declaration);
            
            assertEquals("testDecl", node.getName());
            assertEquals("int", node.getType());
        }

        @Test
        @DisplayName("Should handle null name")
        void testConstructorWithNullName() {
            DeclarationNode node = new DeclarationNode(null, "String");
            
            assertNull(node.getName());
            assertEquals("String", node.getType());
        }

        @Test
        @DisplayName("Should handle null type")
        void testConstructorWithNullType() {
            DeclarationNode node = new DeclarationNode("testName", null);
            
            assertEquals("testName", node.getName());
            assertNull(node.getType());
        }

        @Test
        @DisplayName("Should handle empty strings")
        void testConstructorWithEmptyStrings() {
            DeclarationNode node = new DeclarationNode("", "");
            
            assertEquals("", node.getName());
            assertEquals("", node.getType());
        }
    }

    @Nested
    @DisplayName("Attribute String Tests")
    class AttributeStringTests {

        private DeclarationNode node;

        @BeforeEach
        void setUp() {
            node = new DeclarationNode("testName", "testType");
        }

        @Test
        @DisplayName("Should have default attribute strings")
        void testDefaultAttributeStrings() {
            assertEquals("name", node.getNameAttributeString());
            assertEquals("type", node.getTypeAttributeString());
        }

        @Test
        @DisplayName("Should set custom name attribute string")
        void testSetNameAttributeString() {
            node.setNameAttributeString("customName");
            assertEquals("customName", node.getNameAttributeString());
        }

        @Test
        @DisplayName("Should set custom type attribute string")
        void testSetTypeAttributeString() {
            node.setTypeAttributeString("customType");
            assertEquals("customType", node.getTypeAttributeString());
        }

        @Test
        @DisplayName("Should handle null attribute strings")
        void testSetNullAttributeStrings() {
            node.setNameAttributeString(null);
            node.setTypeAttributeString(null);
            
            assertNull(node.getNameAttributeString());
            assertNull(node.getTypeAttributeString());
        }

        @Test
        @DisplayName("Should handle empty attribute strings")
        void testSetEmptyAttributeStrings() {
            node.setNameAttributeString("");
            node.setTypeAttributeString("");
            
            assertEquals("", node.getNameAttributeString());
            assertEquals("", node.getTypeAttributeString());
        }
    }

    @Nested
    @DisplayName("String Representation Tests")
    class StringRepresentationTests {

        @Test
        @DisplayName("Should generate correct content string")
        void testToContentString() {
            DeclarationNode node = new DeclarationNode("testName", "testType");
            String content = node.toContentString();
            
            assertEquals("name: testName, type: testType", content);
        }

        @Test
        @DisplayName("Should use custom attribute strings in content")
        void testToContentStringWithCustomAttributes() {
            DeclarationNode node = new DeclarationNode("testName", "testType");
            node.setNameAttributeString("customName");
            node.setTypeAttributeString("customType");
            
            String content = node.toContentString();
            assertEquals("customName: testName, customType: testType", content);
        }

        @Test
        @DisplayName("Should handle null values in content string")
        void testToContentStringWithNullValues() {
            DeclarationNode node = new DeclarationNode(null, null);
            String content = node.toContentString();
            
            assertEquals("name: null, type: null", content);
        }

        @Test
        @DisplayName("Should have meaningful toString")
        void testToString() {
            DeclarationNode node = new DeclarationNode("testName", "testType");
            String str = node.toString();
            
            assertNotNull(str);
            assertTrue(str.contains("DeclarationNode"));
        }
    }

    @Nested
    @DisplayName("JSON Serialization Tests")
    class JsonSerializationTests {

        @Test
        @DisplayName("Should generate correct JSON")
        void testToJson() {
            DeclarationNode node = new DeclarationNode("testName", "testType");
            BuilderWithIndentation builder = new BuilderWithIndentation();
            
            String json = node.toJson(builder);
            
            assertNotNull(json);
            assertTrue(json.contains("\"type\": \"testType\""));
            assertTrue(json.contains("\"name\": \"testName\""));
            assertTrue(json.startsWith("{"));
            assertTrue(json.endsWith("}"));
        }

        @Test
        @DisplayName("Should use custom attribute strings in JSON")
        void testToJsonWithCustomAttributes() {
            DeclarationNode node = new DeclarationNode("testName", "testType");
            node.setNameAttributeString("customName");
            node.setTypeAttributeString("customType");
            
            BuilderWithIndentation builder = new BuilderWithIndentation();
            String json = node.toJson(builder);
            
            assertTrue(json.contains("\"customType\": \"testType\""));
            assertTrue(json.contains("\"customName\": \"testName\""));
        }

        @Test
        @DisplayName("Should handle null values in JSON")
        void testToJsonWithNullValues() {
            DeclarationNode node = new DeclarationNode(null, null);
            BuilderWithIndentation builder = new BuilderWithIndentation();
            
            String json = node.toJson(builder);
            
            assertTrue(json.contains("\"type\": \"null\""));
            assertTrue(json.contains("\"name\": \"null\""));
        }

        @Test
        @DisplayName("Should handle empty values in JSON")
        void testToJsonWithEmptyValues() {
            DeclarationNode node = new DeclarationNode("", "");
            BuilderWithIndentation builder = new BuilderWithIndentation();
            
            String json = node.toJson(builder);
            
            assertTrue(json.contains("\"type\": \"\""));
            assertTrue(json.contains("\"name\": \"\""));
        }

        @Test
        @DisplayName("Should handle special characters in JSON")
        void testToJsonWithSpecialCharacters() {
            DeclarationNode node = new DeclarationNode("name\"with\"quotes", "type\\with\\backslashes");
            BuilderWithIndentation builder = new BuilderWithIndentation();
            
            String json = node.toJson(builder);
            
            assertNotNull(json);
            // The JSON should be properly formatted even with special characters
            assertTrue(json.startsWith("{"));
            assertTrue(json.endsWith("}"));
        }
    }

    @Nested
    @DisplayName("Getter Tests")
    class GetterTests {

        @Test
        @DisplayName("Should return correct name")
        void testGetName() {
            DeclarationNode node = new DeclarationNode("testName", "testType");
            assertEquals("testName", node.getName());
        }

        @Test
        @DisplayName("Should return correct type")
        void testGetType() {
            DeclarationNode node = new DeclarationNode("testName", "testType");
            assertEquals("testType", node.getType());
        }

        @Test
        @DisplayName("Should return immutable name")
        void testNameImmutability() {
            DeclarationNode node = new DeclarationNode("testName", "testType");
            String name = node.getName();
            
            // The name should be the same instance or equal
            assertEquals("testName", name);
            assertEquals("testName", node.getName());
        }

        @Test
        @DisplayName("Should return immutable type")
        void testTypeImmutability() {
            DeclarationNode node = new DeclarationNode("testName", "testType");
            String type = node.getType();
            
            // The type should be the same instance or equal
            assertEquals("testType", type);
            assertEquals("testType", node.getType());
        }
    }

    @Nested
    @DisplayName("Complex Types Tests")
    class ComplexTypesTests {

        @Test
        @DisplayName("Should handle generic types")
        void testGenericTypes() {
            DeclarationNode node = new DeclarationNode("list", "List<String>");
            
            assertEquals("list", node.getName());
            assertEquals("List<String>", node.getType());
        }

        @Test
        @DisplayName("Should handle nested generic types")
        void testNestedGenericTypes() {
            DeclarationNode node = new DeclarationNode("map", "Map<String, List<Integer>>");
            
            assertEquals("map", node.getName());
            assertEquals("Map<String, List<Integer>>", node.getType());
        }

        @Test
        @DisplayName("Should handle array types")
        void testArrayTypes() {
            DeclarationNode node = new DeclarationNode("array", "String[]");
            
            assertEquals("array", node.getName());
            assertEquals("String[]", node.getType());
        }

        @Test
        @DisplayName("Should handle qualified names")
        void testQualifiedNames() {
            DeclarationNode node = new DeclarationNode("qualified_name", "java.util.List");
            
            assertEquals("qualified_name", node.getName());
            assertEquals("java.util.List", node.getType());
        }
    }

    @Nested
    @DisplayName("Inheritance Tests")
    class InheritanceTests {

        @Test
        @DisplayName("Should inherit LangSpecNode behavior")
        void testInheritedMethods() {
            DeclarationNode node = new DeclarationNode("test", "String");
            
            // Test inherited methods
            assertEquals(0, node.getNumChildren());
            assertFalse(node.hasChildren());
            assertNotNull(node.toString());
        }

        @Test
        @DisplayName("Should allow adding children")
        void testAddingChildren() {
            DeclarationNode node = new DeclarationNode("parent", "String");
            DeclarationNode child = new DeclarationNode("child", "int");
            
            node.addChild(child);
            
            assertEquals(1, node.getNumChildren());
            assertTrue(node.hasChildren());
            assertEquals(child, node.getChild(0));
        }
    }
}
