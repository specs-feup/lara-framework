/**
 * Copyright 2016 SPeCS.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

package org.lara.interpreter.joptions.config.interpreter;

import org.lara.interpreter.joptions.keys.FileList;
import org.lara.interpreter.joptions.keys.OptionalFile;
import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Datakey.KeyFactory;
import org.suikasoft.jOptions.storedefinition.StoreDefinition;
import org.suikasoft.jOptions.storedefinition.StoreDefinitionBuilder;
import pt.up.fe.specs.jsengine.JsEngineType;

import javax.swing.*;
import java.io.File;
import java.util.*;

public interface LaraiKeys {

    static String getServerFlag() {
        return "server";
    }

    DataKey<File> LARA_FILE = KeyFactory.file("aspect", "lara", "js").setLabel("Aspect");

    DataKey<String> MAIN_ASPECT = KeyFactory.string("main").setLabel("Main Aspect");

    DataKey<String> ASPECT_ARGS = KeyFactory.string("argv").setLabel("Aspect Arguments")
            .setCustomGetter(LaraIKeyFactory::customGetterLaraArgs);

    // DataKey<DataStore> WEAVER_ARGS = KeyFactory.dataStore("argw", new StoreDefinitionBuilder("Weaver Options"))
    // .setLabel("Weaver Options");

    // DataKey<File> WORKSPACE_FOLDER = KeyFactory.folder("workspace", false).setLabel("Source Folder");
    // DataKey<FileList> WORKSPACE_FOLDER = LaraIKeyFactory.folderList("workspace").setLabel(
    DataKey<FileList> WORKSPACE_FOLDER = LaraIKeyFactory.fileList("workspace", JFileChooser.FILES_AND_DIRECTORIES,
                    Collections.emptyList())
            .setLabel("Sources");
    // .setDefault(() -> new FileList(Collections.emptyList()));

    DataKey<Map<File, File>> WORKSPACE_EXTRA = KeyFactory.filesWithBaseFolders("workspace_extra")
                    .setLabel("Additional Sources (separated by ;)");

    DataKey<File> OUTPUT_FOLDER = KeyFactory.folder("output", false)
            .setLabel("Output Folder")
            // According to LaraIDataStore
            .setDefault(() -> new File("."));

    DataKey<VerboseLevel> VERBOSE = KeyFactory.enumeration("verbose", VerboseLevel.class).setLabel("Verbose Level")
            .setDefault(() -> VerboseLevel.warnings);
    // .setDecoder(StringCodec.newInstance(level -> Integer.toString(level.ordinal()),
    // string->VerboseLevel.values()[Integer.parseInt(string)]));

    DataKey<OptionalFile> LOG_FILE = LaraIKeyFactory.optionalFile("log", false).setLabel("Use Log File");

    DataKey<Boolean> LOG_JS_OUTPUT = KeyFactory.bool("javascript").setLabel("Log JavaScript Output");
    DataKey<Boolean> DEBUG_MODE = KeyFactory.bool("debug").setLabel("Debug Mode");

    DataKey<Boolean> RESTRICT_MODE = KeyFactory.bool("restrict mode")
            .setLabel("Restrict mode (some Java classes are not allowed)");

    DataKey<JsEngineType> JS_ENGINE = KeyFactory.enumeration("jsEngine", JsEngineType.class)
            .setLabel("JavaScript Engine")
            // TODO: Change to GraalVM when transition is done
            .setDefault(() -> JsEngineType.GRAALVM_COMPAT);
    // .setDefault(() -> JsEngineType.GRAALVM);


    DataKey<FileList> JAR_PATHS = LaraIKeyFactory.fileList("jarPaths", JFileChooser.FILES_AND_DIRECTORIES, Set.of("jar"))
            .setLabel("Paths to JARs")
            .setDefault(() -> FileList.newInstance());

    // No GUI, only CLI
    DataKey<Boolean> UNIT_TEST_MODE = KeyFactory.bool("unit_test_mode").setLabel("Unit-testing mode");
    // DataKey<StringList> UNIT_TEST_ARGS = KeyFactory.stringList("unit_test_args").setLabel("Unit-testing arguments");
    // .setLabel("Unit-testing arguments");
    DataKey<List<String>> UNIT_TEST_ARGS = KeyFactory.generic("unit_test_args", new ArrayList<>());

    // No GUI, only CLI
    DataKey<Boolean> GENERATE_DOCUMENTATION = KeyFactory.bool("generateDoc").setLabel("Generate Documentation");

    // No GUI or CLI
    // When generating JS from LARA, disables use of 'with' keyword.
    // Disabling 'with' breaks LARA files that use the keyword 'output', since output variables do not get automatically
    // associated with 'this'. For LARA files to work, they need to be changed to add 'this' to each output variable
    // use.
    // E.g., 'output var1 end' implies that uses must be something like 'this.var1'.
    DataKey<Boolean> DISABLE_WITH_KEYWORD_IN_LARA_JS = KeyFactory.bool("disableWithKeywordInLaraJs")
            .setLabel("Disable 'with' keyword in Lara JS");

    DataKey<Boolean> SHOW_HELP = KeyFactory.bool("help").setLabel("Show Help");

    // DataKey<WeaverEngine> WEAVER_INSTANCE = KeyFactory.object("weaver instance", WeaverEngine.class);

    /// Keys outside of the definition

    // If DataStore comes from a configuration file, stores the path to the file
    // DataKey<Optional<File>> CONFIGURATION_FILE = KeyFactory.optional("configurationFile");

    StoreDefinition STORE_DEFINITION = new StoreDefinitionBuilder("LaraI Options")
            .addKeys(LARA_FILE, MAIN_ASPECT, ASPECT_ARGS, WORKSPACE_FOLDER, WORKSPACE_EXTRA, OUTPUT_FOLDER,
                    VERBOSE, LOG_FILE, LOG_JS_OUTPUT,
                    DEBUG_MODE, RESTRICT_MODE, JS_ENGINE, JAR_PATHS, SHOW_HELP)
            .build();

}
