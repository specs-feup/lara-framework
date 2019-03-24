// xmljavabind
// Thu Oct 26 14:27:21 2017
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

/****************************** Class Statement ******************************/
public class Statement extends Base implements IElement {
    public String xml_location;
    public String xmltag;
    public IElement parent;
    public java.util.ArrayList<CodeElem> components = new java.util.ArrayList<CodeElem>();
    public boolean concurrent;
    public String coord;
    public String desc;
    public boolean dynamic;
    public String execute;
    public String label;
    public String name;

    public Statement() {
        concurrent = false;
        coord = "";
        desc = "";
        dynamic = false;
        execute = "";
        label = "";
        name = "";

    }

    public Statement(Element e,
            String rootName, Document doc) throws DOMException, Exception {
        if (e == null)
            return;
        xmltag = e.getTagName();
        if ((!rootName.equals("")) && (!e.getNodeName().equals(rootName))) {
            throw new Exception(" Error unexpected : " + e.getNodeName() + ", " + rootName);
        }
        int found_name = 0;
        for (int i = 0; i < e.getAttributes().getLength(); i++) {
            Node a = e.getAttributes().item(i);
            if (a.getNodeName().equals("comment")) {
                comment = a.getNodeValue();
            } else if (a.getNodeName().equals("concurrent")) {
                concurrent = Boolean.parseBoolean(a.getNodeValue());
            } else if (a.getNodeName().equals("coord")) {
                coord = a.getNodeValue();
            } else if (a.getNodeName().equals("desc")) {
                desc = a.getNodeValue();
            } else if (a.getNodeName().equals("dynamic")) {
                dynamic = Boolean.parseBoolean(a.getNodeValue());
            } else if (a.getNodeName().equals("execute")) {
                execute = a.getNodeValue();
            } else if (a.getNodeName().equals("label")) {
                label = a.getNodeValue();
            } else if (a.getNodeName().equals("name")) {
                name = a.getNodeValue();
                found_name++;
            } else
                throw new Exception(
                        "Unexpected attribute in Node Statement: " + e.getAttributes().item(0).getNodeName());
        }
        Node n = e.getFirstChild();
        while (n != null && !(n instanceof Element))
            n = n.getNextSibling();
        Element q = (n != null) ? (Element) n : null;
        while (q != null) {
            if (q.getNodeName().equals("code")) {
                Code _m;
                _m = new Code(q, "", doc);
                _m.parent = (IElement) this;
                if (components.contains(_m))
                    throw new Exception(" Error duplicate: " + _m);
                components.add(_m);
            } else if (q.getNodeName().equals("expression")) {
                Expression _m;
                _m = new Expression(q, "", doc);
                _m.parent = (IElement) this;
                if (components.contains(_m))
                    throw new Exception(" Error duplicate: " + _m);
                components.add(_m);
            } else
                throw new Exception(" Error unexpected : " + q.getNodeName());
            do {
                n = n.getNextSibling();
            } while (n != null && !(n instanceof Element));
            q = (n != null) ? (Element) n : null;
        }
        if (!e.getTextContent().trim().isEmpty())
            throw new Exception("Error unexpected: text");
        if (found_name < 1)
            throw new Exception(" too few name: " + found_name + "(min: " + 1 + "x)");
    }

    public Statement(String fileName, String rootName) throws Exception {
        this(readDocument(fileName), rootName);
    }

    public static Document readDocument(String fileName) throws Exception {
        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
        Document doc = docBuilder.parse(new File(fileName));
        return doc;
    }

    public Statement(Document doc,
            String rootName) throws Exception {
        concurrent = false;
        coord = "";
        desc = "";
        dynamic = false;
        execute = "";
        label = "";
        name = "";
        parent = null;
        Element e = (Element) doc.getFirstChild();
        if (e == null)
            return;
        xmltag = e.getTagName();
        if ((!rootName.equals("")) && (!e.getNodeName().equals(rootName))) {
            throw new Exception(" Error unexpected : " + e.getNodeName() + ", " + rootName);
        }
        int found_name = 0;
        for (int i = 0; i < e.getAttributes().getLength(); i++) {
            Node a = e.getAttributes().item(i);
            if (a.getNodeName().equals("comment")) {
                comment = a.getNodeValue();
            } else if (a.getNodeName().equals("concurrent")) {
                concurrent = Boolean.parseBoolean(a.getNodeValue());
            } else if (a.getNodeName().equals("coord")) {
                coord = a.getNodeValue();
            } else if (a.getNodeName().equals("desc")) {
                desc = a.getNodeValue();
            } else if (a.getNodeName().equals("dynamic")) {
                dynamic = Boolean.parseBoolean(a.getNodeValue());
            } else if (a.getNodeName().equals("execute")) {
                execute = a.getNodeValue();
            } else if (a.getNodeName().equals("label")) {
                label = a.getNodeValue();
            } else if (a.getNodeName().equals("name")) {
                name = a.getNodeValue();
                found_name++;
            } else
                throw new Exception(
                        "Unexpected attribute in Node Statement: " + e.getAttributes().item(0).getNodeName());
        }
        Node n = e.getFirstChild();
        while (n != null && !(n instanceof Element))
            n = n.getNextSibling();
        Element q = (n != null) ? (Element) n : null;
        while (q != null) {
            if (q.getNodeName().equals("code")) {
                Code _m;
                _m = new Code(q, "", doc);
                _m.parent = (IElement) this;
                if (components.contains(_m))
                    throw new Exception(" Error duplicate: " + _m);
                components.add(_m);
            } else if (q.getNodeName().equals("expression")) {
                Expression _m;
                _m = new Expression(q, "", doc);
                _m.parent = (IElement) this;
                if (components.contains(_m))
                    throw new Exception(" Error duplicate: " + _m);
                components.add(_m);
            } else
                throw new Exception(" Error unexpected : " + q.getNodeName());
            do {
                n = n.getNextSibling();
            } while (n != null && !(n instanceof Element));
            q = (n != null) ? (Element) n : null;
        }
        if (!e.getTextContent().trim().isEmpty())
            throw new Exception("Error unexpected: text");
        if (found_name < 1)
            throw new Exception(" too few name: " + found_name + "(min: " + 1 + "x)");
    }

