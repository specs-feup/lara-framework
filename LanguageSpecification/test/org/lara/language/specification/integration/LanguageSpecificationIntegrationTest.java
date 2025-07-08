package org.lara.language.specification.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.lara.language.specification.dsl.LanguageSpecification;
import org.lara.language.specification.dsl.JoinPointClass;
import org.lara.language.specification.dsl.Action;
import org.lara.language.specification.dsl.Attribute;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the LanguageSpecification project.
 * Tests end-to-end functionality across DSL, AST, and type systems.
 */
class LanguageSpecificationIntegrationTest {

    @TempDir
    Path tempDir;

    private File testXmlFile;

    @BeforeEach
    void setUp() throws IOException {
        testXmlFile = createTestXMLDirectory();
    }

    private File createTestXMLDirectory() throws IOException {
        // Create a directory for the language specification files
        File specDir = tempDir.resolve("test-spec").toFile();
        specDir.mkdirs();
        
        // Create joinPointModel.xml
        try (FileWriter writer = new FileWriter(new File(specDir, "joinPointModel.xml"))) {
            writer.write("<?xml version=\"1.0\"?>\n");
            writer.write("<joinpoints root_alias=\"root\" root_class=\"statement\">\n");
            writer.write("  <joinpoint class=\"statement\"/>\n");
            writer.write("  <joinpoint class=\"loop\" extends=\"statement\">\n");
            writer.write("  </joinpoint>\n");
            writer.write("  <joinpoint class=\"if\" extends=\"statement\">\n");
            writer.write("  </joinpoint>\n");
            writer.write("</joinpoints>\n");
        }
        
        // Create artifacts.xml
        try (FileWriter writer = new FileWriter(new File(specDir, "artifacts.xml"))) {
            writer.write("<?xml version=\"1.0\"?>\n");
            writer.write("<artifacts>\n");
            writer.write("  <global>\n");
            writer.write("    <attribute name=\"global_attr\" type=\"String\"/>\n");
            writer.write("  </global>\n");
            writer.write("  <artifact class=\"statement\" default=\"id\">\n");
            writer.write("    <attribute name=\"id\" type=\"String\"/>\n");
            writer.write("    <attribute name=\"line\" type=\"Integer\"/>\n");
            writer.write("  </artifact>\n");
            writer.write("  <artifact class=\"loop\">\n");
            writer.write("    <attribute name=\"kind\" type=\"String\"/>\n");
            writer.write("  </artifact>\n");
            writer.write("  <artifact class=\"if\">\n");
            writer.write("    <attribute name=\"condition\" type=\"String\"/>\n");
            writer.write("  </artifact>\n");
            writer.write("</artifacts>\n");
        }
        
        // Create actionModel.xml
        try (FileWriter writer = new FileWriter(new File(specDir, "actionModel.xml"))) {
            writer.write("<?xml version=\"1.0\"?>\n");
            writer.write("<actions>\n");
            writer.write("  <action name=\"replace\" class=\"statement\" return=\"void\">\n");
            writer.write("    <parameter name=\"code\" type=\"String\"/>\n");
            writer.write("  </action>\n");
            writer.write("  <action name=\"unroll\" class=\"loop\" return=\"void\">\n");
            writer.write("    <parameter name=\"factor\" type=\"Integer\"/>\n");
            writer.write("  </action>\n");
            writer.write("</actions>\n");
        }
        
        return specDir;
    }

    @Test
    void testEndToEndXMLParsing() {
        LanguageSpecification langSpec = LanguageSpecification.newInstance(testXmlFile);
        
        assertNotNull(langSpec);
        assertNotNull(langSpec.getAllJoinPoints());
        assertFalse(langSpec.getAllJoinPoints().isEmpty());
        
        // Test that we have the expected join points
        assertTrue(langSpec.getAllJoinPoints().size() >= 3);
        
        // Test that specific join points exist
        JoinPointClass statement = langSpec.getJoinPoint("statement");
        assertNotNull(statement);
        assertEquals("statement", statement.getName());
        
        JoinPointClass loop = langSpec.getJoinPoint("loop");
        assertNotNull(loop);
        assertEquals("loop", loop.getName());
        assertTrue(loop.getExtend().isPresent());
        assertEquals("statement", loop.getExtend().get().getName());
        
        JoinPointClass ifJp = langSpec.getJoinPoint("if");
        assertNotNull(ifJp);
        assertEquals("if", ifJp.getName());
        assertTrue(ifJp.getExtend().isPresent());
        assertEquals("statement", ifJp.getExtend().get().getName());
    }

