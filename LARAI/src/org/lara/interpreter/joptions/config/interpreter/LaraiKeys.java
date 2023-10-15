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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Datakey.KeyFactory;
import org.suikasoft.jOptions.storedefinition.StoreDefinition;
import org.suikasoft.jOptions.storedefinition.StoreDefinitionBuilder;

import pt.up.fe.specs.jsengine.JsEngineType;
import pt.up.fe.specs.util.utilities.StringList;

public interface LaraiKeys {

    static String getUnitTestFlag() {
        return "ut";
    }

    static String getDocGeneratorFlag() {
        return "doc";
    }

    static String getServerFlag() {
        return "server";
    }

    static String getApiFlag() {
        return "api";
    }

    DataKey<File> LARA_FILE = KeyFactory.file("aspect", "lara", "js").setLabel("Aspect");

    DataKey<String> MAIN_ASPECT = KeyFactory.string("main").setLabel("Main Aspect");

    DataKey<String> ASPECT_ARGS = KeyFactory.string("argv").setLabel("Aspect Arguments")
            .setCustomGetter(LaraIKeyFactory::customGetterLaraArgs);

    DataKey<Map<File, File>> WORKSPACE_EXTRA = KeyFactory.filesWithBaseFolders("workspace_extra")
            .setLabel("Additional Sources (separated by ;)");

    DataKey<File> OUTPUT_FOLDER = KeyFactory.folder("output", false).setLabel("Output Folder");

    DataKey<Boolean> AUTOMATICALLY_IMPORT_JS = KeyFactory.bool("autoimportjs")
            .setLabel("Automatically import JS files in include folders");

    DataKey<StringList> EXTERNAL_DEPENDENCIES = KeyFactory.stringList("external_dependencies")
            .setLabel("External dependencies (URLs, git repos)");

    DataKey<VerboseLevel> VERBOSE = KeyFactory.enumeration("verbose", VerboseLevel.class).setLabel("Verbose Level")
            .setDefault(() -> VerboseLevel.warnings);

    DataKey<Boolean> LOG_JS_OUTPUT = KeyFactory.bool("javascript").setLabel("Log JavaScript Output");
    DataKey<Boolean> DEBUG_MODE = KeyFactory.bool("debug").setLabel("Debug Mode");

    DataKey<Boolean> TRACE_MODE = KeyFactory.bool("stack trace").setLabel("Show LARA Stack Trace")
            .setDefault(() -> true);

    // TODO: Use List<String> instead
    DataKey<String> BUNDLE_TAGS = KeyFactory.string("bundle_tags").setLabel("Bundle tags");

    DataKey<Boolean> RESTRICT_MODE = KeyFactory.bool("restrict mode")
            .setLabel("Restrict mode (some Java classes are not allowed)");

    DataKey<JsEngineType> JS_ENGINE = KeyFactory.enumeration("jsEngine", JsEngineType.class)
            .setLabel("JavaScript Engine")
            // TODO: Change to GraalVM when transition is done
            // .setDefault(() -> JsEngineType.NASHORN);
            .setDefault(() -> JsEngineType.GRAALVM_COMPAT);

    DataKey<Boolean> UNIT_TEST_MODE = KeyFactory.bool("unit_test_mode").setLabel("Unit-testing mode");
    // DataKey<StringList> UNIT_TEST_ARGS = KeyFactory.stringList("unit_test_args").setLabel("Unit-testing arguments");
    // .setLabel("Unit-testing arguments");
    DataKey<List<String>> UNIT_TEST_ARGS = KeyFactory.generic("unit_test_args", new ArrayList<>());

    DataKey<String> CALL_ARGS = KeyFactory.string("call_args").setLabel(
            "If present, interpret aspect file as the full path to an aspect to be called, and this argument as the arguments to use to call the aspect");

    // No GUI, only CLI
    DataKey<Boolean> GENERATE_DOCUMENTATION = KeyFactory.bool("generateDoc").setLabel("Generate Documentation");

    
    StoreDefinition STORE_DEFINITION = new StoreDefinitionBuilder("LaraI Options")
            .addKeys(LARA_FILE, MAIN_ASPECT, ASPECT_ARGS, WORKSPACE_EXTRA, OUTPUT_FOLDER,
                    AUTOMATICALLY_IMPORT_JS, EXTERNAL_DEPENDENCIES, VERBOSE, LOG_JS_OUTPUT,
                    DEBUG_MODE, TRACE_MODE, BUNDLE_TAGS, RESTRICT_MODE, JS_ENGINE)
            .build();
}
