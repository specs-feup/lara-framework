/**
 *  Copyright 2015 SPeCS.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.lara.interpreter.exception;

import pt.up.fe.specs.tools.lara.exception.BaseException;

public class ScriptImportException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String source;
	private final boolean internalScript;

	public ScriptImportException(String source, boolean internalScript, Throwable e) {
		super(e);
		this.source = source;
		this.internalScript = internalScript;
	}

	@Override
	protected String generateMessage() {
		// TODO Auto-generated method stub
		return "Script evaluation exception " + generateSimpleMessage();
	}

	@Override
	protected String generateSimpleMessage() {
		String location = internalScript ? "internal" : "extern";
		return "when importing " + location + " script file: " + source;
	}

}
