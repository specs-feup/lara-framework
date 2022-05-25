laraImport("lcl.LaraCommonLanguage");
laraImport("weaver.Query");

laraImport("lcl.patterns.DetectionAlgorithmLight");

class DetectionAlgorithm {

	constructor(members, connections) {
		this.members = members;
		this.connections = connections;
		this.detections = [];
		
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
		var classTypes = Query.search("classType").get();
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
		
		var classTypes = Query.search("classType").get();
		
		this.#recursive(classTypes, [], 0);
		
		this.detections = DetectionAlgorithmLight.removeDuplicates(this.detections);
	
		return this.detections;
	}
	
	detect() {
		this.parseRelations();
		return this.detect(this.members, this.connections);
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
	
				// println(":: " + candidates.map(x => x.name));
				
				this.#recursive(newClassTypes, newCandidates, depth + 1);
			}
		}
		else {
			this.detections.push(candidates.map(x => x.name));
			// println(":: " + candidates.map(x => x.name));
		}
	}
	
	/*
	checkAbstraction(classType, abstractionLevel) {
		if (abstractionLevel == "Abstracted" && (classType.instanceOf("interface") || classType.instanceOf("class"))) return true;
		if (abstractionLevel == "Abstract" && classType.instanceOf("class")) return true;
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
	
		var $calls = Query.searchFrom(fromObj, "call").get();
		
		for(var $call of $calls) {
	
			// filter out constructor calls
			if ($call.instanceOf("constructorCall")) continue;
			if ($call.method == undefined || $call.method == null) continue;
			if ($call.method.isStatic) continue;
	
			// filter out of scope
			let scopeElement = DetectionAlgorithmLight.getScope($call);
			if (scopeElement.instanceOf("constructorCall")) continue;
			
			// check if expr only
			let callsFunctionResult = DetectionAlgorithmLight.callsFunctionResult($call);
			if (callsFunctionResult == true && this.dpCoreCompatibility == true) continue;
			
			// check if calls super
			let callsSuper = DetectionAlgorithmLight.callsSuper($call);
			if (callsSuper == true) continue;
			
			// check if protected method
			let protectedMethod = false;
			for (var child of $call.children) {
				if (child.code.startsWith($call.method.name)) protectedMethod = true;
				break
			}
			if (protectedMethod == true) continue;
			
			if (toObj.name == $call.method.class.name) return true;
		}
	
		return false;
	}
	
	checkRelationCreates(fromObj, toObj) {
		
		var $constructorCalls = Query.searchFrom(fromObj, "constructorCall").get();
		
		for(var $constructorCall of $constructorCalls) {
			
			// filter out
			if ($constructorCall.method == undefined || $constructorCall.method == null) continue;
			
			// filter out of scope
			let scopeElement = DetectionAlgorithmLight.getScope($constructorCall.parent);
			if (scopeElement.instanceOf("constructorCall")) continue;
			if (scopeElement.name != this.classType.name) continue;
			
			if (toObj.name == $constructorCall.method.class.name) return true;
		}
		
		return false;
	}
	
	checkRelationHas(fromObj, toObj) {
		
		var $fields = fromObj.fields;
		
		if ($fields == undefined) return false;
	
		for (var $field of $fields) {
			var classType = $field.type.classType;
			if (classType == null) continue;
			
			if (toObj.name == classType.name) return true;
		}
		
		return false;
	}
	
	checkRelationInherits(fromObj, toObj) {
	
		// var superClasses = fromObj.allSuperClasses;
		// var interfaces = fromObj.allInterfaces;
		var superClasses = fromObj.superClasses;
		var interfaces = fromObj.interfaces;
	
		var names = superClasses.map(c => c.name)
			.concat(interfaces.map(i => i.name));
	
		for (var name of names) {
			if (toObj.name == name) return true;
		}
		return false;
	}
	
	checkRelationReferences(fromObj, toObj) {
	
		// //  and constructors
		var $constructors = Query.searchFrom(fromObj, "constructor").get(); 
	
		// 
		// for(var $method of fromObj.allMethods) {
		for(var $method of fromObj.methods.concat($constructors)) {
			// if ($method.instanceOf("constructorCall")) continue;
			if ($method.isStatic) continue;
			
			for (var $param of $method.params) {
	
				var classType = $param.type.classType;
				if (classType == null) continue;
	
				if (toObj.name == classType.name) return true;
			}
		}
	
		return false;
	}
	
	checkRelationUses(fromObj, toObj) {
	
		// 
		// for(var $method of fromObj.allMethods) {
		for(var $method of fromObj.methods) {
	
			// filter constructors, and static
			if ($method.instanceOf("constructor")) continue;
			if ($method.isStatic) continue;
			
			var classType = $method.returnType.classType;
			if (classType == null) continue;
			
			if (toObj.name == classType.name) return true;
		}
	
		return false;
	}
}
