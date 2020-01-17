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

import java.util.Stack;

public class LARAParserBase implements LARAEcmaScriptTreeConstants,
	LARAEcmaScriptConstants {

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
