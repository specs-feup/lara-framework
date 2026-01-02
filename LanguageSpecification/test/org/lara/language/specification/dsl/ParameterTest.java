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

package org.lara.language.specification.dsl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.lara.language.specification.dsl.types.IType;
import org.lara.language.specification.dsl.types.PrimitiveClasses;
import org.lara.language.specification.exception.LanguageSpecificationException;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link Parameter} class.
 * 
 * Tests cover:
 * - Construction with different parameter combinations
 * - Default value management
 * - Declaration handling
 * - Type access (via Declaration)
 * - Name access (via Declaration) 
 * - String representation
 * - Edge cases and error handling
 * 
 * @author Generated Test Suite
 */
@DisplayName("Parameter Tests")
class ParameterTest {

    private IType stringType;
    private IType integerType;
    private IType booleanType;
    private IType voidType;
    private String testName;
    private String testDefaultValue;
    private Declaration testDeclaration;

    @BeforeEach
    void setUp() {
        stringType = PrimitiveClasses.STRING;
        integerType = PrimitiveClasses.INTEGER;
        booleanType = PrimitiveClasses.BOOLEAN;
        voidType = PrimitiveClasses.VOID;
        testName = "testParam";
        testDefaultValue = "defaultValue";
        testDeclaration = new Declaration(stringType, testName);
    }

    @Nested
    @DisplayName("Construction Tests")
    class ConstructionTests {

        @Test
        @DisplayName("Should create parameter with type and name only")
        void shouldCreateParameterWithTypeAndName() {
            Parameter parameter = new Parameter(stringType, testName);

            assertThat(parameter.getType()).isEqualTo("String");
            assertThat(parameter.getName()).isEqualTo(testName);
            assertThat(parameter.getDefaultValue()).isEmpty();
            assertThat(parameter.getDeclaration()).isNotNull();
            assertThat(parameter.getDeclaration().getType()).isEqualTo(stringType);
            assertThat(parameter.getDeclaration().getName()).isEqualTo(testName);
        }

        @Test
        @DisplayName("Should create parameter with type, name, and default value")
        void shouldCreateParameterWithTypeNameAndDefaultValue() {
            Parameter parameter = new Parameter(integerType, testName, testDefaultValue);

            assertThat(parameter.getType()).isEqualTo("Integer");
            assertThat(parameter.getName()).isEqualTo(testName);
            assertThat(parameter.getDefaultValue()).isEqualTo(testDefaultValue);
            assertThat(parameter.getDeclaration()).isNotNull();
        }

        @Test
        @DisplayName("Should create parameter with declaration and default value")
        void shouldCreateParameterWithDeclarationAndDefaultValue() {
            Parameter parameter = new Parameter(testDeclaration, testDefaultValue);

            assertThat(parameter.getDeclaration()).isSameAs(testDeclaration);
            assertThat(parameter.getDefaultValue()).isEqualTo(testDefaultValue);
            assertThat(parameter.getType()).isEqualTo("String");
            assertThat(parameter.getName()).isEqualTo(testName);
        }

        @Test
        @DisplayName("Should handle null default value in constructor")
        void shouldHandleNullDefaultValueInConstructor() {
            Parameter parameter = new Parameter(stringType, testName, null);

            assertThat(parameter.getType()).isEqualTo("String");
            assertThat(parameter.getName()).isEqualTo(testName);
            assertThat(parameter.getDefaultValue()).isNull();
        }

        @Test
        @DisplayName("Should handle empty default value in constructor")
        void shouldHandleEmptyDefaultValueInConstructor() {
            Parameter parameter = new Parameter(stringType, testName, "");

            assertThat(parameter.getType()).isEqualTo("String");
            assertThat(parameter.getName()).isEqualTo(testName);
            assertThat(parameter.getDefaultValue()).isEmpty();
        }

        @Test
        @DisplayName("Should handle null declaration in constructor")
        void shouldHandleNullDeclarationInConstructor() {
            Declaration nullDeclaration = null;
            Parameter parameter = new Parameter(nullDeclaration, testDefaultValue);

            assertThat(parameter.getDeclaration()).isNull();
            assertThat(parameter.getDefaultValue()).isEqualTo(testDefaultValue);
            // Note: getName() and getType() will throw NullPointerException if declaration is null
        }
    }

    @Nested
    @DisplayName("Default Value Management Tests")
    class DefaultValueManagementTests {

