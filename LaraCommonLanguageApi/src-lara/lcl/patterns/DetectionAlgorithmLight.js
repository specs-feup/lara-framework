laraImport("lcl.LaraCommonLanguage");
laraImport("weaver.Query");

class DetectionAlgorithmLight {

	constructor(members, connections) {
		this.members = members;
		this.connections = connections;
		this.detections = [];
		this.classTypesMap = new Map();
		
		this.dpCoreCompatibility = false;
	}
	
	setCompatibility(dpCoreCompatibility) {
		this.dpCoreCompatibility = dpCoreCompatibility;
	}
	
	detect() {
		this.detections = [];
		
		this.classTypesMap = new Map();
		
		var classTypes = Query.search("classType").get();
		
		for (var i = 0 ; i < classTypes.length ; i++) {
			var classType = classTypes[i];
	 	// for (const classType of classTypes) {
			// print("  - caching : " + i + "/" + classTypes.length + " ==> " + classType.name);
			// println(" (" + Array.from(classType.types()) + ")");
			var classTypeObject = new ClassTypeObject(classType);
			classTypeObject.setCompatibility(this.dpCoreCompatibility);
			classTypeObject.compute();
			this.classTypesMap.set(classType.name, classTypeObject);
		}
	
		this.recursive(classTypes, [], 0);
		
		this.detections = this.constructor.removeDuplicates(this.detections);
	
		return this.detections;
	}
	
	recursive(classTypes, candidates, depth) {
		
		if (depth < this.members.length) {
	
			for (var i = 0 ; i < classTypes.length ; i++) {
				
				// if (depth == 0) println("  - " + i + "/" + classTypes.length + " ==> " + classTypes[i].name);
	
				var classType = classTypes[i];
				
				var isAbstraction = this.checkAbstraction(classType, this.members[depth][1]);
				if (!isAbstraction) continue;
				
				var isConnections = this.checkConnections(classType, candidates, depth);
				if (!isConnections) continue;
	
				var newClassTypes = classTypes.map(x => x);
				var newCandidates = candidates.map(x => x);
	
				newClassTypes.splice(i, 1);
				newCandidates.push(classType);
	
				// println(":: " + candidates.map(x => x.name));
				
				this.recursive(newClassTypes, newCandidates, depth + 1);
			}
		}
		else {
			this.detections.push(candidates.map(x => x.name));
			// println(":: " + candidates.map(x => x.name));
		}
	}
	
	/*
	checkAbstraction(classType, abstractionLevel) {
		if (abstractionLevel == "Abstracted" && (classType.instanceOf("interface") || (classType.instanceOf("class") && classType.isAbstract()))) return true;
		if (abstractionLevel == "Abstract" && classType.instanceOf("class") && classType.isAbstract()) return true;
		if (abstractionLevel == "Interface" && classType.instanceOf("interface")) return true;
		if (abstractionLevel == "Normal" && classType.instanceOf("class")) return true;
		if (abstractionLevel == "Any" && classType.instanceOf("classType")) return true;
		return false;
	}
	*/
	
	checkAbstraction(classType, abstractionLevel) {
		var abstraction = this.abstractionOf(classType);
		if (abstractionLevel == abstraction) return true;
		if (abstractionLevel == "Abstracted" && (abstraction == "Interface" || abstraction == "Abstract")) return true;
		if (abstractionLevel == "Any") return true;
		return false;
	}
	
	abstractionOf(classType) {
		if (classType.instanceOf("class") && !classType.isAbstract) return "Normal";
		if (classType.instanceOf("class") && classType.isAbstract) return "Abstract";
		if (classType.instanceOf("interface")) return "Interface";
		return "Unknown";
		
	}
	
	checkConnections(classType, candidates, depth) {
	
		for (var connection of this.connections) {
			var fromPatternConnectionId = connection[0].charCodeAt(0) - 65;
			var toPatternConnectionId = connection[1].charCodeAt(0) - 65;
			var fromClassObjectId = connection[0].charCodeAt(0) - 65;
			var toClassObjectId = connection[1].charCodeAt(0) - 65;
	
			if (fromPatternConnectionId == depth && toClassObjectId < depth) {
				var isRelation = this.checkRelation(connection[2], classType, candidates[toClassObjectId]);
				if (!isRelation) return false;
			}
			else if (toPatternConnectionId == depth && fromClassObjectId < depth) {
				var isRelation = this.checkRelation(connection[2], candidates[fromClassObjectId], classType);
				if (!isRelation) return false;
			}
		}
		
		return true;
	}
	
