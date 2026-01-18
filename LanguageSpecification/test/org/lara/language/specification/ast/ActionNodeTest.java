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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ActionNode Tests")
class ActionNodeTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        
        @Test
        @DisplayName("Should create ActionNode with DeclarationNode")
        void shouldCreateActionNodeWithDeclarationNode() {
            DeclarationNode declNode = new DeclarationNode("testAction", "Void");
            ActionNode actionNode = new ActionNode(declNode);
            
            assertThat(actionNode).isNotNull();
            assertThat(actionNode.getChildren()).hasSize(1);
            assertThat(actionNode.getChildren().get(0)).isEqualTo(declNode);
            assertThat(declNode.getParent()).isEqualTo(actionNode);
        }
        
        @Test
        @DisplayName("Should set up declaration relationship correctly")
        void shouldSetupDeclarationRelationshipCorrectly() {
            DeclarationNode declNode = new DeclarationNode("myAction", "Object");
            ActionNode actionNode = new ActionNode(declNode);
            
            DeclarationNode retrievedDecl = actionNode.getDeclaration();
            
            assertThat(retrievedDecl).isEqualTo(declNode);
            assertThat(retrievedDecl.getName()).isEqualTo("myAction");
            assertThat(retrievedDecl.getType()).isEqualTo("Object");
        }
    }
    
    @Nested
    @DisplayName("Declaration Access Tests")
    class DeclarationAccessTests {
        
        private ActionNode actionNode;
        private DeclarationNode declNode;
        
        @BeforeEach
        void setUp() {
            declNode = new DeclarationNode("testAction", "Void");
            actionNode = new ActionNode(declNode);
        }
        
        @Test
        @DisplayName("Should get declaration as first child")
        void shouldGetDeclarationAsFirstChild() {
            DeclarationNode declaration = actionNode.getDeclaration();
            
            assertThat(declaration).isEqualTo(declNode);
            assertThat(declaration).isEqualTo(actionNode.getChildren().get(0));
        }
        
        @Test
        @DisplayName("Should provide access to declaration properties")
        void shouldProvideAccessToDeclarationProperties() {
            DeclarationNode declaration = actionNode.getDeclaration();
            
            assertThat(declaration.getName()).isEqualTo("testAction");
            assertThat(declaration.getType()).isEqualTo("Void");
        }
    }
    
    @Nested
    @DisplayName("Parameter Management Tests")
    class ParameterManagementTests {
        
        private ActionNode actionNode;
        
        @BeforeEach
        void setUp() {
            DeclarationNode declNode = new DeclarationNode("testAction", "Void");
            actionNode = new ActionNode(declNode);
        }
        
        @Test
        @DisplayName("Should add ParameterNode children")
        void shouldAddParameterNodeChildren() {
            ParameterNode paramNode = new ParameterNode("String", "param1", "defaultValue");
            
            actionNode.addChild(paramNode);
            
            assertThat(actionNode.getChildren()).hasSize(2); // DeclarationNode + ParameterNode
            assertThat(actionNode.getChildren().get(1)).isEqualTo(paramNode);
            assertThat(paramNode.getParent()).isEqualTo(actionNode);
        }
        
        @Test
        @DisplayName("Should get parameters excluding declaration")
        void shouldGetParametersExcludingDeclaration() {
            ParameterNode param1 = new ParameterNode("String", "param1", "default1");
            ParameterNode param2 = new ParameterNode("Integer", "param2", "0");
            
            actionNode.addChild(param1);
            actionNode.addChild(param2);
            
            List<ParameterNode> parameters = actionNode.getParameters();
            
            assertThat(parameters).hasSize(2);
            assertThat(parameters).containsExactly(param1, param2);
        }
        
        @Test
        @DisplayName("Should handle no parameters")
        void shouldHandleNoParameters() {
            List<ParameterNode> parameters = actionNode.getParameters();
            
            assertThat(parameters).isEmpty();
            assertThat(actionNode.getChildren()).hasSize(1); // Only DeclarationNode
        }
        
        @Test
        @DisplayName("Should maintain parameter order")
        void shouldMaintainParameterOrder() {
            ParameterNode param1 = new ParameterNode("String", "param1", "first");
            ParameterNode param2 = new ParameterNode("Integer", "param2", "second");
            ParameterNode param3 = new ParameterNode("Boolean", "param3", "third");
            
            actionNode.addChild(param1);
            actionNode.addChild(param2);
            actionNode.addChild(param3);
            
            List<ParameterNode> parameters = actionNode.getParameters();
            
            assertThat(parameters).hasSize(3);
            assertThat(parameters).containsExactly(param1, param2, param3);
        }
    }
    
    @Nested
    @DisplayName("Child Structure Tests")
    class ChildStructureTests {
        
        @Test
        @DisplayName("Should have DeclarationNode as first child always")
        void shouldHaveDeclarationNodeAsFirstChildAlways() {
            DeclarationNode declNode = new DeclarationNode("action", "Void");
            ActionNode actionNode = new ActionNode(declNode);
            
            // Add parameters
            ParameterNode param1 = new ParameterNode("String", "param1", "value1");
            ParameterNode param2 = new ParameterNode("Integer", "param2", "value2");
            actionNode.addChild(param1);
            actionNode.addChild(param2);
            
            assertThat(actionNode.getChildren()).hasSize(3);
            assertThat(actionNode.getChildren().get(0)).isInstanceOf(DeclarationNode.class);
            assertThat(actionNode.getChildren().get(0)).isEqualTo(declNode);
            assertThat(actionNode.getChildren().get(1)).isInstanceOf(ParameterNode.class);
            assertThat(actionNode.getChildren().get(2)).isInstanceOf(ParameterNode.class);
        }
        
        @Test
        @DisplayName("Should correctly identify parameter children")
        void shouldCorrectlyIdentifyParameterChildren() {
            DeclarationNode declNode = new DeclarationNode("action", "Object");
            ActionNode actionNode = new ActionNode(declNode);
            
            ParameterNode param1 = new ParameterNode("String", "param1", "default");
            ParameterNode param2 = new ParameterNode("Integer", "param2", "0");
            actionNode.addChild(param1);
            actionNode.addChild(param2);
            
            // getParameters should only return ParameterNode children
            List<ParameterNode> parameters = actionNode.getParameters();
            assertThat(parameters).hasSize(2);
            assertThat(parameters.get(0)).isEqualTo(param1);
            assertThat(parameters.get(1)).isEqualTo(param2);
            
            // But children should include everything
            assertThat(actionNode.getChildren()).hasSize(3);
        }
    }
    
    @Nested
    @DisplayName("ToolTip Tests")
    class ToolTipTests {
        
        private ActionNode actionNode;
        
        @BeforeEach
        void setUp() {
            DeclarationNode declNode = new DeclarationNode("testAction", "Void");
            actionNode = new ActionNode(declNode);
        }
        
        @Test
        @DisplayName("Should set and get tooltip")
        void shouldSetAndGetTooltip() {
            actionNode.setToolTip("This is an action tooltip");
            
            assertThat(actionNode.getToolTip()).isPresent();
            assertThat(actionNode.getToolTip().get()).isEqualTo("This is an action tooltip");
        }
        
        @Test
        @DisplayName("Should initially have no tooltip")
        void shouldInitiallyHaveNoTooltip() {
            assertThat(actionNode.getToolTip()).isEmpty();
        }
        
        @Test
        @DisplayName("Should handle null tooltip")
        void shouldHandleNullTooltip() {
            actionNode.setToolTip(null);
            
            assertThat(actionNode.getToolTip()).isEmpty();
        }
        
        @Test
        @DisplayName("Should handle empty tooltip")
        void shouldHandleEmptyTooltip() {
            actionNode.setToolTip("");
            
            assertThat(actionNode.getToolTip()).isPresent();
            assertThat(actionNode.getToolTip().get()).isEmpty();
        }
    }
    
    @Nested
    @DisplayName("String Representation Tests")
    class StringRepresentationTests {
        
        @Test
        @DisplayName("Should generate correct toString")
        void shouldGenerateCorrectToString() {
            DeclarationNode declNode = new DeclarationNode("myAction", "Void");
            ActionNode actionNode = new ActionNode(declNode);
            
            String result = actionNode.toString();
            
            assertThat(result).contains("ActionNode");
            assertThat(result).contains("myAction");
        }
        
        @Test
        @DisplayName("Should generate correct toContentString")
        void shouldGenerateCorrectToContentString() {
            DeclarationNode declNode = new DeclarationNode("myAction", "Object");
            ActionNode actionNode = new ActionNode(declNode);
            ParameterNode paramNode = new ParameterNode("String", "param1", "default");
            actionNode.addChild(paramNode);
            
            String result = actionNode.toContentString();
            
            // ActionNode inherits empty toContentString from base class
            assertThat(result).isEmpty();
        }
    }
    
    @Nested
    @DisplayName("Edge Cases and Error Handling Tests")
    class EdgeCasesAndErrorHandlingTests {
        
        @Test
        @DisplayName("Should handle action with many parameters")
        void shouldHandleActionWithManyParameters() {
            DeclarationNode declNode = new DeclarationNode("complexAction", "Object");
            ActionNode actionNode = new ActionNode(declNode);
            
            // Add many parameters
            for (int i = 0; i < 100; i++) {
                ParameterNode param = new ParameterNode("String", "param" + i, "default" + i);
                actionNode.addChild(param);
            }
            
            assertThat(actionNode.getChildren()).hasSize(101); // DeclarationNode + 100 parameters
            assertThat(actionNode.getParameters()).hasSize(100);
            assertThat(actionNode.getDeclaration()).isEqualTo(declNode);
            
            // Check that all parameters are correctly identified
            List<ParameterNode> parameters = actionNode.getParameters();
            for (int i = 0; i < 100; i++) {
                ParameterNode param = parameters.get(i);
                assertThat(param.getName()).isEqualTo("param" + i);
                assertThat(param.getDefaultValue()).isEqualTo("default" + i);
            }
        }
        
        @Test
        @DisplayName("Should maintain parent-child relationships correctly")
        void shouldMaintainParentChildRelationshipsCorrectly() {
            DeclarationNode declNode = new DeclarationNode("parentTestAction", "Void");
            ActionNode actionNode = new ActionNode(declNode);
            ParameterNode paramNode = new ParameterNode("String", "param1", "value");
            
            actionNode.addChild(paramNode);
            
            assertThat(paramNode.getParent()).isEqualTo(actionNode);
            assertThat(declNode.getParent()).isEqualTo(actionNode);
            assertThat(actionNode.getChildren()).contains(paramNode);
            assertThat(actionNode.getChildren()).contains(declNode);
            
            // Test navigation up the tree
            assertThat(paramNode.getParent()).isInstanceOf(ActionNode.class);
            ActionNode parentAction = (ActionNode) paramNode.getParent();
            assertThat(parentAction.getDeclaration().getName()).isEqualTo("parentTestAction");
        }
        
        @Test
        @DisplayName("Should handle different parameter types")
        void shouldHandleDifferentParameterTypes() {
            DeclarationNode declNode = new DeclarationNode("multiTypeAction", "Object");
            ActionNode actionNode = new ActionNode(declNode);
            
            ParameterNode stringParam = new ParameterNode("String", "stringParam", "defaultString");
            ParameterNode intParam = new ParameterNode("Integer", "intParam", "42");
            ParameterNode boolParam = new ParameterNode("Boolean", "boolParam", "true");
            ParameterNode objectParam = new ParameterNode("Object", "objectParam", null);
            
            actionNode.addChild(stringParam);
            actionNode.addChild(intParam);
            actionNode.addChild(boolParam);
            actionNode.addChild(objectParam);
            
            List<ParameterNode> parameters = actionNode.getParameters();
            assertThat(parameters).hasSize(4);
            
            assertThat(parameters.get(0).getType()).isEqualTo("String");
            assertThat(parameters.get(1).getType()).isEqualTo("Integer");
            assertThat(parameters.get(2).getType()).isEqualTo("Boolean");
            assertThat(parameters.get(3).getType()).isEqualTo("Object");
        }
        
        @Test
        @DisplayName("Should handle null declaration name and type")
        void shouldHandleNullDeclarationNameAndType() {
            DeclarationNode declNode = new DeclarationNode(null, null);
            ActionNode actionNode = new ActionNode(declNode);
            
            assertThat(actionNode).isNotNull();
            assertThat(actionNode.getDeclaration()).isEqualTo(declNode);
            assertThat(actionNode.getDeclaration().getName()).isNull();
            assertThat(actionNode.getDeclaration().getType()).isNull();
        }
    }
}
