/**
 * Copyright 2026 SPeCS.
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

package org.lara.language.specification.dsl.types;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ParameterizedType Tests")
class ParameterizedTypeTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create with base type and single argument")
        void testSingleArgument() {
            IType baseType = new GenericType("List", false);
            IType argType = PrimitiveClasses.STRING;
            
            ParameterizedType paramType = new ParameterizedType(baseType, List.of(argType));
            
            assertEquals(baseType, paramType.getBaseType());
            assertEquals(1, paramType.getTypeArguments().size());
            assertEquals(argType, paramType.getTypeArguments().get(0));
        }

        @Test
        @DisplayName("Should create with base type and multiple arguments")
        void testMultipleArguments() {
            IType baseType = new GenericType("Map", false);
            IType keyType = PrimitiveClasses.STRING;
            IType valueType = PrimitiveClasses.INTEGER;
            
            ParameterizedType paramType = new ParameterizedType(baseType, List.of(keyType, valueType));
            
            assertEquals(baseType, paramType.getBaseType());
            assertEquals(2, paramType.getTypeArguments().size());
            assertEquals(keyType, paramType.getTypeArguments().get(0));
            assertEquals(valueType, paramType.getTypeArguments().get(1));
        }

        @Test
        @DisplayName("Should reject null base type")
        void testNullBaseType() {
            assertThrows(NullPointerException.class, () -> 
                new ParameterizedType(null, List.of(PrimitiveClasses.STRING)));
        }

        @Test
        @DisplayName("Should reject null type arguments")
        void testNullTypeArguments() {
            assertThrows(NullPointerException.class, () -> 
                new ParameterizedType(new GenericType("List", false), null));
        }

        @Test
        @DisplayName("Should reject empty type arguments")
        void testEmptyTypeArguments() {
            assertThrows(IllegalArgumentException.class, () -> 
                new ParameterizedType(new GenericType("List", false), List.of()));
        }
    }

    @Nested
    @DisplayName("Factory Method Tests")
    class FactoryMethodTests {

        @Test
        @DisplayName("of() with single argument")
        void testOfSingleArg() {
            IType baseType = new GenericType("List", false);
            IType argType = PrimitiveClasses.STRING;
            
            ParameterizedType paramType = ParameterizedType.of(baseType, argType);
            
            assertEquals("List<String>", paramType.toString());
        }

        @Test
        @DisplayName("of() with varargs")
        void testOfVarargs() {
            IType baseType = new GenericType("Map", false);
            
            ParameterizedType paramType = ParameterizedType.of(baseType, 
                    PrimitiveClasses.STRING, PrimitiveClasses.INTEGER);
            
            assertEquals("Map<String, Integer>", paramType.toString());
        }
    }

    @Nested
    @DisplayName("Type Interface Implementation Tests")
    class TypeInterfaceTests {

        @Test
        @DisplayName("Should return correct type string for single argument")
        void testTypeSingleArg() {
            ParameterizedType paramType = ParameterizedType.of(
                    new GenericType("List", false), PrimitiveClasses.STRING);
            
            assertEquals("List<String>", paramType.type());
        }

        @Test
        @DisplayName("Should return correct type string for multiple arguments")
        void testTypeMultipleArgs() {
            ParameterizedType paramType = ParameterizedType.of(
                    new GenericType("Map", false), 
                    PrimitiveClasses.STRING, PrimitiveClasses.INTEGER);
            
            assertEquals("Map<String, Integer>", paramType.type());
        }

        @Test
        @DisplayName("toString should match type()")
        void testToStringMatchesType() {
            ParameterizedType paramType = ParameterizedType.of(
                    new GenericType("Set", false), PrimitiveClasses.DOUBLE);
            
            assertEquals(paramType.type(), paramType.toString());
        }

        @Test
        @DisplayName("Should not be an array type")
        void testIsArrayFalse() {
            ParameterizedType paramType = ParameterizedType.of(
                    new GenericType("List", false), PrimitiveClasses.STRING);
            
            assertFalse(paramType.isArray());
        }
    }

    @Nested
    @DisplayName("ThisType Integration Tests")
    class ThisTypeIntegrationTests {

        @Test
        @DisplayName("Should support ThisType as type argument")
        void testThisTypeAsArgument() {
            ParameterizedType paramType = ParameterizedType.of(
                    new GenericType("List", false), ThisType.getInstance());
            
            assertEquals("List<this>", paramType.toString());
        }

        @Test
        @DisplayName("Should support ThisType mixed with other types")
        void testThisTypeMixed() {
            ParameterizedType paramType = ParameterizedType.of(
                    new GenericType("Map", false), 
                    PrimitiveClasses.STRING, ThisType.getInstance());
            
            assertEquals("Map<String, this>", paramType.toString());
        }

    }

    @Nested
    @DisplayName("Nested Generics Tests")
    class NestedGenericsTests {

        @Test
        @DisplayName("Should support nested parameterized types")
        void testNestedParameterizedType() {
            ParameterizedType innerType = ParameterizedType.of(
                    new GenericType("List", false), PrimitiveClasses.STRING);
            ParameterizedType outerType = ParameterizedType.of(
                    new GenericType("Map", false), PrimitiveClasses.STRING, innerType);
            
            assertEquals("Map<String, List<String>>", outerType.toString());
        }

        @Test
        @DisplayName("Should handle deeply nested types")
        void testDeeplyNestedTypes() {
            ParameterizedType level1 = ParameterizedType.of(
                    new GenericType("Optional", false), ThisType.getInstance());
            ParameterizedType level2 = ParameterizedType.of(
                    new GenericType("List", false), level1);
            ParameterizedType level3 = ParameterizedType.of(
                    new GenericType("Map", false), PrimitiveClasses.STRING, level2);
            
            assertEquals("Map<String, List<Optional<this>>>", level3.toString());
            // Verify nested ThisType is accessible
            ParameterizedType innerLevel2 = (ParameterizedType) level3.getTypeArguments().get(1);
            ParameterizedType innerLevel1 = (ParameterizedType) innerLevel2.getTypeArguments().get(0);
            assertInstanceOf(ThisType.class, innerLevel1.getTypeArguments().get(0));
        }
    }

    @Nested
    @DisplayName("Equality Tests")
    class EqualityTests {

        @Test
        @DisplayName("Should be equal with same base and arguments")
        void testEquals() {
            ParameterizedType type1 = ParameterizedType.of(
                    new GenericType("List", false), PrimitiveClasses.STRING);
            ParameterizedType type2 = ParameterizedType.of(
                    new GenericType("List", false), PrimitiveClasses.STRING);
            
            assertEquals(type1, type2);
        }

        @Test
        @DisplayName("Should not be equal with different base types")
        void testNotEqualsDifferentBase() {
            ParameterizedType type1 = ParameterizedType.of(
                    new GenericType("List", false), PrimitiveClasses.STRING);
            ParameterizedType type2 = ParameterizedType.of(
                    new GenericType("Set", false), PrimitiveClasses.STRING);
            
            assertNotEquals(type1, type2);
        }

        @Test
        @DisplayName("Should not be equal with different arguments")
        void testNotEqualsDifferentArgs() {
            ParameterizedType type1 = ParameterizedType.of(
                    new GenericType("List", false), PrimitiveClasses.STRING);
            ParameterizedType type2 = ParameterizedType.of(
                    new GenericType("List", false), PrimitiveClasses.INTEGER);
            
            assertNotEquals(type1, type2);
        }

        @Test
        @DisplayName("Should have consistent hashCode")
        void testHashCode() {
            ParameterizedType type1 = ParameterizedType.of(
                    new GenericType("List", false), PrimitiveClasses.STRING);
            ParameterizedType type2 = ParameterizedType.of(
                    new GenericType("List", false), PrimitiveClasses.STRING);
            
            assertEquals(type1.hashCode(), type2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal to null")
        void testNotEqualsNull() {
            ParameterizedType type = ParameterizedType.of(
                    new GenericType("List", false), PrimitiveClasses.STRING);
            
            assertNotEquals(null, type);
        }

        @Test
        @DisplayName("Should not be equal to other types")
        void testNotEqualsOtherType() {
            ParameterizedType type = ParameterizedType.of(
                    new GenericType("List", false), PrimitiveClasses.STRING);
            
            assertNotEquals(type, new GenericType("List<String>", false));
        }
    }

    @Nested
    @DisplayName("Immutability Tests")
    class ImmutabilityTests {

        @Test
        @DisplayName("Type arguments list should be immutable")
        void testTypeArgumentsImmutable() {
            ParameterizedType type = ParameterizedType.of(
                    new GenericType("List", false), PrimitiveClasses.STRING);
            
            assertThrows(UnsupportedOperationException.class, () ->
                type.getTypeArguments().add(PrimitiveClasses.INTEGER));
        }
    }

    @Nested
    @DisplayName("IType Contract Tests")
    class ITypeContractTests {

        @Test
        @DisplayName("Should implement IType interface")
        void testImplementsIType() {
            ParameterizedType type = ParameterizedType.of(
                    new GenericType("List", false), PrimitiveClasses.STRING);
            
            assertInstanceOf(IType.class, type);
        }
    }
}
