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
 * specific language governing permissions and limitations under the License.
 */
package org.lara.interpreter.joptions.config.interpreter;

import larai.LaraI;
import org.lara.interpreter.exception.LaraIException;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.lara.interpreter.weaver.options.WeaverOption;
import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Interfaces.DataStore;
import pt.up.fe.specs.util.SpecsIo;
import java.io.File;
import java.util.*;

/**
 * TODO: Should deprecate and just use DataStore directly?
 * TODO: Also, the "ifs" in the getters interfere with the default values set in
 * the DataKey
 *
 * @author JoaoBispo
 */
public class LaraIDataStore implements LaraiKeys {

    public static final String CONFIG_FILE_NAME = "larai.properties";

    public static String getConfigFileName() {
        return CONFIG_FILE_NAME;
    }

    private final DataStore dataStore;

    public LaraIDataStore(LaraI lara, DataStore dataStore, WeaverEngine weaverEngine) {

        this.dataStore = dataStore;

        for (WeaverOption option : weaverEngine.getOptions()) {
            DataKey<?> key = option.dataKey();
            Optional<?> value = this.dataStore.getTry(key);
            value.ifPresent(o -> this.dataStore.setRaw(key, o));
        }
        setLaraProperties();
    }

    /**
     * Set an option on lara according to the value given, if the option exists on
     * the enum {@link Argument}
     */
    private void setLaraProperties() {
        if (!dataStore.hasValue(LaraiKeys.LARA_FILE)) {

            throw new LaraIException(
                    "The script file is mandatory! Please define the input script file (e.g.: main.js)");
        }
        if (dataStore.hasValue(LaraiKeys.OUTPUT_FOLDER)) {
            File output = dataStore.get(LaraiKeys.OUTPUT_FOLDER);
            SpecsIo.mkdir(output);
        }
    }

    @Override
    public String toString() {
        return dataStore.toString();
    }
}
