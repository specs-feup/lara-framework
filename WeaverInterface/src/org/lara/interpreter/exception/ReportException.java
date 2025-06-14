/**
 * Copyright 2015 SPeCS.
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

import pt.up.fe.specs.tools.lara.exception.BaseException;

public class ReportException extends BaseException {

    private static final long serialVersionUID = 1L;
    private String when;
    private String attribute;

    public ReportException(String message, Throwable e) {
        this(message, null, e);
    }

    public ReportException(String message, String attribute, Throwable e) {
        super(e);
        when = message;
        this.attribute = attribute;
    }

    @Override
    protected String generateMessage() {
        return "Exception " + generateSimpleMessage();
    }

    @Override
    protected String generateSimpleMessage() {
        return when + (attribute != null ? "( in property " + attribute + ")" : "");
    }

}
