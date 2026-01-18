/**
 * Copyright 2024 SPeCS.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.lara.language.specification.dsl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link BaseNode} class.
 * 
 * Since BaseNode is abstract, we test it through a concrete implementation.
 * Tests cover:
 * - Tooltip functionality
 * - Default flag functionality
 * - State management
 * - Edge cases and error handling
 * 
 * @author Generated Test Suite
 */
@DisplayName("BaseNode Tests")
class BaseNodeTest {

    /**
     * Concrete implementation of BaseNode for testing purposes.
     */
    private static class TestableBaseNode extends BaseNode {
        // No additional functionality needed for testing BaseNode's methods
    }

    private TestableBaseNode node;

    @BeforeEach
    void setUp() {
        node = new TestableBaseNode();
    }

    @Nested
    @DisplayName("Tooltip Management Tests")
    class TooltipManagementTests {

        @Test
        @DisplayName("Should initialize with empty tooltip")
        void shouldInitializeWithEmptyTooltip() {
            assertThat(node.getToolTip()).isEmpty();
        }

        @Test
        @DisplayName("Should set and get tooltip correctly")
        void shouldSetAndGetTooltipCorrectly() {
            String tooltip = "This is a test tooltip";
            node.setToolTip(tooltip);

            Optional<String> result = node.getToolTip();
            assertThat(result).isPresent().contains(tooltip);
        }

        @Test
        @DisplayName("Should handle null tooltip")
        void shouldHandleNullTooltip() {
            // Set a tooltip first
            node.setToolTip("initial tooltip");
            assertThat(node.getToolTip()).isPresent();

            // Set to null
            node.setToolTip(null);
            assertThat(node.getToolTip()).isEmpty();
        }

        @Test
        @DisplayName("Should handle empty tooltip")
        void shouldHandleEmptyTooltip() {
            node.setToolTip("");
            
            Optional<String> result = node.getToolTip();
            assertThat(result).isPresent().contains("");
        }

        @Test
        @DisplayName("Should handle tooltip replacement")
        void shouldHandleTooltipReplacement() {
            String firstTooltip = "First tooltip";
            String secondTooltip = "Second tooltip";

            node.setToolTip(firstTooltip);
            assertThat(node.getToolTip()).isPresent().contains(firstTooltip);

            node.setToolTip(secondTooltip);
            assertThat(node.getToolTip()).isPresent().contains(secondTooltip);
        }

        @Test
        @DisplayName("Should handle very long tooltips")
        void shouldHandleVeryLongTooltips() {
            String longTooltip = "x".repeat(10000);
            node.setToolTip(longTooltip);

            assertThat(node.getToolTip()).isPresent().contains(longTooltip);
        }

        @Test
        @DisplayName("Should handle tooltips with special characters")
        void shouldHandleTooltipsWithSpecialCharacters() {
            String specialTooltip = "Tooltip with special chars: @#$%^&*()[]{}|;:'\",.<>?/~`";
            node.setToolTip(specialTooltip);

            assertThat(node.getToolTip()).isPresent().contains(specialTooltip);
        }

        @Test
        @DisplayName("Should handle Unicode characters in tooltips")
        void shouldHandleUnicodeCharactersInTooltips() {
            String unicodeTooltip = "Unicode tooltip: 测试文本 🚀 ñáéíóú";
            node.setToolTip(unicodeTooltip);

            assertThat(node.getToolTip()).isPresent().contains(unicodeTooltip);
        }

        @Test
        @DisplayName("Should handle multiline tooltips")
        void shouldHandleMultilineTooltips() {
            String multilineTooltip = "Line 1\nLine 2\nLine 3\n\nLine 5";
            node.setToolTip(multilineTooltip);

            assertThat(node.getToolTip()).isPresent().contains(multilineTooltip);
        }
    }

    @Nested
    @DisplayName("Default Flag Management Tests")
    class DefaultFlagManagementTests {

        @Test
        @DisplayName("Should initialize with default flag as false")
        void shouldInitializeWithDefaultFlagAsFalse() {
            assertThat(node.isDefault()).isFalse();
        }

        @Test
        @DisplayName("Should set and get default flag correctly")
        void shouldSetAndGetDefaultFlagCorrectly() {
            // Set to true
            node.setDefault(true);
            assertThat(node.isDefault()).isTrue();

            // Set to false
            node.setDefault(false);
            assertThat(node.isDefault()).isFalse();
        }

