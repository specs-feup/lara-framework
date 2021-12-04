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
 	
 	derivedFoo() {
 	}
 }
 
 /**
  * A top-level function
  * 
  * @param {bool} param1 - example param1
  * @param {bool} param2 - example param2  
  */
 function exampleFunction(param1, param2) {
 }
 
 /**
  * A top-level global variable
  */
 var EXAMPLE_GLOBAL = 10;
 
 /**
  * Top-level decl example
  */
 var EXAMPLE_GLOBAL_DECL1, EXAMPLE_GLOBAL_DECL2;
 
 /**
  * A top-level class being declared in a legacy way
  *
  * @class
  */
  var ExampleLegacyClass = function() {
  	this.a = 0;
  }

 /**
  * A top-level class being declared in a legacy way but without 'class' tag
  *
  */
  var ExampleLegacyClassWithoutTag = function() {
  	this.a = 0;
  }

/**
 * A top-level variable that declares a function
 */ 
 var exampleVarThatIsFunction = function(param1, param2, param3) {
	return param1+param2+param3;
}
  
/** 
 * A top-level instance method being declared in a legacy way
 */
ExampleLegacyClass.prototype.legacyInstanceMethod = function () {
	return this.a;
}


/**
 * A legacy static class.
 * @class
 */
var ExampleLegacyStaticClass = {};

/** 
 * A top-level static method being declared in a legacy way
 */
ExampleLegacyStaticClass.legacyStaticMethod = function () {
	return 0;
}

ExampleLegacyStaticClass.LEGACY_STATIC_CLASS_VAR = 0