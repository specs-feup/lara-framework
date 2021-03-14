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

import org.lara.interpreter.weaver.interf.WeaverEngine;

import pt.up.fe.specs.tools.lara.logging.LaraLog;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.parsing.arguments.ArgumentsParser;
import pt.up.fe.specs.util.utilities.StringLines;

public class LaraArgs {

    private static final String ARGS_EXTENSION = "args";
    private static final String GLOBAL_ARGS_FILENAME = "global." + ARGS_EXTENSION;
    private static final String ARGS_COMMENT = "#";

    private static final String BASE_MACRO = "$BASE";
    private static final String SEPARATOR_MACRO = "$SEP";

    private static final String IGNORE_DIRECTIVE = "Ignore";
    private static final String IGNORE_ARG = "$IGNORE_TEST";

    private int includeIndex;

    public static String getArgsExtension() {
        return ARGS_EXTENSION;
    }

    public static String getGlobalArgsFilename() {
        return GLOBAL_ARGS_FILENAME;
    }

    public static String getLocalArgsFilename(File testFile) {
        return SpecsIo.removeExtension(testFile.getName()) + "." + ARGS_EXTENSION;
    }

    private final WeaverEngine weaverEngine;
    private final File baseFolder;
    private final List<String> currentArgs;

    public LaraArgs(WeaverEngine weaverEngine, File baseFolder) {
        this(weaverEngine, baseFolder, new ArrayList<>());
    }

    private LaraArgs(WeaverEngine weaverEngine, File baseFolder, List<String> currentArgs) {
        this.weaverEngine = weaverEngine;
        this.baseFolder = baseFolder;
        this.currentArgs = currentArgs;

        includeIndex = getIncludeIndex(currentArgs);
    }

    private int getIncludeIndex(List<String> args) {
        for (int i = 0; i < args.size(); i++) {
            var arg = args.get(i);
            if (arg.equals("-i") || arg.equals("--includes")) {
                return i;
            }
        }

        // No include index
        return -1;
    }

    public static String getIgnoreArg() {
        return IGNORE_ARG;
    }

    public LaraArgs copy() {
        return new LaraArgs(weaverEngine, baseFolder, new ArrayList<>(currentArgs));
    }

    public void addArgs(File argsFile) {

        ArgumentsParser parser = ArgumentsParser.newCommandLine();

        boolean ignoreArgs = false;

        for (String line : StringLines.getLines(argsFile)) {

            String trimmedLine = line.trim();

            // Ignore empty lines
            if (trimmedLine.isEmpty()) {
                continue;
            }

            // Ignore comments
            if (trimmedLine.startsWith(ARGS_COMMENT)) {
                continue;
            }

            // Check if Ignore directive
            if (trimmedLine.equals(IGNORE_DIRECTIVE)) {
                addArg(IGNORE_ARG);
                continue;
            }

            // Check if weaver section start
            if (trimmedLine.startsWith("Weaver ")) {
                // Check if section should be used by current weaver
                var weaverName = trimmedLine.substring("Weaver ".length()).trim();
                var isValidSection = weaverEngine.getName().equals(weaverName);
                var message = isValidSection ? "it will be used" : "it will be ignored";
                LaraLog.debug(
                        "Found " + weaverName + " specific section during parsing of " + argsFile + ", " + message);

                ignoreArgs = !isValidSection;
                continue;
            }

            // Check if section end
            if (trimmedLine.equals("end")) {
                // If currently in an ignore state, re-enable
                if (ignoreArgs == true) {
                    ignoreArgs = false;
                    LaraLog.debug("Reenable parsing of arguments");
                }
                continue;
            }

            // If in an section for other weaver, ignore
            if (ignoreArgs) {
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
        arg = arg.replace(SEPARATOR_MACRO, SpecsIo.getUniversalPathSeparator());

        currentArgs.add(arg);
    }

    public List<String> getCurrentArgs() {
        return currentArgs;
    }

    public void addGlobalArgs(File testPath) {

        File testFolder = testPath.isDirectory() ? testPath : testPath.getParentFile();

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

        return currentIncludes + SpecsIo.getUniversalPathSeparator() + path;
    }

    public boolean hasArg(String arg) {
        return currentArgs.stream()
                .filter(currentArg -> currentArg.equals(arg))
                .findFirst()
                .isPresent();
    }
}
