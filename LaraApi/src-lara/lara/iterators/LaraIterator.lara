/**
 * Base class for iterators in Lara.
 *
 * @class
 */
var LaraIterator = function() {

};

/*** METHODS TO IMPLEMENT ***/

/**
 * @returns {string} the type of this iterator.
 */
LaraIterator.prototype.getType = function() {
	notImplemented("LaraIterator.getType");
}

/**
 * @returns the next element.
 */
LaraIterator.prototype.next = function() {
	notImplemented("LaraIterator.next");
}

/**
 * @returns {boolean} true if it has another element to return.
 */
LaraIterator.prototype.hasNext = function() {
	notImplemented("LaraIterator.hasNext");
}

/**
 * Resets the iterator.
 */
LaraIterator.prototype.reset = function() {
	notImplemented("LaraIterator.reset");
}

/**
 * @returns {number} the total number of elements of the iterator, or undefined if it is not possible to calculate.
 */
LaraIterator.prototype.getNumElements = function() {
	notImplemented("LaraIterator.getNumElements");
}

/**
 * @returns The number of values returned by a call to next(). A value of one means one value, a value greater than one means an array with that amount of values.
 */
LaraIterator.prototype.getNumValuesPerElement = function() {
	notImplemented("LaraIterator.getNumValuesPerElement");
}

/*** IMPLEMENTED FUNCTIONS ***/

LaraIterator.prototype.jsIterator = function() {
	return new _JsIterator(this);
	
	/*
	var iterator = {};
	iterator.next = _jsNext(this);
	return iterator;
	*/
	//jsIterator.laraIterator = this;
	/*
	iterator.next = function() {
		if(this.hasNext()) {
			return {value: this.next(), done: false}; 
		}
		
		return {done: true};
	}
	
	*/
}



var _JsIterator = function(laraIterator) {
	this.laraIterator = laraIterator;
};

_JsIterator.prototype.next = function() {
	if(this.laraIterator.hasNext()) {
		return {value: this.laraIterator.next(), done: false}; 
	}
		
	return {done: true};
}

/*
//LaraIterator.prototype._jsNext = function() {
function _jsNext(laraIterator) {
	if(laraIterator.hasNext()) {
		return {value: laraIterator.next(), done: false}; 
	}
		
	return {done: true};
}
*/