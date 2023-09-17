import lara.mutation.Mutation;

/**
 * Abstract mutation class that implements .getMutants(), but makes function Mutation.mutate($jp) abstract. Allows more efficient, generator-based Mutation implementations.
 * 
 * This should not be instantiated directly, instead should be extended.
 * 
 * @constructor
 *
 * @param {String} [name="<unnamed mutation>"] - the name of the mutation
 *
 */
var IterativeMutation = function(name) {
	// Parent constructor
    Mutation.call(this, name);
};

// Inheritance
IterativeMutation.prototype = Object.create(Mutation.prototype);


/*** IMPLEMENTATION ***/

IterativeMutation.prototype.getMutants = function($jp) {
	var mutations = [];

	for(var mutation of this.mutate($jp)) {
		mutations.push(mutation);	
	}
	
	return mutations;
}


/*** TO IMPLEMENT ***/


/**
 * Iterative implementation of the function.
 */
IterativeMutation.prototype.mutate = function* ($jp) {
	notImplemented("IterativeMutation.mutate*");
}



