package org.lara.weavergen2.fixtures;

public final class BaselineRegen {

    public static final String PROPERTY = "weaver.integration.regen";

    private BaselineRegen() {
    }

    public static boolean isEnabled() {
        return Boolean.getBoolean(PROPERTY);
    }

    public static void runOrVerify(ThrowingRunnable regenAction, ThrowingRunnable verifyAction) throws Exception {
        if (isEnabled()) {
            regenAction.run();
            return;
        }

        verifyAction.run();
    }

    public static void ifEnabled(ThrowingRunnable action) throws Exception {
        if (isEnabled()) {
            action.run();
        }
    }

    @FunctionalInterface
    public interface ThrowingRunnable {
        void run() throws Exception;
    }
}
