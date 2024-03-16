/**
 * Copyright 2017 SPeCS.
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

package pt.up.fe.specs.larai;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.lara.interpreter.cli.OptionsParser;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Datakey.KeyFactory;
import org.suikasoft.jOptions.Interfaces.DataStore;
import org.suikasoft.jOptions.app.AppPersistence;
import org.suikasoft.jOptions.storedefinition.StoreDefinition;

import larai.LaraI;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.utilities.ProgressCounter;

public class WeaverBatchTester {

    public static final DataKey<File> CURRENT_CONFIGURATION_FILE = KeyFactory.file("Weaver Tester Current Config File");

    public static void testWithArray(File baseFolder, String configExtension, Consumer<String[]> weaverLauncher) {
        test(baseFolder, configExtension, list -> weaverLauncher.accept(list.toArray(new String[0])));
    }

    public static void test(File baseFolder, String configExtension, Consumer<List<String>> weaverLauncher) {

        List<File> configurationFiles = SpecsIo.getFilesRecursive(baseFolder, configExtension);
        ProgressCounter counter = new ProgressCounter(configurationFiles.size());
        SpecsLogs.msgInfo("Found " + counter.getMaxCount() + " configuration files");
        for (File configurationFile : configurationFiles) {
            SpecsLogs.msgInfo("Running '" + configurationFile + "' " + counter.next());

            weaverLauncher.accept(Arrays.asList("-c", configurationFile.getAbsolutePath()));
        }

    }

    public static void testWithDataStore(File baseFolder, String configExtension,
            Supplier<WeaverEngine> weaverEngineSupplier, Consumer<DataStore> dataStoreProcessor) {

        List<File> configurationFiles = SpecsIo.getFilesRecursive(baseFolder, configExtension);
        ProgressCounter counter = new ProgressCounter(configurationFiles.size());
        SpecsLogs.msgInfo("Found " + counter.getMaxCount() + " configuration files");
        for (File configurationFile : configurationFiles) {
            SpecsLogs.msgInfo("Running '" + configurationFile + "' " + counter.next());

            WeaverEngine weaverEngine = weaverEngineSupplier.get();

            // CommandLine cmd = OptionsParser.parse(new String[] { configurationFile.getAbsolutePath() },
            // new Options());

            StoreDefinition laraiDefinition = OptionsParser.getLaraStoreDefinition(weaverEngine);
            AppPersistence persistence = OptionsParser.getXmlPersistence(laraiDefinition);
            DataStore dataStore = persistence.loadData(configurationFile);

            // Add info about current configuration file
            dataStore.add(CURRENT_CONFIGURATION_FILE, configurationFile);

            // DataStore dataStore = OptionsConverter.configFile2DataStore(weaverEngine, cmd);
            dataStoreProcessor.accept(dataStore);

            LaraI.exec(dataStore, weaverEngine);

            // DataStore dataStore = new XmlPersistence(definition).loadData(configurationFile);
            //
            // weaverLauncher.accept(dataStore);
        }

    }
}
