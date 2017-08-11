//
// Set Object
//

// Initialize		: var s = new Set();
// Add element		: s.add("foo");
// Remove element	: s.remove("foo");
// Check element	: "foo" in s
function Set() {
	this.content = {};
    for (var i = 0; i < arguments.length; i++) {
        this.content[arguments[i]] = true;
    }
}


Set.prototype.add = function(val) {
	this.content[val]=true;
	return val; /* For compatibility with ECMAScript 6 Set */
}

Set.prototype.remove = function(val) {
	delete this.content[val];
}

/**
 * Equivalent to 'remove', with return tweak for compatibility with ECMAScript 6 Set.
 */
Set.prototype.delete = function(val) {
	var hasValue = this.has(str);
	delete this.content[val];
	return hasValue;
}

Set.prototype.contains = function(val) {
	return (val in this.content);
}

/**
 * Equivalent to 'contains', for compatibility with ECMAScript 6 Set.
 */
Set.prototype.has = function(val) {
	return this.contains(val);
}

Set.prototype.asArray = function() {
	var res = [];
	for (var val in this.content) {
		res.push(val);
	}

	return res;
}

/**
 * Equivalent to 'asArray', for compatibility with ECMAScript 6 Set.
 */
Set.prototype.values = function() {
	return this.asArray();
}
	

Set.prototype.size = function() {
	var res = 0;
	for (var p in this.content) {
		
		if(this.content.hasOwnProperty(p)) {
			res++;
		}
	}
	return res;
}
Set.prototype.print = function() {

	for (var p in this.content) {
		
		if(this.content.hasOwnProperty(p)) {
			println(p);
		}
	}
}

