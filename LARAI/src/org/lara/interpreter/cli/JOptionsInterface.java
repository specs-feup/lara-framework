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

package org.lara.interpreter.cli;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.lara.interpreter.joptions.config.interpreter.LaraiKeys;
import org.lara.interpreter.joptions.config.interpreter.VerboseLevel;
import org.lara.interpreter.utils.LaraIUtils;
import org.lara.interpreter.weaver.options.WeaverOption;
import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Interfaces.DataStore;

import pt.up.fe.specs.util.SpecsEnums;
import pt.up.fe.specs.util.SpecsLogs;
import utils.LARASystem;

public class JOptionsInterface {

    private static final Map<WeaverOption, DataKey<?>> CONVERSION_MAP;

    static {
        CONVERSION_MAP = new HashMap<>();
        JOptionsInterface.CONVERSION_MAP.put(CLIOption.main, LaraiKeys.MAIN_ASPECT);
        JOptionsInterface.CONVERSION_MAP.put(CLIOption.argv, LaraiKeys.ASPECT_ARGS);
        // JOptionsInterface.CONVERSION_MAP.put(CLIOption.argw, LaraiKeys.WEAVER_ARGS);
        JOptionsInterface.CONVERSION_MAP.put(CLIOption.workspace, LaraiKeys.WORKSPACE_FOLDER);
        JOptionsInterface.CONVERSION_MAP.put(CLIOption.workspace_extra, LaraiKeys.WORKSPACE_EXTRA);
        JOptionsInterface.CONVERSION_MAP.put(CLIOption.output, LaraiKeys.OUTPUT_FOLDER);
        JOptionsInterface.CONVERSION_MAP.put(CLIOption.debug, LaraiKeys.DEBUG_MODE);
        JOptionsInterface.CONVERSION_MAP.put(CLIOption.stack, LaraiKeys.TRACE_MODE);
        JOptionsInterface.CONVERSION_MAP.put(CLIOption.log, LaraiKeys.LOG_FILE);
        JOptionsInterface.CONVERSION_MAP.put(CLIOption.javascript, LaraiKeys.LOG_JS_OUTPUT);
        JOptionsInterface.CONVERSION_MAP.put(CLIOption.report, LaraiKeys.REPORT_FILE);
        JOptionsInterface.CONVERSION_MAP.put(CLIOption.metrics, LaraiKeys.METRICS_FILE);
        JOptionsInterface.CONVERSION_MAP.put(CLIOption.tools, LaraiKeys.TOOLS_FILE);
        JOptionsInterface.CONVERSION_MAP.put(CLIOption.restrict, LaraiKeys.RESTRICT_MODE);
        // Setting custom decoder because Properties use numbers instead of names for the verbose level
        JOptionsInterface.CONVERSION_MAP.put(CLIOption.verbose,
                LaraiKeys.VERBOSE.setDecoder(s -> VerboseLevel.values()[Integer.parseInt(s)]));
        JOptionsInterface.CONVERSION_MAP.put(CLIOption.jsengine, LaraiKeys.JS_ENGINE);
        JOptionsInterface.CONVERSION_MAP.put(CLIOption.jarpaths, LaraiKeys.JAR_PATHS);
    }

    private static final EnumSet<CLIOption> IGNORE_SET = EnumSet.of(CLIOption.help, CLIOption.version);

    public static DataStore getDataStore(String name, Properties properties) {

        DataStore data = DataStore.newInstance(name);

        Map<String, CLIOption> enumMap = SpecsEnums.buildMap(CLIOption.values());

        for (Object key : properties.keySet()) {
            CLIOption option = enumMap.get(key);
            if (option == null) {
                SpecsLogs.msgInfo("Could not find property with name '" + key + "'");
                continue;
            }

            // Just ignore
            if (JOptionsInterface.IGNORE_SET.contains(option)) {
                continue;
            }

            DataKey<?> datakey = JOptionsInterface.CONVERSION_MAP.get(option);
            // New key that forgot to be added
            if (datakey == null) {
                throw new RuntimeException("Did not find a mapping for option '" + option + "'");
            }

            String property = properties.getProperty(key.toString());
            property.replace(LARASystem.LARAPATH, LaraIUtils.getJarFoldername());
            data.setString(datakey, property);
        }

        return data;
    }

    public static Map<WeaverOption, DataKey<?>> getConversionMap() {
        return JOptionsInterface.CONVERSION_MAP;
    }
}
