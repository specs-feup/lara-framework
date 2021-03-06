// xmljavabind
// Thu Oct 26 14:27:20 2017
// Warning:  This file has been automatically generated.
//    Any modifications to the file could be lost.

package pt.up.fe.specs.lara.aspectir;
import java.io.File;
import java.io.PrintStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/****************************** Class CodeElem ******************************/
public class CodeElem extends Base implements IElement {
	public String xml_location;
	public String xmltag;
	public IElement parent;
	public String desc;

	public CodeElem(){
		desc = "";

	}

	public CodeElem(Element e, 
			String rootName, Document doc) throws DOMException, Exception{
	if (e == null) return;
	xmltag = e.getTagName();
	if ((!rootName.equals("")) && (!e.getNodeName().equals(rootName))){
		throw new Exception(" Error unexpected : "+e.getNodeName()+", "+rootName);
	}
	for (int i = 0; i < e.getAttributes().getLength(); i++){
		Node a = e.getAttributes().item(i);
		if (a.getNodeName().equals("comment")){
			comment = a.getNodeValue();
		}
		else if (a.getNodeName().equals("desc")){
			desc = a.getNodeValue();
		}
		else
						throw new Exception("Unexpected attribute in Node CodeElem: "+e.getAttributes().item(0).getNodeName());
	}
	Node n = e.getFirstChild();
	while(n != null && !(n instanceof Element))
		n = n.getNextSibling();
	Element q = (n!=null)?(Element)n:null;
	while (q != null){
		throw new Exception(" Error unexpected : " + q.getNodeName());
	}
	if (!e.getTextContent().trim().isEmpty())
		throw new Exception("Error unexpected: text");
}

	public CodeElem(String fileName, String rootName) throws Exception {
this(readDocument(fileName), rootName);
	}

	public static Document readDocument(String fileName) throws Exception {
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		Document doc = docBuilder.parse(new File(fileName));
		return doc;
	}

	public CodeElem(Document doc,
			String rootName) throws Exception {
		desc = "";
		parent = null;
			Element e = (Element)doc.getFirstChild();
			if (e == null) return;
			xmltag = e.getTagName();
			if ((!rootName.equals("")) && (!e.getNodeName().equals(rootName))){
				throw new Exception(" Error unexpected : "+e.getNodeName()+", "+rootName);
			}
	for (int i = 0; i < e.getAttributes().getLength(); i++){
		Node a = e.getAttributes().item(i);
		if (a.getNodeName().equals("comment")){
			comment = a.getNodeValue();
		}
		else if (a.getNodeName().equals("desc")){
			desc = a.getNodeValue();
		}
		else
						throw new Exception("Unexpected attribute in Node CodeElem: "+e.getAttributes().item(0).getNodeName());
	}
	Node n = e.getFirstChild();
	while(n != null && !(n instanceof Element))
		n = n.getNextSibling();
	Element q = (n!=null)?(Element)n:null;
	while (q != null){
		throw new Exception(" Error unexpected : " + q.getNodeName());
	}
	if (!e.getTextContent().trim().isEmpty())
		throw new Exception("Error unexpected: text");
	}
	public void loadXml(Element e, String rootName, Document doc) throws Exception{
	if (e == null) return;
	if (!e.getNodeName().equals(rootName)){
		throw new Exception(" Error unexpected: " + e.getNodeName() +" != "+ rootName);
	}
	for (int i = 0; i < e.getAttributes().getLength(); i++){
		Node a = e.getAttributes().item(i);
		if (a.getNodeName().equals("comment")){
			comment = a.getNodeValue();
		}
		else if (a.getNodeName().equals("desc")){
			desc = a.getNodeValue();
		}
		else
						throw new Exception("Unexpected attribute in Node CodeElem: "+e.getAttributes().item(0).getNodeName());
	}
	Node n = e.getFirstChild();
	while(n != null && !(n instanceof Element))
		n = n.getNextSibling();
	Element q = (n!=null)?(Element)n:null;
	while (q != null){
		throw new Exception(" Error unexpected : " + q.getNodeName());
	}
	if (!e.getTextContent().trim().isEmpty())
		throw new Exception("Error unexpected: text");
}

public IElement getParent(){
	return parent;
}

public 	Document getXmlDocument(){
	Document doc = null;	try {
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		docBuilder = dbfac.newDocumentBuilder();
		doc = docBuilder.newDocument();
	} catch (DOMException e) {
		e.printStackTrace();
	} catch (Exception e) {
		e.printStackTrace();
	}
	Element tagEl = doc.createElement(xmltag);
		doc.appendChild(tagEl);
	tagEl.setAttribute("comment", ""+comment);
	tagEl.setAttribute("desc", ""+desc);
	return doc;}

	public 	void writeXml(Document doc, Element parent, String  rootName, int level){
	String tagName = ((rootName.isEmpty())?xmltag:rootName);
	Element tagEl = doc.createElement(tagName);
	parent.appendChild(tagEl);
		tagEl.setAttribute("comment", ""+comment);
		tagEl.setAttribute("desc", ""+desc);
}

	public void print(PrintStream os, int indent){
	os.println("CodeElem {");
	printIndent(os, indent+1);
	os.println("comment = '" + comment);
	printIndent(os, indent+1);
	os.println("desc = '" + desc);
	printIndent(os, indent);
	os.println("}");
}

	public String typeName(){
	return "CodeElem";
}

	public void printIndent(PrintStream os, int indent){
		for (int i=0; i<indent; i++) 
			os.print("  ");
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}

/********************************************************************************/

