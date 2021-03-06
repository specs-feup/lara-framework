// xmljavabind
// Thu Oct 26 14:27:20 2017
// Warning: This file has been automatically generated.
// Any modifications to the file could be lost.

package pt.up.fe.specs.lara.aspectir;

import java.io.File;
import java.io.PrintStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/****************************** Class Base ******************************/
public class Base implements IElement {
    public String xml_location;
    public String xmltag;
    public IElement parent;
    public String comment;

    public Base() {
        comment = "";

    }

    public Base(Element e,
            String rootName, Document doc) throws DOMException, Exception {

        if (e == null)
            return;
        xmltag = e.getTagName();
        if ((!rootName.equals("")) && (!e.getNodeName().equals(rootName))) {
            throw new Exception(" Error unexpected : " + e.getNodeName() + ", " + rootName);
        }
        for (int i = 0; i < e.getAttributes().getLength(); i++) {
            Node a = e.getAttributes().item(i);
            if (a.getNodeName().equals("comment")) {
                comment = a.getNodeValue();
            } else
                throw new Exception("Unexpected attribute in Node Base: " + e.getAttributes().item(0).getNodeName());
        }
        Node n = e.getFirstChild();
        while (n != null && !(n instanceof Element))
            n = n.getNextSibling();
        Element q = (n != null) ? (Element) n : null;
        while (q != null) {
            throw new Exception(" Error unexpected : " + q.getNodeName());
        }
        if (!e.getTextContent().trim().isEmpty())
            throw new Exception("Error unexpected: text");
    }

    public Base(String fileName, String rootName) throws Exception {
        this(readDocument(fileName), rootName);
    }

    public static Document readDocument(String fileName) throws Exception {
        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
        Document doc = docBuilder.parse(new File(fileName));
        return doc;
    }

    public Base(Document doc,
            String rootName) throws Exception {

        comment = "";
        Element e = (Element) doc.getFirstChild();
        if (e == null)
            return;
        xmltag = e.getTagName();
        if ((!rootName.equals("")) && (!e.getNodeName().equals(rootName))) {
            throw new Exception(" Error unexpected : " + e.getNodeName() + ", " + rootName);
        }
        for (int i = 0; i < e.getAttributes().getLength(); i++) {
            Node a = e.getAttributes().item(i);
            if (a.getNodeName().equals("comment")) {
                comment = a.getNodeValue();
            } else
                throw new Exception("Unexpected attribute in Node Base: " + e.getAttributes().item(0).getNodeName());
        }
        Node n = e.getFirstChild();
        while (n != null && !(n instanceof Element))
            n = n.getNextSibling();
        Element q = (n != null) ? (Element) n : null;
        while (q != null) {
            throw new Exception(" Error unexpected : " + q.getNodeName());
        }
        if (!e.getTextContent().trim().isEmpty())
            throw new Exception("Error unexpected: text");
    }

    @Override
    public void loadXml(Element e, String rootName, Document doc) throws Exception {
        if (e == null)
            return;
        if (!e.getNodeName().equals(rootName)) {
            throw new Exception(" Error unexpected: " + e.getNodeName() + " != " + rootName);
        }
        for (int i = 0; i < e.getAttributes().getLength(); i++) {
            Node a = e.getAttributes().item(i);
            if (a.getNodeName().equals("comment")) {
                comment = a.getNodeValue();
            } else
                throw new Exception("Unexpected attribute in Node Base: " + e.getAttributes().item(0).getNodeName());
        }
        Node n = e.getFirstChild();
        while (n != null && !(n instanceof Element))
            n = n.getNextSibling();
        Element q = (n != null) ? (Element) n : null;
        while (q != null) {
            throw new Exception(" Error unexpected : " + q.getNodeName());
        }
        if (!e.getTextContent().trim().isEmpty())
            throw new Exception("Error unexpected: text");
    }

    @Override
    public IElement getParent() {
        return null;
    }

    public Document getXmlDocument() {
        Document doc = null;
        try {
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
        tagEl.setAttribute("comment", "" + comment);
        return doc;
    }

    @Override
    public void writeXml(Document doc, Element parent, String rootName, int level) {
        String tagName = ((rootName.isEmpty()) ? xmltag : rootName);
        Element tagEl = doc.createElement(tagName);
        parent.appendChild(tagEl);
        tagEl.setAttribute("comment", "" + comment);
    }

    @Override
    public void print(PrintStream os, int indent) {
        os.println("Base {");
        printIndent(os, indent + 1);
        os.println("comment = '" + comment);
        printIndent(os, indent);
        os.println("}");
    }

    @Override
    public String typeName() {
        return "Base";
    }

    @Override
    public void printIndent(PrintStream os, int indent) {
        for (int i = 0; i < indent; i++)
            os.print("  ");
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void visitDepthFirst(Visitor visitor) {
        accept(visitor);
    }
}

/********************************************************************************/
