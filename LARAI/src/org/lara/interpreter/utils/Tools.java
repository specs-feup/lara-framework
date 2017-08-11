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
package org.lara.interpreter.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import larac.utils.xml.XMLParser;

/**
 * Read an XML file containing the properties of the tools
 * 
 * @author Tiago Carvalho
 * 
 */
public class Tools extends XMLParser {

    /**
     * Create a org.w3c.dom.Document based on the xml input file
     * 
     * @param fileName
     *            xml input file
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public Tools(String fileName) throws ParserConfigurationException, SAXException, IOException {
	this(new File(fileName));
    }

    public Tools(File file) throws ParserConfigurationException, SAXException, IOException {
	super(file);
    }

    /**
     * See if a certain tool is available
     * 
     * @param tool
     *            the tool to search
     * @return returns true if the tools is available, false if not
     */
    public boolean contains(String tool) {
	final Node tools = getDoc().getElementsByTagName("tools").item(0);
	final List<Node> childs = getDirectChilds((Element) tools, tool);
	if (childs != null) {
	    return !childs.isEmpty();
	}
	return false;
    }

    /**
     * Get the properties of a tool
     * 
     * @param tool
     *            the required tool
     * @return the Element of the Tool
     */
    public Element getToolElementProperties(String tool) {
	final Node tools = getDoc().getElementsByTagName("tools").item(0);
	final List<Node> childs = getDirectChilds((Element) tools, tool);
	return (Element) childs.get(0);
    }

    /**
     * Returns the direct Element children to a certain node Element
     * 
     * @param parent
     *            the parent of the children
     * @param tag
     *            the name of the children
     * @return a List containing the children
     */
    protected List<Node> getDirectChilds(Element parent, String tag) {
	final NodeList descendentNodes = parent.getElementsByTagName(tag);
	if (descendentNodes.getLength() == 0) {
	    return null;
	}
	final List<Node> childs = new ArrayList<>();
	for (int i = 0; i < descendentNodes.getLength(); i++) {
	    if (descendentNodes.item(i).getParentNode().getNodeName().equals(parent.getNodeName())) {
		childs.add(descendentNodes.item(i));
	    }
	}
	return childs;
    }
}
