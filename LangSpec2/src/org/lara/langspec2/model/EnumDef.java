package org.lara.langspec2.model;

import java.util.List;

/**
 * A user-defined enumeration.
 *
 * @param name    the enum name
 * @param values  the enum values
 * @param tooltip optional documentation tooltip
 */
public record EnumDef(String name, List<EnumValue> values, String tooltip) {
    public EnumDef {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("EnumDef name must not be null or blank");
        }
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("EnumDef must have at least one value");
        }
        values = List.copyOf(values);
    }

    public EnumDef(String name, List<EnumValue> values) {
        this(name, values, null);
    }
}
