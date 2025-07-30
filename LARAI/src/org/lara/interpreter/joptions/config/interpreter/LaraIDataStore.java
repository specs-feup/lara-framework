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

import com.google.gson.Gson;
import larai.LaraI;
import org.lara.interpreter.exception.LaraIException;
import org.lara.interpreter.joptions.keys.FileList;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.lara.interpreter.weaver.options.WeaverOption;
import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Interfaces.DataStore;
import org.suikasoft.jOptions.JOptionKeys;
import org.suikasoft.jOptions.JOptionsUtils;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.properties.SpecsProperties;
import java.io.File;
import java.util.*;

/**
 * TODO: Should deprecate and just use DataStore directly?
 * TODO: Also, the "ifs" in the getters interfere with the default values set in
 * the DataKey
 *
 * @author JoaoBispo
 */
public class LaraIDataStore implements LaraiKeys {

    public static final String CONFIG_FILE_NAME = "larai.properties";
    private static final String SYSTEM_OPTIONS_FILENAME = "system_options.xml";

    public static String getConfigFileName() {
        return CONFIG_FILE_NAME;
    }

    public static String getSystemOptionsFilename() {
        return SYSTEM_OPTIONS_FILENAME;
    }

    private final DataStore dataStore;

    public LaraIDataStore(LaraI lara, DataStore dataStore, WeaverEngine weaverEngine) {

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
            SpecsLogs.debug(() -> "Local data store does not have a store definition, system-wide options in file '"
                    + systemOptionsFilename + "' not supported");
            return localArgs;
        }

        var persistence = localArgs.getPersistence().orElse(null);

        if (persistence == null) {
            SpecsLogs.debug(
                    () -> "Local data store does not have an instance of AppPersistence set, system-wide options in file '"
                            + systemOptionsFilename + "' not supported");
            return localArgs;
        }

        DataStore defaultOptions = JOptionsUtils.loadDataStore(systemOptionsFilename, getClass(),
                storeDef, persistence);

        var defaultOptionsFile = defaultOptions.getConfigFile().orElse(null);
        if (defaultOptionsFile != null) {
            SpecsLogs.debug("Loading default options in file '" + defaultOptionsFile.getAbsolutePath() + "'");
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
     * Set an option on lara according to the value given, if the option exists on
     * the enum {@link Argument}
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

    public File getLaraFile() {
        return dataStore.get(LaraiKeys.LARA_FILE);
    }

    public FileList getWorkingDir() {
        return getTrySources(LaraiKeys.WORKSPACE_FOLDER);
    }

    public List<File> getExtraSources() {
        if (dataStore.hasValue(LaraiKeys.WORKSPACE_EXTRA)) {
            return new ArrayList<>(dataStore.get(WORKSPACE_EXTRA).keySet());
        }

        return Collections.emptyList();
    }

    public boolean isDebug() {
        if (dataStore.hasValue(LaraiKeys.DEBUG_MODE)) {
            return dataStore.get(LaraiKeys.DEBUG_MODE);
        }
        return false;
    }

    public File getOutputDir() {
        return getTryFolder(LaraiKeys.OUTPUT_FOLDER);
    }

    private File getTryFolder(DataKey<File> folder) {
        if (dataStore.hasValue(folder)) {
            return dataStore.get(folder);
        }
        return new File(".");
    }

    private FileList getTrySources(DataKey<FileList> folder) {
        return dataStore.get(folder);
    }

    /**
     * Returns a JSON string representing the aspect arguments. If the value
     * represents a json file, reads it before
     * returning.
     *
     * @return the aspect arguments as a JSON string
     */
    public String getAspectArgumentsStr() {
        if (dataStore.hasValue(LaraiKeys.ASPECT_ARGS)) {

            String aspectArgs = dataStore.get(LaraiKeys.ASPECT_ARGS);

            // [Old] Can return directly, custom getter of ASPECT_ARGS already parses files
            // and sanitizes JSON
            // [New] Disabled this, because if a json file is given as input, this prevents
            // storing again the path to the JSON file (instead, it stores the contents of
            // the json file itself)

            // Parse aspect args (e.g., in case it is a file, sanitize)
            return parseAspectArgs(aspectArgs);

        }
        return "";
    }

    private String parseAspectArgs(String aspectArgs) {
        // Moved logic from customGetter of ASPECT_ARGS to here, to preserve storing the
        // arguments as a JSON file

        // In the end, we want to return a JSON string
        var jsonString = aspectArgs;

        // Check if an existing file was given as argument
        var file = JOptionKeys.getContextPath(aspectArgs, dataStore);

        if (file.isFile()) {

            var extension = SpecsIo.getExtension(file).toLowerCase();

            // Check if JSON
            if (extension.equals("json")) {
                jsonString = SpecsIo.read(file);
            }

            // Check if properties
            else if (extension.equals("properties")) {
                jsonString = SpecsProperties.newInstance(file).toJson();
            } else {
                // Throw exception
                throw new RuntimeException("Invalid file format (" + aspectArgs
                        + ") for aspect arguments. Supported formats: .json, .properties");
            }

        }

        // String json before testing for curly braces
        jsonString = jsonString.strip();

        // Fix curly braces
        if (!jsonString.startsWith("{")) {
            jsonString = "{" + jsonString;
        }

        if (!jsonString.endsWith("}")) {
            jsonString = jsonString + "}";
        }

        // Sanitize
        var gson = new Gson();
        try {
            return gson.toJson(gson.fromJson(jsonString, Object.class));
        } catch (Exception e) {
            throw new RuntimeException("Passed invalid JSON as argument: '" + aspectArgs + "'", e);
        }

    }

    @Override
    public String toString() {
        return dataStore.toString();
    }
}
