/**
 * Creates ids for arbitrary tuples.
 */
var TupleId = function() {
	this.tuples = TupleId._newTuples();
	this.currentId = 0;
};

TupleId._IS_TUPLE = "!is_tuple";
TupleId._ID = "!tuple_id";

/**
 * @return {Number} an unique id associated with the given tuple
 */
TupleId.prototype.getId = function() {
	// Get an array from the arguments
	var tuple = arrayFromArgs(arguments);

	var currentTuples = this.tuples;
	
	// Iterate over all tuple elements except the last one
	for(var element of tuple) {
		currentTuples = TupleId._nextTuples(currentTuples, element);
	}
	
	// Get id from last tuples
	var id = currentTuples[TupleId._ID];
	if(id === undefined) {
		id = this._nextId();
		currentTuples[TupleId._ID] = id;
	}
	
	return id;
}

/**
 * @return An array where each element is an array which contains a tuple and the corresponding id 
 */ 
TupleId.prototype.getTuples = function() {
	var tuples = {};
	
	TupleId._getTuplesPrivate(this.tuples, [], tuples);
	
	return tuples;
}

/*** PRIVATE FUNCTIONS ***/

TupleId._getTuplesPrivate = function(currentTuples, prefix, tuples) {

	// If currentTuple has an id, add it
	var currentId = currentTuples[TupleId._ID];
	if(currentId !== undefined) {
		//tuples.push(prefix.concat(currentId));
		tuples[currentId] = prefix;
	}
	
	// Iterate over all the indexed tuples
	for(var key in currentTuples) {
		var childTuples = currentTuples[key];
		if(!TupleId._isTuples(childTuples)) {
			continue;
		}

		// Call recursively, building the prefix
		TupleId._getTuplesPrivate(childTuples, prefix.concat(key), tuples);
	}
}

TupleId._isTuples = function(tuples) {
	return tuples[TupleId._IS_TUPLE] === true;
}

/**
 * Creates a new tuples object.
 */
TupleId._newTuples = function() {
	var newTuples = {};
	// This is necessary due to already existing properties such as 'hasOwnProperty'
	newTuples[TupleId._IS_TUPLE] = true;
	
	return newTuples;
}

/**
 * @param {tuples} tuples should always be a valid tuples object, created with _newTuples()
 * @param {Object} element the key for the next tuples object
 * @returns the next tuples object, creating a new tuples if necessary. 
 */
TupleId._nextTuples = function(tuples, element) {
	// Get next tuples
	var nextTuple = tuples[element];
	
	// If undefined, or not a tuples object, create a new one and add it
	if(nextTuple === undefined || !TupleId._isTuples(nextTuple)) {
		nextTuple = TupleId._newTuples();
		tuples[element] = nextTuple;
	}
	
	return nextTuple;
}

/**
 * @returns the next id
 */
TupleId.prototype._nextId = function() {
	var id = this.currentId ;
	this.currentId++;
	return id;
}