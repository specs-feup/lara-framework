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

package org.lara.language.specification.dsl.types;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.lara.language.specification.dsl.JoinPointClass;
import org.lara.language.specification.exception.LanguageSpecificationException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JPType Tests")
public class JPTypeTest {

    private JoinPointClass testJoinPoint;

    @BeforeEach
    void setUp() {
        testJoinPoint = new JoinPointClass("TestJoinPoint");
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create JPType with JoinPointClass")
        void testConstructorWithJoinPointClass() {
            JPType jpType = new JPType(testJoinPoint);
            
            assertNotNull(jpType);
            assertEquals(testJoinPoint, jpType.getJointPoint());
            assertEquals("TestJoinPoint", jpType.type());
        }

        @Test
        @DisplayName("Should handle null JoinPointClass")
        void testConstructorWithNullJoinPointClass() {
            JPType jpType = new JPType(null);
            
            assertNotNull(jpType);
            assertNull(jpType.getJointPoint());
        }
    }

    @Nested
    @DisplayName("Factory Method Tests")
    class FactoryMethodTests {

        @Test
        @DisplayName("Should create JPType using of() method")
        void testOfFactoryMethod() {
            JPType jpType = JPType.of(testJoinPoint);
            
            assertNotNull(jpType);
            assertEquals(testJoinPoint, jpType.getJointPoint());
            assertEquals("TestJoinPoint", jpType.type());
        }

        @Test
        @DisplayName("Should handle null in of() method")
        void testOfFactoryMethodWithNull() {
            JPType jpType = JPType.of(null);
            
            assertNotNull(jpType);
            assertNull(jpType.getJointPoint());
        }

        @Test
        @DisplayName("Should be equivalent to constructor")
        void testFactoryMethodEquivalence() {
            JPType jpType1 = new JPType(testJoinPoint);
            JPType jpType2 = JPType.of(testJoinPoint);
            
            assertEquals(jpType1.getJointPoint(), jpType2.getJointPoint());
            assertEquals(jpType1.type(), jpType2.type());
            assertEquals(jpType1.toString(), jpType2.toString());
        }
    }

    @Nested
    @DisplayName("Type Interface Implementation Tests")
    class TypeInterfaceTests {

        @Test
        @DisplayName("Should return correct type name")
        void testGetType() {
            JPType jpType = new JPType(testJoinPoint);
            
            assertEquals("TestJoinPoint", jpType.type());
        }

        @Test
        @DisplayName("Should handle null join point in getType")
        void testGetTypeWithNullJoinPoint() {
            JPType jpType = new JPType(null);
            
            assertThrows(NullPointerException.class, () -> {
                jpType.type();
            });
        }

        @Test
        @DisplayName("Should implement IType interface correctly")
        void testITypeInterface() {
            JPType jpType = new JPType(testJoinPoint);
            
            assertTrue(jpType instanceof IType);
            assertEquals("TestJoinPoint", jpType.type());
            assertFalse(jpType.isArray()); // Default implementation should return false
        }

        @Test
        @DisplayName("Should handle complex join point names")
        void testComplexJoinPointNames() {
            JoinPointClass complexJoinPoint = new JoinPointClass("Complex_JoinPoint_123");
            JPType jpType = new JPType(complexJoinPoint);
            
            assertEquals("Complex_JoinPoint_123", jpType.type());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should get and set joint point correctly")
        void testGetSetJointPoint() {
            JPType jpType = new JPType(testJoinPoint);
            JoinPointClass newJoinPoint = new JoinPointClass("NewJoinPoint");
            
            assertEquals(testJoinPoint, jpType.getJointPoint());
            
            jpType.setJointPoint(newJoinPoint);
            assertEquals(newJoinPoint, jpType.getJointPoint());
            assertEquals("NewJoinPoint", jpType.type());
        }

        @Test
        @DisplayName("Should handle setting null joint point")
        void testSetNullJointPoint() {
            JPType jpType = new JPType(testJoinPoint);
            
            jpType.setJointPoint(null);
            assertNull(jpType.getJointPoint());
        }

        @Test
        @DisplayName("Should update type when joint point changes")
        void testTypeUpdateOnJointPointChange() {
            JPType jpType = new JPType(testJoinPoint);
            assertEquals("TestJoinPoint", jpType.type());
            
            JoinPointClass newJoinPoint = new JoinPointClass("UpdatedJoinPoint");
            jpType.setJointPoint(newJoinPoint);
            assertEquals("UpdatedJoinPoint", jpType.type());
        }
    }

