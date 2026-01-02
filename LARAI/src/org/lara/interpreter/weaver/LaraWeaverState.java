package org.lara.interpreter.weaver;

import org.lara.interpreter.joptions.config.interpreter.LaraiKeys;
import org.suikasoft.jOptions.Interfaces.DataStore;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * Base runtime state of a LaraWeaverEngine
 */
public class LaraWeaverState {

    private URLClassLoader classLoader;

    /**
     * @param dataStore DataStore that supports LaraIKeys
     */
    public LaraWeaverState(DataStore dataStore) {
        loadJars(dataStore);
    }

    public URLClassLoader getClassLoader() {
        return classLoader;
    }

    private void loadJars(DataStore dataStore) {
        // Get external jar files
        var jarFiles = getJarFiles(dataStore);

        var urls = jarFiles.stream()
                .map(f -> {
                    try {
                        SpecsLogs.debug(() -> "Loading JAR " + f);
                        return f.toURI().toURL();
                    } catch (MalformedURLException e) {
                        throw new RuntimeException("Could not convert JAR file to URL", e);
                    }
                })
                .toArray(URL[]::new);

        classLoader = new URLClassLoader(urls, getClass().getClassLoader());
    }

    private List<File> getJarFiles(DataStore dataStore) {
        var jarPaths = dataStore.get(LaraiKeys.JAR_PATHS);

        var jarFiles = new ArrayList<File>();

        for (var jarPath : jarPaths) {
            if (!jarPath.exists()) {
                SpecsLogs.info("Jar path '" + jarPath + "' does not exist");
                continue;
            }

            jarFiles.addAll(jarPath.isDirectory() ? SpecsIo.getFilesRecursive(jarPath, "jar") : List.of(jarPath));
        }

        return jarFiles;
    }

    public void close() {
        if (classLoader != null) {
            try {
                classLoader.close();
            } catch (IOException e) {
                throw new RuntimeException("Could not close custom class loader", e);
            }
        }
    }
}
