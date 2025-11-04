/**
 * Copyright 2016 SPeCS.
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

package org.lara.interpreter.joptions.keys;

import java.io.File;

import pt.up.fe.specs.util.parsing.StringCodec;

public class OptionalFile {

    private File file;
    private boolean used;

    public static OptionalFile newInstance(String fileStr) {
        // if fileStr is null or is empty then it is not used by default!

        File f = fileStr != null ? new File(fileStr) : new File("");
        boolean used = fileStr != null && !fileStr.isEmpty();
        return new OptionalFile(f, used);
    }

    public OptionalFile(File file, boolean used) {
        this.file = file;
        this.used = used;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "[" + (isUsed() ? 'X' : ' ') + "] " + file.toString();
    }

    public static String encode(OptionalFile optionalFile) {
        if (optionalFile.isUsed()) {
            return optionalFile.file.toString();
        }

        return "";
    }

    public static StringCodec<OptionalFile> getCodec() {
        return StringCodec.newInstance(OptionalFile::encode, OptionalFile::newInstance);
    }
}
