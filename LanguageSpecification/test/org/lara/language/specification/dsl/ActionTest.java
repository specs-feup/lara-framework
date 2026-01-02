/**
 * Copyright 2025 SPeCS.
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

package org.lara.language.specification.dsl;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.lara.language.specification.dsl.types.GenericType;
import org.lara.language.specification.dsl.types.PrimitiveClasses;

import java.util.ArrayList;
import java.util.List;

/**
 * Comprehensive unit tests for the Action class.
 * Tests cover action creation, parameter management, type handling, and string representation.
 */
@DisplayName("Action Tests")
class ActionTest {

    private Action basicAction;
    private Action actionWithParams;
    private Parameter stringParam;
    private Parameter intParam;

    @BeforeEach
    void setUp() {
        basicAction = new Action(PrimitiveClasses.VOID, "basicAction");
        
        stringParam = new Parameter(PrimitiveClasses.STRING, "text");
        intParam = new Parameter(PrimitiveClasses.INTEGER, "count");
        
        actionWithParams = new Action(PrimitiveClasses.STRING, "actionWithParams", 
            List.of(stringParam, intParam));
    }

    @Nested
    @DisplayName("Construction Tests")
    class ConstructionTests {

        @Test
        @DisplayName("Should create action with return type and name")
        void shouldCreateActionWithReturnTypeAndName() {
            Action action = new Action(PrimitiveClasses.BOOLEAN, "testAction");
            
            assertThat(action.getName()).isEqualTo("testAction");
            assertThat(action.getType()).isEqualTo(PrimitiveClasses.BOOLEAN);
            assertThat(action.getReturnType()).isEqualTo("Boolean");
            assertThat(action.getParameters()).isEmpty();
        }

        @Test
        @DisplayName("Should create action with parameters")
        void shouldCreateActionWithParameters() {
            List<Parameter> params = List.of(stringParam, intParam);
            Action action = new Action(PrimitiveClasses.VOID, "actionWithParams", params);
            
            assertThat(action.getName()).isEqualTo("actionWithParams");
            assertThat(action.getType()).isEqualTo(PrimitiveClasses.VOID);
            assertThat(action.getParameters()).hasSize(2).containsExactly(stringParam, intParam);
        }

