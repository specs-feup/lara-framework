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
import org.lara.interpreter.joptions.keys.FileList;
import org.lara.interpreter.weaver.events.EventTrigger;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.suikasoft.jOptions.Interfaces.DataStore;

import pt.up.fe.specs.util.SpecsIo;

public abstract class LaraWeaverEngine extends WeaverEngine {
    private LaraWeaverState state;
    protected DataStore dataStore;

    public LaraWeaverEngine() {
        state = null;
        dataStore = null;
    }

    @Override
    public boolean run(DataStore dataStore) {
        // FIXME: This should not be necessary, as run/begin() already receives the sources list. We have 2 sources of truth.
        List<File> sources = dataStore.get(LaraiKeys.WORKSPACE_FOLDER).getFiles();
        File outputDir = dataStore.get(LaraiKeys.OUTPUT_FOLDER);

        return run(sources, outputDir, dataStore);
    }

    /**
     * Overload that creates temporary files, to ease integration with JS.
     *
     */
    public boolean run(String[] filenames, String[] codes, DataStore dataStore) {

        if (filenames.length != codes.length) {
            throw new RuntimeException("Expected length of filenames and codes to be the same: " + filenames.length
                    + " vs " + codes.length);
        }

        // Create files in temporary folder
        var files = new ArrayList<File>();
        var tempFolder = SpecsIo.getTempFolder();
        for (int i = 0; i < filenames.length; i++) {
            var tempFile = new File(tempFolder, filenames[i]);
            SpecsIo.write(tempFile, codes[i]);
            tempFile.deleteOnExit();
            files.add(tempFile);
        }

        dataStore.set(LaraiKeys.WORKSPACE_FOLDER, FileList.newInstance(files));

        return run(dataStore);
    }

    private boolean run(List<File> sources, File outputDir, DataStore dataStore) {
        // Initialize state
        if (state != null) {
            state.close(); // Close previous state
        }
        state = new LaraWeaverState(dataStore);
        setData(dataStore);

        var eventTrigger = new EventTrigger();
        eventTrigger.registerReceivers(getGears());
        setEventTrigger(eventTrigger);

        return begin(sources, outputDir, dataStore);
    }

    /**
     * Closes the weaver and ends execution.
     */
    public boolean end() {
        if (state != null) {
            state.close();
        }

        // Close weaver
        return close();
    }

    @Override
    public Optional<DataStore> getData() {
        return Optional.ofNullable(dataStore);
    }

    public LaraWeaverEngine setData(DataStore dataStore) {
        this.dataStore = dataStore;
        return this;
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
    protected abstract boolean begin(List<File> sources, File outputDir, DataStore dataStore);

    /**
     * Closes the weaver and specifies the output directory location if the weaver generates new file(s)
     *
     * @return if close was successful
     */
    protected abstract boolean close();

    public Class<?> getClass(String name) {
        try {
            return getLaraWeaverState().getClassLoader().loadClass(name);
            // return classLoader.loadClass(name);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Could not find class", e);
        }
    }

    public static LaraWeaverEngine getThreadLocalWeaver() {
        return (LaraWeaverEngine) WeaverEngine.getThreadLocalWeaver();
    }
}
