/*
 * Copyright 2013 SPeCS.
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
import java.util.Optional;

import org.lara.interpreter.exception.LaraIException;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.lara.interpreter.weaver.options.WeaverOption;
import org.suikasoft.jOptions.JOptionsUtils;
import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Interfaces.DataStore;

import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;

/**
 * TODO: Should deprecate and just use DataStore directly? TODO: Also, the "ifs" in the getters interfere with the
 * default values set in the DataKey
 * 
 * @author JoaoBispo
 *
 */
public class LaraIDataStore implements LaraiKeys {

    // private static final String GIT_QUERY_FOLDER = "folder";
    // private static final Set<String> GIT_URL_QUERIES = new HashSet<>(Arrays.asList(GIT_QUERY_FOLDER));

    public static final String CONFIG_FILE_NAME = "larai.properties";
    private static final String SYSTEM_OPTIONS_FILENAME = "system_options.xml";

    public static String getConfigFileName() {
        return CONFIG_FILE_NAME;
    }

    public static String getSystemOptionsFilename() {
        return SYSTEM_OPTIONS_FILENAME;
    }

    private final DataStore dataStore;

    public LaraIDataStore(DataStore dataStore, WeaverEngine weaverEngine) {

        // Merge system-wise options with local options
        var mergedDataStore = mergeSystemAndLocalOptions(weaverEngine, dataStore);

        // this.dataStore = dataStore;
        this.dataStore = mergedDataStore;

        for (WeaverOption option : weaverEngine.getOptions()) {
            DataKey<?> key = option.dataKey();
            Optional<?> value = this.dataStore.getTry(key);
            if (value.isPresent()) {
                this.dataStore.setRaw(key, value.get());
            }
        }
        setLaraProperties();

    }

    private DataStore mergeSystemAndLocalOptions(WeaverEngine weaverEngine, DataStore localArgs) {
        var systemOptionsFilename = weaverEngine.getName() + "_" + getSystemOptionsFilename();

        var storeDef = localArgs.getStoreDefinitionTry().orElse(null);

        if (storeDef == null) {
            SpecsLogs.debug(
                    "Local data store does not have a store definition, system-wide options in file '"
                            + systemOptionsFilename + "' not supported");
            return localArgs;
        }

        var persistence = localArgs.getPersistence().orElse(null);

        if (persistence == null) {
            SpecsLogs.debug(
                    "Local data store does not have an instance of AppPersistence set, system-wide options in file '"
                            + systemOptionsFilename + "' not supported");
            return localArgs;
        }

        DataStore defaultOptions = JOptionsUtils.loadDataStore(systemOptionsFilename, getClass(),
                storeDef, persistence);

        var defaultOptionsFile = defaultOptions.getConfigFile().orElse(null);
        if (defaultOptionsFile != null) {
            SpecsLogs.info("Loading default options in file '" + defaultOptionsFile.getAbsolutePath() + "'");
        }

        SpecsLogs.debug(() -> "Loading system-wide options");
        SpecsLogs.debug(() -> "Original options: " + localArgs);

        // Merge default values in localArgs
        var localArgsKeys = localArgs.getKeysWithValues();

        for (var key : defaultOptions.getKeysWithValues()) {

            // Only set if no value is set yet
            if (localArgsKeys.contains(key)) {
                continue;
            }

            // And if key is part of the store definition
            if (!storeDef.hasKey(key)) {
                continue;
            }

            localArgs.setRaw(key, defaultOptions.get(key));
        }

        SpecsLogs.debug(() -> "Merged options  : " + localArgs);

        // return mergedOptions;
        return localArgs;
    }

    /**
     * Set an option on lara according to the value given, if the option exists on the enum {@link Argument}
     *
     * @param option
     * @param value
     * @return
     */
    private void setLaraProperties() {
        if (!dataStore.hasValue(LaraiKeys.LARA_FILE)) {

            throw new LaraIException(
                    "The lara aspect file is mandatory! Please define the input lara file (e.g.: aspect.lara)");
        }

        if (dataStore.hasValue(LaraiKeys.OUTPUT_FOLDER)) {
            File output = dataStore.get(LaraiKeys.OUTPUT_FOLDER);
            SpecsIo.mkdir(output);
        }

    }

    /**
     * Right now the arguments are going as a single String!
     *
     * @return
     */
    public DataStore getWeaverArgs() {
        return dataStore;
    }

    @Override
    public String toString() {
        return dataStore.toString();
    }
}
