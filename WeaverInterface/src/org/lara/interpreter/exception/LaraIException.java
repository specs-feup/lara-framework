/**
 * Copyright 2015 SPeCS Research Group.
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

package org.lara.interpreter.exception;

import java.io.File;

import pt.up.fe.specs.tools.lara.exception.BaseException;

public class LaraIException extends BaseException {

    private static final long serialVersionUID = 1L;
    private final String inputName;
    private final String problem;

    public LaraIException(String problem) {
        this("", problem, null);
    }

    public LaraIException(String problem, Throwable e) {
        this("", problem, e);
    }

    public LaraIException(File file, String problem, Throwable e) {
        this(file.getName(), problem, e);
    }

    public LaraIException(String inputName, String problem, Throwable e) {
        super(e);
        this.inputName = inputName;
        this.problem = problem;
    }

    @Override
    protected String generateMessage() {
        return "LARAI Exception for" + generateSimpleMessage();
    }

    @Override
    protected String generateSimpleMessage() {
        String inputMessage = this.inputName.isEmpty() ? "" : " input '" + this.inputName + "' ";

        return inputMessage + this.problem;
    }

}
