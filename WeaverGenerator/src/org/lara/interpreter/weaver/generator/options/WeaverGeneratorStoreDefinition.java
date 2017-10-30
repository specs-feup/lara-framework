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

package org.lara.interpreter.weaver.generator.options;

import org.suikasoft.jOptions.storedefinition.StoreDefinition;
import org.suikasoft.jOptions.storedefinition.StoreDefinitionBuilder;
import org.suikasoft.jOptions.storedefinition.StoreDefinitionProvider;

public class WeaverGeneratorStoreDefinition implements StoreDefinitionProvider {

    private static final String DEFINITION_NAME = "Weaver Generator GUI";

    @Override
    public StoreDefinition getStoreDefinition() {
        StoreDefinitionBuilder builder = new StoreDefinitionBuilder(WeaverGeneratorStoreDefinition.DEFINITION_NAME);

        builder.addKey(WeaverGeneratorKeys.WEAVER_NAME);
        builder.addKey(WeaverGeneratorKeys.LANGUAGE_SPECIFICATION_FOLDER);
        builder.addKey(WeaverGeneratorKeys.ABSTRACT_GETTERS);
        builder.addKey(WeaverGeneratorKeys.ADD_EVENTS);
        builder.addKey(WeaverGeneratorKeys.IMPL_MODE);
        builder.addKey(WeaverGeneratorKeys.DEF_MODE);
        builder.addKey(WeaverGeneratorKeys.NODE_TYPE);
        builder.addKey(WeaverGeneratorKeys.PACKAGE);
        builder.addKey(WeaverGeneratorKeys.OUTPUT_FOLDER);

        return builder.build();
    }

    public static String getDefinitionName() {
        return WeaverGeneratorStoreDefinition.DEFINITION_NAME;
    }

}
