import lara.Check;
import lara.mutation.MutationResult;

/**
 * Base class that represents a code mutation.
 * 
 * This should not be instantiated directly, instead it should be extended.
 * 
 * @constructor
 *
 * @param {String} [name="<unnamed mutation>"] - the name of the mutation
 *
 */
var Mutation = function(name) {
	Check.isString(name);
	
	this.name = name;
};


/**
 * @return {String} the name of this mutation
 */
Mutation.prototype.getName = function() {
	return this.name;
}


/**
 * Generator function for the mutations.
 *
 * @param {$jp} $jp - The point in the code to mutate.
 *
 * @return {lara.mutation.MutationResult} an iterator that results the results of each mutation on each iteration.
 */
Mutation.prototype.mutate = function* ($jp) {
	var mutants = this.mutation.getMutants($jp);
	for(var $mutant of mutants) {
		yield $mutant;
	}
}


/*** TO IMPLEMENT ***/

/**
 * @param {$jp} $jp - A point in the code to test
 * 
 * @return {boolean} true if the given join point is a valid mutation point, false otherwise
 */
Mutation.prototype.isMutationPoint = function($jp) {
	notImplemented("Mutation.isMutationPoint");
}

/**
 * @param {$jp} $jp - The point in the code to mutate
 * 
 * @return {lara.mutation.MutationResult[]} an array with the results of each mutation, which must be out-of-tree copies of the given join point
 */
Mutation.prototype.getMutants = function($jp) {
	notImplemented("Mutation.getMutants");
}




