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

package org.lara.interpreter.cli;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Option.Builder;
import org.lara.interpreter.weaver.options.OptionArguments;
import org.lara.interpreter.weaver.options.WeaverOption;

public class OptionsBuilderUtils {

    /**
     * Create a New Option
     * 
     * @param argName
     * @param shortOpt
     * @param longOpt
     * @param argOption
     * @param description
     * @return
     */
    static Option newOption(String shortOpt, String longOpt, OptionArguments argOption, String argName,
            String description) {

        Builder builder;
        if (shortOpt == null) {
            builder = Option.builder();
        } else {
            builder = Option.builder(shortOpt);
        }
        if (!argName.isEmpty()) {
            builder.argName(argName);
        }
        builder.longOpt(longOpt);

        switch (argOption) {
            case NO_ARGS:
                break;
            case ONE_ARG:
                builder.hasArg();
                break;
            case SEVERAL_ARGS:
                builder.hasArgs();
                break;
            case OPTIONAL_ARG:
                builder.hasArg();
                builder.optionalArg(true);
                break;
            case OPTIONAL_ARGS:
                builder.hasArgs();
                builder.optionalArg(true);
                break;
            default:
                break;
        }

        builder.desc(description);

        return builder.build();
    }

    /**
     * New Option using the CLIOptions enum
     * 
     * @param option
     * @param description
     * @return
     */
    static Option newOption(CLIOption option, String argName, OptionArguments argOption,
            String description) {
        return newOption(option.shortOption(), option.name(), argOption, argName, description);
    }

    /**
     * New Option without arguments
     * 
     * @param option
     * @param description
     * @return
     */
    static Option newOption(CLIOption option, String description) {
        return newOption(option, "", OptionArguments.NO_ARGS, description);
    }

    static Option newOption(WeaverOption option) {

        return newOption(option.shortOption(), option.longOption(), option.args(), option.argName(),
                option.description());
    }

}
