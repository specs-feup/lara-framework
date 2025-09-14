package org.lara.interpreter.weaver.interf.events;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StageTest {

    @Test
    void getName_isLowercase() {
        assertThat(Stage.BEGIN.getName()).isEqualTo("begin");
        assertThat(Stage.DURING.getName()).isEqualTo("during");
        assertThat(Stage.END.getName()).isEqualTo("end");
    }

    @Test
    void toCode_format() {
        assertThat(Stage.BEGIN.toCode()).isEqualTo("Stage.BEGIN");
        assertThat(Stage.DURING.toCode()).isEqualTo("Stage.DURING");
        assertThat(Stage.END.toCode()).isEqualTo("Stage.END");
    }
}
