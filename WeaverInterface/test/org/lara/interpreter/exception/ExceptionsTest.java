package org.lara.interpreter.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExceptionsTest {

    @Test
    @DisplayName("LaraIException formats message")
    void laraIExceptionMessage() {
        LaraIException ex = new LaraIException("Something went wrong");
        assertThat(ex.getMessage()).contains("LARAI Exception for");
        assertThat(ex.getMessage()).contains("Something went wrong");
    }

    @Test
    @DisplayName("ActionException formats message including join point and action")
    void actionExceptionMessage() {
        ActionException ex = new ActionException("MyJp", "doIt", new RuntimeException("cause"));
        assertThat(ex.getMessage()).contains("Exception");
        assertThat(ex.getMessage()).contains("in action MyJp.doIt");
        assertThat(ex.getCause()).isInstanceOf(RuntimeException.class);
    }
}
