laraImport("lara.graphs.Graphs");

class Graph {
	/**
	 * A Cytoscape graph
	 */
	graph;

	/**
	 * Creates a new instance of the Graph class
	 * @param graph a Cytoscape graph
	 */
	constructor(graph) {
		this.graph = graph;
	}

	/**
	 * Sets the Cytoscape graph
	 */
	set graph(graph) {
		this.graph = graph;
	}

	/**
	 * @returns the Cytoscape graph
	 */
	get graph() {
		return this.graph;
	}

	/**
	 * @returns the Cytoscape instance
	 */
	getCy() {
		return this.graph.cy();
	}

	/**
	 * @param {lara.graphs.DotFormatter} dotFormatter 
	 */
	toDot(dotFormatter) {
		return Graphs.toDot(this.graph, dotFormatter);
	}
}