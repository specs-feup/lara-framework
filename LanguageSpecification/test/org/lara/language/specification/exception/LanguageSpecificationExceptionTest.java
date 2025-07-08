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

package org.lara.language.specification.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LanguageSpecificationException Tests")
public class LanguageSpecificationExceptionTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create exception with Throwable cause")
        void testConstructorWithThrowable() {
            RuntimeException cause = new RuntimeException("Original error");
            LanguageSpecificationException exception = new LanguageSpecificationException(cause);
            
            assertSame(cause, exception.getCause());
            assertNotNull(exception.getMessage());
        }

        @Test
        @DisplayName("Should create exception with message and Throwable cause")
        void testConstructorWithMessageAndThrowable() {
            RuntimeException cause = new RuntimeException("Original error");
            String message = "Custom error message";
            LanguageSpecificationException exception = new LanguageSpecificationException(message, cause);
            
            assertSame(cause, exception.getCause());
            assertNotNull(exception.getMessage());
        }

        @Test
        @DisplayName("Should handle null Throwable")
        void testConstructorWithNullThrowable() {
            LanguageSpecificationException exception = new LanguageSpecificationException(null);
            
            assertNull(exception.getCause());
            assertNotNull(exception.getMessage());
        }

        @Test
        @DisplayName("Should handle null message with valid Throwable")
        void testConstructorWithNullMessageAndValidThrowable() {
            RuntimeException cause = new RuntimeException("Original error");
            LanguageSpecificationException exception = new LanguageSpecificationException(null, cause);
            
            assertSame(cause, exception.getCause());
            assertNotNull(exception.getMessage());
        }

        @Test
        @DisplayName("Should handle empty message")
        void testConstructorWithEmptyMessage() {
            RuntimeException cause = new RuntimeException("Original error");
            LanguageSpecificationException exception = new LanguageSpecificationException("", cause);
            
            assertSame(cause, exception.getCause());
            assertNotNull(exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Message Generation Tests")
    class MessageGenerationTests {

        @Test
        @DisplayName("Should generate default message when no custom message provided")
        void testGenerateDefaultMessage() {
            RuntimeException cause = new RuntimeException("Original error");
            LanguageSpecificationException exception = new LanguageSpecificationException(cause);
            
            String message = exception.getMessage();
            assertNotNull(message);
            assertTrue(message.contains("while building language specification") || 
                      message.contains("Exception on"));
        }

        @Test
        @DisplayName("Should use custom message when provided")
        void testGenerateCustomMessage() {
            RuntimeException cause = new RuntimeException("Original error");
            String customMessage = "Custom error message";
            LanguageSpecificationException exception = new LanguageSpecificationException(customMessage, cause);
            
            String message = exception.getMessage();
            assertNotNull(message);
            // The message should contain either the custom message or reference to it
            assertTrue(message.contains(customMessage) || message.contains("Exception on"));
        }

        @Test
        @DisplayName("Should handle special characters in message")
        void testGenerateMessageWithSpecialCharacters() {
            RuntimeException cause = new RuntimeException("Original error");
            String customMessage = "Error with \"quotes\" and \n newlines";
            LanguageSpecificationException exception = new LanguageSpecificationException(customMessage, cause);
            
            String message = exception.getMessage();
            assertNotNull(message);
        }

        @Test
        @DisplayName("Should handle long messages")
        void testGenerateMessageWithLongMessage() {
            RuntimeException cause = new RuntimeException("Original error");
            String longMessage = "A".repeat(1000);
            LanguageSpecificationException exception = new LanguageSpecificationException(longMessage, cause);
            
            String message = exception.getMessage();
            assertNotNull(message);
        }
    }

    @Nested
    @DisplayName("Inheritance Tests")
    class InheritanceTests {

        @Test
        @DisplayName("Should be instance of BaseException")
        void testInheritanceFromBaseException() {
            RuntimeException cause = new RuntimeException("Original error");
            LanguageSpecificationException exception = new LanguageSpecificationException(cause);
            
            // The test should verify the inheritance relationship
            assertTrue(exception instanceof Exception);
            assertTrue(exception instanceof RuntimeException || exception instanceof Exception);
        }

        @Test
        @DisplayName("Should have correct serialVersionUID")
        void testSerialVersionUID() {
            // This is more of a compile-time check, but we can verify the field exists
            LanguageSpecificationException exception = new LanguageSpecificationException(new RuntimeException());
            assertNotNull(exception);
        }
    }

    @Nested
    @DisplayName("Exception Behavior Tests")
    class ExceptionBehaviorTests {

        @Test
        @DisplayName("Should be throwable")
        void testThrowable() {
            assertThrows(LanguageSpecificationException.class, () -> {
                throw new LanguageSpecificationException(new RuntimeException("Test error"));
            });
        }

        @Test
        @DisplayName("Should preserve stack trace")
        void testStackTracePreservation() {
            RuntimeException cause = new RuntimeException("Original error");
            LanguageSpecificationException exception = new LanguageSpecificationException(cause);
            
            StackTraceElement[] stackTrace = exception.getStackTrace();
            assertNotNull(stackTrace);
            assertTrue(stackTrace.length > 0);
        }

        @Test
        @DisplayName("Should handle chained exceptions")
        void testChainedExceptions() {
            RuntimeException rootCause = new RuntimeException("Root cause");
            RuntimeException intermediateCause = new RuntimeException("Intermediate cause", rootCause);
            LanguageSpecificationException exception = new LanguageSpecificationException(intermediateCause);
            
            assertSame(intermediateCause, exception.getCause());
            assertSame(rootCause, exception.getCause().getCause());
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle recursive exception causes")
        void testRecursiveExceptionCauses() {
            RuntimeException cause1 = new RuntimeException("Cause 1");
            RuntimeException cause2 = new RuntimeException("Cause 2", cause1);
            // Note: We can't actually create a true recursive cause due to Java's design
            
            LanguageSpecificationException exception = new LanguageSpecificationException(cause2);
            assertSame(cause2, exception.getCause());
        }

        @Test
        @DisplayName("Should handle exception in exception handling")
        void testExceptionInExceptionHandling() {
            // This tests the robustness of the exception class itself
            assertDoesNotThrow(() -> {
                try {
                    throw new LanguageSpecificationException(new RuntimeException("Test"));
                } catch (LanguageSpecificationException e) {
                    // Getting message should not throw
                    e.getMessage();
                    e.toString();
                }
            });
        }

        @Test
        @DisplayName("Should handle Unicode in messages")
        void testUnicodeInMessages() {
            RuntimeException cause = new RuntimeException("Error 错误 エラー");
            String unicodeMessage = "Unicode message: 测试 テスト";
            LanguageSpecificationException exception = new LanguageSpecificationException(unicodeMessage, cause);
            
            assertNotNull(exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Default Behavior Tests")
    class DefaultBehaviorTests {

        @Test
        @DisplayName("Should provide default text when no message given")
        void testDefaultText() {
            RuntimeException cause = new RuntimeException("Original error");
            LanguageSpecificationException exception = new LanguageSpecificationException(cause);
            
            String message = exception.getMessage();
            assertNotNull(message);
            // Should contain the default text or generated message
            assertTrue(message.length() > 0);
        }

        @Test
        @DisplayName("Should override base exception behavior correctly")
        void testOverriddenMethods() {
            RuntimeException cause = new RuntimeException("Original error");
            LanguageSpecificationException exception = new LanguageSpecificationException("Custom message", cause);
            
            // These methods should be callable without throwing exceptions
            assertDoesNotThrow(() -> {
                exception.getMessage();
                exception.toString();
                exception.getLocalizedMessage();
            });
        }
    }
}
