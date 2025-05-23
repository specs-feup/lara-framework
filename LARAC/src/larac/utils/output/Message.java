/*
 * Copyright 2013 SPeCS.
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
package larac.utils.output;

import java.io.PrintStream;

public class Message {
	private final PrintStream output;
	protected static PrintStream defaulOutput = System.out;
	protected String tag = "";

	@SuppressWarnings("unused")
	private final boolean showStackTrace;

	protected Message(PrintStream stream, boolean showStackTrace) {
		output = stream;
		this.showStackTrace = showStackTrace;
	}

	public Message() {
		this(System.out, false);
		// output = System.out;
	}

	public Message(PrintStream stream) {
		this(stream, false);
		// output = stream;
	}

	public void print(String s) {
		output.print(tag + s);
		/*
		 * if (showStackTrace) { LoggingUtils.msgWarn(s); }
		 */
	}

	public void println(String s) {
		print(s + "\n");
	}

	public static void say(String message) {
		Message.defaulOutput.println(message);
	}

	public PrintStream getStream() {
		// TODO Auto-generated method stub
		return output;
	}
}
