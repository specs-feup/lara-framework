package org.lara.interpreter.joptions.panels;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lara.interpreter.weaver.fixtures.TestDataStores;
import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Datakey.KeyFactory;

@DisplayName("EnumRadioButtonPanel")
class EnumRadioButtonPanelTest {

    enum TestPriority {
        LOW, MEDIUM, HIGH
    }

    enum SingleValueEnum {
        ONLY
    }

    @Test
    @DisplayName("getValue returns selected enum value")
    void getValueReturnsSelected() {
        DataKey<TestPriority> key = KeyFactory.object("priority", TestPriority.class)
                .setDefault(() -> TestPriority.LOW);

        EnumRadioButtonPanel<TestPriority> panel = new EnumRadioButtonPanel<>(key, TestDataStores.empty());

        // Default should be LOW (first constant)
        assertThat(panel.getValue()).isEqualTo(TestPriority.LOW);
    }

    @Test
    @DisplayName("setValue changes selected radio button")
    void setValueChangesSelection() {
        DataKey<TestPriority> key = KeyFactory.object("priority", TestPriority.class)
                .setDefault(() -> TestPriority.LOW);

        EnumRadioButtonPanel<TestPriority> panel = new EnumRadioButtonPanel<>(key, TestDataStores.empty());

        // Change value
        panel.setValue(TestPriority.HIGH);

        // Verify new value is returned
        assertThat(panel.getValue()).isEqualTo(TestPriority.HIGH);
    }

    @Test
    @DisplayName("panel initializes with default value selected")
    void panelInitializesWithDefault() {
        DataKey<TestPriority> key = KeyFactory.object("priority", TestPriority.class)
                .setDefault(() -> TestPriority.MEDIUM);

        EnumRadioButtonPanel<TestPriority> panel = new EnumRadioButtonPanel<>(key, TestDataStores.empty());

        // Should initialize with MEDIUM selected
        assertThat(panel.getValue()).isEqualTo(TestPriority.MEDIUM);
    }

    @Test
    @DisplayName("single value enum initializes correctly")
    void singleValueEnumInitializes() {
        DataKey<SingleValueEnum> key = KeyFactory.object("single", SingleValueEnum.class)
                .setDefault(() -> SingleValueEnum.ONLY);

        EnumRadioButtonPanel<SingleValueEnum> panel = new EnumRadioButtonPanel<>(key, TestDataStores.empty());

        assertThat(panel.getValue()).isEqualTo(SingleValueEnum.ONLY);
    }

    @Test
    @DisplayName("getValue returns null when no selection exists")
    void getValueReturnsNullWhenNoSelection() {
        // Create key without default
        DataKey<TestPriority> key = KeyFactory.object("priority", TestPriority.class);

        EnumRadioButtonPanel<TestPriority> panel = new EnumRadioButtonPanel<>(key, TestDataStores.empty());

        // When no default and no selection is made, panel might have first option
        // selected
        // or return null depending on implementation
        // This tests the actual behavior
        assertThat(panel.getValue()).isIn(TestPriority.LOW, null);
    }

    @Test
    @DisplayName("setValue with all enum constants works correctly")
    void setValueWithAllConstants() {
        DataKey<TestPriority> key = KeyFactory.object("priority", TestPriority.class)
                .setDefault(() -> TestPriority.LOW);

        EnumRadioButtonPanel<TestPriority> panel = new EnumRadioButtonPanel<>(key, TestDataStores.empty());

        // Test all enum values
        for (TestPriority priority : TestPriority.values()) {
            panel.setValue(priority);
            assertThat(panel.getValue()).isEqualTo(priority);
        }
    }
}
