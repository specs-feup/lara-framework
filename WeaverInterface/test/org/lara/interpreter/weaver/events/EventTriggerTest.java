package org.lara.interpreter.weaver.events;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lara.interpreter.weaver.fixtures.TestGear;
import org.lara.interpreter.weaver.fixtures.TestJoinPoint;
import org.lara.interpreter.weaver.interf.events.Stage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class EventTriggerTest {

    private final EventTrigger trigger = new EventTrigger();

    @AfterEach
    void cleanup() {
        // no global state to cleanup for EventTrigger tests
    }

    @Test
    @DisplayName("hasListeners reflects receiver registration (single and list)")
    void hasListeners_registerReceiver_and_registerReceivers() {
        assertThat(trigger.hasListeners()).isFalse();

        var gear1 = new TestGear();
        trigger.registerReceiver(gear1);
        assertThat(trigger.hasListeners()).isTrue();

        var gear2 = new TestGear();
        var gear3 = new TestGear();
        var trigger2 = new EventTrigger();
        assertThat(trigger2.hasListeners()).isFalse();
        trigger2.registerReceivers(List.of(gear2, gear3));
        assertThat(trigger2.hasListeners()).isTrue();

        // Sanity: all gears receive events after registration via list
        var jp = new TestJoinPoint("node");
        trigger2.triggerAction(Stage.BEGIN, "touch", jp, Optional.empty(), "a", 1);
        assertThat(gear2.getActionEvents()).hasSize(1);
        assertThat(gear3.getActionEvents()).hasSize(1);
    }

    @Test
    @DisplayName("triggerAction dispatches BEGIN and END with correct payloads")
    void triggerAction_begin_end() {
        var gear = new TestGear();
        trigger.registerReceiver(gear);

        var target = new TestJoinPoint("function");

        // BEGIN without result
        trigger.triggerAction(Stage.BEGIN, "insert", target, Optional.empty(), 10, "x");

        // END with result
        trigger.triggerAction(Stage.END, "insert", target, List.of(10, "x"), Optional.of("ok"));

        var events = gear.getActionEvents();
        assertThat(events).hasSize(2);

        var begin = events.get(0);
        assertThat(begin.getStage()).isEqualTo(Stage.BEGIN);
        assertThat(begin.getActionName()).isEqualTo("insert");
        assertThat(begin.getJoinPoint()).isSameAs(target);
        assertThat(begin.getArguments()).containsExactly(10, "x");
        assertThat(begin.toString())
                .contains("Stage: begin")
                .contains("action insert")
                .contains("with arguments: (10,x)")
                .doesNotContain("result:");

        var end = events.get(1);
        assertThat(end.getStage()).isEqualTo(Stage.END);
        assertThat(end.getActionName()).isEqualTo("insert");
        assertThat(end.getJoinPoint()).isSameAs(target);
        assertThat(end.getArguments()).containsExactly(10, "x");
        assertThat(end.toString())
                .contains("Stage: end")
                .contains("action insert")
                .contains("with arguments: (10,x)")
                .contains("result: ok");
    }

    @Test
    @DisplayName("triggerAttribute dispatches BEGIN and END with correct payloads")
    void triggerAttribute_begin_end() {
        var gear = new TestGear();
        trigger.registerReceiver(gear);

        var target = new TestJoinPoint("loop");

        trigger.triggerAttribute(Stage.BEGIN, target, "size", Optional.empty());
        trigger.triggerAttribute(Stage.END, target, "size", List.of(), Optional.of(42));

        var attrs = gear.getAttributeEvents();
        assertThat(attrs).hasSize(2);

        var begin = attrs.get(0);
        assertThat(begin.getStage()).isEqualTo(Stage.BEGIN);
        assertThat(begin.getTarget()).isSameAs(target);
        assertThat(begin.getAttribute()).isEqualTo("size");
        assertThat(begin.getArguments()).isEmpty();
        assertThat(begin.toString()).contains("Stage: begin"); // from BaseEvent.toString

        var end = attrs.get(1);
        assertThat(end.getStage()).isEqualTo(Stage.END);
        assertThat(end.getTarget()).isSameAs(target);
        assertThat(end.getAttribute()).isEqualTo("size");
        assertThat(end.getArguments()).isEmpty();
        assertThat(end.getResult()).contains(42);
        assertThat(end.toString()).contains("Stage: end");
    }
}
