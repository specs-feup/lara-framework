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

import java.util.List;

import pt.up.fe.specs.tools.lara.exception.BaseException;

public class SyntaxException extends BaseException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final List<Throwable> exceptions;

    public SyntaxException(List<Throwable> exceptions) {
	this(exceptions, null);
    }

    public SyntaxException(List<Throwable> exceptions, Throwable cause) {
	super(cause);
	this.exceptions = exceptions;
    }

    @Override
    protected String generateMessage() {
	return "Parsing problems";
    }

    @Override
    protected String generateSimpleMessage() {
	if (exceptions.isEmpty()) {
	    return "this message shouldn't even appear! the exceptions list is empty!";
	}
	if (exceptions.size() > 1) {
	    String string = "Multiple syntax problems:";
	    for (int i = 0; i < exceptions.size(); i++) {
		string += "\n " + (i + 1) + ". " + exceptions.get(i).getMessage();
	    }
	    // string += tdrc.utils.StringUtils.join(this.exceptions, Exception::getMessage, "\n");
	    return string;
	}
	return exceptions.get(0).getMessage();
    }

    public List<Throwable> getExceptions() {
	return exceptions;
    }

}
