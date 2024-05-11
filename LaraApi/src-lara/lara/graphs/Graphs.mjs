laraImport("lara.graphs.NodeData");
laraImport("lara.graphs.EdgeData");
laraImport("lara.graphs.DotFormatter");

import cytoscape from "lara-js/api/libs/cytoscape-3.21.1.min.cjs";

/**
 * Utility class related with graph creation and manipulation.
 *
 * Current implementation uses Cytoscape.js (https://js.cytoscape.org/)
 */
class Graphs {
    static #isLibLoaded = false;

    /**
     * @param {Object} [config = {}] configuration for the graph, according to what Cytoscape accepts as configuration object
     */
    static newGraph(config) {
        // Ensure library is loaded
        Graphs.loadLibrary();

        const _config = config ?? {};

        return cytoscape(_config);
    }

    static loadLibrary() {
        if (Graphs.#isLibLoaded) {
            return;
        }

        globalThis.cytoscape = cytoscape;

        Graphs.#isLibLoaded = true;
    }

    static addNode(graph, nodeData) {
        let _nodeData = nodeData ?? {};

        // Check if NodeData
        if (!(_nodeData instanceof NodeData)) {
            _nodeData = Object.assign(new NodeData(), _nodeData);
        }

        return graph.add({ group: "nodes", data: _nodeData });
    }

    static addEdge(graph, sourceNode, targetNode, edgeData) {
        let _edgeData = edgeData ?? {};

        // Check if EdgeData
        if (!(_edgeData instanceof EdgeData)) {
            _edgeData = Object.assign(new EdgeData(), _edgeData);
        }

        _edgeData.source = sourceNode.id();
        _edgeData.target = targetNode.id();

        return graph.add({ group: "edges", data: _edgeData });
    }

    /**
     *
     * @param {graph} graph
     * @param {lara.graphs.DotFormatter} dotFormatter
     * @returns
     */
    static toDot(graph, dotFormatter) {
        dotFormatter ??= new DotFormatter();

        var dot = "digraph test {\n";

        const layout = dotFormatter.getLayoutOptions();
        dot += layout + "\n";

        // Declare nodes
        for (const node of graph.nodes()) {
            dot +=
                '"' +
                node.id() +
                '" [label="' +
                dotFormatter.getNodeLabel(node) +
                '" shape=box';

            // Add node attributes
            const nodeAttrs = dotFormatter.getNodeAttributes(node);
            dot += nodeAttrs.length === 0 ? "" : " " + nodeAttrs;

            dot += "];\n";
        }

        for (const edge of graph.edges()) {
            dot +=
                '"' +
                edge.data().source +
                '" -> "' +
                edge.data().target +
                '" [label="' +
                dotFormatter.getEdgeLabel(edge) +
                '"';

            // Get edge attributes
            const edgeAttrs = dotFormatter.getEdgeAttributes(edge);
            dot += edgeAttrs.length === 0 ? "" : " " + edgeAttrs;

            dot += "];\n";
        }

        dot += "}\n";

        return dot;
    }

    /**
     *
     * @param {node} node
     * @param {boolean} [loopsAreLeafs = false]
     * @returns true if the outdegree (number of edges with this node as source) is zero, false otherwise. By default, if a node has a connection to itself (loop) it is not considered a leaf
     */
    static isLeaf(node, loopsAreLeafs = false) {
        const includeLoops = !loopsAreLeafs;
        return node.outdegree(includeLoops) === 0;
    }

    /**
     * Removes a node from the graph. Before removing the node, creates connections between all connecting sources and targets.
     *
     * @param {graph} graph
     * @param {node} node
     * @param {(edge, edge) -> EdgeData)} edgeMap function that receives the incoming edge and the outgoing edge, and returns a new EdgeData that replaces both edges
     */
    static removeNode(graph, node, edgeMapper) {
        // Get edges of node
        const edges = node.connectedEdges();

        const incomingEdges = edges.filter((edge) => edge.target().equals(node));
        const outgoingEdges = edges.filter((edge) => edge.source().equals(node));

        for (const incoming of incomingEdges) {
            for (const outgoing of outgoingEdges) {
                const newEdgeData = edgeMapper(incoming, outgoing);
                Graphs.addEdge(
                    graph,
                    incoming.source(),
                    outgoing.target(),
                    newEdgeData
                );
            }
        }

        // Remove node
        node.remove();
    }
}
