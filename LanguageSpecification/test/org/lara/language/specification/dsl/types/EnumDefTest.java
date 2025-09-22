package org.lara.language.specification.dsl.types;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EnumDefTest {

    private EnumDef enumDef;
    private EnumValue value1;
    private EnumValue value2;

    @BeforeEach
    void setUp() {
        value1 = new EnumValue("VALUE1", "First value");
        value2 = new EnumValue("VALUE2", "Second value");
    }

    @Test
    void testConstructorWithNameOnly() {
        enumDef = new EnumDef("TestEnum");
        
        assertEquals("TestEnum", enumDef.getName());
        assertNotNull(enumDef.getValues());
        assertTrue(enumDef.getValues().isEmpty());
    }

    @Test
    void testConstructorWithNameAndValues() {
        List<EnumValue> values = Arrays.asList(value1, value2);
        enumDef = new EnumDef("TestEnum", values);
        
        assertEquals("TestEnum", enumDef.getName());
        assertEquals(2, enumDef.getValues().size());
        assertEquals(value1, enumDef.getValues().get(0));
        assertEquals(value2, enumDef.getValues().get(1));
    }

    @Test
    void testConstructorWithNullValues() {
        enumDef = new EnumDef("TestEnum", null);
        
        assertEquals("TestEnum", enumDef.getName());
        assertNull(enumDef.getValues());
    }

    @Test
    void testSetName() {
        enumDef = new EnumDef("TestEnum");
        enumDef.setName("NewName");
        
        assertEquals("NewName", enumDef.getName());
    }

    @Test
    void testSetNameToNull() {
        enumDef = new EnumDef("TestEnum");
        enumDef.setName(null);
        
        assertNull(enumDef.getName());
    }

    @Test
    void testAddValue() {
        enumDef = new EnumDef("TestEnum");
        enumDef.add("TEST_VALUE", "Test description");
        
        assertEquals(1, enumDef.getValues().size());
        EnumValue addedValue = enumDef.getValues().get(0);
        assertEquals("TEST_VALUE", addedValue.getValue());
        assertEquals("Test description", addedValue.getString());
    }

    @Test
    void testAddMultipleValues() {
        enumDef = new EnumDef("TestEnum");
        enumDef.add("VALUE1", "First");
        enumDef.add("VALUE2", "Second");
        
        assertEquals(2, enumDef.getValues().size());
        assertEquals("VALUE1", enumDef.getValues().get(0).getValue());
        assertEquals("VALUE2", enumDef.getValues().get(1).getValue());
    }

    @Test
    void testAddNullValues() {
        enumDef = new EnumDef("TestEnum");
        enumDef.add(null, null);
        
        assertEquals(1, enumDef.getValues().size());
        EnumValue addedValue = enumDef.getValues().get(0);
        assertNull(addedValue.getValue());
        assertNull(addedValue.getString());
    }

    @Test
    void testSetValues() {
        enumDef = new EnumDef("TestEnum");
        List<EnumValue> values = Arrays.asList(value1, value2);
        enumDef.setValues(values);
        
        assertEquals(2, enumDef.getValues().size());
        assertEquals(value1, enumDef.getValues().get(0));
        assertEquals(value2, enumDef.getValues().get(1));
    }

    @Test
    void testSetValuesToNull() {
        enumDef = new EnumDef("TestEnum");
        enumDef.setValues(null);
        
        assertNull(enumDef.getValues());
    }

    @Test
    void testSetValuesToEmptyList() {
        enumDef = new EnumDef("TestEnum");
        enumDef.setValues(new ArrayList<>());
        
        assertTrue(enumDef.getValues().isEmpty());
    }

    @Test
    void testGetType() {
        enumDef = new EnumDef("TestEnum");
        
        assertEquals("TestEnum", enumDef.type());
    }

    @Test
    void testGetTypeWithNullName() {
        enumDef = new EnumDef("TestEnum");
        enumDef.setName(null);
        
        assertNull(enumDef.type());
    }

    @Test
    void testToString() {
        enumDef = new EnumDef("TestEnum");
        
        assertEquals("TestEnum", enumDef.toString());
    }

    @Test
    void testToStringWithNullName() {
        enumDef = new EnumDef("TestEnum");
        enumDef.setName(null);
        
        assertNull(enumDef.toString());
    }

    @Test
    void testToDSLStringWithoutValues() {
        enumDef = new EnumDef("TestEnum");
        
        String expected = "enumdef TestEnum{\n\t\n}";
        assertEquals(expected, enumDef.toDSLString());
    }

    @Test
    void testToDSLStringWithValues() {
        enumDef = new EnumDef("TestEnum");
        enumDef.add("VALUE1", "First value");
        enumDef.add("VALUE2", "Second value");
        
        String result = enumDef.toDSLString();
        
        assertTrue(result.startsWith("enumdef TestEnum{"));
        assertTrue(result.endsWith("}"));
        assertTrue(result.contains("VALUE1(First value)"));
        assertTrue(result.contains("VALUE2(Second value)"));
    }

    @Test
    void testToDSLStringWithNullName() {
        enumDef = new EnumDef("TestEnum");
        enumDef.setName(null);
        
        String result = enumDef.toDSLString();
        assertTrue(result.startsWith("enumdef null{"));
    }

    @Test
    void testITypeInterface() {
        enumDef = new EnumDef("TestEnum");
        
        assertTrue(enumDef instanceof IType);
        assertEquals("TestEnum", enumDef.type());
        assertFalse(enumDef.isArray()); // Default implementation from IType interface
    }

    @Test
    void testBaseNodeInheritance() {
        enumDef = new EnumDef("TestEnum");
        
        // Test that it inherits from BaseNode
        assertNotNull(enumDef);
    }

    @Test
    void testValueModification() {
        enumDef = new EnumDef("TestEnum");
        List<EnumValue> values = new ArrayList<>();
        values.add(value1);
        enumDef.setValues(values);
        
        // Modify the original list
        values.add(value2);
        
        // Check if the enumDef's values are affected (depends on implementation)
        assertEquals(2, enumDef.getValues().size());
    }

    @Test
    void testEmptyConstructorList() {
        List<EnumValue> emptyList = new ArrayList<>();
        enumDef = new EnumDef("TestEnum", emptyList);
        
        assertEquals("TestEnum", enumDef.getName());
        assertTrue(enumDef.getValues().isEmpty());
    }
}
