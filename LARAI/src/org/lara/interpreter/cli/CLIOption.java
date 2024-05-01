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
 * specific language governing permissions and limitations under the License. under the License.
 */
package org.lara.interpreter.cli;

import org.lara.interpreter.joptions.config.interpreter.LaraiKeys;
import org.lara.interpreter.weaver.options.OptionArguments;
import org.lara.interpreter.weaver.options.WeaverOption;
import org.suikasoft.jOptions.Datakey.DataKey;

/**
 * TODO: These should probably be replaced by calls to WeaverOptionBuilder.
 * TODO: The name of the enum is supposed to be the long option name...
 */
public enum CLIOption implements WeaverOption {

    help("h", "print this message", null),
    version("v", "print version information and exit", null),
    javascript("j", "show the javascript output in the same stream as the application's output",
            LaraiKeys.LOG_JS_OUTPUT),
    debug("d", "show all process information", LaraiKeys.DEBUG_MODE),
    stack("s", "show detailed call stack trace instead of LARAI trace", LaraiKeys.TRACE_MODE),
    argv("av", OptionArguments.ONE_ARG, "arguments",
            "arguments for the main aspect. Supports passing a .properties file with the arguments",
            LaraiKeys.ASPECT_ARGS),
    // argw("aw", OptionArguments.SEVERAL_ARGS, "arguments", "arguments for the weaver", LaraiKeys.WEAVER_ARGS),

    output("o", OptionArguments.ONE_ARG, "dir", "change output dir", LaraiKeys.OUTPUT_FOLDER),
    workspace("p", OptionArguments.ONE_ARG, "dir", "change the working directory", LaraiKeys.WORKSPACE_FOLDER),
    workspace_extra("pe", OptionArguments.ONE_ARG, "sources", "extra sources", LaraiKeys.WORKSPACE_EXTRA),
    verbose("b", OptionArguments.ONE_ARG, "level", "verbose level", LaraiKeys.VERBOSE),
    main("m", OptionArguments.ONE_ARG, "aspect", "select main aspect", LaraiKeys.MAIN_ASPECT),
    tools("t", OptionArguments.ONE_ARG, "tools.xml", "location of the tools description", LaraiKeys.TOOLS_FILE),
    log("l", OptionArguments.OPTIONAL_ARG, "fileName",
            "outputs to a log file. If file ends in .zip, compresses the file", LaraiKeys.LOG_FILE),
    includes("i", OptionArguments.ONE_ARG, "dir",
            "includes folder (imports files with extensions: lara, jar, js, class)", LaraiKeys.INCLUDES_FOLDER),
    autoimport("ai", OptionArguments.NO_ARGS, LaraiKeys.AUTOMATICALLY_IMPORT_JS),
    dependencies("dep", OptionArguments.ONE_ARG, "urls",
            "external dependencies (URLs, git repos)", LaraiKeys.EXTERNAL_DEPENDENCIES),
    report("r", OptionArguments.ONE_ARG, "file_name.json", "Output file for the main aspect, in JSON format",
            LaraiKeys.REPORT_FILE),
    metrics("e", OptionArguments.ONE_ARG, "file_name.json", "Output file for the weaving metrics",
            LaraiKeys.METRICS_FILE),
    loc("lc", OptionArguments.NO_ARGS, "loc", "LARA CSV with stats (LoC, #aspects, etc)",
            LaraiKeys.LARA_LOC),
    bundle_tags("bt", OptionArguments.ONE_ARG, "bundle tags",
            "Bundle tags, in the following format: <TAG1>=<VALUE1>[,<TAG_N>=<VALUE_N>]*",
            LaraiKeys.BUNDLE_TAGS),
    restrict("rm", OptionArguments.NO_ARGS, "restrict", "Restrict mode (some Java classes are not allowed)",
            LaraiKeys.RESTRICT_MODE),
    call("ca", OptionArguments.ONE_ARG, "call args", LaraiKeys.CALL_ARGS.getLabel(), LaraiKeys.CALL_ARGS),
    jsengine("js", OptionArguments.ONE_ARG, "engine name",
            "JS Engine to use. Available: NASHORN, GRAALVM_COMPAT, GRAALVM", LaraiKeys.JS_ENGINE),

    jarpaths("jp", OptionArguments.ONE_ARG, "dir1/file1[;dir2/file2]*", "JAR files that will be added to a separate classpath and will be accessible in scripts", LaraiKeys.JAR_PATHS),



    unit(LaraiKeys.getUnitTestFlag(), "run in unit test mode", LaraiKeys.UNIT_TEST_MODE),
    doc(LaraiKeys.getDocGeneratorFlag(), "generate documentation mode", LaraiKeys.GENERATE_DOCUMENTATION);
    // weaver("w"), //I'm forcing these two arguments to be passed as java arguments in LARAI.exec
    // XMLspec("x"),

    private String shortArgument;
    private String description;
    private OptionArguments hasArgs;

    // TODO: This argName seems to not be actually used, consider removing it. All constructors receive a DataKey, and should use its name
    private String argName;
    private DataKey<?> dataKey;

    CLIOption(String shortArgument, OptionArguments args, String argName, String description, DataKey<?> dataKey) {
        this.shortArgument = shortArgument;
        this.description = description;
        hasArgs = args;
        this.argName = argName;
        this.dataKey = dataKey;
    }

    CLIOption(String shortArgument, String description, DataKey<?> dataKey) {
        this(shortArgument, OptionArguments.NO_ARGS, "", description, dataKey);
    }

    CLIOption(String shortArgument, OptionArguments args, DataKey<?> dataKey) {
        this(shortArgument, args, dataKey.getName(), dataKey.getLabel(), dataKey);
    }

    @Override
    public String shortOption() {
        return shortArgument;
    }

    public static CLIOption getArgumentByShortName(String shortName) {
        for (final CLIOption arg : CLIOption.values()) {
            if (arg.shortArgument.equals(shortName)) {
                return arg;
            }
        }
        return null;
    }

    public static boolean contains(String argumentName) {
        for (final CLIOption opt : CLIOption.values()) {
            if (opt.name().equals(argumentName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsShort(String shortOption) {
        for (final CLIOption opt : CLIOption.values()) {
            if (opt.shortOption().equals(shortOption)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public OptionArguments args() {
        return hasArgs;
    }

    @Override
    public String argName() {
        return argName;
    }

    @Override
    public String longOption() {
        return name();
    }

    @Override
    public DataKey<?> dataKey() {
        return dataKey;
    }
}
