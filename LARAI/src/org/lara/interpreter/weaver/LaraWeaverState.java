package org.lara.interpreter.weaver;

import org.lara.interpreter.joptions.config.interpreter.LaraiKeys;
import org.suikasoft.jOptions.Interfaces.DataStore;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * Base runtime state of a LaraWeaverEngine
 */
public class LaraWeaverState {

    private final DataStore data;

    private URLClassLoader classLoader;

    /**
     *
     * @param dataStore DataStore that supports LaraIKeys
     */
    public LaraWeaverState(DataStore dataStore) {
        this.data = dataStore;

        // Load JARs to classloader
        loadJars();
    }

    public URLClassLoader getClassLoader() {
        return classLoader;
    }

    private void loadJars() {
        // Get external jar files
        var jarFiles = getJarFiles();

        var urls = jarFiles.stream()
                .map(f -> {
                    try {
                        System.out.println("Loading JAR " + f);
                        return f.toURI().toURL();
                    } catch (MalformedURLException e) {
                        throw new RuntimeException("Could not convert JAR file to URL", e);
                    }
                })
                .toArray(s -> new URL[s]);

        classLoader = new URLClassLoader(urls, getClass().getClassLoader()
        );
    }

    private List<File> getJarFiles() {
        var jarPaths = data.get(LaraiKeys.JAR_PATHS);

        var jarFiles = new ArrayList<File>();

        for(var jarPath : jarPaths) {
            if(!jarPath.exists()) {
                SpecsLogs.info("Jar path '"+jarPath+"' does not exist");
                continue;
            }

            jarFiles.addAll(jarPath.isDirectory() ? SpecsIo.getFilesRecursive(jarPath, "jar") : List.of(jarPath));
        }

        return jarFiles;
    }

}
