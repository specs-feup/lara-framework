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

public enum CLIOption implements WeaverOption {

    help("h", "print this message", null),
    version("v", "print version information and exit", null),
    javascript("j", "show the javascript output in the same stream as the application's output",
            LaraiKeys.LOG_JS_OUTPUT),
    debug("d", "show all process information", LaraiKeys.DEBUG_MODE),

    argv("av", OptionArguments.ONE_ARG, "arguments", "arguments for the main aspect", LaraiKeys.ASPECT_ARGS),
    // argw("aw", OptionArguments.SEVERAL_ARGS, "arguments", "arguments for the weaver", LaraiKeys.WEAVER_ARGS),

    output("o", OptionArguments.ONE_ARG, "dir", "change output dir", LaraiKeys.OUTPUT_FOLDER),
    workspace("p", OptionArguments.ONE_ARG, "dir", "change the working directory", LaraiKeys.WORKSPACE_FOLDER),
    verbose("b", OptionArguments.ONE_ARG, "level", "verbose level", LaraiKeys.VERBOSE),
    main("m", OptionArguments.ONE_ARG, "aspect", "select main aspect", LaraiKeys.MAIN_ASPECT),
    tools("t", OptionArguments.ONE_ARG, "tools.xml", "location of the tools description", LaraiKeys.TOOLS_FILE),
    log("l", OptionArguments.OPTIONAL_ARG, "fileName", "outputs to a log file", LaraiKeys.LOG_FILE),
    includes("i", OptionArguments.ONE_ARG, "dir",
            "includes folder (imports files with extensions: lara, jar, js, class)", LaraiKeys.INCLUDES_FOLDER),
    report("r", OptionArguments.ONE_ARG, "file_name.json", "Output file for the main aspect, in JSON format",
            LaraiKeys.REPORT_FILE),
    metrics("e", OptionArguments.ONE_ARG, "file_name.json", "Output file for the weaving metrics",
            LaraiKeys.METRICS_FILE);

    // weaver("w"), //I'm forcing these two arguments to be passed as java arguments in LARAI.exec
    // XMLspec("x"),

    private String shortArgument;
    private String description;
    private OptionArguments hasArgs;
    private String argName;
    private DataKey<?> dataKey;

    CLIOption(String shortArgument, String description, DataKey<?> dataKey) {
        this(shortArgument, OptionArguments.NO_ARGS, "", description, dataKey);
    }

    CLIOption(String shortArgument, OptionArguments args, String argName, String description, DataKey<?> dataKey) {
        this.shortArgument = shortArgument;
        this.description = description;
        hasArgs = args;
        this.argName = argName;
        this.dataKey = dataKey;
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
