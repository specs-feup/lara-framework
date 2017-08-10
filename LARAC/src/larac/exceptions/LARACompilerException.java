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

package larac.exceptions;

import pt.up.fe.specs.tools.lara.exception.BaseException;

public class LARACompilerException extends BaseException {

    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_TEXT = "when running LARAC";
    private String message;

    public LARACompilerException(String message, Throwable e) {
	super(e);
	this.message = message;
    }

    public LARACompilerException(Throwable e) {
	this(null, e);
    }

    public LARACompilerException(String message) {
	this(message, null);
    }

    @Override
    protected String generateMessage() {

	return "Exception on ";
    }

    @Override
    protected String generateSimpleMessage() {
	return this.message != null ? this.message : LARACompilerException.DEFAULT_TEXT;
    }
}
