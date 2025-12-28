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
package larai;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.lara.interpreter.cli.OptionsConverter;
import org.lara.interpreter.cli.OptionsParser;
import org.lara.interpreter.cli.OptionsParser.ExecutionMode;
import org.lara.interpreter.joptions.config.interpreter.LaraiKeys;
import org.lara.interpreter.utils.LaraIUtils;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.suikasoft.jOptions.Interfaces.DataStore;
import org.suikasoft.jOptions.storedefinition.StoreDefinition;
import org.suikasoft.jOptions.storedefinition.StoreDefinitionBuilder;
import pt.up.fe.specs.util.SpecsLogs;
import java.util.*;
import java.util.function.Supplier;

public class LaraI {
    public static final String PROPERTY_JAR_PATH = "lara.jarpath";

    private static Supplier<Long> timeProvider = System::currentTimeMillis;

    public static StoreDefinition buildStoreDefinition(WeaverEngine weaverEngine) {
        String weaverName = weaverEngine.getName();
        return new StoreDefinitionBuilder(weaverName)
                // Add LaraI keys
                .addDefinition(LaraiKeys.STORE_DEFINITION)
                // Add weaver custom keys
                .addDefinition(weaverEngine.getStoreDefinition())
                .build();
    }

    /**
     * Converts an array of strings to the corresponding DataStore.
     *
     * @return A DataStore that corresponds to the given arguments, or empty if the
     *         arguments represent a GUI execution mode.
     */
    public static Optional<DataStore> convertArgsToDataStore(Object[] objArgs, WeaverEngine weaverEngine) {

        var args = new String[objArgs.length];
        for (int i = 0; i < objArgs.length; i++) {
            args[i] = objArgs[i].toString();
        }

        Options finalOptions = getCliOptions(weaverEngine);

        CommandLine cmd = OptionsParser.parse(args, finalOptions);

        ExecutionMode mode = OptionsParser.getExecMode(args[0], cmd, finalOptions);

        SpecsLogs.debug("Detected launch mode " + mode);

        var dataStore = buildDataStore(weaverEngine, mode, cmd, args[0]);

        // If help, print help
        var helpFlag = dataStore.map(data -> data.get(LaraiKeys.SHOW_HELP)).orElse(false);
        if (helpFlag) {
            LaraIUtils.printHelp(cmd, finalOptions);
        }

        return dataStore;
    }

    /**
     * Builds the list of command-line interface options available to the given
     * weaver.
     * 
     * <p>
     * This is a super-set of getWeaverOptions(), which includes launch-specific
     * flags, and returns an instance of the
     * Apache Commons CLI package.
     *
     */
    private static Options getCliOptions(WeaverEngine weaverEngine) {
        Collection<Option> configOptions = OptionsParser.buildConfigOptions();
        Collection<Option> mainOptions = OptionsParser.buildLaraIOptionGroup();
        OptionsParser.addExtraOptions(mainOptions, weaverEngine.getOptions());

        Options completeOptions = new Options();

        configOptions.forEach(completeOptions::addOption); // So the config options appear on the top
        mainOptions.forEach(completeOptions::addOption);

        return completeOptions;
    }

    private static Optional<DataStore> buildDataStore(WeaverEngine weaverEngine, ExecutionMode mode, CommandLine cmd,
            String mainScript) {
        return switch (mode) {
            // convert configuration file to data store and run
            case CONFIG -> Optional.of(OptionsConverter.configFile2DataStore(weaverEngine, cmd));

            // convert options to data store and run
            case OPTIONS ->
                Optional.of(OptionsConverter.commandLine2DataStore(mainScript, cmd, weaverEngine.getOptions()));

            // convert configuration file to data store, override with extra options and run
            case CONFIG_OPTIONS ->
                Optional.of(OptionsConverter.configExtraOptions2DataStore(mainScript, cmd, weaverEngine));
        };
    }

    public static long getCurrentTime() {
        return timeProvider.get();
    }
}
