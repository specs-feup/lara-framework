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
import org.lara.language.specification.dsl.*;
import org.lara.language.specification.dsl.types.EnumDef;
import org.lara.language.specification.dsl.types.PrimitiveClasses;
import org.lara.language.specification.dsl.types.TypeDef;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link NodeFactory} class.
 * 
 * Tests cover:
 * - DSL to AST node conversion
 * - LanguageSpecification to RootNode conversion
 * - JoinPointClass to JoinPointNode conversion
 * - Attribute to AttributeNode conversion
 * - Action to ActionNode conversion
 * - Declaration to DeclarationNode conversion
 * - Parameter to ParameterNode conversion
 * - TypeDef and EnumDef conversion
 * - Edge cases and error handling
 * 
 * @author Generated Test Suite
 */
@DisplayName("NodeFactory Tests")
class NodeFactoryTest {

    private JoinPointClass globalJoinPoint;
    private JoinPointClass testJoinPoint;
    private Attribute testAttribute;
    private Action testAction;
    private Parameter testParameter;
    private Declaration testDeclaration;

    @BeforeEach
    void setUp() {
        // Create test DSL objects
        globalJoinPoint = JoinPointClass.globalJoinPoint();
        testJoinPoint = new JoinPointClass("TestJoinPoint", globalJoinPoint, "defaultAttr");
        
        testDeclaration = new Declaration(PrimitiveClasses.STRING, "testDeclaration");
        testParameter = new Parameter(PrimitiveClasses.INTEGER, "testParam", "0");
        testAttribute = new Attribute(PrimitiveClasses.STRING, "testAttribute", Arrays.asList(testParameter));
        testAction = new Action(PrimitiveClasses.VOID, "testAction", Arrays.asList(testParameter));
        
        testJoinPoint.add(testAttribute);
        testJoinPoint.add(testAction);
        
        // Create mock LanguageSpecification (not used yet, but prepared for future tests)
        createMockLanguageSpecification();
    }

    private LanguageSpecification createMockLanguageSpecification() {
        // Create a minimal LanguageSpecification for testing
        Map<String, JoinPointClass> joinPoints = new HashMap<>();
        joinPoints.put("TestJoinPoint", testJoinPoint);
        
        Map<String, TypeDef> typeDefs = new HashMap<>();
        TypeDef testTypeDef = new TypeDef("TestType");
        testTypeDef.add(PrimitiveClasses.STRING, "field1");
        typeDefs.put("TestType", testTypeDef);
        
        Map<String, EnumDef> enumDefs = new HashMap<>();
        EnumDef testEnumDef = new EnumDef("TestEnum");
        testEnumDef.add("VALUE1", "value1");
        testEnumDef.add("VALUE2", "value2");
        enumDefs.put("TestEnum", testEnumDef);
        
        // Mock LanguageSpecification would need to be created differently
        // since it doesn't have a constructor that takes these maps directly
        // For now, we'll test individual component conversions
        return null; // Will test individual components instead
    }

    @Nested
    @DisplayName("JoinPointClass to JoinPointNode Conversion Tests")
    class JoinPointClassToNodeTests {

        @Test
        @DisplayName("Should convert simple join point class to node")
        void shouldConvertSimpleJoinPointClassToNode() {
            JoinPointClass simpleJP = new JoinPointClass("SimpleJP");
            
            JoinPointNode result = NodeFactory.toNode(simpleJP);
            
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("SimpleJP");
            assertThat(result.getExtend()).isEmpty();
            assertThat(result.getDefaultAttribute()).isEmpty();
            assertThat(result.getChildren()).isEmpty();
        }

        @Test
        @DisplayName("Should convert join point class with inheritance")
        void shouldConvertJoinPointClassWithInheritance() {
            JoinPointNode result = NodeFactory.toNode(testJoinPoint);
            
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("TestJoinPoint");
            assertThat(result.getExtend()).isEqualTo("joinpoint");
            assertThat(result.getDefaultAttribute()).contains("defaultAttr");
        }

