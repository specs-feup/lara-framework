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

package org.lara.language.specification;

import java.io.OutputStream;

public interface IModel {

	/**
	 * Verify if the model contains the specified name
	 * 
	 * @param name
	 * @return
	 */
	public boolean contains(String name);

	/**
	 * Verify if the model contains the specified &lt;subname&gt; inside an
	 * element with atribute name=&lt;name&gt;
	 * 
	 * @param name
	 * @param subname
	 * @return
	 */
	public boolean contains(String name, String subname);

	/**
	 * Generate the model into an XML representation
	 * 
	 * @param oStream
	 *            the output stream to use
	 * @throws Exception
	 */
	public void toXML(OutputStream oStream) throws Exception;
}