        @Test
        @DisplayName("Should get and set default value")
        void shouldGetAndSetDefaultValue() {
            Parameter parameter = new Parameter(stringType, testName);
            assertThat(parameter.getDefaultValue()).isEmpty();

            parameter.setDefaultValue(testDefaultValue);
            assertThat(parameter.getDefaultValue()).isEqualTo(testDefaultValue);
        }

        @Test
        @DisplayName("Should handle null default value")
        void shouldHandleNullDefaultValue() {
            Parameter parameter = new Parameter(stringType, testName, testDefaultValue);
            assertThat(parameter.getDefaultValue()).isEqualTo(testDefaultValue);

            parameter.setDefaultValue(null);
            assertThat(parameter.getDefaultValue()).isNull();
        }

        @Test
        @DisplayName("Should handle empty default value")
        void shouldHandleEmptyDefaultValue() {
            Parameter parameter = new Parameter(stringType, testName, testDefaultValue);
            assertThat(parameter.getDefaultValue()).isEqualTo(testDefaultValue);

            parameter.setDefaultValue("");
            assertThat(parameter.getDefaultValue()).isEmpty();
        }

        @Test
        @DisplayName("Should handle special characters in default value")
        void shouldHandleSpecialCharactersInDefaultValue() {
            String specialValue = "test@#$%^&*()[]{}|;:'\",.<>?/~`";
            Parameter parameter = new Parameter(stringType, testName);

            parameter.setDefaultValue(specialValue);
            assertThat(parameter.getDefaultValue()).isEqualTo(specialValue);
        }

        @Test
        @DisplayName("Should handle very long default value")
        void shouldHandleVeryLongDefaultValue() {
            String longValue = "x".repeat(1000);
            Parameter parameter = new Parameter(stringType, testName);

            parameter.setDefaultValue(longValue);
            assertThat(parameter.getDefaultValue()).isEqualTo(longValue);
        }
    }

    @Nested
    @DisplayName("Declaration Management Tests")
    class DeclarationManagementTests {

        @Test
        @DisplayName("Should get and set declaration")
        void shouldGetAndSetDeclaration() {
            Parameter parameter = new Parameter(stringType, testName);
            Declaration originalDeclaration = parameter.getDeclaration();
            assertThat(originalDeclaration).isNotNull();

            Declaration newDeclaration = new Declaration(integerType, "newName");
            parameter.setDeclaration(newDeclaration);

            assertThat(parameter.getDeclaration()).isSameAs(newDeclaration);
            assertThat(parameter.getType()).isEqualTo("Integer");
            assertThat(parameter.getName()).isEqualTo("newName");
        }

        @Test
        @DisplayName("Should handle null declaration")
        void shouldHandleNullDeclaration() {
            Parameter parameter = new Parameter(stringType, testName);
            
            parameter.setDeclaration(null);
            assertThat(parameter.getDeclaration()).isNull();
            
            // getName() and getType() should throw NullPointerException with null declaration
            assertThatThrownBy(() -> parameter.getName()).isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> parameter.getType()).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should reflect declaration changes in type and name")
        void shouldReflectDeclarationChangesInTypeAndName() {
            Parameter parameter = new Parameter(stringType, testName);
            Declaration declaration = parameter.getDeclaration();

            // Modify declaration
            declaration.setType(booleanType);
            declaration.setName("modifiedName");

            // Parameter should reflect the changes
            assertThat(parameter.getType()).isEqualTo("Boolean");
            assertThat(parameter.getName()).isEqualTo("modifiedName");
        }
    }

    @Nested
    @DisplayName("Type Access Tests")
    class TypeAccessTests {

        @Test
        @DisplayName("Should return correct type string for different types")
        void shouldReturnCorrectTypeStringForDifferentTypes() {
            Parameter stringParam = new Parameter(stringType, "str");
            Parameter intParam = new Parameter(integerType, "num");
            Parameter boolParam = new Parameter(booleanType, "flag");
            Parameter voidParam = new Parameter(voidType, "empty");

            assertThat(stringParam.getType()).isEqualTo("String");
            assertThat(intParam.getType()).isEqualTo("Integer");
            assertThat(boolParam.getType()).isEqualTo("Boolean");
            assertThat(voidParam.getType()).isEqualTo("Void");
        }

