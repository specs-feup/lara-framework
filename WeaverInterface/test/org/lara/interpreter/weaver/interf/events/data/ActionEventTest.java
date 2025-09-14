package org.lara.interpreter.weaver.interf.events.data;

import org.junit.jupiter.api.Test;
import org.lara.interpreter.weaver.fixtures.TestJoinPoint;
import org.lara.interpreter.weaver.interf.events.Stage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ActionEventTest {

    @Test
    void getters_and_toString_contains_expected_fields() {
        var jp = new TestJoinPoint("call");
        var ev = new ActionEvent(Stage.BEGIN, "replace", jp, List.of("arg1", 2), Optional.empty());

        assertThat(ev.getStage()).isEqualTo(Stage.BEGIN);
        assertThat(ev.getActionName()).isEqualTo("replace");
        assertThat(ev.getJoinPoint()).isSameAs(jp);
        assertThat(ev.getArguments()).containsExactly("arg1", 2);

        var text = ev.toString();
        assertThat(text).contains("Stage: begin");
        assertThat(text).contains("action replace");
        assertThat(text).contains("with arguments: (arg1,2)");
        assertThat(text).contains("join point call");

        var ev2 = new ActionEvent(Stage.END, "replace", jp, List.of(), Optional.of("done"));
        assertThat(ev2.toString()).contains("result: done");
    }
}
