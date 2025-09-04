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

    @Override
    public StoreDefinition getStoreDefinition() {

        StoreDefinitionBuilder builder = new StoreDefinitionBuilder(LaraiKeys.STORE_DEFINITION.getName());
        builder.addDefinition(LaraiKeys.STORE_DEFINITION);
        final StoreDefinitionBuilder finalBuilder = builder.setDefaultValues(getDefaultValues());

        extraKeys.forEach(finalBuilder::addKey);
        return finalBuilder.build();

    }

    private static DataStore getDefaultValues() {
        Properties properties = getDefaultProperties();

        return JOptionsInterface.getDataStore(getDefinitionName(), properties);
    }

    private static Properties getDefaultProperties() {
        String jarLoc = LaraIUtils.getJarFoldername();
        Properties properties = new Properties();

        File globalFile = new File(jarLoc, LaraIDataStore.getConfigFileName());
        File localFile = new File(LaraIDataStore.getConfigFileName());
        loadProperties(properties, globalFile);
        loadProperties(properties, localFile);

        return properties;
    }

    /**
     * Loads the properties from a given file. It does not load/given an exception
     * if the file does not exist!
     *
     */
    private static void loadProperties(Properties properties, File globalFile) {
        if (globalFile.exists()) {
            try (final InputStream inputConfigStream = new FileInputStream(globalFile)) {
                properties.load(inputConfigStream);
            } catch (IOException e) {
                SpecsLogs.warn("Error message:\n", e);
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
