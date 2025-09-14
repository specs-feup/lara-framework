package org.lara.interpreter.weaver.fixtures;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lara.interpreter.weaver.events.EventTrigger;

public class FixturesSmokeTest {

    @Test
    @DisplayName("Fixtures can be instantiated and wired with EventTrigger")
    void smoke() {
        var engine = new TestWeaverEngine();
        var gear = new TestGear();
        var trigger = new EventTrigger();
        trigger.registerReceiver(gear);
        engine.setEventTrigger(trigger);
        engine.setWeaver();
        try {
            assertThat(engine.getRootJp()).isNotNull();
            assertThat(engine.implementsEvents()).isTrue();
        } finally {
            // Cleanup thread-local
            engine.removeWeaver();
        }
    }
}
