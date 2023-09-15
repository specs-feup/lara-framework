/**
 * Base class for data of graph edges.
 */
class EdgeData {
	
	id;
	source;
	target;
	
	constructor(id, source, target) {
		this.id = id;
		this.source = source;	
		this.target = target;			
	}	
	
}
