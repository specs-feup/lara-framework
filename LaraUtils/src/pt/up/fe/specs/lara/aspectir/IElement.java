package pt.up.fe.specs.lara.aspectir;
import java.io.PrintStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/****************************** Interface IElement ******************************/
public interface IElement extends Visitable {
	public void loadXml(Element e, String rootName, Document doc) throws Exception;
	public IElement getParent();
	public void writeXml(Document doc, Element parent, String  rootName, int level);
	public void print(PrintStream os, int indent);
	public String typeName();
	public void printIndent(PrintStream os, int indent);
}
/********************************************************************************/
