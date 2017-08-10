/**
 * Copyright 2015 SPeCS Research Group.
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
import java.util.List;

import pt.up.fe.specs.tools.lara.exception.BaseException;
import tdrc.utils.StringUtils;

public class PointcutExprException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String name;
	private final int lineNumber;
	private final String[] chain;

	public PointcutExprException(String name, String[] chain, int lineNumber, Throwable e) {
		super(e);
		this.name = name;
		this.lineNumber = lineNumber;
		this.chain = chain;
	}

	@Override
	protected String generateMessage() {
		return "Pointcut creation exception for " + generateSimpleMessage();
	}

	@Override
	protected String generateSimpleMessage() {
		List<String> asList = Arrays.asList(chain);
		String chain = StringUtils.join(asList, "->");
		String lNStr = lineNumber > -1 ? "#" + lineNumber + ": " : "";
		return lNStr + "select " + name + "(" + chain + ")";
	}
}
