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
import org.lara.language.specification.dsl.types.LiteralEnum;
import org.lara.language.specification.dsl.types.PrimitiveClasses;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link Declaration} class.
 * 
 * Tests cover:
 * - Construction and initialization
 * - Type management
 * - Name management
 * - String representation (including LiteralEnum special case)
 * - Edge cases and error handling
 * 
 * @author Generated Test Suite
 */
@DisplayName("Declaration Tests")
class DeclarationTest {

    private IType stringType;
    private IType integerType;
    private IType booleanType;
    private IType voidType;
    private String testName;

    @BeforeEach
    void setUp() {
        stringType = PrimitiveClasses.STRING;
        integerType = PrimitiveClasses.INTEGER;
        booleanType = PrimitiveClasses.BOOLEAN;
        voidType = PrimitiveClasses.VOID;
        testName = "testDeclaration";
    }

    @Nested
    @DisplayName("Construction Tests")
    class ConstructionTests {

        @Test
        @DisplayName("Should create declaration with type and name")
        void shouldCreateDeclarationWithTypeAndName() {
            Declaration declaration = new Declaration(stringType, testName);

            assertThat(declaration.getType()).isEqualTo(stringType);
            assertThat(declaration.getName()).isEqualTo(testName);
        }

        @Test
        @DisplayName("Should handle null type")
        void shouldHandleNullType() {
            Declaration declaration = new Declaration(null, testName);

            assertThat(declaration.getType()).isNull();
            assertThat(declaration.getName()).isEqualTo(testName);
        }

        @Test
        @DisplayName("Should handle null name")
        void shouldHandleNullName() {
            Declaration declaration = new Declaration(stringType, null);

            assertThat(declaration.getType()).isEqualTo(stringType);
            assertThat(declaration.getName()).isNull();
        }

        @Test
        @DisplayName("Should handle both null type and name")
        void shouldHandleBothNullTypeAndName() {
            Declaration declaration = new Declaration(null, null);

            assertThat(declaration.getType()).isNull();
            assertThat(declaration.getName()).isNull();
        }

