/*
 * Copyright 2013 SPeCS.
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

/**
 * Class containing all the predefined messages used on Lara
 * 
 * @author Tiago Carvalho
 * 
 */
public class MessageConstants {
    public static final String OVERLINE = " -==================================================-";
    public static final String UNDERLINE = MessageConstants.OVERLINE;

    public static int order = 1;

    public static final String getElapsedTimeMessage(long timeMillis) {
        return getElapsedTimeMessage(timeMillis, "Elapsed Time");
    }

    public static final String getElapsedTimeMessage(long timeMillis, String text) {
        StringBuilder sb = new StringBuilder(MessageConstants.UNDERLINE + "\n");
        sb.append("  " + text + ": " + timeMillis + "ms\n");
        sb.append(MessageConstants.OVERLINE + "");
        return sb.toString();
    }

    public static final String getHeaderMessage(int order, String text) {
        StringBuilder sb = new StringBuilder(MessageConstants.UNDERLINE + "\n");
        sb.append("  " + order + ". " + text + "\n");
        sb.append(MessageConstants.OVERLINE + "");
        return sb.toString();
    }
}
