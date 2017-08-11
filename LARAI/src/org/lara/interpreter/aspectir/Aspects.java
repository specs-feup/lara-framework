// xmljavabind
// Thu Feb 23 16:08:26 2017
// Warning:  This file has been automatically generated.
//    Any modifications to the file could be lost.

package org.lara.interpreter.aspectir;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.PrintStream;

/****************************** Class Aspects ******************************/
public class Aspects implements IElement {
	public String xml_location;
	public String xmltag;
	public java.util.HashMap<String, Aspect> aspects= new java.util.HashMap<String, Aspect>();
	public java.util.ArrayList<Statement> declarations= new java.util.ArrayList<Statement>();
	public String main;

	public Aspects(){
		main = "";

	}

	public Aspects(Element e, 
			String rootName, Document doc) throws DOMException, Exception{
	if (e == null) return;
	xmltag = e.getTagName();
	if ((!rootName.equals("")) && (!e.getNodeName().equals(rootName))){
		throw new Exception(" Error unexpected : "+e.getNodeName()+", "+rootName);
	}
	for (int i = 0; i < e.getAttributes().getLength(); i++){
		Node a = e.getAttributes().item(i);
		if (a.getNodeName().equals("main")){
			main = a.getNodeValue();
		}
		else
						throw new Exception("Unexpected attribute in Node Aspects: "+e.getAttributes().item(0).getNodeName());
	}
	Node n = e.getFirstChild();
	while(n != null && !(n instanceof Element))
		n = n.getNextSibling();
	Element q = (n!=null)?(Element)n:null;
	while (q != null){
		if (q.getNodeName().equals("aspect")){
			Aspect _m;
			_m = new Aspect(q,"",doc);
			if (aspects.containsKey(_m.name))
				throw new Exception(" Error duplicate: "+_m.name);
			aspects.put(_m.name,_m);
		}
		else if (q.getNodeName().equals("declaration")){
			Statement _m;
			_m = new Statement(q,"",doc);
			if (declarations.contains(_m))
				throw new Exception(" Error duplicate: "+_m);
			declarations.add(_m);
		}
		else
			throw new Exception(" Error unexpected : " + q.getNodeName());
		do{
			n = n.getNextSibling();
		}while(n!= null && !(n instanceof Element));
		q = (n!=null)?(Element)n:null;
	}
	if (!e.getTextContent().trim().isEmpty())
		throw new Exception("Error unexpected: text");
}

	public Aspects(String fileName, String rootName) throws Exception {
this(readDocument(fileName), rootName);
	}

	public static Document readDocument(String fileName) throws Exception {
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		Document doc = docBuilder.parse(new File(fileName));
		return doc;
	}

	public Aspects(Document doc,
			String rootName) throws Exception {
		main = "";
			Element e = (Element)doc.getFirstChild();
			if (e == null) return;
			xmltag = e.getTagName();
			if ((!rootName.equals("")) && (!e.getNodeName().equals(rootName))){
				throw new Exception(" Error unexpected : "+e.getNodeName()+", "+rootName);
			}
	for (int i = 0; i < e.getAttributes().getLength(); i++){
		Node a = e.getAttributes().item(i);
		if (a.getNodeName().equals("main")){
			main = a.getNodeValue();
		}
		else
						throw new Exception("Unexpected attribute in Node Aspects: "+e.getAttributes().item(0).getNodeName());
	}
	Node n = e.getFirstChild();
	while(n != null && !(n instanceof Element))
		n = n.getNextSibling();
	Element q = (n!=null)?(Element)n:null;
	while (q != null){
		if (q.getNodeName().equals("aspect")){
			Aspect _m;
			_m = new Aspect(q,"",doc);
			if (aspects.containsKey(_m.name))
				throw new Exception(" Error duplicate: "+_m.name);
			aspects.put(_m.name,_m);
		}
		else if (q.getNodeName().equals("declaration")){
			Statement _m;
			_m = new Statement(q,"",doc);
			if (declarations.contains(_m))
				throw new Exception(" Error duplicate: "+_m);
			declarations.add(_m);
		}
		else
			throw new Exception(" Error unexpected : " + q.getNodeName());
		do{
			n = n.getNextSibling();
		}while(n!= null && !(n instanceof Element));
		q = (n!=null)?(Element)n:null;
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
		if (a.getNodeName().equals("main")){
			main = a.getNodeValue();
		}
		else
						throw new Exception("Unexpected attribute in Node Aspects: "+e.getAttributes().item(0).getNodeName());
	}
	Node n = e.getFirstChild();
	while(n != null && !(n instanceof Element))
		n = n.getNextSibling();
	Element q = (n!=null)?(Element)n:null;
	while (q != null){
		if (q.getNodeName().equals("aspect")){
			Aspect _m;
			_m = new Aspect(q,"",doc);
			if (aspects.containsKey(_m.name))
				throw new Exception(" Error duplicate: "+_m.name);
			aspects.put(_m.name,_m);
		}
		else if (q.getNodeName().equals("declaration")){
			Statement _m;
			_m = new Statement(q,"",doc);
			if (declarations.contains(_m))
				throw new Exception(" Error duplicate: "+_m);
			declarations.add(_m);
		}
		else
			throw new Exception(" Error unexpected : " + q.getNodeName());
		do{
			n = n.getNextSibling();
		}while(n!= null && !(n instanceof Element));
		q = (n!=null)?(Element)n:null;
	}
	if (!e.getTextContent().trim().isEmpty())
		throw new Exception("Error unexpected: text");
}

	public IElement getParent(){
	return null;
}

public 	Document getXmlDocument(){
	Document doc = null;	int level = 0;	try {
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
	tagEl.setAttribute("main", ""+main);
	for(Aspect i_aspects: aspects.values())
		i_aspects.writeXml(doc,tagEl,"",level+1);
	for(Statement i_declarations: declarations)
		i_declarations.writeXml(doc,tagEl,"",level+1);
	return doc;}

	public 	void writeXml(Document doc, Element parent, String  rootName, int level){
	String tagName = ((rootName.isEmpty())?xmltag:rootName);
	Element tagEl = doc.createElement(tagName);
	parent.appendChild(tagEl);
		tagEl.setAttribute("main", ""+main);
	for(Aspect i_aspects: aspects.values())
		i_aspects.writeXml(doc,tagEl,"",level+1);
	for(Statement i_declarations: declarations)
		i_declarations.writeXml(doc,tagEl,"",level+1);
}

	public void print(PrintStream os, int indent){
	os.println("Aspects {");
	printIndent(os, indent+1);
	os.println("aspects = <[");
	for(Aspect i_aspects: aspects.values()){
		printIndent(os, indent+2);
		i_aspects.print(os, indent+2);
	}
	printIndent(os, indent+1);
	os.println("]>");
	printIndent(os, indent+1);
	os.println("declarations = <[");
	for(Statement i_declarations: declarations){
		printIndent(os, indent+2);
		i_declarations.print(os, indent+2);
	}
	printIndent(os, indent+1);
	os.println("]>");
	printIndent(os, indent+1);
	os.println("main = '" + main);
	printIndent(os, indent);
	os.println("}");
}

	public String typeName(){
	return "Aspects";
}

	public Aspect getAspect(String key) throws Exception{
	Aspect elem = aspects.get(key);
	if (elem == null) {
		String msg;
		msg = "cannot find Aspect \"" + key + "\"!";
		throw new Exception(msg);
	}

	return elem; 
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

