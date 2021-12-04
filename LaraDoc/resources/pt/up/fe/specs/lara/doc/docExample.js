/**
 * A top-level class
 */
 class ExampleClass {
 
 	/**
 	 * The ExampleClass constructor.
 	 *
	 * @param {string} stringParam - A string parameter
	 * @param {number} [optionalNumberParam=0] - An optional
	 */
 	constructor(stringParam) {
 		this.stringParam = stringParam;
 		this.optionalNumberParam = optionalNumberParam !== undefined ? optionalNumberParam : 0;
 	}
 	
 	/**
 	 * An instance method of example class
 	 *
 	 * @param {number} param1 
	 * @param {string} param2
 	 */
 	instanceMethod(param1, param2) {
 	}
 	
 	instanceMethodWithoutComments(param1, param2, param3) {
 	}
 	
 	/**
 	 * A static property
 	 */
 	//static staticProperty = 'someValue';
    
    /**
     * A static method of example class
     */
    static staticMethod() {
    	
  	}
 }
 
 /**
  * Example of derived class
  */
 class ExampleDerivedClass extends ExampleClass {
 
 	constructor(stringParam, anotherParam) {
 		super(stringParam);
 		
 		this.anotherParam = anotherParam;
 	}
 }
 
 /**
  * A top-level function
  */
 function exampleFunction(param1, param2) {
 }