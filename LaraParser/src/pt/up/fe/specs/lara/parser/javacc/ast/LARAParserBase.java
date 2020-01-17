/**
 * Copyright 2015 SPeCS.
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

package pt.up.fe.specs.lara.parser.javacc.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import pt.up.fe.specs.util.SpecsCheck;
import pt.up.fe.specs.util.SpecsLogs;

public class LARAParserBase implements LARAEcmaScriptTreeConstants,
        LARAEcmaScriptConstants {

    private static final String DEFAULT_NAMED_PARAM = "defaultParam";

    Stack<SimpleNode> nodeStack = new Stack<>();

    // ArrayList<ASTFunctionDef> functions = new ArrayList<ASTFunctionDef>();
    // ArrayList<SimpleNode> variables = new ArrayList<SimpleNode>();

    protected void jjtreeOpenNodeScope(SimpleNode astNode) {
        nodeStack.push(astNode);
    }

    protected void jjtreeCloseNodeScope(SimpleNode astNode) {
        if (nodeStack.size() > 0) {
            nodeStack.pop();
        }

        // if (astNode instanceof ASTFunctionDef)
        // addFunction((ASTFunctionDef) astNode);
        // else if (astNode instanceof ASTPoint)
        // addVariable(astNode);
    }

    protected SimpleNode getCurrentNode() {
        if (nodeStack.size() > 0) {
            return nodeStack.peek();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    protected void set(SimpleNode node, Object... values) {
        // Ignore if no values
        if (values.length == 0) {
            return;
        }

        List<Object> valuesList = (List<Object>) getNamed(node, DEFAULT_NAMED_PARAM);

        if (valuesList == null) {
            valuesList = new ArrayList<>();
            set(node, DEFAULT_NAMED_PARAM, valuesList);
        }

        // List<Object> valuesList = new ArrayList<>();
        //
        // var previousValue = node.jjtGetValue();
        //
        // if (previousValue != null) {
        // SpecsCheck.checkClass(previousValue, Collection.class);
        // valuesList.addAll((Collection<Object>) node.jjtGetValue());
        // // SpecsLogs.warn("Overwriting value of SimpleNode");
        // }

        for (var value : values) {
            valuesList.add(value);
        }

        // node.jjtSetValue(valuesList);
        /*
        if (values.length == 0) {
            node.jjtSetValue(null);
        } else if (values.length == 1) {
            node.jjtSetValue(values[0]);
        } else {
            node.jjtSetValue(values);
        }
        */
    }

    protected void setNamed(SimpleNode node, String name, Object value) {
        @SuppressWarnings("unchecked")
        var valueMap = (HashMap<String, Object>) node.jjtGetValue();

        // Value is always an hashmap
        if (valueMap != null) {
            valueMap = new HashMap<>();
            node.jjtSetValue(valueMap);
        }

        var previousValue = valueMap.put(name, value);
        if (previousValue != null) {
            SpecsLogs.warn("Replacing value '" + previousValue + "' with " + value);
        }
    }

    protected Object getNamed(SimpleNode node, String name) {
        @SuppressWarnings("unchecked")
        var valueMap = (HashMap<String, Object>) node.jjtGetValue();

        if (valueMap == null) {
            return null;
        }

        return valueMap.get(name);
    }

    protected void inc(SimpleNode node) {
        if (node.jjtGetValue() == null) {
            node.jjtSetValue(Integer.valueOf(0));
            return;
        }

        var currentValue = node.jjtGetValue();

        SpecsCheck.checkClass(currentValue, Integer.class);

        Integer newValue = ((Integer) currentValue) + 1;

        node.jjtSetValue(newValue);
    }

    // void addFunction(ASTFunctionDef function)
    // {
    // functions.add(function);
    // }
    //
    // public ArrayList<ASTFunctionDef> getFunctions()
    // {
    // return functions;
    // }
    //
    // void addVariable (SimpleNode varNode)
    // {
    // variables.add(varNode);
    // }
    //
    // public ArrayList<SimpleNode> getVriables()
    // {
    // return variables;
    // }
}
