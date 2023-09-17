import lara.mutation.Mutation;

/**
 * Iterative mutator, allows to perform one mutation at a time, and restore the code before each mutation.
 * 
 * @constructor
 * @param {lara.mutation.Mutation...} mutations
 *
 */
var IterativeMutator = function() {
	this.mutations = arrayFromArgs(arguments);	
	
	if(this.mutations.length === 0) {
		throw "IterativeMutator needs at least one mutation";
	}
	
	// TODO: Check if elements are mutations

	// True if the current code is mutated, false otherwise
	this.isMutated = false;

	// If true, before each call to .mutate() will check if the code is already mutated, 
	// and call restore before the mutation is applied
	this.automaticRestore = true;
	
	this.joinPoints = [];
	//this.currentMutationPoint = 0;
	
	this.mutantIterator = undefined;
	this.currentOriginalPoint = undefined;
	this.currentMutatedPoint = undefined;
	
	this.hasFinished = false;
};



/**
 * Introduces a single mutation to the code. 
 * If the code has been mutated already, restores the code before mutating again. 
 * If there are no mutations left, does nothing.
 *
 * @return true if a mutation occurred, false otherwise
 */
IterativeMutator.prototype.mutateSource = function() {
	// If no mutations left, do nothing
	if(this.hasFinished) {
		return false;
	}
	
	// If no iterator, create it
	if(this.mutantIterator === undefined) {
		this.mutantIterator = this._generator();
	}


	// Get next element
	var element = this.mutantIterator.next();

	if(!element.done) {
		return true;
	}
	
	this.hasFinished = true;
	return false;
}


/**
 *  If the code has been mutated, restores the code to its original state. If not, does nothing.
 */
IterativeMutator.prototype.restoreSource = function() {
	// If not mutated, return 
	if(!this.isMutated) {
		return;
	}


	if(this.currentOriginalPoint === undefined) {
		throw "Original point is undefined";
	}
	
	if(this.currentMutatedPoint === undefined) {
		throw "Mutated point is undefined";
	}	

	this.isMutated = false;
	this.currentMutatedPoint.insertReplace(this.currentOriginalPoint);
	
	// Unset mutated point
	this.currentMutatedPoint = undefined;

}


/**
 * @return {$jp} the point in the code where the mutation is occurring, or undefined if there are not more mutations left.
 */
IterativeMutator.prototype.getMutationPoint = function() {
	return this.currentOriginalPoint;
} 

/**
 * @return {$jp} the point with currently mutated code, or undefined if the code is not currently mutated.
 */
IterativeMutator.prototype.getMutatedPoint = function() {
	return this.currentMutatedPoint;
} 

IterativeMutator.prototype._generator = function*() {

	if(this.isMutated) {
		throw "Code is currently mutated, cannot create a generator";
	}


	// Iterate over all join points
	for(var $jp of this.joinPoints) {
	
		
		this.currentOriginalPoint = $jp;
	
		// Iterate over all mutations
		for(var mutation of this.mutations) {

			// If current mutation does not mutate the point, skip
			if(!mutation.isMutationPoint($jp)) {
				continue;
			}

			// Found a mutation point, iterate over all mutations
			for(var mutationResult of mutation.mutate($jp)) {

				var $mutatedJp = mutationResult.getMutation();
				
				// Mutate code and return
				this.isMutated = true;
				this.currentMutatedPoint = $mutatedJp;
				$jp.insertReplace($mutatedJp);
				//yield this.currentMutatedPoint;
				yield mutationResult;
			
				// After resuming, restore
				this.restoreSource();
			}		
		}
	
	}

	// Mark as finished
	this.hasFinished = true;
	
	// Unset current original point
	this.currentOriginalPoint = undefined;
}


/**
 * @param {joinpoint...} 
 */
IterativeMutator.prototype.addJps = function() {
	var jps = arrayFromArgs(arguments);	
	for(var $jp of jps) {
		this.joinPoints.push($jp);
	}
}

/**
 * @return {String} the name of this mutator
 */
IterativeMutator.prototype.getName = function() {
	return "IterativeMutator";
}


