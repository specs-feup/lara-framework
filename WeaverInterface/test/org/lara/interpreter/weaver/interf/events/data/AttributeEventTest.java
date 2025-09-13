package org.lara.interpreter.weaver.interf.events.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lara.interpreter.weaver.fixtures.TestJoinPoint;
import org.lara.interpreter.weaver.interf.events.Stage;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class AttributeEventTest {

    private TestJoinPoint joinPoint;

    @BeforeEach
    void setUp() {
        joinPoint = new TestJoinPoint("testJoinPoint");
    }

    @Test
    void constructor_shouldSetAllFields() {
        List<Object> arguments = Arrays.asList("arg1", "arg2");
        Optional<Object> result = Optional.of("attributeValue");
        
        AttributeEvent event = new AttributeEvent(Stage.BEGIN, joinPoint, "testAttribute", arguments, result);
        
        assertThat(event.getStage()).isEqualTo(Stage.BEGIN);
        assertThat(event.getTarget()).isEqualTo(joinPoint);
        assertThat(event.getAttribute()).isEqualTo("testAttribute");
        assertThat(event.getArguments()).isEqualTo(arguments);
        assertThat(event.getResult()).isEqualTo(result);
    }

    @Test
    void getTarget_shouldReturnTarget() {
        AttributeEvent event = new AttributeEvent(Stage.BEGIN, joinPoint, "name", Collections.emptyList(), Optional.empty());
        
        assertThat(event.getTarget()).isEqualTo(joinPoint);
    }

    @Test
    void getAttribute_shouldReturnAttributeName() {
        AttributeEvent event = new AttributeEvent(Stage.BEGIN, joinPoint, "functionName", Collections.emptyList(), Optional.empty());
        
        assertThat(event.getAttribute()).isEqualTo("functionName");
    }

    @Test
    void getArguments_shouldReturnArguments() {
        List<Object> arguments = Arrays.asList("index", 42);
        AttributeEvent event = new AttributeEvent(Stage.BEGIN, joinPoint, "param", arguments, Optional.empty());
        
        assertThat(event.getArguments()).isEqualTo(arguments);
        assertThat(event.getArguments()).containsExactly("index", 42);
    }

    @Test
    void getResult_shouldReturnResult() {
        Optional<Object> result = Optional.of("attributeValue");
        AttributeEvent event = new AttributeEvent(Stage.BEGIN, joinPoint, "attribute", Collections.emptyList(), result);
        
        assertThat(event.getResult()).isEqualTo(result);
        assertThat(event.getResult()).isPresent().contains("attributeValue");
    }

    @Test
    void getResult_shouldReturnEmptyOptionalWhenNoResult() {
        AttributeEvent event = new AttributeEvent(Stage.BEGIN, joinPoint, "attribute", Collections.emptyList(), Optional.empty());
        
        assertThat(event.getResult()).isEmpty();
    }

    @Test
    void getStage_shouldReturnInheritedStage() {
        AttributeEvent event = new AttributeEvent(Stage.END, joinPoint, "attribute", Collections.emptyList(), Optional.empty());
        
        // AttributeEvent extends BaseEvent, so should have getStage method
        assertThat(event.getStage()).isEqualTo(Stage.END);
    }

    @Test
    void toString_shouldInheritFromBaseEvent() {
        AttributeEvent event = new AttributeEvent(Stage.DURING, joinPoint, "name", Collections.emptyList(), Optional.of("functionName"));
        
        String toString = event.toString();
        
        // Should contain stage information from BaseEvent
        assertThat(toString).contains("Stage: during");
    }

    @Test
    void attributeEvent_withComplexArguments() {
        List<Object> arguments = Arrays.asList("param1", 123, null, false);
        Optional<Object> result = Optional.of("complexResult");
        
        AttributeEvent event = new AttributeEvent(Stage.BEGIN, joinPoint, "complexAttribute", arguments, result);
        
        assertThat(event.getAttribute()).isEqualTo("complexAttribute");
        assertThat(event.getArguments()).containsExactly("param1", 123, null, false);
        assertThat(event.getResult()).isPresent().contains("complexResult");
    }

    @Test
    void attributeEvent_withEmptyArguments() {
        AttributeEvent event = new AttributeEvent(Stage.END, joinPoint, "simpleAttribute", Collections.emptyList(), Optional.of("value"));
        
        assertThat(event.getArguments()).isEmpty();
        assertThat(event.getAttribute()).isEqualTo("simpleAttribute");
        assertThat(event.getResult()).isPresent().contains("value");
    }

    @Test
    void attributeEvent_allStages() {
        // Test that AttributeEvent works with all stages
        AttributeEvent beginEvent = new AttributeEvent(Stage.BEGIN, joinPoint, "attr", Collections.emptyList(), Optional.empty());
        AttributeEvent duringEvent = new AttributeEvent(Stage.DURING, joinPoint, "attr", Collections.emptyList(), Optional.empty());
        AttributeEvent endEvent = new AttributeEvent(Stage.END, joinPoint, "attr", Collections.emptyList(), Optional.empty());
        
        assertThat(beginEvent.getStage()).isEqualTo(Stage.BEGIN);
        assertThat(duringEvent.getStage()).isEqualTo(Stage.DURING);
        assertThat(endEvent.getStage()).isEqualTo(Stage.END);
    }

    @Test
    void attributeEvent_shouldMaintainArgumentsOrder() {
        List<Object> arguments = Arrays.asList("first", "second", "third");
        AttributeEvent event = new AttributeEvent(Stage.BEGIN, joinPoint, "orderedAttribute", arguments, Optional.empty());
        
        List<Object> retrievedArgs = event.getArguments();
        assertThat(retrievedArgs).containsExactly("first", "second", "third");
    }

    @Test 
    void attributeEvent_withNullAttributeName_shouldAcceptNull() {
        // Test edge case where attribute name might be null
        AttributeEvent event = new AttributeEvent(Stage.BEGIN, joinPoint, null, Collections.emptyList(), Optional.empty());
        
        assertThat(event.getAttribute()).isNull();
        assertThat(event.getTarget()).isEqualTo(joinPoint);
    }

    @Test
    void attributeEvent_resultPresenceCheck() {
        AttributeEvent eventWithResult = new AttributeEvent(Stage.BEGIN, joinPoint, "attr", Collections.emptyList(), Optional.of("result"));
        AttributeEvent eventWithoutResult = new AttributeEvent(Stage.BEGIN, joinPoint, "attr", Collections.emptyList(), Optional.empty());
        
        assertThat(eventWithResult.getResult()).isPresent();
        assertThat(eventWithoutResult.getResult()).isEmpty();
    }
}