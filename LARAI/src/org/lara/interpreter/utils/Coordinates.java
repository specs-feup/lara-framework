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

import java.util.Arrays;

import tdrc.utils.StringUtils;

public class Coordinates {

    private String fileName;
    private int lineBegin;
    private int lineEnd;
    private int columnBegin;
    private int columnEnd;
    private boolean wellParsed;

    public Coordinates(String coord) {
        String[] splitted = coord.split(":");
        // Will consider that last 4 positions are the coordenates and all
        // previous positions are related to the name
        // since the file path may contain, for instance, "C:\"
        if (splitted.length < 5) {
            wellParsed = false;
            fileName = coord;
            lineBegin = 0;
            lineEnd = 0;
            columnBegin = 0;
            columnEnd = 0;
            return;
        }
        int endPosition = splitted.length - 1;
        columnEnd = Integer.parseInt(splitted[endPosition]);
        endPosition--;
        lineEnd = Integer.parseInt(splitted[endPosition]);
        endPosition--;
        columnBegin = Integer.parseInt(splitted[endPosition]);
        endPosition--;
        lineBegin = Integer.parseInt(splitted[endPosition]);
        endPosition--;
        String[] onlyTheName = Arrays.copyOf(splitted, splitted.length - 4);
        fileName = StringUtils.join(Arrays.asList(onlyTheName), ":");
        wellParsed = true;

    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getLineBegin() {
        return lineBegin;
    }

    public void setLineBegin(int lineBegin) {
        this.lineBegin = lineBegin;
    }

    public int getLineEnd() {
        return lineEnd;
    }

    public void setLineEnd(int lineEnd) {
        this.lineEnd = lineEnd;
    }

    public int getColumnBegin() {
        return columnBegin;
    }

    public void setColumnBegin(int columnBegin) {
        this.columnBegin = columnBegin;
    }

    public int getColumnEnd() {
        return columnEnd;
    }

    public void setColumnEnd(int columnEnd) {
        this.columnEnd = columnEnd;
    }

    public boolean isWellParsed() {
        return wellParsed;
    }

    public void setWellParsed(boolean wellParsed) {
        this.wellParsed = wellParsed;
    }

    public String fileAndLineString() {
        return fileName + ", line " + lineBegin;
    }
}
