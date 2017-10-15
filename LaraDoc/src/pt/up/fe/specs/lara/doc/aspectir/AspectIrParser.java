/**
 * Copyright 2017 SPeCS.
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

package pt.up.fe.specs.lara.doc.aspectir;

import java.util.HashSet;
import java.util.Set;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.lara.interpreter.aspectir.Base;
import org.lara.interpreter.aspectir.CodeElem;
import org.lara.interpreter.aspectir.Expression;
import org.lara.interpreter.aspectir.Statement;

import com.google.common.base.Preconditions;

import pt.up.fe.specs.lara.doc.aspectir.elements.StatementElement;
import pt.up.fe.specs.lara.doc.aspectir.elements.VarDeclElement;
import pt.up.fe.specs.lara.doc.comments.LaraDocComment;
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTag;
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTagProperty;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.classmap.BiFunctionClassMap;
import tdrc.utils.StringUtils;

public class AspectIrParser {

    private final BiFunctionClassMap<Base, LaraDocComment, AspectIrElement> parsers;
    private final Set<String> seenUnsupportedNodes;

    public AspectIrParser() {
        this.parsers = new BiFunctionClassMap<>();
        this.seenUnsupportedNodes = new HashSet<>();
        addParsers(parsers);
    }

    private void addParsers(BiFunctionClassMap<Base, LaraDocComment, AspectIrElement> parsers) {
        parsers.put(Statement.class, this::parseStatement);
    }

    public static String toXml(Base aspectIrNode) {
        try {
            return StringUtils.xmlToStringBuffer(aspectIrNode.getXmlDocument(), 3).toString();
        } catch (TransformerFactoryConfigurationError | TransformerException e) {
            throw new RuntimeException("Could not convert aspect IR node to XML: ", e);
        }
    }

    public AspectIrElement parse(Base aspectIrNode, LaraDocComment laraComment) {
        // System.out.println("XML:" + toXml(aspectIrNode));
        return parsers.apply(aspectIrNode, laraComment);
    }

    public AspectIrElement parseStatement(Statement statement, LaraDocComment laraComment) {
        // Check type of statement
        switch (statement.name) {
        case "vardecl":
            return parseVarDeclStatement(statement, laraComment);
        default:
            if (!seenUnsupportedNodes.contains(statement.name)) {
                SpecsLogs.msgInfo("AspectIrParser does not support yet statement '" + statement.name + "'");
                seenUnsupportedNodes.add(statement.name);
            }

            return new StatementElement(laraComment);
        }
    }

    private AspectIrElement parseVarDeclStatement(Statement statement, LaraDocComment laraComment) {
        // Extract vardecl name. The first child should be an expression, with a
        // literal of type String
        Preconditions.checkArgument(!statement.components.isEmpty(),
                "Expected var decl to have at least one code element");
        CodeElem expression = statement.components.get(0);

        Preconditions.checkArgument(expression instanceof Expression,
                "Expected first code element of var decl to be an expression");

        String vardeclName = CodeElems.parseStringLiteralExpr((Expression) expression);
        System.out.println("LARA COMMENT:" + laraComment);
        laraComment.addTagIfMissing(new JsDocTag("alias").setValue(JsDocTagProperty.NAME_PATH, vardeclName));

        return new VarDeclElement(vardeclName, laraComment);
    }

    /*
     * public AspectIrElement parseDeclaration(Statement declaration,
     * LaraDocComment laraComment) {
     * 
     * System.out.println("XML:" + toXml(declaration)); // TODO Auto-generated
     * method stub return null; }
     * 
     * public AspectIrElement parseAspect(Aspect aspect, LaraDocComment
     * laraComment) { // TODO Auto-generated method stub return null; }
     */

}
