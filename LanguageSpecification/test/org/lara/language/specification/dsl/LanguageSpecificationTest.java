/**
 * Copyright 2025 SPeCS.
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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.lara.language.specification.exception.LanguageSpecificationException;
import pt.up.fe.specs.lara.langspec.LangSpecsXmlParser;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Comprehensive unit tests for the LanguageSpecification class.
 * Tests cover XML parsing, DSL construction, join point management, and error handling.
 */
@DisplayName("LanguageSpecification Tests")
class LanguageSpecificationTest {

    private LanguageSpecification langSpec;
    private final String SAMPLE_JOIN_POINT_XML = """
        <joinpoints root_alias="root" root_class="file">
            <joinpoint class="file">
                <select alias="function" class="function"/> 
            </joinpoint>
            <joinpoint class="function">
                <select alias="input" class="var"/>
                <select class="body"/>
            </joinpoint>
            <joinpoint class="body">
                <select class="statement"/>
            </joinpoint>
            <joinpoint class="statement"/>
            <joinpoint class="var"/>
        </joinpoints>
        """;

    private final String SAMPLE_ACTION_XML = """
        <actions>
            <action name="replaceWith" return="joinpoint">
                <parameter name="node" type="joinpoint"/>
            </action>
            <action name="insert" class="*">
                <parameter name="position" type="string" default="before"/>
                <parameter name="code" type="template"/>
            </action>
        </actions>
        """;

    private final String SAMPLE_ATTRIBUTE_XML = """
        <?xml version="1.0"?>
        <artifacts>
            <global>
                <attribute name="name" type="String"/>
                <attribute name="line" type="Integer"/>
            </global>
        </artifacts>
        """;

    @BeforeEach
    void setUp() throws Exception {
        // Create a basic language specification for most tests
        try (InputStream joinPointStream = new ByteArrayInputStream(SAMPLE_JOIN_POINT_XML.getBytes());
             InputStream actionStream = new ByteArrayInputStream(SAMPLE_ACTION_XML.getBytes());
             InputStream attributeStream = new ByteArrayInputStream(SAMPLE_ATTRIBUTE_XML.getBytes())) {
            
            langSpec = LangSpecsXmlParser.parse(joinPointStream, attributeStream, actionStream);
        }
    }

    @Nested
    @DisplayName("Basic Properties Tests")
    class BasicPropertiesTests {

        @Test
        @DisplayName("Should have correct root information")
        void shouldHaveCorrectRootInformation() {
            assertNotNull(langSpec.getRoot());
            assertEquals("file", langSpec.getRoot().getName());
            assertEquals("root", langSpec.getRootAlias());
        }

        @Test
        @DisplayName("Should return all join points")
        void shouldReturnAllJoinPoints() {
            List<JoinPointClass> allJoinPoints = langSpec.getAllJoinPoints();
            
            assertNotNull(allJoinPoints);
            assertFalse(allJoinPoints.isEmpty());
            assertEquals(6, allJoinPoints.size()); // Actual count based on parser behavior
            
            // Verify expected join points exist
            List<String> joinPointNames = allJoinPoints.stream()
                .map(JoinPointClass::getName)
                .collect(Collectors.toList());
            assertTrue(joinPointNames.contains("file"));
            assertTrue(joinPointNames.contains("function"));
            assertTrue(joinPointNames.contains("body"));
            assertTrue(joinPointNames.contains("statement"));
            assertTrue(joinPointNames.contains("var"));
        }

        @Test
        @DisplayName("Should return join point names")
        void shouldReturnJoinPointNames() {
            List<String> names = langSpec.getAllJoinPoints().stream()
                    .map(JoinPointClass::getName)
                    .collect(Collectors.toList());
            
            assertNotNull(names);
            assertFalse(names.isEmpty());
            assertTrue(names.contains("file"));
            assertTrue(names.contains("function"));
            assertTrue(names.contains("body"));
            assertTrue(names.contains("statement"));
            assertTrue(names.contains("var"));
        }
    }

