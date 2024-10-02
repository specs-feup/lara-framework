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
import java.util.*;

import javax.swing.JFileChooser;

import org.lara.interpreter.joptions.keys.FileList;
import org.lara.interpreter.joptions.keys.OptionalFile;
import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Datakey.KeyFactory;
import org.suikasoft.jOptions.storedefinition.StoreDefinition;
import org.suikasoft.jOptions.storedefinition.StoreDefinitionBuilder;

import pt.up.fe.specs.jsengine.JsEngineType;
import pt.up.fe.specs.util.utilities.StringList;

public interface LaraiKeys {

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

    // DataKey<DataStore> WEAVER_ARGS = KeyFactory.dataStore("argw", new StoreDefinitionBuilder("Weaver Options"))
    // .setLabel("Weaver Options");

    // DataKey<File> WORKSPACE_FOLDER = KeyFactory.folder("workspace", false).setLabel("Source Folder");
    // DataKey<FileList> WORKSPACE_FOLDER = LaraIKeyFactory.folderList("workspace").setLabel(
    DataKey<FileList> WORKSPACE_FOLDER = LaraIKeyFactory.fileList("workspace", JFileChooser.FILES_AND_DIRECTORIES,
            Collections.emptyList())
            .setLabel("Sources");
    // .setDefault(() -> new FileList(Collections.emptyList()));

    // DataKey<String> WORKSPACE_EXTRA = KeyFactory.string("workspace_extra")
    // .setLabel("Additional Sources (separated by ;)");

    DataKey<Map<File, File>> WORKSPACE_EXTRA = KeyFactory.filesWithBaseFolders("workspace_extra")
            .setLabel("Additional Sources (separated by ;)");

    DataKey<File> OUTPUT_FOLDER = KeyFactory.folder("output", false)
            .setLabel("Output Folder")
            // According to LaraIDataStore
            .setDefault(() -> new File("."));

    DataKey<FileList> INCLUDES_FOLDER = LaraIKeyFactory.folderList("include")
            .setLabel("Includes Folder (LARA, JS scripts, JARs)");

    DataKey<Boolean> AUTOMATICALLY_IMPORT_JS = KeyFactory.bool("autoimportjs")
            .setLabel("Automatically import JS files in include folders");

    DataKey<StringList> EXTERNAL_DEPENDENCIES = KeyFactory.stringList("external_dependencies")
            .setLabel("External dependencies (URLs, git repos)");

    DataKey<OptionalFile> TOOLS_FILE = LaraIKeyFactory.optionalFile("tools", true, "xml").setLabel("Tools File");

    DataKey<OptionalFile> REPORT_FILE = LaraIKeyFactory.optionalFile("report", false, "js").setLabel("Report File");

    DataKey<OptionalFile> METRICS_FILE = LaraIKeyFactory.optionalFile("metrics", false, "js").setLabel("Metrics File");

    DataKey<VerboseLevel> VERBOSE = KeyFactory.enumeration("verbose", VerboseLevel.class).setLabel("Verbose Level")
            .setDefault(() -> VerboseLevel.warnings);
    // .setDecoder(StringCodec.newInstance(level -> Integer.toString(level.ordinal()),
    // string->VerboseLevel.values()[Integer.parseInt(string)]));

    DataKey<OptionalFile> LOG_FILE = LaraIKeyFactory.optionalFile("log", false).setLabel("Use Log File");

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
    // .setDefault(() -> JsEngineType.GRAALVM);


    DataKey<FileList> JAR_PATHS = LaraIKeyFactory.fileList("jarPaths", JFileChooser.FILES_AND_DIRECTORIES, Set.of("jar"))
            .setLabel("Paths to JARs")
            .setDefault(() -> FileList.newInstance());

    DataKey<Boolean> API_AUTOLOAD = KeyFactory.bool("api_autoload")
            .setLabel("Autoload select APIs into execution environment").setDefault(() -> true);

    // No GUI, only CLI
    DataKey<Boolean> UNIT_TEST_MODE = KeyFactory.bool("unit_test_mode").setLabel("Unit-testing mode");
    // DataKey<StringList> UNIT_TEST_ARGS = KeyFactory.stringList("unit_test_args").setLabel("Unit-testing arguments");
    // .setLabel("Unit-testing arguments");
    DataKey<List<String>> UNIT_TEST_ARGS = KeyFactory.generic("unit_test_args", new ArrayList<>());

    DataKey<String> CALL_ARGS = KeyFactory.string("call_args").setLabel(
            "If present, interpret aspect file as the full path to an aspect to be called, and this argument as the arguments to use to call the aspect");

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

    // DataKey<WeaverEngine> WEAVER_INSTANCE = KeyFactory.object("weaver instance", WeaverEngine.class);

    /// Keys outside of the definition

    // If DataStore comes from a configuration file, stores the path to the file
    // DataKey<Optional<File>> CONFIGURATION_FILE = KeyFactory.optional("configurationFile");

    StoreDefinition STORE_DEFINITION = new StoreDefinitionBuilder("LaraI Options")
            .addKeys(LARA_FILE, MAIN_ASPECT, ASPECT_ARGS, WORKSPACE_FOLDER, WORKSPACE_EXTRA, OUTPUT_FOLDER,
                    INCLUDES_FOLDER, AUTOMATICALLY_IMPORT_JS, EXTERNAL_DEPENDENCIES, TOOLS_FILE, REPORT_FILE,
                    METRICS_FILE, VERBOSE, LOG_FILE, LOG_JS_OUTPUT,
                    DEBUG_MODE, TRACE_MODE, BUNDLE_TAGS, RESTRICT_MODE, JS_ENGINE, API_AUTOLOAD, JAR_PATHS)
            .build();

    // StoreDefinition STORE_DEFINITION_EXTRA = new StoreDefinitionBuilder("LaraI Options Extra")
    // .addKeys(CONFIGURATION_FILE)
    // .build();

    /**
     * Backup code
     */
    // DataKey<VerboseLevel> VERBOSE = LaraiKeyFactory.radioEnum("b", VerboseLevel.class)

    // DataKey<OptionalFile> INCLUDES_FOLDER = LaraiKeyFactory.optionalFolder("include")
    // // .setDefault(DEFAULT_DIR)
    // DataKey<Boolean> LOG_FILE = KeyFactory.bool("l").setLabel("Use log file");

    // .setLabel("Includes Folder");
    // DataKey<Boolean> SHOW_HELP = KeyFactory.bool("help").setLabel("Show Help");

    // DataKey<ClassProvider> WEAVER_CLASS = LaraiKeyFactory.classProvider("w")
    // .setDefault(ClassProvider.newInstance(DefaultWeaver.class))
    // .setLabel("Weaver Class");
    // DataKey<File> LANGUAGE_SPECIFICATION_FOLDER = KeyFactory.folder("x")
    // .setDefault(LaraiKeys.DEFAULT_DIR)
    // .setLabel("Language Specification");

    // DataKey<OptionsParser> CLI_PARSER = KeyFactory.object("cli_parser", OptionsParser.class);
}
