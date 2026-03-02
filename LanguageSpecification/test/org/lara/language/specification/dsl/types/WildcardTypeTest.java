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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link WildcardType}.
 * 
 * <p>Tests cover factory methods, kind checking, string representation,
 * bounds access, equality/hashCode, validation, and integration with
 * other IType implementations.</p>
 */
@DisplayName("WildcardType Tests")
class WildcardTypeTest {

    @Nested
    @DisplayName("Factory Method Tests")
    class FactoryMethodTests {

        @Test
        @DisplayName("unbounded() should create unbounded wildcard")
        void testUnboundedFactory() {
            WildcardType wildcard = WildcardType.unbounded();

            assertNotNull(wildcard);
            assertEquals(WildcardType.Kind.UNBOUNDED, wildcard.getKind());
            assertNull(wildcard.getBound());
        }

        @Test
        @DisplayName("extendsType() should create upper bounded wildcard")
        void testExtendsTypeFactory() {
            IType bound = PrimitiveClasses.STRING;
            WildcardType wildcard = WildcardType.extendsType(bound);

            assertNotNull(wildcard);
            assertEquals(WildcardType.Kind.EXTENDS, wildcard.getKind());
            assertEquals(bound, wildcard.getBound());
        }

        @Test
        @DisplayName("superType() should create lower bounded wildcard")
        void testSuperTypeFactory() {
            IType bound = PrimitiveClasses.INTEGER;
            WildcardType wildcard = WildcardType.superType(bound);

            assertNotNull(wildcard);
            assertEquals(WildcardType.Kind.SUPER, wildcard.getKind());
            assertEquals(bound, wildcard.getBound());
        }

        @Test
        @DisplayName("extendsType() should reject null bound")
        void testExtendsTypeRejectsNull() {
            assertThrows(NullPointerException.class, () -> WildcardType.extendsType(null));
        }

        @Test
        @DisplayName("superType() should reject null bound")
        void testSuperTypeRejectsNull() {
            assertThrows(NullPointerException.class, () -> WildcardType.superType(null));
        }
    }

    @Nested
    @DisplayName("Kind Checking Tests")
    class KindCheckingTests {

        @Test
        @DisplayName("isUnbounded() should return true for unbounded wildcard")
        void testIsUnboundedTrue() {
            WildcardType wildcard = WildcardType.unbounded();

            assertTrue(wildcard.isUnbounded());
            assertFalse(wildcard.isUpperBounded());
            assertFalse(wildcard.isLowerBounded());
        }

        @Test
        @DisplayName("isUpperBounded() should return true for extends wildcard")
        void testIsUpperBoundedTrue() {
            WildcardType wildcard = WildcardType.extendsType(PrimitiveClasses.STRING);

            assertFalse(wildcard.isUnbounded());
            assertTrue(wildcard.isUpperBounded());
            assertFalse(wildcard.isLowerBounded());
        }

        @Test
        @DisplayName("isLowerBounded() should return true for super wildcard")
        void testIsLowerBoundedTrue() {
            WildcardType wildcard = WildcardType.superType(PrimitiveClasses.STRING);

            assertFalse(wildcard.isUnbounded());
            assertFalse(wildcard.isUpperBounded());
            assertTrue(wildcard.isLowerBounded());
        }
    }

    @Nested
    @DisplayName("String Representation Tests")
    class StringRepresentationTests {

        @Test
        @DisplayName("Unbounded wildcard type() should return '?'")
        void testUnboundedType() {
            WildcardType wildcard = WildcardType.unbounded();

            assertEquals("?", wildcard.type());
        }

        @Test
        @DisplayName("Upper bounded wildcard type() should return '? extends T'")
        void testUpperBoundedType() {
            WildcardType wildcard = WildcardType.extendsType(PrimitiveClasses.STRING);

            assertEquals("? extends String", wildcard.type());
        }

        @Test
        @DisplayName("Lower bounded wildcard type() should return '? super T'")
        void testLowerBoundedType() {
            WildcardType wildcard = WildcardType.superType(PrimitiveClasses.INTEGER);

            assertEquals("? super Integer", wildcard.type());
        }

        @Test
        @DisplayName("toString() should match type() for unbounded")
        void testToStringMatchesTypeUnbounded() {
            WildcardType wildcard = WildcardType.unbounded();

            assertEquals(wildcard.type(), wildcard.toString());
            assertEquals("?", wildcard.toString());
        }

