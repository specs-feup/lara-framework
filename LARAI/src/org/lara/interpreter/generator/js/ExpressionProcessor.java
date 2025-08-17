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

package org.lara.interpreter.generator.js;

import org.apache.commons.lang3.StringEscapeUtils;
import org.lara.interpreter.Interpreter;
import org.lara.interpreter.utils.LaraIUtils;

import pt.up.fe.specs.lara.aspectir.ExprId;
import pt.up.fe.specs.tools.lara.trace.CallStackTrace;

public class ExpressionProcessor {

    // private final Interpreter interpreter;

    // public ExpressionProcessor(Interpreter interpreter) {
    // this.interpreter = interpreter;
    // }

    // ================================================================================//
    // ==================================== ExprId ====================================//
    // ================================================================================//
    public static StringBuilder getJavascriptString(ExprId id, int depth) {
        return new StringBuilder(LaraIUtils.getSpace(depth)
                + (id.name.startsWith("@") ? Interpreter.getAttributes() + "." + id.name.substring(1) : id.name));
    }

    public static String pushToStack(String callee, String position) {
        return CallStackTrace.STACKTRACE_NAME + ".push('" + callee + "','" + StringEscapeUtils.escapeJava(position)
                + "')";
    }

    public static String popFromStack(String callee, String position) {
        return CallStackTrace.STACKTRACE_NAME + ".pop()";
    }

}
