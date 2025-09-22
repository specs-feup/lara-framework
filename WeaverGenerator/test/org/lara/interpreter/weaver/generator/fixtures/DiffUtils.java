package org.lara.interpreter.weaver.generator.fixtures;

import static org.assertj.core.api.Assertions.fail;

import java.util.Map;

public final class DiffUtils {
    private DiffUtils() {
    }

    public static void assertEqualsNormalized(String expected, String actual) {
        assertEqualsNormalized(expected, actual, Map.of());
    }

    public static void assertEqualsNormalized(String expected, String actual, Map<String, String> tokenMap) {
        String e = applyTokens(normalize(expected), tokenMap);
        String a = applyTokens(normalize(actual), tokenMap);
        if (!e.equals(a)) {
            fail("Contents differ.\nEXPECTED:\n" + e + "\nACTUAL:\n" + a);
        }
    }

    private static String normalize(String s) {
        return s.replace("\r\n", "\n").replace('\r', '\n');
    }

    private static String applyTokens(String s, Map<String, String> tokenMap) {
        String result = s;
        for (var entry : tokenMap.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
