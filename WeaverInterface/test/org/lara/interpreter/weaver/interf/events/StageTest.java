package org.lara.interpreter.weaver.interf.events;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StageTest {

    @Test
    void getName_shouldReturnLowercaseName() {
        assertThat(Stage.BEGIN.getName()).isEqualTo("begin");
        assertThat(Stage.DURING.getName()).isEqualTo("during");
        assertThat(Stage.END.getName()).isEqualTo("end");
    }

    @Test
    void toCode_shouldReturnProperFormat() {
        assertThat(Stage.BEGIN.toCode()).isEqualTo("Stage.BEGIN");
        assertThat(Stage.DURING.toCode()).isEqualTo("Stage.DURING");
        assertThat(Stage.END.toCode()).isEqualTo("Stage.END");
    }

    @Test
    void toString_shouldUseDefaultEnumToString() {
        // Verify that the enum values maintain their default toString behavior
        assertThat(Stage.BEGIN.toString()).isEqualTo("BEGIN");
        assertThat(Stage.DURING.toString()).isEqualTo("DURING");
        assertThat(Stage.END.toString()).isEqualTo("END");
    }

    @Test
    void values_shouldContainAllExpectedValues() {
        Stage[] values = Stage.values();
        
        assertThat(values).hasSize(3);
        assertThat(values).containsExactlyInAnyOrder(Stage.BEGIN, Stage.DURING, Stage.END);
    }

    @Test
    void valueOf_shouldReturnCorrectStageForValidName() {
        assertThat(Stage.valueOf("BEGIN")).isEqualTo(Stage.BEGIN);
        assertThat(Stage.valueOf("DURING")).isEqualTo(Stage.DURING);
        assertThat(Stage.valueOf("END")).isEqualTo(Stage.END);
    }
}