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

package larac.options.optionprovider;

/**
 * Description of a program option
 * 
 * @author Tiago
 * 
 */
public class Descriptor {

	private String name;
	private String shortName;
	private ArgOption numOfArguments;
	private String argumentName;
	private String description;

	/**
	 * Creates a new Descriptor
	 * 
	 * @param name
	 *            the name of the option
	 * @param shortName
	 *            short version for the option
	 * @param numOfArguments
	 *            number of arguments the option requires (see {@link ArgOption}
	 *            )
	 * @param description
	 *            a description for the option
	 */
	public Descriptor(String name, String shortName, ArgOption numOfArguments, String description) {
		setName(name);
		setShortName(shortName);
		setNumOfArguments(numOfArguments);
		setArgumentName(null);
		setDescription(description);
	}

	/**
	 * Creates a new Descriptor (with a name for the option argument)
	 * 
	 * @param name
	 *            the name of the option
	 * @param shortName
	 *            short version for the option
	 * @param numOfArguments
	 *            number of arguments the option requires (see {@link ArgOption}
	 *            )
	 * @param description
	 *            a description for the option
	 */
	public Descriptor(String name, String shortName, ArgOption numOfArguments, String argumentName,
			String description) {
		setName(name);
		setShortName(shortName);
		setNumOfArguments(numOfArguments);
		setArgumentName(argumentName);
		setDescription(description);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the shortName
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 * @return the numOfArguments
	 */
	public ArgOption getNumOfArguments() {
		return numOfArguments;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	protected void setName(String name) {
		this.name = name;
	}

	/**
	 * @param shortName
	 *            the shortName to set
	 */
	protected void setShortName(String shortName) {
		this.shortName = shortName;
	}

	/**
	 * @param numOfArguments
	 *            the numOfArguments to set
	 */
	protected void setNumOfArguments(ArgOption numOfArguments) {
		this.numOfArguments = numOfArguments;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	protected void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the argumentName
	 */
	public String getArgumentName() {
		return argumentName;
	}

	/**
	 * @param argumentName
	 *            the argumentName to set
	 */
	protected void setArgumentName(String argumentName) {
		this.argumentName = argumentName;
	}

}
