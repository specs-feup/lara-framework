class Graphs {
	
	static #isLibLoaded = false;
	
	static loadLibrary() {
		if(Graphs.#isLibLoaded) {
			return;
		}
		
		load("https://cdnjs.cloudflare.com/ajax/libs/cytoscape/3.21.1/cytoscape.min.js");
		Graphs.#isLibLoaded = true;
	}
	
	static addNode(graph, nodeData) {
		return graph.add({ group: 'nodes', data: nodeData});
	}
	
	static toDot(graph) {
		var dot = "digraph test {\n";
		
		// Declare nodes
		for(const node of graph.nodes()) {
			//println("Node: ")
			//printlnObject(node.data());
			dot += '"' + node.id() + '" [label="'+node.data().toString()+'" shape=box];\n'
			//println("Id: " + node.id)
		}
		
		for(const edge of graph.edges()) {
			println("Edge: ")
			printlnObject(edge.data());
			println("Source: " + edge.source());
			println("Target: " + edge.target());
		}
		
		dot += "}\n";
		
		return dot;
	}
}