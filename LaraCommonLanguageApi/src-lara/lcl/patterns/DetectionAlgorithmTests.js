laraImport("lcl.LaraCommonLanguage");
laraImport("weaver.Query");

class DetectionAlgorithmTests {

	constructor() {
		
	}
	
	
	static testClassJp() {
	
		for(var $class of Query.search("class")) {
			println();
			println("Class " + $class.name);
	
			// 'superClasses' and 'allSuperClasses' properties
			// 
			var superClasses = $class.superClasses;
			if (superClasses.length != 0)    println("    (sp) " + superClasses.map(c => c.name));
			
			var allSuperClasses = $class.allSuperClasses;
			if (allSuperClasses.length != 0) println("(all sp) " + allSuperClasses.map(c => c.name));
			
			// 'interface' and 'allInterfaces' properties
			// 
			var interfaces = $class.interfaces;
			if (interfaces.length != 0) println("     (i) " + interfaces.map(i => i.name));
			
			var allInterfaces = $class.allInterfaces;
			if (allInterfaces.length != 0) println(" (all i) " + allInterfaces.map(i => i.name));
			
			// 'methods', 'allMethods', 'listOfAllMethods' properties
			// 
			var methods = $class.methods;
			if (methods.length != 0)    println("     (m) " + methods.map(c => c.name));
			
			var allMethods = $class.allMethods;
			if (allMethods.length != 0) println(" (all m) " + allMethods.map(c => c.name));
			
			var listOfAllMethods = $class.listOfAllMethods;
			if (listOfAllMethods.length != 0) println("(list m) " + listOfAllMethods.map(c => c.name));
			
		}
	}
	
	static testInterfaceJp() {
	
		for(var $interface of Query.search("interface")) {
			println();
			println("Interface " + $interface.name);
	
			// 'interface' and 'allInterfaces' properties
			// 
			var interfaces = $interface.interfaces;
			if (interfaces.length != 0) {
				println("    (i) - " + interfaces.map(i => i.name));
			}
			var allInterfaces = $interface.allInterfaces;
			if (allInterfaces.length != 0) {
				println("(all i) - " + allInterfaces.map(i => i.name));
			}
	
			// 'method' and 'allMethods' properties
			// 
			var methods = $interface.methods;
			if (methods.length != 0) {
				println("    (m) - " + methods.map(m => m.name));
			}
			var allMethods = $interface.allMethods;
			if (allMethods.length != 0) {
				println("(all m) - " + allMethods.map(m => m.name));
			}
		}
	}
	
	static RelationCalls() {
		
		// uma classe chama um método de outra classe
	
		for(var $class of Query.search("class")) {
			println();
			println("Class " + $class.name);
	
			var $calls = Query.searchFrom($class, "call");
			
			for(var $call of $calls) {
	
				// filter out constructor calls
				if ($call.instanceOf("constructor")) continue;
				if ($call.method.isStatic) continue;
				
				// !! returns null, when method is from interface
				
				println("Class '" + $class.name +
				"' calls '" + $call.method.name + 
				"' of '" + $call.method.class.name +
				"'");
			}
		}
	}
	
	static RelationCreates() {
	
		// uma classe cria um objeto de outra classe
	
		for(var $class of Query.search("class")) {
			// println();
			// println("Class " + $class.name);
	
			var $constructorCalls = Query.searchFrom($class, "constructorCall");
	
			if ($constructorCalls.length == 0) continue;
			
			println();
			
			for(var $constructorCall of $constructorCalls) {
				// println($constructorCall);
				// println($constructorCall.method.name);
				
				println("Class '" + $class.name + "' creates '" + $constructorCall.method.class.name + "'");
			}
		}
	}
	
	static RelationHas() {
	
		// uma classe tem 1+ objectos de outra classe
	
		// !! falta saber class type
		// 
		for(var $class of Query.search("class")) {
			println();
			println("Class " + $class.name + " (" + $class.fields.map(m => m.name) + ")");
	
			for (var $field of $class.fields) {
				// print("Field (name) " + $field.name);
				// println("Field " + $field.class.name);
	
				// !! if is interface, 'isClass' returns false
				// !! if string, 'isClass' returns true
				// !! if object, 'isClass' returns true
	
				var classType = $field.type.classType;
				if (classType == null) continue;
				
				println("Class '" + $class.name + "' has '" + classType.name + "'");
			}
		}
	}
	
	static RelationInherit() {
	
		for(var $class of Query.search("class")) {
	
			var superClasses = $class.allSuperClasses;
			var interfaces = $class.allInterfaces;
	
			var names = superClasses.map(c => "(c)" + c.name)
				.concat(interfaces.map(i => "(i)" + i.name));
	
			if (names.length == 0) continue;
				
			// println();
			// println("Class " + $class.name + " => (" + names + ")");
			println("Class '" + $class.name + "' inherits from '" + names + "'");
		}
	}
	
	static RelationReferences() {
	
		// um método de uma classe tem como parâmetros um objeto de outra classe
	
		// constructors default nao sao incluidos (?)
		for(var $class of Query.search("classType")) {
			println();
			println("Class " + $class.name + " => " + $class.allMethods.map(m => m.name));
	
			// (check) !! includes constructors
			// (check) !! 'params' is not implemented (on kadabra ?)
			// (check) !! includes static method, how to filter
			// 
			for(var $method of $class.allMethods) {
				if ($method.instanceOf("constructor")) continue;
				if ($method.isStatic) continue;
				
				// println("Method " + $method.name + " " + $method.params.length + " (" + $method.params.map(p => p.name) + ")");
	
				for (var $param of $method.params) {
	
					var classType = $param.type.classType;
					if (classType == null) continue;
					
					println("Class '" + $class.name + "' references '" + classType.name + "'");
				}
			}
		}
	}
	
	static RelationUses() {
	
		// um método de uma classe retorna um objeto de outra classe
	
		//
		// !! type of return, get ClassType
		for(var $class of Query.search("classType")) {
			println();
			println("Class " + $class.name + " => " + $class.allMethods.map(m => m.name));
	
			// 
			for(var $method of $class.allMethods) {
	
				// filter constructors, and static
				if ($method.instanceOf("constructor")) continue;
				if ($method.isStatic) continue;
				
				var classType = $method.returnType.classType;
				if (classType == null) continue;
				
				println("Class '" + $class.name + "' uses '" + classType.name + "'");
			}
		}
	}
}
