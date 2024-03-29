/**
 * Copyright 2022 SPeCS.
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

package pt.up.fe.specs.lara.importer;

import java.io.File;
import java.util.Optional;

import pt.up.fe.specs.jsengine.JsFileType;

public class LaraImportData {

    private final String filename;
    private final String code;
    private final JsFileType fileType;
    private final File jsFile;
    private String suffixCode;

    public LaraImportData(String filename, String code, JsFileType fileType, File jsFile) {
        this.filename = filename;
        this.code = code;
        this.fileType = fileType;
        this.jsFile = jsFile;
    }

    // public LaraImportData(String filename, String code, JsFileType fileType) {
    // this(filename, code, fileType, null);
    // }

    public String getFilename() {
        return filename;
    }

    public String getCode() {
        return code;
    }

    public JsFileType getFileType() {
        return fileType;
    }

    public Optional<File> getJsFile() {
        return Optional.ofNullable(jsFile);
    }

    /**
     * @return the postCode
     */
    public Optional<String> getSuffixCode() {
        return Optional.ofNullable(suffixCode);
    }

    /**
     * @param postCode
     *            the postCode to set
     */
    public void setSuffixCode(String postCode) {
        this.suffixCode = postCode;
    }

}
