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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import pt.up.fe.specs.util.SpecsStrings;

public class LaraUnitReport {

    public static LaraUnitReport failedReport() {
        return new LaraUnitReport(new HashMap<>(), true);
    }

    private final Map<File, List<TestResult>> results;
    private final boolean failed;

    public LaraUnitReport(Map<File, List<TestResult>> results) {
        this(results, false);
    }

    private LaraUnitReport(Map<File, List<TestResult>> results, boolean failed) {
        this.results = results;
        this.failed = failed;
    }

    public boolean isSuccess() {
        if (failed) {
            return false;
        }

        // Iterate over all test results, looking for a failure
        return results.values().stream()
                .flatMap(list -> list.stream())
                .parallel()
                .filter(test -> !test.isSuccess())
                .findAny()
                .map(test -> false)
                .orElse(true);
    }

    public String getReport() {

        StringBuilder failedTests = new StringBuilder();
        StringBuilder passedTests = new StringBuilder();

        int testCounter = 0;
        int failedTestsCounter = 0;

        for (Entry<File, List<TestResult>> entry : results.entrySet()) {
            File testFile = entry.getKey();
            List<TestResult> results = entry.getValue();

            // Increment test counter
            testCounter += results.size();

            for (TestResult testResult : results) {
                // Increment failure counter
                if (!testResult.isSuccess()) {
                    failedTestsCounter++;
                    addFailedTest(failedTests, testFile, testResult);
                } else {
                    addPassedTest(passedTests, testFile, testResult);
                }
            }

        }

        if (testCounter == 0) {
            return "No tests found";
        }

        int passedTestsCount = testCounter - failedTestsCounter;

        StringBuilder report = new StringBuilder();

        String failedTestsReport = failedTests.toString();
        if (!failedTestsReport.isEmpty()) {
            report.append("\nFailed tests:\n");
            report.append(failedTestsReport);
        }

        String passedTestsReport = passedTests.toString();
        if (!passedTestsReport.isEmpty()) {
            report.append("\nPassed tests:\n");
            report.append(passedTestsReport);
        }

        report.append("\nTotal Tests: " + testCounter).append("\n");
        report.append("Passed / Failed: " + passedTestsCount + " / " + failedTestsCounter).append("\n");

        if (passedTestsCount == testCounter) {
            report.append("\n ALL TEST PASSED!");
        } else {
            report.append("\n SOME TESTS FAILED");
        }

        return report.toString();
    }

    private void addPassedTest(StringBuilder passedTests, File testFile, TestResult testResult) {
        passedTests.append(getTestStatus(testFile, testResult));
    }

    private void addFailedTest(StringBuilder failedTests, File testFile, TestResult testResult) {
        failedTests.append(getTestStatus(testFile, testResult));
        failedTests.append(" [ERROR] ").append(testResult.getSimpleError()).append("\n");
        failedTests.append(" [STACK] ").append(testResult.getCause().getMessage()).append("\n\n");

    }

    private static String getTestStatus(File testFile, TestResult testResult) {
        return " - " + testFile.getName() + "::" + testResult.getTestName() + " ("
                + SpecsStrings.parseTime(testResult.getTimeNanos()) + ")\n";
    }

}
