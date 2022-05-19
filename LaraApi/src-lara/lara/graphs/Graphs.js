laraImport("lara.graphs.NodeData");
laraImport("lara.graphs.EdgeData");

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
		if(Graphs.#isLibLoaded) {
			return;
		}
		
		load("https://cdnjs.cloudflare.com/ajax/libs/cytoscape/3.21.1/cytoscape.min.js");
		Graphs.#isLibLoaded = true;
	}
	
	static addNode(graph, nodeData) {
		let _nodeData = nodeData ?? {};
		
		// Check if NodeData
		if(!(_nodeData instanceof NodeData)) {
			_nodeData = Object.assign(new NodeData(), _nodeData);
		}
		
		return graph.add({ group: 'nodes', data: _nodeData});
	}
	
	static addEdge(graph, sourceNode, targetNode, edgeData) {
		let _edgeData = edgeData ?? {};

		// Check if EdgeData
		if(!(_edgeData instanceof EdgeData)) {
			_edgeData = Object.assign(new EdgeData(), _edgeData);
		}
				
		_edgeData.source = sourceNode.id();
		_edgeData.target = targetNode.id();
				
		return graph.add({ group: 'edges', data: _edgeData});
	}
	
	static toDot(graph) {
		var dot = "digraph test {\n";
		
		// Declare nodes
		for(const node of graph.nodes()) {
			//println("Node: ")
			//printlnObject(node.data());
			dot += '"' + node.id() + '" [label="'+Graphs._sanitizeDotLabel(node.data().toString())+'" shape=box];\n'
			//println("Id: " + node.id)
		}
		
		for(const edge of graph.edges()) {
			dot += '"' + edge.data().source + '" -> "' + edge.data().target + '" [label="'+Graphs._sanitizeDotLabel(edge.data().toString())+'"];\n'
			//println("Edge: ")
			//printlnObject(edge.data());
			//println("Source: " + edge.data().source);
			//println("Target: " + edge.data().target);
		}
		
		dot += "}\n";
		
		return dot;
	}
	
	static _sanitizeDotLabel(label) {
		return label.replaceAll("\n", "\\l")
					.replaceAll("\r", "");
	}
}