        @Test
        @DisplayName("toString() should match type() for upper bounded")
        void testToStringMatchesTypeUpperBounded() {
            WildcardType wildcard = WildcardType.extendsType(PrimitiveClasses.DOUBLE);

            assertEquals(wildcard.type(), wildcard.toString());
            assertEquals("? extends Double", wildcard.toString());
        }

        @Test
        @DisplayName("toString() should match type() for lower bounded")
        void testToStringMatchesTypeLowerBounded() {
            WildcardType wildcard = WildcardType.superType(PrimitiveClasses.LONG);

            assertEquals(wildcard.type(), wildcard.toString());
            assertEquals("? super Long", wildcard.toString());
        }
    }

    @Nested
    @DisplayName("Bounds Access Tests")
    class BoundsAccessTests {

        @Test
        @DisplayName("getBound() should return null for unbounded wildcard")
        void testGetBoundUnbounded() {
            WildcardType wildcard = WildcardType.unbounded();

            assertNull(wildcard.getBound());
        }

        @Test
        @DisplayName("getBound() should return bound type for extends wildcard")
        void testGetBoundExtends() {
            IType bound = PrimitiveClasses.STRING;
            WildcardType wildcard = WildcardType.extendsType(bound);

            assertSame(bound, wildcard.getBound());
        }

        @Test
        @DisplayName("getBound() should return bound type for super wildcard")
        void testGetBoundSuper() {
            IType bound = PrimitiveClasses.INTEGER;
            WildcardType wildcard = WildcardType.superType(bound);

            assertSame(bound, wildcard.getBound());
        }

        @Test
        @DisplayName("getKind() should return correct kind for each type")
        void testGetKindAllTypes() {
            assertEquals(WildcardType.Kind.UNBOUNDED, WildcardType.unbounded().getKind());
            assertEquals(WildcardType.Kind.EXTENDS, WildcardType.extendsType(PrimitiveClasses.STRING).getKind());
            assertEquals(WildcardType.Kind.SUPER, WildcardType.superType(PrimitiveClasses.STRING).getKind());
        }
    }

    @Nested
    @DisplayName("Equality and HashCode Tests")
    class EqualityHashCodeTests {

        @Test
        @DisplayName("Unbounded wildcards should be equal")
        void testUnboundedEquality() {
            WildcardType w1 = WildcardType.unbounded();
            WildcardType w2 = WildcardType.unbounded();

            assertEquals(w1, w2);
            assertEquals(w1.hashCode(), w2.hashCode());
        }

        @Test
        @DisplayName("Same extends wildcards should be equal")
        void testExtendsEquality() {
            WildcardType w1 = WildcardType.extendsType(PrimitiveClasses.STRING);
            WildcardType w2 = WildcardType.extendsType(PrimitiveClasses.STRING);

            assertEquals(w1, w2);
            assertEquals(w1.hashCode(), w2.hashCode());
        }

        @Test
        @DisplayName("Same super wildcards should be equal")
        void testSuperEquality() {
            WildcardType w1 = WildcardType.superType(PrimitiveClasses.INTEGER);
            WildcardType w2 = WildcardType.superType(PrimitiveClasses.INTEGER);

            assertEquals(w1, w2);
            assertEquals(w1.hashCode(), w2.hashCode());
        }

        @Test
        @DisplayName("Different kind wildcards should not be equal")
        void testDifferentKindNotEqual() {
            WildcardType unbounded = WildcardType.unbounded();
            WildcardType extendsW = WildcardType.extendsType(PrimitiveClasses.STRING);
            WildcardType superW = WildcardType.superType(PrimitiveClasses.STRING);

            assertNotEquals(unbounded, extendsW);
            assertNotEquals(unbounded, superW);
            assertNotEquals(extendsW, superW);
        }

        @Test
        @DisplayName("Different bound types should not be equal")
        void testDifferentBoundsNotEqual() {
            WildcardType w1 = WildcardType.extendsType(PrimitiveClasses.STRING);
            WildcardType w2 = WildcardType.extendsType(PrimitiveClasses.INTEGER);

            assertNotEquals(w1, w2);
        }

        @Test
        @DisplayName("Wildcard should equal itself")
        void testEqualsItself() {
            WildcardType wildcard = WildcardType.extendsType(PrimitiveClasses.STRING);

            assertEquals(wildcard, wildcard);
        }

