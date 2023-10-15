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
import org.lara.interpreter.weaver.options.WeaverOption;
import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Interfaces.DataStore;

import pt.up.fe.specs.util.SpecsEnums;
import pt.up.fe.specs.util.SpecsLogs;

public class JOptionsInterface {

    private static final Map<WeaverOption, DataKey<?>> CONVERSION_MAP;

    static {
        CONVERSION_MAP = new HashMap<>();
        JOptionsInterface.CONVERSION_MAP.put(CLIOption.output, LaraiKeys.OUTPUT_FOLDER);
        JOptionsInterface.CONVERSION_MAP.put(CLIOption.javascript, LaraiKeys.LOG_JS_OUTPUT);
        JOptionsInterface.CONVERSION_MAP.put(CLIOption.dependencies, LaraiKeys.EXTERNAL_DEPENDENCIES);
        JOptionsInterface.CONVERSION_MAP.put(CLIOption.jsengine, LaraiKeys.JS_ENGINE);
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
            data.setString(datakey, property);
        }

        return data;
    }

    public static Map<WeaverOption, DataKey<?>> getConversionMap() {
        return JOptionsInterface.CONVERSION_MAP;
    }
}
