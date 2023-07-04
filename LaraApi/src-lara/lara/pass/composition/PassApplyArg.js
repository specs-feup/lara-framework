class PassApplyArg {
	
	static PASS_CLASS = new PassApplyArg("PASS_CLASS")
	static PASS_INSTANCE = new PassApplyArg("PASS_INSTANCE")
	static FUNCTION = new PassApplyArg("FUNCTION")
	static ARRAY_ARG = new PassApplyArg("ARRAY_ARG", true)
	static OBJECT_ARG = new PassApplyArg("OBJECT_ARG", true)		

	#name;
	
	#isArg;
	
	constructor(name, isArg) {
	    this.#name = name;
	    this.#isArg = isArg !== undefined ? isArg : false;
	}
	
	get name() {
		return this.#name;
	}

	get isArg() {
		return this.#isArg;
	}
	
	toString() {
		return this.name;
	}
	
}