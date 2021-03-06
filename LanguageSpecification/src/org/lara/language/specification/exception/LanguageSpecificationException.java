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

package org.lara.language.specification.exception;

import pt.up.fe.specs.tools.lara.exception.BaseException;

public class LanguageSpecificationException extends BaseException {

    private static final String DEFAULT_TEXT = "while building language specification";
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String message;

    public LanguageSpecificationException(Throwable e) {
	super(e);
    }

    public LanguageSpecificationException(String message, Throwable e) {
	super(e);
	this.message = message;
    }

    @Override
    protected String generateMessage() {

	return "Exception on ";
    }

    @Override
    protected String generateSimpleMessage() {
	// TODO Auto-generated method stub
	return this.message != null ? this.message : LanguageSpecificationException.DEFAULT_TEXT;
    }

}
