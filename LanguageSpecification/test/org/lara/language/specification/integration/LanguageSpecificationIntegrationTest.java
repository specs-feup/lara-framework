package org.lara.language.specification.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junitpioneer.jupiter.RetryingTest;
import org.lara.language.specification.dsl.LanguageSpecification;
import org.lara.language.specification.dsl.JoinPointClass;
import org.lara.language.specification.dsl.Action;
import org.lara.language.specification.dsl.Attribute;
import org.lara.language.specification.dsl.types.IType;
import org.lara.language.specification.dsl.types.ThisType;
import org.lara.language.specification.dsl.types.ParameterizedType;

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
                assertNotNull(attr.getType().type());
                assertFalse(attr.getType().type().trim().isEmpty());
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

    @RetryingTest(5)
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

    // ==================== ThisType and Generics Integration Tests ====================

    @Test
    void testThisTypeInAttributeReturnType() throws IOException {
        File specDir = createSpecWithThisType();
        LanguageSpecification langSpec = LanguageSpecification.newInstance(specDir);

        JoinPointClass node = langSpec.getJoinPoint("node");
        assertNotNull(node);

        // Find the 'clone' attribute which returns 'this'
        Attribute cloneAttr = node.getAttributesSelf().stream()
                .filter(a -> "clone".equals(a.getName()))
                .findFirst()
                .orElse(null);

        assertNotNull(cloneAttr, "Should have 'clone' attribute");
        IType returnType = cloneAttr.getType();
        
        // Should be ThisType, not resolved to JPType
        assertInstanceOf(ThisType.class, returnType);
        assertEquals("this", returnType.toString());
    }

    @Test
    void testThisTypeInActionReturnType() throws IOException {
        File specDir = createSpecWithThisType();
        LanguageSpecification langSpec = LanguageSpecification.newInstance(specDir);

        JoinPointClass node = langSpec.getJoinPoint("node");
        assertNotNull(node);

        // Find the 'copy' action which returns 'this'
        Action copyAction = node.getActionsSelf().stream()
                .filter(a -> "copy".equals(a.getName()))
                .findFirst()
                .orElse(null);

        assertNotNull(copyAction, "Should have 'copy' action");
        assertEquals("this", copyAction.getReturnType());
    }

    @Test
    void testThisTypeInActionParameter() throws IOException {
        File specDir = createSpecWithThisType();
        LanguageSpecification langSpec = LanguageSpecification.newInstance(specDir);

        JoinPointClass node = langSpec.getJoinPoint("node");
        assertNotNull(node);

        // Find the 'merge' action which has 'this' as parameter type
        Action mergeAction = node.getActionsSelf().stream()
                .filter(a -> "merge".equals(a.getName()))
                .findFirst()
                .orElse(null);

        assertNotNull(mergeAction, "Should have 'merge' action");
        assertEquals(1, mergeAction.getParameters().size());
        assertEquals("this", mergeAction.getParameters().get(0).getType());
    }

    @Test
    void testGenericTypeWithThisArgument() throws IOException {
        File specDir = createSpecWithThisType();
        LanguageSpecification langSpec = LanguageSpecification.newInstance(specDir);

        JoinPointClass node = langSpec.getJoinPoint("node");
        assertNotNull(node);

        // Find the 'children' attribute which returns 'List<this>'
        Attribute childrenAttr = node.getAttributesSelf().stream()
                .filter(a -> "children".equals(a.getName()))
                .findFirst()
                .orElse(null);

        assertNotNull(childrenAttr, "Should have 'children' attribute");
        IType returnType = childrenAttr.getType();
        
        // Should be ParameterizedType with ThisType argument
        assertInstanceOf(ParameterizedType.class, returnType);
        ParameterizedType paramType = (ParameterizedType) returnType;
        assertEquals("List<this>", paramType.toString());
        assertInstanceOf(ThisType.class, paramType.getTypeArguments().get(0));
    }

    @Test
    void testComplexGenericWithThis() throws IOException {
        File specDir = createSpecWithThisType();
        LanguageSpecification langSpec = LanguageSpecification.newInstance(specDir);

        JoinPointClass node = langSpec.getJoinPoint("node");
        assertNotNull(node);

        // Find the 'metadata' attribute which returns 'Map<String, this>'
        Attribute metadataAttr = node.getAttributesSelf().stream()
                .filter(a -> "metadata".equals(a.getName()))
                .findFirst()
                .orElse(null);

        assertNotNull(metadataAttr, "Should have 'metadata' attribute");
        IType returnType = metadataAttr.getType();
        
        // Should be ParameterizedType: Map<String, this>
        assertInstanceOf(ParameterizedType.class, returnType);
        ParameterizedType paramType = (ParameterizedType) returnType;
        assertEquals("Map<String, this>", paramType.toString());
        assertEquals(2, paramType.getTypeArguments().size());
        assertInstanceOf(ThisType.class, paramType.getTypeArguments().get(1));
    }

    @Test
    void testNestedGenericWithThis() throws IOException {
        File specDir = createSpecWithThisType();
        LanguageSpecification langSpec = LanguageSpecification.newInstance(specDir);

        JoinPointClass node = langSpec.getJoinPoint("node");
        assertNotNull(node);

        // Find the 'nestedChildren' attribute which returns 'List<List<this>>'
        Attribute nestedAttr = node.getAttributesSelf().stream()
                .filter(a -> "nestedChildren".equals(a.getName()))
                .findFirst()
                .orElse(null);

        assertNotNull(nestedAttr, "Should have 'nestedChildren' attribute");
        IType returnType = nestedAttr.getType();
        
        // Should be ParameterizedType: List<List<this>>
        assertInstanceOf(ParameterizedType.class, returnType);
        ParameterizedType outerType = (ParameterizedType) returnType;
        assertEquals("List<List<this>>", outerType.toString());
        
        // Inner type should also be ParameterizedType
        IType innerType = outerType.getTypeArguments().get(0);
        assertInstanceOf(ParameterizedType.class, innerType);
        ParameterizedType innerParamType = (ParameterizedType) innerType;
        assertInstanceOf(ThisType.class, innerParamType.getTypeArguments().get(0));
    }

    @Test
    void testThisTypePreservedInInheritedAttribute() throws IOException {
        File specDir = createSpecWithThisType();
        LanguageSpecification langSpec = LanguageSpecification.newInstance(specDir);

        JoinPointClass node = langSpec.getJoinPoint("node");
        JoinPointClass expr = langSpec.getJoinPoint("expr");
        assertNotNull(node);
        assertNotNull(expr);

        // expr extends node, so it should inherit the 'clone' attribute
        // The 'this' type should still be ThisType (late-bound)
        Attribute exprClone = expr.getAttributes().stream()
                .filter(a -> "clone".equals(a.getName()))
                .findFirst()
                .orElse(null);

        assertNotNull(exprClone, "expr should inherit 'clone' attribute from node");
        IType returnType = exprClone.getType();
        
        // Should still be ThisType, not resolved to 'expr' or 'node'
        assertInstanceOf(ThisType.class, returnType);
        assertEquals("this", returnType.toString());
    }

    /**
     * Creates a language specification with 'this' type usage.
     */
    private File createSpecWithThisType() throws IOException {
        File specDir = tempDir.resolve("this-type-spec").toFile();
        specDir.mkdirs();

        // Create joinPointModel.xml
        try (FileWriter writer = new FileWriter(new File(specDir, "joinPointModel.xml"))) {
            writer.write("<?xml version=\"1.0\"?>\n");
            writer.write("<joinpoints root_alias=\"root\" root_class=\"node\">\n");
            writer.write("  <joinpoint class=\"node\"/>\n");
            writer.write("  <joinpoint class=\"expr\" extends=\"node\"/>\n");
            writer.write("</joinpoints>\n");
        }

        // Create artifacts.xml with 'this' and generic types
        try (FileWriter writer = new FileWriter(new File(specDir, "artifacts.xml"))) {
            writer.write("<?xml version=\"1.0\"?>\n");
            writer.write("<artifacts>\n");
            writer.write("  <artifact class=\"node\">\n");
            // Attribute returning 'this'
            writer.write("    <attribute name=\"clone\" type=\"this\"/>\n");
            // Attribute returning List<this>
            writer.write("    <attribute name=\"children\" type=\"List&lt;this&gt;\"/>\n");
            // Attribute returning Map<String, this>
            writer.write("    <attribute name=\"metadata\" type=\"Map&lt;String, this&gt;\"/>\n");
            // Attribute returning nested generic List<List<this>>
            writer.write("    <attribute name=\"nestedChildren\" type=\"List&lt;List&lt;this&gt;&gt;\"/>\n");
            writer.write("  </artifact>\n");
            writer.write("</artifacts>\n");
        }

        // Create actionModel.xml with 'this' in return and parameter types
        try (FileWriter writer = new FileWriter(new File(specDir, "actionModel.xml"))) {
            writer.write("<?xml version=\"1.0\"?>\n");
            writer.write("<actions>\n");
            // Action returning 'this'
            writer.write("  <action name=\"copy\" class=\"node\" return=\"this\"/>\n");
            // Action with 'this' as parameter type
            writer.write("  <action name=\"merge\" class=\"node\" return=\"void\">\n");
            writer.write("    <parameter name=\"other\" type=\"this\"/>\n");
            writer.write("  </action>\n");
            writer.write("</actions>\n");
        }

        return specDir;
    }
}
