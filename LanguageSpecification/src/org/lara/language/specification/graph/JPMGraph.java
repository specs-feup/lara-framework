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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import pt.up.fe.specs.util.graphs.Graph;

/**
 * Create a join point model graph Representation
 * 
 * @author Tiago
 *
 */
public class JPMGraph extends Graph<JoinPointNode, JPNodeInfo, JPEdgeInfo> {

    private String name;

    /**
     * Create a join point model graph Representation
     * 
     * @param graphName the name of the graph
     */
    public JPMGraph(String graphName) {
        name = graphName;
    }

    private JPMGraph(List<JoinPointNode> nodeList, Map<String, JoinPointNode> graphNodes) {
        super(nodeList, graphNodes);
    }

    @Override
    public JPMGraph getUnmodifiableGraph() {
        return new JPMGraph(Collections.unmodifiableList(getNodeList()), Collections.unmodifiableMap(getGraphNodes()));
    }

    @Override
    protected JoinPointNode newNode(String operationId, JPNodeInfo nodeInfo) {
        return new JoinPointNode(operationId, nodeInfo);
    }

    /**
     * Add a new node with default {@link JPNodeInfo}.
     * 
     * @param sourceId
     * @param targetId
     */
    public JoinPointNode addNode(String operationId) {
        return addNode(operationId, new JPNodeInfo(operationId));
    }

    /**
     * Add a new select between a source join point and a target join point.
     * 
     * @param alias
     * @param sourceId
     * @param targetId
     */
    public void addSelect(String label, String sourceId, String targetId) {
        addConnection(sourceId, targetId, JPEdgeInfo.newSelects(label, sourceId, targetId));
    }

    /**
     * Add a new select between a source join point and a target join point.
     * 
     * @param sourceId
     * @param targetId
     */
    public void addSelect(String sourceId, String targetId) {
        addConnection(sourceId, targetId, JPEdgeInfo.newSelects(null, sourceId, targetId));
    }

    /**
     * Add a new extend between a source join point and a target join point.
     * 
     * @param sourceId
     * @param targetId
     */
    public void addExtend(String sourceId, String targetId) {
        addConnection(sourceId, targetId, JPEdgeInfo.newExtends(sourceId, targetId));
    }

    /**
     * Convert this graph into the graphviz 'DSL' format
     * 
     * @return a string representing the graph in the graphviz form
     */
    public String toGraphviz() {
        final String lineSeparator = System.lineSeparator();
        final StringBuilder graphStr = new StringBuilder("digraph ");
        graphStr.append(name);
        graphStr.append("{");
        graphStr.append(lineSeparator);

        for (final JoinPointNode joinPointNode : getNodeList()) {
            final String thisNode = joinPointNode.getId();

            joinPointNode.getChildren().forEach(node -> {
                final String targetId = node.getId();
                final String edgeStr = "\t" + thisNode + "->" + targetId;
                joinPointNode.getEdges(targetId).forEach(edge -> {
                    graphStr.append(edgeStr);
                    graphStr.append(edge.toString());
                    graphStr.append(";\n");
                });
            });

        }
        graphStr.append("}");
        return graphStr.toString();
    }
}
