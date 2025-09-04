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

package org.lara.language.specification.dsl.types;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ArrayType Tests")
public class ArrayTypeTest {

    private IType baseType;

    @BeforeEach
    void setUp() {
        baseType = Primitive.INT;
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create ArrayType with base type (default dimension)")
        void testConstructorWithBaseType() {
            ArrayType arrayType = new ArrayType(baseType);
            
            assertEquals(baseType, arrayType.getBaseType());
            assertEquals(1, arrayType.getDimension());
            assertEquals("int[]", arrayType.type());
        }

        @Test
        @DisplayName("Should create ArrayType with base type and dimension")
        void testConstructorWithBaseTypeAndDimension() {
            ArrayType arrayType = new ArrayType(baseType, 3);
            
            assertEquals(baseType, arrayType.getBaseType());
            assertEquals(3, arrayType.getDimension());
            assertEquals("int[][][]", arrayType.type());
        }

        @Test
        @DisplayName("Should handle null base type")
        void testConstructorWithNullBaseType() {
            ArrayType arrayType = new ArrayType(null);
            
            assertNotNull(arrayType);
            assertNull(arrayType.getBaseType());
            assertEquals(1, arrayType.getDimension());
        }

        @Test
        @DisplayName("Should handle zero dimension")
        void testConstructorWithZeroDimension() {
            ArrayType arrayType = new ArrayType(baseType, 0);
            
            assertEquals(baseType, arrayType.getBaseType());
            assertEquals(0, arrayType.getDimension());
            assertEquals("int", arrayType.type()); // No brackets for 0 dimension
        }

        @Test
        @DisplayName("Should handle negative dimension")
        void testConstructorWithNegativeDimension() {
            ArrayType arrayType = new ArrayType(baseType, -1);
            
            assertEquals(baseType, arrayType.getBaseType());
            assertEquals(-1, arrayType.getDimension());
        }
    }

    @Nested
    @DisplayName("Factory Method Tests")
    class FactoryMethodTests {

        @Test
        @DisplayName("Should create ArrayType using of() method")
        void testOfFactoryMethod() {
            ArrayType arrayType = ArrayType.of(baseType);
            
            assertNotNull(arrayType);
            assertEquals(baseType, arrayType.getBaseType());
            assertEquals(1, arrayType.getDimension());
            assertEquals("int[]", arrayType.type());
        }

        @Test
        @DisplayName("Should handle null in of() method")
        void testOfFactoryMethodWithNull() {
            ArrayType arrayType = ArrayType.of(null);
            
            assertNotNull(arrayType);
            assertNull(arrayType.getBaseType());
            assertEquals(1, arrayType.getDimension());
        }

        @Test
        @DisplayName("Should be equivalent to single-argument constructor")
        void testFactoryMethodEquivalence() {
            ArrayType arrayType1 = new ArrayType(baseType);
            ArrayType arrayType2 = ArrayType.of(baseType);
            
            assertEquals(arrayType1.getBaseType(), arrayType2.getBaseType());
            assertEquals(arrayType1.getDimension(), arrayType2.getDimension());
            assertEquals(arrayType1.type(), arrayType2.type());
        }
    }

    @Nested
    @DisplayName("Type Interface Implementation Tests")
    class TypeInterfaceTests {

        @Test
        @DisplayName("Should return correct type string for single dimension")
        void testGetTypeSingleDimension() {
            ArrayType arrayType = new ArrayType(baseType, 1);
            assertEquals("int[]", arrayType.type());
        }

        @Test
        @DisplayName("Should return correct type string for multiple dimensions")
        void testGetTypeMultipleDimensions() {
            ArrayType arrayType = new ArrayType(baseType, 3);
            assertEquals("int[][][]", arrayType.type());
        }

        @Test
        @DisplayName("Should return correct type string for zero dimensions")
        void testGetTypeZeroDimensions() {
            ArrayType arrayType = new ArrayType(baseType, 0);
            assertEquals("int", arrayType.type());
        }