	checkRelation(relationType, fromObj, toObj) {
	
		// calls, creates, references, uses, inherits, has, relates
		if (relationType == "calls") {
			return this.checkRelationCalls(fromObj, toObj);
		}
		if (relationType == "references") {
			return this.checkRelationReferences(fromObj, toObj);
		}
		if (relationType == "inherits") {
			return this.checkRelationInherits(fromObj, toObj);
		}
		if (relationType == "has") {
			return this.checkRelationHas(fromObj, toObj);
		}
		if (relationType == "creates") {
			return this.checkRelationCreates(fromObj, toObj);
		}
		if (relationType == "uses") {
			return this.checkRelationUses(fromObj, toObj);
		}
	
		return true;
	}
	
	checkRelationCalls(fromObj, toObj) {
		
		var $fromObj = this.classTypesMap.get(fromObj.name);
		
		return $fromObj.relationCalls.includes(toObj.name);
	}
	
	checkRelationCreates(fromObj, toObj) {
		
		var $fromObj = this.classTypesMap.get(fromObj.name);
		
		return $fromObj.relationCreates.includes(toObj.name);
	}
	
	checkRelationHas(fromObj, toObj) {
		
		var $fromObj = this.classTypesMap.get(fromObj.name);
		
		return $fromObj.relationHas.includes(toObj.name);
	}
	
	checkRelationInherits(fromObj, toObj) {
		
		var $fromObj = this.classTypesMap.get(fromObj.name);
		
		return $fromObj.relationInherits.includes(toObj.name);
	}
	
	checkRelationReferences(fromObj, toObj) {
		
		var $fromObj = this.classTypesMap.get(fromObj.name);
		
		return $fromObj.relationReferences.includes(toObj.name);
	}
	
	checkRelationUses(fromObj, toObj) {
		
		var $fromObj = this.classTypesMap.get(fromObj.name);
		
		return $fromObj.relationUses.includes(toObj.name);
	}
	
	 static removeDuplicates(detections) {
		
		function arrayEquals(a, b) {
			return Array.isArray(a) &&
		    	Array.isArray(b) &&
			    a.length === b.length &&
			    a.every((val, index) => val === b[index]);
		}
		
		let out = [];
		detections.forEach((detection) => {
			for (var o of out) {
				if (arrayEquals(detection, o)) return;
			}
			
			out.push(detection);
		});
		
		return out;
	}
	
	
	static getScope(element) {
		// println("   + " + element);
		if (element == null) return null;
		if (element == undefined) return null;
		
		if (element.joinPointType == "class") return element;
		if (element.joinPointType == "interface") return element;
		if (element.joinPointType == "classType") return element;
		
		if (element.joinPointType == "constructorCall") return element;
		
		return this.getScope(element.parent);
	}
	
	static callsFunctionResult($call) {
		
		var isFunctionResult = false;
		for (var child of $call.children) {
			// skip type
			if (child.joinPointType == "type") continue;
			
			if (child.code.includes("()")) isFunctionResult = true;
			// if (child.code.match("()")) isFunctionResult = true;
			/*
			if (child.joinPointType == "expr") {
				skip = isFunctionResult;
			}
			else if (child.joinPointType == "memberCall") {
				skip = isFunctionResult;
			}
			else {
				skip = isFunctionResult;
			}
			*/
			break;
		}
		
		return isFunctionResult;
	}
}

	
class ClassTypeObject {
	
	constructor(classType) {
		this.classType = classType;
		
		this.dpCoreCompatibility = false;
	}
	
	setCompatibility(dpCoreCompatibility) {
		this.dpCoreCompatibility = dpCoreCompatibility;
	}
	
	compute() {
		this.#computeRelationCalls();
		this.#computeRelationCreates();
		this.#computeRelationHas();
		this.#computeRelationInherits();
		this.#computeRelationReferences();
		this.#computeRelationUses();
		
		this.relationCalls = [...new Set(this.relationCalls)];
		this.relationCreates = [...new Set(this.relationCreates)];
		this.relationHas = [...new Set(this.relationHas)];
		this.relationInherits = [...new Set(this.relationInherits)];
		this.relationReferences = [...new Set(this.relationReferences)];
		this.relationUses = [...new Set(this.relationUses)];
	}
	
	
	#computeRelationCalls() {
		
		this.relationCalls = [];
	
		var $calls = Query.searchFrom(this.classType, "call").get();
		
