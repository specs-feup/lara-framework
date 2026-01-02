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

package pt.up.fe.specs.tools.lara.exception;

import java.io.Serial;

/**
 * Abstract exception of a LARA exception. These type of exceptions are used to
 * encapsulate other thrown exceptions
 *
 * @author Tiago
 *
 */
public abstract class BaseException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, Throwable e) {
        super(message, e);
    }

    public BaseException(Throwable e) {
        super(e);
    }

    /**
     * Generate a complete message to present to the user, usually containing
     * 'Exception on' + generateSimpleMessage()
     *
     */
    protected abstract String generateMessage();

    /**
     * Generate the concrete message to present to the user. Usually this method
     * does not contain the 'Exception on' stuff, just get to the point!
     *
     */
    protected String generateSimpleMessage() {
        return LARAExceptionBuilder.getEvaluationExceptionMessage();
    }

    @Override
    public String getMessage() {
        return generateMessage();
    }

    protected String getDetailMessage() {
        return super.getMessage();
    }

    public String getSimpleMessage() {
        return generateSimpleMessage();
    }

    public RuntimeException generateRuntimeException() {
        LARAExceptionBuilder builder = generateExceptionBuilder();
        throw builder.getRuntimeException();
    }

    public LARAExceptionBuilder generateExceptionBuilder() {
        LARAExceptionBuilder builder = new LARAExceptionBuilder();
        generateException(builder);
        return builder;
    }

    protected void generateException(LARAExceptionBuilder builder) {
        String thisMessage = generateSimpleMessage();
        if (thisMessage != null) {
            builder.add(thisMessage);
        }
        Throwable causedBy = getCause();
        // If there was no cause, then this was the cause
        if (causedBy == null) {
            builder.setLastException(this);
            builder.setLastLARAException(this);
            builder.setLastTrace(getStackTrace());
            return;
        }

        // If the cause is a BaseException, then process it, recursively
        if (causedBy instanceof BaseException) {
            ((BaseException) causedBy).generateException(builder);
            return;
        }

        // Otherwise the cause is another type of exception and is considered
        // the last cause to capture
        builder.setLastException(causedBy);
        builder.setLastLARAException(this);
        builder.setLastTrace(causedBy.getStackTrace());
    }

    public boolean useLastMessage() {
        return false;
    }
}
