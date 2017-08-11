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

public class MessageDef {

	public boolean normal = true;
	public boolean error = true;
	public boolean warning = true;
	private int verbose;

	public MessageDef(int level) {
		setDef(level);
	}

	public MessageDef() {
		setDef(3);
	}

	public void setDef(int level) {
		verbose = level;
		switch (level) {
		case 0:
			normal = error = warning = false;
			break;
		case 1:
			normal = false;
			warning = false;
			error = true;
			break;
		case 2:
			normal = false;
			warning = true;
			error = true;
			break;
		case 3:
			normal = true;
			warning = true;
			error = true;
			break;
		default:
			break;
		}
	}

	public int getVerbose() {
		return verbose;
	}
}
