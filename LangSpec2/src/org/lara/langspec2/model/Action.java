package org.lara.langspec2.model;

import org.lara.langspec2.types.JpDataType;

import java.util.List;

/**
 * An action declared on a join point class.
 *
 * @param name       the action name
 * @param returnType the return type of the action
 * @param parameters the action's parameters
 * @param tooltip    optional documentation tooltip
 */
public record Action(String name, JpDataType returnType, List<Parameter> parameters, String tooltip) {
    public Action {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Action name must not be null or blank");
        }
        if (returnType == null) {
            throw new IllegalArgumentException("Action return type must not be null");
        }
        parameters = parameters == null ? List.of() : List.copyOf(parameters);
    }

    /**
     * Convenience constructor without tooltip.
     */
    public Action(String name, JpDataType returnType, List<Parameter> parameters) {
        this(name, returnType, parameters, null);
    }

    /**
     * Convenience constructor for parameter-less actions.
     */
    public Action(String name, JpDataType returnType) {
        this(name, returnType, List.of(), null);
    }
}