        @Test
        @DisplayName("Wildcard should not equal null")
        void testNotEqualsNull() {
            WildcardType wildcard = WildcardType.unbounded();

            assertNotEquals(null, wildcard);
        }

        @Test
        @DisplayName("Wildcard should not equal other types")
        void testNotEqualsOtherTypes() {
            WildcardType wildcard = WildcardType.unbounded();

            assertNotEquals(wildcard, "?");
            assertNotEquals(wildcard, new GenericType("?", false));
            assertNotEquals(wildcard, PrimitiveClasses.STRING);
        }

        @Test
        @DisplayName("HashCode should be consistent")
        void testHashCodeConsistency() {
            WildcardType wildcard = WildcardType.extendsType(PrimitiveClasses.DOUBLE);

            int hash1 = wildcard.hashCode();
            int hash2 = wildcard.hashCode();

            assertEquals(hash1, hash2);
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Constructor should reject null kind")
        void testNullKindRejected() {
            assertThrows(NullPointerException.class, () -> new WildcardType(null, null));
        }

        @Test
        @DisplayName("Constructor should reject non-null bound for UNBOUNDED")
        void testUnboundedWithBoundRejected() {
            assertThrows(IllegalArgumentException.class, () -> 
                new WildcardType(WildcardType.Kind.UNBOUNDED, PrimitiveClasses.STRING));
        }

        @Test
        @DisplayName("Constructor should reject null bound for EXTENDS")
        void testExtendsWithoutBoundRejected() {
            assertThrows(IllegalArgumentException.class, () -> 
                new WildcardType(WildcardType.Kind.EXTENDS, null));
        }

        @Test
        @DisplayName("Constructor should reject null bound for SUPER")
        void testSuperWithoutBoundRejected() {
            assertThrows(IllegalArgumentException.class, () -> 
                new WildcardType(WildcardType.Kind.SUPER, null));
        }
    }

    @Nested
    @DisplayName("Singleton Behavior Tests")
    class SingletonBehaviorTests {

        @Test
        @DisplayName("unbounded() should return same instance (singleton)")
        void testUnboundedSingleton() {
            WildcardType w1 = WildcardType.unbounded();
            WildcardType w2 = WildcardType.unbounded();

            assertSame(w1, w2);
        }

        @Test
        @DisplayName("Bounded wildcards should not be singletons")
        void testBoundedNotSingleton() {
            WildcardType e1 = WildcardType.extendsType(PrimitiveClasses.STRING);
            WildcardType e2 = WildcardType.extendsType(PrimitiveClasses.STRING);

            // They are equal but not same instance
            assertEquals(e1, e2);
            assertNotSame(e1, e2);
        }
    }

    @Nested
    @DisplayName("ThisType as Bound Tests")
    class ThisTypeAsBoundTests {

        @Test
        @DisplayName("Should support ThisType as extends bound")
        void testExtendsThis() {
            WildcardType wildcard = WildcardType.extendsType(ThisType.getInstance());

            assertEquals(WildcardType.Kind.EXTENDS, wildcard.getKind());
            assertSame(ThisType.getInstance(), wildcard.getBound());
            assertEquals("? extends this", wildcard.toString());
        }

        @Test
        @DisplayName("Should support ThisType as super bound")
        void testSuperThis() {
            WildcardType wildcard = WildcardType.superType(ThisType.getInstance());

            assertEquals(WildcardType.Kind.SUPER, wildcard.getKind());
            assertSame(ThisType.getInstance(), wildcard.getBound());
            assertEquals("? super this", wildcard.toString());
        }

        @Test
        @DisplayName("Wildcards with ThisType should have correct equality")
        void testThisTypeEquality() {
            WildcardType w1 = WildcardType.extendsType(ThisType.getInstance());
            WildcardType w2 = WildcardType.extendsType(ThisType.getInstance());

            assertEquals(w1, w2);
            assertEquals(w1.hashCode(), w2.hashCode());
        }
    }

    @Nested
    @DisplayName("IType Implementations as Bounds Tests")
    class ITypeImplementationsAsBoundsTests {

        @Test
        @DisplayName("Should support GenericType as bound")
        void testGenericTypeBound() {
            GenericType bound = new GenericType("List", false);
            WildcardType wildcard = WildcardType.extendsType(bound);

            assertEquals(bound, wildcard.getBound());
            assertEquals("? extends List", wildcard.toString());
        }

