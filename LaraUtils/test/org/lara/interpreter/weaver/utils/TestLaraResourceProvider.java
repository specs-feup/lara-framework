package org.lara.interpreter.weaver.utils;

/**
 * Simple concrete implementation of LaraResourceProvider for testing purposes.
 */
public class TestLaraResourceProvider implements LaraResourceProvider {
    
    private final String originalResource;
    private final char separatorChar;
    
    public TestLaraResourceProvider(String originalResource) {
        this(originalResource, LaraResourceProvider.getDefaultSeparatorChar());
    }
    
    public TestLaraResourceProvider(String originalResource, char separatorChar) {
        this.originalResource = originalResource;
        this.separatorChar = separatorChar;
    }
    
    @Override
    public String getOriginalResource() {
        return originalResource;
    }
    
    @Override
    public char getSeparatorChar() {
        return separatorChar;
    }
}
