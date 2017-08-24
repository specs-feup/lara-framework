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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.parsers.ParserConfigurationException;

import org.lara.interpreter.api.WeaverApis;
import org.lara.interpreter.aspectir.Argument;
import org.lara.interpreter.exception.LaraIException;
import org.lara.interpreter.joptions.keys.FileList;
import org.lara.interpreter.joptions.keys.OptionalFile;
import org.lara.interpreter.utils.Tools;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.lara.interpreter.weaver.options.WeaverOption;
import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Interfaces.DataStore;
import org.xml.sax.SAXException;

import com.google.common.base.Preconditions;

import larai.LaraI;
import pt.up.fe.specs.lara.LaraApis;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.providers.ResourceProvider;

public class LaraIDataStore implements LaraiKeys {
    public static final String CONFIG_FILE_NAME = "larai.properties";
    private final DataStore dataStore;
    private final LaraI larai;
    private Tools tools = null;
    // private final DataStore weaverDataStore;
    private final List<ResourceProvider> laraAPIs;

    public LaraIDataStore(LaraI lara, DataStore dataStore, WeaverEngine weaverEngine) {
        larai = lara;

        this.dataStore = dataStore;
        // weaverDataStore = DataStore.newInstance("Weaver Arguments");
        laraAPIs = new ArrayList<>();
        laraAPIs.addAll(LaraApis.getApis());
        laraAPIs.addAll(WeaverApis.getApis());
        laraAPIs.addAll(weaverEngine.getAspectsAPI());
        // laraAPIs = weaverEngine.getAspectsAPI();
        for (WeaverOption option : weaverEngine.getOptions()) {
            DataKey<?> key = option.dataKey();
            Optional<?> value = dataStore.getTry(key);
            if (value.isPresent()) {
                // weaverDataStore.setRaw(key, value.get());
                dataStore.setRaw(key, value.get());
            }
        }
        setLaraProperties();
        // System.out.println("\n\n" + this.dataStore);
        // System.out.println(".........................");
        // System.out.println("\n\n" + dataStore);
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
            if (logFile.isUsed()) {
                larai.out.setStream(logFile.getFile());
            }
        }

        if (dataStore.hasValue(LaraiKeys.VERBOSE)) {
            int level = dataStore.get(LaraiKeys.VERBOSE).ordinal();
            larai.out.setLevel(level);
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
        if (dataStore.hasValue(folder)) {
            return dataStore.get(folder);
        }
        return FileList.newInstance(new File("."));
    }

    public FileList getIncludeDirs() {
        if (dataStore.hasValue(LaraiKeys.INCLUDES_FOLDER)) {
            FileList includesFolder = dataStore.get(LaraiKeys.INCLUDES_FOLDER);

            return includesFolder;
        }
        return FileList.newInstance();
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

    public String getAspectArgumentsStr() {
        if (dataStore.hasValue(LaraiKeys.ASPECT_ARGS)) {
            return dataStore.get(LaraiKeys.ASPECT_ARGS);
        }
        return "";
    }

    public boolean isJavaScriptStream() {
        if (dataStore.hasValue(LaraiKeys.LOG_JS_OUTPUT)) {
            return dataStore.get(LaraiKeys.LOG_JS_OUTPUT);
        }
        return false;
    }

    public List<ResourceProvider> getLaraAPIs() {
        return laraAPIs;
    }

    public Map<String, String> getBundleTags() {
        if (dataStore.hasValue(LaraiKeys.BUNDLE_TAGS)) {
            return parseBundleTags(dataStore.get(LaraiKeys.BUNDLE_TAGS));
        }
        return Collections.emptyMap();
    }

    private Map<String, String> parseBundleTags(String bundleTagsString) {
        Map<String, String> bundleTags = new HashMap<>();

        // Split around the comma
        String[] tagPairs = bundleTagsString.split(",");
        for (String tagPair : tagPairs) {
            int equalIndex = tagPair.indexOf('=');
            Preconditions.checkArgument(equalIndex != -1,
                    "Found a tag-value pair without equal sign (=): " + bundleTagsString);

            String tag = tagPair.substring(0, equalIndex);
            String value = tagPair.substring(equalIndex + 1, tagPair.length());

            bundleTags.put(tag, value);
        }

        return bundleTags;
    }

}
