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

@DisplayName("ThisType Tests")
class ThisTypeTest {

    @Nested
    @DisplayName("Singleton Pattern Tests")
    class SingletonTests {

        @Test
        @DisplayName("Should return the same instance")
        void testSingletonInstance() {
            ThisType first = ThisType.getInstance();
            ThisType second = ThisType.getInstance();
            
            assertSame(first, second);
        }

        @Test
        @DisplayName("Should not be null")
        void testNotNull() {
            assertNotNull(ThisType.getInstance());
        }
    }

    @Nested
    @DisplayName("Type Interface Implementation Tests")
    class TypeInterfaceTests {

        @Test
        @DisplayName("Should return 'this' as type string")
        void testType() {
            assertEquals("this", ThisType.getInstance().type());
        }

        @Test
        @DisplayName("Should return 'this' from toString")
        void testToString() {
            assertEquals("this", ThisType.getInstance().toString());
        }

        @Test
        @DisplayName("Should not be an array type")
        void testIsArrayFalse() {
            assertFalse(ThisType.getInstance().isArray());
        }
    }

    @Nested
    @DisplayName("Keyword Constant Tests")
    class KeywordTests {

        @Test
        @DisplayName("THIS_KEYWORD should be 'this'")
        void testThisKeyword() {
            assertEquals("this", ThisType.THIS_KEYWORD);
        }

        @Test
        @DisplayName("type() should match THIS_KEYWORD")
        void testTypeMatchesKeyword() {
            assertEquals(ThisType.THIS_KEYWORD, ThisType.getInstance().type());
        }
    }

    @Nested
    @DisplayName("Equality Tests")
    class EqualityTests {

        @Test
        @DisplayName("Should be equal to itself")
        void testEqualsItself() {
            ThisType thisType = ThisType.getInstance();
            assertEquals(thisType, thisType);
        }

        @Test
        @DisplayName("Two instances should be equal")
        void testEqualsTwoInstances() {
            // Even though it's a singleton, test the equals implementation
            assertEquals(ThisType.getInstance(), ThisType.getInstance());
        }

        @Test
        @DisplayName("Should not be equal to null")
        void testNotEqualsNull() {
            assertNotEquals(null, ThisType.getInstance());
        }

        @Test
        @DisplayName("Should not be equal to other types")
        void testNotEqualsOtherTypes() {
            assertNotEquals(ThisType.getInstance(), new GenericType("this", false));
            assertNotEquals(ThisType.getInstance(), "this");
        }

        @Test
        @DisplayName("Should have consistent hashCode")
        void testHashCode() {
            int hash1 = ThisType.getInstance().hashCode();
            int hash2 = ThisType.getInstance().hashCode();
            
            assertEquals(hash1, hash2);
            assertEquals("this".hashCode(), hash1);
        }
    }

    @Nested
    @DisplayName("IType Contract Tests")
    class ITypeContractTests {

        @Test
        @DisplayName("Should implement IType interface")
        void testImplementsIType() {
            assertInstanceOf(IType.class, ThisType.getInstance());
        }

        @Test
        @DisplayName("Should be usable as IType")
        void testUsableAsIType() {
            IType type = ThisType.getInstance();
            
            assertEquals("this", type.type());
            assertEquals("this", type.toString());
            assertFalse(type.isArray());
        }
    }
}
