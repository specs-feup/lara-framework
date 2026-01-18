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
import pt.up.fe.specs.util.utilities.BuilderWithIndentation;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AttributeNode Tests")
public class AttributeNodeTest {

    private DeclarationNode testDeclaration;

    @BeforeEach
    void setUp() {
        testDeclaration = new DeclarationNode("testAttr", "String");
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create AttributeNode with DeclarationNode")
        void testConstructorWithDeclaration() {
            AttributeNode node = new AttributeNode(testDeclaration);
            
            assertEquals(1, node.getNumChildren());
            assertTrue(node.hasChildren());
            assertSame(testDeclaration, node.getDeclaration());
        }

        @Test
        @DisplayName("Should throw exception with null declaration")
        void testConstructorWithNullDeclaration() {
            assertThrows(Exception.class, () -> {
                new AttributeNode(null);
            });
        }
    }

    @Nested
    @DisplayName("Child Management Tests")
    class ChildManagementTests {

        @Test
        @DisplayName("Should return correct declaration")
        void testGetDeclaration() {
            AttributeNode node = new AttributeNode(testDeclaration);
            DeclarationNode declaration = node.getDeclaration();
            
            assertNotNull(declaration);
            assertEquals("testAttr", declaration.getName());
            assertEquals("String", declaration.getType());
        }

        @Test
        @DisplayName("Should return parameters correctly")
        void testGetParametersWithOnlyDeclaration() {
            AttributeNode node = new AttributeNode(testDeclaration);
            List<DeclarationNode> parameters = node.getParameters();
            
            assertNotNull(parameters);
            assertTrue(parameters.isEmpty());
        }

        @Test
        @DisplayName("Should handle multiple declaration children")
        void testGetParametersWithMultipleDeclarations() {
            AttributeNode node = new AttributeNode(testDeclaration);
            
            // Add additional declaration nodes (as parameters)
            DeclarationNode param1 = new DeclarationNode("param1", "int");
            DeclarationNode param2 = new DeclarationNode("param2", "boolean");
            node.addChild(param1);
            node.addChild(param2);
            
            List<DeclarationNode> parameters = node.getParameters();
            assertEquals(2, parameters.size());
            assertEquals("param1", parameters.get(0).getName());
            assertEquals("param2", parameters.get(1).getName());
        }

        @Test
        @DisplayName("Should manage children correctly")
        void testChildrenManagement() {
            AttributeNode node = new AttributeNode(testDeclaration);
            
            assertEquals(1, node.getNumChildren());
            assertTrue(node.hasChildren());
            
            // Add more children
            DeclarationNode param = new DeclarationNode("param", "double");
            node.addChild(param);
            
            assertEquals(2, node.getNumChildren());
        }
    }

    @Nested
    @DisplayName("JSON Serialization Tests")
    class JsonSerializationTests {

        @Test
        @DisplayName("Should generate correct JSON with single declaration")
        void testToJsonWithSingleDeclaration() {
            AttributeNode node = new AttributeNode(testDeclaration);
            BuilderWithIndentation builder = new BuilderWithIndentation();
            
            String json = node.toJson(builder);
            
            assertNotNull(json);
            assertTrue(json.contains("\"type\": \"attribute\""));
            assertTrue(json.startsWith("{"));
            assertTrue(json.endsWith("}"));
            // Should contain the child declaration's JSON
            assertTrue(json.contains("testAttr"));
            assertTrue(json.contains("String"));
        }

        @Test
        @DisplayName("Should generate correct JSON with multiple children")
        void testToJsonWithMultipleChildren() {
            AttributeNode node = new AttributeNode(testDeclaration);
            node.addChild(new DeclarationNode("param1", "int"));
            node.addChild(new DeclarationNode("param2", "boolean"));
            
            BuilderWithIndentation builder = new BuilderWithIndentation();
            String json = node.toJson(builder);
            
            assertNotNull(json);
            assertTrue(json.contains("\"type\": \"attribute\""));
            assertTrue(json.contains("testAttr"));
            assertTrue(json.contains("param1"));
            assertTrue(json.contains("param2"));
        }

        @Test
        @DisplayName("Should handle JSON with different indentation")
        void testToJsonWithIndentation() {
            AttributeNode node = new AttributeNode(testDeclaration);
            BuilderWithIndentation builder = new BuilderWithIndentation();
            builder.increaseIndentation();
            
            String json = node.toJson(builder);
            
            assertNotNull(json);
            assertTrue(json.contains("\"type\": \"attribute\""));
        }
    }

    @Nested
    @DisplayName("String Representation Tests")
    class StringRepresentationTests {

        @Test
        @DisplayName("Should have meaningful toString")
        void testToString() {
            AttributeNode node = new AttributeNode(testDeclaration);
            String str = node.toString();
            
            assertNotNull(str);
            assertTrue(str.contains("AttributeNode"));
        }

        @Test
        @DisplayName("Should handle toContentString")
        void testToContentString() {
            AttributeNode node = new AttributeNode(testDeclaration);
            String content = node.toContentString();
            
            // AttributeNode doesn't override toContentString, so it should use default
            assertNotNull(content);
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle complex declaration names")
        void testComplexDeclarationNames() {
            DeclarationNode complexDecl = new DeclarationNode("complex_name_123", "List<Map<String, Integer>>");
            AttributeNode node = new AttributeNode(complexDecl);
            
            assertEquals("complex_name_123", node.getDeclaration().getName());
            assertEquals("List<Map<String, Integer>>", node.getDeclaration().getType());
        }

        @Test
        @DisplayName("Should handle empty type and name")
        void testEmptyTypeAndName() {
            DeclarationNode emptyDecl = new DeclarationNode("", "");
            AttributeNode node = new AttributeNode(emptyDecl);
            
            assertEquals("", node.getDeclaration().getName());
            assertEquals("", node.getDeclaration().getType());
        }

        @Test
        @DisplayName("Should handle special characters in declaration")
        void testSpecialCharacters() {
            DeclarationNode specialDecl = new DeclarationNode("attr$with%special@chars", "Type<T>");
            AttributeNode node = new AttributeNode(specialDecl);
            
            assertEquals("attr$with%special@chars", node.getDeclaration().getName());
            assertEquals("Type<T>", node.getDeclaration().getType());
        }
    }

    @Nested
    @DisplayName("Inheritance Tests")
    class InheritanceTests {

        @Test
        @DisplayName("Should inherit LangSpecNode behavior")
        void testInheritedMethods() {
            AttributeNode node = new AttributeNode(testDeclaration);
            
            // Test inherited methods
            assertTrue(node.hasChildren());
            assertEquals(1, node.getNumChildren());
            assertNotNull(node.toString());
        }

        @Test
        @DisplayName("Should handle node hierarchy correctly")
        void testNodeHierarchy() {
            AttributeNode node = new AttributeNode(testDeclaration);
            
            // The declaration should be a child
            assertEquals(testDeclaration, node.getChild(0));
            assertEquals(testDeclaration, node.getChild(DeclarationNode.class, 0));
        }
    }
}
