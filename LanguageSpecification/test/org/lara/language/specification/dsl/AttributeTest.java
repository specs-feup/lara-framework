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

package org.lara.language.specification.dsl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.lara.language.specification.dsl.types.IType;
import org.lara.language.specification.dsl.types.PrimitiveClasses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link Attribute} class.
 * 
 * Tests cover:
 * - Construction and initialization
 * - Type management
 * - Name management
 * - Parameter management
 * - Declaration handling
 * - String representation
 * - Comparable interface
 * - BaseNode inheritance (tooltip, default flag)
 * 
 * @author Generated Test Suite
 */
@DisplayName("Attribute Tests")
class AttributeTest {

    private IType stringType;
    private IType integerType;
    private IType voidType;
    private String testName;
    private List<Parameter> testParameters;

    @BeforeEach
    void setUp() {
        stringType = PrimitiveClasses.STRING;
        integerType = PrimitiveClasses.INTEGER;
        voidType = PrimitiveClasses.VOID;
        testName = "testAttribute";
        testParameters = new ArrayList<>();
        testParameters.add(new Parameter(integerType, "param1", "0"));
        testParameters.add(new Parameter(stringType, "param2", "default"));
    }

    @Nested
    @DisplayName("Construction Tests")
    class ConstructionTests {

        @Test
        @DisplayName("Should create attribute with type and name only")
        void shouldCreateAttributeWithTypeAndName() {
            Attribute attribute = new Attribute(stringType, testName);

            assertThat(attribute.getType()).isEqualTo(stringType);
            assertThat(attribute.getName()).isEqualTo(testName);
            assertThat(attribute.getParameters()).isNotNull().isEmpty();
            assertThat(attribute.getDeclaration()).isNotNull();
        }

        @Test
        @DisplayName("Should create attribute with type, name, and parameters")
        void shouldCreateAttributeWithTypeNameAndParameters() {
            Attribute attribute = new Attribute(stringType, testName, testParameters);

            assertThat(attribute.getType()).isEqualTo(stringType);
            assertThat(attribute.getName()).isEqualTo(testName);
            assertThat(attribute.getParameters()).isEqualTo(testParameters);
            assertThat(attribute.getDeclaration()).isNotNull();
        }

        @Test
        @DisplayName("Should handle null parameters list")
        void shouldHandleNullParametersList() {
            Attribute attribute = new Attribute(stringType, testName, null);

            assertThat(attribute.getType()).isEqualTo(stringType);
            assertThat(attribute.getName()).isEqualTo(testName);
            assertThat(attribute.getParameters()).isNull();
        }

