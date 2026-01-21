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
 * specific language governing permissions and limitations under the License.
 */
package org.lara.interpreter.utils;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.lara.interpreter.cli.CLIOption;
import org.lara.interpreter.cli.OptionsParser;
import pt.up.fe.specs.util.SpecsSystem;

public class LaraIUtils {

    public static boolean printHelp(CommandLine cmd, Options options) {
        if (cmd.hasOption(CLIOption.help.shortOption())) {
            System.out.println(OptionsParser.getHelp(options));
            return true;
        }
        if (cmd.hasOption(CLIOption.version.shortOption())) {
            var implVersion = SpecsSystem.getBuildNumber();
            if (implVersion == null) {
                implVersion = "<build number not found>";
            }

            System.out.println("Build: " + implVersion);

            return true;
        }
        return false;
    }
}
