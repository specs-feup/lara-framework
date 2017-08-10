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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import larac.utils.xml.entity.Artifact;
import larac.utils.xml.entity.Attribute;

@Deprecated
public class Artifacts extends XMLParser {
	private Map<String, Artifact> artifacts;
	// private HashMap<String, HashMap<String, Pair<String, String>>> artifacts;
	private Map<String, List<Element>> objects;

	// private Map<String, String> defaultProps;

	/**
	 * Returns the type of an attribute of a join point
	 * 
	 * @param joinpoint
	 *            the join point containing the attribute
	 * @param attributeString
	 *            the attribute to get the type
	 * @return the type of the attribute
	 */
	public String getAttributeType(String joinpoint, String attributeString) {
		// Pair<String, String> attrs = getAttributeFromArtifact(joinpoint,
		// attribute);
		final Attribute attribute = getAttributeFromArtifact(joinpoint, attributeString);

		if (attribute != null) {
			return attribute.getType();
		}
		return null;
	}

	/**
	 * Verifies if an attribute can be defined
	 * 
	 * @param joinpoint
	 *            the join point containing the attribute
	 * @param attributeString
	 *            the attribute to verify
	 * @return true if the attribute can be defined; false if not
	 */
	public boolean canDefine(String joinpoint, String attributeString) {
		final Artifact artifact = artifacts.get(joinpoint);
		if (artifact != null) {
			final Attribute attribute = artifact.get(attributeString);
			if (attribute != null) {
				return attribute.isCanDefine();
			}
		}

		// throw new
		// RuntimeException("The attribute "+attributeString+" from the
		// joinpoint "+joinpoint+" could not be found!");
		return false;
	}

	/**
	 * Returns an attribute from a join point. If the join point does not
	 * contain the attribute, it will search on the global artifact
	 * 
	 * @param joinpoint
	 *            the join point to search
	 * @param attributeString
	 *            the attribute to get
	 * @return the Attribute of the join point
	 */
	private Attribute getAttributeFromArtifact(String joinpoint, String attributeString) {

		final Artifact artifact = artifacts.get(joinpoint);
		if (artifact != null) {
			final Attribute attribute = artifact.get(attributeString);
			if (attribute != null) {
				return attribute;
			}
			return artifacts.get("global").get(attributeString);
		}
		// throw new RuntimeException("The joinpoint "+joinpoint+" could not be
		// found");
		return null;
	}

	/**
	 * Sees if a join point exists on the join point model
	 * 
	 * @param joinpoint
	 *            the join point to search
	 * @return true if the join point exists; false otherwise
	 */
	public boolean contains(String joinpoint) {
		return artifacts.containsKey(joinpoint);
	}

	/**
	 * Returns the default property of a join point
	 * 
	 * @param joinpoint
	 *            the join point to search
	 * @return the default property of the join point, or null if the join point
	 *         does not contain one
	 */
	public String getDefaultProperty(String joinpoint) {
		final Artifact artifact = artifacts.get(joinpoint);
		if (artifact != null) {
			return artifact.getDefaultAttribute().getName();
		}

		// throw new RuntimeException("The joinpoint "+joinpoint+" could not be
		// found");
		return null;
	}

	/**
	 * Get all attributes pertaining to a join point
	 * 
	 * @param joinpoint
	 *            the join point to search for its attributes
	 * @return a map containing the attributes
	 */
	public Map<String, Attribute> getAttributes(String joinpoint) {
		final Artifact artifact = artifacts.get(joinpoint);
		if (artifact != null) {
			return artifact.getAttributes();
		}
		// throw new RuntimeException("The joinpoint "+joinpoint+" could not be
		// found");
		return null;
	}

