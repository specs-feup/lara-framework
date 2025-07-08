package org.lara.language.specification.dsl.types;

import org.junit.jupiter.api.Test;
import org.lara.language.specification.dsl.JoinPointClass;

import static org.junit.jupiter.api.Assertions.*;

class ITypeTest {

    @Test
    void testITypeImplementations() {
        // Test that all our type classes implement IType
        assertTrue(Primitive.INT instanceof IType);
        assertTrue(PrimitiveClasses.STRING instanceof IType);
        assertTrue(new JPType(new JoinPointClass("TestType")) instanceof IType);
        assertTrue(new ArrayType(Primitive.INT) instanceof IType);
        assertTrue(new GenericType("TestGeneric", false) instanceof IType);
        assertTrue(new TypeDef("TestTypeDef") instanceof IType);
        assertTrue(new EnumDef("TestEnumDef") instanceof IType);
        assertTrue(new LiteralEnum("TestLiteralEnum") instanceof IType);
    }

    @Test
    void testDefaultIsArrayMethod() {
        // Test the default implementation of isArray() returns false
        IType primitiveType = Primitive.INT;
        IType primitiveClassType = PrimitiveClasses.STRING;
        IType jpType = new JPType(new JoinPointClass("TestType"));
        IType typeDefType = new TypeDef("TestTypeDef");
        IType enumDefType = new EnumDef("TestEnumDef");
        IType literalEnumType = new LiteralEnum("TestLiteralEnum");
        
        assertFalse(primitiveType.isArray());
        assertFalse(primitiveClassType.isArray());
        assertFalse(jpType.isArray());
        assertFalse(typeDefType.isArray());
        assertFalse(enumDefType.isArray());
        assertFalse(literalEnumType.isArray());
    }

    @Test
    void testArrayTypeOverridesIsArray() {
        // ArrayType should override isArray() to return true
        IType arrayType = new ArrayType(Primitive.INT);
        assertTrue(arrayType.isArray());
        
        IType nestedArrayType = new ArrayType(new ArrayType(Primitive.INT));
        assertTrue(nestedArrayType.isArray());
    }

    @Test
    void testGenericTypeOverridesIsArray() {
        // GenericType can override isArray() based on constructor parameter
        IType nonArrayGenericType = new GenericType("TestGeneric", false);
        assertFalse(nonArrayGenericType.isArray());
        
        IType arrayGenericType = new GenericType("TestGeneric", true);
        assertTrue(arrayGenericType.isArray());
    }

    @Test
    void testGetTypeMethodExists() {
        // Test that all implementations provide getType() method
        IType primitiveType = Primitive.INT;
        IType primitiveClassType = PrimitiveClasses.STRING;
        IType jpType = new JPType(new JoinPointClass("TestType"));
        IType arrayType = new ArrayType(Primitive.INT);
        IType genericType = new GenericType("TestGeneric", false);
        IType typeDefType = new TypeDef("TestTypeDef");
        IType enumDefType = new EnumDef("TestEnumDef");
        IType literalEnumType = new LiteralEnum("TestLiteralEnum");
        
        assertEquals("int", primitiveType.getType());
        assertEquals("String", primitiveClassType.getType());
        assertEquals("TestType", jpType.getType());
        assertEquals("int[]", arrayType.getType());
        assertEquals("TestGeneric", genericType.getType());
        assertEquals("TestTypeDef", typeDefType.getType());
        assertEquals("TestEnumDef", enumDefType.getType());
        assertNotNull(literalEnumType.getType()); // Returns string representation
    }

    @Test
    void testToStringMethodRequired() {
        // The interface declares toString() as abstract, test that all implementations provide it
        IType primitiveType = Primitive.INT;
        IType primitiveClassType = PrimitiveClasses.STRING;
        IType jpType = new JPType(new JoinPointClass("TestType"));
        IType arrayType = new ArrayType(Primitive.INT);
        IType genericType = new GenericType("TestGeneric", false);
        IType typeDefType = new TypeDef("TestTypeDef");
        IType enumDefType = new EnumDef("TestEnumDef");
        IType literalEnumType = new LiteralEnum("TestLiteralEnum");
        
        assertNotNull(primitiveType.toString());
        assertNotNull(primitiveClassType.toString());
        assertNotNull(jpType.toString());
        assertNotNull(arrayType.toString());
        assertNotNull(genericType.toString());
        assertNotNull(typeDefType.toString());
        assertNotNull(enumDefType.toString());
        assertNotNull(literalEnumType.toString());
    }

    @Test
    void testInterfaceMethods() {
        // Create a simple implementation to test interface methods directly
        IType testType = new IType() {
            @Override
            public String getType() {
                return "TestType";
            }

            @Override
            public String toString() {
                return "TestType";
            }
        };

        assertEquals("TestType", testType.getType());
        assertEquals("TestType", testType.toString());
        assertFalse(testType.isArray()); // Default implementation
    }

    @Test
    void testInterfaceWithArrayOverride() {
        // Create a simple implementation that overrides isArray()
        IType testArrayType = new IType() {
            @Override
            public String getType() {
                return "TestArrayType";
            }

            @Override
            public String toString() {
                return "TestArrayType";
            }

            @Override
            public boolean isArray() {
                return true;
            }
        };

        assertEquals("TestArrayType", testArrayType.getType());
        assertEquals("TestArrayType", testArrayType.toString());
        assertTrue(testArrayType.isArray()); // Overridden implementation
    }

    @Test
    void testPolymorphism() {
        // Test polymorphic behavior with IType
        IType[] types = {
            Primitive.INT,
            PrimitiveClasses.STRING,
            new JPType(new JoinPointClass("TestType")),
            new ArrayType(Primitive.INT),
            new GenericType("TestGeneric", false),
            new TypeDef("TestTypeDef"),
            new EnumDef("TestEnumDef"),
            new LiteralEnum("TestLiteralEnum")
        };

        for (IType type : types) {
            assertNotNull(type.getType());
            assertNotNull(type.toString());
            // isArray() varies depending on implementation
        }
    }

    @Test
    void testInterfaceDefaultMethodOverride() {
        // Test that default method can be overridden
        class CustomType implements IType {
            @Override
            public String getType() {
                return "CustomType";
            }

            @Override
            public String toString() {
                return "CustomType";
            }

            @Override
            public boolean isArray() {
                return true; // Override default
            }
        }

        IType customType = new CustomType();
        assertTrue(customType.isArray());
    }

    @Test
    void testInterfaceDefaultMethodInheritance() {
        // Test that default method is inherited when not overridden
        class SimpleType implements IType {
            @Override
            public String getType() {
                return "SimpleType";
            }

            @Override
            public String toString() {
                return "SimpleType";
            }
            // isArray() not overridden, should use default
        }

        IType simpleType = new SimpleType();
        assertFalse(simpleType.isArray()); // Uses default implementation
    }
}