    @Test
    void testInheritanceHierarchy() {
        LanguageSpecification langSpec = LanguageSpecification.newInstance(testXmlFile);
        
        JoinPointClass statement = langSpec.getJoinPoint("statement");
        JoinPointClass loop = langSpec.getJoinPoint("loop");
        JoinPointClass ifJp = langSpec.getJoinPoint("if");
        
        // Test inheritance relationships
        assertNotNull(statement);
        assertNotNull(loop);
        assertNotNull(ifJp);
        
        // Test attribute inheritance
        assertEquals(3, statement.getAttributes().size()); // id, line, global_attr
        assertTrue(loop.getAttributes().size() >= 4); // inherited + own (id, line, global_attr, kind)
        assertTrue(ifJp.getAttributes().size() >= 4); // inherited + own (id, line, global_attr, condition)
        
        // Test action inheritance
        assertEquals(1, statement.getActions().size()); // replace
        assertTrue(loop.getActions().size() >= 2); // inherited + own (replace, unroll)
        assertEquals(1, ifJp.getActions().size()); // inherited only (replace)
    }

    @Test
    void testAttributeTypeSystem() {
        LanguageSpecification langSpec = LanguageSpecification.newInstance(testXmlFile);
        
        JoinPointClass statement = langSpec.getJoinPoint("statement");
        
        // Test attributes exist and have correct types
        Attribute idAttr = null;
        Attribute lineAttr = null;
        
        for (Attribute attr : statement.getAttributes()) {
            if ("id".equals(attr.getName())) {
                idAttr = attr;
            } else if ("line".equals(attr.getName())) {
                lineAttr = attr;
            }
        }
        
        assertNotNull(idAttr);
        assertNotNull(lineAttr);
        
        assertEquals("String", idAttr.getType().toString());
        assertEquals("Integer", lineAttr.getType().toString());
    }

    @Test
    void testActionParameterSystem() {
        LanguageSpecification langSpec = LanguageSpecification.newInstance(testXmlFile);
        
        JoinPointClass statement = langSpec.getJoinPoint("statement");
        JoinPointClass loop = langSpec.getJoinPoint("loop");
        
        // Test statement replace action
        Action replaceAction = null;
        for (Action action : statement.getActions()) {
            if ("replace".equals(action.getName())) {
                replaceAction = action;
                break;
            }
        }
        
        assertNotNull(replaceAction);
        assertEquals("replace", replaceAction.getName());
        assertEquals("void", replaceAction.getReturnType());
        assertEquals(1, replaceAction.getParameters().size());
        assertEquals("code", replaceAction.getParameters().get(0).getName());
        
        // Test loop unroll action
        Action unrollAction = null;
        for (Action action : loop.getActions()) {
            if ("unroll".equals(action.getName())) {
                unrollAction = action;
                break;
            }
        }
        
        assertNotNull(unrollAction);
        assertEquals("unroll", unrollAction.getName());
        assertEquals(1, unrollAction.getParameters().size());
        assertEquals("factor", unrollAction.getParameters().get(0).getName());
    }

    @Test
    void testDSLToASTIntegration() {
        LanguageSpecification langSpec = LanguageSpecification.newInstance(testXmlFile);
        
        // Test that we can convert from DSL to AST representation
        assertNotNull(langSpec);
        
        // The XML parsing uses NodeFactory internally
        // Test that the factory has created appropriate structures
        JoinPointClass statement = langSpec.getJoinPoint("statement");
        assertNotNull(statement);
        
        // Test that the DSL classes work correctly with the parsed data
        assertTrue(statement.getAttributes().size() > 0);
        assertTrue(statement.getActions().size() > 0);
    }

