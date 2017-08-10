/**
 * Copyright 2015 SPeCS Research Group.
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

package org.lara.language.specification.graph;

import java.io.File;

import pt.up.fe.specs.util.SpecsIo;

public class TestGraph {
    public static void main(String[] args) {
        final JPMGraph graph = new JPMGraph("TestWeaver");
        graph.addNode("file");
        graph.addNode("function");
        graph.addNode("body");
        graph.addNode("loop");
        graph.addNode("stmt");
        graph.addNode("expr");
        graph.addNode("binExpr");
        graph.addNode("arrayAcc");
        graph.addSelect(null, "file", "function");
        graph.addSelect(null, "function", "body");
        graph.addSelect(null, "body", "loop");
        graph.addSelect(null, "loop", "body");
        graph.addSelect("firstStmt", "body", "stmt");
        graph.addSelect(null, "body", "stmt");
        graph.addSelect(null, "body", "expr");
        graph.addSelect(null, "body", "arrayAcc");
        graph.addSelect(null, "body", "binExpr");
        graph.addExtend("binExpr", "expr");
        graph.addExtend("arrayAcc", "expr");
        // graph.addSelect(null,"expr", "arrayAcc");
        // System.out.println(graph);
        final String graphviz = graph.toGraphviz();
        System.out.println(graphviz);
        SpecsIo.write(new File("test.dot"), graphviz);
    }
}
