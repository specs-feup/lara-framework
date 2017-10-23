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

/****************************** Class Aspect ******************************/
public class Aspect extends Base implements IElement {
	public String xml_location;
	public String xmltag;
	public Expression check;
	public String coord;
	public Code finalize;
	public Code initialize;
	public String name;
	public ParameterSection parameters;
	public String stage;
	public java.util.ArrayList<Statement> statements= new java.util.ArrayList<Statement>();
	public Code staticBlock;

	public Aspect(){
		check = null;
		coord = "";
		finalize = null;
		initialize = null;
		name = "";
		parameters = null;
		stage = "";
		staticBlock = null;

	}

	public Aspect(Element e, 
			String rootName, Document doc) throws DOMException, Exception{
	if (e == null) return;
	xmltag = e.getTagName();
	if ((!rootName.equals("")) && (!e.getNodeName().equals(rootName))){
		throw new Exception(" Error unexpected : "+e.getNodeName()+", "+rootName);
	}
	int found_check = 0;
	int found_finalize = 0;
	int found_initialize = 0;
	int found_name = 0;
	int found_parameters = 0;
	int found_staticBlock = 0;
	for (int i = 0; i < e.getAttributes().getLength(); i++){
		Node a = e.getAttributes().item(i);
		if (a.getNodeName().equals("comment")){
			comment = a.getNodeValue();
		}
		else if (a.getNodeName().equals("coord")){
			coord = a.getNodeValue();
		}
		else if (a.getNodeName().equals("name")){
			name = a.getNodeValue();
			found_name++;
		}
		else if (a.getNodeName().equals("stage")){
			stage = a.getNodeValue();
		}
		else
						throw new Exception("Unexpected attribute in Node Aspect: "+e.getAttributes().item(0).getNodeName());
	}
	Node n = e.getFirstChild();
	while(n != null && !(n instanceof Element))
		n = n.getNextSibling();
	Element q = (n!=null)?(Element)n:null;
	while (q != null){
		if (q.getNodeName().equals("check")){
			check = new Expression(q,"",doc);
			found_check++;
		}
		else if (q.getNodeName().equals("finalize")){
			finalize = new Code(q,"",doc);
			found_finalize++;
		}
		else if (q.getNodeName().equals("initialize")){
			initialize = new Code(q,"",doc);
			found_initialize++;
		}
		else if (q.getNodeName().equals("parameters")){
			parameters = new ParameterSection(q,"",doc);
			found_parameters++;
		}
		else if (q.getNodeName().equals("statement")){
			Statement _m;
			_m = new Statement(q,"",doc);
			if (statements.contains(_m))
				throw new Exception(" Error duplicate: "+_m);
			statements.add(_m);
		}
		else if (q.getNodeName().equals("static")){
			staticBlock = new Code(q,"",doc);
			found_staticBlock++;
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
	if (found_check > 1)
		throw new Exception(" too many check: " +found_check+"(max: "+1+ "x)");
	if (found_finalize > 1)
		throw new Exception(" too many finalize: " +found_finalize+"(max: "+1+ "x)");
	if (found_initialize > 1)
		throw new Exception(" too many initialize: " +found_initialize+"(max: "+1+ "x)");
	if (found_name < 1)
		throw new Exception(" too few name: " +found_name+"(min: "+1+ "x)");
	if (found_parameters > 1)
		throw new Exception(" too many parameters: " +found_parameters+"(max: "+1+ "x)");
	if (found_staticBlock > 1)
		throw new Exception(" too many static: " +found_staticBlock+"(max: "+1+ "x)");
}

	public Aspect(String fileName, String rootName) throws Exception {
this(readDocument(fileName), rootName);
	}

	public static Document readDocument(String fileName) throws Exception {
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		Document doc = docBuilder.parse(new File(fileName));
		return doc;
	}

	public Aspect(Document doc,
			String rootName) throws Exception {
		check = null;
		coord = "";
		finalize = null;
		initialize = null;
		name = "";
		parameters = null;
		stage = "";
		staticBlock = null;
			Element e = (Element)doc.getFirstChild();
			if (e == null) return;
			xmltag = e.getTagName();
			if ((!rootName.equals("")) && (!e.getNodeName().equals(rootName))){
				throw new Exception(" Error unexpected : "+e.getNodeName()+", "+rootName);
			}
	int found_check = 0;
	int found_finalize = 0;
	int found_initialize = 0;
	int found_name = 0;
	int found_parameters = 0;
	int found_staticBlock = 0;
	for (int i = 0; i < e.getAttributes().getLength(); i++){
		Node a = e.getAttributes().item(i);
		if (a.getNodeName().equals("comment")){
			comment = a.getNodeValue();
		}
		else if (a.getNodeName().equals("coord")){
			coord = a.getNodeValue();
		}
		else if (a.getNodeName().equals("name")){
			name = a.getNodeValue();
			found_name++;
		}
		else if (a.getNodeName().equals("stage")){
			stage = a.getNodeValue();
		}
		else
						throw new Exception("Unexpected attribute in Node Aspect: "+e.getAttributes().item(0).getNodeName());
	}
	Node n = e.getFirstChild();
	while(n != null && !(n instanceof Element))
		n = n.getNextSibling();
	Element q = (n!=null)?(Element)n:null;
	while (q != null){
		if (q.getNodeName().equals("check")){
			check = new Expression(q,"",doc);
			found_check++;
		}
		else if (q.getNodeName().equals("finalize")){
			finalize = new Code(q,"",doc);
			found_finalize++;
		}
		else if (q.getNodeName().equals("initialize")){
			initialize = new Code(q,"",doc);
			found_initialize++;
		}
		else if (q.getNodeName().equals("parameters")){
			parameters = new ParameterSection(q,"",doc);
			found_parameters++;
		}
		else if (q.getNodeName().equals("statement")){
			Statement _m;
			_m = new Statement(q,"",doc);
			if (statements.contains(_m))
				throw new Exception(" Error duplicate: "+_m);
			statements.add(_m);
		}
		else if (q.getNodeName().equals("static")){
			staticBlock = new Code(q,"",doc);
			found_staticBlock++;
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
	if (found_check > 1)
		throw new Exception(" too many check: " +found_check+"(max: "+1+ "x)");
	if (found_finalize > 1)
		throw new Exception(" too many finalize: " +found_finalize+"(max: "+1+ "x)");
	if (found_initialize > 1)
		throw new Exception(" too many initialize: " +found_initialize+"(max: "+1+ "x)");
	if (found_name < 1)
		throw new Exception(" too few name: " +found_name+"(min: "+1+ "x)");
	if (found_parameters > 1)
		throw new Exception(" too many parameters: " +found_parameters+"(max: "+1+ "x)");
	if (found_staticBlock > 1)
		throw new Exception(" too many static: " +found_staticBlock+"(max: "+1+ "x)");
	}
	public void loadXml(Element e, String rootName, Document doc) throws Exception{
	if (e == null) return;
	if (!e.getNodeName().equals(rootName)){
		throw new Exception(" Error unexpected: " + e.getNodeName() +" != "+ rootName);
	}
	int found_check = 0;
	int found_finalize = 0;
	int found_initialize = 0;
	int found_name = 0;
	int found_parameters = 0;
	int found_staticBlock = 0;
	for (int i = 0; i < e.getAttributes().getLength(); i++){
		Node a = e.getAttributes().item(i);
		if (a.getNodeName().equals("comment")){
			comment = a.getNodeValue();
		}
		else if (a.getNodeName().equals("coord")){
			coord = a.getNodeValue();
		}
		else if (a.getNodeName().equals("name")){
			name = a.getNodeValue();
			found_name++;
		}
		else if (a.getNodeName().equals("stage")){
			stage = a.getNodeValue();
		}
		else
						throw new Exception("Unexpected attribute in Node Aspect: "+e.getAttributes().item(0).getNodeName());
	}
	Node n = e.getFirstChild();
	while(n != null && !(n instanceof Element))
		n = n.getNextSibling();
	Element q = (n!=null)?(Element)n:null;
	while (q != null){
		if (q.getNodeName().equals("check")){
			check = new Expression(q,"",doc);
			found_check++;
		}
		else if (q.getNodeName().equals("finalize")){
			finalize = new Code(q,"",doc);
			found_finalize++;
		}
		else if (q.getNodeName().equals("initialize")){
			initialize = new Code(q,"",doc);
			found_initialize++;
		}
		else if (q.getNodeName().equals("parameters")){
			parameters = new ParameterSection(q,"",doc);
			found_parameters++;
		}
		else if (q.getNodeName().equals("statement")){
			Statement _m;
			_m = new Statement(q,"",doc);
			if (statements.contains(_m))
				throw new Exception(" Error duplicate: "+_m);
			statements.add(_m);
		}
		else if (q.getNodeName().equals("static")){
			staticBlock = new Code(q,"",doc);
			found_staticBlock++;
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
	if (found_check > 1)
		throw new Exception(" too many check: " +found_check+"(max: "+1+ "x)");
	if (found_finalize > 1)
		throw new Exception(" too many finalize: " +found_finalize+"(max: "+1+ "x)");
	if (found_initialize > 1)
		throw new Exception(" too many initialize: " +found_initialize+"(max: "+1+ "x)");
	if (found_name < 1)
		throw new Exception(" too few name: " +found_name+"(min: "+1+ "x)");
	if (found_parameters > 1)
		throw new Exception(" too many parameters: " +found_parameters+"(max: "+1+ "x)");
	if (found_staticBlock > 1)
		throw new Exception(" too many static: " +found_staticBlock+"(max: "+1+ "x)");
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
	tagEl.setAttribute("comment", ""+comment);
	tagEl.setAttribute("coord", ""+coord);
	tagEl.setAttribute("name", ""+name);
	tagEl.setAttribute("stage", ""+stage);
	if (check != null)
		check.writeXml(doc, tagEl,"", level+1);
	if (finalize != null)
		finalize.writeXml(doc, tagEl,"", level+1);
	if (initialize != null)
		initialize.writeXml(doc, tagEl,"", level+1);
	if (parameters != null)
		parameters.writeXml(doc, tagEl,"", level+1);
	for(Statement i_statements: statements)
		i_statements.writeXml(doc,tagEl,"",level+1);
	if (staticBlock != null)
		staticBlock.writeXml(doc, tagEl,"", level+1);
	return doc;}

	public 	void writeXml(Document doc, Element parent, String  rootName, int level){
	String tagName = ((rootName.isEmpty())?xmltag:rootName);
	Element tagEl = doc.createElement(tagName);
	parent.appendChild(tagEl);
		tagEl.setAttribute("comment", ""+comment);
		tagEl.setAttribute("coord", ""+coord);
		tagEl.setAttribute("name", ""+name);
		tagEl.setAttribute("stage", ""+stage);
	if (check != null)
		check.writeXml(doc, tagEl,"", level+1);
	if (finalize != null)
		finalize.writeXml(doc, tagEl,"", level+1);
	if (initialize != null)
		initialize.writeXml(doc, tagEl,"", level+1);
	if (parameters != null)
		parameters.writeXml(doc, tagEl,"", level+1);
	for(Statement i_statements: statements)
		i_statements.writeXml(doc,tagEl,"",level+1);
	if (staticBlock != null)
		staticBlock.writeXml(doc, tagEl,"", level+1);
}

	public void print(PrintStream os, int indent){
	os.println("Aspect {");
	printIndent(os, indent+1);
	os.println("comment = '" + comment);
	printIndent(os, indent+1);
	os.print("check = ");
	if (check == null)
		os.println("(null)");
	else
		check.print(os, indent+1);
	printIndent(os, indent+1);
	os.println("coord = '" + coord);
	printIndent(os, indent+1);
	os.print("finalize = ");
	if (finalize == null)
		os.println("(null)");
	else
		finalize.print(os, indent+1);
	printIndent(os, indent+1);
	os.print("initialize = ");
	if (initialize == null)
		os.println("(null)");
	else
		initialize.print(os, indent+1);
	printIndent(os, indent+1);
	os.println("name = '" + name);
	printIndent(os, indent+1);
	os.print("parameters = ");
	if (parameters == null)
		os.println("(null)");
	else
		parameters.print(os, indent+1);
	printIndent(os, indent+1);
	os.println("stage = '" + stage);
	printIndent(os, indent+1);
	os.println("statements = <[");
	for(Statement i_statements: statements){
		printIndent(os, indent+2);
		i_statements.print(os, indent+2);
	}
	printIndent(os, indent+1);
	os.println("]>");
	printIndent(os, indent+1);
	os.print("staticBlock = ");
	if (staticBlock == null)
		os.println("(null)");
	else
		staticBlock.print(os, indent+1);
	printIndent(os, indent);
	os.println("}");
}

	public String typeName(){
	return "Aspect";
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

