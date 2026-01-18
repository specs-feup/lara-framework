package org.lara.language.specification.dsl.types;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EnumValueTest {

    private EnumValue enumValue;

    @BeforeEach
    void setUp() {
        enumValue = new EnumValue("TEST_VALUE", "Test description");
    }

    @Test
    void testConstructorWithParameters() {
        EnumValue value = new EnumValue("VALUE1", "Description 1");
        
        assertEquals("VALUE1", value.getValue());
        assertEquals("Description 1", value.getString());
    }

    @Test
    void testConstructorWithNullParameters() {
        EnumValue value = new EnumValue(null, null);
        
        assertNull(value.getValue());
        assertNull(value.getString());
    }

    @Test
    void testDefaultConstructor() {
        EnumValue value = new EnumValue();
        
        assertNull(value.getValue());
        assertNull(value.getString());
    }

    @Test
    void testGetValue() {
        assertEquals("TEST_VALUE", enumValue.getValue());
    }

    @Test
    void testSetValue() {
        enumValue.setValue("NEW_VALUE");
        
        assertEquals("NEW_VALUE", enumValue.getValue());
    }

    @Test
    void testSetValueToNull() {
        enumValue.setValue(null);
        
        assertNull(enumValue.getValue());
    }

    @Test
    void testGetString() {
        assertEquals("Test description", enumValue.getString());
    }

    @Test
    void testSetString() {
        enumValue.setString("New description");
        
        assertEquals("New description", enumValue.getString());
    }

    @Test
    void testSetStringToNull() {
        enumValue.setString(null);
        
        assertNull(enumValue.getString());
    }

    @Test
    void testToString() {
        String expected = "TEST_VALUE(Test description)";
        assertEquals(expected, enumValue.toString());
    }

    @Test
    void testToStringWithNullValue() {
        enumValue.setValue(null);
        
        String result = enumValue.toString();
        assertEquals("null(Test description)", result);
    }

    @Test
    void testToStringWithNullString() {
        enumValue.setString(null);
        
        String result = enumValue.toString();
        assertEquals("TEST_VALUE(null)", result);
    }

    @Test
    void testToStringWithBothNull() {
        enumValue.setValue(null);
        enumValue.setString(null);
        
        String result = enumValue.toString();
        assertEquals("null(null)", result);
    }

    @Test
    void testCompareTo() {
        EnumValue value1 = new EnumValue("ALPHA", "First");
        EnumValue value2 = new EnumValue("BETA", "Second");
        EnumValue value3 = new EnumValue("ALPHA", "Different description");
        
        assertTrue(value1.compareTo(value2) < 0);
        assertTrue(value2.compareTo(value1) > 0);
        assertEquals(0, value1.compareTo(value3));
    }

    @Test
    void testCompareToWithNullValue() {
        EnumValue valueWithNull = new EnumValue(null, "Description");
        EnumValue valueWithString = new EnumValue("VALUE", "Description");
        
        // This will depend on how null is compared - let's test the actual behavior
        assertThrows(NullPointerException.class, () -> {
            valueWithNull.compareTo(valueWithString);
        });
    }

    @Test
    void testCompareToWithBothNullValues() {
        EnumValue value1 = new EnumValue(null, "Desc1");
        EnumValue value2 = new EnumValue(null, "Desc2");
        
        assertThrows(NullPointerException.class, () -> {
            value1.compareTo(value2);
        });
    }

    @Test
    void testCompareToSelf() {
        assertEquals(0, enumValue.compareTo(enumValue));
    }

    @Test
    void testCompareToIdenticalValues() {
        EnumValue other = new EnumValue("TEST_VALUE", "Different description");
        
        assertEquals(0, enumValue.compareTo(other));
    }

    @Test
    void testComparableInterface() {
        assertTrue(enumValue instanceof Comparable);
    }

    @Test
    void testSorting() {
        EnumValue value1 = new EnumValue("CHARLIE", "Third");
        EnumValue value2 = new EnumValue("ALPHA", "First");
        EnumValue value3 = new EnumValue("BETA", "Second");
        
        List<EnumValue> values = Arrays.asList(value1, value2, value3);
        values.sort(null); // Uses natural ordering (compareTo)
        
        assertEquals("ALPHA", values.get(0).getValue());
        assertEquals("BETA", values.get(1).getValue());
        assertEquals("CHARLIE", values.get(2).getValue());
    }

    @Test
    void testEmptyStringValues() {
        EnumValue value = new EnumValue("", "");
        
        assertEquals("", value.getValue());
        assertEquals("", value.getString());
        assertEquals("()", value.toString());
    }

    @Test
    void testSpecialCharacters() {
        EnumValue value = new EnumValue("VALUE_WITH_UNDERSCORE", "Description with spaces & symbols!");
        
        assertEquals("VALUE_WITH_UNDERSCORE", value.getValue());
        assertEquals("Description with spaces & symbols!", value.getString());
        assertEquals("VALUE_WITH_UNDERSCORE(Description with spaces & symbols!)", value.toString());
    }

    @Test
    void testModifyAfterCreation() {
        EnumValue value = new EnumValue("ORIGINAL", "Original description");
        
        value.setValue("MODIFIED");
        value.setString("Modified description");
        
        assertEquals("MODIFIED", value.getValue());
        assertEquals("Modified description", value.getString());
        assertEquals("MODIFIED(Modified description)", value.toString());
    }
}
