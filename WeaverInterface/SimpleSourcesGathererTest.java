import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.lara.interpreter.weaver.utils.SourcesGatherer;

/**
 * Simple validation test for SourcesGatherer
 */
public class SimpleSourcesGathererTest {
    
    public static void main(String[] args) throws IOException {
        System.out.println("Running simple SourcesGatherer validation tests...");
        
        // Create a temporary directory for testing
        Path tempDir = Files.createTempDirectory("sources-test");
        File tempDirFile = tempDir.toFile();
        
        try {
            // Test 1: Empty inputs
            testEmptyInputs();
            
            // Test 2: Single file discovery
            testSingleFileDiscovery(tempDirFile);
            
            // Test 3: Directory traversal
            testDirectoryTraversal(tempDirFile);
            
            // Test 4: Invalid paths (should not throw)
            testInvalidPaths(tempDirFile);
            
            System.out.println("All basic validation tests passed!");
            
        } finally {
            // Cleanup temp directory
            deleteRecursively(tempDir);
        }
    }
    
    private static void testEmptyInputs() {
        System.out.print("Testing empty inputs... ");
        SourcesGatherer gatherer = SourcesGatherer.build(Collections.emptyList(), Arrays.asList("java"));
        assert gatherer.getSourceFiles().isEmpty() : "Empty inputs should result in empty mapping";
        System.out.println("✓");
    }
    
    private static void testSingleFileDiscovery(File tempDir) throws IOException {
        System.out.print("Testing single file discovery... ");
        
        // Create test files
        File javaFile = new File(tempDir, "Test.java");
        Files.writeString(javaFile.toPath(), "public class Test {}");
        
        File txtFile = new File(tempDir, "readme.txt");
        Files.writeString(txtFile.toPath(), "readme");
        
        // Test with java extension only
        List<File> sources = Arrays.asList(javaFile, txtFile);
        SourcesGatherer gatherer = SourcesGatherer.build(sources, Arrays.asList("java"));
        
        Map<File, File> results = gatherer.getSourceFiles();
        assert results.size() == 1 : "Should find exactly 1 java file, found " + results.size();
        assert results.containsKey(javaFile) : "Should contain java file";
        assert !results.containsKey(txtFile) : "Should not contain txt file";
        
        System.out.println("✓");
    }
    
    private static void testDirectoryTraversal(File tempDir) throws IOException {
        System.out.print("Testing directory traversal... ");
        
        // Create subdirectory structure
        File subDir = new File(tempDir, "subdir");
        subDir.mkdirs();
        
        File javaFile = new File(subDir, "Test.java");
        Files.writeString(javaFile.toPath(), "public class Test {}");
        
        // Test directory as source
        SourcesGatherer gatherer = SourcesGatherer.build(Arrays.asList(tempDir), Arrays.asList("java"));
        
        Map<File, File> results = gatherer.getSourceFiles();
        assert !results.isEmpty() : "Should find files in subdirectory";
        
        boolean foundJavaFile = false;
        for (File foundFile : results.keySet()) {
            if (foundFile.getName().equals("Test.java")) {
                foundJavaFile = true;
                break;
            }
        }
        assert foundJavaFile : "Should find java file in subdirectory";
        
        System.out.println("✓");
    }
    
    private static void testInvalidPaths(File tempDir) {
        System.out.print("Testing invalid paths... ");
        
        File nonExistent = new File(tempDir, "nonexistent.java");
        
        // Should not throw exception
        try {
            SourcesGatherer gatherer = SourcesGatherer.build(Arrays.asList(nonExistent), Arrays.asList("java"));
            // Should succeed without throwing
            System.out.println("✓");
        } catch (Exception e) {
            throw new RuntimeException("Should not throw exception for invalid paths", e);
        }
    }
    
    private static void deleteRecursively(Path path) throws IOException {
        Files.walk(path)
            .sorted((a, b) -> -a.compareTo(b)) // reverse order to delete children first
            .forEach(p -> {
                try {
                    Files.delete(p);
                } catch (IOException e) {
                    // ignore cleanup errors
                }
            });
    }
}