        @Test
        @DisplayName("Should handle repeated flag changes")
        void shouldHandleRepeatedFlagChanges() {
            // Test multiple toggles
            for (int i = 0; i < 100; i++) {
                boolean expectedValue = i % 2 == 0;
                node.setDefault(expectedValue);
                assertThat(node.isDefault()).isEqualTo(expectedValue);
            }
        }

        @Test
        @DisplayName("Should handle setting same value repeatedly")
        void shouldHandleSettingSameValueRepeatedly() {
            // Set true multiple times
            for (int i = 0; i < 10; i++) {
                node.setDefault(true);
                assertThat(node.isDefault()).isTrue();
            }

            // Set false multiple times
            for (int i = 0; i < 10; i++) {
                node.setDefault(false);
                assertThat(node.isDefault()).isFalse();
            }
        }
    }

    @Nested
    @DisplayName("State Independence Tests")
    class StateIndependenceTests {

        @Test
        @DisplayName("Should maintain independent tooltip and default flag states")
        void shouldMaintainIndependentTooltipAndDefaultFlagStates() {
            // Set tooltip without affecting default flag
            node.setToolTip("test tooltip");
            assertThat(node.getToolTip()).isPresent();
            assertThat(node.isDefault()).isFalse(); // Should remain false

            // Set default flag without affecting tooltip
            node.setDefault(true);
            assertThat(node.isDefault()).isTrue();
            assertThat(node.getToolTip()).isPresent().contains("test tooltip"); // Should remain

            // Clear tooltip without affecting default flag
            node.setToolTip(null);
            assertThat(node.getToolTip()).isEmpty();
            assertThat(node.isDefault()).isTrue(); // Should remain true

            // Change default flag without affecting tooltip
            node.setDefault(false);
            assertThat(node.isDefault()).isFalse();
            assertThat(node.getToolTip()).isEmpty(); // Should remain empty
        }

        @Test
        @DisplayName("Should handle all combinations of states")
        void shouldHandleAllCombinationsOfStates() {
            String tooltip = "test tooltip";

            // Test all 4 combinations: (tooltip present/absent) x (default true/false)
            
            // State 1: No tooltip, default false
            node.setToolTip(null);
            node.setDefault(false);
            assertThat(node.getToolTip()).isEmpty();
            assertThat(node.isDefault()).isFalse();

            // State 2: No tooltip, default true
            node.setToolTip(null);
            node.setDefault(true);
            assertThat(node.getToolTip()).isEmpty();
            assertThat(node.isDefault()).isTrue();

            // State 3: Tooltip present, default false
            node.setToolTip(tooltip);
            node.setDefault(false);
            assertThat(node.getToolTip()).isPresent().contains(tooltip);
            assertThat(node.isDefault()).isFalse();

            // State 4: Tooltip present, default true
            node.setToolTip(tooltip);
            node.setDefault(true);
            assertThat(node.getToolTip()).isPresent().contains(tooltip);
            assertThat(node.isDefault()).isTrue();
        }
    }

    @Nested
    @DisplayName("Inheritance and Polymorphism Tests")
    class InheritanceAndPolymorphismTests {

        @Test
        @DisplayName("Should work correctly when accessed through BaseNode reference")
        void shouldWorkCorrectlyWhenAccessedThroughBaseNodeReference() {
            BaseNode baseRef = new TestableBaseNode();

            // Test tooltip functionality through base reference
            baseRef.setToolTip("base tooltip");
            assertThat(baseRef.getToolTip()).isPresent().contains("base tooltip");

            // Test default flag functionality through base reference
            baseRef.setDefault(true);
            assertThat(baseRef.isDefault()).isTrue();
        }