        @Test
        @DisplayName("Should always return true for isArray()")
        void testIsArray() {
            ArrayType arrayType = new ArrayType(baseType);
            assertTrue(arrayType.isArray());
            
            // Even with zero dimensions, it's still an ArrayType
            ArrayType zeroDimArray = new ArrayType(baseType, 0);
            assertTrue(zeroDimArray.isArray());
        }

        @Test
        @DisplayName("Should implement IType interface correctly")
        void testITypeInterface() {
            ArrayType arrayType = new ArrayType(baseType);
            
            assertTrue(arrayType instanceof IType);
            assertEquals("int[]", arrayType.type());
            assertTrue(arrayType.isArray());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should get and set base type correctly")
        void testGetSetBaseType() {
            ArrayType arrayType = new ArrayType(baseType);
            IType newBaseType = Primitive.DOUBLE;
            
            assertEquals(baseType, arrayType.getBaseType());
            
            arrayType.setBaseType(newBaseType);
            assertEquals(newBaseType, arrayType.getBaseType());
            assertEquals("double[]", arrayType.type());
        }

        @Test
        @DisplayName("Should get and set dimension correctly")
        void testGetSetDimension() {
            ArrayType arrayType = new ArrayType(baseType, 2);
            
            assertEquals(2, arrayType.getDimension());
            
            arrayType.setDimension(5);
            assertEquals(5, arrayType.getDimension());
            assertEquals("int[][][][][]", arrayType.type());
        }

        @Test
        @DisplayName("Should handle setting null base type")
        void testSetNullBaseType() {
            ArrayType arrayType = new ArrayType(baseType);
            
            arrayType.setBaseType(null);
            assertNull(arrayType.getBaseType());
        }

        @Test
        @DisplayName("Should update type when base type changes")
        void testTypeUpdateOnBaseTypeChange() {
            ArrayType arrayType = new ArrayType(baseType);
            assertEquals("int[]", arrayType.type());
            
            arrayType.setBaseType(Primitive.DOUBLE);
            assertEquals("double[]", arrayType.type());
        }

        @Test
        @DisplayName("Should update type when dimension changes")
        void testTypeUpdateOnDimensionChange() {
            ArrayType arrayType = new ArrayType(baseType);
            assertEquals("int[]", arrayType.type());
            
            arrayType.setDimension(3);
            assertEquals("int[][][]", arrayType.type());
        }
    }

    @Nested
    @DisplayName("String Representation Tests")
    class StringRepresentationTests {

        @Test
        @DisplayName("Should return correct toString")
        void testToString() {
            ArrayType arrayType = new ArrayType(baseType, 2);
            
            assertEquals("int[][]", arrayType.toString());
            assertEquals(arrayType.type(), arrayType.toString());
        }

        @Test
        @DisplayName("Should handle null base type in toString")
        void testToStringWithNullBaseType() {
            ArrayType arrayType = new ArrayType(baseType);
            arrayType.setBaseType(null);
            
            assertThrows(NullPointerException.class, () -> {
                arrayType.toString();
            });
        }

        @Test
        @DisplayName("Should handle complex base types")
        void testToStringWithComplexBaseTypes() {
            ArrayType nestedArray = new ArrayType(baseType, 2); // int[][]
            ArrayType outerArray = new ArrayType(nestedArray, 1); // (int[][])[]
            
            // The result should be int[][] + [] = int[][][]
            assertEquals("int[][][]", outerArray.toString());
        }
    }

    @Nested
    @DisplayName("Complex Base Types Tests")
    class ComplexBaseTypesTests {

        @Test
        @DisplayName("Should handle primitive base types")
        void testPrimitiveBaseTypes() {
            for (Primitive primitive : Primitive.values()) {
                ArrayType arrayType = new ArrayType(primitive, 2);
                String expected = primitive.toString().toLowerCase() + "[][]";
                assertEquals(expected, arrayType.type());
            }
        }

