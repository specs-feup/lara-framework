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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.JFileChooser;

import org.lara.interpreter.joptions.keys.FileList;
import org.lara.interpreter.joptions.keys.OptionalFile;
import org.suikasoft.jOptions.JOptionKeys;
import org.suikasoft.jOptions.Datakey.CustomGetter;
import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Datakey.KeyFactory;
import org.suikasoft.jOptions.Interfaces.DataStore;
import org.suikasoft.jOptions.gui.panels.option.FilePanel;

import pt.up.fe.specs.util.SpecsIo;

public class LaraIKeyFactory {

    public static DataKey<FileList> folderList(String id) {
        return fileList(id, JFileChooser.DIRECTORIES_ONLY, Collections.emptyList());
    }

    /**
     * Default file selection is {@link JFileChooser#FILES_ONLY}.
     *
     */
    public static DataKey<FileList> fileList(String id, Collection<String> extensions) {
        return fileList(id, JFileChooser.FILES_ONLY, extensions);
    }

    /**
     *
     * @param selectionMode file selection mode, using constants:
     *                      {@link JFileChooser#FILES_AND_DIRECTORIES},
     *                      {@link JFileChooser#FILES_ONLY} or
     *                      {@link JFileChooser#DIRECTORIES_ONLY}
     */
    public static DataKey<FileList> fileList(String id, int selectionMode, Collection<String> extensions) {
        Optional<Boolean> isFolder = isFolder(selectionMode);

        DataKey<FileList> fileListKey = KeyFactory.object(id, FileList.class)
                .setDecoder(FileList::newInstance)
                .setDefault(FileList::newInstance);

        if (isFolder.isPresent()) {
            fileListKey = fileListKey.setCustomGetter(customGetterFileList(isFolder.get(), !isFolder.get(), false));
        } else {
            fileListKey = fileListKey.setCustomGetter(customGetterFileList(true, true, false));
        }

        //

        return fileListKey;
    }

    public static CustomGetter<FileList> customGetterFileList(boolean isFolder, boolean isFile, boolean create) {
        return (fileList, dataStore) -> {

            List<File> processedFiles = fileList.getFiles().stream()
                    // In the option 'exists', using 'false' since this is a new option and this way
                    // the behaviour is the same
                    .map(file -> KeyFactory.customGetterFile(isFolder, isFile, create, false).get(file, dataStore))
                    .collect(Collectors.toList());

            return FileList.newInstance(processedFiles);
        };
    }

    private static Optional<Boolean> isFolder(int selectionMode) {
        return switch (selectionMode) {
            case JFileChooser.FILES_ONLY -> Optional.of(false);
            case JFileChooser.DIRECTORIES_ONLY -> Optional.of(true);
            default -> Optional.empty();
        };

    }

    /**
     * Based on {@link KeyFactory#file(String, String...)}
     *
     *
     * @param selectionMode file selection mode, using constants:
     *                      {@link JFileChooser#FILES_AND_DIRECTORIES},
     *                      {@link JFileChooser#FILES_ONLY} or
     *                      {@link JFileChooser#DIRECTORIES_ONLY}
     */
    public static DataKey<File> file(String id, int selectionMode, boolean create, Collection<String> extensions) {

        return KeyFactory.object(id, File.class)
                .setDefault(() -> new File(""))
                .setDecoder(s -> new File(Objects.requireNonNullElse(s, "")))
                .setKeyPanelProvider((key, data) -> new FilePanel(key, data, selectionMode, extensions))
                .setCustomGetter(customGetterFile(selectionMode, create));
    }

    /**
     * Based on {@link KeyFactory#customGetterFile(boolean, boolean)}
     *
     */
    private static CustomGetter<File> customGetterFile(int selectionMode, boolean create) {
        return (file, dataStore) -> {
            // If an empty path, return an empty path
            if (file.getPath().isEmpty() && selectionMode == JFileChooser.FILES_ONLY && !create) {
                // System.out.println("RETURN 0:" + file);
                return file;
            }

            File currentFile = JOptionKeys.getContextPath(file, dataStore);

            currentFile = processPath(selectionMode != JFileChooser.FILES_ONLY, create, currentFile);

            // If relative paths is enabled, make relative path with working folder.
            Optional<String> workingFolder = dataStore.get(JOptionKeys.CURRENT_FOLDER_PATH);
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

    /**
     * A File DataKey, with default value file with current path (.).
     *
     * <p>
     * If 'isFolder' is true, it will try to create the folder when returning the
     * File instance, even if it does not
     * exist.
     *
     */
    public static DataKey<OptionalFile> optionalFile(String id, boolean exists) {
        return optionalFile(id, false, false, exists, Collections.emptyList());
    }

    public static DataKey<OptionalFile> optionalFolder(String id, boolean exists) {

        return optionalFile(id, true, false, exists, Collections.emptyList());
    }

    public static DataKey<OptionalFile> optionalFile(String id, boolean exists, String... data) {
        return optionalFile(id, false, false, exists, Arrays.asList(data));
    }

    public static DataKey<OptionalFile> optionalFile(String id, boolean isFolder, boolean create, boolean exists,
            Collection<String> extensions) {
        return KeyFactory.object(id, OptionalFile.class)
                .setDecoder(OptionalFile.getCodec())
                .setDefault(() -> OptionalFile.newInstance(null))
                .setCustomGetter((optFile, dataStore) -> {
                    File file = optFile.getFile();
                    if (file != null) {
                        file = KeyFactory.customGetterFile(isFolder, !isFolder, create, false).get(file, dataStore);
                        optFile.setFile(file);
                    }
                    return optFile;
                });
    }

    public static String customGetterLaraArgs(String args, DataStore dataStore) {
        return args.strip();
    }

}