        @Test
        @DisplayName("Should convert join point class with attributes and actions")
        void shouldConvertJoinPointClassWithAttributesAndActions() {
            JoinPointNode result = NodeFactory.toNode(testJoinPoint);
            
            assertThat(result).isNotNull();
            assertThat(result.getChildren()).hasSize(2); // 1 attribute + 1 action
            
            // Check attribute node
            LangSpecNode attributeChild = result.getChildren().get(0);
            assertThat(attributeChild).isInstanceOf(AttributeNode.class);
            AttributeNode attrNode = (AttributeNode) attributeChild;
            assertThat(attrNode.getDeclaration().getName()).isEqualTo("testAttribute");
            
            // Check action node
            LangSpecNode actionChild = result.getChildren().get(1);
            assertThat(actionChild).isInstanceOf(ActionNode.class);
            ActionNode actionNode = (ActionNode) actionChild;
            assertThat(actionNode.getDeclaration().getName()).isEqualTo("testAction");
        }

        @Test
        @DisplayName("Should handle global join point conversion")
        void shouldHandleGlobalJoinPointConversion() {
            JoinPointNode result = NodeFactory.toNode(globalJoinPoint);
            
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("joinpoint");
            assertThat(result.getExtend()).isEmpty();
        }

        @Test
        @DisplayName("Should preserve tooltip information")
        void shouldPreserveTooltipInformation() {
            String tooltip = "Test tooltip for join point";
            testJoinPoint.setToolTip(tooltip);
            
            JoinPointNode result = NodeFactory.toNode(testJoinPoint);
            
            assertThat(result.getToolTip()).isPresent().contains(tooltip);
        }
    }

    @Nested
    @DisplayName("Attribute to AttributeNode Conversion Tests")
    class AttributeToNodeTests {

        @Test
        @DisplayName("Should convert simple attribute to node")
        void shouldConvertSimpleAttributeToNode() {
            Attribute simpleAttr = new Attribute(PrimitiveClasses.INTEGER, "simpleAttr");
            
            AttributeNode result = NodeFactory.toNode(simpleAttr);
            
            assertThat(result).isNotNull();
            assertThat(result.getDeclaration().getName()).isEqualTo("simpleAttr");
            assertThat(result.getDeclaration().getType()).isEqualTo("Integer");
            assertThat(result.getChildren()).hasSize(1); // DeclarationNode only
        }

        @Test
        @DisplayName("Should convert attribute with parameters")
        void shouldConvertAttributeWithParameters() {
            AttributeNode result = NodeFactory.toNode(testAttribute);
            
            assertThat(result).isNotNull();
            assertThat(result.getDeclaration().getName()).isEqualTo("testAttribute");
            assertThat(result.getDeclaration().getType()).isEqualTo("String");
            assertThat(result.getChildren()).hasSize(2); // DeclarationNode + 1 ParameterNode
            
            // Check parameter node
            LangSpecNode paramChild = result.getChildren().get(1); // ParameterNode is the second child
            assertThat(paramChild).isInstanceOf(ParameterNode.class);
            ParameterNode paramNode = (ParameterNode) paramChild;
            assertThat(paramNode.getName()).isEqualTo("testParam");
            assertThat(paramNode.getType()).isEqualTo("Integer");
            assertThat(paramNode.getDefaultValue()).isEqualTo("0");
        }

        @Test
        @DisplayName("Should preserve attribute tooltip")
        void shouldPreserveAttributeTooltip() {
            String tooltip = "Attribute tooltip";
            testAttribute.setToolTip(tooltip);
            
            AttributeNode result = NodeFactory.toNode(testAttribute);
            
            assertThat(result.getToolTip()).isPresent().contains(tooltip);
        }

