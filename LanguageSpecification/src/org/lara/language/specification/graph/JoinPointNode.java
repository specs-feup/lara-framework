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

import java.util.ArrayList;
import java.util.List;

import pt.up.fe.specs.util.graphs.GraphNode;

public class JoinPointNode extends GraphNode<JoinPointNode, JPNodeInfo, JPEdgeInfo> {

    public JoinPointNode(String id, JPNodeInfo nodeInfo) {
        super(id, nodeInfo);
    }

    @Override
    protected JoinPointNode getThis() {
        return this;
    }

    /**
     * Return a list of {@link JPEdgeInfo} connecting to a target Node.
     * 
     * @param targetId the target node
     * @return a list of {@link JPEdgeInfo}
     */
    public List<JPEdgeInfo> getEdges(String targetId) {

        final List<JPEdgeInfo> edges = new ArrayList<>();
        for (final JPEdgeInfo jpEdgeInfo : getChildrenConnections()) {

            if (jpEdgeInfo.getTargetId().equals(targetId)) {
                edges.add(jpEdgeInfo);
            }
        }
        return edges;
    }
}
