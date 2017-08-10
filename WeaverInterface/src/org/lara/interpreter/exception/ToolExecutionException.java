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

import java.util.Arrays;

import pt.up.fe.specs.tools.lara.exception.BaseException;
import tdrc.utils.StringUtils;

public class ToolExecutionException extends BaseException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String tool;
    private String[] args;

    public ToolExecutionException(String tool, String[] args, Throwable e) {
        super(e);
        this.tool = tool;
        this.args = args;
    }

    @Override
    protected String generateMessage() {
        return "Problem  " + generateSimpleMessage();
    }

    @Override
    protected String generateSimpleMessage() {
        String argsStr = StringUtils.join(Arrays.asList(this.args), ",");
        return "when executing " + this.tool + " with arguments [" + argsStr + "]";
    }

}
