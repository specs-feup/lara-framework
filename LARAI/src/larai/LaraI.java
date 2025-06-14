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
import org.apache.commons.cli.Options;
import org.lara.interpreter.cli.LaraCli;
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
import pt.up.fe.specs.util.utilities.SpecsThreadLocal;
import java.util.*;
import java.util.function.Supplier;

/**
 * An interpreter for the LARA language, which converts the Aspect-IR into a javascript representation and runs that
 * script. This is used in REFLECT as an outer-loop for the project-flow and also for design-space exploration.
 * Furthermore, one can have a weaver which can be used with this interpreter. For that, one should implement the
 * interface org.reflect.larai.IWeaver, available in this project, and follow its instructions, and change the weaver
 * with the -w/--weaver option with the name of that implementation (E.g.: -weaver org.specs.Matisse) OR invoke
 * LARAI.exec(2)/LARAI.exec(5)
 *
 * @author Tiago
 */
public class LaraI {
    public static final double LARA_VERSION = 3.1; // Since we are using GraalVM
    public static final String LARAI_VERSION_TEXT = "Lara interpreter version: " + LaraI.LARA_VERSION;
    public static final String PROPERTY_JAR_PATH = "lara.jarpath";

    private static Supplier<Long> timeProvider = System::currentTimeMillis;

    /**
     * Thread-scope LaraC
     */
    private static final SpecsThreadLocal<LaraI> THREAD_LOCAL_LARAI = new SpecsThreadLocal<>(LaraI.class);

    public static LaraI getThreadLocalLarai() {
        return THREAD_LOCAL_LARAI.get();
    }

    /**
     * Create a new LaraI with the input datastore
     *
     * @param dataStore
     * @param weaverEngine
     */
    private LaraI(DataStore dataStore, WeaverEngine weaverEngine) {}

    public static StoreDefinition getStoreDefinition(WeaverEngine weaverEngine) {
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
     * @param objArgs
     * @param weaverEngine
     * @return A DataStore that corresponds to the given arguments, or empty if the arguments represent a GUI execution mode.
     */
    public static Optional<DataStore> convertArgsToDataStore(Object[] objArgs, WeaverEngine weaverEngine) {

        var args = new String[objArgs.length];
        for (int i = 0; i < objArgs.length; i++) {
            args[i] = objArgs[i].toString();
        }

        Options finalOptions = LaraCli.getCliOptions(weaverEngine);

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

    private static Optional<DataStore> buildDataStore(WeaverEngine weaverEngine, ExecutionMode mode, CommandLine cmd, String mainScript) {
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
