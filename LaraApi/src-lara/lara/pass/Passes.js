laraImport("lara.pass.Pass");
laraImport("lara.pass.PassApplyArg");

class Passes {
	
	static apply($jp) {
		// Ensure it is an array
		const passesArray = arrayFromArgs(arguments, 1);

		const results = [];

		for(let i=0; i<passesArray.length; i++) {
			const passElement = passesArray[i];
			const argType = Passes.getArgType(passElement);

			if(argType === undefined) {
				throw new Error("Parameter #" + (i+1) + " is not valid: " + passElement);
			}

			// If an instance of a Pass, just call apply over the given $jp
			if(argType === PassApplyArg.PASS_INSTANCE) {
				results.push(passElement.apply($jp));
				continue;
			}

			// Now it is either a function or a Pass class, either can receive arguments
			// Check if next element is an array (of arguments) or an object
			// Use empty array as default arguments value
			let args = [];
			let isArray = true;
			if(i<(passesArray.length-1)) {
				const nextElem = passesArray[i+1];
				const nextElemType = Passes.getArgType(nextElem);

				if(nextElemType.isArg) {
					// Skip arguments
					i++;					
					args = nextElem;

					if(nextElemType === PassApplyArg.ARRAY_ARG) {
						isArray = true;
					} else if(nextElemType === PassApplyArg.OBJECT_ARG) {
						isArray = false;
					} else {
						throw new Error("Not implemented yet for apply argument of type " + argType)
					}
				}
			}

			// Both cases will return true if tested for function, first test in class of Pass
			if(argType === PassApplyArg.PASS_CLASS) {
				const passInstance = isArray ? new passElement(...args) : new passElement(args);
				results.push(passInstance.apply($jp));
				continue;
			}
			
			// Finally, test if function
			if(argType === PassApplyArg.FUNCTION) {
				if(isArray) {
					results.push(passElement($jp, ...args));					
				} else {
					results.push(passElement($jp, args));										
				}

				continue;				
			}
			
			throw new Error("Not implemented yet for apply argument of type " + argType);
		}
		
		return results;
	}
	
	static getArgType(applyArg) {

			// If an instance of a Pass, just call apply over the given $jp
			if(applyArg instanceof Pass) {
				return PassApplyArg.PASS_INSTANCE;
			}
	
			// Check if it is an array argument		
			if(Array.isArray(applyArg)) {
				return PassApplyArg.ARRAY_ARG;
			}

			// Both Pass class and function will return true if tested for function, first test in class of Pass			
			if(applyArg.prototype instanceof Pass) {
				return PassApplyArg.PASS_CLASS;
			}
			
			if(applyArg instanceof Function) {
				return PassApplyArg.FUNCTION;
			}
			
			// Finally, test if object
			if((typeof applyArg) === 'object' && (applyArg !== null)) {
				return PassApplyArg.OBJECT_ARG;
			}
			
			return undefined;
	}
			
	
}