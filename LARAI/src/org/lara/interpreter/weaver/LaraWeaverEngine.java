/**
 * Copyright 2023 SPeCS.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

package org.lara.interpreter.weaver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.lara.interpreter.joptions.config.interpreter.LaraiKeys;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.suikasoft.jOptions.Interfaces.DataStore;

import pt.up.fe.specs.util.SpecsIo;

public abstract class LaraWeaverEngine extends WeaverEngine {
    private LaraWeaverState state;
    private boolean hasRun;

    public LaraWeaverEngine() {
        state = null;
        hasRun = false;
    }

    @Override
    public boolean run(DataStore dataStore) {
        File outputDir = dataStore.get(LaraiKeys.OUTPUT_FOLDER);
        List<File> sources = dataStore.get(LaraiKeys.WORKSPACE_FOLDER).getFiles();

        return run(sources, outputDir, dataStore);
    }

    private boolean run(List<File> sources, File outputDir, DataStore dataStore) {
        hasRun = true;

        // Initialize state
        state = new LaraWeaverState(outputDir, dataStore);

        return begin(sources, outputDir, dataStore);
    }

    /**
     * After the weaver has run at least once, can restart the weaver with the given files, outputdir and options.
     *
     * @param sources
     * @param outputDir
     * @param dataStore
     * @return
     */
    public boolean restart(List<File> sources, File outputDir, DataStore dataStore) {
        if (!hasRun) {
            return run(sources, outputDir, dataStore);
        }

        // Re-initialize state
        state.close();
        state = new LaraWeaverState(outputDir, dataStore);

        // Close weaver
        close();

        return begin(sources, outputDir, dataStore);
    }

    /**
     * Overload that receives only the sources.
     *
     * @param sources
     * @return
     */
    public boolean restart(List<File> sources) {
        return restart(sources, getLaraWeaverState().getOutputDir(), getLaraWeaverState().getData());
    }

    /**
     * Overload that creates temporary files, to ease integration with JS.
     *
     * @param filenames
     * @param codes
     * @return
     */
    public boolean restart(String[] filenames, String[] codes) {

        if (filenames.length != codes.length) {
            throw new RuntimeException("Expected length of filenames and codes to be the same: " + filenames.length + " vs " + codes.length);
        }

        // Create files in temporary folder
        var files = new ArrayList<File>();
        var tempFolder = SpecsIo.getTempFolder();
        for (int i = 0; i < filenames.length; i++) {
            var tempFile = new File(tempFolder, filenames[i]);
            SpecsIo.write(tempFile, codes[i]);
            tempFile.deleteOnExit();
        }

        // Call restart
        return restart(files);
    }

    /**
     * TODO: Needs similar treatement as begin()/run()
     */
    public void closeTop() {
        state.close();
    }

    @Override
    public Optional<DataStore> getData() {
        if (state == null) {
            return Optional.empty();
        }

        return Optional.of(state.getData());
    }

    public Optional<LaraWeaverState> getLaraWeaverStateTry() {
        return Optional.ofNullable(state);
    }

    public LaraWeaverState getLaraWeaverState() {
        return getLaraWeaverStateTry().orElseThrow(() -> new RuntimeException("No LARA weaver state defined"));
    }

    /**
     * This method will be called at the end of method run()
     *
     * @param sources   the file/directory with the source code
     * @param outputDir output directory for the generated file(s)
     * @param dataStore the dataStore containing the options for the weaver
     * @return true if executed without errors
     */
    public abstract boolean begin(List<File> sources, File outputDir, DataStore dataStore);

    /**
     * @param name
     * @return
     */
    public Class<?> getClass(String name) {
        try {
            return getLaraWeaverState().getClassLoader().loadClass(name);
            // return classLoader.loadClass(name);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Could not find class", e);
        }
    }

}
