/**
 * Copyright 2024 SPeCS.
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

package pt.up.fe.specs.lara;

import java.util.Optional;

public class WeaverResult {

    private final boolean isSuccess;
    private final String[] args;
    private final Exception exception;

    private WeaverResult(String[] args, boolean isSuccess, Exception exception) {
        this.args = args;
        this.isSuccess = isSuccess;
        this.exception = exception;
    }

    /**
     * For executions without exception.
     * 
     * @param isSuccess
     */
    public WeaverResult(String[] args, boolean isSuccess) {
        this(args, isSuccess, null);
    }

    /**
     * Unsuccessful execution with exception.
     * 
     * @param e
     */
    public WeaverResult(String[] args, Exception e) {
        this(args, false, e);
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public String[] getArgs() {
        return args;
    }

    public Optional<Exception> getException() {
        return Optional.ofNullable(exception);
    }

}
