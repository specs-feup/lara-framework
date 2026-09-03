package org.lara.language.specification.dsl;

import org.lara.language.specification.dsl.types.ThisType;
import org.lara.language.specification.exception.LanguageSpecificationException;

import java.util.Set;

/**
 * Central place for identifier validation shared across the language specification model.
 */
public final class IdentifierValidator {

    /**
     * Reserved keywords that cannot be used as identifiers for join points, attributes, etc.
     */
    private static final Set<String> RESERVED_KEYWORDS = Set.of(
            ThisType.THIS_KEYWORD  // 'this' is reserved for late-bound self type
    );

    private IdentifierValidator() {
    }

    public static void requireValid(String identifier, String context) {
        if (identifier == null || identifier.isEmpty()) {
            return;
        }

        if (RESERVED_KEYWORDS.contains(identifier)) {
            throw new LanguageSpecificationException(
                    "Identifier '" + identifier + "' for " + context
                            + " is a reserved keyword and cannot be used");
        }

        if (!isValidJavaLikeIdentifier(identifier)) {
            throw new LanguageSpecificationException(
                    "Identifier '" + identifier + "' for " + context
                            + " must follow Java identifier rules");
        }
    }

    /**
     * Checks if the given identifier is a reserved keyword.
     *
     * @param identifier the identifier to check
     * @return true if it's a reserved keyword
     */
    public static boolean isReservedKeyword(String identifier) {
        return identifier != null && RESERVED_KEYWORDS.contains(identifier);
    }

    private static boolean isValidJavaLikeIdentifier(String identifier) {
        int firstCodePoint = identifier.codePointAt(0);
        if (!Character.isJavaIdentifierStart(firstCodePoint)) {
            return false;
        }

        for (int offset = Character.charCount(firstCodePoint); offset < identifier.length();
                offset += Character.charCount(identifier.codePointAt(offset))) {
            int codePoint = identifier.codePointAt(offset);
            if (!Character.isJavaIdentifierPart(codePoint)) {
                return false;
            }
        }

        return true;
    }
}