        @Test
        @DisplayName("Should handle nested array types")
        void testNestedArrayTypes() {
            ArrayType innerArray = new ArrayType(baseType, 2); // int[][]
            ArrayType outerArray = new ArrayType(innerArray, 3); // (int[][])[][][]
            
            assertEquals(innerArray, outerArray.getBaseType());
            assertEquals(3, outerArray.getDimension());
            // int[][] + [][][] = int[][][][][]
            assertEquals("int[][][][][]", outerArray.type());
        }

        @Test
        @DisplayName("Should handle custom IType implementations")
        void testCustomITypeImplementations() {
            IType customType = new IType() {
                @Override
                public String type() {
                    return "CustomType";
                }
                
                @Override
                public String toString() {
                    return type();
                }
            };
            
            ArrayType arrayType = new ArrayType(customType, 2);
            assertEquals("CustomType[][]", arrayType.type());
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle very high dimensions")
        void testVeryHighDimensions() {
            ArrayType arrayType = new ArrayType(baseType, 100);
            
            assertEquals(100, arrayType.getDimension());
            String expected = "int" + "[]".repeat(100);
            assertEquals(expected, arrayType.type());
        }

        @Test
        @DisplayName("Should handle dimension changes from positive to negative")
        void testDimensionChangeToNegative() {
            ArrayType arrayType = new ArrayType(baseType, 3);
            assertEquals("int[][][]", arrayType.type());
            
            arrayType.setDimension(-1);
            assertEquals(-1, arrayType.getDimension());
        }

        @Test
        @DisplayName("Should handle multiple base type changes")
        void testMultipleBaseTypeChanges() {
            ArrayType arrayType = new ArrayType(baseType, 2);
            assertEquals("int[][]", arrayType.type());
            
            arrayType.setBaseType(Primitive.BOOLEAN);
            assertEquals("boolean[][]", arrayType.type());
            
            arrayType.setBaseType(Primitive.FLOAT);
            assertEquals("float[][]", arrayType.type());
        }

        @Test
        @DisplayName("Should maintain consistency after multiple operations")
        void testConsistencyAfterOperations() {
            ArrayType arrayType = new ArrayType(baseType, 2);
            
            // Perform multiple operations
            IType originalBase = arrayType.getBaseType();
            int originalDimension = arrayType.getDimension();
            
            arrayType.setBaseType(Primitive.DOUBLE);
            arrayType.setDimension(3);
            arrayType.setBaseType(originalBase);
            arrayType.setDimension(originalDimension);
            
            // Should be back to original state
            assertEquals(originalBase, arrayType.getBaseType());
            assertEquals(originalDimension, arrayType.getDimension());
            assertEquals("int[][]", arrayType.type());
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should work with all primitive types")
        void testWithAllPrimitiveTypes() {
            for (Primitive primitive : Primitive.values()) {
                ArrayType arrayType = new ArrayType(primitive);
                
                assertEquals(primitive, arrayType.getBaseType());
                assertTrue(arrayType.isArray());
                assertTrue(arrayType.type().contains("[]"));
                assertEquals(arrayType.type(), arrayType.toString());
            }
        }

        @Test
        @DisplayName("Should work correctly with array of arrays")
        void testArrayOfArrays() {
            ArrayType innerArray = new ArrayType(Primitive.INT, 2); // int[][]
            ArrayType outerArray = new ArrayType(innerArray, 1); // (int[][])[]
            
            assertTrue(innerArray.isArray());
            assertTrue(outerArray.isArray());
            assertEquals("int[][][]", outerArray.type());
        }

        @Test
        @DisplayName("Should support deep nesting")
        void testDeepNesting() {
            ArrayType level1 = new ArrayType(Primitive.CHAR);        // char[]
            ArrayType level2 = new ArrayType(level1);               // char[][]
            ArrayType level3 = new ArrayType(level2);               // char[][][]
            ArrayType level4 = new ArrayType(level3);               // char[][][][]
            
            assertEquals("char[][][][]", level4.type());
            assertEquals(1, level4.getDimension());
            assertEquals(level3, level4.getBaseType());
        }
    }
}
