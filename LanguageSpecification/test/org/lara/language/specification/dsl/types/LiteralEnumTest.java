package org.lara.language.specification.dsl.types;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LiteralEnumTest {

    private LiteralEnum literalEnum;

    @BeforeEach
    void setUp() {
        literalEnum = new LiteralEnum("TestEnum");
    }

    @Test
    void testConstructorWithNameOnly() {
        LiteralEnum enumObj = new LiteralEnum("TestEnum");
        
        assertEquals("TestEnum", enumObj.getName());
        assertNotNull(enumObj.getValues());
        assertTrue(enumObj.getValues().isEmpty());
    }

    @Test
    void testConstructorWithNameAndStringValues() {
        LiteralEnum enumObj = new LiteralEnum("TestEnum", "{value1, value2, value3}");
        
        assertEquals("TestEnum", enumObj.getName());
        assertEquals(3, enumObj.getValues().size());
        assertEquals("value1", enumObj.getValues().get(0));
        assertEquals("value2", enumObj.getValues().get(1));
        assertEquals("value3", enumObj.getValues().get(2));
    }

    @Test
    void testConstructorWithNameAndStringValuesNoBraces() {
        LiteralEnum enumObj = new LiteralEnum("TestEnum", "value1, value2, value3");
        
        assertEquals("TestEnum", enumObj.getName());
        assertEquals(3, enumObj.getValues().size());
        assertEquals("value1", enumObj.getValues().get(0));
        assertEquals("value2", enumObj.getValues().get(1));
        assertEquals("value3", enumObj.getValues().get(2));
    }

    @Test
    void testConstructorWithNameAndStringValuesWithSpaces() {
        LiteralEnum enumObj = new LiteralEnum("TestEnum", "{ value1 , value2 , value3 }");
        
        assertEquals("TestEnum", enumObj.getName());
        assertEquals(3, enumObj.getValues().size());
        assertEquals("value1", enumObj.getValues().get(0));
        assertEquals("value2", enumObj.getValues().get(1));
        assertEquals("value3", enumObj.getValues().get(2));
    }

    @Test
    void testConstructorWithNameAndList() {
        List<String> values = Arrays.asList("value1", "value2", "value3");
        LiteralEnum enumObj = new LiteralEnum("TestEnum", values);
        
        assertEquals("TestEnum", enumObj.getName());
        assertEquals(3, enumObj.getValues().size());
        assertEquals("value1", enumObj.getValues().get(0));
        assertEquals("value2", enumObj.getValues().get(1));
        assertEquals("value3", enumObj.getValues().get(2));
    }

    @Test
    void testConstructorWithNullName() {
        LiteralEnum enumObj = new LiteralEnum(null);
        
        assertNull(enumObj.getName());
        assertNotNull(enumObj.getValues());
        assertTrue(enumObj.getValues().isEmpty());
    }

    @Test
    void testConstructorWithNullValues() {
        List<String> nullValues = null;
        LiteralEnum enumObj = new LiteralEnum("TestEnum", nullValues);
        
        assertEquals("TestEnum", enumObj.getName());
        assertNull(enumObj.getValues());
    }

    @Test
    void testOfStaticMethod() {
        LiteralEnum enumObj = LiteralEnum.of("TestEnum", "value1", "value2", "value3");
        
        assertEquals("TestEnum", enumObj.getName());
        assertEquals(3, enumObj.getValues().size());
        assertEquals("value1", enumObj.getValues().get(0));
        assertEquals("value2", enumObj.getValues().get(1));
        assertEquals("value3", enumObj.getValues().get(2));
    }

    @Test
    void testOfStaticMethodWithNoValues() {
        LiteralEnum enumObj = LiteralEnum.of("TestEnum");
        
        assertEquals("TestEnum", enumObj.getName());
        assertTrue(enumObj.getValues().isEmpty());
    }

    @Test
    void testOfStaticMethodWithNullName() {
        LiteralEnum enumObj = LiteralEnum.of(null, "value1", "value2");
        
        assertNull(enumObj.getName());
        assertEquals(2, enumObj.getValues().size());
    }

    @Test
    void testGetName() {
        assertEquals("TestEnum", literalEnum.getName());
    }

    @Test
    void testSetName() {
        literalEnum.setName("NewName");
        
        assertEquals("NewName", literalEnum.getName());
    }

    @Test
    void testSetNameToNull() {
        literalEnum.setName(null);
        
        assertNull(literalEnum.getName());
    }

    @Test
    void testAddValue() {
        literalEnum.addValue("newValue");
        
        assertEquals(1, literalEnum.getValues().size());
        assertEquals("newValue", literalEnum.getValues().get(0));
    }

    @Test
    void testAddMultipleValues() {
        literalEnum.addValue("value1");
        literalEnum.addValue("value2");
        literalEnum.addValue("value3");
        
        assertEquals(3, literalEnum.getValues().size());
        assertEquals("value1", literalEnum.getValues().get(0));
        assertEquals("value2", literalEnum.getValues().get(1));
        assertEquals("value3", literalEnum.getValues().get(2));
    }

    @Test
    void testAddNullValue() {
        literalEnum.addValue(null);
        
        assertEquals(1, literalEnum.getValues().size());
        assertNull(literalEnum.getValues().get(0));
    }

    @Test
    void testGetValues() {
        List<String> values = Arrays.asList("value1", "value2");
        literalEnum.setValues(values);
        
        assertEquals(2, literalEnum.getValues().size());
        assertEquals("value1", literalEnum.getValues().get(0));
        assertEquals("value2", literalEnum.getValues().get(1));
    }

    @Test
    void testSetValues() {
        List<String> values = Arrays.asList("newValue1", "newValue2");
        literalEnum.setValues(values);
        
        assertEquals(2, literalEnum.getValues().size());
        assertEquals("newValue1", literalEnum.getValues().get(0));
        assertEquals("newValue2", literalEnum.getValues().get(1));
    }

    @Test
    void testSetValuesToNull() {
        literalEnum.setValues(null);
        
        assertNull(literalEnum.getValues());
    }

    @Test
    void testSetValuesToEmptyList() {
        literalEnum.setValues(new ArrayList<>());
        
        assertTrue(literalEnum.getValues().isEmpty());
    }

    @Test
    void testGetType() {
        literalEnum.addValue("value1");
        literalEnum.addValue("value2");
        
        String type = literalEnum.getType();
        assertEquals(literalEnum.toString(), type);
    }

    @Test
    void testToString() {
        literalEnum.addValue("value1");
        literalEnum.addValue("value2");
        literalEnum.addValue("value3");
        
        String result = literalEnum.toString();
        assertEquals("[value1| value2| value3]", result);
    }

    @Test
    void testToStringWithEmptyValues() {
        String result = literalEnum.toString();
        assertEquals("[]", result);
    }

    @Test
    void testToStringWithSingleValue() {
        literalEnum.addValue("singleValue");
        
        String result = literalEnum.toString();
        assertEquals("[singleValue]", result);
    }

    @Test
    void testToStringWithNullValues() {
        literalEnum.setValues(null);
        
        assertThrows(NullPointerException.class, () -> {
            literalEnum.toString();
        });
    }

    @Test
    void testITypeInterface() {
        assertTrue(literalEnum instanceof IType);
        assertFalse(literalEnum.isArray()); // Default implementation from IType interface
        assertNotNull(literalEnum.getType());
    }

    @Test
    void testParseValuesMethod() {
        LiteralEnum enumObj = new LiteralEnum("TestEnum", "{value1,value2,value3}");
        
        assertEquals(3, enumObj.getValues().size());
        assertEquals("value1", enumObj.getValues().get(0));
        assertEquals("value2", enumObj.getValues().get(1));
        assertEquals("value3", enumObj.getValues().get(2));
    }

    @Test
    void testParseValuesWithMixedSpaces() {
        LiteralEnum enumObj = new LiteralEnum("TestEnum", "{ value1, value2 ,value3}");
        
        assertEquals(3, enumObj.getValues().size());
        assertEquals("value1", enumObj.getValues().get(0));
        assertEquals("value2", enumObj.getValues().get(1));
        assertEquals("value3", enumObj.getValues().get(2));
    }

    @Test
    void testParseEmptyString() {
        LiteralEnum enumObj = new LiteralEnum("TestEnum", "{}");
        
        assertTrue(enumObj.getValues().isEmpty() || 
                  (enumObj.getValues().size() == 1 && enumObj.getValues().get(0).isEmpty()));
    }

    @Test
    void testValueModification() {
        List<String> values = new ArrayList<>();
        values.add("original");
        literalEnum.setValues(values);
        
        // Modify the original list
        values.add("modified");
        
        // Check if the literalEnum's values are affected
        assertEquals(2, literalEnum.getValues().size());
    }

    @Test
    void testSpecialCharactersInValues() {
        literalEnum.addValue("value_with_underscore");
        literalEnum.addValue("value-with-dash");
        literalEnum.addValue("value with spaces");
        
        assertEquals(3, literalEnum.getValues().size());
        String result = literalEnum.toString();
        assertTrue(result.contains("value_with_underscore"));
        assertTrue(result.contains("value-with-dash"));
        assertTrue(result.contains("value with spaces"));
    }
}
