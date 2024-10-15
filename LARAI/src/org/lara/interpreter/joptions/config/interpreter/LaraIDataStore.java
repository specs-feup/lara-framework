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
import org.lara.interpreter.joptions.keys.OptionalFile;
import org.lara.interpreter.utils.Tools;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.lara.interpreter.weaver.options.WeaverOption;
import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Interfaces.DataStore;
import org.suikasoft.jOptions.JOptionKeys;
import org.suikasoft.jOptions.JOptionsUtils;
import org.xml.sax.SAXException;
import pt.up.fe.specs.jsengine.JsEngineType;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.properties.SpecsProperties;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * TODO: Should deprecate and just use DataStore directly?
 * TODO: Also, the "ifs" in the getters interfere with the default values set in the DataKey
 *
 * @author JoaoBispo
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
    private final LaraI larai;
    private Tools tools = null;


    public LaraIDataStore(LaraI lara, DataStore dataStore, WeaverEngine weaverEngine) {
        larai = lara;

        // Merge system-wise options with local options
        var mergedDataStore = mergeSystemAndLocalOptions(weaverEngine, dataStore);

        // this.dataStore = dataStore;
        this.dataStore = mergedDataStore;

        for (WeaverOption option : weaverEngine.getOptions()) {
            DataKey<?> key = option.dataKey();
            Optional<?> value = this.dataStore.getTry(key);
            if (value.isPresent()) {
                // weaverDataStore.setRaw(key, value.get());
                this.dataStore.setRaw(key, value.get());
            }
        }
        setLaraProperties();
        // System.out.println("\n\n" + this.dataStore);
        // System.out.println(".........................");
        // System.out.println("\n\n" + dataStore);

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
        if (dataStore.hasValue(LaraiKeys.LOG_FILE)) {
            OptionalFile logFile = dataStore.get(LaraiKeys.LOG_FILE);
            if (larai != null && logFile.isUsed()) {
                larai.out.addFileStream(logFile.getFile());
            }
        }

        if (dataStore.hasValue(LaraiKeys.VERBOSE)) {
            int level = dataStore.get(LaraiKeys.VERBOSE).ordinal();
            if (larai != null) {
                larai.out.setLevel(level);
            }
        }

        if (dataStore.hasValue(LaraiKeys.OUTPUT_FOLDER)) {
            File output = dataStore.get(LaraiKeys.OUTPUT_FOLDER);
            SpecsIo.mkdir(output);
        }

    }

    public Tools getTools() {
        if (tools == null) {
            setTools(createTools(dataStore, larai));
        }
        return tools;
    }

    private static Tools createTools(DataStore dataStore, LaraI larai) {
        try {

            if (dataStore.hasValue(LaraiKeys.TOOLS_FILE)) {

                OptionalFile optionalFile = dataStore.get(LaraiKeys.TOOLS_FILE);

                if (optionalFile.isUsed()) {
                    return new Tools(optionalFile.getFile());
                }

            }

        } catch (final FileNotFoundException e) {
            larai.out.warn("Could not find tools.xml in the given path");
        } catch (final ParserConfigurationException | SAXException | IOException e) {
            larai.out.warn("Could not parse tools.xml: " + e.getLocalizedMessage());
        }
        return null;
    }

    public void setTools(Tools tools) {
        this.tools = tools;
    }

    /**
     * Right now the arguments are going as a single String!
     *
     * @return
     */
    public DataStore getWeaverArgs() {

        // return weaverDataStore;
        return dataStore;
    }

    public File getLaraFile() {
        return dataStore.get(LaraiKeys.LARA_FILE);
    }

    public String getMainAspect() {
        if (dataStore.hasValue(LaraiKeys.MAIN_ASPECT)) {
            return dataStore.get(LaraiKeys.MAIN_ASPECT);
        }
        return "";
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

    public boolean isRestricMode() {
        return dataStore.get(LaraiKeys.RESTRICT_MODE);
    }

    public boolean useStackTrace() {
        if (dataStore.hasValue(LaraiKeys.TRACE_MODE)) {
            return dataStore.get(LaraiKeys.TRACE_MODE);
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
        /*
        if (dataStore.hasValue(folder)) {
            return dataStore.get(folder);
        }
        return FileList.newInstance(new File("."));
        */
    }

    public OptionalFile getReportFile() {
        if (dataStore.hasValue(LaraiKeys.REPORT_FILE)) {
            return dataStore.get(LaraiKeys.REPORT_FILE);
        }
        return OptionalFile.newInstance(null);
    }

    public OptionalFile getMetricsFile() {
        if (dataStore.hasValue(LaraiKeys.METRICS_FILE)) {
            return dataStore.get(LaraiKeys.METRICS_FILE);
        }
        return OptionalFile.newInstance(null);
    }

    /**
     * Returns a JSON string representing the aspect arguments. If the value represents a json file, reads it before
     * returning.
     *
     * @return the aspect arguments as a JSON string
     */
    public String getAspectArgumentsStr() {
        if (dataStore.hasValue(LaraiKeys.ASPECT_ARGS)) {

            String aspectArgs = dataStore.get(LaraiKeys.ASPECT_ARGS);

            // [Old] Can return directly, custom getter of ASPECT_ARGS already parses files and sanitizes JSON
            // [New] Disabled this, because if a json file is given as input, this prevents storing again the path
            // to the JSON file (instead, it stores the contents of the json file itself
            // return aspectArgs;

            // Parse aspect args (e.g., in case it is a file, sanitize)
            return parseAspectArgs(aspectArgs);

        }
        return "";
    }

    private String parseAspectArgs(String aspectArgs) {
        // Moved logic from customGetter of ASPECT_ARGS to here, to preserve storing the arguments as a JSON file

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

    public boolean isJavaScriptStream() {
        if (dataStore.hasValue(LaraiKeys.LOG_JS_OUTPUT)) {
            return dataStore.get(LaraiKeys.LOG_JS_OUTPUT);
        }
        return false;
    }

    public boolean disableWithKeywordInLaraJs() {
        return dataStore.get(LaraiKeys.DISABLE_WITH_KEYWORD_IN_LARA_JS);
    }

    public JsEngineType getJsEngine() {
        if (dataStore.hasValue(LaraiKeys.JS_ENGINE)) {
            return dataStore.get(LaraiKeys.JS_ENGINE);
        }
        return JS_ENGINE.getDefault().get();
    }

    @Override
    public String toString() {
        return dataStore.toString();
    }
}
