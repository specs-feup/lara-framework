/**
 * Utility methods related to execution of scripts in a LARA environment.
 */
class Script {
	
	static #scriptOutput = {};
	
	
	/**
	 * Sets the output of the script, which will be written to disk when using the flag -r
	 * @param {any}
	 */
	static setOutput(value) {
		Script.#scriptOutput = value;
	}
	

	/**
	 * Reads the value set by Script.setOutput()
	 *
	 * @return {any} - the value set by Script.setOutput()
 	 */	
	static getOutput() {
		return Script.#scriptOutput;
	}
}