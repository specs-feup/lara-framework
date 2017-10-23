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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.lara.interpreter.cli.JOptionsInterface;
import org.lara.interpreter.utils.LaraIUtils;
import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Interfaces.DataStore;
import org.suikasoft.jOptions.storedefinition.StoreDefinition;
import org.suikasoft.jOptions.storedefinition.StoreDefinitionBuilder;
import org.suikasoft.jOptions.storedefinition.StoreDefinitionProvider;

import pt.up.fe.specs.util.SpecsLogs;

public class LaraiStoreDefinition implements StoreDefinitionProvider {

    private static final String DEFINITION_NAME = "LaraI Options";

    private final List<DataKey<?>> extraKeys = new ArrayList<>();

    // private final Class<? extends WeaverEngine> weaver;

    // private LanguageSpecification langSpec;

    // public LaraiStoreDefinition(Class<? extends WeaverEngine> weaver) {
    // this.weaver = weaver;
    // // TODO Auto-generated constructor stub
    // }

    @Override
    public StoreDefinition getStoreDefinition() {

        // return LaraiKeys.STORE_DEFINITION;

        StoreDefinitionBuilder builder = new StoreDefinitionBuilder(LaraiStoreDefinition.DEFINITION_NAME);

        builder.addKey(LaraiKeys.LARA_FILE);

        builder.addKey(LaraiKeys.MAIN_ASPECT);
        builder.addKey(LaraiKeys.ASPECT_ARGS);
        // builder.addKey(LaraiKeys.WEAVER_ARGS);
        // builder.addKey(LaraiKeys.LANGUAGE_SPECIFICATION_FOLDER);
        builder.addKey(LaraiKeys.WORKSPACE_FOLDER);
        builder.addKey(LaraiKeys.OUTPUT_FOLDER);
        builder.addKey(LaraiKeys.INCLUDES_FOLDER);
        // DataKey<ClassProvider> weaverClass = LaraiKeys.WEAVER_CLASS.setDefault(ClassProvider.newInstance(weaver));
        // builder.addKey(weaverClass);
        builder.addKey(LaraiKeys.VERBOSE);
        builder.addKey(LaraiKeys.REPORT_FILE);
        builder.addKey(LaraiKeys.METRICS_FILE);
        builder.addKey(LaraiKeys.TOOLS_FILE);
        builder.addKey(LaraiKeys.LOG_FILE);
        builder.addKey(LaraiKeys.LOG_JS_OUTPUT);
        builder.addKey(LaraiKeys.DEBUG_MODE);
        builder.addKey(LaraiKeys.TRACE_MODE);
        builder.addKey(LaraiKeys.BUNDLE_TAGS);
        // builder.addKey(LaraiKeys.SHOW_HELP);

        final StoreDefinitionBuilder finalBuilder = builder.setDefaultValues(getDefaultValues());

        extraKeys.forEach(finalBuilder::addKey);
        // List<WeaverOption> engineOptions;
        // try {
        // engineOptions = this.weaver.newInstance().getOptions();
        // engineOptions.forEach(opt -> finalBuilder.addKey(opt.dataKey()));
        // } catch (InstantiationException | IllegalAccessException e) {
        // LoggingUtils.msgWarn("Error message:\n", e);
        // }
        return finalBuilder.build();

    }

    private static DataStore getDefaultValues() {
        Properties properties = getDefaultProperties();

        return JOptionsInterface.getDataStore(getDefinitionName(), properties);
    }

    private static Properties getDefaultProperties() {
        String jarLoc = LaraIUtils.getJarFoldername();
        Properties properties = new Properties();

        File globalFile = new File(jarLoc, LaraIDataStore.CONFIG_FILE_NAME);
        File localFile = new File(LaraIDataStore.CONFIG_FILE_NAME);
        loadProperties(properties, globalFile);
        loadProperties(properties, localFile);

        return properties;
    }

    /**
     * Loads the properties from a given file. It does not load/given an exception if the file does not exist!
     * 
     * @param properties
     * @param globalFile
     */
    private static void loadProperties(Properties properties, File globalFile) {
        if (globalFile.exists()) {
            try (final InputStream inputConfigStream = new FileInputStream(globalFile);) {
                properties.load(inputConfigStream);
            } catch (IOException e) {
                SpecsLogs.msgWarn("Error message:\n", e);
            }
        }
    }

    public static String getDefinitionName() {
        return LaraiStoreDefinition.DEFINITION_NAME;
    }

    public void addExtraKeys(StoreDefinition storeDefinition) {
        extraKeys.addAll(storeDefinition.getKeys());
    }
}
