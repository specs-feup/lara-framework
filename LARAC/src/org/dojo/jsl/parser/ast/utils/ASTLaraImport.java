/**
 * Copyright 2021 SPeCS.
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

package org.dojo.jsl.parser.ast.utils;

import java.util.List;
import java.util.stream.Collectors;

import org.dojo.jsl.parser.ast.SimpleNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import pt.up.fe.specs.util.SpecsCheck;

public class ASTLaraImport extends SimpleNode {

    private static final String NODE_NAME = "LaraImport";

    private final List<String> qualifiedImport;

    public ASTLaraImport(List<String> qualifiedImport) {
        super(-1);

        this.qualifiedImport = qualifiedImport;
    }

    @Override
    public Object organize(Object obj) {
        // Do nothing
        return null;
    }

    @Override
    public void globalToXML(Document doc, Element parent) {
        SpecsCheck.checkArgument(parent.getNodeName().equals("aspects"),
                () -> "Expected node to be 'aspects', is '" + parent.getNodeName() + "'");

        // HACK: To avoid regeneration the Java classes from XML, using attributes Statement already has
        final Element statementDeclEl = doc.createElement("declaration");
        statementDeclEl.setAttribute("name", NODE_NAME);
        statementDeclEl.setAttribute("desc",
                "laraImport(\'" + qualifiedImport.stream().collect(Collectors.joining(".")) + "\')");

        // Get first node that is not a LaraImport, or null if there are no nodes or only has imports
        var insertPoint = getInsertPoint(parent);
        parent.insertBefore(statementDeclEl, insertPoint);

        toXML(doc, statementDeclEl);
    }

    private Node getInsertPoint(Element parent) {
        var nodeList = parent.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            var item = nodeList.item(i);

            // If LaraImport, skip
            if (item.getNodeName().equals("declaration") &&
                    NODE_NAME.equals(item.getAttributes().getNamedItem("name").getNodeValue())) {
                continue;
            }

            // Found item
            return item;
        }

        return null;
    }

    @Override
    public void toXML(Document doc, Element parent) {
        // Do nothing
    }

    @Override
    public String toString() {
        return "LaraImport (" + qualifiedImport + ")";
    }
}
