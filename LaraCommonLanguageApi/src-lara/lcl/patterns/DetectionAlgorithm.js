laraImport("lcl.LaraCommonLanguage");
laraImport("weaver.Query");

class DetectionAlgorithm {

	constructor(members, connections) {
		this.members = members;
		this.connections = connections;
		this.detections = [];
		this.classTypesMap = new Map();
		
		this.dpCoreCompatibility = false;
		this.fullNaming = false;
	}
	
	setCompatibility(dpCoreCompatibility) {
		this.dpCoreCompatibility = dpCoreCompatibility;
	}
	
	setFullNaming(fullNaming) {
		this.fullNaming = fullNaming;
	}
	
	parseRelations() {
		
		this.classTypesMap = new Map();
		
		var classTypes = Query.search("classType").get();
		
		for (var i = 0 ; i < classTypes.length ; i++) {
			var classType = classTypes[i];
			var classTypeName = this.#namingOf(classType);
			// println("  - caching : " + i + "/" + classTypes.length + " ==> " + classType.name + " / " + classType._qualifiedName);
			// println();// " (" + Array.from(classType.types()) + ")");
			
			// avoid duplicates
			if (classTypeName == undefined) {
				// println("warning: duplicate of '" + classTypeName + "' - skipping");
				// continue;
				println("warn: undefined name for '" + classType.name + " => " + classType);
			}
			
			// avoid duplicates
			if (this.classTypesMap.has(classTypeName)) {
				// println("warning: duplicate of '" + classTypeName + "' - skipping");
				// continue;
				println("warn: duplicate of '" + classTypeName + "' - overwriting");
			}
			
			
			var classTypeObject = new ClassTypeObject(classType);
			classTypeObject.setCompatibility(this.dpCoreCompatibility);
			classTypeObject.setFullNaming(this.fullNaming);
			classTypeObject.compute();
			this.classTypesMap.set(classTypeName, classTypeObject);
		}	
	}
	
	detect(members, connections) {
		if (typeof members !== "undefined" && typeof connections !== "undefined") {
			// it was previously parsed
			this.members = members;
			this.connections = connections;
		}
		else {
			// back-compactability, parse again
			this.parseRelations();
		}
		this.detections = [];
	
		this.#recursive(Array.from(this.classTypesMap.values()).map(obj => obj.classType), [], 0);
		
		this.detections = this.constructor.removeDuplicates(this.detections);
	
		return this.detections;
	}
	
