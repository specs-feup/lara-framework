package org.lara.langspec2.types;

import java.util.List;

/**
 * Sealed interface hierarchy representing all possible types in a weaver specification.
 * Uses records for immutability and pattern matching support.
 */
public sealed interface JpDataType {

    /**
     * Primitive type (int, boolean, String, void, long, double, float, byte, short, char, Object).
     */
    record PrimitiveType(String name) implements JpDataType {
        public PrimitiveType {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Primitive type name must not be null or blank");
            }
        }
    }

    /**
     * Self-referential type ("this") -- resolves to the CRTP Self type parameter in generated code.
     */
    record SelfType() implements JpDataType {}

    /**
     * Reference to another join point type by name.
     */
    record JpRefType(String jpName) implements JpDataType {
        public JpRefType {
            if (jpName == null || jpName.isBlank()) {
                throw new IllegalArgumentException("Join point reference name must not be null or blank");
            }
        }
    }

    /**
     * Array type (T[]).
     */
    record ArrayType(JpDataType element) implements JpDataType {
        public ArrayType {
            if (element == null) {
                throw new IllegalArgumentException("Array element type must not be null");
            }
        }
    }

    /**
     * Parameterized (generic) type, e.g., List&lt;String&gt;, Map&lt;String, Integer&gt;.
     */
    record ParameterizedType(JpDataType base, List<JpDataType> args) implements JpDataType {
        public ParameterizedType {
            if (base == null) {
                throw new IllegalArgumentException("Parameterized type base must not be null");
            }
            if (args == null || args.isEmpty()) {
                throw new IllegalArgumentException("Parameterized type must have at least one type argument");
            }
            args = List.copyOf(args);
        }
    }

    /**
     * Wildcard type (?, ? extends X, ? super X).
     */
    record WildcardType(BoundKind kind, JpDataType bound) implements JpDataType {
        public WildcardType {
            if (kind == null) {
                throw new IllegalArgumentException("Wildcard bound kind must not be null");
            }
            if (kind != BoundKind.UNBOUNDED && bound == null) {
                throw new IllegalArgumentException("Bounded wildcard must have a bound type");
            }
            if (kind == BoundKind.UNBOUNDED && bound != null) {
                throw new IllegalArgumentException("Unbounded wildcard must not have a bound type");
            }
        }
    }

    enum BoundKind {
        UNBOUNDED, EXTENDS, SUPER
    }
}
