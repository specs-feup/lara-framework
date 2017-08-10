/**
 * Copyright 2013 SPeCS Research Group.
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

package larac.utils.xml.entity;

import java.util.ArrayList;
import java.util.List;

import larac.utils.xml.Pair;

/**
 * Represents an attribute pertaining to an artifact
 * 
 * @author Tiago
 * 
 */
public class Attribute {
	private String name;
	private String type;
	private boolean canDefine;
	/****** List<Pair<Type, Name>> parameters **/
	private List<Pair<String, String>> parameters;

	/**
	 * Create a new attribute with name and type
	 * 
	 * @param name
	 *            the name of the attribute
	 * @param type
	 *            the type of the attribute
	 */
	public Attribute(String name, String type) {
		this.name = name;
		this.type = type;
		canDefine = false;
		parameters = new ArrayList<>();
	}

	/**
	 * Add a new parameter to the attribute
	 * 
	 * @param type
	 *            the type of the attribute
	 * @param name
	 *            the name of the attribute
	 */
	public void addParameter(String type, String name) {
		parameters.add(new Pair<>(type, name));
	}

	/**
	 * @return the parameters
	 */
	public List<Pair<String, String>> getParameters() {
		return parameters;
	}

	/**
	 * @param parameters
	 *            the parameters to set
	 */
	public void setParameters(List<Pair<String, String>> parameters) {
		this.parameters = parameters;
	}

	/**
	 * @return the canDefine
	 */
	public boolean isCanDefine() {
		return canDefine;
	}

	/**
	 * @param canDefine
	 *            the canDefine to set
	 */
	public void setCanDefine(boolean canDefine) {
		this.canDefine = canDefine;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
}