	#namingOf(classType) {
		if (this.fullNaming) {
			return classType._qualifiedName;
		}
		else {
			return classType.name
		}
	}
	
	#recursive(classTypes, candidates, depth) {
		
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
	
				// println(":: " + newCandidates.map(x => x.name));
				
				this.#recursive(newClassTypes, newCandidates, depth + 1);
			}
		}
		else {
			this.detections.push(candidates.map(x => this.#namingOf(x)));
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
		
		var $fromObj = this.classTypesMap.get(this.#namingOf(fromObj));
		
		return $fromObj.relationCalls.includes(this.#namingOf(toObj));
	}
	
	checkRelationCreates(fromObj, toObj) {
		
		var $fromObj = this.classTypesMap.get(this.#namingOf(fromObj));
		
		return $fromObj.relationCreates.includes(this.#namingOf(toObj));
	}
	
	checkRelationHas(fromObj, toObj) {
		
		var $fromObj = this.classTypesMap.get(this.#namingOf(fromObj));
		
		return $fromObj.relationHas.includes(this.#namingOf(toObj));
	}
	
	checkRelationInherits(fromObj, toObj) {
		
		var $fromObj = this.classTypesMap.get(this.#namingOf(fromObj));
		
		return $fromObj.relationInherits.includes(this.#namingOf(toObj));
	}
	
	checkRelationReferences(fromObj, toObj) {
		
		var $fromObj = this.classTypesMap.get(this.#namingOf(fromObj));
		
		return $fromObj.relationReferences.includes(this.#namingOf(toObj));
	}
	
	checkRelationUses(fromObj, toObj) {
		
		var $fromObj = this.classTypesMap.get(this.#namingOf(fromObj));
		
		return $fromObj.relationUses.includes(this.#namingOf(toObj));
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
			if (child.code == undefined) continue;
			
			if (child.code.includes("(") && child.code.includes(")")) isFunctionResult = true;
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
	
	static callsSuper($call) {
		
		for (var child of $call.children) {
			if (child.code == "super") return true;
		}
		
		return false;
	}
}

	
class ClassTypeObject {
	
	constructor(classType) {
		this.classType = classType;
		
		this.dpCoreCompatibility = false;
		this.fullNaming = false;
	}
	
	setCompatibility(dpCoreCompatibility) {
		this.dpCoreCompatibility = dpCoreCompatibility;
	}
	
	setFullNaming(fullNaming) {
		this.fullNaming = fullNaming;
	}
	
	#namingOf(classType) {
		if (this.fullNaming) {
			return classType._qualifiedName;
		}
		else {
			return classType.name
		}
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
			if ($call == undefined || $call == null) continue;
			if ($call.instanceOf("constructorCall")) continue;
			if ($call.method == undefined || $call.method == null) continue;
			if ($call.method.isStatic) continue;
			
			// filter out of scope
			let scopeElement = DetectionAlgorithm.getScope($call);
			if (scopeElement != null && scopeElement.instanceOf("constructorCall")) continue;
			
			
			// check if expr only
			let callsFunctionResult = DetectionAlgorithm.callsFunctionResult($call);
			if (callsFunctionResult == true && this.dpCoreCompatibility == true) continue;
			
			// check if calls super
			let callsSuper = DetectionAlgorithm.callsSuper($call);
			if (callsSuper == true) continue;
			
			// check if protected method
			let protectedMethod = false;
			for (var child of $call.children) {
				if (child.code.startsWith($call.method.name)) protectedMethod = true;
				break
			}
			if (protectedMethod == true) continue;
			
			// push name
			this.relationCalls.push(this.#namingOf($call.method.class));
		}
	}
	
	#computeRelationCreates() {
		
		this.relationCreates = [];
		
		var $constructorCalls = Query.searchFrom(this.classType, "constructorCall").get();
		
		for(var $constructorCall of $constructorCalls) {
			
			// filter out
			if ($constructorCall.method == undefined || $constructorCall.method == null) continue;
			
			// filter out of scope
			let scopeElement = DetectionAlgorithm.getScope($constructorCall.parent);
			if (scopeElement == null || scopeElement == undefined) continue;
			if (scopeElement.instanceOf("constructorCall")) continue;
			if (scopeElement.name != this.classType.name) continue;

			// push name
			this.relationCreates.push(this.#namingOf($constructorCall.method.class));
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
			this.relationHas.push(this.#namingOf(classType));
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
	
		var names = superClasses.map(c => this.#namingOf(c))
			.concat(interfaces.map(i => this.#namingOf(i)));
	
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
		// for(var $method of this.classType.allMethods.concat($constructors)) {
		for(var $method of this.classType.methods.concat($constructors)) {
			// if ($method.instanceOf("constructor")) continue;
			if ($method.isStatic) continue;
			
			for (var $param of $method.params) {
				
				var classType = $param.type.classType;
				if (classType == null) continue;
	
				// push name
				this.relationReferences.push(this.#namingOf(classType));
			}
		}
	}
	
	#computeRelationUses() {
		
		this.relationUses = [];
	
		// 
		// for(var $method of this.classType.allMethods) {
		for(var $method of this.classType.methods) {
	
			// filter constructors, and static
			if ($method == null || $method == undefined) continue;
			if ($method.instanceOf("constructor")) continue;
			if ($method.isStatic) continue;
			
			var classType = $method.returnType.classType;
			if (classType == null) continue;
			
			// push name
			this.relationUses.push(this.#namingOf(classType));
		}
	}
	
};
