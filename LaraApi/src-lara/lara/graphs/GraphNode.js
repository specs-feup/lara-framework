laraImport("lara.graphs.NodeData");

/**
 * Base class for graph nodes.
 *
 * @deprecated use lara.graphs.NodeData instead
 */
class GraphNode extends NodeData {
		
	constructor(id, parent) {
		super(id, parent);

		println("!!! Class lara.graphs.GraphNode is deprecated, please use lara.graphs.NodeData instead");
	}
	
}
