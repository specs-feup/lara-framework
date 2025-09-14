package org.lara.interpreter.weaver.interf.events.data;

import org.junit.jupiter.api.Test;
import org.lara.interpreter.weaver.fixtures.TestJoinPoint;
import org.lara.interpreter.weaver.interf.events.Stage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class AttributeEventTest {

    @Test
    void getters_and_base_toString() {
        var jp = new TestJoinPoint("loop");
        var ev = new AttributeEvent(Stage.BEGIN, jp, "iters", List.of(1, 2), Optional.empty());

        assertThat(ev.getStage()).isEqualTo(Stage.BEGIN);
        assertThat(ev.getTarget()).isSameAs(jp);
        assertThat(ev.getAttribute()).isEqualTo("iters");
        assertThat(ev.getArguments()).containsExactly(1, 2);
        assertThat(ev.getResult()).isEmpty();
        assertThat(ev.toString()).contains("Stage: begin");

        var ev2 = new AttributeEvent(Stage.END, jp, "iters", List.of(), Optional.of(99));
        assertThat(ev2.getResult()).contains(99);
        assertThat(ev2.toString()).contains("Stage: end");
    }
}