        @Test
        @DisplayName("Should handle empty name")
        void shouldHandleEmptyName() {
            Declaration declaration = new Declaration(stringType, "");

            assertThat(declaration.getType()).isEqualTo(stringType);
            assertThat(declaration.getName()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Type Management Tests")
    class TypeManagementTests {

        @Test
        @DisplayName("Should get and set type correctly")
        void shouldGetAndSetType() {
            Declaration declaration = new Declaration(stringType, testName);
            assertThat(declaration.getType()).isEqualTo(stringType);

            declaration.setType(integerType);
            assertThat(declaration.getType()).isEqualTo(integerType);

            declaration.setType(booleanType);
            assertThat(declaration.getType()).isEqualTo(booleanType);
        }

        @Test
        @DisplayName("Should handle setting null type")
        void shouldHandleSettingNullType() {
            Declaration declaration = new Declaration(stringType, testName);
            assertThat(declaration.getType()).isEqualTo(stringType);

            declaration.setType(null);
            assertThat(declaration.getType()).isNull();
        }

        @Test
        @DisplayName("Should handle different primitive types")
        void shouldHandleDifferentPrimitiveTypes() {
            Declaration declaration = new Declaration(stringType, testName);

            // Test all primitive types
            for (PrimitiveClasses primitiveType : PrimitiveClasses.values()) {
                declaration.setType(primitiveType);
                assertThat(declaration.getType()).isEqualTo(primitiveType);
            }
        }

        @Test
        @DisplayName("Should handle complex types")
        void shouldHandleComplexTypes() {
            Declaration declaration = new Declaration(stringType, testName);

            declaration.setType(PrimitiveClasses.OBJECT);
            assertThat(declaration.getType()).isEqualTo(PrimitiveClasses.OBJECT);

            declaration.setType(PrimitiveClasses.MAP);
            assertThat(declaration.getType()).isEqualTo(PrimitiveClasses.MAP);

            declaration.setType(PrimitiveClasses.JOINPOINT_INTERFACE);
            assertThat(declaration.getType()).isEqualTo(PrimitiveClasses.JOINPOINT_INTERFACE);
        }
    }

    @Nested
    @DisplayName("Name Management Tests")
    class NameManagementTests {

        @Test
        @DisplayName("Should get and set name correctly")
        void shouldGetAndSetName() {
            Declaration declaration = new Declaration(stringType, testName);
            assertThat(declaration.getName()).isEqualTo(testName);

            String newName = "newDeclarationName";
            declaration.setName(newName);
            assertThat(declaration.getName()).isEqualTo(newName);
        }

        @Test
        @DisplayName("Should handle setting null name")
        void shouldHandleSettingNullName() {
            Declaration declaration = new Declaration(stringType, testName);
            assertThat(declaration.getName()).isEqualTo(testName);

            declaration.setName(null);
            assertThat(declaration.getName()).isNull();
        }

        @Test
        @DisplayName("Should handle setting empty name")
        void shouldHandleSettingEmptyName() {
            Declaration declaration = new Declaration(stringType, testName);
            assertThat(declaration.getName()).isEqualTo(testName);

            declaration.setName("");
            assertThat(declaration.getName()).isEmpty();
        }

        @Test
        @DisplayName("Should handle names with special characters")
        void shouldHandleNamesWithSpecialCharacters() {
            String specialName = "test_name$123@#!";
            Declaration declaration = new Declaration(stringType, testName);

            declaration.setName(specialName);
            assertThat(declaration.getName()).isEqualTo(specialName);
        }

        @Test
        @DisplayName("Should handle very long names")
        void shouldHandleVeryLongNames() {
            String longName = "a".repeat(1000);
            Declaration declaration = new Declaration(stringType, testName);

            declaration.setName(longName);
            assertThat(declaration.getName()).isEqualTo(longName);
        }

        @Test
        @DisplayName("Should handle Unicode characters in names")
        void shouldHandleUnicodeCharactersInNames() {
            String unicodeName = "测试声明名称";
            Declaration declaration = new Declaration(stringType, testName);

            declaration.setName(unicodeName);
            assertThat(declaration.getName()).isEqualTo(unicodeName);
        }

        @Test
        @DisplayName("Should handle whitespace in names")
        void shouldHandleWhitespaceInNames() {
            String nameWithSpaces = "declaration with spaces";
            Declaration declaration = new Declaration(stringType, testName);

            declaration.setName(nameWithSpaces);
            assertThat(declaration.getName()).isEqualTo(nameWithSpaces);
        }
    }

    @Nested
    @DisplayName("String Representation Tests")
    class StringRepresentationTests {

        @Test
        @DisplayName("Should generate correct string for normal types")
        void shouldGenerateCorrectStringForNormalTypes() {
            Declaration declaration = new Declaration(stringType, testName);
            
            String result = declaration.toString();
            assertThat(result).isEqualTo("String testDeclaration");
        }

        @Test
        @DisplayName("Should handle different primitive types in string representation")
        void shouldHandleDifferentPrimitiveTypesInStringRepresentation() {
            Declaration stringDeclaration = new Declaration(stringType, "stringVar");
            Declaration intDeclaration = new Declaration(integerType, "intVar");
            Declaration boolDeclaration = new Declaration(booleanType, "boolVar");
            Declaration voidDeclaration = new Declaration(voidType, "voidVar");

            assertThat(stringDeclaration.toString()).isEqualTo("String stringVar");
            assertThat(intDeclaration.toString()).isEqualTo("Integer intVar");
            assertThat(boolDeclaration.toString()).isEqualTo("Boolean boolVar");
            assertThat(voidDeclaration.toString()).isEqualTo("Void voidVar");
        }

        @Test
        @DisplayName("Should handle special types in string representation")
        void shouldHandleSpecialTypesInStringRepresentation() {
            Declaration objectDeclaration = new Declaration(PrimitiveClasses.OBJECT, "obj");
            Declaration mapDeclaration = new Declaration(PrimitiveClasses.MAP, "map");
            Declaration jpDeclaration = new Declaration(PrimitiveClasses.JOINPOINT_INTERFACE, "jp");

            assertThat(objectDeclaration.toString()).isEqualTo("Object obj");
            assertThat(mapDeclaration.toString()).isEqualTo("Map map");
            assertThat(jpDeclaration.toString()).isEqualTo("JoinpointInterface jp");
        }

        @Test
        @DisplayName("Should handle LiteralEnum types specially")
        void shouldHandleLiteralEnumTypesSpecially() {
            // Make it a LiteralEnum by creating an anonymous subclass
            LiteralEnum testLiteralEnum = new LiteralEnum("TestEnum") {
                @Override
                public String getType() {
                    return "TestEnumValue";
                }
            };

            Declaration declaration = new Declaration(testLiteralEnum, "enumVar");
            
            String result = declaration.toString();
            // For LiteralEnum, only the type should be returned, not the name
            assertThat(result).isEqualTo("TestEnumValue");
        }

        @Test
        @DisplayName("Should handle null type in string representation")
        void shouldHandleNullTypeInStringRepresentation() {
            Declaration declaration = new Declaration(null, testName);

            // toString() will throw NullPointerException when calling getType() on null type
            assertThatThrownBy(() -> declaration.toString()).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should handle null name in string representation")
        void shouldHandleNullNameInStringRepresentation() {
            Declaration declaration = new Declaration(stringType, null);

            String result = declaration.toString();
            assertThat(result).isEqualTo("String null");
        }

        @Test
        @DisplayName("Should handle both null type and name in string representation")
        void shouldHandleBothNullTypeAndNameInStringRepresentation() {
            Declaration declaration = new Declaration(null, null);

            // toString() will throw NullPointerException when calling getType() on null type
            assertThatThrownBy(() -> declaration.toString()).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should handle empty name in string representation")
        void shouldHandleEmptyNameInStringRepresentation() {
            Declaration declaration = new Declaration(stringType, "");

            String result = declaration.toString();
            assertThat(result).isEqualTo("String ");
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class EdgeCasesAndErrorHandlingTests {

        @Test
        @DisplayName("Should handle repeated type and name changes")
        void shouldHandleRepeatedTypeAndNameChanges() {
            Declaration declaration = new Declaration(stringType, testName);

            // Multiple type changes
            for (int i = 0; i < 100; i++) {
                if (i % 2 == 0) {
                    declaration.setType(stringType);
                    assertThat(declaration.getType()).isEqualTo(stringType);
                } else {
                    declaration.setType(integerType);
                    assertThat(declaration.getType()).isEqualTo(integerType);
                }
            }

            // Multiple name changes
            for (int i = 0; i < 100; i++) {
                String newName = "name" + i;
                declaration.setName(newName);
                assertThat(declaration.getName()).isEqualTo(newName);
            }
        }

        @Test
        @DisplayName("Should handle extreme values")
        void shouldHandleExtremeValues() {
            String extremeName = "x".repeat(10000);
            Declaration declaration = new Declaration(stringType, extremeName);

            assertThat(declaration.getName()).isEqualTo(extremeName);
            assertThat(declaration.toString()).contains(extremeName);
        }

        @Test
        @DisplayName("Should maintain state consistency")
        void shouldMaintainStateConsistency() {
            Declaration declaration = new Declaration(stringType, testName);

            // Original state
            IType originalType = declaration.getType();
            String originalName = declaration.getName();

            // Change type
            declaration.setType(integerType);
            assertThat(declaration.getType()).isEqualTo(integerType);
            assertThat(declaration.getName()).isEqualTo(originalName); // Name should remain unchanged

            // Change name
            declaration.setName("newName");
            assertThat(declaration.getType()).isEqualTo(integerType); // Type should remain unchanged
            assertThat(declaration.getName()).isEqualTo("newName");

            // Revert type
            declaration.setType(originalType);
            assertThat(declaration.getType()).isEqualTo(originalType);
            assertThat(declaration.getName()).isEqualTo("newName"); // Name should remain changed
        }

        @Test
        @DisplayName("Should handle concurrent-like access patterns")
        void shouldHandleConcurrentLikeAccessPatterns() {
            Declaration declaration = new Declaration(stringType, testName);

            // Simulate rapid get/set operations
            for (int i = 0; i < 1000; i++) {
                String currentName = declaration.getName();
                
                declaration.setName(currentName + "_" + i);
                declaration.setType(i % 2 == 0 ? stringType : integerType);
                
                assertThat(declaration.getName()).isEqualTo(currentName + "_" + i);
                assertThat(declaration.getType()).isEqualTo(i % 2 == 0 ? stringType : integerType);
            }
        }

        @Test
        @DisplayName("Should handle identity operations")
        void shouldHandleIdentityOperations() {
            Declaration declaration = new Declaration(stringType, testName);

            // Setting the same type should not cause issues
            declaration.setType(stringType);
            assertThat(declaration.getType()).isEqualTo(stringType);

            // Setting the same name should not cause issues
            declaration.setName(testName);
            assertThat(declaration.getName()).isEqualTo(testName);

            // Multiple identity operations
            for (int i = 0; i < 10; i++) {
                declaration.setType(stringType);
                declaration.setName(testName);
                assertThat(declaration.getType()).isEqualTo(stringType);
                assertThat(declaration.getName()).isEqualTo(testName);
            }
        }
    }
}
