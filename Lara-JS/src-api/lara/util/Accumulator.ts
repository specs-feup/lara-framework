import StringSet from "./StringSet.js"
import{
    checkTrue,
    toArray,
} from "../core/LaraCore.js"

/**
 * Counts occurrences of tuples.
 */

export default class Accumulator {

    value: number;
    accs: {[key: string]: Accumulator};
    seenKeys: StringSet;

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

	add(...args: any ) {
		const chainArray: Array<string> = this._parseArguments(arguments);
	
		let currentAcc:Accumulator = this;
	
		// Travel chain of values
		for(const chainElement of chainArray) {
			let nextAcc:Accumulator|undefined = currentAcc.accs[chainElement];
			
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
		const previousValue:number = currentAcc.value;
	
		// Increment acc value
		currentAcc.value++;
		
		return previousValue;
	}
	
	/**
	* Adds the value associated to the given tuple. If no value is defined for the given tuple, returns 0.
	* <p>
	* Alternatively, can also receive an array with the chain of values
	*/
	get(...args: any) {
		const chainArray: Array<string> = this._parseArguments(arguments);
		
		let currentAcc: Accumulator = this;
		
		// Travel chain of values
		for(const chainElement of chainArray) {
			const nextAcc: Accumulator = currentAcc.accs[chainElement];
			
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
	
	
	copy(...args: any) {
	
		let copy:Accumulator = new Accumulator();
	
		for(const key of this.keys()) {
			const value: number = this.get(key);
	
			// TODO: Not efficient, should have a method to internally set a value   ---OLD---
			for(let i = 0; i<value; i++) {
				copy.add(key);
			}
		}
	
		return copy;
	}
	
	/**
	* Returns an array of arrays with keys that have a value set.
	*/
	keys() {
		const chains:Array<Array<string>> = [];
		const currentChain:Array<string> = [];
	
		this._keysPrivate(currentChain, chains);
		return chains;
	}
	
	// TODO: Use # for private functions? ---OLD---
	_keysPrivate(currentChain:Array<string>, chains: Array<Array<string>>) {
	
		// If this accumulator has a value, add current chain
		if(this.value > 0) {
			chains.push(currentChain);
		}
		
	
		for(const key of this.seenKeys.values()) {
			const updatedChain = currentChain.concat(key);
			
			let nextAcc = this.accs[key];
			
			if(nextAcc === undefined) {
				continue;
			}
			nextAcc._keysPrivate(updatedChain, chains);
		}
		
	}
	
	
	/**
	* Receives an array with the arguments of the previous function.
	*/
        _parseArguments(...args: any ) {
            checkTrue(args.length === 1, "Accumulator._parseArguments: Expected arguments to have length 1");
        
            let functionArguments = arguments[0];
        
            // If one argument and array, return it
            if((functionArguments.length === 1) && (functionArguments[0].constructor === Array)) {
                // Retrive the array in the previous arguments
                return functionArguments[0];
            }
            
            // Transform arguments into array
            return toArray(functionArguments);
        }
}