    @Nested
    @DisplayName("Join Point Retrieval Tests")
    class JoinPointRetrievalTests {

        @Test
        @DisplayName("Should retrieve existing join point by name")
        void shouldRetrieveExistingJoinPointByName() {
            JoinPointClass fileJoinPoint = langSpec.getJoinPoint("file");
            
            assertNotNull(fileJoinPoint);
            assertEquals("file", fileJoinPoint.getName());
        }

        @Test
        @DisplayName("Should return null for non-existent join point")
        void shouldReturnNullForNonExistentJoinPoint() {
            assertNull(langSpec.getJoinPoint("nonexistent"));
        }

        @Test
        @DisplayName("Should check if join point exists")
        void shouldCheckIfJoinPointExists() {
            assertTrue(langSpec.hasJoinPoint("file"));
            assertTrue(langSpec.hasJoinPoint("function"));
            assertFalse(langSpec.hasJoinPoint("nonexistent"));
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle null join point name gracefully")
        void shouldHandleNullJoinPointNameGracefully() {
            assertNull(langSpec.getJoinPoint(null));
        }

        @Test
        @DisplayName("Should handle empty join point name gracefully")
        void shouldHandleEmptyJoinPointNameGracefully() {
            assertNull(langSpec.getJoinPoint(""));
        }

        @Test
        @DisplayName("Should reject duplicate join point definitions")
        void shouldRejectDuplicateJoinPoints() {
            LanguageSpecification spec = new LanguageSpecification();
            spec.add(new JoinPointClass("dup"));

            LanguageSpecificationException thrown = assertThrows(LanguageSpecificationException.class,
                    () -> spec.add(new JoinPointClass("dup")));

            assertEquals("Duplicate join point class 'dup' while building language specification",
                    thrown.getSimpleMessage());
        }
    }

    @Nested
    @DisplayName("XML Parsing Tests")
    class XmlParsingTests {

        @Test
        @DisplayName("Should parse from streams successfully")
        void shouldParseFromStreamsSuccessfully() throws Exception {
            try (InputStream joinPointStream = new ByteArrayInputStream(SAMPLE_JOIN_POINT_XML.getBytes());
                 InputStream actionStream = new ByteArrayInputStream(SAMPLE_ACTION_XML.getBytes());
                 InputStream attributeStream = new ByteArrayInputStream(SAMPLE_ATTRIBUTE_XML.getBytes())) {
                
                LanguageSpecification parsedSpec = LangSpecsXmlParser.parse(joinPointStream, attributeStream, actionStream);

                assertNotNull(parsedSpec);
                assertEquals("file", parsedSpec.getRoot().getName());
                assertEquals(6, parsedSpec.getAllJoinPoints().size());
            }
        }

        @Test
        @DisplayName("Should handle malformed XML gracefully")
        void shouldHandleMalformedXmlGracefully() {
            String malformedXml = "<invalid><xml>";
            
            assertThrows(RuntimeException.class, () -> {
                try (InputStream stream = new ByteArrayInputStream(malformedXml.getBytes())) {
                    LangSpecsXmlParser.parse(stream, stream, stream);
                }
            });
        }

        @Test
        @DisplayName("Should handle empty XML gracefully")
        void shouldHandleEmptyXmlGracefully() {
            String emptyXml = "";
            
            assertThrows(RuntimeException.class, () -> {
                try (InputStream stream = new ByteArrayInputStream(emptyXml.getBytes())) {
                    LangSpecsXmlParser.parse(stream, stream, stream);
                }
            });
        }
    }

    @Nested
    @DisplayName("ToString and String Representation Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should provide meaningful string representation")
        void shouldProvideMeaningfulStringRepresentation() {
            String stringRepresentation = langSpec.toString();
            
            assertNotNull(stringRepresentation);
            assertFalse(stringRepresentation.isEmpty());
            assertTrue(stringRepresentation.contains("file")); // Should contain root join point
        }
    }

