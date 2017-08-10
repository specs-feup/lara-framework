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
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import larac.exceptions.LARACompilerException;
import larac.utils.output.WarningMsg;

@Deprecated
public class JPModel extends XMLParser {

    private final ArrayList<Node> roots;
    private String lastPointcutType = null;

    /**
     * @return the lastPointcutType
     */
    public String getLastPointcutType() {
	return this.lastPointcutType;
    }

    /**
     * @param lastPointcutType
     *            the lastPointcutType to set
     */
    public void setLastPointcutType(String lastPointcutType) {
	this.lastPointcutType = lastPointcutType;
    }

    public Boolean contains(String joinpoint) {
	return getDoc().getElementsByTagName(joinpoint).getLength() != 0;
    }

    public JPModel(String fileName) throws ParserConfigurationException, SAXException, IOException {
	super(fileName);
	this.roots = new ArrayList<>();
	setRoots();
    }

    public JPModel(File file) throws ParserConfigurationException, SAXException, IOException {
	super(file);
	this.roots = new ArrayList<>();
	setRoots();
    }

    private void setRoots() {
	final NodeList tagList = getDoc().getElementsByTagName("roots");
	final Element joinPoints = (Element) (getDoc().getElementsByTagName("joinpoints").item(0));
	for (int i = 0; i < tagList.getLength(); i++) {
	    final Node rts = tagList.item(i);
	    final NodeList rtsList = rts.getChildNodes();
	    for (int j = 0; j < rtsList.getLength(); j++) {
		if (!rtsList.item(j).getNodeName().contains("#")) {
		    this.roots.addAll(getDirectChilds(joinPoints, rtsList.item(j).getNodeName()));
		}
	    }

	}
    }

    private ArrayList<String> visited;

    private ArrayList<String> getPathAux(Node parent, String tag) {
	final ArrayList<String> path = new ArrayList<>();
	if (parent.getNodeName().equals(tag)) {
	    this.lastPointcutType = ((Element) parent).getAttribute("type");
	    return path;
	}

	if (this.visited.contains(parent.getNodeName())) {
	    return null;
	}

	this.visited.add(parent.getNodeName());
	final int pos = this.visited.size() - 1;
	path.add(parent.getNodeName() + "(" + ((Element) parent).getAttribute("type") + ")");

	if (isChild(parent, tag)) {
	    this.lastPointcutType = getAttribute(getChild(parent, tag), "type");
	    return path;
	}

	final NodeList nList = parent.getChildNodes();
	for (int i = 0; i < nList.getLength(); i++) {
	    final Node child = nList.item(i);
	    if (child.getNodeName().contains("#")) {
		continue;
	    }
	    final Node jpNode = getJoinPointDef(getAttribute(child, "type"));
	    if (jpNode == null) {
		continue;
	    }
	    final ArrayList<String> path2 = getPathAux(jpNode, tag);
	    if (path2 != null) {
		if (path.size() > 1) {
		    new WarningMsg().println("More than one path for inital join point: " + tag);
		    new WarningMsg().println("\tWill use: " + path + "->" + tag);
		    break;
		}
		if (!path2.isEmpty()) {
		    path2.set(0, child.getNodeName() + "(" + ((Element) child).getAttribute("type") + ")");
		}
		path.addAll(path2);
	    }
	}
	this.visited.remove(pos);
	if (path.size() <= 1) {
	    return null;
	}
	return path;
    }

    /**
     * Get the path previous to a join point
     * 
     * @param tag
     *            the join point
     * @return the path to the join point
     */
    public ArrayList<String> getPath(String tag) {

	if ("roots".equals(tag)) {

	    throw new LARACompilerException(
		    "The keyword 'roots' cannot be used as a join point in the join point model");
	}

	this.lastPointcutType = null;
	this.visited = new ArrayList<>();
	ArrayList<String> path = null;
	for (final Node root : this.roots) {
	    final ArrayList<String> pathAux = getPathAux(root, tag);
	    if (pathAux != null) {
		if (path != null) {
		    new WarningMsg().println("More than one path for inital join point: " + tag);
		    new WarningMsg().println("\tWill use: " + path + "->" + tag);
		    break;
		}
		path = pathAux;
	    }

	}

	return path;
    }

    private ArrayList<String> getMiddlePathAux(Node parent, String tag) {

	final ArrayList<String> path = new ArrayList<>();
	if (this.visited.contains(parent.getNodeName())) {
	    return null;
	}
	path.add(parent.getNodeName() + "(" + ((Element) parent).getAttribute("type") + ")");
	if (isChild(parent, tag)) {
	    this.lastPointcutType = getAttribute(getChild(parent, tag), "type");
	    return path;
	}
	this.visited.add(parent.getNodeName());
	final int pos = this.visited.size() - 1;

	final NodeList nList = parent.getChildNodes();
	for (int i = 0; i < nList.getLength(); i++) {
	    final Node child = nList.item(i);
	    if (child.getNodeName().contains("#")) {
		continue;
	    }
	    final Node jpNode = getJoinPointDef(getAttribute(child, "type"));
	    if (jpNode == null) {
		continue;
	    }
	    final ArrayList<String> path2 = getMiddlePathAux(jpNode, tag);
	    if (path2 != null) {
		if (path.size() > 1) {
		    new WarningMsg().println("More than one path for " + parent + "." + tag);
		    new WarningMsg().println("\tWill use: " + parent + "->" + path + "->" + tag);
		    break;
		}
		if (!path2.isEmpty()) {
		    path2.set(0, child.getNodeName() + "(" + ((Element) child).getAttribute("type") + ")");
		}
		path.addAll(path2);

	    }

	}
	this.visited.remove(pos);
	if (path.size() <= 1) {
	    return null;
	}
	return path;
    }

