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
 * specific language governing permissions and limitations under the License.
 */

package pt.up.fe.specs.tools.lara.exception;

import java.util.ArrayList;
import java.util.List;

import pt.up.fe.specs.util.SpecsLogs;

public class LARAExceptionBuilder {

    private static final String EVALUATION_EXCEPTION_MESSAGE = "when evaluating javascript";

    public static String getEvaluationExceptionMessage() {
        return EVALUATION_EXCEPTION_MESSAGE;
    }

    private static final String INDENT = " ";
    private static final String SEPARATOR = "\n";
    private static final int PREDEFINED_SPACE = 0;
    private List<String> messages;
    private StackTraceElement[] lastTrace;
    private BaseException lastLARAException;
    private Throwable lastException;

    private static final List<String> ignoredPackages;

    static {
        ignoredPackages = new ArrayList<>();
        LARAExceptionBuilder.ignoredPackages.add("sun.reflect.");
        LARAExceptionBuilder.ignoredPackages.add("java.lang.reflect.");
        LARAExceptionBuilder.ignoredPackages.add("org.mozilla.javascript.");
        LARAExceptionBuilder.ignoredPackages.add("java.util.");
    }

    public LARAExceptionBuilder() {

        setMessages(new ArrayList<>());
    }

    public RuntimeException getRuntimeException() {
        if (lastException != null) {
            SpecsLogs.info("Last exception type: " + lastException.getClass());
        }
        if (lastException != null && lastException != lastLARAException) {
            Throwable last = lastException;
            while (last != null) {

                String message = last.getMessage();

                String causeMessage = message != null ? message : "caused by " + last.getClass().getSimpleName();

                add(causeMessage);

                lastTrace = last.getStackTrace(); // We will only show the cause trace
                last = last.getCause();
            }
        }

        String str = "\n";// + LARAExceptionBuilder.EXCEPTION_HEADER + ":\n";
        StringBuilder completeMessage = new StringBuilder(str);
        String indentStr = LARAExceptionBuilder.INDENT;
        if (!messages.isEmpty()) {

            completeMessage.append(indentStr).append(messages.get(0));
            for (int i = 1; i < messages.size(); i++) {
                String indentation = " ".repeat(LARAExceptionBuilder.PREDEFINED_SPACE) + indentStr.repeat(i + 1);
                String message = messages.get(i);
                message = message.replaceAll("\n", "\n" + indentation);
                String finalMessage = indentation + message;
                completeMessage.append(LARAExceptionBuilder.SEPARATOR).append(finalMessage);
            }
        }

        return new RuntimeException(completeMessage.toString(), lastException);
    }

    /**
     * Remove unnecessary Elements, such as package org.mozilla.javascript
     *
     */
    private StackTraceElement[] cleanTrace() {
        List<StackTraceElement> newTraceList = new ArrayList<>();
        for (StackTraceElement stackTraceElement : lastTrace) {
            String className = stackTraceElement.getClassName();

            if (LARAExceptionBuilder.ignoredPackages.stream().anyMatch(className::startsWith)) {
                continue;
            }
            /*
             * if (className.startsWith("sun.reflect.") ||
             * className.startsWith("java.lang.reflect.") ||
             * className.startsWith("org.mozilla.javascript.")) { continue; //
             * Ignore these packages }
             */
            newTraceList.add(stackTraceElement);
        }
        return newTraceList.toArray(new StackTraceElement[0]);
    }

    public void add(String string) {
        messages.add(string);
    }

    /**
     * @return the message
     */
    public List<String> getMessages() {
        return messages;
    }

    /**
     * @param messages the messages to set
     */
    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    /**
     * @return the lastLARAException
     */
    public BaseException getLastLARAException() {
        return lastLARAException;
    }

    /**
     * @param lastLARAException the lastLARAException to set
     */
    public void setLastLARAException(BaseException lastLARAException) {
        this.lastLARAException = lastLARAException;
    }

    /**
     * @return the lastException
     */
    public Throwable getLastException() {
        return lastException;
    }

    /**
     * @param lastException the lastException to set
     */
    public void setLastException(Throwable lastException) {
        this.lastException = lastException;
    }

    /**
     * @return the lastTrace
     */
    public StackTraceElement[] getLastTrace() {
        return cleanTrace();
    }

    /**
     * @param lastTrace the lastTrace to set
     */
    public void setLastTrace(StackTraceElement[] lastTrace) {
        this.lastTrace = lastTrace;
    }

}