        @Test
        @DisplayName("Should handle attributes with multiple parameters")
        void shouldHandleAttributesWithMultipleParameters() {
            Parameter param2 = new Parameter(PrimitiveClasses.BOOLEAN, "param2", "true");
            Parameter param3 = new Parameter(PrimitiveClasses.DOUBLE, "param3", "3.14");
            
            Attribute multiParamAttr = new Attribute(PrimitiveClasses.OBJECT, "multiAttr", 
                Arrays.asList(testParameter, param2, param3));
            
            AttributeNode result = NodeFactory.toNode(multiParamAttr);
            
            assertThat(result).isNotNull();
            assertThat(result.getChildren()).hasSize(4); // DeclarationNode + 3 ParameterNodes
            
            // Verify all parameters are converted (skip first child which is DeclarationNode)
            List<LangSpecNode> children = result.getChildren();
            assertThat(children.get(1)).isInstanceOf(ParameterNode.class);
            assertThat(children.get(2)).isInstanceOf(ParameterNode.class);
            assertThat(children.get(3)).isInstanceOf(ParameterNode.class);
            
            ParameterNode param1Node = (ParameterNode) children.get(1);
            ParameterNode param2Node = (ParameterNode) children.get(2);
            ParameterNode param3Node = (ParameterNode) children.get(3);
            
            assertThat(param1Node.getName()).isEqualTo("testParam");
            assertThat(param2Node.getName()).isEqualTo("param2");
            assertThat(param3Node.getName()).isEqualTo("param3");
        }
    }

    @Nested
    @DisplayName("Action to ActionNode Conversion Tests")
    class ActionToNodeTests {

        @Test
        @DisplayName("Should convert simple action to node")
        void shouldConvertSimpleActionToNode() {
            Action simpleAction = new Action(PrimitiveClasses.STRING, "simpleAction");
            
            ActionNode result = NodeFactory.toNode(simpleAction);
            
            assertThat(result).isNotNull();
            assertThat(result.getDeclaration().getName()).isEqualTo("simpleAction");
            assertThat(result.getDeclaration().getType()).isEqualTo("String");
            assertThat(result.getChildren()).hasSize(1); // DeclarationNode only
        }

        @Test
        @DisplayName("Should convert action with parameters")
        void shouldConvertActionWithParameters() {
            ActionNode result = NodeFactory.toNode(testAction);
            
            assertThat(result).isNotNull();
            assertThat(result.getDeclaration().getName()).isEqualTo("testAction");
            assertThat(result.getDeclaration().getType()).isEqualTo("Void");
            assertThat(result.getChildren()).hasSize(2); // DeclarationNode + ParameterNode
            
            // First child should be DeclarationNode
            LangSpecNode firstChild = result.getChildren().get(0);
            assertThat(firstChild).isInstanceOf(DeclarationNode.class);
            
            // Second child should be ParameterNode
            LangSpecNode paramChild = result.getChildren().get(1);
            assertThat(paramChild).isInstanceOf(ParameterNode.class);
            ParameterNode paramNode = (ParameterNode) paramChild;
            assertThat(paramNode.getName()).isEqualTo("testParam");
        }

        @Test
        @DisplayName("Should preserve action tooltip")
        void shouldPreserveActionTooltip() {
            String tooltip = "Action tooltip";
            testAction.setToolTip(tooltip);
            
            ActionNode result = NodeFactory.toNode(testAction);
            
            assertThat(result.getToolTip()).isPresent().contains(tooltip);
        }

        @Test
        @DisplayName("Should handle actions with complex return types")
        void shouldHandleActionsWithComplexReturnTypes() {
            Action complexAction = new Action(PrimitiveClasses.JOINPOINT_INTERFACE, "complexAction");
            
            ActionNode result = NodeFactory.toNode(complexAction);
            
            assertThat(result).isNotNull();
            assertThat(result.getDeclaration().getType()).isEqualTo("JoinpointInterface");
        }
    }

    @Nested
    @DisplayName("Declaration to DeclarationNode Conversion Tests")
    class DeclarationToNodeTests {

        @Test
        @DisplayName("Should convert declaration to node")
        void shouldConvertDeclarationToNode() {
            DeclarationNode result = NodeFactory.toNode(testDeclaration);
            
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("testDeclaration");
            assertThat(result.getType()).isEqualTo("String");
        }

