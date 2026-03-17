package org.lara.langspec2.model;

import org.lara.langspec2.types.JpDataType;

import java.util.List;

/**
 * A parameter of an attribute or action.
 *
 * @param name         the parameter name
 * @param type         the parameter type
 * @param defaultValue optional default value (null if none)
 */
public record Parameter(String name, JpDataType type, String defaultValue) {
    public Parameter {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Parameter name must not be null or blank");
        }
        if (type == null) {
            throw new IllegalArgumentException("Parameter type must not be null");
        }
    }

    /**
     * Convenience constructor without default value.
     */
    public Parameter(String name, JpDataType type) {
        this(name, type, null);
    }
}