        @Test
        @DisplayName("Should support multiple instances with independent state")
        void shouldSupportMultipleInstancesWithIndependentState() {
            TestableBaseNode node1 = new TestableBaseNode();
            TestableBaseNode node2 = new TestableBaseNode();
            TestableBaseNode node3 = new TestableBaseNode();

            // Set different states for each node
            node1.setToolTip("tooltip1");
            node1.setDefault(true);

            node2.setToolTip("tooltip2");
            node2.setDefault(false);

            node3.setToolTip(null);
            node3.setDefault(true);

            // Verify each node maintains its own state
            assertThat(node1.getToolTip()).isPresent().contains("tooltip1");
            assertThat(node1.isDefault()).isTrue();

            assertThat(node2.getToolTip()).isPresent().contains("tooltip2");
            assertThat(node2.isDefault()).isFalse();

            assertThat(node3.getToolTip()).isEmpty();
            assertThat(node3.isDefault()).isTrue();
        }

        /**
         * Additional concrete implementation to test polymorphism
         */
        private static class AnotherTestableBaseNode extends BaseNode {
            // Different implementation for testing
        }

        @Test
        @DisplayName("Should work with different concrete implementations")
        void shouldWorkWithDifferentConcreteImplementations() {
            BaseNode node1 = new TestableBaseNode();
            BaseNode node2 = new AnotherTestableBaseNode();

            // Both should have the same BaseNode functionality
            node1.setToolTip("tooltip1");
            node1.setDefault(true);

            node2.setToolTip("tooltip2");
            node2.setDefault(false);

            assertThat(node1.getToolTip()).isPresent().contains("tooltip1");
            assertThat(node1.isDefault()).isTrue();

            assertThat(node2.getToolTip()).isPresent().contains("tooltip2");
            assertThat(node2.isDefault()).isFalse();
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class EdgeCasesAndErrorHandlingTests {

        @Test
        @DisplayName("Should handle rapid state changes")
        void shouldHandleRapidStateChanges() {
            // Rapid tooltip changes
            for (int i = 0; i < 1000; i++) {
                String tooltip = "tooltip" + i;
                node.setToolTip(tooltip);
                assertThat(node.getToolTip()).isPresent().contains(tooltip);
            }

            // Rapid default flag changes
            for (int i = 0; i < 1000; i++) {
                boolean defaultValue = i % 2 == 0;
                node.setDefault(defaultValue);
                assertThat(node.isDefault()).isEqualTo(defaultValue);
            }
        }

        @Test
        @DisplayName("Should handle extreme tooltip lengths")
        void shouldHandleExtremeTooltipLengths() {
            // Very long tooltip
            String longTooltip = "a".repeat(100000);
            node.setToolTip(longTooltip);
            assertThat(node.getToolTip()).isPresent().contains(longTooltip);

            // Very short tooltip
            String shortTooltip = "a";
            node.setToolTip(shortTooltip);
            assertThat(node.getToolTip()).isPresent().contains(shortTooltip);
        }

        @Test
        @DisplayName("Should handle whitespace-only tooltips")
        void shouldHandleWhitespaceOnlyTooltips() {
            String whitespaceTooltip = "   \t\n\r   ";
            node.setToolTip(whitespaceTooltip);
            assertThat(node.getToolTip()).isPresent().contains(whitespaceTooltip);
        }

        @Test
        @DisplayName("Should maintain state after multiple method calls")
        void shouldMaintainStateAfterMultipleMethodCalls() {
            String tooltip = "persistent tooltip";
            node.setToolTip(tooltip);
            node.setDefault(true);

            // Multiple getter calls should not affect state
            for (int i = 0; i < 100; i++) {
                assertThat(node.getToolTip()).isPresent().contains(tooltip);
                assertThat(node.isDefault()).isTrue();
            }

            // State should still be preserved
            assertThat(node.getToolTip()).isPresent().contains(tooltip);
            assertThat(node.isDefault()).isTrue();
        }

        @Test
        @DisplayName("Should handle concurrent-like access patterns")
        void shouldHandleConcurrentLikeAccessPatterns() {
            // Simulate rapid alternating access to different properties
            for (int i = 0; i < 1000; i++) {
                if (i % 4 == 0) {
                    node.setToolTip("tooltip" + i);
                } else if (i % 4 == 1) {
                    node.setDefault(i % 8 < 4);
                } else if (i % 4 == 2) {
                    Optional<String> tooltip = node.getToolTip();
                    assertThat(tooltip).isNotNull(); // Just verify it doesn't crash
                } else {
                    boolean isDefault = node.isDefault();
                    // Just verify it doesn't crash and returns a valid boolean
                    assertThat(isDefault).isIn(true, false);
                }
            }
        }
    }
}