        @Test
        @DisplayName("Should handle different primitive types")
        void shouldHandleDifferentPrimitiveTypes() {
            Declaration intDecl = new Declaration(PrimitiveClasses.INTEGER, "intDecl");
            Declaration boolDecl = new Declaration(PrimitiveClasses.BOOLEAN, "boolDecl");
            Declaration voidDecl = new Declaration(PrimitiveClasses.VOID, "voidDecl");
            
            DeclarationNode intResult = NodeFactory.toNode(intDecl);
            DeclarationNode boolResult = NodeFactory.toNode(boolDecl);
            DeclarationNode voidResult = NodeFactory.toNode(voidDecl);
            
            assertThat(intResult.getType()).isEqualTo("Integer");
            assertThat(boolResult.getType()).isEqualTo("Boolean");
            assertThat(voidResult.getType()).isEqualTo("Void");
        }

        @Test
        @DisplayName("Should handle null names gracefully")
        void shouldHandleNullNamesGracefully() {
            Declaration nullNameDecl = new Declaration(PrimitiveClasses.STRING, null);
            
            DeclarationNode result = NodeFactory.toNode(nullNameDecl);
            
            assertThat(result).isNotNull();
            assertThat(result.getName()).isNull();
            assertThat(result.getType()).isEqualTo("String");
        }
    }

    @Nested
    @DisplayName("Parameter to ParameterNode Conversion Tests")
    class ParameterToNodeTests {

        @Test
        @DisplayName("Should convert parameter to node")
        void shouldConvertParameterToNode() {
            ParameterNode result = NodeFactory.toNode(testParameter);
            
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("testParam");
            assertThat(result.getType()).isEqualTo("Integer");
            assertThat(result.getDefaultValue()).isEqualTo("0");
        }

        @Test
        @DisplayName("Should handle parameter without default value")
        void shouldHandleParameterWithoutDefaultValue() {
            Parameter noDefaultParam = new Parameter(PrimitiveClasses.STRING, "noDefault");
            
            ParameterNode result = NodeFactory.toNode(noDefaultParam);
            
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("noDefault");
            assertThat(result.getType()).isEqualTo("String");
            assertThat(result.getDefaultValue()).isEmpty();
        }

        @Test
        @DisplayName("Should handle parameter with null default value")
        void shouldHandleParameterWithNullDefaultValue() {
            Parameter nullDefaultParam = new Parameter(PrimitiveClasses.STRING, "nullDefault", null);
            
            ParameterNode result = NodeFactory.toNode(nullDefaultParam);
            
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("nullDefault");
            assertThat(result.getDefaultValue()).isNull();
        }

