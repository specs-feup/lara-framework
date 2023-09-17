/**
 * Contains the results of a single mutation.
 */
var MutationResult = function($mutation) {
	this.$mutation = $mutation;
};


/**
 * @return {$jp} a copy of the original join point, where the mutation was applied.
 */
MutationResult.prototype.getMutation = function() {
	return this.$mutation;
}