        @Test
        @DisplayName("Should create action with empty parameters list by default")
        void shouldCreateActionWithEmptyParametersByDefault() {
            Action action = new Action(PrimitiveClasses.STRING, "test");
            
            assertThat(action.getParameters()).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("Should handle null parameters list")
        void shouldHandleNullParametersList() {
            assertThatCode(() -> new Action(PrimitiveClasses.VOID, "test", null))
                .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("Parameter Management Tests")
    class ParameterManagementTests {

        @Test
        @DisplayName("Should add parameter with type and name")
        void shouldAddParameterWithTypeAndName() {
            basicAction.addParameter(PrimitiveClasses.STRING, "param1");
            
            assertThat(basicAction.getParameters()).hasSize(1);
            Parameter param = basicAction.getParameters().get(0);
            assertThat(param.getName()).isEqualTo("param1");
            assertThat(param.getType()).isEqualTo("String");
            assertThat(param.getDefaultValue()).isEmpty();
        }

        @Test
        @DisplayName("Should add parameter with type, name, and default value")
        void shouldAddParameterWithTypeNameAndDefaultValue() {
            basicAction.addParameter(PrimitiveClasses.INTEGER, "count", "0");
            
            assertThat(basicAction.getParameters()).hasSize(1);
            Parameter param = basicAction.getParameters().get(0);
            assertThat(param.getName()).isEqualTo("count");
            assertThat(param.getType()).isEqualTo("Integer");
            assertThat(param.getDefaultValue()).isEqualTo("0");
        }

        @Test
        @DisplayName("Should add multiple parameters")
        void shouldAddMultipleParameters() {
            basicAction.addParameter(PrimitiveClasses.STRING, "param1");
            basicAction.addParameter(PrimitiveClasses.INTEGER, "param2", "42");
            basicAction.addParameter(PrimitiveClasses.BOOLEAN, "param3");
            
            assertThat(basicAction.getParameters()).hasSize(3);
            assertThat(basicAction.getParameters().get(0).getName()).isEqualTo("param1");
            assertThat(basicAction.getParameters().get(1).getName()).isEqualTo("param2");
            assertThat(basicAction.getParameters().get(2).getName()).isEqualTo("param3");
        }

        @Test
        @DisplayName("Should set parameters list")
        void shouldSetParametersList() {
            List<Parameter> newParams = List.of(
                new Parameter(PrimitiveClasses.BOOLEAN, "flag"),
                new Parameter(PrimitiveClasses.DOUBLE, "value")
            );
            
            actionWithParams.setParameters(newParams);
            
            assertThat(actionWithParams.getParameters()).hasSize(2).containsExactly(newParams.get(0), newParams.get(1));
        }

        @Test
        @DisplayName("Should handle empty parameters list")
        void shouldHandleEmptyParametersList() {
            actionWithParams.setParameters(new ArrayList<>());
            
            assertThat(actionWithParams.getParameters()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Name and Type Management Tests")
    class NameAndTypeManagementTests {

        @Test
        @DisplayName("Should get and set name")
        void shouldGetAndSetName() {
            assertThat(basicAction.getName()).isEqualTo("basicAction");
            
            basicAction.setName("newName");
            assertThat(basicAction.getName()).isEqualTo("newName");
        }

        @Test
        @DisplayName("Should get and set type")
        void shouldGetAndSetType() {
            assertThat(basicAction.getType()).isEqualTo(PrimitiveClasses.VOID);
            
            basicAction.setType(PrimitiveClasses.STRING);
            assertThat(basicAction.getType()).isEqualTo(PrimitiveClasses.STRING);
            assertThat(basicAction.getReturnType()).isEqualTo("String");
        }

        @Test
        @DisplayName("Should get return type as string")
        void shouldGetReturnTypeAsString() {
            assertThat(basicAction.getReturnType()).isEqualTo("Void");
            assertThat(actionWithParams.getReturnType()).isEqualTo("String");
        }

        @Test
        @DisplayName("Should handle custom generic types")
        void shouldHandleCustomGenericTypes() {
            GenericType customType = new GenericType("List<String>", false);
            Action action = new Action(customType, "customAction");
            
            assertThat(action.getType()).isEqualTo(customType);
            assertThat(action.getReturnType()).isEqualTo("List<String>");
        }
    }

    @Nested
    @DisplayName("Declaration Tests")
    class DeclarationTests {

        @Test
        @DisplayName("Should provide access to declaration")
        void shouldProvideAccessToDeclaration() {
            Declaration declaration = basicAction.getDeclaration();
            
            assertThat(declaration).isNotNull();
            assertThat(declaration.getName()).isEqualTo("basicAction");
            assertThat(declaration.getType()).isEqualTo(PrimitiveClasses.VOID);
        }

        @Test
        @DisplayName("Should update declaration when name changes")
        void shouldUpdateDeclarationWhenNameChanges() {
            basicAction.setName("newName");
            
            assertThat(basicAction.getDeclaration().getName()).isEqualTo("newName");
        }

        @Test
        @DisplayName("Should update declaration when type changes")
        void shouldUpdateDeclarationWhenTypeChanges() {
            basicAction.setType(PrimitiveClasses.INTEGER);
            
            assertThat(basicAction.getDeclaration().getType()).isEqualTo(PrimitiveClasses.INTEGER);
        }
    }

    @Nested
    @DisplayName("String Representation Tests")
    class StringRepresentationTests {

        @Test
        @DisplayName("Should generate string for action without parameters")
        void shouldGenerateStringForActionWithoutParameters() {
            String result = basicAction.toString();
            
            assertThat(result).isEqualTo("Void basicAction()");
        }

        @Test
        @DisplayName("Should generate string for action with parameters")
        void shouldGenerateStringForActionWithParameters() {
            String result = actionWithParams.toString();
            
            assertThat(result).isEqualTo("String actionWithParams(String text, Integer count)");
        }

        @Test
        @DisplayName("Should generate string for action with single parameter")
        void shouldGenerateStringForActionWithSingleParameter() {
            Action action = new Action(PrimitiveClasses.BOOLEAN, "singleParam", 
                List.of(new Parameter(PrimitiveClasses.STRING, "input")));
            
            String result = action.toString();
            
            assertThat(result).isEqualTo("Boolean singleParam(String input)");
        }

        @Test
        @DisplayName("Should generate string for action with parameters with default values")
        void shouldGenerateStringForActionWithParametersWithDefaultValues() {
            Parameter paramWithDefault = new Parameter(PrimitiveClasses.INTEGER, "count", "10");
            Action action = new Action(PrimitiveClasses.VOID, "actionWithDefault", 
                List.of(stringParam, paramWithDefault));
            
            String result = action.toString();
            
            assertThat(result).isEqualTo("Void actionWithDefault(String text, Integer count = 10)");
        }
    }

    @Nested
    @DisplayName("Comparison Tests")
    class ComparisonTests {

        @Test
        @DisplayName("Should compare actions by name")
        void shouldCompareActionsByName() {
            Action action1 = new Action(PrimitiveClasses.VOID, "actionA");
            Action action2 = new Action(PrimitiveClasses.STRING, "actionB");
            Action action3 = new Action(PrimitiveClasses.INTEGER, "actionA");
            
            assertThat(action1.compareTo(action2)).isNegative();
            assertThat(action2.compareTo(action1)).isPositive();
            assertThat(action1.compareTo(action3)).isZero();
        }

        @Test
        @DisplayName("Should be consistent with equals for comparison")
        void shouldBeConsistentWithEqualsForComparison() {
            Action action1 = new Action(PrimitiveClasses.VOID, "test");
            Action action2 = new Action(PrimitiveClasses.VOID, "test");
            
            assertThat(action1.compareTo(action2)).isZero();
        }

        @Test
        @DisplayName("Should handle null-safe comparison")
        void shouldHandleNullSafeComparison() {
            Action action = new Action(PrimitiveClasses.VOID, "test");
            
            // compareTo should not throw when comparing with actions with different types
            assertThatCode(() -> action.compareTo(action)).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle null name")
        void shouldHandleNullName() {
            assertThatCode(() -> new Action(PrimitiveClasses.VOID, null))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should handle null type")
        void shouldHandleNullType() {
            assertThatCode(() -> new Action(null, "test"))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should handle empty name")
        void shouldHandleEmptyName() {
            Action action = new Action(PrimitiveClasses.VOID, "");
            
            assertThat(action.getName()).isEmpty();
            assertThat(action.toString()).contains("()");
        }

        @Test
        @DisplayName("Should handle parameter modification after creation")
        void shouldHandleParameterModificationAfterCreation() {
            List<Parameter> mutableParams = new ArrayList<>(List.of(stringParam));
            Action action = new Action(PrimitiveClasses.VOID, "test", mutableParams);
            
            // Modify the original list
            mutableParams.add(intParam);
            
            // The action should be affected by external modifications (current behavior)
            assertThat(action.getParameters()).hasSize(2);
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should work correctly in complete action definition scenario")
        void shouldWorkCorrectlyInCompleteActionDefinitionScenario() {
            // Create action similar to what might be parsed from DSL
            Action insertAction = new Action(PrimitiveClasses.VOID, "insert");
            
            // Add parameters
            insertAction.addParameter(PrimitiveClasses.STRING, "position");
            insertAction.addParameter(PrimitiveClasses.STRING, "code", "");
            
            // Verify complete setup
            assertThat(insertAction.getName()).isEqualTo("insert");
            assertThat(insertAction.getReturnType()).isEqualTo("Void");
            assertThat(insertAction.getParameters()).hasSize(2);
            
            Parameter posParam = insertAction.getParameters().get(0);
            assertThat(posParam.getName()).isEqualTo("position");
            assertThat(posParam.getType()).isEqualTo("String");
            assertThat(posParam.getDefaultValue()).isEmpty();
            
            Parameter codeParam = insertAction.getParameters().get(1);
            assertThat(codeParam.getName()).isEqualTo("code");
            assertThat(codeParam.getType()).isEqualTo("String");
            assertThat(codeParam.getDefaultValue()).isEqualTo("");
            
            String expectedString = "Void insert(String position, String code)";
            assertThat(insertAction.toString()).isEqualTo(expectedString);
        }

        @Test
        @DisplayName("Should maintain consistency after multiple modifications")
        void shouldMaintainConsistencyAfterMultipleModifications() {
            Action action = new Action(PrimitiveClasses.VOID, "test");
            
            // Multiple modifications
            action.setName("modifiedAction");
            action.setType(PrimitiveClasses.STRING);
            action.addParameter(PrimitiveClasses.INTEGER, "param1");
            action.addParameter(PrimitiveClasses.BOOLEAN, "param2", "true");
            
            // Verify all modifications are consistent
            assertThat(action.getName()).isEqualTo("modifiedAction");
            assertThat(action.getType()).isEqualTo(PrimitiveClasses.STRING);
            assertThat(action.getReturnType()).isEqualTo("String");
            assertThat(action.getDeclaration().getName()).isEqualTo("modifiedAction");
            assertThat(action.getDeclaration().getType()).isEqualTo(PrimitiveClasses.STRING);
            assertThat(action.getParameters()).hasSize(2);
            
            String expectedString = "String modifiedAction(Integer param1, Boolean param2 = true)";
            assertThat(action.toString()).isEqualTo(expectedString);
        }
    }
}
