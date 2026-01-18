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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GenericType Tests")
public class GenericTypeTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create GenericType with type and array flag")
        void testConstructorWithTypeAndArrayFlag() {
            GenericType genericType = new GenericType("List<String>", false);
            
            assertEquals("List<String>", genericType.type());
            assertFalse(genericType.isArray());
        }

        @Test
        @DisplayName("Should create GenericType as array type")
        void testConstructorAsArrayType() {
            GenericType genericType = new GenericType("Map<String, Integer>", true);
            
            assertEquals("Map<String, Integer>", genericType.type());
            assertTrue(genericType.isArray());
        }

        @Test
        @DisplayName("Should handle null type")
        void testConstructorWithNullType() {
            GenericType genericType = new GenericType(null, false);
            
            assertNull(genericType.type());
            assertFalse(genericType.isArray());
        }

        @Test
        @DisplayName("Should handle empty type")
        void testConstructorWithEmptyType() {
            GenericType genericType = new GenericType("", true);
            
            assertEquals("", genericType.type());
            assertTrue(genericType.isArray());
        }
    }

    @Nested
    @DisplayName("Type Interface Implementation Tests")
    class TypeInterfaceTests {

        @Test
        @DisplayName("Should return correct type string")
        void testGetType() {
            GenericType genericType = new GenericType("ArrayList<Integer>", false);
            assertEquals("ArrayList<Integer>", genericType.type());
        }

        @Test
        @DisplayName("Should return correct array status for non-array")
        void testIsArrayFalse() {
            GenericType genericType = new GenericType("Set<String>", false);
            assertFalse(genericType.isArray());
        }

        @Test
        @DisplayName("Should return correct array status for array")
        void testIsArrayTrue() {
            GenericType genericType = new GenericType("List<Object>", true);
            assertTrue(genericType.isArray());
        }

        @Test
        @DisplayName("Should implement IType interface correctly")
        void testITypeInterface() {
            GenericType genericType = new GenericType("HashMap<String, Object>", true);
            
            assertTrue(genericType instanceof IType);
            assertEquals("HashMap<String, Object>", genericType.type());
            assertTrue(genericType.isArray());
        }
    }

    @Nested
    @DisplayName("Immutability Tests")
    class ImmutabilityTests {

        @Test
        @DisplayName("Should be immutable - type cannot be changed")
        void testTypeImmutability() {
            GenericType genericType = new GenericType("Vector<Double>", false);
            String originalType = genericType.type();
            
            // Multiple calls should return the same value
            assertEquals(originalType, genericType.type());
            assertEquals("Vector<Double>", genericType.type());
        }

        @Test
        @DisplayName("Should be immutable - array flag cannot be changed")
        void testArrayFlagImmutability() {
            GenericType genericType = new GenericType("LinkedList<Character>", true);
            boolean originalArrayFlag = genericType.isArray();
            
            // Multiple calls should return the same value
            assertEquals(originalArrayFlag, genericType.isArray());
            assertTrue(genericType.isArray());
        }

        @Test
        @DisplayName("Should maintain consistency across multiple method calls")
        void testConsistency() {
            GenericType genericType = new GenericType("TreeMap<Integer, String>", false);
            
            // Multiple calls should be consistent
            for (int i = 0; i < 10; i++) {
                assertEquals("TreeMap<Integer, String>", genericType.type());
                assertFalse(genericType.isArray());
            }
        }
    }

    @Nested
    @DisplayName("Complex Generic Types Tests")
    class ComplexGenericTypesTests {

        @Test
        @DisplayName("Should handle simple generic types")
        void testSimpleGenericTypes() {
            GenericType listType = new GenericType("List<String>", false);
            assertEquals("List<String>", listType.type());
            assertFalse(listType.isArray());
        }

        @Test
        @DisplayName("Should handle nested generic types")
        void testNestedGenericTypes() {
            GenericType nestedType = new GenericType("Map<String, List<Integer>>", true);
            assertEquals("Map<String, List<Integer>>", nestedType.type());
            assertTrue(nestedType.isArray());
        }

        @Test
        @DisplayName("Should handle deeply nested generic types")
        void testDeeplyNestedGenericTypes() {
            GenericType deepType = new GenericType("Map<String, Map<Integer, List<Set<Object>>>>", false);
            assertEquals("Map<String, Map<Integer, List<Set<Object>>>>", deepType.type());
            assertFalse(deepType.isArray());
        }

        @Test
        @DisplayName("Should handle generic types with wildcards")
        void testGenericTypesWithWildcards() {
            GenericType wildcardType = new GenericType("List<? extends Number>", true);
            assertEquals("List<? extends Number>", wildcardType.type());
            assertTrue(wildcardType.isArray());
        }

        @Test
        @DisplayName("Should handle generic types with bounded wildcards")
        void testGenericTypesWithBoundedWildcards() {
            GenericType boundedType = new GenericType("Map<? super String, ? extends Collection<Integer>>", false);
            assertEquals("Map<? super String, ? extends Collection<Integer>>", boundedType.type());
            assertFalse(boundedType.isArray());
        }
    }

    @Nested
    @DisplayName("String Representation Tests")
    class StringRepresentationTests {

        @Test
        @DisplayName("Should have meaningful toString")
        void testToString() {
            GenericType genericType = new GenericType("Optional<String>", false);
            String str = genericType.toString();
            
            assertNotNull(str);
            // Default toString should contain class name and likely the type
            assertTrue(str.contains("GenericType") || str.contains("Optional<String>"));
        }

        @Test
        @DisplayName("Should handle null type in toString")
        void testToStringWithNullType() {
            GenericType genericType = new GenericType(null, true);
            
            assertDoesNotThrow(() -> {
                String str = genericType.toString();
                assertNotNull(str);
            });
        }

        @Test
        @DisplayName("Should handle empty type in toString")
        void testToStringWithEmptyType() {
            GenericType genericType = new GenericType("", false);
            
            String str = genericType.toString();
            assertNotNull(str);
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle very long type names")
        void testVeryLongTypeNames() {
            String longType = "VeryLong" + "Type".repeat(100) + "<String>";
            GenericType genericType = new GenericType(longType, true);
            
            assertEquals(longType, genericType.type());
            assertTrue(genericType.isArray());
        }

        @Test
        @DisplayName("Should handle special characters in type names")
        void testSpecialCharactersInTypeNames() {
            GenericType genericType = new GenericType("Type$With@Special#Chars<Value>", false);
            
            assertEquals("Type$With@Special#Chars<Value>", genericType.type());
            assertFalse(genericType.isArray());
        }

        @Test
        @DisplayName("Should handle Unicode in type names")
        void testUnicodeInTypeNames() {
            GenericType genericType = new GenericType("Type名前<値>", true);
            
            assertEquals("Type名前<値>", genericType.type());
            assertTrue(genericType.isArray());
        }

        @Test
        @DisplayName("Should handle type names with newlines")
        void testTypeNamesWithNewlines() {
            GenericType genericType = new GenericType("Type\nWith\nNewlines<Value>", false);
            
            assertEquals("Type\nWith\nNewlines<Value>", genericType.type());
            assertFalse(genericType.isArray());
        }

        @Test
        @DisplayName("Should handle type names with quotes")
        void testTypeNamesWithQuotes() {
            GenericType genericType = new GenericType("Type\"With\"Quotes<\"Value\">", true);
            
            assertEquals("Type\"With\"Quotes<\"Value\">", genericType.type());
            assertTrue(genericType.isArray());
        }
    }

    @Nested
    @DisplayName("Comparison and Equality Tests")
    class ComparisonTests {

        @Test
        @DisplayName("Should create different instances for same parameters")
        void testDifferentInstances() {
            GenericType type1 = new GenericType("List<String>", false);
            GenericType type2 = new GenericType("List<String>", false);
            
            // Should be different instances
            assertNotSame(type1, type2);
            
            // But should have same values
            assertEquals(type1.type(), type2.type());
            assertEquals(type1.isArray(), type2.isArray());
        }

        @Test
        @DisplayName("Should distinguish between array and non-array types")
        void testArrayVsNonArray() {
            GenericType arrayType = new GenericType("Set<Integer>", true);
            GenericType nonArrayType = new GenericType("Set<Integer>", false);
            
            assertEquals(arrayType.type(), nonArrayType.type());
            assertNotEquals(arrayType.isArray(), nonArrayType.isArray());
        }

        @Test
        @DisplayName("Should handle case sensitivity in type names")
        void testCaseSensitivity() {
            GenericType upperType = new GenericType("LIST<STRING>", false);
            GenericType lowerType = new GenericType("list<string>", false);
            
            assertNotEquals(upperType.type(), lowerType.type());
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should work with common Java generic types")
        void testCommonJavaGenericTypes() {
            String[] commonTypes = {
                "List<String>",
                "Map<String, Integer>",
                "Set<Object>",
                "Optional<Double>",
                "Collection<? extends Number>",
                "Supplier<List<String>>",
                "Function<String, Integer>",
                "BiFunction<String, Integer, Boolean>"
            };
            
            for (String type : commonTypes) {
                GenericType arrayGeneric = new GenericType(type, true);
                GenericType nonArrayGeneric = new GenericType(type, false);
                
                assertEquals(type, arrayGeneric.type());
                assertEquals(type, nonArrayGeneric.type());
                assertTrue(arrayGeneric.isArray());
                assertFalse(nonArrayGeneric.isArray());
            }
        }

        @Test
        @DisplayName("Should work correctly with IType interface methods")
        void testITypeInterfaceMethods() {
            GenericType genericType = new GenericType("Stream<Map<String, List<Integer>>>", true);
            
            // Test as IType
            IType iType = genericType;
            assertEquals("Stream<Map<String, List<Integer>>>", iType.type());
            assertTrue(iType.isArray());
        }

        @Test
        @DisplayName("Should maintain behavior with repeated operations")
        void testRepeatedOperations() {
            GenericType genericType = new GenericType("ConcurrentHashMap<String, AtomicInteger>", false);
            
            // Perform multiple operations and verify consistency
            for (int i = 0; i < 100; i++) {
                assertEquals("ConcurrentHashMap<String, AtomicInteger>", genericType.type());
                assertFalse(genericType.isArray());
                assertTrue(genericType instanceof IType);
            }
        }
    }
}