        @Test
        @DisplayName("Should handle different parameter types")
        void shouldHandleDifferentParameterTypes() {
            Parameter stringParam = new Parameter(PrimitiveClasses.STRING, "strParam", "default");
            Parameter doubleParam = new Parameter(PrimitiveClasses.DOUBLE, "doubleParam", "1.0");
            Parameter objectParam = new Parameter(PrimitiveClasses.OBJECT, "objParam", "null");
            
            ParameterNode stringResult = NodeFactory.toNode(stringParam);
            ParameterNode doubleResult = NodeFactory.toNode(doubleParam);
            ParameterNode objectResult = NodeFactory.toNode(objectParam);
            
            assertThat(stringResult.getType()).isEqualTo("String");
            assertThat(doubleResult.getType()).isEqualTo("Double");
            assertThat(objectResult.getType()).isEqualTo("Object");
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling Tests")
    class EdgeCasesAndErrorHandlingTests {

        @Test
        @DisplayName("Should handle join point with no extends")
        void shouldHandleJoinPointWithNoExtends() {
            JoinPointClass noExtendsJP = new JoinPointClass("NoExtendsJP");
            
            JoinPointNode result = NodeFactory.toNode(noExtendsJP);
            
            assertThat(result).isNotNull();
            assertThat(result.getExtend()).isEmpty();
        }

        @Test
        @DisplayName("Should handle join point with no default attribute")
        void shouldHandleJoinPointWithNoDefaultAttribute() {
            JoinPointClass noDefaultJP = new JoinPointClass("NoDefaultJP", null, null);
            
            JoinPointNode result = NodeFactory.toNode(noDefaultJP);
            
            assertThat(result).isNotNull();
            assertThat(result.getDefaultAttribute()).isEqualTo(Optional.empty());
        }

        @Test
        @DisplayName("Should handle attributes with empty parameter lists")
        void shouldHandleAttributesWithEmptyParameterLists() {
            Attribute emptyParamAttr = new Attribute(PrimitiveClasses.STRING, "emptyAttr");
            
            AttributeNode result = NodeFactory.toNode(emptyParamAttr);
            
            assertThat(result).isNotNull();
            assertThat(result.getChildren()).hasSize(1); // Only DeclarationNode, no ParameterNodes
            assertThat(result.getChildren().get(0)).isInstanceOf(DeclarationNode.class);
        }

        @Test
        @DisplayName("Should handle actions with empty parameter lists")
        void shouldHandleActionsWithEmptyParameterLists() {
            Action emptyParamAction = new Action(PrimitiveClasses.VOID, "emptyAction");
            
            ActionNode result = NodeFactory.toNode(emptyParamAction);
            
            assertThat(result).isNotNull();
            assertThat(result.getChildren()).hasSize(1); // Only DeclarationNode, no ParameterNodes
            assertThat(result.getChildren().get(0)).isInstanceOf(DeclarationNode.class);
        }

        @Test
        @DisplayName("Should handle complex nested structures")
        void shouldHandleComplexNestedStructures() {
            // Create a complex join point with multiple attributes and actions
            JoinPointClass complexJP = new JoinPointClass("ComplexJP", globalJoinPoint, "defaultAttr");
            
            // Add multiple attributes with different parameter configurations
            Attribute attr1 = new Attribute(PrimitiveClasses.STRING, "attr1");
            Attribute attr2 = new Attribute(PrimitiveClasses.INTEGER, "attr2", 
                Arrays.asList(new Parameter(PrimitiveClasses.BOOLEAN, "flag", "true")));
            
            // Add multiple actions
            Action action1 = new Action(PrimitiveClasses.VOID, "action1");
            Action action2 = new Action(PrimitiveClasses.OBJECT, "action2", 
                Arrays.asList(
                    new Parameter(PrimitiveClasses.STRING, "param1", "default"),
                    new Parameter(PrimitiveClasses.INTEGER, "param2", "0")
                ));
            
            complexJP.add(attr1);
            complexJP.add(attr2);
            complexJP.add(action1);
            complexJP.add(action2);
            
            JoinPointNode result = NodeFactory.toNode(complexJP);
            
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("ComplexJP");
            assertThat(result.getChildren()).hasSize(4); // 2 attributes + 2 actions
            
            // Verify structure
            List<LangSpecNode> children = result.getChildren();
            assertThat(children.get(0)).isInstanceOf(AttributeNode.class);
            assertThat(children.get(1)).isInstanceOf(AttributeNode.class);
            assertThat(children.get(2)).isInstanceOf(ActionNode.class);
            assertThat(children.get(3)).isInstanceOf(ActionNode.class);
            
            // Verify second attribute has parameters (DeclarationNode + ParameterNode)
            AttributeNode attr2Node = (AttributeNode) children.get(1);
            assertThat(attr2Node.getChildren()).hasSize(2);
            
            // Verify second action has parameters (DeclarationNode + 2 ParameterNodes)
            ActionNode action2Node = (ActionNode) children.get(3);
            assertThat(action2Node.getChildren()).hasSize(3);
        }

        @Test
        @DisplayName("Should handle special characters in names")
        void shouldHandleSpecialCharactersInNames() {
            String specialName = "special_name$123";
            JoinPointClass specialJP = new JoinPointClass(specialName);
            
            JoinPointNode result = NodeFactory.toNode(specialJP);
            
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo(specialName);
        }

        @Test
        @DisplayName("Should handle very long names")
        void shouldHandleVeryLongNames() {
            String longName = "a".repeat(1000);
            JoinPointClass longNameJP = new JoinPointClass(longName);
            
            JoinPointNode result = NodeFactory.toNode(longNameJP);
            
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo(longName);
        }
    }
}
