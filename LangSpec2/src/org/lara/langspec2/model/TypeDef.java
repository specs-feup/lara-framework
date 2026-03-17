package org.lara.langspec2.model;

import java.util.List;

/**
 * A user-defined composite type (struct-like).
 *
 * @param name    the type name
 * @param fields  the fields of the type definition
 * @param tooltip optional documentation tooltip
 */
public record TypeDef(String name, List<Attribute> fields, String tooltip) {
    public TypeDef {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("TypeDef name must not be null or blank");
        }
        fields = fields == null ? List.of() : List.copyOf(fields);
    }

    public TypeDef(String name, List<Attribute> fields) {
        this(name, fields, null);
    }
}
