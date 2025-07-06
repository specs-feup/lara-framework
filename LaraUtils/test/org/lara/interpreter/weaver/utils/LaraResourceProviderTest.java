package org.lara.interpreter.weaver.utils;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for LaraResourceProvider interface.
 */
class LaraResourceProviderTest {

    @Test
    void testGetDefaultSeparatorChar_returnsPercent() {
        // When
        char defaultSeparator = LaraResourceProvider.getDefaultSeparatorChar();
        
        // Then
        assertThat(defaultSeparator).isEqualTo('%');
    }

    @Test
    void testGetSeparatorChar_defaultImplementation_returnsDefaultSeparator() {
        // Given
        TestLaraResourceProvider provider = new TestLaraResourceProvider("test");
        
        // When
        char separatorChar = provider.getSeparatorChar();
        
        // Then
        assertThat(separatorChar).isEqualTo(LaraResourceProvider.getDefaultSeparatorChar());
    }

    @Test
    void testGetSeparatorChar_customImplementation_returnsCustomSeparator() {
        // Given
        char customSeparator = '#';
        TestLaraResourceProvider provider = new TestLaraResourceProvider("test", customSeparator);
        
        // When
        char separatorChar = provider.getSeparatorChar();
        
        // Then
        assertThat(separatorChar).isEqualTo(customSeparator);
    }

    @Test
    void testGetResource_noSeparator_returnsOriginalResource() {
        // Given
        String originalResource = "test-resource";
        TestLaraResourceProvider provider = new TestLaraResourceProvider(originalResource);
        
        // When
        String resource = provider.getResource();
        
        // Then
        assertThat(resource).isEqualTo(originalResource);
    }

    @Test
    void testGetResource_withSeparator_removesSeparatorChar() {
        // Given
        String originalResource = "test%resource";
        TestLaraResourceProvider provider = new TestLaraResourceProvider(originalResource);
        
        // When
        String resource = provider.getResource();
        
        // Then
        assertThat(resource).isEqualTo("testresource");
    }

    @Test
    void testGetResource_withSeparatorAtBeginning_removesFirstChar() {
        // Given
        String originalResource = "%resource";
        TestLaraResourceProvider provider = new TestLaraResourceProvider(originalResource);
        
        // When
        String resource = provider.getResource();
        
        // Then
        assertThat(resource).isEqualTo("resource");
    }

    @Test
    void testGetResource_withSeparatorAtEnd_removesLastChar() {
        // Given
        String originalResource = "resource%";
        TestLaraResourceProvider provider = new TestLaraResourceProvider(originalResource);
        
        // When
        String resource = provider.getResource();
        
        // Then
        assertThat(resource).isEqualTo("resource");
    }

    @Test
    void testGetResource_withMultipleSeparators_removesFirstOccurrence() {
        // Given
        String originalResource = "test%resource%more";
        TestLaraResourceProvider provider = new TestLaraResourceProvider(originalResource);
        
        // When
        String resource = provider.getResource();
        
        // Then
        assertThat(resource).isEqualTo("testresource%more");
    }

    @Test
    void testGetResource_withCustomSeparator_removesCustomSeparator() {
        // Given
        String originalResource = "test#resource";
        char customSeparator = '#';
        TestLaraResourceProvider provider = new TestLaraResourceProvider(originalResource, customSeparator);
        
        // When
        String resource = provider.getResource();
        
        // Then
        assertThat(resource).isEqualTo("testresource");
    }

    @Test
    void testGetResource_emptyString_returnsEmpty() {
        // Given
        TestLaraResourceProvider provider = new TestLaraResourceProvider("");
        
        // When
        String resource = provider.getResource();
        
        // Then
        assertThat(resource).isEmpty();
    }

    @Test
    void testGetFileLocation_noSeparator_returnsOriginalResource() {
        // Given
        String originalResource = "test-resource";
        TestLaraResourceProvider provider = new TestLaraResourceProvider(originalResource);
        
        // When
        String fileLocation = provider.getFileLocation();
        
        // Then
        assertThat(fileLocation).isEqualTo(originalResource);
    }

    @Test
    void testGetFileLocation_withSeparator_returnsPartAfterSeparator() {
        // Given
        String originalResource = "test%resource";
        TestLaraResourceProvider provider = new TestLaraResourceProvider(originalResource);
        
        // When
        String fileLocation = provider.getFileLocation();
        
        // Then
        assertThat(fileLocation).isEqualTo("resource");
    }

    @Test
    void testGetFileLocation_withSeparatorAtBeginning_returnsEverythingAfter() {
        // Given
        String originalResource = "%resource";
        TestLaraResourceProvider provider = new TestLaraResourceProvider(originalResource);
        
        // When
        String fileLocation = provider.getFileLocation();
        
        // Then
        assertThat(fileLocation).isEqualTo("resource");
    }

    @Test
    void testGetFileLocation_withSeparatorAtEnd_returnsEmpty() {
        // Given
        String originalResource = "resource%";
        TestLaraResourceProvider provider = new TestLaraResourceProvider(originalResource);
        
        // When
        String fileLocation = provider.getFileLocation();
        
        // Then
        assertThat(fileLocation).isEmpty();
    }

    @Test
    void testGetFileLocation_withMultipleSeparators_returnsAfterFirst() {
        // Given
        String originalResource = "test%resource%more";
        TestLaraResourceProvider provider = new TestLaraResourceProvider(originalResource);
        
        // When
        String fileLocation = provider.getFileLocation();
        
        // Then
        assertThat(fileLocation).isEqualTo("resource%more");
    }

    @Test
    void testGetFileLocation_withCustomSeparator_usesCustomSeparator() {
        // Given
        String originalResource = "test#resource";
        char customSeparator = '#';
        TestLaraResourceProvider provider = new TestLaraResourceProvider(originalResource, customSeparator);
        
        // When
        String fileLocation = provider.getFileLocation();
        
        // Then
        assertThat(fileLocation).isEqualTo("resource");
    }

    @Test
    void testGetFileLocation_emptyString_returnsEmpty() {
        // Given
        TestLaraResourceProvider provider = new TestLaraResourceProvider("");
        
        // When
        String fileLocation = provider.getFileLocation();
        
        // Then
        assertThat(fileLocation).isEmpty();
    }

    @Test
    void testConsistency_getResourceAndGetFileLocation_complement() {
        // Given
        String originalResource = "prefix%suffix";
        TestLaraResourceProvider provider = new TestLaraResourceProvider(originalResource);
        
        // When
        String resource = provider.getResource();
        String fileLocation = provider.getFileLocation();
        
        // Then
        assertThat(resource).isEqualTo("prefixsuffix");
        assertThat(fileLocation).isEqualTo("suffix");
    }
}
