package org.lara.langspec2.model;

import java.util.List;

/**
 * An enum value entry.
 *
 * @param value   the programmatic value
 * @param display the display string (null means same as value)
 */
public record EnumValue(String value, String display) {
    public EnumValue {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("EnumValue value must not be null or blank");
        }
    }

    public EnumValue(String value) {
        this(value, null);
    }

    /**
     * Returns the display name, falling back to value if display is null.
     */
    public String displayOrValue() {
        return display != null ? display : value;
    }
}
