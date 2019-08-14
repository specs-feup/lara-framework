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

import pt.up.fe.specs.tools.lara.exception.BaseException;

/**
 * This exception is used when the user throws an exception in LARA
 * 
 * @author Tiago
 *
 */
public class UserException extends BaseException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Object exception;
    private int lineNumber;

    public UserException(Object exception, int lineNumber) {
        // super(newRuntimeExceptionWithoutStackTrace(exception)); //Exception
        // with only the
        super(processException(exception));
        setStackTrace(new StackTraceElement[0]);
        setException(exception);
        this.lineNumber = lineNumber;
    }

    // private static RuntimeException
    // newRuntimeExceptionWithoutStackTrace(Object exception) {
    //
    // RuntimeException runtimeException = new
    // RuntimeException(exception.toString());
    // runtimeException.setStackTrace(new StackTraceElement[0]);
    // return runtimeException;
    // }

    private static Throwable processException(Object exception) {
        if (exception instanceof Throwable) {
            Throwable exception2 = (Throwable) exception;
            exception2.setStackTrace(new StackTraceElement[0]);
            return exception2;
        }
        return null;
    }

    @Override
    protected String generateMessage() {
        return generateSimpleMessage();
    }

    @Override
    protected String generateSimpleMessage() {
        return "User exception on line " + lineNumber + (getCause() == null ? ": " + getException() : "");
    }

    /**
     * @return the exception
     */
    public Object getException() {
        return exception;
    }

    /**
     * @param exception
     *            the exception to set
     */
    public void setException(Object exception) {
        this.exception = exception;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    @Override
    public boolean useLastMessage() {
        return true;
    }

}
