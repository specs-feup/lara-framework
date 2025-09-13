package org.lara.interpreter.weaver.events;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lara.interpreter.weaver.fixtures.TestGear;
import org.lara.interpreter.weaver.fixtures.TestJoinPoint;
import org.lara.interpreter.weaver.interf.events.Stage;
import org.lara.interpreter.weaver.interf.events.data.ActionEvent;
import org.lara.interpreter.weaver.interf.events.data.AttributeEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class EventTriggerTest {

    private EventTrigger eventTrigger;
    private TestGear testGear;
    private TestJoinPoint testJoinPoint;

    @BeforeEach
    void setUp() {
        eventTrigger = new EventTrigger();
        testGear = new TestGear();
        testJoinPoint = new TestJoinPoint("testJoinPoint");
    }

    @Test
    void hasListeners_shouldReturnFalse_whenNoListenersRegistered() {
        assertThat(eventTrigger.hasListeners()).isFalse();
    }

    @Test
    void hasListeners_shouldReturnTrue_whenListenerRegistered() {
        eventTrigger.registerReceiver(testGear);
        
        assertThat(eventTrigger.hasListeners()).isTrue();
    }

    @Test
    void registerReceiver_shouldRegisterSingleReceiver() {
        eventTrigger.registerReceiver(testGear);
        
        assertThat(eventTrigger.hasListeners()).isTrue();
    }

    @Test
    void registerReceivers_shouldRegisterMultipleReceivers() {
        TestGear gear1 = new TestGear();
        TestGear gear2 = new TestGear();
        List<TestGear> gears = Arrays.asList(gear1, gear2);
        
        eventTrigger.registerReceivers(gears);
        
        assertThat(eventTrigger.hasListeners()).isTrue();
    }

    @Test
    void triggerAction_shouldTriggerBeginStage() {
        eventTrigger.registerReceiver(testGear);
        List<Object> params = Arrays.asList("param1", "param2");
        Optional<Object> result = Optional.of("result");
        
        eventTrigger.triggerAction(Stage.BEGIN, "testAction", testJoinPoint, params, result);
        
        List<ActionEvent> actionEvents = testGear.getActionEvents();
        assertThat(actionEvents).hasSize(1);
        
        ActionEvent event = actionEvents.get(0);
        assertThat(event.getStage()).isEqualTo(Stage.BEGIN);
        assertThat(event.getActionName()).isEqualTo("testAction");
        assertThat(event.getJoinPoint()).isEqualTo(testJoinPoint);
        assertThat(event.getArguments()).containsExactly("param1", "param2");
        // Verify result through toString since getResult() is not public
        assertThat(event.toString()).contains("result: result");
    }

    @Test
    void triggerAction_shouldTriggerEndStage() {
        eventTrigger.registerReceiver(testGear);
        Optional<Object> result = Optional.empty();
        
        eventTrigger.triggerAction(Stage.END, "testAction", testJoinPoint, result, "param1", "param2");
        
        List<ActionEvent> actionEvents = testGear.getActionEvents();
        assertThat(actionEvents).hasSize(1);
        
        ActionEvent event = actionEvents.get(0);
        assertThat(event.getStage()).isEqualTo(Stage.END);
        assertThat(event.getActionName()).isEqualTo("testAction");
        assertThat(event.getJoinPoint()).isEqualTo(testJoinPoint);
        assertThat(event.getArguments()).containsExactly("param1", "param2");
        // For empty result, toString should not contain "result:"
        assertThat(event.toString()).doesNotContain("result:");
    }

    @Test
    void triggerAttribute_shouldTriggerAttributeEventWithArgs() {
        eventTrigger.registerReceiver(testGear);
        Optional<Object> result = Optional.of("attributeValue");
        
        eventTrigger.triggerAttribute(Stage.BEGIN, testJoinPoint, "testAttribute", result, "arg1", "arg2");
        
        List<AttributeEvent> attributeEvents = testGear.getAttributeEvents();
        assertThat(attributeEvents).hasSize(1);
        
        AttributeEvent event = attributeEvents.get(0);
        assertThat(event.getStage()).isEqualTo(Stage.BEGIN);
        assertThat(event.getTarget()).isEqualTo(testJoinPoint);
        assertThat(event.getAttribute()).isEqualTo("testAttribute");
        assertThat(event.getArguments()).containsExactly("arg1", "arg2");
        assertThat(event.getResult()).isEqualTo(result);
    }

    @Test
    void triggerAttribute_shouldTriggerAttributeEventWithList() {
        eventTrigger.registerReceiver(testGear);
        List<Object> args = Arrays.asList("arg1", "arg2");
        Optional<Object> result = Optional.of("attributeValue");
        
        eventTrigger.triggerAttribute(Stage.END, testJoinPoint, "testAttribute", args, result);
        
        List<AttributeEvent> attributeEvents = testGear.getAttributeEvents();
        assertThat(attributeEvents).hasSize(1);
        
        AttributeEvent event = attributeEvents.get(0);
        assertThat(event.getStage()).isEqualTo(Stage.END);
        assertThat(event.getTarget()).isEqualTo(testJoinPoint);
        assertThat(event.getAttribute()).isEqualTo("testAttribute");
        assertThat(event.getArguments()).containsExactly("arg1", "arg2");
        assertThat(event.getResult()).isEqualTo(result);
    }

    @Test
    void triggerEvents_shouldRecordEventPayloadsEndToEnd() {
        eventTrigger.registerReceiver(testGear);
        
        // Trigger action event
        eventTrigger.triggerAction(Stage.BEGIN, "insert", testJoinPoint, Optional.of("insertResult"), "beforeStmt");
        
        // Trigger attribute event
        eventTrigger.triggerAttribute(Stage.END, testJoinPoint, "name", Optional.of("functionName"));
        
        // Verify both events were recorded
        assertThat(testGear.getActionEvents()).hasSize(1);
        assertThat(testGear.getAttributeEvents()).hasSize(1);
        
        ActionEvent actionEvent = testGear.getActionEvents().get(0);
        assertThat(actionEvent.getStage()).isEqualTo(Stage.BEGIN);
        assertThat(actionEvent.getActionName()).isEqualTo("insert");
        assertThat(actionEvent.toString()).contains("result: insertResult");
        
        AttributeEvent attributeEvent = testGear.getAttributeEvents().get(0);
        assertThat(attributeEvent.getStage()).isEqualTo(Stage.END);
        assertThat(attributeEvent.getAttribute()).isEqualTo("name");
        assertThat(attributeEvent.getResult()).isPresent().contains("functionName");
    }

    @Test
    void triggerAction_withMultipleGears_shouldNotifyAllGears() {
        TestGear gear1 = new TestGear();
        TestGear gear2 = new TestGear();
        
        eventTrigger.registerReceiver(gear1);
        eventTrigger.registerReceiver(gear2);
        
        eventTrigger.triggerAction(Stage.BEGIN, "testAction", testJoinPoint, Optional.empty(), "param");
        
        assertThat(gear1.getActionEvents()).hasSize(1);
        assertThat(gear2.getActionEvents()).hasSize(1);
        
        assertThat(gear1.getActionEvents().get(0).getActionName()).isEqualTo("testAction");
        assertThat(gear2.getActionEvents().get(0).getActionName()).isEqualTo("testAction");
    }

    @Test
    void triggerAttribute_withMultipleGears_shouldNotifyAllGears() {
        TestGear gear1 = new TestGear();
        TestGear gear2 = new TestGear();
        
        eventTrigger.registerReceiver(gear1);
        eventTrigger.registerReceiver(gear2);
        
        eventTrigger.triggerAttribute(Stage.BEGIN, testJoinPoint, "testAttr", Optional.empty());
        
        assertThat(gear1.getAttributeEvents()).hasSize(1);
        assertThat(gear2.getAttributeEvents()).hasSize(1);
        
        assertThat(gear1.getAttributeEvents().get(0).getAttribute()).isEqualTo("testAttr");
        assertThat(gear2.getAttributeEvents().get(0).getAttribute()).isEqualTo("testAttr");
    }
}