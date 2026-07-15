package org.lara.langspec2.model;

import org.lara.langspec2.types.JpDataType;

import java.util.List;

/**
 * An attribute declared on a join point class.
 *
 * @param name       the attribute name
 * @param type       the attribute return type
 * @param parameters optional parameters for parameterized attributes (e.g., getAncestor(type))
 * @param tooltip    optional documentation tooltip
 */
public record Attribute(String name, JpDataType type, List<Parameter> parameters, String tooltip) {
    public Attribute {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Attribute name must not be null or blank");
        }
        if (type == null) {
            throw new IllegalArgumentException("Attribute type must not be null");
        }
        parameters = parameters == null ? List.of() : List.copyOf(parameters);
    }

    /**
     * Convenience constructor for simple attributes without parameters.
     */
    public Attribute(String name, JpDataType type) {
        this(name, type, List.of(), null);
    }

    /**
     * Convenience constructor without tooltip.
     */
    public Attribute(String name, JpDataType type, List<Parameter> parameters) {
        this(name, type, parameters, null);
    }
}
