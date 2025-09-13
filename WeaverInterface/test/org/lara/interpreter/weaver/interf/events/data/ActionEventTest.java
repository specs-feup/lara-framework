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

class ActionEventTest {

    private TestJoinPoint joinPoint;

    @BeforeEach
    void setUp() {
        joinPoint = new TestJoinPoint("testJoinPoint");
    }

    @Test
    void constructor_shouldSetAllFields() {
        List<Object> arguments = Arrays.asList("arg1", "arg2");
        Optional<Object> result = Optional.of("result");
        
        ActionEvent event = new ActionEvent(Stage.BEGIN, "testAction", joinPoint, arguments, result);
        
        assertThat(event.getStage()).isEqualTo(Stage.BEGIN);
        assertThat(event.getActionName()).isEqualTo("testAction");
        assertThat(event.getJoinPoint()).isEqualTo(joinPoint);
        assertThat(event.getArguments()).isEqualTo(arguments);
        // Result field is private, so we verify it through toString()
        assertThat(event.toString()).contains("result: result");
    }

    @Test
    void getActionName_shouldReturnActionName() {
        ActionEvent event = new ActionEvent(Stage.BEGIN, "insert", joinPoint, Collections.emptyList(), Optional.empty());
        
        assertThat(event.getActionName()).isEqualTo("insert");
    }

    @Test
    void getJoinPoint_shouldReturnJoinPoint() {
        ActionEvent event = new ActionEvent(Stage.BEGIN, "action", joinPoint, Collections.emptyList(), Optional.empty());
        
        assertThat(event.getJoinPoint()).isEqualTo(joinPoint);
    }

    @Test
    void getArguments_shouldReturnArguments() {
        List<Object> arguments = Arrays.asList("param1", 42, true);
        ActionEvent event = new ActionEvent(Stage.BEGIN, "action", joinPoint, arguments, Optional.empty());
        
        assertThat(event.getArguments()).isEqualTo(arguments);
        assertThat(event.getArguments()).containsExactly("param1", 42, true);
    }

    @Test
    void actionEvent_resultHandling_shouldBeReflectedInToString() {
        Optional<Object> result = Optional.of("actionResult");
        ActionEvent event = new ActionEvent(Stage.BEGIN, "action", joinPoint, Collections.emptyList(), result);
        
        assertThat(event.toString()).contains("result: actionResult");
    }

    @Test
    void actionEvent_emptyResult_shouldNotShowInToString() {
        ActionEvent event = new ActionEvent(Stage.BEGIN, "action", joinPoint, Collections.emptyList(), Optional.empty());
        
        assertThat(event.toString()).doesNotContain("result:");
    }

    @Test
    void toString_shouldContainStageActionJoinPointArgsAndResult() {
        List<Object> arguments = Arrays.asList("arg1", "arg2");
        Optional<Object> result = Optional.of("testResult");
        
        ActionEvent event = new ActionEvent(Stage.BEGIN, "insertBefore", joinPoint, arguments, result);
        
        String toString = event.toString();
        
        // Should contain stage information (inherited from BaseEvent)
        assertThat(toString).contains("Stage: begin");
        
        // Should contain action name
        assertThat(toString).contains("action insertBefore");
        
        // Should contain join point information 
        assertThat(toString).contains("in join point " + joinPoint.get_class());
        
        // Should contain arguments
        assertThat(toString).contains("with arguments: (arg1,arg2)");
        
        // Should contain result when present
        assertThat(toString).contains("result: testResult");
    }

    @Test
    void toString_shouldNotContainResultWhenEmpty() {
        List<Object> arguments = Arrays.asList("arg1");
        
        ActionEvent event = new ActionEvent(Stage.END, "remove", joinPoint, arguments, Optional.empty());
        
        String toString = event.toString();
        
        assertThat(toString).contains("Stage: end");
        assertThat(toString).contains("action remove");
        assertThat(toString).contains("with arguments: (arg1)");
        assertThat(toString).doesNotContain("result:");
    }

    @Test
    void toString_shouldHandleEmptyArguments() {
        ActionEvent event = new ActionEvent(Stage.DURING, "getName", joinPoint, Collections.emptyList(), Optional.of("name"));
        
        String toString = event.toString();
        
        assertThat(toString).contains("with arguments: ()");
        assertThat(toString).contains("result: name");
    }

    @Test
    void toString_shouldHandleComplexArguments() {
        List<Object> arguments = Arrays.asList("string", 42, null, true);
        ActionEvent event = new ActionEvent(Stage.BEGIN, "complexAction", joinPoint, arguments, Optional.empty());
        
        String toString = event.toString();
        
        // The StringUtils.join should handle various argument types including null
        assertThat(toString).contains("with arguments:");
        assertThat(toString).contains("string");
        assertThat(toString).contains("42");
        assertThat(toString).contains("true");
    }

    @Test
    void inheritedFromBaseEvent_shouldHaveStageGetter() {
        ActionEvent event = new ActionEvent(Stage.END, "action", joinPoint, Collections.emptyList(), Optional.empty());
        
        // ActionEvent extends BaseEvent, so should have getStage method
        assertThat(event.getStage()).isEqualTo(Stage.END);
    }
}