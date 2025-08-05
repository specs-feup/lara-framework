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
import java.util.stream.Collectors;

import pt.up.fe.specs.util.SpecsIo;

public class GeneratedFile {

    private final String filename;
    private final String code;
    private final List<String> packagePrefix;

    public GeneratedFile(String filename, String code, List<String> packagePrefix) {
        this.filename = filename;
        this.code = code;
        this.packagePrefix = packagePrefix;
    }

    /**
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @return the packagePrefix
     */
    public List<String> getPackagePrefix() {
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
        return packagePrefix.stream().collect(Collectors.joining("/")) + "/" + filename;
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
