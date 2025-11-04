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
 * specific language governing permissions and limitations under the License.
 */

package org.lara.interpreter.joptions.config.engine;

import java.util.List;

import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.lara.interpreter.weaver.options.WeaverOption;
import org.suikasoft.jOptions.storedefinition.StoreDefinition;
import org.suikasoft.jOptions.storedefinition.StoreDefinitionBuilder;
import org.suikasoft.jOptions.storedefinition.StoreDefinitionProvider;

public class WeaverEngineStoreDefinition implements StoreDefinitionProvider {

    private static final String DEFINITION_NAME = "Weaver Engine";

    private final WeaverEngine weaver;

    public WeaverEngineStoreDefinition(WeaverEngine weaver) {
        this.weaver = weaver;
    }

    @Override
    public StoreDefinition getStoreDefinition() {

        StoreDefinitionBuilder builder = new StoreDefinitionBuilder(
                weaver.getClass().getSimpleName() + " Options");
        List<WeaverOption> opts = weaver.getOptions();

        for (WeaverOption weaverOption : opts) {
            builder.addKey(weaverOption.dataKey());
        }

        return builder.build();
    }

    public static String getDefinitionName() {
        return WeaverEngineStoreDefinition.DEFINITION_NAME;
    }

}
