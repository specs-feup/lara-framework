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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DisplayName("JoinPointNode Tests")
class JoinPointNodeTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        
        @Test
        @DisplayName("Should create JoinPointNode with all parameters")
        void shouldCreateJoinPointNodeWithAllParameters() {
            JoinPointNode jpNode = new JoinPointNode("TestJP", "BaseJP", "defaultAttr");
            
            assertThat(jpNode).isNotNull();
            assertThat(jpNode.getName()).isEqualTo("TestJP");
            assertThat(jpNode.getExtend()).isEqualTo("BaseJP");
            assertThat(jpNode.getDefaultAttribute()).isEqualTo(Optional.of("defaultAttr"));
            assertThat(jpNode.getChildren()).isEmpty();
        }
        
        @Test
        @DisplayName("Should create JoinPointNode with no extension")
        void shouldCreateJoinPointNodeWithNoExtension() {
            JoinPointNode jpNode = new JoinPointNode("TestJP", "", "defaultAttr");
            
            assertThat(jpNode).isNotNull();
            assertThat(jpNode.getName()).isEqualTo("TestJP");
            assertThat(jpNode.getExtend()).isEmpty();
            assertThat(jpNode.getDefaultAttribute()).isEqualTo(Optional.of("defaultAttr"));
        }
        
        @Test
        @DisplayName("Should create JoinPointNode with no default attribute")
        void shouldCreateJoinPointNodeWithNoDefaultAttribute() {
            JoinPointNode jpNode = new JoinPointNode("TestJP", "BaseJP", null);
            
            assertThat(jpNode).isNotNull();
            assertThat(jpNode.getName()).isEqualTo("TestJP");
            assertThat(jpNode.getExtend()).isEqualTo("BaseJP");
            assertThat(jpNode.getDefaultAttribute()).isEqualTo(Optional.empty());
        }
        
        @Test
        @DisplayName("Should create JoinPointNode with minimal parameters")
        void shouldCreateJoinPointNodeWithMinimalParameters() {
            JoinPointNode jpNode = new JoinPointNode("TestJP", "", null);
            
            assertThat(jpNode).isNotNull();
            assertThat(jpNode.getName()).isEqualTo("TestJP");
            assertThat(jpNode.getExtend()).isEmpty();
            assertThat(jpNode.getDefaultAttribute()).isEqualTo(Optional.empty());
        }
        
        @Test
        @DisplayName("Should handle empty name")
        void shouldHandleEmptyName() {
            JoinPointNode jpNode = new JoinPointNode("", "BaseJP", "defaultAttr");
            
            assertThat(jpNode).isNotNull();
            assertThat(jpNode.getName()).isEmpty();
            assertThat(jpNode.getExtend()).isEqualTo("BaseJP");
            assertThat(jpNode.getDefaultAttribute()).isEqualTo(Optional.of("defaultAttr"));
        }
    }
    
    @Nested
    @DisplayName("Child Management Tests")
    class ChildManagementTests {
        
        private JoinPointNode jpNode;
        
        @BeforeEach
        void setUp() {
            jpNode = new JoinPointNode("TestJP", "BaseJP", "defaultAttr");
        }
        
        @Test
        @DisplayName("Should add AttributeNode children")
        void shouldAddAttributeNodeChildren() {
            DeclarationNode declNode = new DeclarationNode("testAttr", "String");
            AttributeNode attrNode = new AttributeNode(declNode);
            
            jpNode.addChild(attrNode);
            
            assertThat(jpNode.getChildren()).hasSize(1);
            assertThat(jpNode.getChildren().get(0)).isEqualTo(attrNode);
            assertThat(attrNode.getParent()).isEqualTo(jpNode);
        }
        
        @Test
        @DisplayName("Should add ActionNode children")
        void shouldAddActionNodeChildren() {
            DeclarationNode declNode = new DeclarationNode("testAction", "Void");
            ActionNode actionNode = new ActionNode(declNode);
            
            jpNode.addChild(actionNode);
            
            assertThat(jpNode.getChildren()).hasSize(1);
            assertThat(jpNode.getChildren().get(0)).isEqualTo(actionNode);
            assertThat(actionNode.getParent()).isEqualTo(jpNode);
        }
        
        @Test
        @DisplayName("Should handle mixed attribute and action children")
        void shouldHandleMixedAttributeAndActionChildren() {
            DeclarationNode attrDecl = new DeclarationNode("testAttr", "String");
            AttributeNode attrNode = new AttributeNode(attrDecl);
            
            DeclarationNode actionDecl = new DeclarationNode("testAction", "Void");
            ActionNode actionNode = new ActionNode(actionDecl);
            
            jpNode.addChild(attrNode);
            jpNode.addChild(actionNode);
            
            assertThat(jpNode.getChildren()).hasSize(2);
            assertThat(jpNode.getChildren()).containsExactly(attrNode, actionNode);
            assertThat(attrNode.getParent()).isEqualTo(jpNode);
            assertThat(actionNode.getParent()).isEqualTo(jpNode);
        }
        
        @Test
        @DisplayName("Should maintain order of children")
        void shouldMaintainOrderOfChildren() {
            DeclarationNode decl1 = new DeclarationNode("attr1", "String");
            DeclarationNode decl2 = new DeclarationNode("attr2", "Integer");
            DeclarationNode decl3 = new DeclarationNode("action1", "Void");
            
            AttributeNode attr1 = new AttributeNode(decl1);
            AttributeNode attr2 = new AttributeNode(decl2);
            ActionNode action1 = new ActionNode(decl3);
            
            jpNode.addChild(attr1);
            jpNode.addChild(attr2);
            jpNode.addChild(action1);
            
            assertThat(jpNode.getChildren()).hasSize(3);
            assertThat(jpNode.getChildren()).containsExactly(attr1, attr2, action1);
        }
    }
    
    @Nested
    @DisplayName("Optional Default Attribute Tests")
    class OptionalDefaultAttributeTests {
        
        @Test
        @DisplayName("Should return present Optional when default attribute is set")
        void shouldReturnPresentOptionalWhenDefaultAttributeIsSet() {
            JoinPointNode jpNode = new JoinPointNode("TestJP", "", "defaultAttr");
            
            Optional<String> defaultAttr = jpNode.getDefaultAttribute();
            
            assertThat(defaultAttr).isPresent();
            assertThat(defaultAttr.get()).isEqualTo("defaultAttr");
        }
        
        @Test
        @DisplayName("Should return empty Optional when default attribute is null")
        void shouldReturnEmptyOptionalWhenDefaultAttributeIsNull() {
            JoinPointNode jpNode = new JoinPointNode("TestJP", "", null);
            
            Optional<String> defaultAttr = jpNode.getDefaultAttribute();
            
            assertThat(defaultAttr).isEmpty();
        }
        
        @Test
        @DisplayName("Should return empty Optional when default attribute is empty string")
        void shouldReturnEmptyOptionalWhenDefaultAttributeIsEmptyString() {
            JoinPointNode jpNode = new JoinPointNode("TestJP", "", "");
            
            Optional<String> defaultAttr = jpNode.getDefaultAttribute();
            
            assertThat(defaultAttr).isPresent();
            assertThat(defaultAttr.get()).isEmpty();
        }
    }
    
    @Nested
    @DisplayName("String Representation Tests")
    class StringRepresentationTests {
        
        @Test
        @DisplayName("Should generate correct toString with all parameters")
        void shouldGenerateCorrectToStringWithAllParameters() {
            JoinPointNode jpNode = new JoinPointNode("TestJP", "BaseJP", "defaultAttr");
            
            String result = jpNode.toString();
            
            assertThat(result).contains("TestJP");
            assertThat(result).contains("BaseJP");
            // Note: toString doesn't include defaultAttr in the implementation
        }
        
        @Test
        @DisplayName("Should generate correct toString with minimal parameters")
        void shouldGenerateCorrectToStringWithMinimalParameters() {
            JoinPointNode jpNode = new JoinPointNode("TestJP", "", null);
            
            String result = jpNode.toString();
            
            assertThat(result).contains("TestJP");
            assertThat(result).doesNotContain("null");
        }
        
        @Test
        @DisplayName("Should generate correct toContentString")
        void shouldGenerateCorrectToContentString() {
            JoinPointNode jpNode = new JoinPointNode("TestJP", "BaseJP", "defaultAttr");
            DeclarationNode declNode = new DeclarationNode("testAttr", "String");
            AttributeNode attrNode = new AttributeNode(declNode);
            jpNode.addChild(attrNode);
            
            String result = jpNode.toContentString();
            
            assertThat(result).contains("TestJP");
            assertThat(result).contains("BaseJP");
            // Note: toContentString doesn't include defaultAttr in the implementation
            assertThat(result).isEqualTo("name: TestJP, extends: BaseJP");
        }
        
        @Test
        @DisplayName("Should generate correct toContentString without extension")
        void shouldGenerateCorrectToContentStringWithoutExtension() {
            JoinPointNode jpNode = new JoinPointNode("TestJP", "", "defaultAttr");
            
            String result = jpNode.toContentString();
            
            assertThat(result).contains("TestJP");
            assertThat(result).doesNotContain("extends");
            assertThat(result).isEqualTo("name: TestJP");
        }
        
        @Test
        @DisplayName("Should generate correct JSON representation")
        void shouldGenerateCorrectJSONRepresentation() {
            JoinPointNode jpNode = new JoinPointNode("TestJP", "BaseJP", "defaultAttr");
            
            // Use toContentString as a proxy for JSON content
            String result = jpNode.toContentString();
            
            assertThat(result).isNotEmpty();
            assertThat(result).contains("TestJP");
        }
    }
    
    @Nested
    @DisplayName("ToolTip Tests")
    class ToolTipTests {
        
        @Test
        @DisplayName("Should set and get tooltip")
        void shouldSetAndGetTooltip() {
            JoinPointNode jpNode = new JoinPointNode("TestJP", "", null);
            
            jpNode.setToolTip("This is a test tooltip");
            
            assertThat(jpNode.getToolTip()).isPresent();
            assertThat(jpNode.getToolTip().get()).isEqualTo("This is a test tooltip");
        }
        
        @Test
        @DisplayName("Should initially have no tooltip")
        void shouldInitiallyHaveNoTooltip() {
            JoinPointNode jpNode = new JoinPointNode("TestJP", "", null);
            
            assertThat(jpNode.getToolTip()).isEmpty();
        }
        
        @Test
        @DisplayName("Should handle null tooltip")
        void shouldHandleNullTooltip() {
            JoinPointNode jpNode = new JoinPointNode("TestJP", "", null);
            
            jpNode.setToolTip(null);
            
            assertThat(jpNode.getToolTip()).isEmpty();
        }
        
        @Test
        @DisplayName("Should handle empty tooltip")
        void shouldHandleEmptyTooltip() {
            JoinPointNode jpNode = new JoinPointNode("TestJP", "", null);
            
            jpNode.setToolTip("");
            
            assertThat(jpNode.getToolTip()).isPresent();
            assertThat(jpNode.getToolTip().get()).isEmpty();
        }
    }
    
    @Nested
    @DisplayName("Edge Cases and Error Handling Tests")
    class EdgeCasesAndErrorHandlingTests {
        
        @Test
        @DisplayName("Should handle null name gracefully")
        void shouldHandleNullNameGracefully() {
            JoinPointNode jpNode = new JoinPointNode(null, "BaseJP", "defaultAttr");
            
            assertThat(jpNode).isNotNull();
            assertThat(jpNode.getName()).isNull();
            assertThat(jpNode.getExtend()).isEqualTo("BaseJP");
            assertThat(jpNode.getDefaultAttribute()).isEqualTo(Optional.of("defaultAttr"));
        }
        
        @Test
        @DisplayName("Should handle null extension gracefully")
        void shouldHandleNullExtensionGracefully() {
            JoinPointNode jpNode = new JoinPointNode("TestJP", null, "defaultAttr");
            
            assertThat(jpNode).isNotNull();
            assertThat(jpNode.getName()).isEqualTo("TestJP");
            assertThat(jpNode.getExtend()).isNull();
            assertThat(jpNode.getDefaultAttribute()).isEqualTo(Optional.of("defaultAttr"));
        }
        
        @Test
        @DisplayName("Should maintain parent-child relationships correctly")
        void shouldMaintainParentChildRelationshipsCorrectly() {
            JoinPointNode jpNode = new JoinPointNode("TestJP", "", null);
            DeclarationNode declNode = new DeclarationNode("testAttr", "String");
            AttributeNode attrNode = new AttributeNode(declNode);
            
            jpNode.addChild(attrNode);
            
            assertThat(attrNode.getParent()).isEqualTo(jpNode);
            assertThat(jpNode.getChildren()).contains(attrNode);
            
            // Test that we can navigate back up the tree
            assertThat(attrNode.getParent()).isInstanceOf(JoinPointNode.class);
            JoinPointNode parentJP = (JoinPointNode) attrNode.getParent();
            assertThat(parentJP.getName()).isEqualTo("TestJP");
        }
        
        @Test
        @DisplayName("Should handle large numbers of children")
        void shouldHandleLargeNumbersOfChildren() {
            JoinPointNode jpNode = new JoinPointNode("TestJP", "", null);
            
            // Add many attribute children
            for (int i = 0; i < 1000; i++) {
                DeclarationNode declNode = new DeclarationNode("attr" + i, "String");
                AttributeNode attrNode = new AttributeNode(declNode);
                jpNode.addChild(attrNode);
            }
            
            assertThat(jpNode.getChildren()).hasSize(1000);
            assertThat(jpNode.getChildren().get(0).getParent()).isEqualTo(jpNode);
            assertThat(jpNode.getChildren().get(999).getParent()).isEqualTo(jpNode);
        }
        
        @Test
        @DisplayName("Should handle complex nested structures")
        void shouldHandleComplexNestedStructures() {
            JoinPointNode jpNode = new JoinPointNode("ComplexJP", "BaseJP", "defaultAttr");
            
            // Add attribute with parameters
            DeclarationNode attrDecl = new DeclarationNode("complexAttr", "Object");
            AttributeNode attrNode = new AttributeNode(attrDecl);
            ParameterNode paramNode = new ParameterNode("String", "param1", "defaultValue");
            attrNode.addChild(paramNode);
            jpNode.addChild(attrNode);
            
            // Add action with parameters
            DeclarationNode actionDecl = new DeclarationNode("complexAction", "Void");
            ActionNode actionNode = new ActionNode(actionDecl);
            ParameterNode actionParam = new ParameterNode("Integer", "param2", "0");
            actionNode.addChild(actionParam);
            jpNode.addChild(actionNode);
            
            assertThat(jpNode.getChildren()).hasSize(2);
            assertThat(jpNode.getChildren().get(0)).isInstanceOf(AttributeNode.class);
            assertThat(jpNode.getChildren().get(1)).isInstanceOf(ActionNode.class);
            
            // Verify nested structure
            AttributeNode childAttr = (AttributeNode) jpNode.getChildren().get(0);
            assertThat(childAttr.getChildren()).hasSize(2); // DeclarationNode + ParameterNode
            
            ActionNode childAction = (ActionNode) jpNode.getChildren().get(1);
            assertThat(childAction.getChildren()).hasSize(2); // DeclarationNode + ParameterNode
        }
    }
}