	/**
	 * Create a new
	 * 
	 * @param fileName
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public Artifacts(String fileName) throws ParserConfigurationException, SAXException, IOException {
		super(fileName);
		getAllArtifacts();
	}

	/**
	 * Create a new artifact
	 * 
	 * @param fileName
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public Artifacts(File file) throws ParserConfigurationException, SAXException, IOException {
		super(file);
		getAllArtifacts();
	}

	/*************************
	 * For test purposes ******************************** public static void
	 * main(String[] args) {
	 * 
	 * Artifacts attributes = new Artifacts("src\\artifacts.xml");
	 * attributes.printArtifacts();
	 * System.out.println(attributes.getAttributeType("var", "live_in_size"));
	 * 
	 * } /
	 ****************************************************************************/
	private Map<String, Artifact> getAllArtifacts() {
		artifacts = new HashMap<>();
		setObjects();
		final NodeList artifactList = getDoc().getElementsByTagName("artifact");
		for (int i = 0; i < artifactList.getLength(); i++) {
			final Node artifact = artifactList.item(i);
			if (artifact.getNodeType() == Node.ELEMENT_NODE) {
				final Element elArtifact = (Element) artifact;
				final String name = elArtifact.getAttribute("name");
				final Artifact newArtifact = new Artifact(name);
				// HashMap<String, Pair<String, String>> attrs = new
				// HashMap<String, Pair<String, String>>();

				addAttributesToArtifact(elArtifact, newArtifact);

				final String defaultProp = elArtifact.getAttribute("default");
				newArtifact.setDefaultAttribute(defaultProp);
				artifacts.put(name, newArtifact);
			}
		}

		final Artifact globalArtifact = new Artifact("global");
		artifacts.put("global", globalArtifact);

		final NodeList globalArtifacts = getDoc().getElementsByTagName("global");
		if (globalArtifacts.getLength() == 0) {
			return artifacts;
		}

		final Element elGlobalArtifact = (Element) getDoc().getElementsByTagName("global").item(0);

		addAttributesToArtifact(elGlobalArtifact, globalArtifact);

		// HashMap<String, Pair<String, String>> attrs = new HashMap<String,
		// Pair<String, String>>();

		return artifacts;
	}

	/**
	 * Add attributes to the artifact
	 * 
	 * @param elArtifact
	 *            the {@link Element} of the artifact
	 * @param artifact
	 *            the artifact to add the attributes
	 */
	private static void addAttributesToArtifact(Element elArtifact, Artifact artifact) {
		final NodeList attributeList = elArtifact.getElementsByTagName("attribute");

		for (int j = 0; j < attributeList.getLength(); j++) {
			final Node attribute = attributeList.item(j);
			if (attribute.getNodeType() == Node.ELEMENT_NODE) {
				final Element elAttribute = (Element) attribute;
				final String attributeName = elAttribute.getAttribute("name");
				final String attributeType = elAttribute.getAttribute("type");
				final String canDefineAttribute = elAttribute.getAttribute("define");
				final Attribute newAttribute = new Attribute(attributeName, attributeType);
				if (canDefineAttribute != null) {
					newAttribute.setCanDefine(Boolean.parseBoolean(canDefineAttribute));
				}

				addParametersToAttribute(elAttribute, newAttribute);

				artifact.add(newAttribute);
			}
		}

	}

	/**
	 * Adds parameters to the attribute, if any
	 * 
	 * @param attribute
	 *            the attribute to add the parameters
	 * @param elAttribute
	 *            {@link Element} of the attribute, possibly containing the
	 *            parameters
	 */
	private static void addParametersToAttribute(Element elAttribute, Attribute attribute) {
		final NodeList parameterList = elAttribute.getElementsByTagName("parameter");

		for (int j = 0; j < parameterList.getLength(); j++) {
			final Node parameterNode = parameterList.item(j);
			if (parameterNode.getNodeType() == Node.ELEMENT_NODE) {
				final Element elParameter = (Element) parameterNode;
				final String parameterName = elParameter.getAttribute("name");
				final String parameterType = elParameter.getAttribute("type");

				attribute.addParameter(parameterType, parameterName);
			}
		}
	}

	/**
	 * Sets the map containing the "classes" used in the attributes of the
	 * artifacts
	 */
	private void setObjects() {
		objects = new HashMap<>();
		final NodeList objectsList = getDoc().getElementsByTagName("object");
		for (int i = 0; i < objectsList.getLength(); i++) {
			final Node artifact = objectsList.item(i);
			if (artifact.getNodeType() == Node.ELEMENT_NODE) {
				final Element elObject = (Element) artifact;
				final String name = elObject.getAttribute("name");
				final List<Element> attributes = new ArrayList<>();
				final NodeList attributeList = elObject.getElementsByTagName("attribute");
				for (int j = 0; j < attributeList.getLength(); j++) {
					final Node attribute = attributeList.item(j);
					if (attribute.getNodeType() == Node.ELEMENT_NODE) {
						final Element elAttribute = (Element) attribute;
						attributes.add(elAttribute);
					}
				}
				objects.put(name, attributes);
			}
		}
	}

	/**
	 * @return the objects
	 */
	public Map<String, List<Element>> getObjects() {
		return objects;
	}

	public void printArtifacts() {
		for (final String pointcut : artifacts.keySet()) {

			System.out.println("On Pointcut: " + pointcut);

			final Map<String, Attribute> attrs = artifacts.get(pointcut).getAttributes();
			for (final String attribute : attrs.keySet()) {
				System.out.println("\t" + attribute + "=\"" + attrs.get(attribute).getType() + "\"");
			}
		}
	}

	public Artifacts copy() throws ParserConfigurationException, SAXException, IOException {

		return new Artifacts(getFileName());
	}
}
