/**
 * Copyright 2018 SPeCS.
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

package pt.up.fe.specs.lara.unit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pt.up.fe.specs.util.parsing.arguments.ArgumentsParser;
import pt.up.fe.specs.util.utilities.StringLines;

public class LaraArgs {

    private static final String ARGS_EXTENSION = "args";
    private static final String GLOBAL_ARGS_FILENAME = "test." + ARGS_EXTENSION;
    private static final String ARGS_COMMENT = "#";

    public static String getArgsExtension() {
        return ARGS_EXTENSION;
    }

    public static String getGlobalArgsFilename() {
        return GLOBAL_ARGS_FILENAME;
    }

    private final List<String> currentArgs;

    public LaraArgs() {
        this(new ArrayList<>());
    }

    private LaraArgs(List<String> currentArgs) {
        this.currentArgs = currentArgs;
    }

    public LaraArgs copy() {
        return new LaraArgs(new ArrayList<>(currentArgs));
    }

    public void addArgs(File argsFile) {
        // List of arguments
        // List<String> arguments = new ArrayList<>();

        ArgumentsParser parser = ArgumentsParser.newCommandLine();

        for (String line : StringLines.getLines(argsFile)) {
            String trimmedLine = line.trim();

            // Ignore empty lines
            if (line.isEmpty()) {
                continue;
            }

            // Ignore comments
            if (line.startsWith(ARGS_COMMENT)) {
                continue;
            }

            // Parse arguments
            currentArgs.addAll(parser.parse(trimmedLine));
        }
    }

    public List<String> getCurrentArgs() {
        return currentArgs;
    }

    public void addGlobalArgs(File testFolder) {
        // Check if there is a global arguments file
        File globalArgsFile = new File(testFolder, LaraArgs.getGlobalArgsFilename());

        if (!globalArgsFile.isFile()) {
            return;
        }

        addArgs(globalArgsFile);
    }

}
