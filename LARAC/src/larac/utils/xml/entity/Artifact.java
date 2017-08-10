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

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an artifact (of a join point) containing its attributes
 * 
 * @author Tiago
 * 
 */
public class Artifact {
	private String name;
	private Attribute defaultAttribute;
	private Map<String, Attribute> attributes;

	/**
	 * Create an artifact with a name
	 * 
	 * @param name
	 *            the name of the artifact
	 */
	public Artifact(String name) {

		this.name = name;
		attributes = new HashMap<>();
	}

	/**
	 * Add a new attribute
	 * 
	 * @param attr
	 *            the attribute to add
	 */
	public void add(Attribute attr) {
		attributes.put(attr.getName(), attr);
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
	 * @return the attributes
	 */
	public Map<String, Attribute> getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes
	 *            the attributes to set
	 */
	public void setAttributes(Map<String, Attribute> attributes) {
		this.attributes = attributes;
	}

	/**
	 * @return the defaultAttribute
	 */
	public Attribute getDefaultAttribute() {
		return defaultAttribute;
	}

	/**
	 * @param defaultAttribute
	 *            the defaultAttribute to set
	 */
	public void setDefaultAttribute(Attribute defaultAttribute) {
		this.defaultAttribute = defaultAttribute;
	}

	/**
	 * @param defaultAttribute
	 *            the default attribute name to set
	 */
	public void setDefaultAttribute(String defaultAttribute) {
		this.defaultAttribute = get(defaultAttribute);
	}

	/**
	 * Get an attribute from the artifact
	 * 
	 * @param attribute
	 *            the attribute to get
	 * @return
	 */
	public Attribute get(String attribute) {
		return getAttributes().get(attribute);
	}
}
