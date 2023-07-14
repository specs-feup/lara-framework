laraImport("lara.graphs.Graphs");

class Graph {
	/**
	 * A Cytoscape graph
	 */
	#graph;

	/**
	 * Creates a new instance of the Graph class
	 * @param {Object} [config = {}] configuration for the graph, according to what Cytoscape accepts as configuration object
	 */
	constructor(config) {
		this.#graph = Graphs.newGraph(config);
	}

	/**
	 * Sets the Cytoscape graph
	 */
	set graph(graph) {
		this.#graph = graph;
	}

	/**
	 * @returns the Cytoscape graph
	 */
	get graph() {
		return this.#graph;
	}

	/**
	 * @returns the Cytoscape instance
	 */
	getCy() {
		return this.#graph.cy();
	}

	/**
	 * @param {lara.graphs.DotFormatter} dotFormatter 
	 */
	toDot(dotFormatter) {
		return Graphs.toDot(this.#graph, dotFormatter);
	}
}