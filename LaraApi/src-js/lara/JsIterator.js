var myIterable = {};
// Apparently, function* is not supported
myIterable[Symbol.iterator] = function* () {
    yield 1;
    yield 2;
    yield 3;
};

var _JsIterator = function(laraIterator) {
	this.laraIterator = laraIterator;
};

_JsIterator.prototype.next = function() {
	if(this.laraIterator.hasNext()) {
		return {value: this.laraIterator.next(), done: false}; 
	}
		
	return {done: true};
}