package pt.up.fe.specs.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Mock implementation of SpecsIo for testing purposes
 */
public class SpecsIo {
    
    public static String getExtension(File file) {
        String name = file.getName();
        int lastDot = name.lastIndexOf('.');
        if (lastDot == -1 || lastDot == name.length() - 1) {
            return "";
        }
        return name.substring(lastDot + 1);
    }
    
    public static List<File> getFilesRecursive(File directory, Collection<String> extensions) {
        List<File> result = new ArrayList<>();
        if (directory == null || !directory.exists() || !directory.isDirectory()) {
            return result;
        }
        
        collectFilesRecursive(directory, extensions, result);
        return result;
    }
    
    private static void collectFilesRecursive(File directory, Collection<String> extensions, List<File> result) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        
        for (File file : files) {
            if (file.isFile()) {
                String ext = getExtension(file);
                if (extensions.contains(ext)) {
                    result.add(file);
                }
            } else if (file.isDirectory()) {
                collectFilesRecursive(file, extensions, result);
            }
        }
    }
}