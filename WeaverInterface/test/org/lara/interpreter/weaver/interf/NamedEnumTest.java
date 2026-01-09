package org.lara.interpreter.weaver.interf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lara.interpreter.exception.LaraIException;

class NamedEnumTest {

    private enum Color implements NamedEnum {
        RED("red"), BLUE("blue");

        private final String name;

        Color(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    @Test
    @DisplayName("fromString returns matching constant by name")
    void fromStringSuccess() {
        var c = NamedEnum.fromString(Color.class, "red", "Color");
        assertThat(c).isEqualTo(Color.RED);
        assertThat(c.getString()).isEqualTo("red");
    }

    @Test
    @DisplayName("fromString throws LaraIException with expected message on unknown value")
    void fromStringFailure() {
        assertThatThrownBy(() -> NamedEnum.fromString(Color.class, "green", "Color"))
                .isInstanceOf(LaraIException.class)
                .hasMessageContaining("Unknown value for Color: green")
                .hasMessageContaining("red")
                .hasMessageContaining("blue");
    }
}
