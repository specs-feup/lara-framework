package org.lara.language.specification.dsl.types;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PrimitiveTest {

    @Test
    void testAllPrimitiveValues() {
        Primitive[] expectedValues = {
            Primitive.VOID, Primitive.BYTE, Primitive.SHORT, Primitive.INT,
            Primitive.LONG, Primitive.FLOAT, Primitive.DOUBLE, 
            Primitive.BOOLEAN, Primitive.CHAR
        };
        
        assertArrayEquals(expectedValues, Primitive.values());
    }

    @Test
    void testGetExistingPrimitiveByName() {
        assertEquals(Primitive.INT, Primitive.get("int"));
        assertEquals(Primitive.DOUBLE, Primitive.get("double"));
        assertEquals(Primitive.BOOLEAN, Primitive.get("boolean"));
        assertEquals(Primitive.VOID, Primitive.get("void"));
        assertEquals(Primitive.BYTE, Primitive.get("byte"));
        assertEquals(Primitive.SHORT, Primitive.get("short"));
        assertEquals(Primitive.LONG, Primitive.get("long"));
        assertEquals(Primitive.FLOAT, Primitive.get("float"));
        assertEquals(Primitive.CHAR, Primitive.get("char"));
    }

    @Test
    void testGetCaseMatters() {
        // The implementation expects lowercase names exactly
        assertEquals(Primitive.INT, Primitive.get("int"));
        assertEquals(Primitive.DOUBLE, Primitive.get("double"));
        assertEquals(Primitive.BOOLEAN, Primitive.get("boolean"));
        
        // These should fail because they're not lowercase
        assertThrows(RuntimeException.class, () -> Primitive.get("INT"));
        assertThrows(RuntimeException.class, () -> Primitive.get("Int"));
        assertThrows(RuntimeException.class, () -> Primitive.get("DOUBLE"));
        assertThrows(RuntimeException.class, () -> Primitive.get("Boolean"));
    }

    @Test
    void testGetNonExistentPrimitive() {
        assertThrows(RuntimeException.class, () -> Primitive.get("string"));
        assertThrows(RuntimeException.class, () -> Primitive.get("object"));
        assertThrows(RuntimeException.class, () -> Primitive.get("invalid"));
    }

    @Test
    void testGetWithNullName() {
        assertThrows(RuntimeException.class, () -> Primitive.get(null));
    }

    @Test
    void testGetWithEmptyName() {
        assertThrows(RuntimeException.class, () -> Primitive.get(""));
    }

    @Test
    void testContainsExistingPrimitives() {
        assertTrue(Primitive.contains("int"));
        assertTrue(Primitive.contains("double"));
        assertTrue(Primitive.contains("boolean"));
        assertTrue(Primitive.contains("void"));
        assertTrue(Primitive.contains("byte"));
        assertTrue(Primitive.contains("short"));
        assertTrue(Primitive.contains("long"));
        assertTrue(Primitive.contains("float"));
        assertTrue(Primitive.contains("char"));
    }

    @Test
    void testContainsCaseMatters() {
        // The implementation expects lowercase names exactly
        assertTrue(Primitive.contains("int"));
        assertTrue(Primitive.contains("double"));
        assertTrue(Primitive.contains("boolean"));
        
        // These should return false because they're not lowercase
        assertFalse(Primitive.contains("INT"));
        assertFalse(Primitive.contains("Int"));
        assertFalse(Primitive.contains("DOUBLE"));
        assertFalse(Primitive.contains("Boolean"));
    }

    @Test
    void testContainsNonExistentPrimitive() {
        assertFalse(Primitive.contains("string"));
        assertFalse(Primitive.contains("object"));
        assertFalse(Primitive.contains("invalid"));
    }

    @Test
    void testContainsWithNullName() {
        assertFalse(Primitive.contains(null));
    }

    @Test
    void testContainsWithEmptyName() {
        assertFalse(Primitive.contains(""));
    }

    @Test
    void testGetType() {
        assertEquals("void", Primitive.VOID.type());
        assertEquals("byte", Primitive.BYTE.type());
        assertEquals("short", Primitive.SHORT.type());
        assertEquals("int", Primitive.INT.type());
        assertEquals("long", Primitive.LONG.type());
        assertEquals("float", Primitive.FLOAT.type());
        assertEquals("double", Primitive.DOUBLE.type());
        assertEquals("boolean", Primitive.BOOLEAN.type());
        assertEquals("char", Primitive.CHAR.type());
    }

    @Test
    void testToString() {
        assertEquals("void", Primitive.VOID.toString());
        assertEquals("byte", Primitive.BYTE.toString());
        assertEquals("short", Primitive.SHORT.toString());
        assertEquals("int", Primitive.INT.toString());
        assertEquals("long", Primitive.LONG.toString());
        assertEquals("float", Primitive.FLOAT.toString());
        assertEquals("double", Primitive.DOUBLE.toString());
        assertEquals("boolean", Primitive.BOOLEAN.toString());
        assertEquals("char", Primitive.CHAR.toString());
    }

    @Test
    void testGetTypeEqualsToString() {
        for (Primitive primitive : Primitive.values()) {
            assertEquals(primitive.type(), primitive.toString());
        }
    }

    @Test
    void testITypeInterface() {
        for (Primitive primitive : Primitive.values()) {
            assertTrue(primitive instanceof IType);
            assertFalse(primitive.isArray()); // Default implementation from IType interface
            assertNotNull(primitive.type());
        }
    }

    @Test
    void testEnumComparability() {
        // Test that enum values can be compared
        assertTrue(Primitive.VOID.ordinal() < Primitive.CHAR.ordinal());
        assertEquals(Primitive.INT, Primitive.valueOf("INT"));
    }

    @Test
    void testAllPrimitivesHaveValidTypes() {
        for (Primitive primitive : Primitive.values()) {
            String type = primitive.type();
            assertNotNull(type);
            assertFalse(type.isEmpty());
            assertEquals(primitive.name().toLowerCase(), type);
        }
    }

    @Test
    void testGetAndContainsConsistency() {
        // For every primitive that contains() returns true, get() should work
        for (Primitive primitive : Primitive.values()) {
            String typeName = primitive.type();
            assertTrue(Primitive.contains(typeName));
            assertEquals(primitive, Primitive.get(typeName));
        }
    }

    @Test
    void testGetAndContainsWithWhitespace() {
        assertFalse(Primitive.contains(" int "));
        assertThrows(RuntimeException.class, () -> Primitive.get(" int "));
    }

    @Test
    void testPrimitiveIsEnum() {
        assertTrue(Primitive.INT instanceof Enum);
        assertEquals(Primitive.class, Primitive.INT.getDeclaringClass());
    }

    @Test
    void testEnumValues() {
        Primitive[] values = Primitive.values();
        assertEquals(9, values.length); // Verify we have exactly 9 primitives
        
        List<Primitive> valuesList = Arrays.asList(values);
        assertTrue(valuesList.contains(Primitive.VOID));
        assertTrue(valuesList.contains(Primitive.BYTE));
        assertTrue(valuesList.contains(Primitive.SHORT));
        assertTrue(valuesList.contains(Primitive.INT));
        assertTrue(valuesList.contains(Primitive.LONG));
        assertTrue(valuesList.contains(Primitive.FLOAT));
        assertTrue(valuesList.contains(Primitive.DOUBLE));
        assertTrue(valuesList.contains(Primitive.BOOLEAN));
        assertTrue(valuesList.contains(Primitive.CHAR));
    }
}
