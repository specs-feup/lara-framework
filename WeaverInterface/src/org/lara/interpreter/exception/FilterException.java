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

import org.lara.interpreter.weaver.interf.JoinPoint;

import pt.up.fe.specs.tools.lara.exception.BaseException;

public class FilterException extends BaseException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final JoinPoint jp;
    private final String filter;

    public FilterException(JoinPoint jp, String filter, Throwable e) {
	super(e);
	this.jp = jp;
	this.filter = filter;
    }

    @Override
    protected String generateMessage() {
	return "Exception " + generateSimpleMessage();
    }

    @Override
    protected String generateSimpleMessage() {
	return "when filtering a join point (type: " + jp.get_class() + ") with the filter: " + filter;
    }

}
