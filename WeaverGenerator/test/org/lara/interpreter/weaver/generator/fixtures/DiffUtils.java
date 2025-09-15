package org.lara.interpreter.weaver.generator.fixtures;

import static org.assertj.core.api.Assertions.fail;

public final class DiffUtils {
    private DiffUtils() {}

    public static void assertEqualsNormalized(String expected, String actual) {
        String e = normalize(expected);
        String a = normalize(actual);
        if (!e.equals(a)) {
            fail("Contents differ.\nEXPECTED:\n" + e + "\nACTUAL:\n" + a);
        }
    }

    private static String normalize(String s) {
        return s.replace("\r\n", "\n").replace('\r', '\n');
    }
}
