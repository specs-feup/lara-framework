package org.lara.interpreter.weaver.generator.fixtures;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Random;

public final class Determinism {
    private Determinism() {}

    public static final long TEST_SEED = 0x5EEDCAFECAFEL;

    public static Random fixedRandom() {
        return new Random(TEST_SEED);
    }

    public static Clock fixedClock() {
        return Clock.fixed(Instant.parse("2020-01-01T00:00:00Z"), ZoneId.of("UTC"));
    }

    // SecureRandom only if needed; avoid if generator does not use it.
    public static SecureRandom fixedSecureRandom() {
        var r = new SecureRandom();
        r.setSeed(TEST_SEED);
        return r;
    }
}