    @Nested
    @DisplayName("Edge Cases and Boundary Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle specifications with single join point")
        void shouldHandleSpecificationsWithSingleJoinPoint() throws Exception {
            String singleJoinPointXml = """
                <joinpoints root_alias="root" root_class="simple">
                    <joinpoint class="simple"/>
                </joinpoints>
                """;
            
            String emptyActionsXml = "<actions></actions>";
            String emptyAttributesXml = "<artifacts></artifacts>";

            try (InputStream joinPointStream = new ByteArrayInputStream(singleJoinPointXml.getBytes());
                 InputStream actionStream = new ByteArrayInputStream(emptyActionsXml.getBytes());
                 InputStream attributeStream = new ByteArrayInputStream(emptyAttributesXml.getBytes())) {
                
                LanguageSpecification simpleSpec = LangSpecsXmlParser.parse(joinPointStream, attributeStream, actionStream);
                
                assertEquals(2, simpleSpec.getAllJoinPoints().size());
                assertEquals("simple", simpleSpec.getRoot().getName());
                assertEquals("root", simpleSpec.getRootAlias());
            }
        }

        @Test
        @DisplayName("Should handle specifications with complex inheritance hierarchy")
        void shouldHandleSpecificationsWithComplexInheritanceHierarchy() throws Exception {
            String complexHierarchyXml = """
                <joinpoints root_alias="root" root_class="base">
                    <joinpoint class="base"/>
                    <joinpoint class="level1" extends="base"/>
                    <joinpoint class="level2a" extends="level1"/>
                    <joinpoint class="level2b" extends="level1"/>
                    <joinpoint class="level3" extends="level2a"/>
                </joinpoints>
                """;
            
            String emptyActionsXml = "<actions></actions>";
            String emptyAttributesXml = "<artifacts></artifacts>";

            try (InputStream joinPointStream = new ByteArrayInputStream(complexHierarchyXml.getBytes());
                 InputStream actionStream = new ByteArrayInputStream(emptyActionsXml.getBytes());
                 InputStream attributeStream = new ByteArrayInputStream(emptyAttributesXml.getBytes())) {
                
                LanguageSpecification complexSpec = LangSpecsXmlParser.parse(joinPointStream, attributeStream, actionStream);
                
                assertEquals(6, complexSpec.getAllJoinPoints().size());
                
                // Test inheritance relationships
                JoinPointClass level3 = complexSpec.getJoinPoint("level3");
                assertTrue(level3.getExtend().isPresent());
                assertEquals("level2a", level3.getExtend().get().getName());
            }
        }
    }

    @Nested
    @DisplayName("Action and Attribute Tests")
    class ActionAndAttributeTests {

        @Test
        @DisplayName("Should retrieve all actions")
        void shouldRetrieveAllActions() {
            List<Action> allActions = langSpec.getAllActions();
            
            assertNotNull(allActions);
            assertFalse(allActions.isEmpty());
            
            // Should have the global replaceWith action and class-specific insert action
            assertTrue(allActions.stream().anyMatch(action -> "replaceWith".equals(action.getName())));
            assertTrue(allActions.stream().anyMatch(action -> "insert".equals(action.getName())));
        }

        @Test
        @DisplayName("Should check if action exists")
        void shouldCheckIfActionExists() {
            assertTrue(langSpec.hasAction("replaceWith"));
            assertTrue(langSpec.hasAction("insert"));
            assertFalse(langSpec.hasAction("nonexistent"));
        }

        @Test
        @DisplayName("Should check if attribute exists")
        void shouldCheckIfAttributeExists() {
            assertTrue(langSpec.hasAttribute("name"));
            assertTrue(langSpec.hasAttribute("line"));
            assertFalse(langSpec.hasAttribute("nonexistent"));
        }
    }
}
