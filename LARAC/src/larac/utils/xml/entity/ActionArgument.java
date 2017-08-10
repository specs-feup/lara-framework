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
package larac.utils.xml.entity;

import org.dojo.jsl.parser.ast.ASTLiteral;
import org.dojo.jsl.parser.ast.LARAEcmaScriptTreeConstants;
import org.dojo.jsl.parser.ast.SimpleNode;
import org.lara.language.specification.LanguageSpecification;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import larac.objects.Enums.Types;
import larac.utils.OrganizeUtils;

public class ActionArgument {
	private String name;
	private String type;
	private SimpleNode value;
	private LanguageSpecification langSpec;

	public ActionArgument(String name, String type, LanguageSpecification spec) {
		langSpec = spec;
		setName(name);
		setType(type);
	}

	public ActionArgument(String name, String type, String value) {
		setName(name);
		setType(type);
		setValue(value);
	}

	public boolean hasValue() {
		return value != null;
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

	/**
	 * @return the value
	 */
	public SimpleNode getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(SimpleNode value) {
		this.value = value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {

		final ASTLiteral lit = new ASTLiteral(LARAEcmaScriptTreeConstants.JJTLITERAL);
		lit.jjtSetValue(value);
		// try {
		if (type.contains("[]")) {

			lit.setType(Types.Array);
		} else {
			lit.setType(OrganizeUtils.getConvertedType(type, langSpec));
		}
		// } catch (Exception e) {
		// if (value.equals("null"))
		// lit.setType(Types.Null);
		// else
		// value.equals(Types.Object);
		// }
		this.value = lit;
	}

	public void toXML(Document doc, Element actEl) {
		if (value != null) {
			final Element argEL = doc.createElement("argument");
			actEl.appendChild(argEL);
			argEL.setAttribute("name", name);
			value.toXML(doc, argEL);
		}
	}
}
