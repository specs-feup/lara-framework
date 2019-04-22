/**
 * Copyright 2019 SPeCS.
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

package pt.up.fe.specs.lara.loc.visitors;

import pt.up.fe.specs.lara.aspectir.ExprOp;
import pt.up.fe.specs.lara.aspectir.GenericVisitor;

public class FunctionsVisitor implements GenericVisitor {

    private static final String FUNCTION_DECL = "fndecl";
    private static final String FUNCTION_OP = "FN";

    private int numFunctions;

    public FunctionsVisitor() {
        this.numFunctions = 0;
    }

    // @Override
    // public void visit(Statement statement) {
    // if (FUNCTION_DECL.equals(statement.name)) {
    // // numFunctions++;
    // }
    // }

    @Override
    public void visit(ExprOp exprOp) {
        if (FUNCTION_OP.equals(exprOp.name)) {
            numFunctions++;
        }

    }

    public int getNumFunctions() {
        return numFunctions;
    }
}
