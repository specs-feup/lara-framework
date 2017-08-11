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
package org.lara.interpreter.cli;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.lara.interpreter.exception.LaraIException;
import org.lara.interpreter.joptions.config.interpreter.LaraiKeys;
import org.lara.interpreter.joptions.config.interpreter.LaraiStoreDefinition;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.lara.interpreter.weaver.options.WeaverOption;
import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Interfaces.DataStore;
import org.suikasoft.jOptions.app.AppPersistence;
import org.suikasoft.jOptions.storedefinition.StoreDefinition;

public class OptionsConverter {

    /**
     * Adds the commandline options into an existing datastore, using the Mapping in {@link JOptionsInterface}
     * 
     * @param laraFileName
     * @param cmd
     * @return
     */
    public static DataStore configExtraOptions2DataStore(String laraFileName, CommandLine cmd,
            WeaverEngine engine) {
        DataStore dataStore = configFile2DataStore(engine, cmd);
        if (!laraFileName.startsWith("-")) {

            dataStore.set(LaraiKeys.LARA_FILE, new File(laraFileName));
        }

        Map<WeaverOption, DataKey<?>> conversionMap = JOptionsInterface.getConversionMap();

        /**
         * Maybe it is better to deal with each option, see if it exists and put it in the datakey, this way we can
         * control better the multiple arguments
         */
        Map<String, WeaverOption> map = engine.getOptions().stream()
                .collect(Collectors.toMap(WeaverOption::longOption, value -> value));

        for (Option opt : Arrays.asList(cmd.getOptions())) {

            if (CLIConfigOption.contains(opt.getLongOpt())) {
                continue; // Just ignore options for config file (such as -c and -g)
            }
            if (CLIOption.contains(opt.getLongOpt())) {
                processLaraIOption(dataStore, conversionMap, opt);

            } else if (map.containsKey(opt.getLongOpt())) {
                DataKey<?> weaverOptionKey = map.get(opt.getLongOpt()).dataKey();
                setValue(dataStore, weaverOptionKey, opt);
            } else {

                throw new LaraIException("LARAI does not have option '" + opt.getLongOpt() + "'.");
            }
        }

        return dataStore;

    }

    /**
     * Convert the commandline into a datastore, using the Mapping in {@link JOptionsInterface}
     * 
     * @param laraFileName
     * @param cmd
     * @return
     */
    public static DataStore commandLine2DataStore(String laraFileName, CommandLine cmd,
            List<WeaverOption> weaverOptions) {
        DataStore dataStore = getDataStoreFromArgs(cmd); // This way the data store contains at least the values defined
                                                         // in the properties files
        dataStore.add(LaraiKeys.LARA_FILE, new File(laraFileName));

        Map<WeaverOption, DataKey<?>> conversionMap = JOptionsInterface.getConversionMap();

        /**
         * Maybe it is better to deal with each option, see if it exists and put it in the datakey, this way we can
         * control better the multiple arguments
         */
        Map<String, WeaverOption> map = weaverOptions.stream()
                .collect(Collectors.toMap(WeaverOption::longOption, value -> value));

        for (Option opt : Arrays.asList(cmd.getOptions())) {

            if (CLIOption.contains(opt.getLongOpt())) {
                processLaraIOption(dataStore, conversionMap, opt);

            } else if (map.containsKey(opt.getLongOpt())) {
                DataKey<?> weaverOptionKey = map.get(opt.getLongOpt()).dataKey();
                setValue(dataStore, weaverOptionKey, opt);
            } else {

                throw new LaraIException("LARAI does not have option '" + opt.getLongOpt() + "'.");
            }
        }

        return dataStore;

    }

    private static void processLaraIOption(DataStore dataStore, Map<WeaverOption, DataKey<?>> conversionMap,
            Option opt) {
        CLIOption cliOpt = CLIOption.valueOf(opt.getLongOpt());
        if (!conversionMap.containsKey(cliOpt)) {
            throw new LaraIException("Option " + cliOpt + "does not exist in LARAI.");
            // When the weaver is working with WeaverOption this condition must change to verify if WeaverOptionList
            // contains the option
        }
        DataKey<?> dataKey = conversionMap.get(cliOpt);

        // if (opt.hasArgs()) {
        // TODO: See this!
        // } else
        setValue(dataStore, dataKey, opt);
    }

    private static void setValue(DataStore dataStore, DataKey<?> dataKey, Option opt) {
        if (opt.hasArg()) {

            dataStore.setString(dataKey, opt.getValue());
        } else {
            dataStore.setString(dataKey, "true");
        }
    }

    /**
     * Convert the varargs into a DataStore
     * 
     * @param args
     */
    private static DataStore getDataStoreFromArgs(CommandLine cmd) {

        // Create a DataStore with the default values (i.e., values that are defined in the properties files
        StoreDefinition definition = new LaraiStoreDefinition().getStoreDefinition();
        DataStore dataStore = definition.getDefaultValues();

        return dataStore;
    }

    public static DataStore configFile2DataStore(WeaverEngine weaverEngine, CommandLine cmd) {
        File file = OptionsParser.getConfigFile(cmd);
        StoreDefinition laraiDefinition = OptionsParser.getLaraStoreDefinition(weaverEngine);
        AppPersistence persistence = OptionsParser.getXmlPersistence(laraiDefinition);
        DataStore laraiStore = persistence.loadData(file);
        return laraiStore;
    }
}
