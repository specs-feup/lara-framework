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

package org.lara.interpreter.cli;

import org.apache.commons.cli.Option;
import org.lara.interpreter.weaver.options.OptionArguments;

public enum CLIConfigOption {

    config("c", OptionArguments.ONE_ARG, "file", "configuration file with defined options");

    private String shortOpt;
    private String description;
    private OptionArguments args;
    private String argName;

    CLIConfigOption(String shortOpt, String description) {
        this(shortOpt, OptionArguments.NO_ARGS, "", description);
    }

    CLIConfigOption(String shortOpt, OptionArguments args, String argName, String description) {
        this.shortOpt = shortOpt;
        this.description = description;
        this.args = args;
        this.argName = argName;
    }

    public String getShortOpt() {
        return shortOpt;
    }

    public String getDescription() {
        return description;
    }

    public OptionArguments getHasArgs() {
        return args;
    }

    public String getArgName() {
        return argName;
    }

    public Option buildOption() {
        return OptionsBuilderUtils.newOption(shortOpt, name(), args, getArgName(),
                description);
    }

    public static boolean contains(String optionName) {
        for (final CLIConfigOption opt : CLIConfigOption.values()) {
            if (opt.name().equals(optionName)) {
                return true;
            }
        }
        return false;
    }

    public boolean sameAs(String optionName) {
        for (final CLIConfigOption opt : CLIConfigOption.values()) {
            if (opt.name().equals(optionName) || opt.getShortOpt().equals(optionName)) {
                return true;
            }
        }
        return false;
    }
}
