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

import java.util.ArrayList;
import java.util.List;

import pt.up.fe.specs.tools.lara.trace.CallStackTrace;
import pt.up.fe.specs.tools.lara.trace.Trace;
import tdrc.utils.StringUtils;

public class LARAExceptionBuilder {

    private static final String EVALUATION_EXCEPTION_MESSAGE = "when evaluating javascript";

    public static String getEvaluationExceptionMessage() {
        return EVALUATION_EXCEPTION_MESSAGE;
    }

    // private static final String EXCEPTION_HEADER = "An exception occured";
    private static final String INDENT = " ";
    private static final String SEPARATOR = "\n";
    // private static final int PREDEFINED_SPACE =
    // java.lang.RuntimeException.class.getCanonicalName().length() + 2;
    private static final int PREDEFINED_SPACE = 0;
    private List<String> messages;
    // private Set<String> addedMessages;
    private StackTraceElement[] lastTrace;
    private BaseException lastLARAException;
    private Throwable lastException;
    private CallStackTrace stackTrace;

    private static final List<String> ignoredPackages;

    static {
        ignoredPackages = new ArrayList<>();
        LARAExceptionBuilder.ignoredPackages.add("sun.reflect.");
        LARAExceptionBuilder.ignoredPackages.add("java.lang.reflect.");
        LARAExceptionBuilder.ignoredPackages.add("org.mozilla.javascript.");
        LARAExceptionBuilder.ignoredPackages.add("java.util.");
    }

    public LARAExceptionBuilder(CallStackTrace stacktrace) {
        this();
        this.stackTrace = stacktrace;
    }

    public LARAExceptionBuilder() {

        setMessages(new ArrayList<>());
    }

    public RuntimeException getRuntimeException() {

        if (lastException != lastLARAException) {
            Throwable last = lastException;
            while (last != null) {

                String message = last.getMessage();
                String causeMessage = "caused by " + last.getClass().getSimpleName()
                        + (message != null ? ": " + message : "");
                // if (causeMessage != null) {
                add(causeMessage);

                // }
                lastTrace = last.getStackTrace(); // We will only show the cause
                // trace
                last = last.getCause();
                // System.out.println();
            }

            // String causeMessage = lastException.getMessage();
            // if (causeMessage != null) {
            // add(causeMessage);
            // }
        }

        String str = "\n";// + LARAExceptionBuilder.EXCEPTION_HEADER + ":\n";
        StringBuilder completeMessage = new StringBuilder(str);
        String separator = LARAExceptionBuilder.SEPARATOR;
        int predefinedSpace = LARAExceptionBuilder.PREDEFINED_SPACE;
        String indentStr = LARAExceptionBuilder.INDENT;
        if (stackTrace != null) {
            // completeMessage.append(indentStr + messages.get(0));
            // int i = 1;
            List<String> locations = new ArrayList<>();
            Trace trace = stackTrace.pop();
            while (trace != null) {
                locations.add(trace.toString());
                trace = stackTrace.pop();
            }

            int size = locations.size() - 1;
            for (int i = size; i >= 0; i--) {
                int repeatValue = (size - i) + 1;
                if (repeatValue > 15) {
                    repeatValue = 15;
                }
                String indentation = StringUtils.repeat(" ", predefinedSpace)
                        + StringUtils.repeat(indentStr, repeatValue);
                String finalMessage = indentation + locations.get(i);
                completeMessage.append(separator + finalMessage);
            }
            int repeatValue = size + 2;
            if (repeatValue > 16) {
                repeatValue = 16;
            }
            String indentation = StringUtils.repeat(" ", predefinedSpace) + StringUtils.repeat(indentStr, repeatValue);

            // for (String message : messages) {
            // completeMessage.append(separator + indentation + message);
            // }

            // String lastMessage = messages.get(messages.size() - 1);
            String lastMessage = getLastMessage();

            completeMessage.append(separator + indentation + lastMessage);
            // String firstMessage = messages.get(0);
            // completeMessage.append(separator + indentation + firstMessage);
        } else if (!messages.isEmpty()) {

            completeMessage.append(indentStr + messages.get(0));
            for (int i = 1; i < messages.size(); i++) {
                String indentation = StringUtils.repeat(" ", predefinedSpace) + StringUtils.repeat(indentStr, i + 1);
                String message = messages.get(i);
                message = message.replaceAll("\n", "\n" + indentation);
                String finalMessage = indentation + message;
                completeMessage.append(separator + finalMessage);
            }
        }

        // Class<? extends Throwable> type;
        // if (lastException != null) {
        // type = lastException.getClass();
        // } else {
        // type = lastLARAException.getClass();
        // }
        // String lastSpace = StringUtils.repeat(indentStr, messages.size() +
        // 2);
        // completeMessage.append(separator + StringUtils.repeat(" ",
        // predefinedSpace) + lastSpace + "Exception type: "
        // + type.toString().replaceAll("class", "").trim());
        // String string = message.toString().isEmpty() ? "" :
        // message.toString();
        // String separator = causeClass.isEmpty() || string.isEmpty() ? "" : ":
        // ";
        // String completeMessage = causeClass + separator + string;
        // RuntimeException re = new RuntimeException(completeMessage.toString());// ,
        // lastException);

        RuntimeException re = new RuntimeException(completeMessage.toString(), lastException);

        // re.setStackTrace(cleanTrace());
        return re;
    }

    private String getLastMessage() {
        for (String message : messages) {
            if (EVALUATION_EXCEPTION_MESSAGE.equals(message)) {
                continue;
            }

            return message;
        }

        return "<no message>";
        // return messages.get(messages.size() - 1);

    }

    /**
     * Remove unnecessary Elements, such as package org.mozilla.javascript
     * 
     * @return
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
        // if (addedMessages.contains(string)) {
        // return;
        // }

        messages.add(string);
        // addedMessages.add(string);
    }

    /**
     * @return the message
     */
    public List<String> getMessages() {
        return messages;
    }

    /**
     * @param message
     *            the message to set
     */
    public void setMessages(List<String> messages) {
        this.messages = messages;
        // this.addedMessages = new HashSet<>(messages);
    }

    /**
     * @return the lastLARAException
     */
    public BaseException getLastLARAException() {
        return lastLARAException;
    }

    /**
     * @param lastLARAException
     *            the lastLARAException to set
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
     * @param lastException
     *            the lastException to set
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
     * @param lastTrace
     *            the lastTrace to set
     */
    public void setLastTrace(StackTraceElement[] lastTrace) {
        this.lastTrace = lastTrace;
    }

}
