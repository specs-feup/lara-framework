/**
 * Copyright 2018 SPeCS.
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

package org.lara.interpreter.weaver.options;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Interfaces.DataStore;

import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.lazy.Lazy;

public class WeaverOptions {

    private final List<WeaverOption> options;
    private final Lazy<Map<String, WeaverOption>> optionsMap;

    public WeaverOptions(List<WeaverOption> options) {
        this.options = options;
        this.optionsMap = Lazy.newInstance(() -> WeaverOptions.buildMap(this.options));
    }

    private static Map<String, WeaverOption> buildMap(List<WeaverOption> options) {
        // Map<String, WeaverOption> optionsMap = new HashMap<>();
        //
        // for (WeaverOption option : options) {
        // if (option.dataKey() == null) {
        // System.out.println("OPTION " + option.shortOption() + " has no DataKey");
        // continue;
        // }
        //
        // optionsMap.put(option.dataKey().getName(), option);
        // }
        //
        // return optionsMap;
        return options.stream()
                // Only options that have a DataKey
                .filter(option -> option.dataKey() != null)
                .collect(Collectors.toMap(option -> option.dataKey().getName(), option -> option));
    }

    /**
     * Converts the given DataStore into the equivalent command-line string.
     * 
     * @param dataStore
     * @return
     */
    @SuppressWarnings("unchecked") // In order to get encoder for Object
    public String toCli(DataStore dataStore) {

        List<String> arguments = new ArrayList<>();
        String aspect = null;
        System.out.println("DATASTORE:" + dataStore);
        for (String keyName : dataStore.getKeysWithValues()) {

            WeaverOption weaverOption = this.optionsMap.get().get(keyName);
            if (weaverOption == null) {

                // Special case: 'aspect'
                // This option does not have a CLI flag, it is just the argument
                if (keyName.equals("aspect")) {
                    aspect = dataStore.get(keyName).toString();
                    if (aspect.contains(" ")) {
                        aspect = "\"" + aspect + "\"";
                    }
                    continue;
                }

                SpecsLogs.msgInfo("toCli: Could not obtain weaver option for key '" + keyName + "'");
                continue;
            }

            if (weaverOption.shortOption() != null) {
                arguments.add("-" + weaverOption.shortOption());
            } else if (weaverOption.longOption() != null) {
                arguments.add("--" + weaverOption.longOption());
            } else {
                throw new RuntimeException("Should not arrive here, means that no short nor long option were defined");
            }

            // If no args, just continue
            if (weaverOption.args() == OptionArguments.NO_ARGS) {
                continue;
            }

            // Encode arguments
            arguments.add(((DataKey<Object>) weaverOption.dataKey()).encode(dataStore.get(keyName)));
        }

        if (aspect == null) {
            SpecsLogs.msgInfo("WeaverOptions.toCli(): Given DataStore did not contain an 'aspect' option");
        } else {
            arguments.add(0, aspect);
        }

        return arguments.stream().collect(Collectors.joining(" "));
    }
}
