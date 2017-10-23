// xmljavabind
// Thu Sep 14 01:51:29 2017
// Warning:  This file has been automatically generated.
//    Any modifications to the file could be lost.

package pt.up.fe.specs.lara.aspectir;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.PrintStream;

/****************************** Class Code ******************************/
public class Code extends CodeElem implements IElement {
	public String xml_location;
	public String xmltag;
	public java.util.ArrayList<Statement> statements= new java.util.ArrayList<Statement>();

	public Code(){

	}

	public Code(Element e, 
			String rootName, Document doc) throws DOMException, Exception{
	if (e == null) return;
	xmltag = e.getTagName();
	if ((!rootName.equals("")) && (!e.getNodeName().equals(rootName))){
		throw new Exception(" Error unexpected : "+e.getNodeName()+", "+rootName);
	}
	for (int i = 0; i < e.getAttributes().getLength(); i++){
		Node a = e.getAttributes().item(i);
		if (a.getNodeName().equals("desc")){
			desc = a.getNodeValue();
		}
		else
						throw new Exception("Unexpected attribute in Node Code: "+e.getAttributes().item(0).getNodeName());
	}
	Node n = e.getFirstChild();
	while(n != null && !(n instanceof Element))
		n = n.getNextSibling();
	Element q = (n!=null)?(Element)n:null;
	while (q != null){
		if (q.getNodeName().equals("statement")){
			Statement _m;
			_m = new Statement(q,"",doc);
			if (statements.contains(_m))
				throw new Exception(" Error duplicate: "+_m);
			statements.add(_m);
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

	public Code(String fileName, String rootName) throws Exception {
this(readDocument(fileName), rootName);
	}

	public static Document readDocument(String fileName) throws Exception {
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		Document doc = docBuilder.parse(new File(fileName));
		return doc;
	}

	public Code(Document doc,
			String rootName) throws Exception {
			Element e = (Element)doc.getFirstChild();
			if (e == null) return;
			xmltag = e.getTagName();
			if ((!rootName.equals("")) && (!e.getNodeName().equals(rootName))){
				throw new Exception(" Error unexpected : "+e.getNodeName()+", "+rootName);
			}
	for (int i = 0; i < e.getAttributes().getLength(); i++){
		Node a = e.getAttributes().item(i);
		if (a.getNodeName().equals("desc")){
			desc = a.getNodeValue();
		}
		else
						throw new Exception("Unexpected attribute in Node Code: "+e.getAttributes().item(0).getNodeName());
	}
	Node n = e.getFirstChild();
	while(n != null && !(n instanceof Element))
		n = n.getNextSibling();
	Element q = (n!=null)?(Element)n:null;
	while (q != null){
		if (q.getNodeName().equals("statement")){
			Statement _m;
			_m = new Statement(q,"",doc);
			if (statements.contains(_m))
				throw new Exception(" Error duplicate: "+_m);
			statements.add(_m);
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
		if (a.getNodeName().equals("desc")){
			desc = a.getNodeValue();
		}
		else
						throw new Exception("Unexpected attribute in Node Code: "+e.getAttributes().item(0).getNodeName());
	}
	Node n = e.getFirstChild();
	while(n != null && !(n instanceof Element))
		n = n.getNextSibling();
	Element q = (n!=null)?(Element)n:null;
	while (q != null){
		if (q.getNodeName().equals("statement")){
			Statement _m;
			_m = new Statement(q,"",doc);
			if (statements.contains(_m))
				throw new Exception(" Error duplicate: "+_m);
			statements.add(_m);
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
	tagEl.setAttribute("desc", ""+desc);
	for(Statement i_statements: statements)
		i_statements.writeXml(doc,tagEl,"",level+1);
	return doc;}

	public 	void writeXml(Document doc, Element parent, String  rootName, int level){
	String tagName = ((rootName.isEmpty())?xmltag:rootName);
	Element tagEl = doc.createElement(tagName);
	parent.appendChild(tagEl);
		tagEl.setAttribute("desc", ""+desc);
	for(Statement i_statements: statements)
		i_statements.writeXml(doc,tagEl,"",level+1);
}

	public void print(PrintStream os, int indent){
	os.println("Code {");
	printIndent(os, indent+1);
	os.println("desc = '" + desc);
	printIndent(os, indent+1);
	os.println("statements = <[");
	for(Statement i_statements: statements){
		printIndent(os, indent+2);
		i_statements.print(os, indent+2);
	}
	printIndent(os, indent+1);
	os.println("]>");
	printIndent(os, indent);
	os.println("}");
}

	public String typeName(){
	return "Code";
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

