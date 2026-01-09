package pt.up.fe.specs.tools.lara.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for LARAExceptionBuilder.
 */
class LARAExceptionBuilderTest {

    private LARAExceptionBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new LARAExceptionBuilder();
    }

    @Test
    void testGetEvaluationExceptionMessage_returnsConstantMessage() {
        // When
        String message = LARAExceptionBuilder.getEvaluationExceptionMessage();
        
        // Then
        assertThat(message).isEqualTo("when evaluating javascript");
    }

    @Test
    void testConstructor_initializesEmptyMessages() {
        // Then
        assertThat(builder.getMessages()).isNotNull();
        assertThat(builder.getMessages()).isEmpty();
    }

    @Test
    void testAdd_singleMessage_addsToMessagesList() {
        // Given
        String message = "Test message";
        
        // When
        builder.add(message);
        
        // Then
        assertThat(builder.getMessages()).hasSize(1);
        assertThat(builder.getMessages()).contains(message);
    }

    @Test
    void testAdd_multipleMessages_addsInOrder() {
        // Given
        String message1 = "First message";
        String message2 = "Second message";
        String message3 = "Third message";
        
        // When
        builder.add(message1);
        builder.add(message2);
        builder.add(message3);
        
        // Then
        assertThat(builder.getMessages()).hasSize(3);
        assertThat(builder.getMessages()).containsExactly(message1, message2, message3);
    }

    @Test
    void testSetMessages_replacesExistingMessages() {
        // Given
        builder.add("Original message");
        List<String> newMessages = new ArrayList<>();
        newMessages.add("New message 1");
        newMessages.add("New message 2");
        
        // When
        builder.setMessages(newMessages);
        
        // Then
        assertThat(builder.getMessages()).hasSize(2);
        assertThat(builder.getMessages()).containsExactly("New message 1", "New message 2");
    }

    @Test
    void testGetMessages_returnsModifiableList() {
        // Given
        builder.add("Test message");
        
        // When
        List<String> messages = builder.getMessages();
        messages.add("Added directly");
        
        // Then
        assertThat(builder.getMessages()).hasSize(2);
        assertThat(builder.getMessages()).contains("Added directly");
    }

    @Test
    void testLastExceptionGettersAndSetters() {
        // Given
        RuntimeException exception = new RuntimeException("Test exception");
        
        // When
        builder.setLastException(exception);
        
        // Then
        assertThat(builder.getLastException()).isSameAs(exception);
    }

    @Test
    void testLastLARAExceptionGettersAndSetters() {
        // Given
        TestBaseException laraException = new TestBaseException("Test LARA exception");
        
        // When
        builder.setLastLARAException(laraException);
        
        // Then
        assertThat(builder.getLastLARAException()).isSameAs(laraException);
    }

    @Test
    void testLastTraceGettersAndSetters() {
        // Given
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        
        // When
        builder.setLastTrace(trace);
        
        // Then
        StackTraceElement[] retrievedTrace = builder.getLastTrace();
        assertThat(retrievedTrace).isNotNull();
        // The trace might be cleaned, so we just check it's not null
    }

    @Test
    void testGetLastTrace_filtersIgnoredPackages() {
        // Given
        StackTraceElement[] originalTrace = new StackTraceElement[] {
            new StackTraceElement("sun.reflect.NativeMethodAccessorImpl", "invoke", "NativeMethodAccessorImpl.java", 62),
            new StackTraceElement("java.lang.reflect.Method", "invoke", "Method.java", 498),
            new StackTraceElement("org.mozilla.javascript.MemberBox", "invoke", "MemberBox.java", 126),
            new StackTraceElement("com.example.MyClass", "myMethod", "MyClass.java", 10),
            new StackTraceElement("java.util.ArrayList", "get", "ArrayList.java", 434)
        };
        builder.setLastTrace(originalTrace);
        
        // When
        StackTraceElement[] cleanedTrace = builder.getLastTrace();
        
        // Then
        assertThat(cleanedTrace).isNotNull();
        assertThat(cleanedTrace.length).isLessThan(originalTrace.length);
        
        // Check that only the non-ignored packages remain
        for (StackTraceElement element : cleanedTrace) {
            assertThat(element.getClassName()).doesNotStartWith("sun.reflect.");
            assertThat(element.getClassName()).doesNotStartWith("java.lang.reflect.");
            assertThat(element.getClassName()).doesNotStartWith("org.mozilla.javascript.");
            assertThat(element.getClassName()).doesNotStartWith("java.util.");
        }
    }

    @Test
    void testGetRuntimeException_withNoMessages_createsExceptionWithNewline() {
        // When
        RuntimeException exception = builder.getRuntimeException();
        
        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).startsWith("\n");
    }

    @Test
    void testGetRuntimeException_withSingleMessage_includesMessage() {
        // Given
        String message = "Test error message";
        builder.add(message);
        
        // When
        RuntimeException exception = builder.getRuntimeException();
        
        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).contains(message);
    }

    @Test
    void testGetRuntimeException_withMultipleMessages_includesAllMessages() {
        // Given
        String message1 = "First error";
        String message2 = "Second error";
        String message3 = "Third error";
        builder.add(message1);
        builder.add(message2);
        builder.add(message3);
        
        // When
        RuntimeException exception = builder.getRuntimeException();
        
        // Then
        assertThat(exception).isNotNull();
        String exceptionMessage = exception.getMessage();
        assertThat(exceptionMessage).contains(message1);
        assertThat(exceptionMessage).contains(message2);
        assertThat(exceptionMessage).contains(message3);
    }

    @Test
    void testGetRuntimeException_withMultipleMessages_usesIndentation() {
        // Given
        String message1 = "First error";
        String message2 = "Second error";
        builder.add(message1);
        builder.add(message2);
        
        // When
        RuntimeException exception = builder.getRuntimeException();
        
        // Then
        assertThat(exception).isNotNull();
        String exceptionMessage = exception.getMessage();
        
        // Check that messages are properly indented
        String[] lines = exceptionMessage.split("\n");
        assertThat(lines.length).isGreaterThanOrEqualTo(2);
        
        // First message should have basic indentation
        assertThat(lines[1]).startsWith(" ");
        
        // Second message should have more indentation
        assertThat(lines[2]).startsWith("  ");
    }

    @Test
    void testGetRuntimeException_withMessageContainingNewlines_handlesCorrectly() {
        // Given
        String messageWithNewlines = "First line\nSecond line\nThird line";
        builder.add(messageWithNewlines);
        
        // When
        RuntimeException exception = builder.getRuntimeException();
        
        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).contains("First line");
        assertThat(exception.getMessage()).contains("Second line");
        assertThat(exception.getMessage()).contains("Third line");
    }

    @Test
    void testGetRuntimeException_withLastException_includesCause() {
        // Given
        RuntimeException cause = new RuntimeException("Original cause");
        builder.setLastException(cause);
        builder.add("LARA error message");
        
        // When
        RuntimeException exception = builder.getRuntimeException();
        
        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getCause()).isSameAs(cause);
    }

    @Test
    void testGetRuntimeException_withExceptionCauseChain_processesAllCauses() {
        // Given
        RuntimeException rootCause = new RuntimeException("Root cause");
        RuntimeException intermediateCause = new RuntimeException("Intermediate cause", rootCause);
        RuntimeException topCause = new RuntimeException("Top cause", intermediateCause);
        
        builder.setLastException(topCause);
        
        // When
        RuntimeException exception = builder.getRuntimeException();
        
        // Then
        assertThat(exception).isNotNull();
        String message = exception.getMessage();
        assertThat(message).contains("Top cause");
        assertThat(message).contains("Intermediate cause");
        assertThat(message).contains("Root cause");
    }

    @Test
    void testGetRuntimeException_withNullLastException_handlesGracefully() {
        // Given
        builder.add("Test message");
        // lastException is null by default
        
        // When
        RuntimeException exception = builder.getRuntimeException();
        
        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).contains("Test message");
    }

    @Test
    void testGetRuntimeException_withEmptyMessage_skipsNullCause() {
        // Given
        RuntimeException causeWithNullMessage = new RuntimeException((String) null);
        builder.setLastException(causeWithNullMessage);
        
        // When
        RuntimeException exception = builder.getRuntimeException();
        
        // Then
        assertThat(exception).isNotNull();
        String message = exception.getMessage();
        assertThat(message).contains("caused by RuntimeException");
    }

    /**
     * Simple concrete implementation of BaseException for testing.
     */
    private static class TestBaseException extends BaseException {
        public TestBaseException(String message) {
            super(new RuntimeException(message));
        }

        @Override
        protected String generateMessage() {
            return "Test exception: " + generateSimpleMessage();
        }
    }
}