    public ArrayList<String> getMiddlePath(String parent, String childTag) {
	this.lastPointcutType = null;
	this.visited = new ArrayList<>();
	this.visited.add(parent);
	final Node jpNode = getJoinPointDef(parent);
	if (jpNode == null) {
	    return null;
	}

	final ArrayList<String> path = new ArrayList<>();
	if (isChild(jpNode, childTag)) {
	    this.lastPointcutType = getAttribute(getChild(jpNode, childTag), "type");
	    return path;
	}

	final NodeList nList = jpNode.getChildNodes();
	ArrayList<String> finalPath = null;
	for (int i = 0; i < nList.getLength(); i++) {

	    final Node child = nList.item(i);
	    final String childName = child.getNodeName();

	    if (childName.contains("#")) {
		continue;
	    }
	    final Node jpNodeChild = getJoinPointDef(getAttribute(child, "type"));
	    if (jpNodeChild == null) {
		continue;
	    }

	    final ArrayList<String> path2 = getMiddlePathAux(jpNodeChild, childTag);
	    if (path2 != null) {
		if (finalPath != null) {
		    new WarningMsg().println("More than one path for " + parent + "." + childTag);
		    new WarningMsg().println("\tWill use: " + parent + "->" + finalPath + "->" + childTag);
		    return finalPath;
		}
		if (!path2.isEmpty()) {
		    path2.set(0, child.getNodeName() + "(" + ((Element) child).getAttribute("type") + ")");
		}
		finalPath = path2;
	    }
	}
	return finalPath;
    }

    private boolean isChild(Node root, String tag) {
	final NodeList nList = root.getChildNodes();

	// see if next element (tag) is direct child
	for (int i = 0; i < nList.getLength(); i++) {

	    if (nList.item(i).getNodeName().equals(tag)) {
		return true;
	    }
	}
	// verify the root type and search in that (super) type
	// such as : <assignment type="expression">
	// `-> search inside join point "expression"
	final String rootType = getAttribute(root, "type");
	if (rootType != null && !rootType.equals(root.getNodeName())) {
	    final Node superJoinPoint = getJoinPointDef(rootType);
	    if (superJoinPoint != null) {
		return isChild(superJoinPoint, tag);
	    }
	}

	return false;
    }

    private ArrayList<Node> getDirectChilds(Element joinPoint, String tag) {
	final NodeList descendentNodes = joinPoint.getElementsByTagName(tag);
	ArrayList<Node> childs = null;
	if (descendentNodes.getLength() == 0) {

	    // verify the root type and search in that (super) type
	    // such as : <assignment type="expression">
	    // `-> search inside join point "expression"
	    final String joinPointType = getAttribute(joinPoint, "type");
	    if (joinPointType != null && !joinPointType.equals(joinPoint.getNodeName())) {

		final Node superJoinPoint = getJoinPointDef(joinPointType);
		if (superJoinPoint != null) {
		    childs = getDirectChilds((Element) superJoinPoint, tag);
		} else {
		    throw new LARACompilerException(
			    "Join point \"" + joinPoint.getNodeName() + "\" does not contain \"" + tag + "\"");
		}
	    } else {

		throw new LARACompilerException(
			"Join point \"" + joinPoint.getNodeName() + "\" does not contain \"" + tag + "\"");
	    }

	} else {
	    childs = new ArrayList<>();
	}
	for (int i = 0; i < descendentNodes.getLength(); i++) {
	    if (descendentNodes.item(i).getParentNode().getNodeName().equals(joinPoint.getNodeName())) {
		childs.add(descendentNodes.item(i));
	    }
	}
	return childs;
    }

    /*************************
     * For test purposes ******************************** public static void main(String[] args) { JPModel model = new
     * JPModel("src\\joinPointModel.xml");
     * 
     * ArrayList<String> path2 = new ArrayList<String>(); path2.add("function"); path2.add("if"); path2.add("loop");
     * 
     * ArrayList<String> path = model.getPath(path2.get(0)); for(int i = 0; i < path2.size()-1;i++){
     * path.add(path2.get(i)); ArrayList<String> path3 = model.getMiddlePath(model.getLastPointcutType(),
     * path2.get(i+1)); if(path3 == null) new ErrorMsg(path2.get(i)+"-"+ path2.get(i+1)); else path.addAll(path3); }
     * path.add(path2.get(path2.size()-1)); //System.out.println(model.getPath(path2.get(0))); System.out.println(path);
     * } /
     ****************************************************************************/

    public String getParentName(Node n) {
	if (n.getParentNode() != null) {
	    return n.getParentNode().getNodeName();
	}
	return "";
    }

    public Node getJoinPointDef(String jp) {
	NodeList jps = getDoc().getElementsByTagName("joinpoints");
	final Node joinPoints = jps.item(0);
	jps = joinPoints.getChildNodes();
	for (int i = 0; i < jps.getLength(); i++) {
	    final String nodeName = jps.item(i).getNodeName();
	    if (nodeName.equals(jp)) {
		return jps.item(i);
	    }
	}
	return null;
    }

    public String getAttribute(Node n, String attr) {
	final Node namedItem = n.getAttributes().getNamedItem(attr);
	if (namedItem == null) {
	    return null;
	}
	return namedItem.getNodeValue();
    }

    public Node getChild(Node parent, String child) {
	return getDirectChilds((Element) parent, child).get(0);
    }

    public JPModel copy() throws ParserConfigurationException, SAXException, IOException {
	return new JPModel(getFileName());
    }

    public ArrayList<Node> getRoots() {
	return this.roots;
    }
}
