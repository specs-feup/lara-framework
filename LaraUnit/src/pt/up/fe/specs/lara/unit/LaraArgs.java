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

import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.parsing.arguments.ArgumentsParser;
import pt.up.fe.specs.util.utilities.StringLines;

public class LaraArgs {

    private static final String ARGS_EXTENSION = "args";
    private static final String GLOBAL_ARGS_FILENAME = "test." + ARGS_EXTENSION;
    private static final String ARGS_COMMENT = "#";

    private static final String BASE_MACRO = "$BASE";

    private int includeIndex;

    public static String getArgsExtension() {
        return ARGS_EXTENSION;
    }

    public static String getGlobalArgsFilename() {
        return GLOBAL_ARGS_FILENAME;
    }

    public static String getLocalArgsFilename(File testFile) {
        return testFile.getName() + "." + ARGS_EXTENSION;
    }

    private final File baseFolder;
    private final List<String> currentArgs;

    public LaraArgs(File baseFolder) {
        this(baseFolder, new ArrayList<>());
    }

    private LaraArgs(File baseFolder, List<String> currentArgs) {
        this.baseFolder = baseFolder;
        this.currentArgs = currentArgs;

        includeIndex = -1;
    }

    public LaraArgs copy() {
        return new LaraArgs(baseFolder, new ArrayList<>(currentArgs));
    }

    public void addArgs(File argsFile) {

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
            addArgs(parser.parse(trimmedLine));
        }
    }

    public void addArgs(List<String> args) {
        args.stream().forEach(this::addArg);
    }

    public void addArg(String arg) {
        // Check if include
        if (arg.equals("-i")) {
            if (includeIndex != -1) {
                SpecsLogs.msgInfo("Detected multiple include argument '-i'");
            }

            includeIndex = currentArgs.size();
        }

        // Preprocess arg
        arg = arg.replace(BASE_MACRO, baseFolder.getAbsolutePath());

        currentArgs.add(arg);
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

    @Override
    public String toString() {
        return currentArgs.toString();
    }

    public void addLocalArgs(File testFile) {
        // Check if there is a local arguments file

        File parentFile = SpecsIo.getParent(testFile);
        File localArgsFile = new File(parentFile, LaraArgs.getLocalArgsFilename(testFile));

        if (!localArgsFile.isFile()) {
            return;
        }

        addArgs(localArgsFile);
    }

    public void addInclude(File baseFolder) {
        // If no includes flag, add it
        if (includeIndex == -1) {
            includeIndex = currentArgs.size();
            currentArgs.add("-i");
            currentArgs.add("");
        }

        // Includes
        int includesIndex = includeIndex + 1;

        String currentIncludes = currentArgs.get(includesIndex);
        String updatedIncludes = updateIncludes(currentIncludes, baseFolder.getAbsolutePath());
        currentArgs.set(includesIndex, updatedIncludes);
    }

    private String updateIncludes(String currentIncludes, String path) {
        if (currentIncludes.isEmpty()) {
            return path;
        }

        return currentIncludes + File.pathSeparator + path;
    }

}
