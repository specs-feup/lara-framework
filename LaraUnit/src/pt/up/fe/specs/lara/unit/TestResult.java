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

import java.util.Optional;

import com.google.common.base.Preconditions;

import pt.up.fe.specs.util.SpecsCollections;
import pt.up.fe.specs.util.lazy.Lazy;
import pt.up.fe.specs.util.utilities.StringLines;

public class TestResult {

    private final String moduleId;
    private final String testName;
    private final boolean success;
    private final long timeNanos;
    private final Throwable cause;

    private Lazy<Throwable> firstCause;

    public static TestResult success(String moduleId, String testName, long timeNanos) {
        return new TestResult(moduleId, testName, true, timeNanos, null);
    }

    public static TestResult fail(String moduleId, String testName, long timeNanos, Throwable cause) {
        Preconditions.checkNotNull(cause, "'cause' cannot be null");
        return new TestResult(moduleId, testName, false, timeNanos, cause);
    }

    private TestResult(String moduleId, String testName, boolean success, long timeNanos, Throwable cause) {
        this.moduleId = moduleId;
        this.testName = testName;
        this.success = success;
        this.timeNanos = timeNanos;
        this.cause = cause;
        this.firstCause = Lazy.newInstance(this::buildFirstCause);
    }

    public String getModuleId() {
        return moduleId;
    }

    public String getTestName() {
        return testName;
    }

    public boolean isSuccess() {
        return success;
    }

    public Throwable getCause() {
        return getCauseTry()
                .orElseThrow(() -> new RuntimeException("This test has not failed, and does not have a failure cause"));
    }

    public Optional<Throwable> getCauseTry() {
        return Optional.ofNullable(cause);
    }

    public Optional<Throwable> getFirstCause() {
        return Optional.ofNullable(firstCause.get());
    }

    private Throwable buildFirstCause() {
        if (cause == null) {
            return null;
        }

        Throwable firstCause = cause;
        Throwable nextCause = null;

        while ((nextCause = firstCause.getCause()) != null) {
            firstCause = nextCause;
        }

        return firstCause;
    }

    public long getTimeNanos() {
        return timeNanos;
    }

    public String getSimpleError() {
        Throwable firstCause = getFirstCause().get();

        // Return last line of message
        return SpecsCollections.last(StringLines.getLines(firstCause.getMessage())).trim();
    }

}
