//import lara.util.StringSet;

/**
 * Counts occurrences of tuples.
 */
class Accumulator {

	constructor() {
		this.value = 0;
		this.accs = {};
		this.seenKeys = new StringSet();
	};


	/**
	* Adds the tuple to the accumulator. Each time a tuple is added, the corresponding value increments by 1.
	*
	* Alternatively, can also receive an array with the chain of values
	*
	* @return {Integer} the previous count of the added element
	*/
	add() {
		var chainArray = this._parseArguments(arguments);
	
		var currentAcc = this;
	
		// Travel chain of values
		for(var chainElement of chainArray) {
			var nextAcc = currentAcc.accs[chainElement];
			
			// If no accumulator, create
			if(nextAcc === undefined) {
				nextAcc = new Accumulator();
				currentAcc.accs[chainElement] = nextAcc;
				currentAcc.seenKeys.add(chainElement);
			}
			
			// Update acc
			currentAcc = nextAcc;	
		}
	
		// Store previous value
		var previousValue = currentAcc.value;
	
		// Increment acc value
		currentAcc.value++;
		
		return previousValue;
	}
	
	/**
	* Adds the value associated to the given tuple. If no value is defined for the given tuple, returns 0.
	* <p>
	* Alternatively, can also receive an array with the chain of values
	*/
	get() {
		var chainArray = this._parseArguments(arguments);
		
		var currentAcc = this;
		
		// Travel chain of values
		for(var chainElement of chainArray) {
			var nextAcc = currentAcc.accs[chainElement];
			// If no accumulator, return 0
			if(nextAcc === undefined) {
				return 0;
			}
			
			// Update acc
			currentAcc = nextAcc;	
		}
		
		// Return acc value
		return currentAcc.value;
	}
	
	
	copy() {
	
		var copy = new Accumulator();
	
		for(var key of this.keys()) {
			var value = this.get(key);
	
			// TODO: Not efficient, should have a method to internally set a value
			for(var i = 0; i<value; i++) {
				copy.add(key);
			}
		}
	
		return copy;
	}
	
	/**
	* Returns an array of arrays with keys that have a value set.
	*/
	keys() {
		var chains = [];
		var currentChain = [];
	
		this._keysPrivate(currentChain, chains);
		return chains;
	}
	
	// TODO: Use # for private functions?
	_keysPrivate(currentChain, chains) {
	
		// If this accumulator has a value, add current chain
		if(this.value > 0) {
			chains.push(currentChain);
		}
		
		/*
		for(prop in this.accs) {
			if(!this.seenKeys.has(prop)) {
				continue;
			}
	
			var updatedChain = currentChain.concat(prop);
			
			var nextAcc = this.accs[prop];
	
			nextAcc._keysPrivate(updatedChain, chains);
		}
		*/
		
	
		for(var key of this.seenKeys.values()) {
			var updatedChain = currentChain.concat(key);
			
			var nextAcc = this.accs[key];
			
			if(nextAcc === undefined) {
				continue;
			}
			nextAcc._keysPrivate(updatedChain, chains);
		}
		
	}
	
	
	/**
	* Receives an array with the arguments of the previous function.
	*/
	_parseArguments() {
		checkTrue(arguments.length === 1, "Accumulator._parseArguments: Expected arguments to have length 1");
	
		var functionArguments = arguments[0];
	
		// If one argument and array, return it
		if((functionArguments.length === 1) && (functionArguments[0].constructor === Array)) {
			// Retrive the array in the previous arguments
			return functionArguments[0];
		}
		
		// Transform arguments into array
		return toArray(functionArguments);
	}

}

