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

import java.util.HashSet;
import java.util.Set;

import pt.up.fe.specs.lara.aspectir.Aspect;
import pt.up.fe.specs.lara.aspectir.GenericVisitor;
import pt.up.fe.specs.lara.aspectir.ParameterList;
import pt.up.fe.specs.lara.aspectir.Statement;

public class StatementsVisitor implements GenericVisitor {

    private int numStmts;
    private Set<String> seenLocations;

    public StatementsVisitor() {
        numStmts = 0;
        seenLocations = new HashSet<>();

    }

    @Override
    public void visit(Statement statement) {
        // AspectIR processing can insert new statements, distinguish them by location
        if (seenLocations.contains(statement.coord)) {
            return;
        }

        seenLocations.add(statement.coord);
        numStmts++;
    }

    @Override
    public void visit(ParameterList parameterList) {
        numStmts++;
    }

    @Override
    public void visit(Aspect aspect) {
        numStmts++;
    }

    public int getNumStmts() {
        return numStmts;
    }
}
