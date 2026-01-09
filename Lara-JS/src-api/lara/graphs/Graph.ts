import cytoscape from "cytoscape";
import DotFormatter from "./DotFormatter.js";
import Graphs from "./Graphs.js";

export default class Graph {
  /**
   * A Cytoscape graph
   */
  graph: cytoscape.Core;

  /**
   * Creates a new instance of the Graph class
   * @param graph - A Cytoscape graph
   */
  constructor(graph: cytoscape.Core) {
    this.graph = graph;
  }

  toDot(dotFormatter: DotFormatter) {
    return Graphs.toDot(this.graph, dotFormatter);
  }
}
