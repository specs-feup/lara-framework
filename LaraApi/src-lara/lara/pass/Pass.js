laraImport('weaver.Query');

/**
 * Represents a Lara transformation pass.
 *
 * Need to implement:
 *  - _apply_impl($jp)
 */
class Pass {
	
	_name;
	
	constructor(name) {
		this._name = name;
	}
	
	/*
	getName() {
		return this.name;
	}
	*/
	
	get name() {
		return this._name;
	}

	set name(name) {
		this._name = name;
	}
	
	
	/**
	 * Applies this pass starting at the given join point. If no join point is given, uses the root join point.
	 * 
	 * @param {$jp} $jp - The point in the code where the pass will be applied.
	 */
	apply($jp) {
		let $processedJp = $jp !== undefined ?  $jp : Query.root();
		debug(() => "Applying pass '"+this.name+"' to " + $processedJp.joinPointType + " ("+ $processedJp.location + ")");
		this._apply_impl($processedJp);
	}
	
	_apply_impl($jp) {
		throw new Error("Method '_apply_impl' of pass " + this.name + "not implemented");
	}
	
}