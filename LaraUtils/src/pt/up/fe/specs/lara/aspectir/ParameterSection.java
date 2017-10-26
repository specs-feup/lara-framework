// xmljavabind
// Thu Oct 26 14:27:21 2017
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

/****************************** Class ParameterSection ******************************/
public class ParameterSection extends Base implements IElement {
	public String xml_location;
	public String xmltag;
	public IElement parent;
	public ParameterList input;
	public ParameterList output;

	public ParameterSection(){
		input = null;
		output = null;

	}

	public ParameterSection(Element e, 
			String rootName, Document doc) throws DOMException, Exception{
	if (e == null) return;
	xmltag = e.getTagName();
	if ((!rootName.equals("")) && (!e.getNodeName().equals(rootName))){
		throw new Exception(" Error unexpected : "+e.getNodeName()+", "+rootName);
	}
	int found_input = 0;
	int found_output = 0;
	for (int i = 0; i < e.getAttributes().getLength(); i++){
		Node a = e.getAttributes().item(i);
		if (a.getNodeName().equals("comment")){
			comment = a.getNodeValue();
		}
		else
						throw new Exception("Unexpected attribute in Node ParameterSection: "+e.getAttributes().item(0).getNodeName());
	}
	Node n = e.getFirstChild();
	while(n != null && !(n instanceof Element))
		n = n.getNextSibling();
	Element q = (n!=null)?(Element)n:null;
	while (q != null){
		if (q.getNodeName().equals("input")){
			input = new ParameterList(q,"",doc);
			input.parent = (IElement)this;
			found_input++;
		}
		else if (q.getNodeName().equals("output")){
			output = new ParameterList(q,"",doc);
			output.parent = (IElement)this;
			found_output++;
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
	if (found_input > 1)
		throw new Exception(" too many input: " +found_input+"(max: "+1+ "x)");
	if (found_output > 1)
		throw new Exception(" too many output: " +found_output+"(max: "+1+ "x)");
}

	public ParameterSection(String fileName, String rootName) throws Exception {
this(readDocument(fileName), rootName);
	}

	public static Document readDocument(String fileName) throws Exception {
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		Document doc = docBuilder.parse(new File(fileName));
		return doc;
	}

	public ParameterSection(Document doc,
			String rootName) throws Exception {
		input = null;
		output = null;
		parent = null;
			Element e = (Element)doc.getFirstChild();
			if (e == null) return;
			xmltag = e.getTagName();
			if ((!rootName.equals("")) && (!e.getNodeName().equals(rootName))){
				throw new Exception(" Error unexpected : "+e.getNodeName()+", "+rootName);
			}
	int found_input = 0;
	int found_output = 0;
	for (int i = 0; i < e.getAttributes().getLength(); i++){
		Node a = e.getAttributes().item(i);
		if (a.getNodeName().equals("comment")){
			comment = a.getNodeValue();
		}
		else
						throw new Exception("Unexpected attribute in Node ParameterSection: "+e.getAttributes().item(0).getNodeName());
	}
	Node n = e.getFirstChild();
	while(n != null && !(n instanceof Element))
		n = n.getNextSibling();
	Element q = (n!=null)?(Element)n:null;
	while (q != null){
		if (q.getNodeName().equals("input")){
			input = new ParameterList(q,"",doc);
			input.parent = (IElement)this;
			found_input++;
		}
		else if (q.getNodeName().equals("output")){
			output = new ParameterList(q,"",doc);
			output.parent = (IElement)this;
			found_output++;
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
	if (found_input > 1)
		throw new Exception(" too many input: " +found_input+"(max: "+1+ "x)");
	if (found_output > 1)
		throw new Exception(" too many output: " +found_output+"(max: "+1+ "x)");
	}
	public void loadXml(Element e, String rootName, Document doc) throws Exception{
	if (e == null) return;
	if (!e.getNodeName().equals(rootName)){
		throw new Exception(" Error unexpected: " + e.getNodeName() +" != "+ rootName);
	}
	int found_input = 0;
	int found_output = 0;
	for (int i = 0; i < e.getAttributes().getLength(); i++){
		Node a = e.getAttributes().item(i);
		if (a.getNodeName().equals("comment")){
			comment = a.getNodeValue();
		}
		else
						throw new Exception("Unexpected attribute in Node ParameterSection: "+e.getAttributes().item(0).getNodeName());
	}
	Node n = e.getFirstChild();
	while(n != null && !(n instanceof Element))
		n = n.getNextSibling();
	Element q = (n!=null)?(Element)n:null;
	while (q != null){
		if (q.getNodeName().equals("input")){
			input = new ParameterList(q,"",doc);
			input.parent = (IElement)this;
			found_input++;
		}
		else if (q.getNodeName().equals("output")){
			output = new ParameterList(q,"",doc);
			output.parent = (IElement)this;
			found_output++;
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
	if (found_input > 1)
		throw new Exception(" too many input: " +found_input+"(max: "+1+ "x)");
	if (found_output > 1)
		throw new Exception(" too many output: " +found_output+"(max: "+1+ "x)");
}

public IElement getParent(){
	return parent;
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
	tagEl.setAttribute("comment", ""+comment);
	if (input != null)
		input.writeXml(doc, tagEl,"", level+1);
	if (output != null)
		output.writeXml(doc, tagEl,"", level+1);
	return doc;}

	public 	void writeXml(Document doc, Element parent, String  rootName, int level){
	String tagName = ((rootName.isEmpty())?xmltag:rootName);
	Element tagEl = doc.createElement(tagName);
	parent.appendChild(tagEl);
		tagEl.setAttribute("comment", ""+comment);
	if (input != null)
		input.writeXml(doc, tagEl,"", level+1);
	if (output != null)
		output.writeXml(doc, tagEl,"", level+1);
}

	public void print(PrintStream os, int indent){
	os.println("ParameterSection {");
	printIndent(os, indent+1);
	os.println("comment = '" + comment);
	printIndent(os, indent+1);
	os.print("input = ");
	if (input == null)
		os.println("(null)");
	else
		input.print(os, indent+1);
	printIndent(os, indent+1);
	os.print("output = ");
	if (output == null)
		os.println("(null)");
	else
		output.print(os, indent+1);
	printIndent(os, indent);
	os.println("}");
}

	public String typeName(){
	return "ParameterSection";
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

