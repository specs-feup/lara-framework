import DotFormatter from "./DotFormatter.js";
import EdgeData from "./EdgeData.js";
import NodeData from "./NodeData.js";

import cytoscape from "cytoscape";

/**
 * Utility class related with graph creation and manipulation.
 *
 * Current implementation uses Cytoscape.js (https://js.cytoscape.org/)
 */
export default class Graphs {
  /**
   * @param config - Configuration for the graph, according to what Cytoscape accepts as configuration object
   */
  static newGraph(config = {}) {
    return cytoscape(config);
  }

  /**
   * @deprecated Does nothing. Cytoscape should be loaded by the user.
   */
  static loadLibrary(): void {}

  static addNode(graph: cytoscape.Core, nodeData: NodeData | Record<string, unknown>) {
    if (!(nodeData instanceof NodeData)) {
      nodeData = Object.assign(new NodeData(), nodeData);
    }
    return graph.add({ group: "nodes", data: nodeData });
  }

  static addEdge(
    graph: cytoscape.Core,
    sourceNode: cytoscape.NodeSingular,
    targetNode: cytoscape.NodeSingular,
    edgeData: EdgeData | Record<string, unknown>
  ) {
    if (!(edgeData instanceof EdgeData)) {
      edgeData = Object.assign(new EdgeData(), edgeData);
    }
    
    edgeData.source = sourceNode.id();
    edgeData.target = targetNode.id();

    return graph.add({ group: "edges", data: edgeData });
  }

  static toDot(graph: cytoscape.Core, dotFormatter = new DotFormatter()) {
    let dot = "digraph test {\n";

    // Declare nodes
    for (const node of graph.nodes()) {
      dot += `"${node.id()}" [label="${dotFormatter.getNodeLabel(
        node
      )}" shape=box`;

      // Add node attributes
      const nodeAttrs = dotFormatter.getNodeAttributes(node);
      dot += nodeAttrs.length === 0 ? "" : " " + nodeAttrs;

      dot += "];\n";
    }

    for (const edge of graph.edges()) {
      const edgeData = edge.data() as EdgeData;

      dot += `"${edgeData.source}" -> "${
        edgeData.target
      }" [label="${dotFormatter.getEdgeLabel(edge)}"`;

      // Get edge attributes
      const edgeAttrs = dotFormatter.getEdgeAttributes(edge);
      dot += edgeAttrs.length === 0 ? "" : " " + edgeAttrs;

      dot += "];\n";
    }

    dot += "}\n";

    return dot;
  }

  /**
   * @returns True if the outdegree (number of edges with this node as source) is zero, false otherwise. By default, if a node has a connection to itself (loop) it is not considered a leaf
   */
  static isLeaf(node: cytoscape.NodeSingular, loopsAreLeafs = false) {
    const includeLoops = !loopsAreLeafs;
    return node.outdegree(includeLoops) === 0;
  }

  /**
   * Removes a node from the graph. Before removing the node, creates connections between all connecting sources and targets.
   *
   * @param graph -
   * @param node -
   * @param edgeMap - function that receives the incoming edge and the outgoing edge, and returns a new EdgeData that replaces both edges
   */
  static removeNode(
    graph: cytoscape.Core,
    node: cytoscape.NodeSingular,
    edgeMapper: (
      incoming: cytoscape.EdgeSingular,
      outgoing: cytoscape.EdgeSingular
    ) => EdgeData
  ) {
    // Get edges of node
    const edges = node.connectedEdges();

    const incomingEdges = edges.filter(
      (edge: cytoscape.EdgeSingular) => edge.target() === node
    );
    const outgoingEdges = edges.filter(
      (edge: cytoscape.EdgeSingular) => edge.source() === node
    );

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
