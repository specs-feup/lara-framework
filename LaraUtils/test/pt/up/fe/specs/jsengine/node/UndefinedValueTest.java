package pt.up.fe.specs.jsengine.node;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for the UndefinedValue singleton class.
 */
class UndefinedValueTest {

    @Test
    void testGetUndefined_returnsSameInstance() {
        // Given & When
        UndefinedValue first = UndefinedValue.getUndefined();
        UndefinedValue second = UndefinedValue.getUndefined();
        
        // Then
        assertThat(first).isSameAs(second);
        assertThat(first).isNotNull();
    }

    @Test
    void testGetUndefined_returnsNonNullInstance() {
        // Given & When
        UndefinedValue instance = UndefinedValue.getUndefined();
        
        // Then
        assertThat(instance).isNotNull();
    }

    @Test
    void testSingletonBehavior_multipleCallsReturnSameInstance() {
        // Given
        UndefinedValue[] instances = new UndefinedValue[10];
        
        // When
        for (int i = 0; i < instances.length; i++) {
            instances[i] = UndefinedValue.getUndefined();
        }
        
        // Then
        for (int i = 1; i < instances.length; i++) {
            assertThat(instances[i]).isSameAs(instances[0]);
        }
    }

    @Test
    void testToString_doesNotThrowException() {
        // Given
        UndefinedValue instance = UndefinedValue.getUndefined();
        
        // When & Then
        assertThatCode(instance::toString).doesNotThrowAnyException();
    }

    @Test
    void testEquals_withSameInstance() {
        // Given
        UndefinedValue instance = UndefinedValue.getUndefined();
        
        // When & Then
        assertThat(instance).isEqualTo(instance);
    }

    @Test
    void testHashCode_consistentAcrossCalls() {
        // Given
        UndefinedValue instance = UndefinedValue.getUndefined();
        
        // When
        int hashCode1 = instance.hashCode();
        int hashCode2 = instance.hashCode();
        
        // Then
        assertThat(hashCode1).isEqualTo(hashCode2);
    }
}
