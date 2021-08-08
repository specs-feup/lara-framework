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

package pt.up.fe.specs.lara.doc.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.classmap.ClassMap;
import pt.up.fe.specs.util.treenode.ATreeNode;

public abstract class LaraDocNode extends ATreeNode<LaraDocNode> {
    /*
    private static final BiConsumerClassMap<LaraDocNode, LaraDocNode> NODES_MAPPER;
    static {
        NODES_MAPPER = new BiConsumerClassMap<>();
    
        NODES_MAPPER.put(LaraDocModule.class, LaraDocNode::mapLaraDocModule);
    
    }
    */
    /*
    private static final void mapLaraDocModule(LaraDocModule module,
            LaraDocNode parent) {
    
        Map<String, LaraDocModule> modules = parent.getNodes(LaraDocModule.class);
    
        LaraDocModule previousModule = modules.put(module.getImportPath(), module);
        if (previousModule != null) {
            SpecsLogs.msgInfo("Replacing LARA module with import path '" + module.getImportPath()
                    + "': current file is ' " + module.getMainLara() + " ' and previous file is '"
                    + previousModule.getMainLara()
                    + " '");
    
            // Remove from parent
            parent.removeChild(previousModule);
        }
    
        parent.addChild(module);
    }
    */
    // private final ClassMap<LaraDocNode, Map<String, ? extends LaraDocNode>> nodesMap;
    private final ClassMap<LaraDocNode, Map<String, LaraDocNode>> nodesMap;
    private static final Map<String, LaraDocNode> DUMMY_NODE = new HashMap<>();

    public LaraDocNode() {
        super(Collections.emptyList());
        nodesMap = new ClassMap<>();
        // nodesMap.setDefaultValue(DUMMY_NODE);
        nodesMap.put(LaraDocNode.class, DUMMY_NODE);
        // nodesMapper = buildNodesMapper(nodesMap);
    }

    public abstract String getId();

    public <T extends LaraDocNode> Map<String, T> getNodes(Class<T> aClass) {
        @SuppressWarnings("unchecked") // Internally, makes sure to only add nodes to the map of the corresponding class
        Map<String, T> nodes = (Map<String, T>) getNodesRaw(aClass);

        // @SuppressWarnings("unchecked") // Only puts maps that respect the class
        return (Map<String, T>) nodes;
        /*
        Map<String, T> nodes = (Map<String, T>) nodesMap.get(aClass);
        
        if (nodes == null) {
            nodes = new HashMap<>();
            nodesMap.put(aClass, nodes);
        }
        
        return nodes;
        */
    }

    public <T extends LaraDocNode> Optional<T> getNode(Class<T> nodeClass, String id) {
        Map<String, LaraDocNode> nodes = getNodesRaw(nodeClass);
        @SuppressWarnings("unchecked") // Internally we guarantee that the type will be the same
        T node = (T) nodes.get(id);
        return Optional.ofNullable(node);
    }

    private Map<String, LaraDocNode> getNodesRaw(Class<? extends LaraDocNode> aClass) {

        Map<String, LaraDocNode> nodes = nodesMap.get(aClass);

        if (nodes == DUMMY_NODE) {
            nodes = new HashMap<>();
            nodesMap.put(aClass, nodes);
        }

        return nodes;
    }

    // private FunctionClassMap<LaraDocNode, Boolean> buildNodesMapper(
    // ClassMap<LaraDocNode, Map<String, LaraDocNode>> nodesMap) {
    //
    // FunctionClassMap<LaraDocNode, Boolean> nodesMapper = new FunctionClassMap<>();
    //
    // return nodesMapper;
    // }

    @Override
    public String toContentString() {
        return "<content not implemented for node '" + getClass() + "'>";
    }

    @Override
    protected LaraDocNode getThis() {
        return this;
    }

    @Override
    protected LaraDocNode copyPrivate() {
        throw new RuntimeException("Copying not supported");
    }

    public void add(LaraDocNode node) {
        add(node, false);
    }

    public void addIfNotPresent(LaraDocNode node) {
        add(node, true);
    }

    private void add(LaraDocNode node, boolean checkIfPresent) {
        Map<String, LaraDocNode> nodes = getNodesRaw(node.getClass());

        String key = node.getId();

        if (checkIfPresent) {
            if (nodes.containsKey(key)) {
                return;
            }
        }

        // Check if node will be replaced
        LaraDocNode previousNode = nodes.put(key, node);
        if (previousNode != null) {
            SpecsLogs.msgInfo("Replacing LARA doc node '" + node.toContentString() + "' with '"
                    + previousNode.toContentString() + "'");

            // Remove previous node
            removeChild(previousNode);
        }

        // Add node
        addChild(node);
    }

    public <T extends LaraDocNode> T getOrCreateNode(Class<T> nodeClass, String id, Supplier<T> constructor) {

        /*
        Optional<T> possibleNode = getNode(nodeClass, id);
        
        if (possibleNode.isPresent()) {
            return possibleNode.get();
        }
        
        T node = constructor.get();
        add(node);
        return node;
        */

        T node = getNode(nodeClass, id).orElse(constructor.get());

        // Add module if not present
        addIfNotPresent(node);

        return node;
    }

    public boolean hasNode(LaraDocNode node) {
        return getNodesRaw(node.getClass()).containsKey(node.getId());
    }

    public void remove(LaraDocNode node) {
        if (!hasNode(node)) {
            SpecsLogs.warn("Removing node that does not exist as child. Node: " + node + "\n Parent:" + this);
            return;
        }

        Map<String, LaraDocNode> nodes = getNodesRaw(node.getClass());

        // Remove node from map and from tree
        nodes.remove(node.getId());
        removeChild(node);
    }

}
