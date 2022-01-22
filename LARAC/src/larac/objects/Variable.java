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
/**
 * 
 */
package larac.objects;

import org.dojo.jsl.parser.ast.SimpleNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import larac.objects.Enums.Types;
import larac.utils.output.NormalMsg;

public class Variable {
    public int staticCounter = 0;
    public String staticName = "_larac_call_var_";
    private String name;
    private Types type;
    // private SimpleNode initialize; // REMOVED INIT as it is provoking a big issue when declaring variables with
    // the same name
    private SimpleNode expression;
    private boolean notFound;

    /*
    public Variable(String name, SimpleNode init) {
    	this.name = name;
    	if (!(init instanceof ASTFunctionDeclaration)) {
    	    init.organize(init.jjtGetParent());
    	}
    
    	initialize = init;
    	expression = init;
    	type = init.getExpressionType();
    	if (type == Types.FNDecl) {
    	    type = Types.FN;
    	}
    	if (name.startsWith("$")) {
    	    type = Types.Joinpoint;
    	}
    	notFound = false;
    }
    */
    public Variable(String name) {
        this.name = name;
        // initialize = expression = null;
        type = Types.getDefault();
        notFound = false;
    }

    public Variable(String name, Types type) {
        this.name = name;
        this.type = type;
        notFound = false;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the type
     */
    public Types getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(Types type) {
        this.type = type;
    }

    /**
     * @return the initialize
     *
     *         public SimpleNode getInitialize() { return initialize; }
     */

    /**
     * @param initialize
     *            the initialize to set
     *
     *            public void setInitialize(SimpleNode initialize) { this.initialize = initialize; }
     */

    /**
     * @return the expression
     */
    public SimpleNode getExpression() {
        return expression;
    }

    /**
     * @param expression
     *            the expression to set
     */
    public void setExpression(SimpleNode expression) {
        this.expression = expression;
    }

    public void printIR() {
        new NormalMsg()
                .println("name \"" + getName() + "\"" + "\n type: " + type + "\n"); // + "\n initialize: " + initialize
    }

    public void toXML(Document doc, Element parent) {
        final Element el = doc.createElement("id");
        parent.appendChild(el);
        // String xmlName = name.startsWith("$")?name.substring(1):name;
        final String xmlName = name;
        el.setAttribute("name", xmlName);
        // if(type != Types.Undefined)
        // el.setAttribute("type", type.name());

    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        final Variable newVar = new Variable(getName());
        return newVar;
    }

    /**
     * @return the notFound
     */
    public boolean isNotFound() {
        return notFound;
    }

    /**
     * @param notFound
     *            the notFound to set
     */
    public void setNotFound(boolean notFound) {
        this.notFound = notFound;
    }
}
