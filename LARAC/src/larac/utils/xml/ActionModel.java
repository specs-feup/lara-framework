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
package larac.utils.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.lara.language.specification.LanguageSpecification;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import larac.utils.xml.entity.ActionArgument;

@Deprecated
public class ActionModel extends XMLParser {
	public ActionModel(String fileName) throws ParserConfigurationException, SAXException, IOException {
		super(fileName);
	}

	public ActionModel(File file) throws ParserConfigurationException, SAXException, IOException {
		super(file);
	}

	/*************************
	 * For test purposes ******************************** public static void
	 * main(String[] args) {
	 * 
	 * ActionModel attributes = new ActionModel("actions.xml");
	 * System.out.println("Optimize: "+attributes.contains("optimize"));
	 * System.out.println("Xpto: "+attributes.contains("xpto")); HashMap
	 * <String,ActionArgument> attrs =
	 * attributes.getOptimizationParams("optimize", "loopunroll");
	 * for(ActionArgument arg: attrs.values()){ String name = arg.getName();
	 * String type = arg.getType(); String value = arg.getValue();
	 * System.out.println("Argument: name="+name+" type="+type+(arg.hasValue()?(
	 * " value="+value):"")); } } /
	 ****************************************************************************/
	public Map<String, ActionArgument> getActionParams(String action, LanguageSpecification spec) {
		final Node actions = getDoc().getElementsByTagName("actions").item(0);
		final List<Node> childs = getDirectChilds((Element) actions, action);

		for (final Node child : childs) {
			final Element actionEl = (Element) child;

			final Map<String, ActionArgument> actionArguments = new LinkedHashMap<>();
			for (final Node actionArg : getDirectChilds(actionEl, "parameter")) {
				final ActionArgument arg = new ActionArgument(((Element) actionArg).getAttribute("name"),
						((Element) actionArg).getAttribute("type"), spec);
				if (((Element) actionArg).hasAttribute("default")) {
					arg.setValue(((Element) actionArg).getAttribute("default"));
				}
				actionArguments.put(arg.getName(), arg);
			}
			return actionArguments;
		}
		return null;
	}

	public boolean contains(String action) {
		final Node actions = getDoc().getElementsByTagName("actions").item(0);
		final List<Node> childs = getDirectChilds((Element) actions, action);
		if (childs != null) {
			return !childs.isEmpty();
		}
		return false;
	}

	public ActionModel copy() throws ParserConfigurationException, SAXException, IOException {
		return new ActionModel(getFileName());
	}

	protected List<Node> getDirectChilds(Element joinPoint, String tag) {
		final NodeList descendentNodes = joinPoint.getElementsByTagName(tag);
		final List<Node> childs = new ArrayList<>();
		if (descendentNodes.getLength() == 0) {
			return childs;
		}
		for (int i = 0; i < descendentNodes.getLength(); i++) {
			if (descendentNodes.item(i).getParentNode().getNodeName().equals(joinPoint.getNodeName())) {
				childs.add(descendentNodes.item(i));
			}
		}
		return childs;
	}
}
