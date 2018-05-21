/**
 * Copyright 2016 SPeCS.
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

import java.util.Optional;

import org.dojo.jsl.parser.ast.ASTAction;
import org.dojo.jsl.parser.ast.SimpleNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import larac.objects.Enums.Types;

public class AspectIRFactory {

    /**
     * See {@link AspectIRFactory#varDeclStmtToXML(Document, Element, String, SimpleNode, String)}
     * 
     * @param doc
     * @param parent
     * @param name
     * @param coords
     */
    public static void varDeclStmtToXML(Document doc, Element parent, String name, String coords) {
        varDeclStmtToXML(doc, parent, name, null, coords, null);
    }

    /**
     * Adds a variable declaration to the document, using a name, the initial value (can be null) and the coordinates
     * 
     * @param doc
     * @param parent
     * @param name
     * @param init
     * @param coords
     */
    public static void varDeclStmtToXML(Document doc, Element parent, String name, SimpleNode init, String coords,
            Optional<String> type) {
        final Element statementDeclEl = doc.createElement("statement");
        statementDeclEl.setAttribute("name", "vardecl");
        statementDeclEl.setAttribute("coord", coords);
        varDeclExprToXML(doc, statementDeclEl, name, init, type);
        parent.appendChild(statementDeclEl);

    }

    /**
     * See {@link AspectIRFactory#varDeclExprToXML(Document, Element, String, SimpleNode)}
     * 
     * @param doc
     * @param parent
     * @param name
     */
    public static void varDeclExprToXML(Document doc, Element parent, String name) {
        varDeclExprToXML(doc, parent, name, null, null);
    }

    /**
     * Adds the inner elements of the variable declaration, i.e., an expression containing a literal, of type string,
     * with the name of the variable, and an expression containing the initial value. If init is null, then the initial
     * expression is empty
     * 
     * @param doc
     * @param parent
     * @param name
     * @param init
     */
    public static void varDeclExprToXML(Document doc, Element parent, String name, SimpleNode init,
            Optional<String> type) {
        // TODO- add type ifPresent
        final Element nameExprEl = doc.createElement("expression");
        parent.appendChild(nameExprEl);
        // if(var.getName().startsWith("$"))
        // nameExprEl.setAttribute("type", Types.Joinpoint.toString());
        final Element literalNameEl = doc.createElement("literal");
        literalNameEl.setAttribute("value", name);
        literalNameEl.setAttribute("type", Types.String.toString());
        nameExprEl.appendChild(literalNameEl);

        if (init instanceof ASTAction) {
            ((ASTAction) init).actionExprToXML(doc, parent, true);
        } else {

            final Element initEl = doc.createElement("expression");
            initEl.setAttribute("desc", "init");
            parent.appendChild(initEl);
            if (init != null) {
                init.toXML(doc, initEl);
            }
        }

    }

    public static void idRef(Document doc, Element parent, String name) {
        final Element el = doc.createElement("id");
        el.setAttribute("name", name);
        parent.appendChild(el);
    }
}
