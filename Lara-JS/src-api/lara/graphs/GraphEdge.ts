laraImport("lara.graphs.EdgeData");

/**
 * Base class for graph edges.
 */
class GraphEdge extends EdgeData {
		
	constructor(id, source, target) {
		super(id, source, target);
		
		println("!!! Class lara.graphs.GraphEdge is deprecated, please use lara.graphs.EdgeData instead");
	}	
	
}
