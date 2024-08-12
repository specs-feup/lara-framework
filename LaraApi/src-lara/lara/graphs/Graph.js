import Graphs from "./Graphs.js";
export default class Graph {
    /**
     * A Cytoscape graph
     */
    graph;
    /**
     * Creates a new instance of the Graph class
     * @param graph - A Cytoscape graph
     */
    constructor(graph) {
        this.graph = graph;
    }
    toDot(dotFormatter) {
        return Graphs.toDot(this.graph, dotFormatter);
    }
}
//# sourceMappingURL=Graph.js.map