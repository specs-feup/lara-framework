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

public class AspectDefException extends BaseException {

    private static final String PREFIX = "aspect ";
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String name;
    private int laraLineNumber;
    private int jsLineNumber;
    private String aspectCoords;

    public AspectDefException(String name, String aspectCoords, int laraLineNumber, int jsLineNumber, Throwable e) {
        super(e);
        init(name, aspectCoords, laraLineNumber, jsLineNumber);
    }

    private void init(String name, String aspectCoords, int laraLineNumber, int jsLineNumber) {
        this.name = name;
        this.aspectCoords = aspectCoords;
        this.laraLineNumber = laraLineNumber;
        this.jsLineNumber = jsLineNumber;
    }

    @Override
    protected String generateMessage() {
        return "Exception " + generateSimpleMessage();
    }

    @Override
    protected String generateSimpleMessage() {
        String coords = "unknown";
        if (!aspectCoords.isEmpty()) {
            String[] splittedCoords = aspectCoords.split(":");
            if (splittedCoords.length > 0) {
                coords = splittedCoords[0];
                // int pos = 1;
                if (splittedCoords.length > 5) { // C:\...
                    coords += ":" + splittedCoords[1];
                    // pos++;
                }
                // coords += ", line #" + splittedCoords[pos];
            }
        }
        return AspectDefException.PREFIX + name + "("
                + (laraLineNumber > -1 ? "line #" + laraLineNumber + ", " : "")
                + "file '" + coords + "')";
    }

    protected String generateSimpleDebugMessage() {

        return generateSimpleMessage() + "(js line " + jsLineNumber + ")";
    }
}