    @Test
    void testComplexInheritanceChain() {
        LanguageSpecification langSpec = LanguageSpecification.newInstance(testXmlFile);
        
        JoinPointClass statement = langSpec.getJoinPoint("statement");
        JoinPointClass loop = langSpec.getJoinPoint("loop");
        JoinPointClass ifJp = langSpec.getJoinPoint("if");
        
        // Test that loop inherits from statement
        assertTrue(loop.getAttributes().size() >= statement.getAttributes().size());
        assertTrue(loop.getActions().size() >= statement.getActions().size());
        
        // Test that if inherits from statement  
        assertTrue(ifJp.getAttributes().size() >= statement.getAttributes().size());
        assertEquals(statement.getActions().size(), ifJp.getActions().size()); // if has no additional actions
        
        // Test that inherited attributes/actions are actually present
        boolean hasInheritedIdAttribute = loop.getAttributes().stream()
            .anyMatch(attr -> "id".equals(attr.getName()));
        assertTrue(hasInheritedIdAttribute);
        
        boolean hasInheritedReplaceAction = loop.getActions().stream()
            .anyMatch(action -> "replace".equals(action.getName()));
        assertTrue(hasInheritedReplaceAction);
    }

    @Test
    void testXMLParsingErrorHandling() {
        // Test with non-existent file
        File nonExistentFile = new File(tempDir.toFile(), "non-existent.xml");
        
        assertThrows(RuntimeException.class, () -> {
            LanguageSpecification.newInstance(nonExistentFile);
        });
    }

    @Test
    void testTypeSystemConsistency() {
        LanguageSpecification langSpec = LanguageSpecification.newInstance(testXmlFile);
        
        // Test that all attributes have valid types
        for (JoinPointClass jp : langSpec.getAllJoinPoints()) {
            for (Attribute attr : jp.getAttributes()) {
                assertNotNull(attr.getType());
                assertNotNull(attr.getType().getType());
                assertFalse(attr.getType().getType().trim().isEmpty());
            }
            
            // Test that all actions have valid return types and parameter types
            for (Action action : jp.getActions()) {
                assertNotNull(action.getReturnType());
                
                for (org.lara.language.specification.dsl.Parameter param : action.getParameters()) {
                    assertNotNull(param.getType());
                    assertFalse(param.getType().trim().isEmpty());
                }
            }
        }
    }

    @Test
    void testJoinPointLookupPerformance() {
        LanguageSpecification langSpec = LanguageSpecification.newInstance(testXmlFile);
        
        // Test that join point lookup is efficient (should be fast even with repeated calls)
        long startTime = System.nanoTime();
        
        for (int i = 0; i < 1000; i++) {
            JoinPointClass statement = langSpec.getJoinPoint("statement");
            JoinPointClass loop = langSpec.getJoinPoint("loop");
            JoinPointClass ifJp = langSpec.getJoinPoint("if");
            
            assertNotNull(statement);
            assertNotNull(loop);
            assertNotNull(ifJp);
        }
        
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        
        // Should complete 1000 lookups in reasonable time (less than 100ms)
        assertTrue(duration < 100_000_000, "Join point lookup should be fast");
    }

    @Test
    void testCompleteWorkflow() {
        // Test a complete workflow from XML parsing to DSL manipulation
        LanguageSpecification langSpec = LanguageSpecification.newInstance(testXmlFile);
        
        // 1. Parse and validate structure
        assertNotNull(langSpec.getAllJoinPoints());
        assertTrue(langSpec.getAllJoinPoints().size() >= 3);
        
        // 2. Navigate inheritance hierarchy
        JoinPointClass statement = langSpec.getJoinPoint("statement");
        JoinPointClass loop = langSpec.getJoinPoint("loop");
        
        // 3. Examine attributes and actions
        assertTrue(statement.getAttributes().size() > 0);
        assertTrue(statement.getActions().size() > 0);
        assertTrue(loop.getAttributes().size() > statement.getAttributes().size());
        assertTrue(loop.getActions().size() > statement.getActions().size());
        
        // 4. Verify type system works
        for (Attribute attr : statement.getAttributes()) {
            assertNotNull(attr.getType());
            assertNotNull(attr.getName());
        }
        
        // 5. Verify parameter system works
        for (Action action : statement.getActions()) {
            assertNotNull(action.getName());
            assertNotNull(action.getReturnType());
            for (org.lara.language.specification.dsl.Parameter param : action.getParameters()) {
                assertNotNull(param.getName());
                assertNotNull(param.getType());
            }
        }
        
        // 6. Test string representations work
        assertNotNull(statement.toString());
        assertFalse(statement.toString().trim().isEmpty());
    }
}
