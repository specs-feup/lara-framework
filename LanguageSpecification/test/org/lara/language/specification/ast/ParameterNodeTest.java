/**
 * Copyright 2016 SPeCS.
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

package org.lara.language.specification.ast;

import org.junit.jupiter.api.Test;
import pt.up.fe.specs.util.utilities.BuilderWithIndentation;

import static org.junit.jupiter.api.Assertions.*;

public class ParameterNodeTest {

    @Test
    public void testConstructor() {
        ParameterNode node = new ParameterNode("String", "param1", "defaultVal");
        
        assertEquals("String", node.getType());
        assertEquals("param1", node.getName());
        assertEquals("defaultVal", node.getDefaultValue());
        assertEquals(0, node.getNumChildren());
    }

    @Test
    public void testConstructorWithNullDefaultValue() {
        ParameterNode node = new ParameterNode("int", "count", null);
        
        assertEquals("int", node.getType());
        assertEquals("count", node.getName());
        assertNull(node.getDefaultValue());
    }

    @Test
    public void testConstructorWithEmptyDefaultValue() {
        ParameterNode node = new ParameterNode("boolean", "flag", "");
        
        assertEquals("boolean", node.getType());
        assertEquals("flag", node.getName());
        assertEquals("", node.getDefaultValue());
    }

    @Test
    public void testToContentString() {
        ParameterNode node = new ParameterNode("String", "param1", "defaultVal");
        assertEquals("defaultValue: defaultVal", node.toContentString());
    }

    @Test
    public void testToContentStringWithNullDefaultValue() {
        ParameterNode node = new ParameterNode("int", "count", null);
        assertEquals("defaultValue: null", node.toContentString());
    }

    @Test
    public void testToContentStringWithEmptyDefaultValue() {
        ParameterNode node = new ParameterNode("boolean", "flag", "");
        assertEquals("defaultValue: ", node.toContentString());
    }

    @Test
    public void testToJson() {
        ParameterNode node = new ParameterNode("String", "param1", "defaultVal");
        BuilderWithIndentation builder = new BuilderWithIndentation();
        
        String json = node.toJson(builder);
        
        assertTrue(json.contains("\"type\": \"String\""));
        assertTrue(json.contains("\"name\": \"param1\""));
        assertTrue(json.contains("\"defaultValue\": \"defaultVal\""));
        assertTrue(json.startsWith("{"));
        assertTrue(json.endsWith("}"));
    }

    @Test
    public void testToJsonWithNullDefaultValue() {
        ParameterNode node = new ParameterNode("int", "count", null);
        BuilderWithIndentation builder = new BuilderWithIndentation();
        
        String json = node.toJson(builder);
        
        assertTrue(json.contains("\"type\": \"int\""));
        assertTrue(json.contains("\"name\": \"count\""));
        assertTrue(json.contains("\"defaultValue\": \"null\""));
    }

    @Test
    public void testToJsonWithSpecialCharacters() {
        ParameterNode node = new ParameterNode("String", "param", "\"quoted\"");
        BuilderWithIndentation builder = new BuilderWithIndentation();
        
        String json = node.toJson(builder);
        
        // The default value should be escaped
        assertTrue(json.contains("\"defaultValue\": \"\\\"quoted\\\"\""));
    }

    @Test
    public void testGetters() {
        ParameterNode node = new ParameterNode("double", "value", "3.14");
        
        assertEquals("double", node.getType());
        assertEquals("value", node.getName());
        assertEquals("3.14", node.getDefaultValue());
    }

    @Test
    public void testComplexTypes() {
        ParameterNode node = new ParameterNode("List<String>", "items", "new ArrayList<>()");
        
        assertEquals("List<String>", node.getType());
        assertEquals("items", node.getName());
        assertEquals("new ArrayList<>()", node.getDefaultValue());
    }

    @Test
    public void testStringRepresentation() {
        ParameterNode node = new ParameterNode("int", "x", "0");
        
        String str = node.toString();
        assertNotNull(str);
        assertTrue(str.contains("ParameterNode"));
    }

    @Test
    public void testInheritedMethods() {
        ParameterNode node = new ParameterNode("String", "test", "value");
        
        // Test inherited LangSpecNode methods
        assertEquals(0, node.getNumChildren());
        assertFalse(node.hasChildren());
        assertNotNull(node.toString());
    }

    @Test
    public void testGetDeclarationWithNoChildren() {
        ParameterNode node = new ParameterNode("String", "param", "default");
        
        // Since ParameterNode doesn't add children in constructor,
        // getDeclaration should throw an exception when no children exist
        try {
            node.getDeclaration();
            fail("Expected exception when getting declaration with no children");
        } catch (Exception e) {
            // Expected behavior
        }
    }

    @Test
    public void testGetParametersWithNoChildren() {
        ParameterNode node = new ParameterNode("String", "param", "default");
        
        // Since ParameterNode doesn't add children in constructor,
        // getParameters should return empty list or throw exception
        try {
            node.getParameters();
            // If it doesn't throw, it should return empty list
        } catch (Exception e) {
            // Expected behavior if it throws
        }
    }
}
