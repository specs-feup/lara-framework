/**
 * Copyright 2015 SPeCS.
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

import java.io.File;
import java.util.Arrays;
import java.util.List;

import pt.up.fe.specs.tools.lara.exception.BaseException;
import tdrc.utils.StringUtils;

public class JavaImportException extends BaseException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    // PRIVATE FINAL FILE SOURCE;
    private final List<File> source;

    public JavaImportException(File javaSource, Throwable e) {
        super(e);
        this.source = Arrays.asList(javaSource);
        // this(, e);
    }

    public JavaImportException(List<File> files, Exception e) {
        super(e);
        this.source = files;
    }

    @Override
    protected String generateMessage() {
        // TODO Auto-generated method stub
        return "Exception " + generateSimpleMessage();
    }

    @Override
    protected String generateSimpleMessage() {
        return "when importing java: " + StringUtils.join(this.source, ",");
    }

}