        @Test
        @DisplayName("Should handle empty parameters list")
        void shouldHandleEmptyParametersList() {
            List<Parameter> emptyParams = new ArrayList<>();
            Attribute attribute = new Attribute(stringType, testName, emptyParams);

            assertThat(attribute.getType()).isEqualTo(stringType);
            assertThat(attribute.getName()).isEqualTo(testName);
            assertThat(attribute.getParameters()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Type Management Tests")
    class TypeManagementTests {

        @Test
        @DisplayName("Should get and set type correctly")
        void shouldGetAndSetType() {
            Attribute attribute = new Attribute(stringType, testName);
            assertThat(attribute.getType()).isEqualTo(stringType);

            attribute.setType(integerType);
            assertThat(attribute.getType()).isEqualTo(integerType);
        }

        @Test
        @DisplayName("Should return correct type string")
        void shouldReturnCorrectTypeString() {
            Attribute attribute = new Attribute(stringType, testName);
            assertThat(attribute.getReturnType()).isEqualTo("String");

            attribute.setType(integerType);
            assertThat(attribute.getReturnType()).isEqualTo("Integer");

            attribute.setType(voidType);
            assertThat(attribute.getReturnType()).isEqualTo("Void");
        }

        @Test
        @DisplayName("Should handle type changes affecting declaration")
        void shouldHandleTypeChangesAffectingDeclaration() {
            Attribute attribute = new Attribute(stringType, testName);
            Declaration originalDeclaration = attribute.getDeclaration();

            attribute.setType(integerType);

            assertThat(attribute.getDeclaration()).isSameAs(originalDeclaration);
            assertThat(attribute.getDeclaration().getType()).isEqualTo(integerType);
        }
    }

    @Nested
    @DisplayName("Name Management Tests")
    class NameManagementTests {

        @Test
        @DisplayName("Should get and set name correctly")
        void shouldGetAndSetName() {
            Attribute attribute = new Attribute(stringType, testName);
            assertThat(attribute.getName()).isEqualTo(testName);

            String newName = "newAttributeName";
            attribute.setName(newName);
            assertThat(attribute.getName()).isEqualTo(newName);
        }

        @Test
        @DisplayName("Should handle name changes affecting declaration")
        void shouldHandleNameChangesAffectingDeclaration() {
            Attribute attribute = new Attribute(stringType, testName);
            Declaration originalDeclaration = attribute.getDeclaration();

            String newName = "newName";
            attribute.setName(newName);

            assertThat(attribute.getDeclaration()).isSameAs(originalDeclaration);
            assertThat(attribute.getDeclaration().getName()).isEqualTo(newName);
        }

        @Test
        @DisplayName("Should handle empty and null names")
        void shouldHandleEmptyAndNullNames() {
            Attribute attribute = new Attribute(stringType, testName);

            attribute.setName("");
            assertThat(attribute.getName()).isEmpty();

            attribute.setName(null);
            assertThat(attribute.getName()).isNull();
        }
    }

    @Nested
    @DisplayName("Parameter Management Tests")
    class ParameterManagementTests {

        @Test
        @DisplayName("Should add parameter with type and name")
        void shouldAddParameterWithTypeAndName() {
            Attribute attribute = new Attribute(stringType, testName);
            assertThat(attribute.getParameters()).isEmpty();

            attribute.addParameter(integerType, "newParam");

            assertThat(attribute.getParameters()).hasSize(1);
            Parameter addedParam = attribute.getParameters().get(0);
            assertThat(addedParam.getType()).isEqualTo("Integer");
            assertThat(addedParam.getName()).isEqualTo("newParam");
            assertThat(addedParam.getDefaultValue()).isEmpty();
        }

        @Test
        @DisplayName("Should add parameter with type, name, and default value")
        void shouldAddParameterWithTypeNameAndDefaultValue() {
            Attribute attribute = new Attribute(stringType, testName);

            attribute.addParameter(integerType, "newParam", "42");

            assertThat(attribute.getParameters()).hasSize(1);
            Parameter addedParam = attribute.getParameters().get(0);
            assertThat(addedParam.getType()).isEqualTo("Integer");
            assertThat(addedParam.getName()).isEqualTo("newParam");
            assertThat(addedParam.getDefaultValue()).isEqualTo("42");
        }

        @Test
        @DisplayName("Should add multiple parameters")
        void shouldAddMultipleParameters() {
            Attribute attribute = new Attribute(stringType, testName);

            attribute.addParameter(integerType, "param1", "1");
            attribute.addParameter(stringType, "param2", "default");
            attribute.addParameter(voidType, "param3");

            assertThat(attribute.getParameters()).hasSize(3);
            assertThat(attribute.getParameters().get(0).getName()).isEqualTo("param1");
            assertThat(attribute.getParameters().get(1).getName()).isEqualTo("param2");
            assertThat(attribute.getParameters().get(2).getName()).isEqualTo("param3");
        }

        @Test
        @DisplayName("Should get and set parameters list")
        void shouldGetAndSetParametersList() {
            Attribute attribute = new Attribute(stringType, testName);
            assertThat(attribute.getParameters()).isEmpty();

            attribute.setParameters(testParameters);
            assertThat(attribute.getParameters()).isEqualTo(testParameters);

            List<Parameter> newParams = Arrays.asList(
                new Parameter(voidType, "newParam1", ""),
                new Parameter(integerType, "newParam2", "100")
            );
            attribute.setParameters(newParams);
            assertThat(attribute.getParameters()).isEqualTo(newParams);
        }

        @Test
        @DisplayName("Should handle null parameters list")
        void shouldHandleNullParametersListSet() {
            Attribute attribute = new Attribute(stringType, testName, testParameters);
            assertThat(attribute.getParameters()).isNotEmpty();

            attribute.setParameters(null);
            assertThat(attribute.getParameters()).isNull();
        }
    }

    @Nested
    @DisplayName("Declaration Management Tests")
    class DeclarationManagementTests {

        @Test
        @DisplayName("Should get declaration")
        void shouldGetDeclaration() {
            Attribute attribute = new Attribute(stringType, testName);
            Declaration declaration = attribute.getDeclaration();

            assertThat(declaration).isNotNull();
            assertThat(declaration.getType()).isEqualTo(stringType);
            assertThat(declaration.getName()).isEqualTo(testName);
        }

        @Test
        @DisplayName("Should set new declaration")
        void shouldSetNewDeclaration() {
            Attribute attribute = new Attribute(stringType, testName);
            Declaration newDeclaration = new Declaration(integerType, "newName");

            attribute.setDeclaration(newDeclaration);

            assertThat(attribute.getDeclaration()).isSameAs(newDeclaration);
            assertThat(attribute.getType()).isEqualTo(integerType);
            assertThat(attribute.getName()).isEqualTo("newName");
        }

        @Test
        @DisplayName("Should handle null declaration")
        void shouldHandleNullDeclaration() {
            Attribute attribute = new Attribute(stringType, testName);

            attribute.setDeclaration(null);

            assertThat(attribute.getDeclaration()).isNull();
            // Note: getType() and getName() will throw NullPointerException if declaration is null
            // This is expected behavior based on the implementation
        }
    }

    @Nested
    @DisplayName("String Representation Tests")
    class StringRepresentationTests {

        @Test
        @DisplayName("Should generate correct string without parameters")
        void shouldGenerateCorrectStringWithoutParameters() {
            Attribute attribute = new Attribute(stringType, testName);

            String result = attribute.toString();

            assertThat(result).isEqualTo("String testAttribute");
        }

        @Test
        @DisplayName("Should generate correct string with parameters")
        void shouldGenerateCorrectStringWithParameters() {
            Attribute attribute = new Attribute(stringType, testName, testParameters);

            String result = attribute.toString();

            assertThat(result).isEqualTo("String testAttribute(Integer param1 = 0, String param2 = default)");
        }

        @Test
        @DisplayName("Should generate correct string with single parameter")
        void shouldGenerateCorrectStringWithSingleParameter() {
            List<Parameter> singleParam = Arrays.asList(new Parameter(integerType, "count", "1"));
            Attribute attribute = new Attribute(voidType, "process", singleParam);

            String result = attribute.toString();

            assertThat(result).isEqualTo("Void process(Integer count = 1)");
        }

        @Test
        @DisplayName("Should handle empty parameters list in string representation")
        void shouldHandleEmptyParametersListInStringRepresentation() {
            Attribute attribute = new Attribute(stringType, testName, new ArrayList<>());

            String result = attribute.toString();

            assertThat(result).isEqualTo("String testAttribute");
        }
    }

    @Nested
    @DisplayName("Comparable Interface Tests")
    class ComparableInterfaceTests {

        @Test
        @DisplayName("Should compare attributes by name")
        void shouldCompareAttributesByName() {
            Attribute attr1 = new Attribute(stringType, "apple");
            Attribute attr2 = new Attribute(integerType, "banana");
            Attribute attr3 = new Attribute(voidType, "cherry");

            assertThat(attr1.compareTo(attr2)).isNegative();
            assertThat(attr2.compareTo(attr3)).isNegative();
            assertThat(attr3.compareTo(attr1)).isPositive();
        }

        @Test
        @DisplayName("Should return zero for equal names")
        void shouldReturnZeroForEqualNames() {
            Attribute attr1 = new Attribute(stringType, testName);
            Attribute attr2 = new Attribute(integerType, testName);

            assertThat(attr1.compareTo(attr2)).isZero();
        }

        @Test
        @DisplayName("Should handle case sensitivity in comparison")
        void shouldHandleCaseSensitivityInComparison() {
            Attribute attr1 = new Attribute(stringType, "Apple");
            Attribute attr2 = new Attribute(stringType, "apple");

            // String comparison is case-sensitive
            assertThat(attr1.compareTo(attr2)).isNegative(); // 'A' < 'a'
        }

        @Test
        @DisplayName("Should handle null names in comparison")
        void shouldHandleNullNamesInComparison() {
            Attribute attr1 = new Attribute(stringType, testName);
            Attribute attr2 = new Attribute(stringType, "test");
            
            attr2.setName(null);

            // This will throw NullPointerException, which is expected behavior
            assertThatThrownBy(() -> attr1.compareTo(attr2))
                .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("BaseNode Inheritance Tests")
    class BaseNodeInheritanceTests {

        @Test
        @DisplayName("Should inherit tooltip functionality")
        void shouldInheritTooltipFunctionality() {
            Attribute attribute = new Attribute(stringType, testName);

            // Initial state
            assertThat(attribute.getToolTip()).isEmpty();

            // Set tooltip
            String tooltip = "This is a test attribute";
            attribute.setToolTip(tooltip);
            assertThat(attribute.getToolTip()).isPresent().contains(tooltip);

            // Clear tooltip
            attribute.setToolTip(null);
            assertThat(attribute.getToolTip()).isEmpty();
        }

        @Test
        @DisplayName("Should inherit default flag functionality")
        void shouldInheritDefaultFlagFunctionality() {
            Attribute attribute = new Attribute(stringType, testName);

            // Initial state
            assertThat(attribute.isDefault()).isFalse();

            // Set as default
            attribute.setDefault(true);
            assertThat(attribute.isDefault()).isTrue();

            // Unset default
            attribute.setDefault(false);
            assertThat(attribute.isDefault()).isFalse();
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class EdgeCasesAndErrorHandlingTests {

        @Test
        @DisplayName("Should handle special characters in names")
        void shouldHandleSpecialCharactersInNames() {
            String specialName = "test_attribute$123";
            Attribute attribute = new Attribute(stringType, specialName);

            assertThat(attribute.getName()).isEqualTo(specialName);
            assertThat(attribute.toString()).contains(specialName);
        }

        @Test
        @DisplayName("Should handle very long names")
        void shouldHandleVeryLongNames() {
            String longName = "a".repeat(1000);
            Attribute attribute = new Attribute(stringType, longName);

            assertThat(attribute.getName()).isEqualTo(longName);
            assertThat(attribute.toString()).contains(longName);
        }

        @Test
        @DisplayName("Should handle modification of parameters list after construction")
        void shouldHandleModificationOfParametersListAfterConstruction() {
            List<Parameter> mutableParams = new ArrayList<>(testParameters);
            Attribute attribute = new Attribute(stringType, testName, mutableParams);

            // Modify the original list
            mutableParams.add(new Parameter(voidType, "extraParam", ""));

            // The attribute should reflect the change (since it holds the same list reference)
            assertThat(attribute.getParameters()).hasSize(3);
            assertThat(attribute.getParameters().get(2).getName()).isEqualTo("extraParam");
        }

        @Test
        @DisplayName("Should handle different parameter types and values")
        void shouldHandleDifferentParameterTypesAndValues() {
            Attribute attribute = new Attribute(stringType, testName);

            attribute.addParameter(PrimitiveClasses.BOOLEAN, "flag", "true");
            attribute.addParameter(PrimitiveClasses.DOUBLE, "ratio", "3.14");
            attribute.addParameter(PrimitiveClasses.OBJECT, "data", "null");

            assertThat(attribute.getParameters()).hasSize(3);
            assertThat(attribute.toString()).contains("Boolean flag = true");
            assertThat(attribute.toString()).contains("Double ratio = 3.14");
            assertThat(attribute.toString()).contains("Object data = null");
        }
    }
}
