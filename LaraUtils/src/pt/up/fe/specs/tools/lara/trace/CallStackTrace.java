/**
 * Copyright 2017 SPeCS.
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

package pt.up.fe.specs.tools.lara.trace;

import java.util.Stack;

import pt.up.fe.specs.tools.lara.logging.LaraLog;

public class CallStackTrace {

    public static final String STACKTRACE_NAME = "_STACK_TRACE_";
    public static final String RETURN_FOR_STACK_STRACE = "_RETURN_FOR_STACK_TRACE_WRAPPER_";
    private Stack<Trace> callStack;

    public CallStackTrace() {
        LaraLog.setDebug(true);
        callStack = new Stack<>();
    }

    public void push(String callName, String location) {
        callStack.push(Trace.newInstance(callName, location));
        LaraLog.debug(toString());
    }

    public void push(String callName) {
        push(callName, null);
    }

    public Trace pop() {
        if (isEmpty()) {
            LaraLog.debug(toString());
            return null;
        }
        Trace pop = callStack.pop();
        LaraLog.debug(toString());
        return pop;
    }

    public boolean isEmpty() {
        return callStack.isEmpty();
    }

    @Override
    public String toString() {
        return callStack.toString();
    }
}
