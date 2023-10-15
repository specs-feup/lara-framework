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

package org.lara.interpreter.joptions.config.interpreter;

import java.io.File;
import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Datakey.KeyFactory;
import org.suikasoft.jOptions.storedefinition.StoreDefinition;
import org.suikasoft.jOptions.storedefinition.StoreDefinitionBuilder;

import pt.up.fe.specs.jsengine.JsEngineType;
import pt.up.fe.specs.util.utilities.StringList;

public interface LaraiKeys {

    DataKey<File> OUTPUT_FOLDER = KeyFactory.folder("output", false).setLabel("Output Folder");

    DataKey<StringList> EXTERNAL_DEPENDENCIES = KeyFactory.stringList("external_dependencies")
            .setLabel("External dependencies (URLs, git repos)");

    DataKey<Boolean> LOG_JS_OUTPUT = KeyFactory.bool("javascript").setLabel("Log JavaScript Output");

    DataKey<JsEngineType> JS_ENGINE = KeyFactory.enumeration("jsEngine", JsEngineType.class)
            .setLabel("JavaScript Engine")
            // TODO: Change to GraalVM when transition is done
            .setDefault(() -> JsEngineType.GRAALVM_COMPAT);
    
    StoreDefinition STORE_DEFINITION = new StoreDefinitionBuilder("LaraI Options")
            .addKeys(OUTPUT_FOLDER, EXTERNAL_DEPENDENCIES, LOG_JS_OUTPUT, JS_ENGINE)
            .build();
}