		for(var $call of $calls) {
	
			// filter out constructor calls
			if ($call.instanceOf("constructorCall")) continue;
			if ($call.method == undefined || $call.method == null) continue;
			if ($call.method.isStatic) continue;
			
			// filter out of scope
			let scopeElement = DetectionAlgorithmLight.getScope($call);
			if (scopeElement.instanceOf("constructorCall")) continue;
			
			/*
			if (this.classType.name == "MyTopicSubscriber"
			 || this.classType.name == "Observer"
			 || (this.classType.name == "DrawApplication" && $call.method.class.name == "PaletteButton")) {
				
				println();
				
				println(this.classType.name + " => " + $call.method.class.name + " (" + $call.method.parent.name + ")");
				println(this.classType.name + " => " + $call.method.class.name + " (" + this.#getScope($call) + ")");
				println(this.classType.name + " => " + $call.function.class.name);
				// println(this.classType.name + " => " + $call.method.class.name + " (" + this.#getScope($call).code + ")");
				// println(this.classType.name + " => " + $call.method.class.name + " (" + $call.parent + ")");
				// println(this.classType.name + " => " + $call.method.class.name + " (" + $call.parent.parent + ")");
				// println(this.classType.name + " => " + $call.method.class.name + " (" + $call.parent.parent.parent.parent.parent + ")");
				// println(this.classType.name + " => " + $call.method.class.name + " (" + $call.parent.parent.parent.parent.parent.code + ")");
				println($call.method.id);
				println($call.parent.parent.code);
				/*println($call.method.name);
				println($call.code);
				println($call.children);
				
				for (var child of $call.children) {
				// if ($call.children.length > 1) {
					// println($call.children[1].code);
					println("   -> (" + child.joinPointType + ") " + child.code);
					println("   -> " + child.code.includes("()"));
				}
				/* * /
			}
			/* */
			
			// check if expr only
			let callsFunctionResult = DetectionAlgorithmLight.callsFunctionResult($call);
			if (callsFunctionResult == true && this.dpCoreCompatibility == true) continue;
	
			// push name
			this.relationCalls.push($call.method.class.name);
		}
	}
	
	#computeRelationCreates() {
		
		this.relationCreates = [];
		
		var $constructorCalls = Query.searchFrom(this.classType, "constructorCall").get();
		
		for(var $constructorCall of $constructorCalls) {
			
			// filter out
			if ($constructorCall.method == undefined || $constructorCall.method == null) continue;
			 
			// push name
			this.relationCreates.push($constructorCall.method.class.name);
		}
	}
	
	#computeRelationHas() {
		
		this.relationHas = [];
		
		var $fields = this.classType.fields;
		
		if ($fields == undefined) return false;
	
		for (var $field of $fields) {
			var classType = $field.type.classType;
			if (classType == null) continue;
			
			// push name
			this.relationHas.push(classType.name);
		}
	}
	
	#computeRelationInherits() {
		
		this.relationInherits = [];
	
		// var superClasses = this.classType.allSuperClasses;
		// var interfaces = this.classType.allInterfaces;
		var superClasses = this.classType.superClasses;
		var interfaces = this.classType.interfaces;
		
		if (!superClasses) superClasses = [];
		if (!interfaces) interfaces = [];
	
		var names = superClasses.map(c => c.name)
			.concat(interfaces.map(i => i.name));
	
		for (var name of names) {
			
			// push name
			this.relationInherits.push(name);
		}
	}
	
	#computeRelationReferences() {
		
		this.relationReferences = [];
	
		//
		// //  and constructors
		var $constructors = Query.searchFrom(this.classType, "constructor").get(); 
		//  
		// for(var $method of this.classType.allMethods) {
		for(var $method of this.classType.methods.concat($constructors)) {
			// if ($method.instanceOf("constructor")) continue;
			if ($method.isStatic) continue;
			
			for (var $param of $method.params) {
	
				var classType = $param.type.classType;
				if (classType == null) continue;
	
				// push name
				this.relationReferences.push(classType.name);
			}
		}
	}
	
	#computeRelationUses() {
		
		this.relationUses = [];
	
		// 
		// for(var $method of this.classType.allMethods) {
		for(var $method of this.classType.methods) {
	
			// filter constructors, and static
			if ($method.instanceOf("constructor")) continue;
			if ($method.isStatic) continue;
			
			var classType = $method.returnType.classType;
			if (classType == null) continue;
			
			// push name
			this.relationUses.push(classType.name);
		}
	}
	
};
