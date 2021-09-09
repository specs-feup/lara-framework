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

import java.io.InputStream;

import org.dojo.jsl.parser.ast.SimpleNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import larac.LaraC;
import pt.up.fe.specs.util.SpecsCheck;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.lazy.Lazy;
import pt.up.fe.specs.util.utilities.Incrementer;

public class ASTScriptImport extends SimpleNode {

    private static final ThreadLocal<Incrementer> CURRENT_INDEX = ThreadLocal.withInitial(() -> new Incrementer());

    private final String path;
    private final Lazy<String> scriptContents;

    public ASTScriptImport(InputStream scriptContents, String path) {
        super(-1);

        // To ensure the stream is read only once
        this.scriptContents = Lazy.newInstance(() -> SpecsIo.read(scriptContents));
        this.path = path;
    }

    public String getScriptContents() {
        return scriptContents.get();
    }

    @Override
    public Object organize(Object obj) {
        // Do nothing
        return null;
    }

    @Override
    public void declareGlobal(LaraC lara) {
        lara.aspectIR().addGlobalElement("ScriptImport_" + CURRENT_INDEX.get().getAndIncrement(), this);
    }

    @Override
    public void globalToXML(Document doc, Element parent) {
        SpecsCheck.checkArgument(parent.getNodeName().equals("aspects"),
                () -> "Expected node to be 'aspects', is '" + parent.getNodeName() + "'");

        // HACK: To avoid regeneration the Java classes from XML, using attributes Statement already has
        final Element statementDeclEl = doc.createElement("declaration");
        statementDeclEl.setAttribute("name", "ScriptImport");
        statementDeclEl.setAttribute("coord", path);
        statementDeclEl.setAttribute("desc", scriptContents.get());
        // statementDeclEl.setAttribute("script", scriptContents.get());
        // final Element statementDeclEl = doc.createElement("scriptImport");
        // statementDeclEl.setNodeValue(scriptContents.get());

        // System.out.println("PARENT: " + parent.getNodeName());
        // final Element statementDeclEl = doc.createElement("scriptImport");
        // statementDeclEl.setNodeValue(scriptContents.get());
        // statementDeclEl.setAttribute("name", "scriptImport");
        // statementDeclEl.setAttribute("path", path);
        // statementDeclEl.setAttribute("script", scriptContents.get());
        // statementDeclEl.setAttribute("coord", getCoords());
        // addXMLComent(statementDeclEl);
        parent.appendChild(statementDeclEl);
        toXML(doc, statementDeclEl);
    }

    @Override
    public void toXML(Document doc, Element parent) {
        // Do nothing
    }

    @Override
    public String toString() {
        return "ImportedScript (" + path + ")";
    }
}
