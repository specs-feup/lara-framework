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

package org.lara.interpreter.utils;

import org.lara.interpreter.exception.ApplyException;
import org.lara.interpreter.exception.AspectDefException;
import org.lara.interpreter.exception.UserException;
import org.lara.interpreter.weaver.interf.JoinPoint;

import pt.up.fe.specs.tools.lara.exception.BaseException;

public class ExceptionUtils {

    public static void throwApplyException(Object original, String applyLabel,
            String selectLabel, int currentLine) throws Throwable {

        Throwable e = getException(original);

        throw new ApplyException(applyLabel, selectLabel, new JoinPoint[0], e);
    }

    private static Throwable getException(Object original) {
        Throwable e;
        if (!(original instanceof Throwable)) {

            // If no option is available than it is treated as a string (using
            // toString);
            e = new RuntimeException(original.toString());
        } else {
            e = (Throwable) original;
        }
        return e;
    }

    public static void throwAspectException(Object original, String aspectName, String aspectCoords,
            int lineMapping) { // Map<String, Integer> lineMapping) {
        throw processAspectException(original, aspectName, aspectCoords, lineMapping);
    }

    private static RuntimeException processAspectException(Object original, String aspectName,
            String aspectCoords, int lineMapping) { // Map<String, Integer> lineMapping) {

        Throwable javaScriptException;
        if (original instanceof BaseException) {

            return processAspectException((BaseException) original, aspectName, aspectCoords, -1, lineMapping);
        }

        javaScriptException = getException(original);
        AspectDefException exception = new AspectDefException(aspectName, aspectCoords, -1, -1, javaScriptException);
        return exception;// new WrappedException(exception);
    }

    private static RuntimeException processAspectException(BaseException exception, String aspectName,
            String aspectCoords, int jsLine,
            int lineMapping) { // Map<String, Integer> lineMapping) {
        int line;
        if (exception instanceof UserException) {
            line = -1; // Will already be showned in its message

        } else {
            line = getLARALine(lineMapping, jsLine);
        }

        return new AspectDefException(aspectName, aspectCoords, line, jsLine, exception);

    }

    private static int getLARALine(int lineMapping, int jsLine) { // Map<String,
        // Integer>
        // lineMapping,
        // int
        // jsLine) {
        int laraLine = lineMapping > -1 ? lineMapping : -1;
        // int laraLine = -1;
        // if (jsLine > -1 && lineMapping.containsKey(jsLine)) { // > jsLine &&
        // jsLine > -1
        // laraLine = lineMapping.get(jsLine);
        // }
        // System.out.println("JSLINE: " + jsLine);
        return laraLine;
    }
}
