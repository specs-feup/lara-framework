/**
 * Copyright 2023 SPeCS.
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

package org.lara.interpreter.weaver.generator.generator.templated;

import java.io.File;
import java.util.List;

import pt.up.fe.specs.util.SpecsIo;

public record GeneratedFile(String filename, String code, List<String> packagePrefix) {

    /**
     * @return the filename
     */
    @Override
    public String filename() {
        return filename;
    }

    /**
     * @return the code
     */
    @Override
    public String code() {
        return code;
    }

    /**
     * @return the packagePrefix
     */
    @Override
    public List<String> packagePrefix() {
        return packagePrefix;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "GeneratedFile [filename=" + filename + ", code=" + code + ", packagePrefix=" + packagePrefix + "]";
    }

    public String getRelativePath() {
        return String.join("/", packagePrefix) + "/" + filename;
    }

    public boolean write(File outputDir, boolean replace) {
        var outputFile = new File(outputDir, getRelativePath());
        if (replace || !outputFile.exists()) {
            SpecsIo.write(outputFile, code);
            return true;
        }
        return false;
    }

}
