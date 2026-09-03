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

package org.lara.language.specification.dsl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.lara.language.specification.exception.LanguageSpecificationException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("IdentifierValidator Tests")
class IdentifierValidatorTest {

    @Nested
    @DisplayName("Reserved Keywords Tests")
    class ReservedKeywordsTests {

        @Test
        @DisplayName("Should reject 'this' as identifier")
        void testRejectThis() {
            LanguageSpecificationException exception = assertThrows(
                    LanguageSpecificationException.class,
                    () -> IdentifierValidator.requireValid("this", "join point name"));
            
            assertTrue(exception.getMessage().contains("reserved keyword"));
            assertTrue(exception.getMessage().contains("this"));
        }

        @Test
        @DisplayName("isReservedKeyword should return true for 'this'")
        void testIsReservedKeywordThis() {
            assertTrue(IdentifierValidator.isReservedKeyword("this"));
        }

        @Test
        @DisplayName("isReservedKeyword should return false for normal identifiers")
        void testIsReservedKeywordNormal() {
            assertFalse(IdentifierValidator.isReservedKeyword("node"));
            assertFalse(IdentifierValidator.isReservedKeyword("myAttribute"));
            assertFalse(IdentifierValidator.isReservedKeyword("statement"));
        }

        @Test
        @DisplayName("isReservedKeyword should handle null")
        void testIsReservedKeywordNull() {
            assertFalse(IdentifierValidator.isReservedKeyword(null));
        }
    }

    @Nested
    @DisplayName("Valid Identifier Tests")
    class ValidIdentifierTests {

        @Test
        @DisplayName("Should accept valid Java-like identifiers")
        void testValidIdentifiers() {
            assertDoesNotThrow(() -> IdentifierValidator.requireValid("node", "test"));
            assertDoesNotThrow(() -> IdentifierValidator.requireValid("myAttribute", "test"));
            assertDoesNotThrow(() -> IdentifierValidator.requireValid("_privateField", "test"));
            assertDoesNotThrow(() -> IdentifierValidator.requireValid("$special", "test"));
            assertDoesNotThrow(() -> IdentifierValidator.requireValid("identifier123", "test"));
        }

        @Test
        @DisplayName("Should accept null identifier")
        void testNullIdentifier() {
            assertDoesNotThrow(() -> IdentifierValidator.requireValid(null, "test"));
        }

        @Test
        @DisplayName("Should accept empty identifier")
        void testEmptyIdentifier() {
            assertDoesNotThrow(() -> IdentifierValidator.requireValid("", "test"));
        }
    }

    @Nested
    @DisplayName("Invalid Identifier Tests")
    class InvalidIdentifierTests {

        @Test
        @DisplayName("Should reject identifiers starting with digit")
        void testRejectDigitStart() {
            LanguageSpecificationException exception = assertThrows(
                    LanguageSpecificationException.class,
                    () -> IdentifierValidator.requireValid("123abc", "attribute name"));
            
            assertTrue(exception.getMessage().contains("Java identifier rules"));
        }

        @Test
        @DisplayName("Should reject identifiers with spaces")
        void testRejectSpaces() {
            assertThrows(LanguageSpecificationException.class,
                    () -> IdentifierValidator.requireValid("my attribute", "test"));
        }

        @Test
        @DisplayName("Should reject identifiers with special characters")
        void testRejectSpecialChars() {
            assertThrows(LanguageSpecificationException.class,
                    () -> IdentifierValidator.requireValid("my-attribute", "test"));
            assertThrows(LanguageSpecificationException.class,
                    () -> IdentifierValidator.requireValid("my.attribute", "test"));
        }
    }

    @Nested
    @DisplayName("JoinPointClass Integration Tests")
    class JoinPointClassIntegrationTests {

        @Test
        @DisplayName("Should not allow creating JoinPointClass with 'this' name")
        void testJoinPointClassRejectsThis() {
            LanguageSpecificationException exception = assertThrows(
                    LanguageSpecificationException.class,
                    () -> new JoinPointClass("this"));
            
            assertTrue(exception.getMessage().contains("reserved keyword"));
        }

        @Test
        @DisplayName("Should allow creating JoinPointClass with valid name")
        void testJoinPointClassAcceptsValid() {
            assertDoesNotThrow(() -> new JoinPointClass("node"));
            assertDoesNotThrow(() -> new JoinPointClass("expression"));
            assertDoesNotThrow(() -> new JoinPointClass("statement"));
        }
    }
}
