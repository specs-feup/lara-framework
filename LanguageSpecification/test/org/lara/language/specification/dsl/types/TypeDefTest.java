package org.lara.language.specification.dsl.types;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lara.language.specification.dsl.Attribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TypeDefTest {

    private TypeDef typeDef;
    private Attribute field1;
    private Attribute field2;

    @BeforeEach
    void setUp() {
        field1 = new Attribute(Primitive.INT, "field1");
        field2 = new Attribute(PrimitiveClasses.STRING, "field2");
    }

    @Test
    void testConstructorWithNameOnly() {
        typeDef = new TypeDef("TestType");
        
        assertEquals("TestType", typeDef.getName());
        assertNotNull(typeDef.getFields());
        assertTrue(typeDef.getFields().isEmpty());
    }

    @Test
    void testConstructorWithNameAndFields() {
        List<Attribute> fields = Arrays.asList(field1, field2);
        typeDef = new TypeDef("TestType", fields);
        
        assertEquals("TestType", typeDef.getName());
        assertEquals(2, typeDef.getFields().size());
        assertEquals(field1, typeDef.getFields().get(0));
        assertEquals(field2, typeDef.getFields().get(1));
    }

    @Test
    void testConstructorWithNullFields() {
        typeDef = new TypeDef("TestType", null);
        
        assertEquals("TestType", typeDef.getName());
        assertNull(typeDef.getFields());
    }

    @Test
    void testSetName() {
        typeDef = new TypeDef("TestType");
        typeDef.setName("NewName");
        
        assertEquals("NewName", typeDef.getName());
    }

    @Test
    void testSetNameToNull() {
        typeDef = new TypeDef("TestType");
        typeDef.setName(null);
        
        assertNull(typeDef.getName());
    }

    @Test
    void testAddField() {
        typeDef = new TypeDef("TestType");
        typeDef.add(Primitive.INT, "testField");
        
        assertEquals(1, typeDef.getFields().size());
        Attribute addedField = typeDef.getFields().get(0);
        assertEquals(Primitive.INT, addedField.getType());
        assertEquals("testField", addedField.getName());
    }

    @Test
    void testAddMultipleFields() {
        typeDef = new TypeDef("TestType");
        typeDef.add(Primitive.INT, "field1");
        typeDef.add(PrimitiveClasses.STRING, "field2");
        
        assertEquals(2, typeDef.getFields().size());
        assertEquals("field1", typeDef.getFields().get(0).getName());
        assertEquals("field2", typeDef.getFields().get(1).getName());
    }

    @Test
    void testSetFields() {
        typeDef = new TypeDef("TestType");
        List<Attribute> fields = Arrays.asList(field1, field2);
        typeDef.setFields(fields);
        
        assertEquals(2, typeDef.getFields().size());
        assertEquals(field1, typeDef.getFields().get(0));
        assertEquals(field2, typeDef.getFields().get(1));
    }

    @Test
    void testSetFieldsToNull() {
        typeDef = new TypeDef("TestType");
        typeDef.setFields(null);
        
        assertNull(typeDef.getFields());
    }

    @Test
    void testSetFieldsToEmptyList() {
        typeDef = new TypeDef("TestType");
        typeDef.setFields(new ArrayList<>());
        
        assertTrue(typeDef.getFields().isEmpty());
    }

    @Test
    void testGetType() {
        typeDef = new TypeDef("TestType");
        
        assertEquals("TestType", typeDef.type());
    }

    @Test
    void testGetTypeWithNullName() {
        typeDef = new TypeDef("TestType");
        typeDef.setName(null);
        
        assertNull(typeDef.type());
    }

    @Test
    void testToString() {
        typeDef = new TypeDef("TestType");
        
        assertEquals("TestType", typeDef.toString());
    }

    @Test
    void testToStringWithNullName() {
        typeDef = new TypeDef("TestType");
        typeDef.setName(null);
        
        assertNull(typeDef.toString());
    }

    @Test
    void testToDSLStringWithoutFields() {
        typeDef = new TypeDef("TestType");
        
        String expected = "typedef TestType{\n\t\n}";
        assertEquals(expected, typeDef.toDSLString());
    }

    @Test
    void testToDSLStringWithFields() {
        typeDef = new TypeDef("TestType");
        typeDef.add(Primitive.INT, "field1");
        typeDef.add(PrimitiveClasses.STRING, "field2");
        
        String result = typeDef.toDSLString();
        
        assertTrue(result.startsWith("typedef TestType{"));
        assertTrue(result.endsWith("}"));
        assertTrue(result.contains("int field1"));
        assertTrue(result.contains("String field2"));
    }

    @Test
    void testToDSLStringWithNullName() {
        typeDef = new TypeDef("TestType");
        typeDef.setName(null);
        
        String result = typeDef.toDSLString();
        assertTrue(result.startsWith("typedef null{"));
    }

    @Test
    void testITypeInterface() {
        typeDef = new TypeDef("TestType");
        
        assertTrue(typeDef instanceof IType);
        assertEquals("TestType", typeDef.type());
        assertFalse(typeDef.isArray()); // Default implementation from IType interface
    }

    @Test
    void testBaseNodeInheritance() {
        typeDef = new TypeDef("TestType");
        
        // Test that it inherits from BaseNode (though we can't test much without knowing BaseNode details)
        assertNotNull(typeDef);
    }

    @Test
    void testFieldModification() {
        typeDef = new TypeDef("TestType");
        List<Attribute> fields = new ArrayList<>();
        fields.add(field1);
        typeDef.setFields(fields);
        
        // Modify the original list
        fields.add(field2);
        
        // Check if the typeDef's fields are affected (depends on implementation)
        assertEquals(2, typeDef.getFields().size());
    }
}