        @Test
        @DisplayName("Should handle type changes via declaration")
        void shouldHandleTypeChangesViaDeclaration() {
            Parameter parameter = new Parameter(stringType, testName);
            assertThat(parameter.getType()).isEqualTo("String");

            parameter.getDeclaration().setType(integerType);
            assertThat(parameter.getType()).isEqualTo("Integer");

            parameter.getDeclaration().setType(booleanType);
            assertThat(parameter.getType()).isEqualTo("Boolean");
        }

        @Test
        @DisplayName("Should handle complex type scenarios")
        void shouldHandleComplexTypeScenarios() {
            Parameter parameter = new Parameter(PrimitiveClasses.OBJECT, "obj");
            assertThat(parameter.getType()).isEqualTo("Object");

            parameter.getDeclaration().setType(PrimitiveClasses.MAP);
            assertThat(parameter.getType()).isEqualTo("Map");

            parameter.getDeclaration().setType(PrimitiveClasses.JOINPOINT_INTERFACE);
            assertThat(parameter.getType()).isEqualTo("JoinpointInterface");
        }
    }

    @Nested
    @DisplayName("Name Access Tests")
    class NameAccessTests {

        @Test
        @DisplayName("Should return correct name")
        void shouldReturnCorrectName() {
            Parameter parameter = new Parameter(stringType, testName);
            assertThat(parameter.getName()).isEqualTo(testName);
        }

        @Test
        @DisplayName("Should handle name changes via declaration")
        void shouldHandleNameChangesViaDeclaration() {
            Parameter parameter = new Parameter(stringType, testName);
            assertThat(parameter.getName()).isEqualTo(testName);

            String newName = "changedParameterName";
            parameter.getDeclaration().setName(newName);
            assertThat(parameter.getName()).isEqualTo(newName);
        }

        @Test
        @DisplayName("Should handle special characters in name")
        void shouldHandleSpecialCharactersInName() {
            String specialName = "param_123$test";
            Parameter parameter = new Parameter(stringType, specialName);
            assertThat(parameter.getName()).isEqualTo(specialName);
        }

        @Test
        @DisplayName("Should handle empty and null names via declaration")
        void shouldHandleEmptyAndNullNamesViaDeclaration() {
            Parameter parameter = new Parameter(stringType, testName);

            parameter.getDeclaration().setName("");
            assertThat(parameter.getName()).isEmpty();

            parameter.getDeclaration().setName(null);
            assertThat(parameter.getName()).isNull();
        }
    }

    @Nested
    @DisplayName("String Representation Tests")
    class StringRepresentationTests {

        @Test
        @DisplayName("Should generate correct string without default value")
        void shouldGenerateCorrectStringWithoutDefaultValue() {
            Parameter parameter = new Parameter(stringType, testName);
            
            String result = parameter.toString();
            assertThat(result).isEqualTo("String testParam");
        }

        @Test
        @DisplayName("Should generate correct string with default value")
        void shouldGenerateCorrectStringWithDefaultValue() {
            Parameter parameter = new Parameter(integerType, testName, "42");
            
            String result = parameter.toString();
            assertThat(result).isEqualTo("Integer testParam = 42");
        }

        @Test
        @DisplayName("Should generate correct string with empty default value")
        void shouldGenerateCorrectStringWithEmptyDefaultValue() {
            Parameter parameter = new Parameter(stringType, testName, "");
            
            String result = parameter.toString();
            assertThat(result).isEqualTo("String testParam");
        }

