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
    output("o", OptionArguments.ONE_ARG, "dir", "change output dir", LaraiKeys.OUTPUT_FOLDER),
    dependencies("dep", OptionArguments.ONE_ARG, "urls",
            "external dependencies (URLs, git repos)", LaraiKeys.EXTERNAL_DEPENDENCIES),
    jsengine("js", OptionArguments.ONE_ARG, "engine name",
            "JS Engine to use. Available: NASHORN, GRAALVM_COMPAT, GRAALVM", LaraiKeys.JS_ENGINE);

    private String shortArgument;
    private String description;
    private OptionArguments hasArgs;
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