    @Nested
    @DisplayName("String Representation Tests")
    class StringRepresentationTests {

        @Test
        @DisplayName("Should return correct toString")
        void testToString() {
            JPType jpType = new JPType(testJoinPoint);
            
            assertEquals("TestJoinPoint", jpType.toString());
            assertEquals(jpType.type(), jpType.toString());
        }

        @Test
        @DisplayName("Should handle null joint point in toString")
        void testToStringWithNullJointPoint() {
            JPType jpType = new JPType(null);
            
            assertThrows(NullPointerException.class, () -> {
                jpType.toString();
            });
        }

        @Test
        @DisplayName("Should handle empty joint point name")
        void testToStringWithEmptyJointPointName() {
            JoinPointClass emptyJoinPoint = new JoinPointClass("");
            JPType jpType = new JPType(emptyJoinPoint);
            
            assertEquals("", jpType.toString());
        }

        @Test
        @DisplayName("Should reject special characters in join point name")
        void testRejectSpecialCharactersInJoinPointName() {
            assertThrows(LanguageSpecificationException.class,
                    () -> new JoinPointClass("JoinPoint$With@Special#Chars"));
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle very long join point names")
        void testVeryLongJointPointName() {
            String longName = "A".repeat(1000);
            JoinPointClass longJoinPoint = new JoinPointClass(longName);
            JPType jpType = new JPType(longJoinPoint);
            
            assertEquals(longName, jpType.type());
            assertEquals(longName, jpType.toString());
        }

        @Test
        @DisplayName("Should handle Unicode in join point names")
        void testUnicodeJointPointName() {
            JoinPointClass unicodeJoinPoint = new JoinPointClass("JoinPoint名前");
            JPType jpType = new JPType(unicodeJoinPoint);
            
            assertEquals("JoinPoint名前", jpType.type());
            assertEquals("JoinPoint名前", jpType.toString());
        }

        @Test
        @DisplayName("Should handle multiple type instances")
        void testMultipleInstances() {
            JPType jpType1 = new JPType(testJoinPoint);
            JPType jpType2 = new JPType(testJoinPoint);
            
            // Should be different instances but same content
            assertNotSame(jpType1, jpType2);
            assertEquals(jpType1.type(), jpType2.type());
            assertEquals(jpType1.toString(), jpType2.toString());
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should work with join point class hierarchy")
        void testWithJoinPointClassHierarchy() {
            JoinPointClass parentJoinPoint = new JoinPointClass("ParentJoinPoint");
            JoinPointClass childJoinPoint = new JoinPointClass("ChildJoinPoint");
            childJoinPoint.setExtend(parentJoinPoint);
            
            JPType childType = new JPType(childJoinPoint);
            assertEquals("ChildJoinPoint", childType.type());
            
            JPType parentType = new JPType(parentJoinPoint);
            assertEquals("ParentJoinPoint", parentType.type());
        }

        @Test
        @DisplayName("Should maintain consistency after multiple operations")
        void testConsistencyAfterOperations() {
            JPType jpType = new JPType(testJoinPoint);
            String originalType = jpType.type();
            String originalString = jpType.toString();
            
            // Perform multiple operations
            JoinPointClass temp = jpType.getJointPoint();
            jpType.setJointPoint(temp);
            
            // Should remain consistent
            assertEquals(originalType, jpType.type());
            assertEquals(originalString, jpType.toString());
        }

        @Test
        @DisplayName("Should work correctly with different join point configurations")
        void testWithDifferentJoinPointConfigurations() {
            // Simple join point
            JoinPointClass simpleJP = new JoinPointClass("Simple");
            JPType simpleType = new JPType(simpleJP);
            assertEquals("Simple", simpleType.type());
            
            // Join point with attributes
            JoinPointClass complexJP = new JoinPointClass("Complex");
            // Add some attributes to make it more complex
            JPType complexType = new JPType(complexJP);
            assertEquals("Complex", complexType.type());
        }
    }
}
