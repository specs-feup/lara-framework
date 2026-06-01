package org.lara.weavergen2.api;

import java.util.Locale;

public enum ConcreteSourcePolicy {
    DISABLED,
    VALIDATE_ONLY,
    CREATE_MISSING_AND_VALIDATE;

    public static ConcreteSourcePolicy parse(String value) {
        return switch (value.toLowerCase(Locale.ROOT)) {
            case "disabled" -> DISABLED;
            case "validate-only" -> VALIDATE_ONLY;
            case "create-missing-and-validate" -> CREATE_MISSING_AND_VALIDATE;
            default -> throw new IllegalArgumentException("Unknown concrete source policy: " + value);
        };
    }
}
