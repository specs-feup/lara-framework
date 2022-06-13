laraImport("lara.pass.Pass");

class Passes {
	
	static apply($jp) {
		// Ensure it is an array
		const passesArray = arrayFromArgs(arguments, 1);

		const results = [];

		for(let i=0; i<passesArray.length; i++) {
			const passElement = passesArray[i];

			// If an instance of a Pass, just call apply over the given $jp
			if(passElement instanceof Pass) {
				results.push(passElement.apply($jp));
				continue;
			}

			// Now it is either a function or a Pass class, either can receive arguments
			// Check if next element is an array (of arguments)
			let args = [];
			if(i<(passesArray.length-1) && Array.isArray(passesArray[i+1])) {
				args = passesArray[i+1];
				// Skip arguments
				i++;
			}

			// Both cases will return true if tested for function, first test in class of Pass
			if(passElement.prototype instanceof Pass) {
				const passInstance = new passElement(...args);
				results.push(passInstance.apply($jp));
				continue;
			}
			
			// Finally, test if function
			if(passElement instanceof Function) {
				results.push(passElement($jp, ...args));
				continue;				
			}
			
			throw new Error("Parameter #" + (i+1) + " is not valid: " + passElement);
		}
		
		return results;
	}
	
}