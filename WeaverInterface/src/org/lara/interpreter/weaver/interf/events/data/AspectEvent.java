/**
 * Copyright 2014 SPeCS Research Group.
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

package org.lara.interpreter.weaver.interf.events.data;

import org.lara.interpreter.weaver.interf.events.Stage;

public class AspectEvent extends BaseEvent {

	private String called_by;
	private String aspect_name;
	private Args args;
	private Object exception;

	// public AspectEvent(Stage stage, String aspect_name, String called_by) {
	// super(stage);
	// this.called_by = called_by;
	// this.aspect_name = aspect_name;
	// }

	public AspectEvent(Stage stage, String aspect_name, String called_by, String[] params, Object[] args,
			Object exception) {
		super(stage);
		this.called_by = called_by;
		this.aspect_name = aspect_name;
		this.setArgs(params, args);
		this.exception = exception;
	}

	//
	// public AspectEvent(Stage stage, String aspect_name, String called_by,
	// Object exception) {
	// super(stage);
	// this.aspect_name = aspect_name;
	// this.called_by = called_by;
	// this.setException(exception);
	// }

	@Override
	public String toString() {
		String ret = super.toString();
		ret += ", aspect " + aspect_name;
		if (!called_by.isEmpty()) {
			ret += ", called by " + called_by;
		}
		if (getStage().equals(Stage.BEGIN)) {
			ret += ", inputs: {";
		} else {
			ret += ", outputs: {";
		}

		if (!args.isEmpty()) {
			final String[] params = args.getParameters();
			Object argument = args.getArgument(params[0]);
			if (argument instanceof String) {
				argument = "'" + argument + "'";
			}
			ret += params[0] + ": " + argument;
			for (int i = 1; i < params.length; i++) {
				argument = args.getArgument(params[i]);
				if (argument instanceof String) {
					argument = "'" + argument + "'";
				}
				ret += ", " + params[i] + ": " + argument;
			}
		}
		ret += "}";
		if (exception != null) {
			ret += ", exception thrown: " + exception;
		}
		return ret;
	}

	/**
	 * @return the aspectCaller
	 */
	public String getAspectCaller() {
		return called_by;
	}

	/**
	 * @param aspectCaller
	 *            the aspectCaller to set
	 */
	protected void setAspectCaller(String aspectCaller) {
		called_by = aspectCaller;
	}

	/**
	 * @return the aspectCallee
	 */
	public String getAspectCallee() {
		return aspect_name;
	}

	/**
	 * @param aspectCallee
	 *            the aspectCallee to set
	 */
	protected void setAspectCallee(String aspectCallee) {
		aspect_name = aspectCallee;
	}

	/**
	 * @return the exception
	 */
	public Object getException() {
		return exception;
	}

	/**
	 * @param exception
	 *            the exception to set
	 */
	protected void setException(Object exception) {
		this.exception = exception;
	}

	/**
	 * @return the args
	 */
	public Args getArgs() {
		return args;
	}

	/**
	 * @param args
	 *            the args to set
	 */
	protected void setArgs(Args args) {
		this.args = args;
	}

	private void setArgs(String[] params, Object[] args2) {
		args = new Args(params, args2);
	}
}
