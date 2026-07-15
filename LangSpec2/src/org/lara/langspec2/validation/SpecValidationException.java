package org.lara.langspec2.validation;

import java.util.List;

/**
 * Exception thrown when spec validation fails.
 */
public class SpecValidationException extends RuntimeException {

    private final List<String> errors;

    public SpecValidationException(List<String> errors) {
        super("Specification validation failed:\n  - " + String.join("\n  - ", errors));
        this.errors = List.copyOf(errors);
    }

    public SpecValidationException(String error) {
        this(List.of(error));
    }

    public List<String> getErrors() {
        return errors;
    }
}