    @Override
    public void loadXml(Element e, String rootName, Document doc) throws Exception {
        if (e == null)
            return;
        if (!e.getNodeName().equals(rootName)) {
            throw new Exception(" Error unexpected: " + e.getNodeName() + " != " + rootName);
        }
        int found_name = 0;
        for (int i = 0; i < e.getAttributes().getLength(); i++) {
            Node a = e.getAttributes().item(i);
            if (a.getNodeName().equals("comment")) {
                comment = a.getNodeValue();
            } else if (a.getNodeName().equals("concurrent")) {
                concurrent = Boolean.parseBoolean(a.getNodeValue());
            } else if (a.getNodeName().equals("coord")) {
                coord = a.getNodeValue();
            } else if (a.getNodeName().equals("desc")) {
                desc = a.getNodeValue();
            } else if (a.getNodeName().equals("dynamic")) {
                dynamic = Boolean.parseBoolean(a.getNodeValue());
            } else if (a.getNodeName().equals("execute")) {
                execute = a.getNodeValue();
            } else if (a.getNodeName().equals("label")) {
                label = a.getNodeValue();
            } else if (a.getNodeName().equals("name")) {
                name = a.getNodeValue();
                found_name++;
            } else
                throw new Exception(
                        "Unexpected attribute in Node Statement: " + e.getAttributes().item(0).getNodeName());
        }
        Node n = e.getFirstChild();
        while (n != null && !(n instanceof Element))
            n = n.getNextSibling();
        Element q = (n != null) ? (Element) n : null;
        while (q != null) {
            if (q.getNodeName().equals("code")) {
                Code _m;
                _m = new Code(q, "", doc);
                _m.parent = (IElement) this;
                if (components.contains(_m))
                    throw new Exception(" Error duplicate: " + _m);
                components.add(_m);
            } else if (q.getNodeName().equals("expression")) {
                Expression _m;
                _m = new Expression(q, "", doc);
                _m.parent = (IElement) this;
                if (components.contains(_m))
                    throw new Exception(" Error duplicate: " + _m);
                components.add(_m);
            } else
                throw new Exception(" Error unexpected : " + q.getNodeName());
            do {
                n = n.getNextSibling();
            } while (n != null && !(n instanceof Element));
            q = (n != null) ? (Element) n : null;
        }
        if (!e.getTextContent().trim().isEmpty())
            throw new Exception("Error unexpected: text");
        if (found_name < 1)
            throw new Exception(" too few name: " + found_name + "(min: " + 1 + "x)");
    }

    @Override
    public IElement getParent() {
        return parent;
    }

    @Override
    public Document getXmlDocument() {
        Document doc = null;
        int level = 0;
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
        tagEl.setAttribute("concurrent", "" + concurrent);
        tagEl.setAttribute("coord", "" + coord);
        tagEl.setAttribute("desc", "" + desc);
        tagEl.setAttribute("dynamic", "" + dynamic);
        tagEl.setAttribute("execute", "" + execute);
        tagEl.setAttribute("label", "" + label);
        tagEl.setAttribute("name", "" + name);
        for (CodeElem i_components : components)
            i_components.writeXml(doc, tagEl, "", level + 1);
        return doc;
    }

    @Override
    public void writeXml(Document doc, Element parent, String rootName, int level) {
        String tagName = ((rootName.isEmpty()) ? xmltag : rootName);
        Element tagEl = doc.createElement(tagName);
        parent.appendChild(tagEl);
        tagEl.setAttribute("comment", "" + comment);
        tagEl.setAttribute("concurrent", "" + concurrent);
        tagEl.setAttribute("coord", "" + coord);
        tagEl.setAttribute("desc", "" + desc);
        tagEl.setAttribute("dynamic", "" + dynamic);
        tagEl.setAttribute("execute", "" + execute);
        tagEl.setAttribute("label", "" + label);
        tagEl.setAttribute("name", "" + name);
        for (CodeElem i_components : components)
            i_components.writeXml(doc, tagEl, "", level + 1);
    }

    @Override
    public void print(PrintStream os, int indent) {
        os.println("Statement {");
        printIndent(os, indent + 1);
        os.println("comment = '" + comment);
        printIndent(os, indent + 1);
        os.println("components = <[");
        for (CodeElem i_components : components) {
            printIndent(os, indent + 2);
            i_components.print(os, indent + 2);
        }
        printIndent(os, indent + 1);
        os.println("]>");
        printIndent(os, indent + 1);
        os.println("concurrent = '" + concurrent);
        printIndent(os, indent + 1);
        os.println("coord = '" + coord);
        printIndent(os, indent + 1);
        os.println("desc = '" + desc);
        printIndent(os, indent + 1);
        os.println("dynamic = '" + dynamic);
        printIndent(os, indent + 1);
        os.println("execute = '" + execute);
        printIndent(os, indent + 1);
        os.println("label = '" + label);
        printIndent(os, indent + 1);
        os.println("name = '" + name);
        printIndent(os, indent);
        os.println("}");
    }

    @Override
    public String typeName() {
        return "Statement";
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
        components.stream().forEach(node -> node.visitDepthFirst(visitor));

        accept(visitor);
    }
}

/********************************************************************************/