        @Test
        @DisplayName("Should support ArrayType as bound")
        void testArrayTypeBound() {
            ArrayType bound = new ArrayType(PrimitiveClasses.STRING, 1);
            WildcardType wildcard = WildcardType.superType(bound);

            assertEquals(bound, wildcard.getBound());
            assertEquals("? super String[]", wildcard.toString());
        }

        @Test
        @DisplayName("Should support ParameterizedType as bound")
        void testParameterizedTypeBound() {
            ParameterizedType bound = ParameterizedType.of(
                    new GenericType("List", false), PrimitiveClasses.STRING);
            WildcardType wildcard = WildcardType.extendsType(bound);

            assertEquals(bound, wildcard.getBound());
            assertEquals("? extends List<String>", wildcard.toString());
        }

        @Test
        @DisplayName("Should reject wildcards as bounds")
        void testNestedWildcardAsBoundRejected() {
            // Wildcard bounds cannot themselves be wildcard types
            WildcardType innerWildcard = WildcardType.unbounded();

            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> WildcardType.extendsType(innerWildcard)
            );
            assertEquals("Wildcard bounds cannot be wildcard types", exception.getMessage());
        }

        @Test
        @DisplayName("Should support Primitive as bound")
        void testPrimitiveBound() {
            // Primitives implementing IType
            WildcardType wildcard = WildcardType.extendsType(Primitive.INT);

            assertEquals(Primitive.INT, wildcard.getBound());
            assertEquals("? extends int", wildcard.toString());
        }
    }

    @Nested
    @DisplayName("IType Contract Tests")
    class ITypeContractTests {

        @Test
        @DisplayName("Should implement IType interface")
        void testImplementsIType() {
            WildcardType wildcard = WildcardType.unbounded();

            assertInstanceOf(IType.class, wildcard);
        }

        @Test
        @DisplayName("Should not be an array type")
        void testIsArrayFalse() {
            WildcardType unbounded = WildcardType.unbounded();
            WildcardType extends_ = WildcardType.extendsType(PrimitiveClasses.STRING);
            WildcardType super_ = WildcardType.superType(PrimitiveClasses.STRING);

            assertFalse(unbounded.isArray());
            assertFalse(extends_.isArray());
            assertFalse(super_.isArray());
        }

        @Test
        @DisplayName("Should be usable as IType")
        void testUsableAsIType() {
            IType type = WildcardType.extendsType(PrimitiveClasses.STRING);

            assertEquals("? extends String", type.type());
            assertEquals("? extends String", type.toString());
            assertFalse(type.isArray());
        }
    }

    @Nested
    @DisplayName("Kind Enum Tests")
    class KindEnumTests {

        @Test
        @DisplayName("UNBOUNDED kind should have empty keyword")
        void testUnboundedKeyword() {
            assertEquals("", WildcardType.Kind.UNBOUNDED.getKeyword());
        }

        @Test
        @DisplayName("EXTENDS kind should have 'extends' keyword")
        void testExtendsKeyword() {
            assertEquals("extends", WildcardType.Kind.EXTENDS.getKeyword());
        }

        @Test
        @DisplayName("SUPER kind should have 'super' keyword")
        void testSuperKeyword() {
            assertEquals("super", WildcardType.Kind.SUPER.getKeyword());
        }

        @Test
        @DisplayName("Kind enum should have exactly 3 values")
        void testKindEnumValues() {
            WildcardType.Kind[] values = WildcardType.Kind.values();

            assertEquals(3, values.length);
        }

        @Test
        @DisplayName("Kind valueOf should work correctly")
        void testKindValueOf() {
            assertEquals(WildcardType.Kind.UNBOUNDED, WildcardType.Kind.valueOf("UNBOUNDED"));
            assertEquals(WildcardType.Kind.EXTENDS, WildcardType.Kind.valueOf("EXTENDS"));
            assertEquals(WildcardType.Kind.SUPER, WildcardType.Kind.valueOf("SUPER"));
        }
    }

    @Nested
    @DisplayName("Constant Tests")
    class ConstantTests {

        @Test
        @DisplayName("WILDCARD_SYMBOL should be '?'")
        void testWildcardSymbol() {
            assertEquals("?", WildcardType.WILDCARD_SYMBOL);
        }

        @Test
        @DisplayName("Unbounded type() should use WILDCARD_SYMBOL")
        void testUnboundedUsesSymbol() {
            assertEquals(WildcardType.WILDCARD_SYMBOL, WildcardType.unbounded().type());
        }
    }
}
