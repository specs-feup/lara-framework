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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.lara.interpreter.weaver.interf.WeaverEngine;

import larai.LaraI;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;

public class LaraUnitTester {

    private static final String DEFAULT_TEST_FOLDERNAME = "test";

    private final WeaverEngine weaverEngine;

    public LaraUnitTester(WeaverEngine weaverEngine) {
        this.weaverEngine = weaverEngine;
    }

    public boolean test(String[] args) {
        return LaraI.exec(args, weaverEngine);
    }

    /**
     * 
     * @param baseFolder
     * @param testFolder
     *            can be null
     * @return
     */
    public LaraUnitReport testFolder(File baseFolder, File testFolder) {
        // Using LinkedHashMap to maitain the order of entries
        Map<File, List<TestResult>> testResults = new LinkedHashMap<>();

        testFolder = checkTestFolder(baseFolder, testFolder);
        if (testFolder == null) {
            return new LaraUnitReport(testResults);
        }

        // Get test files
        List<File> testFiles = SpecsIo.getFilesRecursive(testFolder, "lara");

        LaraArgs globalArguments = new LaraArgs(baseFolder);
        globalArguments.addGlobalArgs(testFolder);

        try (LaraUnitHarnessBuilder laraUnitHarness = new LaraUnitHarnessBuilder(weaverEngine, baseFolder,
                globalArguments);) {

            // Test each file
            // boolean passedAllTest = true;
            for (File testFile : testFiles) {
                List<TestResult> results = laraUnitHarness.testFile(testFile);
                testResults.put(testFile, results);
                // if (!testResult) {
                // passedAllTest = false;
                // }
                /*            // For each file, build an iterable
                Iterable<LaraUnitHarness> harnessIterable = laraUnitHarness.buildTests(testFile);
                
                Iterator<LaraUnitHarness> iterator = harnessIterable.iterator();
                while (iterator.hasNext()) {
                
                try (LaraUnitHarness laraTest = iterator.next()) {
                
                }
                
                }
                
                // Create test harness and arguments
                // Pair<File, String[]> testAndArgs = laraUnitHarness.buildTestAndArguments(testFile);
                // File testHarness = laraUnitHarness.buildTest(testFile);
                
                // Create arguments
                String[] args = laraUnitHarness.buildArguments(testFile);
                // LaraArgs testArguments = getTestArguments(globalArguments, baseFolder, testFolder);
                
                boolean success = test(args);
                if (!success) {
                passedAllTest = false;
                }
                */
            }

            return new LaraUnitReport(testResults);
        }

    }

    private File checkTestFolder(File baseFolder, File testFolder) {

        // If test folder is null, check if default folder exists
        if (testFolder == null) {
            File defaultTestFolder = new File(baseFolder, DEFAULT_TEST_FOLDERNAME);

            // If folder does not exist, exit with warning
            if (!defaultTestFolder.isDirectory()) {
                SpecsLogs.msgInfo("No test folder specified and no default 'test' folder found, returning");
                return null;
            }

            // Test folder exists, inform user
            SpecsLogs.msgInfo("Using default test folder '" + defaultTestFolder.getAbsolutePath() + "'");
            return defaultTestFolder;
        }

        // Check if test folder exists
        return testFolder.isDirectory() ? testFolder : null;
    }

    // private LaraArgs getTestArguments(LaraArgs globalArguments, File baseFolder, File testFolder) {
    //
    // LaraArgs testArguments = new LaraArgs();
    //
    // // Add lara file to test as first argument
    //
    // // Check if there is a custom args file
    // String customArgsFilename = SpecsIo.removeExtension(testFile) + LaraArgs.getArgsExtension();
    // File customArgsFile = new File(testFile.getParentFile(), customArgsFilename);
    //
    // if (customArgsFile.isFile()) {
    // testArguments = testArguments.copy();
    // globalArguments.addArgs(customArgsFile);
    // }
    // }

    // private boolean testFile(File baseFolder, File testFile) {
    // //
    // // TODO Auto-generated method stub
    // return true;
    // }
}