        @Test
        @DisplayName("Should handle null default value in string representation")
        void shouldHandleNullDefaultValueInStringRepresentation() {
            Parameter parameter = new Parameter(stringType, testName, null);
            
            // The toString() method will throw NullPointerException when calling isEmpty() on null defaultValue
            assertThatThrownBy(() -> parameter.toString()).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should generate string for different types with defaults")
        void shouldGenerateStringForDifferentTypesWithDefaults() {
            Parameter boolParam = new Parameter(booleanType, "flag", "true");
            Parameter doubleParam = new Parameter(PrimitiveClasses.DOUBLE, "ratio", "3.14");
            Parameter objectParam = new Parameter(PrimitiveClasses.OBJECT, "data", "null");

            assertThat(boolParam.toString()).isEqualTo("Boolean flag = true");
            assertThat(doubleParam.toString()).isEqualTo("Double ratio = 3.14");
            assertThat(objectParam.toString()).isEqualTo("Object data = null");
        }

        @Test
        @DisplayName("Should handle complex default values in string representation")
        void shouldHandleComplexDefaultValuesInStringRepresentation() {
            Parameter param1 = new Parameter(stringType, "path", "/path/to/file.txt");
            Parameter param2 = new Parameter(stringType, "json", "{\"key\": \"value\"}");
            Parameter param3 = new Parameter(stringType, "regex", "^[a-zA-Z0-9]+$");

            assertThat(param1.toString()).isEqualTo("String path = /path/to/file.txt");
            assertThat(param2.toString()).isEqualTo("String json = {\"key\": \"value\"}");
            assertThat(param3.toString()).isEqualTo("String regex = ^[a-zA-Z0-9]+$");
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class EdgeCasesAndErrorHandlingTests {

        @Test
        @DisplayName("Should handle parameter with all extreme values")
        void shouldHandleParameterWithAllExtremeValues() {
            String longName = "a".repeat(500);
            String longDefault = "b".repeat(500);
            
            Parameter parameter = new Parameter(stringType, longName, longDefault);

            assertThat(parameter.getName()).isEqualTo(longName);
            assertThat(parameter.getDefaultValue()).isEqualTo(longDefault);
            assertThat(parameter.toString()).contains(longName).contains(longDefault);
        }

        @Test
        @DisplayName("Should handle Unicode characters in names and defaults")
        void shouldHandleUnicodeCharactersInNamesAndDefaults() {
            String unicodeName = "测试参数";
            String unicodeDefault = "默认值";
            
            Parameter parameter = new Parameter(stringType, unicodeName, unicodeDefault);

            assertThat(parameter.getName()).isEqualTo(unicodeName);
            assertThat(parameter.getDefaultValue()).isEqualTo(unicodeDefault);
            assertThat(parameter.toString()).isEqualTo("String 测试参数 = 默认值");
        }

        @Test
        @DisplayName("Should reject whitespace in names but allow whitespace in defaults")
        void shouldRejectWhitespaceInNamesButAllowWhitespaceInDefaults() {
            String nameWithSpaces = "param with spaces";
            String defaultWithSpaces = "  default value  ";

            assertThatThrownBy(() -> new Parameter(stringType, nameWithSpaces, defaultWithSpaces))
                    .isInstanceOf(LanguageSpecificationException.class)
                    .hasMessageContaining("declaration name");

            Parameter parameter = new Parameter(stringType, "paramWithSpaces", defaultWithSpaces);

            assertThat(parameter.getName()).isEqualTo("paramWithSpaces");
            assertThat(parameter.getDefaultValue()).isEqualTo(defaultWithSpaces);
            assertThat(parameter.toString()).contains("paramWithSpaces").contains(defaultWithSpaces);
        }

        @Test
        @DisplayName("Should handle modification of declaration after parameter creation")
        void shouldHandleModificationOfDeclarationAfterParameterCreation() {
            Declaration mutableDeclaration = new Declaration(stringType, testName);
            Parameter parameter = new Parameter(mutableDeclaration, testDefaultValue);

            // Modify the declaration externally
            mutableDeclaration.setType(integerType);
            mutableDeclaration.setName("modifiedExternally");

            // Parameter should reflect the changes
            assertThat(parameter.getType()).isEqualTo("Integer");
            assertThat(parameter.getName()).isEqualTo("modifiedExternally");
        }

        @Test
        @DisplayName("Should handle multiple parameter instances with same declaration")
        void shouldHandleMultipleParameterInstancesWithSameDeclaration() {
            Declaration sharedDeclaration = new Declaration(stringType, testName);
            Parameter param1 = new Parameter(sharedDeclaration, "default1");
            Parameter param2 = new Parameter(sharedDeclaration, "default2");

            assertThat(param1.getDeclaration()).isSameAs(param2.getDeclaration());
            assertThat(param1.getName()).isEqualTo(param2.getName());
            assertThat(param1.getType()).isEqualTo(param2.getType());
            assertThat(param1.getDefaultValue()).isNotEqualTo(param2.getDefaultValue());

            // Modifying the shared declaration should affect both
            sharedDeclaration.setName("sharedName");
            assertThat(param1.getName()).isEqualTo("sharedName");
            assertThat(param2.getName()).isEqualTo("sharedName");
        }

        @Test
        @DisplayName("Should handle toString with null declaration")
        void shouldHandleToStringWithNullDeclaration() {
            Parameter parameter = new Parameter(testDeclaration, testDefaultValue);
            parameter.setDeclaration(null);

            // toString() should throw NullPointerException when declaration is null
            assertThatThrownBy(() -> parameter.toString()).isInstanceOf(NullPointerException.class);
        }
    }
}
