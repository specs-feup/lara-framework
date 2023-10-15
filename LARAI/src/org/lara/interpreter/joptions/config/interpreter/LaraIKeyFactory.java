/**
 * Copyright 2016 SPeCS.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

package org.lara.interpreter.joptions.config.interpreter;

import java.io.File;
import java.util.Collection;
import java.util.Optional;

import javax.swing.JFileChooser;

import org.suikasoft.jOptions.JOptionKeys;
import org.suikasoft.jOptions.Datakey.CustomGetter;
import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Datakey.KeyFactory;
import org.suikasoft.jOptions.Interfaces.DataStore;
import org.suikasoft.jOptions.gui.panels.option.FilePanel;

import com.google.gson.Gson;

import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.properties.SpecsProperties;

public class LaraIKeyFactory {


    /**
     * Based on {@link KeyFactory#file(String, String...)}
     *
     *
     * @param id
     * @param selectionMode
     *            file selection mode, using constants: {@link JFileChooser#FILES_AND_DIRECTORIES},
     *            {@link JFileChooser#FILES_ONLY} or {@link JFileChooser#DIRECTORIES_ONLY}
     * @param create
     * @param extensions
     * @return
     */
    public static DataKey<File> file(String id, int selectionMode, boolean create, Collection<String> extensions) {

        return KeyFactory.object(id, File.class)
                .setDefault(() -> new File(""))
                .setDecoder(s -> {
                    if (s == null) {
                        return new File("");
                    }
                    return new File(s);
                })
                .setKeyPanelProvider((key, data) -> new FilePanel(key, data, selectionMode, extensions))
                .setCustomGetter(customGetterFile(selectionMode, create));
    }

    /**
     * Based on {@link KeyFactory#customGetterFile(boolean, boolean)}
     *
     * @param selectionMode
     * @param create
     * @return
     */
    private static CustomGetter<File> customGetterFile(int selectionMode, boolean create) {
        return (file, dataStore) -> {
            if (file.getPath().isEmpty() && selectionMode == JFileChooser.FILES_ONLY && !create) {
                return file;
            }

            File currentFile = file;

            // If it has a working folder set
            Optional<String> workingFolder = dataStore.get(JOptionKeys.CURRENT_FOLDER_PATH);
            if (workingFolder.isPresent()) {
                // If path is not absolute, create new file with working folder as parent

                if (!currentFile.isAbsolute()) {
                    File parentFolder = new File(workingFolder.get());
                    currentFile = new File(parentFolder, currentFile.getPath());
                }

            }

            currentFile = processPath(selectionMode != JFileChooser.FILES_ONLY, create, currentFile);

            // If relative paths is enabled, make relative path with working folder.
            if (workingFolder.isPresent() && dataStore.get(JOptionKeys.USE_RELATIVE_PATHS)) {
                currentFile = new File(SpecsIo.getRelativePath(currentFile, new File(workingFolder.get())));
            }

            if (!dataStore.get(JOptionKeys.USE_RELATIVE_PATHS) && workingFolder.isPresent()) {
                currentFile = SpecsIo.getCanonicalFile(currentFile);
            }

            return currentFile;
        };
    }

    private static File processPath(boolean isFolder, boolean create, File currentFile) {
        if (isFolder && create) {
            return SpecsIo.mkdir(currentFile);
        }
        return currentFile;
    }

    public static String customGetterLaraArgs(String args, DataStore dataStore) {
        String finalArgs = args;
        String trimmedArgs = args.strip();

        if (trimmedArgs.isEmpty()) {
            return args;
        }

        // Check if args is an existing JSON or properties file
        if (!trimmedArgs.startsWith("{")) {
            var file = new File(trimmedArgs);
            if (SpecsIo.getExtension(file).equals("json")) {
                if (!file.isFile()) {
                    throw new RuntimeException(
                            "Passed a JSON file '" + file + "' as aspect arguments, but file does not exist.");
                }

                return SpecsIo.read(file);
            } else if (SpecsIo.getExtension(file).equals("properties")) {
                if (!file.isFile()) {
                    throw new RuntimeException(
                            "Passed a properties file '" + file + "' as aspect arguments, but file does not exist.");
                }

                return SpecsProperties.newInstance(file).toJson();
            }
        }

        // Fix curly braces
        if (!trimmedArgs.startsWith("{")) {
            finalArgs = "{" + finalArgs;
        }

        if (!trimmedArgs.endsWith("}")) {
            finalArgs = finalArgs + "}";
        }

        // Sanitize
        var gson = new Gson();
        try {
            return gson.toJson(gson.fromJson(finalArgs, Object.class));
        } catch (Exception e) {
            throw new RuntimeException("Passed invalid JSON as argument: '" + args + "'", e);
        }
    }

}
