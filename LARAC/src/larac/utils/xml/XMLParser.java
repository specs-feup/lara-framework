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
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLParser {

    private Document doc = null;
    private String space = "";
    private final String indentStr = "\t";
    protected String fileName;

    public String getFileName() {
	return fileName;
    }

    private void addSpace() {
	space += indentStr;
    }

    private void removeSpace() {
	space = space.replaceFirst(indentStr, "");
    }

    public XMLParser(String fileName) throws ParserConfigurationException, SAXException, IOException {
	this(new File(fileName));
    }

    public XMLParser(File file) throws ParserConfigurationException, SAXException, IOException {
	fileName = file.getName();
	setDoc(getDocument(file));
    }

    /******************************************************************************
     ****************************** Printing functions ****************************
     ******************************************************************************/
    public void print() {
	final NodeList childs = getDoc().getChildNodes();
	for (int i = 0; i < childs.getLength(); i++) {
	    final Node child = childs.item(i);

	    if (child.getNodeType() == Node.ELEMENT_NODE) {
		System.out.println(space + child.getNodeName());
		final Element elChild = ((Element) child); // todocv
		final NamedNodeMap attrs = elChild.getAttributes();
		for (int k = 0; k < attrs.getLength(); k++) {
		    System.out.println(
			    space + "-" + attrs.item(k).getNodeName() + " value=" + attrs.item(k).getNodeValue());
		}
		addSpace();
		printChild(childs.item(i));
		removeSpace();
	    }
	}
    }

    private void printChild(Node item) {
	final NodeList childs = item.getChildNodes();
	for (int i = 0; i < childs.getLength(); i++) {
	    final Node child = childs.item(i);
	    switch (child.getNodeType()) {
	    case Node.ELEMENT_NODE:
		System.out.println(space + child.getNodeName());
		final Element elChild = ((Element) child); // todocv
		final NamedNodeMap attrs = elChild.getAttributes();
		for (int k = 0; k < attrs.getLength(); k++) {
		    System.out.println(
			    space + "  -" + attrs.item(k).getNodeName() + "=\"" + attrs.item(k).getNodeValue()
				    + "\"");
		}
		addSpace();
		printChild(childs.item(i));
		removeSpace();
		break;
	    case Node.TEXT_NODE:
		String value = child.getNodeValue().replace("\n", "");
		value = value.replace("\r", "");
		value = value.replace("\t", "");
		value = value.replace(" ", "");
		if (!value.equals("")) {
		    System.out.println(space + "Value: " + value);
		}
		break;
	    default:
		break;
	    }
	}
    }

    /**
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     ****************************************************************************/

    // private static Document getDocument(String fileName)
    // throws ParserConfigurationException, SAXException, IOException {
    //
    // // Step 1: create a DocumentBuilderFactory
    // final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    //
    // // Step 2: create a DocumentBuilder
    // final DocumentBuilder db = dbf.newDocumentBuilder();
    // // Step 3: parse the input file to get a Document object
    // final Document doc = db.parse(new File(fileName));
    // return doc;
    // }

    /**
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     ****************************************************************************/

    private static Document getDocument(File file) throws ParserConfigurationException, SAXException, IOException {

	// Step 1: create a DocumentBuilderFactory
	final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

	// Step 2: create a DocumentBuilder
	final DocumentBuilder db = dbf.newDocumentBuilder();
	// Step 3: parse the input file to get a Document object
	final Document doc = db.parse(file);
	return doc;
    }

    public void setDoc(Document doc) {
	this.doc = doc;
    }

    public Document getDoc() {
	return doc;
    }

    public void saveDocument(String filename, String extention, Boolean header) throws Exception {

	final Transformer transformer = TransformerFactory.newInstance().newTransformer();
	transformer.setOutputProperty(OutputKeys.INDENT, "yes");

	// initialize StreamResult with File object to save to file
	final StreamResult result = new StreamResult(new StringWriter());
	final DOMSource source = new DOMSource(doc);
	transformer.transform(source, result);

	String xmlString = result.getWriter().toString();
	if (!header) {
	    xmlString = xmlString.substring(xmlString.indexOf("?>") + 2);
	    if (xmlString.charAt(0) == '\n') {
		xmlString.replaceFirst("\n", "");
	    }
	}
	// System.out.println(xmlString);
	// Utils.toFile(filename, extention, xmlString,"."+File.separator);
    }

}
