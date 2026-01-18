package org.lara.language.specification.dsl;

import org.lara.language.specification.exception.LanguageSpecificationException;

/**
 * Central place for identifier validation shared across the language specification model.
 */
public final class IdentifierValidator {

    private IdentifierValidator() {
    }

    public static void requireValid(String identifier, String context) {
        if (identifier == null || identifier.isEmpty()) {
            return;
        }

        if (!isValidJavaLikeIdentifier(identifier)) {
            throw new LanguageSpecificationException(
                    "Identifier '" + identifier + "' for " + context
                            + " must follow Java identifier rules");
        }
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
