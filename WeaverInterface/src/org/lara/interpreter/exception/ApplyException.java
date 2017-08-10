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

import org.lara.interpreter.weaver.interf.JoinPoint;

import pt.up.fe.specs.tools.lara.exception.BaseException;

public class ApplyException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	// private JoinPoint[] currentJoinPoints;
	private String select;

	public ApplyException(String name, String select, JoinPoint[] currentJoinPoints, Throwable e) {
		super(e);
		init(name, select, currentJoinPoints);
	}

	private void init(String applyName, String selectName, JoinPoint[] currentJoinPoints) {
		name = applyName;
		select = selectName;
		// this.currentJoinPoints = currentJoinPoints;
	}

	@Override
	protected String generateMessage() {
		// List<JoinPoint> asList = Arrays.asList(currentJoinPoints);
		// String chain = StringUtils.join(asList, JoinPoint::get_class, "->");
		return "Exception " + generateSimpleMessage();// + ". current chain: " +
														// chain;
	}

	@Override
	protected String generateSimpleMessage() {
		return "apply " + name + "(to " + select + ")";
	}
}
