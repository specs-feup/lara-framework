package org.lara.language.specification.dsl.types;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PrimitiveClassesTest {

    @Test
    void testAllPrimitiveClassesValues() {
        PrimitiveClasses[] expectedValues = {
            PrimitiveClasses.VOID, PrimitiveClasses.BYTE, PrimitiveClasses.SHORT, 
            PrimitiveClasses.INTEGER, PrimitiveClasses.LONG, PrimitiveClasses.FLOAT, 
            PrimitiveClasses.DOUBLE, PrimitiveClasses.BOOLEAN, PrimitiveClasses.CHAR,
            PrimitiveClasses.STRING, PrimitiveClasses.OBJECT, PrimitiveClasses.MAP,
            PrimitiveClasses.JOINPOINT_INTERFACE
        };
        
        assertArrayEquals(expectedValues, PrimitiveClasses.values());
    }

    @Test
    void testGetExistingPrimitiveClassesByName() {
        assertEquals(PrimitiveClasses.VOID, PrimitiveClasses.get("VOID"));
        assertEquals(PrimitiveClasses.BYTE, PrimitiveClasses.get("BYTE"));
        assertEquals(PrimitiveClasses.SHORT, PrimitiveClasses.get("SHORT"));
        assertEquals(PrimitiveClasses.INTEGER, PrimitiveClasses.get("INTEGER"));
        assertEquals(PrimitiveClasses.LONG, PrimitiveClasses.get("LONG"));
        assertEquals(PrimitiveClasses.FLOAT, PrimitiveClasses.get("FLOAT"));
        assertEquals(PrimitiveClasses.DOUBLE, PrimitiveClasses.get("DOUBLE"));
        assertEquals(PrimitiveClasses.BOOLEAN, PrimitiveClasses.get("BOOLEAN"));
        assertEquals(PrimitiveClasses.CHAR, PrimitiveClasses.get("CHAR"));
        assertEquals(PrimitiveClasses.STRING, PrimitiveClasses.get("STRING"));
        assertEquals(PrimitiveClasses.OBJECT, PrimitiveClasses.get("OBJECT"));
        assertEquals(PrimitiveClasses.MAP, PrimitiveClasses.get("MAP"));
        assertEquals(PrimitiveClasses.JOINPOINT_INTERFACE, PrimitiveClasses.get("JOINPOINT_INTERFACE"));
    }

    @Test
    void testGetCaseInsensitive() {
        assertEquals(PrimitiveClasses.INTEGER, PrimitiveClasses.get("integer"));
        assertEquals(PrimitiveClasses.INTEGER, PrimitiveClasses.get("Integer"));
        assertEquals(PrimitiveClasses.STRING, PrimitiveClasses.get("string"));
        assertEquals(PrimitiveClasses.BOOLEAN, PrimitiveClasses.get("boolean"));
    }

    @Test
    void testGetNonExistentPrimitiveClass() {
        assertThrows(RuntimeException.class, () -> PrimitiveClasses.get("NONEXISTENT"));
        assertThrows(RuntimeException.class, () -> PrimitiveClasses.get("invalid"));
    }

    @Test
    void testGetWithNullName() {
        assertThrows(NullPointerException.class, () -> PrimitiveClasses.get(null));
    }

    @Test
    void testGetWithEmptyName() {
        assertThrows(RuntimeException.class, () -> PrimitiveClasses.get(""));
    }

    @Test
    void testContainsExistingPrimitiveClasses() {
        assertTrue(PrimitiveClasses.contains("VOID"));
        assertTrue(PrimitiveClasses.contains("BYTE"));
        assertTrue(PrimitiveClasses.contains("SHORT"));
        assertTrue(PrimitiveClasses.contains("INTEGER"));
        assertTrue(PrimitiveClasses.contains("LONG"));
        assertTrue(PrimitiveClasses.contains("FLOAT"));
        assertTrue(PrimitiveClasses.contains("DOUBLE"));
        assertTrue(PrimitiveClasses.contains("BOOLEAN"));
        assertTrue(PrimitiveClasses.contains("CHAR"));
        assertTrue(PrimitiveClasses.contains("STRING"));
        assertTrue(PrimitiveClasses.contains("OBJECT"));
        assertTrue(PrimitiveClasses.contains("MAP"));
        assertTrue(PrimitiveClasses.contains("JOINPOINT_INTERFACE"));
    }

    @Test
    void testContainsCaseInsensitive() {
        assertTrue(PrimitiveClasses.contains("integer"));
        assertTrue(PrimitiveClasses.contains("Integer"));
        assertTrue(PrimitiveClasses.contains("string"));
        assertTrue(PrimitiveClasses.contains("Boolean"));
    }

    @Test
    void testContainsNonExistentPrimitiveClass() {
        assertFalse(PrimitiveClasses.contains("NONEXISTENT"));
        assertFalse(PrimitiveClasses.contains("invalid"));
    }

    @Test
    void testContainsWithNullName() {
        assertThrows(NullPointerException.class, () -> PrimitiveClasses.contains(null));
    }

    @Test
    void testContainsWithEmptyName() {
        assertFalse(PrimitiveClasses.contains(""));
    }

    @Test
    void testGetType() {
        assertEquals("Void", PrimitiveClasses.VOID.type());
        assertEquals("Byte", PrimitiveClasses.BYTE.type());
        assertEquals("Short", PrimitiveClasses.SHORT.type());
        assertEquals("Integer", PrimitiveClasses.INTEGER.type());
        assertEquals("Long", PrimitiveClasses.LONG.type());
        assertEquals("Float", PrimitiveClasses.FLOAT.type());
        assertEquals("Double", PrimitiveClasses.DOUBLE.type());
        assertEquals("Boolean", PrimitiveClasses.BOOLEAN.type());
        assertEquals("Char", PrimitiveClasses.CHAR.type());
        assertEquals("String", PrimitiveClasses.STRING.type());
        assertEquals("Object", PrimitiveClasses.OBJECT.type());
        assertEquals("Map", PrimitiveClasses.MAP.type());
        assertEquals("JoinpointInterface", PrimitiveClasses.JOINPOINT_INTERFACE.type());
    }

    @Test
    void testToString() {
        assertEquals("Void", PrimitiveClasses.VOID.toString());
        assertEquals("Byte", PrimitiveClasses.BYTE.toString());
        assertEquals("Short", PrimitiveClasses.SHORT.toString());
        assertEquals("Integer", PrimitiveClasses.INTEGER.toString());
        assertEquals("Long", PrimitiveClasses.LONG.toString());
        assertEquals("Float", PrimitiveClasses.FLOAT.toString());
        assertEquals("Double", PrimitiveClasses.DOUBLE.toString());
        assertEquals("Boolean", PrimitiveClasses.BOOLEAN.toString());
        assertEquals("Char", PrimitiveClasses.CHAR.toString());
        assertEquals("String", PrimitiveClasses.STRING.toString());
        assertEquals("Object", PrimitiveClasses.OBJECT.toString());
        assertEquals("Map", PrimitiveClasses.MAP.toString());
        assertEquals("JoinpointInterface", PrimitiveClasses.JOINPOINT_INTERFACE.toString());
    }

    @Test
    void testGetTypeEqualsToString() {
        for (PrimitiveClasses primitiveClass : PrimitiveClasses.values()) {
            assertEquals(primitiveClass.type(), primitiveClass.toString());
        }
    }

    @Test
    void testITypeInterface() {
        for (PrimitiveClasses primitiveClass : PrimitiveClasses.values()) {
            assertTrue(primitiveClass instanceof IType);
            assertFalse(primitiveClass.isArray()); // Default implementation from IType interface
            assertNotNull(primitiveClass.type());
        }
    }

    @Test
    void testSpecialJoinpointInterfaceCase() {
        // Test the special case for JOINPOINT_INTERFACE
        assertEquals("JoinpointInterface", PrimitiveClasses.JOINPOINT_INTERFACE.type());
        assertEquals("JoinpointInterface", PrimitiveClasses.JOINPOINT_INTERFACE.toString());
    }

    @Test
    void testEnumComparability() {
        // Test that enum values can be compared
        assertTrue(PrimitiveClasses.VOID.ordinal() < PrimitiveClasses.JOINPOINT_INTERFACE.ordinal());
        assertEquals(PrimitiveClasses.INTEGER, PrimitiveClasses.valueOf("INTEGER"));
    }

    @Test
    void testAllPrimitiveClassesHaveValidTypes() {
        for (PrimitiveClasses primitiveClass : PrimitiveClasses.values()) {
            String type = primitiveClass.type();
            assertNotNull(type);
            assertFalse(type.isEmpty());
            // Type should be capitalized version of name (except for special cases)
        }
    }

    @Test
    void testGetAndContainsConsistency() {
        // For every primitive class that contains() returns true, get() should work
        for (PrimitiveClasses primitiveClass : PrimitiveClasses.values()) {
            String typeName = primitiveClass.name();
            assertTrue(PrimitiveClasses.contains(typeName));
            assertEquals(primitiveClass, PrimitiveClasses.get(typeName));
        }
    }

    @Test
    void testGetAndContainsWithWhitespace() {
        assertFalse(PrimitiveClasses.contains(" STRING "));
        assertThrows(RuntimeException.class, () -> PrimitiveClasses.get(" STRING "));
    }

    @Test
    void testPrimitiveClassesIsEnum() {
        assertTrue(PrimitiveClasses.INTEGER instanceof Enum);
        assertEquals(PrimitiveClasses.class, PrimitiveClasses.INTEGER.getDeclaringClass());
    }

    @Test
    void testEnumValues() {
        PrimitiveClasses[] values = PrimitiveClasses.values();
        assertEquals(13, values.length); // Verify we have exactly 13 primitive classes
        
        List<PrimitiveClasses> valuesList = Arrays.asList(values);
        assertTrue(valuesList.contains(PrimitiveClasses.VOID));
        assertTrue(valuesList.contains(PrimitiveClasses.BYTE));
        assertTrue(valuesList.contains(PrimitiveClasses.SHORT));
        assertTrue(valuesList.contains(PrimitiveClasses.INTEGER));
        assertTrue(valuesList.contains(PrimitiveClasses.LONG));
        assertTrue(valuesList.contains(PrimitiveClasses.FLOAT));
        assertTrue(valuesList.contains(PrimitiveClasses.DOUBLE));
        assertTrue(valuesList.contains(PrimitiveClasses.BOOLEAN));
        assertTrue(valuesList.contains(PrimitiveClasses.CHAR));
        assertTrue(valuesList.contains(PrimitiveClasses.STRING));
        assertTrue(valuesList.contains(PrimitiveClasses.OBJECT));
        assertTrue(valuesList.contains(PrimitiveClasses.MAP));
        assertTrue(valuesList.contains(PrimitiveClasses.JOINPOINT_INTERFACE));
    }

    @Test
    void testDifferenceFromPrimitive() {
        // PrimitiveClasses has INTEGER instead of INT
        assertTrue(PrimitiveClasses.contains("INTEGER"));
        assertFalse(PrimitiveClasses.contains("INT"));
        
        // PrimitiveClasses has additional types not in Primitive
        assertTrue(PrimitiveClasses.contains("STRING"));
        assertTrue(PrimitiveClasses.contains("OBJECT"));
        assertTrue(PrimitiveClasses.contains("MAP"));
        assertTrue(PrimitiveClasses.contains("JOINPOINT_INTERFACE"));
    }
}
