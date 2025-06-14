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
package org.lara.interpreter.utils;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.lara.interpreter.cli.CLIOption;
import org.lara.interpreter.cli.OptionsParser;
import larai.LaraI;
import pt.up.fe.specs.util.SpecsSystem;
import pt.up.fe.specs.util.utilities.JarPath;

public class LaraIUtils {

    public static final String SPACE = "\t";

    public static String getSpace(int depth) {
        if (depth <= 0) {
            return "";
        }
        return String.format(String.format("%%0%dd", depth), 0).replace("0", LaraIUtils.SPACE);
    }

    public static boolean printHelp(CommandLine cmd, Options options) {
        if (cmd.hasOption(CLIOption.help.shortOption())) {
            System.out.println(OptionsParser.getHelp(options));
            return true;
        }
        if (cmd.hasOption(CLIOption.version.shortOption())) {
            System.out.println(LaraI.LARAI_VERSION_TEXT);

            var implVersion = SpecsSystem.getBuildNumber();
            if (implVersion == null) {
                implVersion = "<build number not found>";
            }

            System.out.println("Build: " + implVersion);

            return true;
        }
        return false;
    }

    /**
     * Enables lazy initialization of jarParth
     *
     * @author Joao Bispo
     */
    private static class JarPathHolder {
        public static final String instance = new JarPath(LaraI.class, LaraI.PROPERTY_JAR_PATH).buildJarPath();

    }

    public static String getJarFoldername() {
        return JarPathHolder.instance;
    }
}
