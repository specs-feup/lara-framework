package org.lara.interpreter.weaver.generator.fixtures;

public final class BaselineRegen {

    public static final String PROPERTY = "weaver.integration.regen";

    private BaselineRegen() {
    }

    public static boolean isEnabled() {
        return Boolean.getBoolean(PROPERTY);
    }
}